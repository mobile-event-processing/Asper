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

package com.espertech.esper.core.context.factory;

import com.espertech.esper.core.context.subselect.SubSelectStrategyHolder;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.pattern.EvalRootState;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.Viewable;

import java.util.Collections;
import java.util.Map;

public class StatementAgentInstanceFactoryOnTriggerResult extends StatementAgentInstanceFactoryResult {

    private final EvalRootState optPatternRoot;

    public StatementAgentInstanceFactoryOnTriggerResult(Viewable finalView, StopCallback stopCallback, AgentInstanceContext agentInstanceContext, AggregationService aggregationService, Map<ExprSubselectNode, SubSelectStrategyHolder> subselectStrategies,
                                                        EvalRootState optPatternRoot) {
        super(finalView, stopCallback, agentInstanceContext, aggregationService, subselectStrategies,
                Collections.<ExprPriorNode, ExprPriorEvalStrategy>emptyMap(),
                Collections.<ExprPreviousNode, ExprPreviousEvalStrategy>emptyMap(),
                Collections.<StatementAgentInstancePreload>emptyList());
        this.optPatternRoot = optPatternRoot;
    }

    public EvalRootState getOptPatternRoot() {
        return optPatternRoot;
    }
}
