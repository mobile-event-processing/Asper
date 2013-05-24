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

package com.espertech.esper.view.internal;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.core.service.EPStatementHandle;
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.InternalEventRouter;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.util.CollectionUtil;
import com.espertech.esper.view.ViewSupport;

import java.util.Iterator;

/**
 * View for processing split-stream syntax.
 */
public class RouteResultView extends ViewSupport
{
    private final EventType eventType;
    private RouteResultViewHandler handler;
    private ExprEvaluatorContext exprEvaluatorContext;

    /**
     * Ctor.
     * @param isFirst true for the first-where clause, false for all where-clauses
     * @param eventType output type
     * @param epStatementHandle handle
     * @param internalEventRouter routining output events
     * @param processors processors for select clauses
     * @param whereClauses where expressions
     * @param statementContext statement context
     */
    public RouteResultView(boolean isFirst, EventType eventType, EPStatementHandle epStatementHandle, InternalEventRouter internalEventRouter, boolean[] isNamedWindowInsert, ResultSetProcessor[] processors, ExprNode[] whereClauses, StatementContext statementContext)
    {
        if (whereClauses.length != processors.length)
        {
            throw new IllegalArgumentException("Number of where-clauses and processors does not match");
        }

        this.exprEvaluatorContext = new ExprEvaluatorContextStatement(statementContext);
        this.eventType = eventType;
        if (isFirst)
        {
            handler = new RouteResultViewHandlerFirst(epStatementHandle, internalEventRouter, isNamedWindowInsert, processors, ExprNodeUtility.getEvaluators(whereClauses), statementContext);
        }
        else
        {
            handler = new RouteResultViewHandlerAll(epStatementHandle, internalEventRouter, isNamedWindowInsert, processors, ExprNodeUtility.getEvaluators(whereClauses), statementContext);
        }
    }

    public void update(EventBean[] newData, EventBean[] oldData)
    {
        if (newData == null)
        {
            return;
        }

        for (EventBean bean : newData)
        {
            boolean isHandled = handler.handle(bean, exprEvaluatorContext);

            if (!isHandled)
            {
                updateChildren(new EventBean[] {bean}, null);
            }
        }
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public Iterator<EventBean> iterator()
    {
        return CollectionUtil.NULL_EVENT_ITERATOR;
    }
}
