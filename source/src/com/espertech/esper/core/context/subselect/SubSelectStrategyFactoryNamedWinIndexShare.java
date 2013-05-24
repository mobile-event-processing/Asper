/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.context.subselect;

import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.service.EPServicesContext;
import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.agg.service.AggregationServiceFactoryDesc;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.lookup.SubordTableLookupStrategy;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.Viewable;

import java.util.Collections;
import java.util.List;

/**
 * Entry holding lookup resource references for use by {@link SubSelectActivationCollection}.
 */
public class SubSelectStrategyFactoryNamedWinIndexShare implements SubSelectStrategyFactory
{
    private final SubordTableLookupStrategy namedWindowSubqueryLookup;
    private final ExprEvaluator filterExprEval;
    private final AggregationServiceFactoryDesc aggregationServiceFactory;

    public SubSelectStrategyFactoryNamedWinIndexShare(SubordTableLookupStrategy namedWindowSubqueryLookup, ExprEvaluator filterExprEval, AggregationServiceFactoryDesc aggregationServiceFactory) {
        this.namedWindowSubqueryLookup = namedWindowSubqueryLookup;
        this.filterExprEval = filterExprEval;
        this.aggregationServiceFactory = aggregationServiceFactory;
    }

    public SubSelectStrategyRealization instantiate(EPServicesContext services, Viewable viewableRoot, AgentInstanceContext agentInstanceContext, List<StopCallback> stopCallbackList) {

        SubselectAggregationPreprocessor subselectAggregationPreprocessor = null;

        AggregationService aggregationService = null;
        if (aggregationServiceFactory != null) {
            aggregationService = aggregationServiceFactory.getAggregationServiceFactory().makeService(agentInstanceContext, agentInstanceContext.getStatementContext().getMethodResolutionService());
            subselectAggregationPreprocessor = new SubselectAggregationPreprocessor(aggregationService, filterExprEval);
        }

        return new SubSelectStrategyRealization(namedWindowSubqueryLookup, subselectAggregationPreprocessor, aggregationService, Collections.<ExprPriorNode, ExprPriorEvalStrategy>emptyMap(), Collections.<ExprPreviousNode, ExprPreviousEvalStrategy>emptyMap(), null, null);
    }
}
