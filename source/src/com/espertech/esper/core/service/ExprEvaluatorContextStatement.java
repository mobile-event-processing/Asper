/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.service;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.script.AgentInstanceScriptContext;
import com.espertech.esper.schedule.TimeProvider;

/**
 * Represents a statement-level-only context for expression evaluation, not allowing for agents instances and result cache.
 */
public class ExprEvaluatorContextStatement implements ExprEvaluatorContext
{
    private final StatementContext statementContext;

    public ExprEvaluatorContextStatement(StatementContext statementContext) {
        this.statementContext = statementContext;
    }

    /**
     * Returns the time provider.
     * @return time provider
     */
    public TimeProvider getTimeProvider() {
        return statementContext.getTimeProvider();
    }

    public ExpressionResultCacheService getExpressionResultCacheService() {
        return statementContext.getExpressionResultCacheServiceSharable();
    }

    public int getAgentInstanceId() {
        return -1;
    }

    public EventBean getContextProperties() {
        return null;
    }

    public AgentInstanceScriptContext getAgentInstanceScriptContext() {
        return statementContext.getDefaultAgentInstanceScriptContext();
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
        return statementContext.getDefaultAgentInstanceLock();
    }
}