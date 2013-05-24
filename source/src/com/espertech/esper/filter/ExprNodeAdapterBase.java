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
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExprNodeAdapterBase
{
    private static final Log log = LogFactory.getLog(ExprNodeAdapterBase.class);

    protected final String statementName;
    protected final ExprNode exprNode;
    protected final ExprEvaluator exprNodeEval;
    protected final ExprEvaluatorContext evaluatorContext;

    /**
     * Ctor.
     * @param exprNode is the boolean expression
     */
    public ExprNodeAdapterBase(String statementName, ExprNode exprNode, ExprEvaluatorContext evaluatorContext)
    {
        this.statementName = statementName;
        this.exprNode = exprNode;
        this.exprNodeEval = exprNode.getExprEvaluator();
        this.evaluatorContext = evaluatorContext;
    }

    /**
     * Evaluate the boolean expression given the event as a stream zero event.
     *
     * @param theEvent is the stream zero event (current event)
     * @return boolean result of the expression
     */
    public boolean evaluate(EventBean theEvent)
    {
        return evaluatePerStream(new EventBean[] {theEvent});
    }

    protected boolean evaluatePerStream(EventBean[] eventsPerStream) {
        try {
            Boolean result = (Boolean) exprNodeEval.evaluate(eventsPerStream, true, this.evaluatorContext);
            if (result == null)
            {
                return false;
            }
            return result;
        }
        catch (RuntimeException ex) {
            log.error("Error evaluating expression '" + exprNode.toExpressionString() + "' statement '" + statementName + "': " + ex.getMessage(), ex);
            return false;
        }
    }
}
