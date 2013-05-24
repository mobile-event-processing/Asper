/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.service.StatementResultService;
import com.espertech.esper.epl.core.ResultSetProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * View for the on-delete statement that handles removing events from a named window.
 */
public class NamedWindowOnUpdateViewFactory extends NamedWindowOnExprBaseViewFactory
{
    private static final Log log = LogFactory.getLog(NamedWindowOnUpdateViewFactory.class);
    private final StatementResultService statementResultService;
    private final NamedWindowUpdateHelper updateHelper;

    public NamedWindowOnUpdateViewFactory(EventType namedWindowEventType, StatementResultService statementResultService, NamedWindowUpdateHelper updateHelper) {
        super(namedWindowEventType);
        this.statementResultService = statementResultService;
        this.updateHelper = updateHelper;
    }

    public NamedWindowOnExprBaseView make(NamedWindowLookupStrategy lookupStrategy, NamedWindowRootViewInstance namedWindowRootViewInstance, AgentInstanceContext agentInstanceContext, ResultSetProcessor resultSetProcessor) {
        return new NamedWindowOnUpdateView(lookupStrategy, namedWindowRootViewInstance, agentInstanceContext, this);
    }

    public StatementResultService getStatementResultService() {
        return statementResultService;
    }

    public NamedWindowUpdateHelper getUpdateHelper() {
        return updateHelper;
    }
}