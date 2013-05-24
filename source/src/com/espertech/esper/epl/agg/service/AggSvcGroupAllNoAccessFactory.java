/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.agg.service;

import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.expression.ExprEvaluator;

/**
 * Implementation for handling aggregation without any grouping (no group-by).
 */
public class AggSvcGroupAllNoAccessFactory extends AggregationServiceFactoryBase
{
    public AggSvcGroupAllNoAccessFactory(ExprEvaluator evaluators[], AggregationMethodFactory aggregators[]) {
        super(evaluators, aggregators);
    }

    public AggregationService makeService(AgentInstanceContext agentInstanceContext, MethodResolutionService methodResolutionService) {

        AggregationMethod[] aggregatorsAgentInstance = methodResolutionService.newAggregators(super.aggregators, agentInstanceContext.getAgentInstanceId());
        return new AggSvcGroupAllNoAccessImpl(evaluators, aggregatorsAgentInstance);
    }
}
