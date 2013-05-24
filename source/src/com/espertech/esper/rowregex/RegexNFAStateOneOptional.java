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

package com.espertech.esper.rowregex;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprNode;

/**
 * The '?' state in the regex NFA states.
 */
public class RegexNFAStateOneOptional extends RegexNFAStateBase implements RegexNFAState
{
    private ExprEvaluator exprNode;

    /**
     * Ctor.
     * @param nodeNum node num
     * @param variableName variable name
     * @param streamNum stream number
     * @param multiple true for multiple matches
     * @param isGreedy true for greedy
     * @param exprNode filter expression
     */
    public RegexNFAStateOneOptional(String nodeNum, String variableName, int streamNum, boolean multiple, boolean isGreedy, ExprNode exprNode)
    {
        super(nodeNum, variableName, streamNum, multiple, isGreedy);
        if (exprNode != null) {
            this.exprNode = exprNode.getExprEvaluator();
        }
    }

    public boolean matches(EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext)
    {
        if (exprNode == null)
        {
            return true;
        }

        Boolean result = (Boolean) exprNode.evaluate(eventsPerStream, true, exprEvaluatorContext);
        if (result != null)
        {
            return result;
        }
        return false;
    }

    public String toString()
    {
        if (exprNode == null)
        {
            return "OptionalFilterEvent";
        }
        return "OptionalFilterEvent-Filtered";
    }

}
