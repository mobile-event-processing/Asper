/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.filter;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.variable.VariableService;

public class ExprNodeAdapterMultiStreamNoTLStmtLock extends ExprNodeAdapterMultiStreamNoTL
{
    public ExprNodeAdapterMultiStreamNoTLStmtLock(String statementName, ExprNode exprNode, ExprEvaluatorContext evaluatorContext, VariableService variableService, EventBean[] prototype) {
        super(statementName, exprNode, evaluatorContext, variableService, prototype);
    }

    @Override
    protected boolean evaluatePerStream(EventBean[] eventsPerStream) {
        evaluatorContext.getAgentInstanceLock().acquireWriteLock(null);
        try {
            return super.evaluatePerStream(eventsPerStream);
        }
        finally {
            evaluatorContext.getAgentInstanceLock().releaseWriteLock(null);
        }
    }
}
