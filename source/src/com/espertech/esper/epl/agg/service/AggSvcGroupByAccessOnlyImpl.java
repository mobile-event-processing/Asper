/*
 * *************************************************************************************
 *  Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 *  http://esper.codehaus.org                                                          *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 * *************************************************************************************
 */

package com.espertech.esper.epl.agg.service;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.agg.access.AggregationAccess;
import com.espertech.esper.epl.agg.access.AggregationAccessUtil;
import com.espertech.esper.epl.agg.access.AggregationAccessorSlotPair;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Aggregation service for use when only first/last/window aggregation functions are used an none other.
 */
public class AggSvcGroupByAccessOnlyImpl implements AggregationService, AggregationResultFuture
{
    private final MethodResolutionService methodResolutionService;
    private final Map<Object, AggregationAccess[]> accessMap;
    private final AggregationAccessorSlotPair[] accessors;
    private final int[] streams;
    private final boolean isJoin;

    private AggregationAccess[] currentAccess;

    /**
     * Ctor.
     * @param methodResolutionService factory service for implementations
     * @param accessors accessor definitions
     * @param streams streams in join
     * @param isJoin true for join, false for single-stream
     */
    public AggSvcGroupByAccessOnlyImpl(MethodResolutionService methodResolutionService,
                                                   AggregationAccessorSlotPair[] accessors,
                                                   int[] streams,
                                                   boolean isJoin)
    {
        this.methodResolutionService = methodResolutionService;
        this.accessMap = new HashMap<Object, AggregationAccess[]>();
        this.accessors = accessors;
        this.streams = streams;
        this.isJoin = isJoin;
    }

    public void applyEnter(EventBean[] eventsPerStream, Object groupKey, ExprEvaluatorContext exprEvaluatorContext)
    {
        AggregationAccess[] row = getAssertRow(exprEvaluatorContext.getAgentInstanceId(), groupKey);
        for (AggregationAccess access : row) {
            access.applyEnter(eventsPerStream);
        }
    }

    public void applyLeave(EventBean[] eventsPerStream, Object groupKey, ExprEvaluatorContext exprEvaluatorContext)
    {
        AggregationAccess[] row = getAssertRow(exprEvaluatorContext.getAgentInstanceId(), groupKey);
        for (AggregationAccess access : row) {
            access.applyLeave(eventsPerStream);
        }
    }

    public void setCurrentAccess(Object groupKey, int agentInstanceId)
    {
        currentAccess = getAssertRow(agentInstanceId, groupKey);
    }

    public Object getValue(int column, int agentInstanceId)
    {
        AggregationAccessorSlotPair pair = accessors[column];
        return pair.getAccessor().getValue(currentAccess[pair.getSlot()]);
    }

    public Collection<EventBean> getCollection(int column, ExprEvaluatorContext context) {
        AggregationAccessorSlotPair pair = accessors[column];
        return pair.getAccessor().getCollectionReadOnly(currentAccess[pair.getSlot()]);
    }

    public EventBean getEventBean(int column, ExprEvaluatorContext context) {
        AggregationAccessorSlotPair pair = accessors[column];
        return pair.getAccessor().getEventBean(currentAccess[pair.getSlot()]);
    }

    public void clearResults(ExprEvaluatorContext exprEvaluatorContext)
    {
        accessMap.clear();
    }

    private AggregationAccess[] getAssertRow(int agentInstanceId, Object groupKey) {
        AggregationAccess[] row = accessMap.get(groupKey);
        if (row != null) {
            return row;
        }

        row = AggregationAccessUtil.getNewAccesses(agentInstanceId, isJoin, streams, methodResolutionService, groupKey);
        accessMap.put(groupKey, row);
        return row;
    }

    public void setRemovedCallback(AggregationRowRemovedCallback callback) {
        // not applicable
    }

}
