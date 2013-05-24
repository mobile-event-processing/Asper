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
import com.espertech.esper.epl.agg.access.AggregationAccessorSlotPair;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

import java.util.Collection;

/**
 * Aggregation service for use when only first/last/window aggregation functions are used an none other.
 */
public class AggSvcGroupAllAccessOnlyImpl implements AggregationService, AggregationResultFuture
{
    private final AggregationAccessorSlotPair[] accessors;
    protected final AggregationAccess[] accesses;

    public AggSvcGroupAllAccessOnlyImpl(AggregationAccessorSlotPair[] accessors, AggregationAccess[] accesses) {
        this.accessors = accessors;
        this.accesses = accesses;
    }

    public void applyEnter(EventBean[] eventsPerStream, Object groupKey, ExprEvaluatorContext exprEvaluatorContext)
    {
        for (AggregationAccess access : accesses) {
            access.applyEnter(eventsPerStream);
        }
    }

    public void applyLeave(EventBean[] eventsPerStream, Object groupKey, ExprEvaluatorContext exprEvaluatorContext)
    {
        for (AggregationAccess access : accesses) {
            access.applyLeave(eventsPerStream);
        }
    }

    public void setCurrentAccess(Object groupKey, int agentInstanceId)
    {
        // no implementation required
    }

    public Object getValue(int column, int agentInstanceId)
    {
        AggregationAccessorSlotPair pair = accessors[column];
        return pair.getAccessor().getValue(accesses[pair.getSlot()]);
    }

    public EventBean getEventBean(int column, ExprEvaluatorContext context) {
        AggregationAccessorSlotPair pair = accessors[column];
        return pair.getAccessor().getEventBean(accesses[pair.getSlot()]);
    }

    public Collection<EventBean> getCollection(int column, ExprEvaluatorContext context)
    {
        AggregationAccessorSlotPair pair = accessors[column];
        return pair.getAccessor().getCollectionReadOnly(accesses[pair.getSlot()]);
    }

    public void clearResults(ExprEvaluatorContext exprEvaluatorContext)
    {
        for (AggregationAccess access : accesses) {
            access.clear();
        }
    }

    public void setRemovedCallback(AggregationRowRemovedCallback callback) {
        // not applicable
    }

}