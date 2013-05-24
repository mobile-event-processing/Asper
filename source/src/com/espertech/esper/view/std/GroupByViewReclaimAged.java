/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.std;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.MultiKeyUntyped;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.util.ExecutionPathDebugLog;
import com.espertech.esper.view.CloneableView;
import com.espertech.esper.view.StoppableView;
import com.espertech.esper.view.View;
import com.espertech.esper.view.ViewSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class GroupByViewReclaimAged extends ViewSupport implements CloneableView, GroupByView
{
    private final ExprNode[] criteriaExpressions;
    private final ExprEvaluator[] criteriaEvaluators;
    protected final AgentInstanceViewFactoryChainContext agentInstanceContext;
    private final long reclaimMaxAge;
    private final long reclaimFrequency;

    private EventBean[] eventsPerStream = new EventBean[1];
    protected String[] propertyNames;

    protected final Map<Object, GroupByViewAgedEntry> subViewsPerKey = new HashMap<Object, GroupByViewAgedEntry>();
    private final HashMap<GroupByViewAgedEntry, Pair<Object, Object>> groupedEvents = new HashMap<GroupByViewAgedEntry, Pair<Object, Object>>();
    private Long nextSweepTime = null;

    /**
     * Constructor.
     * @param agentInstanceContext contains required view services
     * @param criteriaExpressions is the fields from which to pull the values to group by
     * @param reclaimMaxAge age after which to reclaim group
     * @param reclaimFrequency frequency in which to check for groups to reclaim
     */
    public GroupByViewReclaimAged(AgentInstanceViewFactoryChainContext agentInstanceContext,
                                  ExprNode[] criteriaExpressions,
                                  ExprEvaluator[] criteriaEvaluators,
                                  double reclaimMaxAge, double reclaimFrequency)
    {
        this.agentInstanceContext = agentInstanceContext;
        this.criteriaExpressions = criteriaExpressions;
        this.criteriaEvaluators = criteriaEvaluators;
        this.reclaimMaxAge = (long) (reclaimMaxAge * 1000d);
        this.reclaimFrequency = (long) (reclaimFrequency * 1000d);

        propertyNames = new String[criteriaExpressions.length];
        for (int i = 0; i < criteriaExpressions.length; i++)
        {
            propertyNames[i] = criteriaExpressions[i].toExpressionString();
        }
    }

    public View cloneView()
    {
        return new GroupByViewReclaimAged(agentInstanceContext, criteriaExpressions, criteriaEvaluators, reclaimMaxAge, reclaimFrequency);
    }

    /**
     * Returns the field name that provides the key valie by which to group by.
     * @return field name providing group-by key.
     */
    public ExprNode[] getCriteriaExpressions()
    {
        return criteriaExpressions;
    }

    public final EventType getEventType()
    {
        // The schema is the parent view's schema
        return parent.getEventType();
    }

    public final void update(EventBean[] newData, EventBean[] oldData)
    {
        long currentTime = agentInstanceContext.getTimeProvider().getTime();
        if ((nextSweepTime == null) || (nextSweepTime <= currentTime))
        {
            if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
            {
                log.debug("Reclaiming groups older then " + reclaimMaxAge + " msec and every " + reclaimFrequency + "msec in frequency");
            }
            nextSweepTime = currentTime + reclaimFrequency;
            sweep(currentTime);
        }

        // Algorithm for single new event
        if ((newData != null) && (oldData == null) && (newData.length == 1))
        {
            EventBean theEvent = newData[0];
            EventBean[] newDataToPost = new EventBean[] {theEvent};

            Object groupByValuesKey = getGroupKey(theEvent);

            // Get child views that belong to this group-by value combination
            GroupByViewAgedEntry subViews = this.subViewsPerKey.get(groupByValuesKey);

            // If this is a new group-by value, the list of subviews is null and we need to make clone sub-views
            if (subViews == null)
            {
                Object subviewsList = GroupByViewImpl.makeSubViews(this, propertyNames, groupByValuesKey, agentInstanceContext);
                subViews = new GroupByViewAgedEntry(subviewsList, currentTime);
                subViewsPerKey.put(groupByValuesKey, subViews);
            }
            else {
                subViews.setLastUpdateTime(currentTime);
            }

            GroupByViewImpl.updateChildViews(subViews.getSubviewHolder(), newDataToPost, null);
        }
        else
        {

            // Algorithm for dispatching multiple events
            if (newData != null)
            {
                for (EventBean newValue : newData)
                {
                    handleEvent(newValue, true);
                }
            }

            if (oldData != null)
            {
                for (EventBean oldValue : oldData)
                {
                    handleEvent(oldValue, false);
                }
            }

            // Update child views
            for (Map.Entry<GroupByViewAgedEntry, Pair<Object, Object>> entry : groupedEvents.entrySet())
            {
                EventBean[] newEvents = GroupByViewImpl.convertToArray(entry.getValue().getFirst());
                EventBean[] oldEvents = GroupByViewImpl.convertToArray(entry.getValue().getSecond());
                GroupByViewImpl.updateChildViews(entry.getKey(), newEvents, oldEvents);
            }

            groupedEvents.clear();
        }
    }

    private void handleEvent(EventBean theEvent, boolean isNew)
    {
        Object groupByValuesKey = getGroupKey(theEvent);

        // Get child views that belong to this group-by value combination
        GroupByViewAgedEntry subViews = this.subViewsPerKey.get(groupByValuesKey);

        // If this is a new group-by value, the list of subviews is null and we need to make clone sub-views
        if (subViews == null)
        {
            Object subviewsList = GroupByViewImpl.makeSubViews(this, propertyNames, groupByValuesKey, agentInstanceContext);
            long currentTime = agentInstanceContext.getStatementContext().getTimeProvider().getTime();
            subViews = new GroupByViewAgedEntry(subviewsList, currentTime);
            subViewsPerKey.put(groupByValuesKey, subViews);
        }
        else {
            subViews.setLastUpdateTime(agentInstanceContext.getStatementContext().getTimeProvider().getTime());
        }

        // Construct a pair of lists to hold the events for the grouped value if not already there
        Pair<Object, Object> pair = groupedEvents.get(subViews);
        if (pair == null) {
            pair = new Pair<Object, Object>(null, null);
            groupedEvents.put(subViews, pair);
        }

        // Add event to a child view event list for later child update that includes new and old events
        if (isNew) {
            pair.setFirst(GroupByViewImpl.addUpgradeToDequeIfPopulated(pair.getFirst(), theEvent));
        }
        else {
            pair.setSecond(GroupByViewImpl.addUpgradeToDequeIfPopulated(pair.getSecond(), theEvent));
        }
    }

    public final Iterator<EventBean> iterator()
    {
        throw new UnsupportedOperationException("Cannot iterate over group view, this operation is not supported");
    }

    public final String toString()
    {
        return this.getClass().getName() + " groupFieldNames=" + Arrays.toString(criteriaExpressions);
    }

    private void sweep(long currentTime)
    {
        ArrayDeque<Object> removed = new ArrayDeque<Object>();
        for (Map.Entry<Object, GroupByViewAgedEntry> entry : subViewsPerKey.entrySet())
        {
            long age = currentTime - entry.getValue().getLastUpdateTime();
            if (age > reclaimMaxAge)
            {
                removed.add(entry.getKey());
            }
        }

        for (Object key : removed)
        {
            GroupByViewAgedEntry entry = subViewsPerKey.remove(key);
            Object subviewHolder = entry.getSubviewHolder();
            if (subviewHolder instanceof List) {
                List<View> subviews = (List<View>) subviewHolder;
                for (View view : subviews) {
                    removeSubview(view);
                }
            }
            else if (subviewHolder instanceof View) {
                removeSubview((View) subviewHolder);
            }
        }
    }

    private void removeSubview(View view) {
        view.setParent(null);
        recursiveMergeViewRemove(view);
        if (view instanceof StoppableView) {
            ((StoppableView) view).stopView();
        }
    }

    private void recursiveMergeViewRemove(View view)
    {
        for (View child : view.getViews()) {
            if (child instanceof StoppableView) {
                ((StoppableView) child).stopView();
            }
            if (child instanceof MergeView) {
                MergeView mergeView = (MergeView) child;
                mergeView.removeParentView(view);
            }
            else {
                recursiveMergeViewRemove(child);
            }
        }
    }

    private Object getGroupKey(EventBean theEvent)
    {
        eventsPerStream[0] = theEvent;
        if (criteriaEvaluators.length == 1) {
            return criteriaEvaluators[0].evaluate(eventsPerStream, true, agentInstanceContext);
        }

        Object[] values = new Object[criteriaEvaluators.length];
        for (int i = 0; i < criteriaEvaluators.length; i++)
        {
            values[i] = criteriaEvaluators[i].evaluate(eventsPerStream, true, agentInstanceContext);
        }
        return new MultiKeyUntyped(values);
    }

    private static final Log log = LogFactory.getLog(GroupByViewReclaimAged.class);
}