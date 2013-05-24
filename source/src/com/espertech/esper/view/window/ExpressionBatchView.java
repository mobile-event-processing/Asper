/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.window;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.collection.ViewUpdatedCollection;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.epl.agg.service.AggregationServiceAggExpressionDesc;
import com.espertech.esper.epl.agg.service.AggregationServiceFactoryDesc;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.event.map.MapEventBean;
import com.espertech.esper.view.View;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This view is a moving window extending the into the past until the expression passed to it returns false.
 */
public class ExpressionBatchView extends ExpressionViewBase {

    private final ExpressionBatchViewFactory dataWindowViewFactory;
    protected final Set<EventBean> window = new LinkedHashSet<EventBean>();

    protected EventBean[] lastBatch;
    protected long newestEventTimestamp;
    protected long oldestEventTimestamp;

    /**
     * Constructor creates a moving window extending the specified number of elements into the past.
     * @param dataWindowViewFactory for copying this view in a group-by
     * @param viewUpdatedCollection is a collection that the view must update when receiving events
     * @param variableNames variable names
     */
    public ExpressionBatchView(ExpressionBatchViewFactory dataWindowViewFactory,
                               ViewUpdatedCollection viewUpdatedCollection,
                               ExprEvaluator expiryExpression,
                               AggregationServiceFactoryDesc aggregationServiceFactoryDesc,
                               MapEventBean builtinEventProps,
                               Set<String> variableNames,
                               AgentInstanceViewFactoryChainContext agentInstanceContext)
    {
        super(viewUpdatedCollection, expiryExpression, aggregationServiceFactoryDesc, builtinEventProps, variableNames, agentInstanceContext);
        this.dataWindowViewFactory = dataWindowViewFactory;
    }

    public View cloneView()
    {
        return dataWindowViewFactory.makeView(agentInstanceContext);
    }

    /**
     * Returns true if the window is empty, or false if not empty.
     * @return true if empty
     */
    public boolean isEmpty()
    {
        return window.isEmpty();
    }

    public void scheduleCallback() {
        boolean fireBatch = evaluateExpression(null, window.size());
        if (fireBatch) {
            expire();
        }
    }

    public void update(EventBean[] newData, EventBean[] oldData)
    {
        boolean fireBatch = false;

        // remove points from data window
        if (oldData != null) {
            for (EventBean anOldData : oldData) {
                window.remove(anOldData);
            }
            if (aggregationService != null) {
                aggregationService.applyLeave(oldData, null, agentInstanceContext);
            }

            fireBatch = evaluateExpression(null, window.size());
        }

        // add data points to the window
        if (newData != null)
        {
            if (window.isEmpty()) {
                oldestEventTimestamp = agentInstanceContext.getStatementContext().getSchedulingService().getTime();
            }
            newestEventTimestamp = agentInstanceContext.getStatementContext().getSchedulingService().getTime();

            for (EventBean newEvent : newData) {
                window.add(newEvent);
                if (aggregationService != null) {
                    aggregationService.applyEnter(new EventBean[] {newEvent}, null, agentInstanceContext);
                }
                if (!fireBatch) {
                    fireBatch = evaluateExpression(newEvent, window.size());
                }
            }
        }

        // may fire the batch
        if (fireBatch) {
            expire();
        }
        else {
            if (newData != null) {
                Collections.addAll(window, newData);
            }
        }
    }

    // Called based on schedule evaluation registered when a variable changes (new data is null).
    // Called when new data arrives.
    public void expire() {
        EventBean[] batchNewData = window.toArray(new EventBean[window.size()]);

        if (viewUpdatedCollection != null) {
            viewUpdatedCollection.update(batchNewData, lastBatch);
        }

        // post
        if (batchNewData != null || lastBatch != null) {
            updateChildren(batchNewData, lastBatch);
        }

        // clear
        window.clear();
        lastBatch = batchNewData;
        if (aggregationService != null) {
            aggregationService.clearResults(agentInstanceContext);
        }
    }

    private boolean evaluateExpression(EventBean arriving, int windowSize) {

        builtinEventProps.getProperties().put(ExpressionViewUtil.CURRENT_COUNT, windowSize);
        builtinEventProps.getProperties().put(ExpressionViewUtil.OLDEST_TIMESTAMP, oldestEventTimestamp);
        builtinEventProps.getProperties().put(ExpressionViewUtil.NEWEST_TIMESTAMP, newestEventTimestamp);
        builtinEventProps.getProperties().put(ExpressionViewUtil.VIEW_REFERENCE, this);
        builtinEventProps.getProperties().put(ExpressionViewUtil.EXPIRED_COUNT, 0);
        eventsPerStream[0] = arriving;

        for (AggregationServiceAggExpressionDesc aggregateNode : aggregateNodes) {
            aggregateNode.assignFuture(aggregationService);
        }

        Boolean result = (Boolean) expiryExpression.evaluate(eventsPerStream, true, agentInstanceContext);
        if (result == null) {
            return false;
        }
        return result;
    }

    public final Iterator<EventBean> iterator()
    {
        return window.iterator();
    }

    // Handle variable updates by scheduling a re-evaluation with timers
    public void update(Object newValue, Object oldValue) {
        if (!agentInstanceContext.getStatementContext().getSchedulingService().isScheduled(scheduleHandle)) {
            agentInstanceContext.getStatementContext().getSchedulingService().add(0, scheduleHandle, scheduleSlot);
        }
    }
}
