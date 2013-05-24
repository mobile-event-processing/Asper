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
import com.espertech.esper.client.annotation.AuditEnum;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.core.service.EPStatementHandle;
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.InternalEventRouter;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.util.AuditPath;

/**
 * Handler for split-stream evaluating the first where-clause matching select-clause.
 */
public class RouteResultViewHandlerFirst implements RouteResultViewHandler
{
    private final InternalEventRouter internalEventRouter;
    private final boolean[] isNamedWindowInsert;
    private final EPStatementHandle epStatementHandle;
    private final ResultSetProcessor[] processors;
    private final ExprEvaluator[] whereClauses;
    private final EventBean[] eventsPerStream = new EventBean[1];
    private final StatementContext statementContext;
    private final ExprEvaluatorContextStatement exprEvaluatorContextStatement;
    private final boolean audit;

    /**
     * Ctor.
     * @param epStatementHandle handle
     * @param internalEventRouter routes generated events
     * @param processors select clauses
     * @param whereClauses where clauses
     * @param statementContext statement services
     */
    public RouteResultViewHandlerFirst(EPStatementHandle epStatementHandle, InternalEventRouter internalEventRouter, boolean[] isNamedWindowInsert, ResultSetProcessor[] processors, ExprEvaluator[] whereClauses, StatementContext statementContext)
    {
        this.internalEventRouter = internalEventRouter;
        this.isNamedWindowInsert = isNamedWindowInsert;
        this.epStatementHandle = epStatementHandle;
        this.processors = processors;
        this.whereClauses = whereClauses;
        this.statementContext = statementContext;
        this.exprEvaluatorContextStatement = new ExprEvaluatorContextStatement(statementContext);
        this.audit = AuditEnum.INSERT.getAudit(statementContext.getAnnotations()) != null;
    }

    public boolean handle(EventBean theEvent, ExprEvaluatorContext exprEvaluatorContext)
    {
        int index = -1;
        eventsPerStream[0] = theEvent;

        for (int i = 0; i < whereClauses.length; i++)
        {
            if (whereClauses[i] == null)
            {
                index = i;
                break;
            }

            Boolean pass = (Boolean) whereClauses[i].evaluate(eventsPerStream, true, exprEvaluatorContext);
            if ((pass != null) && (pass))
            {
                index = i;
                break;
            }
        }

        if (index != -1)
        {
            UniformPair<EventBean[]> result = processors[index].processViewResult(eventsPerStream, null, false);
            if ((result != null) && (result.getFirst() != null) && (result.getFirst().length > 0))
            {
                if (audit) {
                    AuditPath.auditInsertInto(statementContext.getEngineURI(), statementContext.getStatementName(), result.getFirst()[0]);
                }
                internalEventRouter.route(result.getFirst()[0], epStatementHandle, statementContext.getInternalEventEngineRouteDest(), exprEvaluatorContextStatement, isNamedWindowInsert[index]);
            }
        }
        
        return index != -1;
    }
}
