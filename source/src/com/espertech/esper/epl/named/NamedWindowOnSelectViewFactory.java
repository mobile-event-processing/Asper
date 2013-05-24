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
import com.espertech.esper.client.annotation.AuditEnum;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.service.EPStatementHandle;
import com.espertech.esper.core.service.InternalEventRouteDest;
import com.espertech.esper.core.service.InternalEventRouter;
import com.espertech.esper.core.service.StatementResultService;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.event.EventBeanReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * View for the on-select statement that handles selecting events from a named window.
 */
public class NamedWindowOnSelectViewFactory extends NamedWindowOnExprBaseViewFactory
{
    private static final Log log = LogFactory.getLog(NamedWindowOnSelectViewFactory.class);

    private final InternalEventRouter internalEventRouter;
    private final boolean addToFront;
    private final EPStatementHandle statementHandle;
    private final EventBeanReader eventBeanReader;
    private final boolean isDistinct;
    private final EventType outputEventType;
    private final StatementResultService statementResultService;
    private final InternalEventRouteDest internalEventRouteDest;
    private final boolean deleteAndSelect;

    public NamedWindowOnSelectViewFactory(EventType namedWindowEventType, InternalEventRouter internalEventRouter, boolean addToFront, EPStatementHandle statementHandle, EventBeanReader eventBeanReader, boolean distinct, EventType outputEventType, StatementResultService statementResultService, InternalEventRouteDest internalEventRouteDest, boolean deleteAndSelect) {
        super(namedWindowEventType);
        this.internalEventRouter = internalEventRouter;
        this.addToFront = addToFront;
        this.statementHandle = statementHandle;
        this.eventBeanReader = eventBeanReader;
        isDistinct = distinct;
        this.outputEventType = outputEventType;
        this.statementResultService = statementResultService;
        this.internalEventRouteDest = internalEventRouteDest;
        this.deleteAndSelect = deleteAndSelect;
    }

    public NamedWindowOnExprBaseView make(NamedWindowLookupStrategy lookupStrategy, NamedWindowRootViewInstance namedWindowRootViewInstance, AgentInstanceContext agentInstanceContext, ResultSetProcessor resultSetProcessor) {
        boolean audit = AuditEnum.INSERT.getAudit(agentInstanceContext.getStatementContext().getAnnotations()) != null;
        return new NamedWindowOnSelectView(lookupStrategy, namedWindowRootViewInstance, agentInstanceContext, this, resultSetProcessor, audit, deleteAndSelect);
    }

    public InternalEventRouter getInternalEventRouter() {
        return internalEventRouter;
    }

    public boolean isAddToFront() {
        return addToFront;
    }

    public EPStatementHandle getStatementHandle() {
        return statementHandle;
    }

    public EventBeanReader getEventBeanReader() {
        return eventBeanReader;
    }

    public boolean isDistinct() {
        return isDistinct;
    }

    public StatementResultService getStatementResultService() {
        return statementResultService;
    }

    public InternalEventRouteDest getInternalEventRouteDest() {
        return internalEventRouteDest;
    }
}
