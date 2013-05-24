/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.core.service.ExpressionResultCacheService;
import com.espertech.esper.epl.script.AgentInstanceScriptContext;
import com.espertech.esper.core.service.StatementAgentInstanceLock;
import com.espertech.esper.event.arr.ObjectArrayEventBean;
import com.espertech.esper.event.arr.ObjectArrayEventType;
import com.espertech.esper.schedule.TimeProvider;

/**
 * Returns the context for expression evaluation.
 */
public interface ExprEvaluatorContext
{
    public String getStatementName();

    public String getEngineURI();

    public String getStatementId();

    /**
     * Returns the time provider.
     * @return time provider
     */
    public TimeProvider getTimeProvider();

    public ExpressionResultCacheService getExpressionResultCacheService();

    public int getAgentInstanceId();

    public EventBean getContextProperties();

    public AgentInstanceScriptContext getAgentInstanceScriptContext();

    public StatementAgentInstanceLock getAgentInstanceLock();
}