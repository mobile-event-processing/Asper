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

import java.util.List;
import java.util.Map;

public class StatementAgentInstanceFactorySelectResult extends StatementAgentInstanceFactoryResult {

    private final EvalRootState[] patternRoots;
    private final StatementAgentInstancePostLoad optionalPostLoadJoin;
    private final Viewable[] topViews;
    private final Viewable[] eventStreamViewables;

    public StatementAgentInstanceFactorySelectResult(Viewable finalView, StopCallback stopCallback, AgentInstanceContext agentInstanceContext, AggregationService optionalAggegationService, Map<ExprSubselectNode, SubSelectStrategyHolder> subselectStrategies, Map<ExprPriorNode, ExprPriorEvalStrategy> priorNodeStrategies, Map<ExprPreviousNode, ExprPreviousEvalStrategy> previousNodeStrategies, List<StatementAgentInstancePreload> preloadList, EvalRootState[] patternRoots, StatementAgentInstancePostLoad optionalPostLoadJoin, Viewable[] topViews, Viewable[] eventStreamViewables) {
        super(finalView, stopCallback, agentInstanceContext, optionalAggegationService, subselectStrategies, priorNodeStrategies, previousNodeStrategies, preloadList);
        this.topViews = topViews;
        this.patternRoots = patternRoots;
        this.optionalPostLoadJoin = optionalPostLoadJoin;
        this.eventStreamViewables = eventStreamViewables;
    }

    public Viewable[] getTopViews() {
        return topViews;
    }

    public EvalRootState[] getPatternRoots() {
        return patternRoots;
    }

    public StatementAgentInstancePostLoad getOptionalPostLoadJoin() {
        return optionalPostLoadJoin;
    }

    public Viewable[] getEventStreamViewables() {
        return eventStreamViewables;
    }
}
