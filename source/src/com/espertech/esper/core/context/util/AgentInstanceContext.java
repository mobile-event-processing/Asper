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

package com.espertech.esper.core.context.util;

import com.espertech.esper.core.context.mgr.AgentInstanceFilterProxy;
import com.espertech.esper.core.service.ExpressionResultCacheService;
import com.espertech.esper.core.service.ExpressionResultCacheServiceAgentInstance;
import com.espertech.esper.core.service.StatementAgentInstanceLock;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.script.AgentInstanceScriptContext;
import com.espertech.esper.event.MappedEventBean;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.util.StopCallback;

import java.util.HashSet;
import java.util.Set;

public class AgentInstanceContext implements ExprEvaluatorContext {

    private final StatementContext statementContext;
    private final EPStatementAgentInstanceHandle epStatementAgentInstanceHandle;
    private final int agentInstanceId;
    private final AgentInstanceFilterProxy agentInstanceFilterProxy;
    private final MappedEventBean agentInstanceProperties;
    private final Set<StopCallback> terminationCallbacks = new HashSet<StopCallback>();
    private final AgentInstanceScriptContext agentInstanceScriptContext;
    private final ExpressionResultCacheService expressionResultCacheService;

    public AgentInstanceContext(StatementContext statementContext, EPStatementAgentInstanceHandle epStatementAgentInstanceHandle, int agentInstanceId, AgentInstanceFilterProxy agentInstanceFilterProxy, MappedEventBean agentInstanceProperties, AgentInstanceScriptContext agentInstanceScriptContext) {
        this.statementContext = statementContext;
        this.epStatementAgentInstanceHandle = epStatementAgentInstanceHandle;
        this.agentInstanceId = agentInstanceId;
        this.agentInstanceFilterProxy = agentInstanceFilterProxy;
        this.agentInstanceProperties = agentInstanceProperties;
        this.agentInstanceScriptContext = agentInstanceScriptContext;
        this.expressionResultCacheService = new ExpressionResultCacheServiceAgentInstance();
    }

    public AgentInstanceFilterProxy getAgentInstanceFilterProxy() {
        return agentInstanceFilterProxy;
    }

    public AgentInstanceScriptContext getAgentInstanceScriptContext() {
        return agentInstanceScriptContext;
    }

    public TimeProvider getTimeProvider() {
        return statementContext.getTimeProvider();
    }

    public ExpressionResultCacheService getExpressionResultCacheService() {
        return expressionResultCacheService;
    }

    public int getAgentInstanceId() {
        return agentInstanceId;
    }

    public StatementContext getStatementContext() {
        return statementContext;
    }

    public EPStatementAgentInstanceHandle getEpStatementAgentInstanceHandle() {
        return epStatementAgentInstanceHandle;
    }

    public MappedEventBean getContextProperties() {
        return agentInstanceProperties;
    }

    public Set<StopCallback> getTerminationCallbacks() {
        return terminationCallbacks;
    }

    public String getStatementName() {
        return statementContext.getStatementName();
    }

    public String getEngineURI() {
        return statementContext.getEngineURI();
    }

    public String getStatementId() {
        return statementContext.getStatementId();
    }

    public StatementAgentInstanceLock getAgentInstanceLock() {
        return epStatementAgentInstanceHandle.getStatementAgentInstanceLock();
    }
}
