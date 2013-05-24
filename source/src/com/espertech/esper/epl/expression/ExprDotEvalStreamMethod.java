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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class ExprDotEvalStreamMethod implements ExprEvaluator
{
    private static final Log log = LogFactory.getLog(ExprDotEvalStreamMethod.class);

    private final int streamNumber;
    private final ExprDotEval[] evaluators;

    public ExprDotEvalStreamMethod(int streamNumber, ExprDotEval[] evaluators) {
        this.streamNumber = streamNumber;
        this.evaluators = evaluators;
    }

    public Class getType()
    {
        return evaluators[evaluators.length - 1].getTypeInfo().getScalar();
    }

    public Map<String, Object> getEventType() {
        return null;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
	{
        // get underlying event
        EventBean theEvent = eventsPerStream[streamNumber];
        if (theEvent == null)
        {
            return null;
        }
        Object inner = theEvent.getUnderlying();

        if (inner == null) {
            return null;
        }

        for (ExprDotEval methodEval : evaluators) {
            inner = methodEval.evaluate(inner, eventsPerStream, isNewData, exprEvaluatorContext);
            if (inner == null) {
                break;
            }
        }
        return inner;
    }
}
