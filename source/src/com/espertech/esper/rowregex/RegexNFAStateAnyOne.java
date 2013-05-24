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
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

/**
 * Any-quantifier.
 */
public class RegexNFAStateAnyOne extends RegexNFAStateBase implements RegexNFAState
{
    /**
     * Ctor.
     * @param nodeNum node num
     * @param variableName variable
     * @param streamNum stream num
     * @param multiple indicator
     */
    public RegexNFAStateAnyOne(String nodeNum, String variableName, int streamNum, boolean multiple)
    {
        super(nodeNum, variableName, streamNum, multiple, null);
    }

    public boolean matches(EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext)
    {
        return true;
    }

    public String toString()
    {
        return "AnyEvent";
    }
}
