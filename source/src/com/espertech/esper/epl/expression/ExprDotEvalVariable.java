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
import com.espertech.esper.epl.enummethod.dot.ExprDotStaticMethodWrap;
import com.espertech.esper.epl.variable.VariableReader;

import java.util.Map;

public class ExprDotEvalVariable implements ExprEvaluator
{
    private final VariableReader variableReader;
    private final ExprDotEval[] chainEval;
    private final ExprDotStaticMethodWrap resultWrapLambda;

    public ExprDotEvalVariable(VariableReader variableReader,
                               ExprDotStaticMethodWrap resultWrapLambda,
                               ExprDotEval[] chainEval)
    {
        this.variableReader = variableReader;
        this.resultWrapLambda = resultWrapLambda;
        this.chainEval = chainEval;
    }

    public Class getType()
    {
        if (chainEval.length == 0) {
            return variableReader.getType();
        }
        else {
            return chainEval[chainEval.length - 1].getTypeInfo().getScalar();
        }
    }

    public Map<String, Object> getEventType() {
        return null;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
	{
        Object result = variableReader.getValue();
        if (result == null) {
            return null;
        }

        if (resultWrapLambda != null) {
            result = resultWrapLambda.convert(result);
        }

        for (ExprDotEval aChainEval : chainEval) {
            result = aChainEval.evaluate(result, eventsPerStream, isNewData, exprEvaluatorContext);
            if (result == null) {
                return result;
            }
        }

        return result;
    }
}
