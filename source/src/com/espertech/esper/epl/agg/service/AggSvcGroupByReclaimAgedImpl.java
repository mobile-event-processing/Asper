/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.agg.service;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.agg.access.AggregationAccess;
import com.espertech.esper.epl.agg.access.AggregationAccessUtil;
import com.espertech.esper.epl.agg.access.AggregationAccessorSlotPair;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.util.ExecutionPathDebugLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Implementation for handling aggregation with grouping by group-keys.
 */
public class AggSvcGroupByReclaimAgedImpl extends AggregationServiceBaseGrouped
{
    private static final Log log = LogFactory.getLog(AggSvcGroupByReclaimAgedImpl.class);

    private static final long DEFAULT_MAX_AGE_MSEC = 60000L;

    private final AggregationAccessorSlotPair[] accessors;
    protected final int[] streams;
    protected final boolean isJoin;

    private final AggSvcGroupByReclaimAgedEvalFunc evaluationFunctionMaxAge;
    private final AggSvcGroupByReclaimAgedEvalFunc evaluationFunctionFrequency;
    private final MethodResolutionService methodResolutionService;

    // maintain for each group a row of aggregator states that the expression node canb pull the data from via index
    protected Map<Object, AggregationMethodRowAged> aggregatorsPerGroup;

    // maintain a current row for random access into the aggregator state table
    // (row=groups, columns=expression nodes that have aggregation functions)
    private AggregationMethod[] currentAggregatorMethods;
    private AggregationAccess[] currentAggregatorAccesses;

    private List<Object> removedKeys;
    private Long nextSweepTime = null;
    private AggregationRowRemovedCallback removedCallback;
    private volatile long currentMaxAge = DEFAULT_MAX_AGE_MSEC;
    private volatile long currentReclaimFrequency = DEFAULT_MAX_AGE_MSEC;

    public AggSvcGroupByReclaimAgedImpl(ExprEvaluator evaluators[], AggregationMethodFactory aggregators[], AggregationAccessorSlotPair[] accessors, int[] streams, boolean join, AggSvcGroupByReclaimAgedEvalFunc evaluationFunctionMaxAge, AggSvcGroupByReclaimAgedEvalFunc evaluationFunctionFrequency, MethodResolutionService methodResolutionService) {
        super(evaluators, aggregators);
        this.accessors = accessors;
        this.streams = streams;
        isJoin = join;
        this.evaluationFunctionMaxAge = evaluationFunctionMaxAge;
        this.evaluationFunctionFrequency = evaluationFunctionFrequency;
        this.methodResolutionService = methodResolutionService;
        this.aggregatorsPerGroup = new HashMap<Object, AggregationMethodRowAged>();
        removedKeys = new ArrayList<Object>();
    }

    public void clearResults(ExprEvaluatorContext exprEvaluatorContext)
    {
        aggregatorsPerGroup.clear();
    }

    public void applyEnter(EventBean[] eventsPerStream, Object groupByKey, ExprEvaluatorContext exprEvaluatorContext)
    {
        long currentTime = exprEvaluatorContext.getTimeProvider().getTime();
        if ((nextSweepTime == null) || (nextSweepTime <= currentTime))
        {
            currentMaxAge = getMaxAge(currentMaxAge);
            currentReclaimFrequency = getReclaimFrequency(currentReclaimFrequency);
            if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
            {
                log.debug("Reclaiming groups older then " + currentMaxAge + " msec and every " + currentReclaimFrequency + "msec in frequency");
            }
            nextSweepTime = currentTime + currentReclaimFrequency;
            sweep(currentTime, currentMaxAge);
        }

        handleRemovedKeys(); // we collect removed keys lazily on the next enter to reduce the chance of empty-group queries creating empty aggregators temporarily

        AggregationMethodRowAged row = aggregatorsPerGroup.get(groupByKey);

        // The aggregators for this group do not exist, need to create them from the prototypes
        AggregationMethod[] groupAggregators;
        AggregationAccess[] groupAccesses;
        if (row == null)
        {
            groupAggregators = methodResolutionService.newAggregators(aggregators, exprEvaluatorContext.getAgentInstanceId(), groupByKey);
            groupAccesses = AggregationAccessUtil.getNewAccesses(exprEvaluatorContext.getAgentInstanceId(), isJoin, streams, methodResolutionService, groupByKey);
            row = new AggregationMethodRowAged(methodResolutionService.getCurrentRowCount(groupAggregators, groupAccesses) + 1, currentTime, groupAggregators, groupAccesses);
            aggregatorsPerGroup.put(groupByKey, row);
        }
        else
        {
            groupAggregators = row.getMethods();
            groupAccesses = row.getAccesses();
            row.increaseRefcount();
            row.setLastUpdateTime(currentTime);
        }

        currentAggregatorMethods = groupAggregators;
        currentAggregatorAccesses = groupAccesses;

        // For this row, evaluate sub-expressions, enter result
        for (int j = 0; j < evaluators.length; j++)
        {
            Object columnResult = evaluators[j].evaluate(eventsPerStream, true, exprEvaluatorContext);
            groupAggregators[j].enter(columnResult);
        }
        for (AggregationAccess access : currentAggregatorAccesses) {
            access.applyEnter(eventsPerStream);
        }
        internalHandleUpdated(groupByKey, row);
    }

    private void sweep(long currentTime, long currentMaxAge)
    {
        ArrayDeque<Object> removed = new ArrayDeque<Object>();
        for (Map.Entry<Object, AggregationMethodRowAged> entry : aggregatorsPerGroup.entrySet())
        {
            long age = currentTime - entry.getValue().getLastUpdateTime();
            if (age > currentMaxAge)
            {
                removed.add(entry.getKey());
            }
        }

        for (Object key : removed)
        {
            aggregatorsPerGroup.remove(key);
            internalHandleRemoved(key);
            removedCallback.removed(key);
        }
    }

    private long getMaxAge(long currentMaxAge)
    {
        Double maxAge = evaluationFunctionMaxAge.getLongValue();
        if ((maxAge == null) || (maxAge <= 0))
        {
            return currentMaxAge;
        }
        return Math.round(maxAge * 1000d);
    }

    private long getReclaimFrequency(long currentReclaimFrequency)
    {
        Double frequency = evaluationFunctionFrequency.getLongValue();
        if ((frequency == null) || (frequency <= 0))
        {
            return currentReclaimFrequency;
        }
        return Math.round(frequency * 1000d);
    }

    public void applyLeave(EventBean[] eventsPerStream, Object groupByKey, ExprEvaluatorContext exprEvaluatorContext)
    {
        AggregationMethodRowAged row = aggregatorsPerGroup.get(groupByKey);
        long currentTime = exprEvaluatorContext.getTimeProvider().getTime();

        // The aggregators for this group do not exist, need to create them from the prototypes
        AggregationMethod[] groupAggregators;
        AggregationAccess[] groupAccesses;
        if (row != null)
        {
            groupAggregators = row.getMethods();
            groupAccesses = row.getAccesses();
        }
        else
        {
            groupAggregators = methodResolutionService.newAggregators(aggregators, exprEvaluatorContext.getAgentInstanceId(), groupByKey);
            groupAccesses = AggregationAccessUtil.getNewAccesses(exprEvaluatorContext.getAgentInstanceId(), isJoin, streams, methodResolutionService, groupByKey);
            row = new AggregationMethodRowAged(methodResolutionService.getCurrentRowCount(groupAggregators, groupAccesses) + 1, currentTime, groupAggregators, groupAccesses);
            aggregatorsPerGroup.put(groupByKey, row);
        }

        currentAggregatorMethods = groupAggregators;
        currentAggregatorAccesses = groupAccesses;

        // For this row, evaluate sub-expressions, enter result
        for (int j = 0; j < evaluators.length; j++)
        {
            Object columnResult = evaluators[j].evaluate(eventsPerStream, false, exprEvaluatorContext);
            groupAggregators[j].leave(columnResult);
        }
        for (AggregationAccess access : currentAggregatorAccesses) {
            access.applyLeave(eventsPerStream);
        }

        row.decreaseRefcount();
        row.setLastUpdateTime(currentTime);
        if (row.getRefcount() <= 0)
        {
            removedKeys.add(groupByKey);
            methodResolutionService.removeAggregators(exprEvaluatorContext.getAgentInstanceId(), groupByKey);  // allow persistence to remove keys already
        }
        internalHandleUpdated(groupByKey, row);
    }

    public void setCurrentAccess(Object groupByKey, int agentInstanceId)
    {
        AggregationMethodRowAged row = aggregatorsPerGroup.get(groupByKey);

        if (row != null)
        {
            currentAggregatorMethods = row.getMethods();
            currentAggregatorAccesses = row.getAccesses();
        }
        else
        {
            currentAggregatorMethods = null;
        }

        if (currentAggregatorMethods == null)
        {
            currentAggregatorMethods = methodResolutionService.newAggregators(aggregators, agentInstanceId, groupByKey);
            currentAggregatorAccesses = AggregationAccessUtil.getNewAccesses(agentInstanceId, isJoin, streams, methodResolutionService, groupByKey);
        }
    }

    public Object getValue(int column, int agentInstanceId)
    {
        if (column < aggregators.length) {
            return currentAggregatorMethods[column].getValue();
        }
        else {
            AggregationAccessorSlotPair pair = accessors[column - aggregators.length];
            return pair.getAccessor().getValue(currentAggregatorAccesses[pair.getSlot()]);
        }
    }

    public Collection<EventBean> getCollection(int column, ExprEvaluatorContext context) {
        if (column < aggregators.length) {
            return null;
        }
        else {
            AggregationAccessorSlotPair pair = accessors[column - aggregators.length];
            return pair.getAccessor().getCollectionReadOnly(currentAggregatorAccesses[pair.getSlot()]);
        }
    }

    public EventBean getEventBean(int column, ExprEvaluatorContext context) {
        if (column < aggregators.length) {
            return null;
        }
        else {
            AggregationAccessorSlotPair pair = accessors[column - aggregators.length];
            return pair.getAccessor().getEventBean(currentAggregatorAccesses[pair.getSlot()]);
        }
    }

    public void setRemovedCallback(AggregationRowRemovedCallback callback) {
        this.removedCallback = callback;
    }

    public void internalHandleUpdated(Object groupByKey, AggregationMethodRowAged row) {
        // no action required
    }

    public void internalHandleRemoved(Object key) {
        // no action required
    }

    protected void handleRemovedKeys() {
        if (!removedKeys.isEmpty())     // we collect removed keys lazily on the next enter to reduce the chance of empty-group queries creating empty aggregators temporarily
        {
            for (Object removedKey : removedKeys)
            {
                aggregatorsPerGroup.remove(removedKey);
                internalHandleRemoved(removedKey);
            }
            removedKeys.clear();
        }
    }
}