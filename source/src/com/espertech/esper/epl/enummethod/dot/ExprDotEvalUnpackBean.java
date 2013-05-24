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

package com.espertech.esper.epl.enummethod.dot;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.expression.ExprDotEval;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

public class ExprDotEvalUnpackBean implements ExprDotEval {

    private final ExprDotEvalTypeInfo returnType;

    public ExprDotEvalUnpackBean(EventType lambdaType) {
        returnType = ExprDotEvalTypeInfo.scalarOrUnderlying(lambdaType.getUnderlyingType());
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (target == null) {
            return null;
        }
        EventBean theEvent = (EventBean) target;
        return theEvent.getUnderlying();
    }

    public ExprDotEvalTypeInfo getTypeInfo() {
        return returnType;
    }
}
