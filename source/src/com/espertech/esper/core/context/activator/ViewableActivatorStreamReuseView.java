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

package com.espertech.esper.core.context.activator;

import com.espertech.esper.collection.Pair;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.service.EPServicesContext;
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.StatementAgentInstanceLock;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.spec.FilterStreamSpecCompiled;
import com.espertech.esper.epl.spec.StatementSpecCompiled;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.EventStream;

public class ViewableActivatorStreamReuseView implements ViewableActivator, StopCallback {

    private final EPServicesContext services;
    private final StatementContext statementContext;
    private final StatementSpecCompiled statementSpec;
    private final FilterStreamSpecCompiled filterStreamSpec;
    private final boolean join;
    private final ExprEvaluatorContextStatement evaluatorContextStmt;
    private final boolean filterSubselectSameStream;

    public ViewableActivatorStreamReuseView(EPServicesContext services, StatementContext statementContext, StatementSpecCompiled statementSpec, FilterStreamSpecCompiled filterStreamSpec, boolean join, ExprEvaluatorContextStatement evaluatorContextStmt, boolean filterSubselectSameStream) {
        this.services = services;
        this.statementContext = statementContext;
        this.statementSpec = statementSpec;
        this.filterStreamSpec = filterStreamSpec;
        this.join = join;
        this.evaluatorContextStmt = evaluatorContextStmt;
        this.filterSubselectSameStream = filterSubselectSameStream;
    }

    public ViewableActivationResult activate(AgentInstanceContext agentInstanceContext, boolean isSubselect, boolean isRecoveringResilient) {
        Pair<EventStream, StatementAgentInstanceLock> pair = services.getStreamService().createStream(statementContext.getStatementId(), filterStreamSpec.getFilterSpec(),
                statementContext.getFilterService(),
                agentInstanceContext.getEpStatementAgentInstanceHandle(),
                join,
                false,
                evaluatorContextStmt,
                !statementSpec.getOrderByList().isEmpty(),
                filterSubselectSameStream,
                statementContext.getAnnotations(),
                statementContext.isStatelessSelect());
        return new ViewableActivationResult(pair.getFirst(), this, pair.getSecond(), null);
    }

    public void stop() {
        services.getStreamService().dropStream(filterStreamSpec.getFilterSpec(), statementContext.getFilterService(), join, false, !statementSpec.getOrderByList().isEmpty(), filterSubselectSameStream, statementContext.isStatelessSelect());
    }
}
