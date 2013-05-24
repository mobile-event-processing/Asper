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

import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.agg.access.AggregationAccess;
import com.espertech.esper.epl.agg.access.AggregationAccessUtil;
import com.espertech.esper.epl.agg.access.AggregationAccessorSlotPair;
import com.espertech.esper.epl.core.MethodResolutionService;

/**
 * Aggregation service for use when only first/last/window aggregation functions are used an none other.
 */
public class AggSvcGroupAllAccessOnlyFactory implements AggregationServiceFactory
{
    protected final AggregationAccessorSlotPair[] accessors;
    protected final int[] streams;
    protected final boolean isJoin;

    public AggSvcGroupAllAccessOnlyFactory(AggregationAccessorSlotPair[] accessors, int[] streams, boolean join) {
        this.accessors = accessors;
        this.streams = streams;
        isJoin = join;
    }

    public AggregationService makeService(AgentInstanceContext agentInstanceContext, MethodResolutionService methodResolutionService) {
        AggregationAccess[] accesses = AggregationAccessUtil.getNewAccesses(agentInstanceContext.getAgentInstanceId(), isJoin, streams, methodResolutionService, null);
        return new AggSvcGroupAllAccessOnlyImpl(accessors, accesses);
    }
}