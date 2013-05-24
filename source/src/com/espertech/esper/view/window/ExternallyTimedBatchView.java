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
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.ViewUpdatedCollection;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.event.EventBeanUtility;
import com.espertech.esper.view.CloneableView;
import com.espertech.esper.view.DataWindowView;
import com.espertech.esper.view.View;
import com.espertech.esper.view.ViewSupport;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Batch window based on timestamp of arriving events.
 */
public class ExternallyTimedBatchView extends ViewSupport implements DataWindowView, CloneableView
{
    private final ExternallyTimedBatchViewFactory factory;
    private final ExprNode timestampExpression;
    private final ExprEvaluator timestampExpressionEval;
    private final long millisecondsBeforeExpiry;

    private final EventBean[] eventsPerStream = new EventBean[1];
    protected EventBean[] lastBatch;

    private Long oldestTimestampRoundedToRef;
    protected final Set<EventBean> window = new LinkedHashSet<EventBean>();
    protected Long referenceTimestamp;

    protected ViewUpdatedCollection viewUpdatedCollection;
    protected AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext;

    /**
     * Constructor.
     * @param timestampExpression is the field name containing a long timestamp value
     * that should be in ascending order for the natural order of events and is intended to reflect
     * System.currentTimeInMillis but does not necessarily have to.
     * @param msecBeforeExpiry is the number of milliseconds before events gets pushed
     * out of the window as oldData in the update method. The view compares
     * each events timestamp against the newest event timestamp and those with a delta
     * greater then secondsBeforeExpiry are pushed out of the window.
     * @param viewUpdatedCollection is a collection that the view must update when receiving events
     * @param factory for copying this view in a group-by
     * @param agentInstanceViewFactoryContext context for expression evalauation
     */
    public ExternallyTimedBatchView(ExternallyTimedBatchViewFactory factory,
                                    ExprNode timestampExpression,
                                    ExprEvaluator timestampExpressionEval,
                                    long msecBeforeExpiry,
                                    Long optionalReferencePoint,
                                    ViewUpdatedCollection viewUpdatedCollection,
                                    AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext)
    {
        this.factory = factory;
        this.timestampExpression = timestampExpression;
        this.timestampExpressionEval = timestampExpressionEval;
        this.millisecondsBeforeExpiry = msecBeforeExpiry;
        this.viewUpdatedCollection = viewUpdatedCollection;
        this.agentInstanceViewFactoryContext = agentInstanceViewFactoryContext;
        this.referenceTimestamp = optionalReferencePoint;
    }

    public View cloneView()
    {
        return factory.makeView(agentInstanceViewFactoryContext);
    }

    /**
     * Returns the field name to get timestamp values from.
     * @return field name for timestamp values
     */
    public final ExprNode getTimestampExpression()
    {
        return timestampExpression;
    }

    public final EventType getEventType()
    {
        // The schema is the parent view's schema
        return parent.getEventType();
    }

    public final void update(EventBean[] newData, EventBean[] oldData)
    {
        // remove points from data window
        if (oldData != null && oldData.length != 0) {
            for (EventBean anOldData : oldData) {
                window.remove(anOldData);
                handleInternalRemovedEvent(anOldData);
            }
            determineOldestTimestamp();
        }

        // add data points to the window
        EventBean[] batchNewData = null;
        if (newData != null) {
            for (EventBean newEvent : newData) {

                long timestamp = getLongValue(newEvent);
                if (referenceTimestamp == null) {
                    referenceTimestamp = timestamp;
                }

                if (oldestTimestampRoundedToRef == null) {
                    oldestTimestampRoundedToRef = roundDownTimestamp(timestamp);
                }
                else {
                    if (timestamp - oldestTimestampRoundedToRef >= millisecondsBeforeExpiry) {
                        if (batchNewData == null) {
                            batchNewData = window.toArray(new EventBean[window.size()]);
                        }
                        else {
                            batchNewData = EventBeanUtility.addToArray(batchNewData, window);
                        }
                        window.clear();
                        oldestTimestampRoundedToRef = null;
                    }
                }

                window.add(newEvent);
                handleInternalAddEvent(newEvent, batchNewData != null);
            }
        }

        if (batchNewData != null) {
            handleInternalPostBatch(window, batchNewData);
            if (viewUpdatedCollection != null) {
                viewUpdatedCollection.update(batchNewData, lastBatch);
            }
            updateChildren(batchNewData, lastBatch);
            lastBatch = batchNewData;
            determineOldestTimestamp();
        }
        if (oldData != null && oldData.length > 0) {
            if (viewUpdatedCollection != null) {
                viewUpdatedCollection.update(null, oldData);
            }
            updateChildren(null, oldData);
        }
    }

    public final Iterator<EventBean> iterator()
    {
        return window.iterator();
    }

    public final String toString()
    {
        return this.getClass().getName() +
                " timestampExpression=" + timestampExpression +
                " millisecondsBeforeExpiry=" + millisecondsBeforeExpiry;
    }
    /**
     * Returns true to indicate the window is empty, or false if the view is not empty.
     * @return true if empty
     */
    public boolean isEmpty()
    {
        return window.isEmpty();
    }

    public long getMillisecondsBeforeExpiry() {
        return millisecondsBeforeExpiry;
    }

    protected void determineOldestTimestamp() {
        if (window.isEmpty()) {
            oldestTimestampRoundedToRef = null;
        }
        else {
            long ts = getLongValue(window.iterator().next());
            oldestTimestampRoundedToRef = roundDownTimestamp(ts);
        }
    }

    protected void handleInternalPostBatch(Set<EventBean> window, EventBean[] batchNewData) {
        // no action require
    }

    protected void handleInternalRemovedEvent(EventBean anOldData) {
        // no action require
    }

    protected void handleInternalAddEvent(EventBean anNewData, boolean isNextBatch) {
        // no action require
    }

    private long roundDownTimestamp(long timestamp) {
        if (timestamp <= referenceTimestamp) {
            return referenceTimestamp;
        }
        long delta = Math.abs(timestamp - referenceTimestamp);
        long factor = delta / millisecondsBeforeExpiry;
        return referenceTimestamp + factor * millisecondsBeforeExpiry;
    }

    private long getLongValue(EventBean obj)
    {
        eventsPerStream[0] = obj;
        Number num = (Number) timestampExpressionEval.evaluate(eventsPerStream, true, agentInstanceViewFactoryContext);
        return num.longValue();
    }
}
