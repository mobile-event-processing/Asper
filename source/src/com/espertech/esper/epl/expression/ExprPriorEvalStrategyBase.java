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

/**
 * Represents the 'prior' prior event function in an expression node tree.
 */
public abstract class ExprPriorEvalStrategyBase implements ExprPriorEvalStrategy
{
    public abstract EventBean getSubstituteEvent(EventBean originalEvent, boolean isNewData, int constantIndexNumber);

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, int streamNumber, ExprEvaluator evaluator, int constantIndexNumber)
    {
        EventBean originalEvent = eventsPerStream[streamNumber];

        EventBean substituteEvent = getSubstituteEvent(originalEvent, isNewData, constantIndexNumber);

        // Substitute original event with prior event, evaluate inner expression
        eventsPerStream[streamNumber] = substituteEvent;
        Object evalResult = evaluator.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        eventsPerStream[streamNumber] = originalEvent;

        return evalResult;
    }
}
