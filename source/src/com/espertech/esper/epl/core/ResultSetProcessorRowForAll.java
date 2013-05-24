/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.*;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.spec.OutputLimitLimitType;
import com.espertech.esper.util.CollectionUtil;
import com.espertech.esper.view.Viewable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Result set processor for the case: aggregation functions used in the select clause, and no group-by,
 * and all properties in the select clause are under an aggregation function.
 * <p>
 * This processor does not perform grouping, every event entering and leaving is in the same group.
 * Produces one old event and one new event row every time either at least one old or new event is received.
 * Aggregation state is simply one row holding all the state.
 */
public class ResultSetProcessorRowForAll implements ResultSetProcessor
{
    private final ResultSetProcessorRowForAllFactory prototype;
    private final SelectExprProcessor selectExprProcessor;
    private final OrderByProcessor orderByProcessor;
    private final AggregationService aggregationService;
    private ExprEvaluatorContext exprEvaluatorContext;

    public ResultSetProcessorRowForAll(ResultSetProcessorRowForAllFactory prototype, SelectExprProcessor selectExprProcessor, OrderByProcessor orderByProcessor, AggregationService aggregationService, ExprEvaluatorContext exprEvaluatorContext) {
        this.prototype = prototype;
        this.selectExprProcessor = selectExprProcessor;
        this.orderByProcessor = orderByProcessor;
        this.aggregationService = aggregationService;
        this.exprEvaluatorContext = exprEvaluatorContext;
    }

    public void setAgentInstanceContext(AgentInstanceContext context) {
        this.exprEvaluatorContext = context;
    }

    public EventType getResultEventType()
    {
        return prototype.getResultEventType();
    }

    public UniformPair<EventBean[]> processJoinResult(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isSynthesize)
    {
        EventBean[] selectOldEvents = null;
        EventBean[] selectNewEvents;

        if (prototype.isUnidirectional())
        {
            this.clear();
        }

        if (prototype.isSelectRStream())
        {
            selectOldEvents = getSelectListEvents(false, isSynthesize);
        }

        if (!newEvents.isEmpty())
        {
            // apply new data to aggregates
            for (MultiKey<EventBean> events : newEvents)
            {
                aggregationService.applyEnter(events.getArray(), null, exprEvaluatorContext);
            }
        }
        if (!oldEvents.isEmpty())
        {
            // apply old data to aggregates
            for (MultiKey<EventBean> events : oldEvents)
            {
                aggregationService.applyLeave(events.getArray(), null, exprEvaluatorContext);
            }
        }

        selectNewEvents = getSelectListEvents(true, isSynthesize);

        if ((selectNewEvents == null) && (selectOldEvents == null))
        {
            return null;
        }
        return new UniformPair<EventBean[]>(selectNewEvents, selectOldEvents);
    }

    public UniformPair<EventBean[]> processViewResult(EventBean[] newData, EventBean[] oldData, boolean isSynthesize)
    {
        EventBean[] selectOldEvents = null;
        EventBean[] selectNewEvents;

        if (prototype.isSelectRStream())
        {
            selectOldEvents = getSelectListEvents(false, isSynthesize);
        }

        EventBean[] eventsPerStream = new EventBean[1];
        if (newData != null)
        {
            // apply new data to aggregates
            for (int i = 0; i < newData.length; i++)
            {
                eventsPerStream[0] = newData[i];
                aggregationService.applyEnter(eventsPerStream, null, exprEvaluatorContext);
            }
        }
        if (oldData != null)
        {
            // apply old data to aggregates
            for (int i = 0; i < oldData.length; i++)
            {
                eventsPerStream[0] = oldData[i];
                aggregationService.applyLeave(eventsPerStream, null, exprEvaluatorContext);
            }
        }

        // generate new events using select expressions
        selectNewEvents = getSelectListEvents(true, isSynthesize);

        if ((selectNewEvents == null) && (selectOldEvents == null))
        {
            return null;
        }

        return new UniformPair<EventBean[]>(selectNewEvents, selectOldEvents);
    }

    private EventBean[] getSelectListEvents(boolean isNewData, boolean isSynthesize)
    {
        // Since we are dealing with strictly aggregation nodes, there are no events required for evaluating
        EventBean theEvent = selectExprProcessor.process(CollectionUtil.EVENT_PER_STREAM_EMPTY, isNewData, isSynthesize, exprEvaluatorContext);

        if (prototype.getOptionalHavingNode() != null)
        {
            Boolean result = (Boolean) prototype.getOptionalHavingNode().evaluate(null, isNewData, exprEvaluatorContext);
            if ((result == null) || (!result))
            {
                return null;
            }
        }

        // The result is always a single row
        return new EventBean[] {theEvent};
    }

    private EventBean getSelectListEvent(boolean isNewData, boolean isSynthesize)
    {
        // Since we are dealing with strictly aggregation nodes, there are no events required for evaluating
        EventBean theEvent = selectExprProcessor.process(CollectionUtil.EVENT_PER_STREAM_EMPTY, isNewData, isSynthesize, exprEvaluatorContext);

        if (prototype.getOptionalHavingNode() != null)
        {
            Boolean result = (Boolean) prototype.getOptionalHavingNode().evaluate(null, isNewData, exprEvaluatorContext);
            if ((result == null) || (!result))
            {
                return null;
            }
        }

        // The result is always a single row
        return theEvent;
    }

    public Iterator<EventBean> getIterator(Viewable parent)
    {
        EventBean[] selectNewEvents = getSelectListEvents(true, true);
        if (selectNewEvents == null)
        {
            return CollectionUtil.NULL_EVENT_ITERATOR;
        }
        return new SingleEventIterator(selectNewEvents[0]);
    }

    public Iterator<EventBean> getIterator(Set<MultiKey<EventBean>> joinSet)
    {
        EventBean[] result = getSelectListEvents(true, true);
        return new ArrayEventIterator(result);
    }

    public void clear()
    {
        aggregationService.clearResults(exprEvaluatorContext);
    }

    public UniformPair<EventBean[]> processOutputLimitedJoin(List<UniformPair<Set<MultiKey<EventBean>>>> joinEventsSet, boolean generateSynthetic, OutputLimitLimitType outputLimitLimitType)
    {
        if (outputLimitLimitType == OutputLimitLimitType.LAST)
        {
            EventBean lastOldEvent = null;
            EventBean lastNewEvent = null;

            // if empty (nothing to post)
            if (joinEventsSet.isEmpty())
            {
                if (prototype.isSelectRStream())
                {
                    lastOldEvent = getSelectListEvent(false, generateSynthetic);
                    lastNewEvent = lastOldEvent;
                }
                else
                {
                    lastNewEvent = getSelectListEvent(false, generateSynthetic);
                }
            }

            for (UniformPair<Set<MultiKey<EventBean>>> pair : joinEventsSet)
            {
                if (prototype.isUnidirectional())
                {
                    this.clear();
                }

                Set<MultiKey<EventBean>> newData = pair.getFirst();
                Set<MultiKey<EventBean>> oldData = pair.getSecond();

                if ((lastOldEvent == null) && (prototype.isSelectRStream()))
                {
                    lastOldEvent = getSelectListEvent(false, generateSynthetic);
                }

                if (newData != null)
                {
                    // apply new data to aggregates
                    for (MultiKey<EventBean> eventsPerStream : newData)
                    {
                        aggregationService.applyEnter(eventsPerStream.getArray(), null, exprEvaluatorContext);
                    }
                }
                if (oldData != null)
                {
                    // apply old data to aggregates
                    for (MultiKey<EventBean> eventsPerStream : oldData)
                    {
                        aggregationService.applyLeave(eventsPerStream.getArray(), null, exprEvaluatorContext);
                    }
                }

                lastNewEvent = getSelectListEvent(true, generateSynthetic);
            }

            EventBean[] lastNew = (lastNewEvent != null) ? new EventBean[] {lastNewEvent} : null;
            EventBean[] lastOld = (lastOldEvent != null) ? new EventBean[] {lastOldEvent} : null;

            if ((lastNew == null) && (lastOld == null))
            {
                return null;
            }
            return new UniformPair<EventBean[]>(lastNew, lastOld);
        }
        else
        {
            List<EventBean> newEvents = new LinkedList<EventBean>();
            List<EventBean> oldEvents = null;
            if (prototype.isSelectRStream())
            {
                oldEvents = new LinkedList<EventBean>();
            }

            List<Object> newEventsSortKey = null;
            List<Object> oldEventsSortKey = null;
            if (orderByProcessor != null)
            {
                newEventsSortKey = new LinkedList<Object>();
                if (prototype.isSelectRStream())
                {
                    oldEventsSortKey = new LinkedList<Object>();
                }
            }

            for (UniformPair<Set<MultiKey<EventBean>>> pair : joinEventsSet)
            {
                if (prototype.isUnidirectional())
                {
                    this.clear();
                }

                Set<MultiKey<EventBean>> newData = pair.getFirst();
                Set<MultiKey<EventBean>> oldData = pair.getSecond();

                if (prototype.isSelectRStream())
                {
                    getSelectListEvent(false, generateSynthetic, oldEvents);
                }

                if (newData != null)
                {
                    // apply new data to aggregates
                    for (MultiKey<EventBean> row : newData)
                    {
                        aggregationService.applyEnter(row.getArray(), null, exprEvaluatorContext);
                    }
                }
                if (oldData != null)
                {
                    // apply old data to aggregates
                    for (MultiKey<EventBean> row : oldData)
                    {
                        aggregationService.applyLeave(row.getArray(), null, exprEvaluatorContext);
                    }
                }

                getSelectListEvent(false, generateSynthetic, newEvents);
            }

            EventBean[] newEventsArr = (newEvents.isEmpty()) ? null : newEvents.toArray(new EventBean[newEvents.size()]);
            EventBean[] oldEventsArr = null;
            if (prototype.isSelectRStream())
            {
                oldEventsArr = (oldEvents.isEmpty()) ? null : oldEvents.toArray(new EventBean[oldEvents.size()]);
            }

            if (orderByProcessor != null)
            {
                Object[] sortKeysNew = (newEventsSortKey.isEmpty()) ? null : newEventsSortKey.toArray(new Object[newEventsSortKey.size()]);
                newEventsArr = orderByProcessor.sort(newEventsArr, sortKeysNew, exprEvaluatorContext);
                if (prototype.isSelectRStream())
                {
                    Object[] sortKeysOld = (oldEventsSortKey.isEmpty()) ? null : oldEventsSortKey.toArray(new Object[oldEventsSortKey.size()]);
                    oldEventsArr = orderByProcessor.sort(oldEventsArr, sortKeysOld, exprEvaluatorContext);
                }
            }

            if (joinEventsSet.isEmpty())
            {
                if (prototype.isSelectRStream())
                {
                    oldEventsArr = getSelectListEvents(false, generateSynthetic);
                }
                newEventsArr = getSelectListEvents(true, generateSynthetic);
            }

            if ((newEventsArr == null) && (oldEventsArr == null))
            {
                return null;
            }
            return new UniformPair<EventBean[]>(newEventsArr, oldEventsArr);
        }
    }

    public UniformPair<EventBean[]> processOutputLimitedView(List<UniformPair<EventBean[]>> viewEventsList, boolean generateSynthetic, OutputLimitLimitType outputLimitLimitType)
    {
        if (outputLimitLimitType == OutputLimitLimitType.LAST)
        {
            // For last, if there are no events:
            //   As insert stream, return the current value, if matching the having clause
            //   As remove stream, return the current value, if matching the having clause
            // For last, if there are events in the batch:
            //   As insert stream, return the newest value that is matching the having clause
            //   As remove stream, return the oldest value that is matching the having clause

            EventBean lastOldEvent = null;
            EventBean lastNewEvent = null;
            EventBean[] eventsPerStream = new EventBean[1];

            // if empty (nothing to post)
            if (viewEventsList.isEmpty())
            {
                if (prototype.isSelectRStream())
                {
                    lastOldEvent = getSelectListEvent(false, generateSynthetic);
                    lastNewEvent = lastOldEvent;
                }
                else
                {
                    lastNewEvent = getSelectListEvent(false, generateSynthetic);
                }
            }

            for (UniformPair<EventBean[]> pair : viewEventsList)
            {
                EventBean[] newData = pair.getFirst();
                EventBean[] oldData = pair.getSecond();

                if ((lastOldEvent == null) && (prototype.isSelectRStream()))
                {
                    lastOldEvent = getSelectListEvent(false, generateSynthetic);
                }

                if (newData != null)
                {
                    // apply new data to aggregates
                    for (EventBean aNewData : newData)
                    {
                        eventsPerStream[0] = aNewData;
                        aggregationService.applyEnter(eventsPerStream, null, exprEvaluatorContext);
                    }
                }
                if (oldData != null)
                {
                    // apply old data to aggregates
                    for (EventBean anOldData : oldData)
                    {
                        eventsPerStream[0] = anOldData;
                        aggregationService.applyLeave(eventsPerStream, null, exprEvaluatorContext);
                    }
                }

                lastNewEvent = getSelectListEvent(false, generateSynthetic);
            }

            EventBean[] lastNew = (lastNewEvent != null) ? new EventBean[] {lastNewEvent} : null;
            EventBean[] lastOld = (lastOldEvent != null) ? new EventBean[] {lastOldEvent} : null;

            if ((lastNew == null) && (lastOld == null))
            {
                return null;
            }
            return new UniformPair<EventBean[]>(lastNew, lastOld);
        }
        else
        {
            List<EventBean> newEvents = new LinkedList<EventBean>();
            List<EventBean> oldEvents = null;
            if (prototype.isSelectRStream())
            {
                oldEvents = new LinkedList<EventBean>();
            }

            List<Object> newEventsSortKey = null;
            List<Object> oldEventsSortKey = null;
            if (orderByProcessor != null)
            {
                newEventsSortKey = new LinkedList<Object>();
                if (prototype.isSelectRStream())
                {
                    oldEventsSortKey = new LinkedList<Object>();
                }
            }

            for (UniformPair<EventBean[]> pair : viewEventsList)
            {
                EventBean[] newData = pair.getFirst();
                EventBean[] oldData = pair.getSecond();

                if (prototype.isSelectRStream())
                {
                    getSelectListEvent(false, generateSynthetic, oldEvents);
                }

                EventBean[] eventsPerStream = new EventBean[1];
                if (newData != null)
                {
                    // apply new data to aggregates
                    for (EventBean aNewData : newData)
                    {
                        eventsPerStream[0] = aNewData;
                        aggregationService.applyEnter(eventsPerStream, null, exprEvaluatorContext);
                    }
                }
                if (oldData != null)
                {
                    // apply old data to aggregates
                    for (EventBean anOldData : oldData)
                    {
                        eventsPerStream[0] = anOldData;
                        aggregationService.applyLeave(eventsPerStream, null, exprEvaluatorContext);
                    }
                }

                getSelectListEvent(true, generateSynthetic, newEvents);
            }

            EventBean[] newEventsArr = (newEvents.isEmpty()) ? null : newEvents.toArray(new EventBean[newEvents.size()]);
            EventBean[] oldEventsArr = null;
            if (prototype.isSelectRStream())
            {
                oldEventsArr = (oldEvents.isEmpty()) ? null : oldEvents.toArray(new EventBean[oldEvents.size()]);
            }
            if (orderByProcessor != null)
            {
                Object[] sortKeysNew = (newEventsSortKey.isEmpty()) ? null : newEventsSortKey.toArray(new Object[newEventsSortKey.size()]);
                newEventsArr = orderByProcessor.sort(newEventsArr, sortKeysNew, exprEvaluatorContext);
                if (prototype.isSelectRStream())
                {
                    Object[] sortKeysOld = (oldEventsSortKey.isEmpty()) ? null : oldEventsSortKey.toArray(new Object[oldEventsSortKey.size()]);
                    oldEventsArr = orderByProcessor.sort(oldEventsArr, sortKeysOld, exprEvaluatorContext);
                }
            }

            if (viewEventsList.isEmpty())
            {
                if (prototype.isSelectRStream())
                {
                    oldEventsArr = getSelectListEvents(false, generateSynthetic);
                }
                newEventsArr = getSelectListEvents(true, generateSynthetic);
            }

            if ((newEventsArr == null) && (oldEventsArr == null))
            {
                return null;
            }
            return new UniformPair<EventBean[]>(newEventsArr, oldEventsArr);
        }
    }

    private void getSelectListEvent(boolean isNewData, boolean isSynthesize, List<EventBean> resultEvents)
    {
        // Since we are dealing with strictly aggregation nodes, there are no events required for evaluating
        EventBean theEvent = selectExprProcessor.process(CollectionUtil.EVENT_PER_STREAM_EMPTY, isNewData, isSynthesize, exprEvaluatorContext);

        if (prototype.getOptionalHavingNode() != null)
        {
            Boolean result = (Boolean) prototype.getOptionalHavingNode().evaluate(null, isNewData, exprEvaluatorContext);
            if ((result == null) || (!result))
            {
                return;
            }
        }

        resultEvents.add(theEvent);
    }

    public boolean hasAggregation() {
        return true;
    }    
}
