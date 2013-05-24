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

package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.enummethod.dot.ExprDotEvalTypeInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Array;

public class ExprDotEvalArrayGet implements ExprDotEval
{
    private static final Log log = LogFactory.getLog(ExprDotEvalArrayGet.class);

    private final ExprDotEvalTypeInfo typeInfo;
    private final ExprEvaluator indexExpression;

    public ExprDotEvalArrayGet(ExprEvaluator index, Class componentType)
    {
        this.indexExpression = index;
        this.typeInfo = ExprDotEvalTypeInfo.scalarOrUnderlying(componentType);
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (target == null) {
            return null;
        }

        Object index = indexExpression.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        if (index == null) {
            return null;
        }
        if (!(index instanceof Integer)) {
            return null;
        }
        int indexNum = (Integer) index;

        if (Array.getLength(target) <= indexNum) {
            return null;
        }
        return Array.get(target, indexNum);
    }

    public ExprDotEvalTypeInfo getTypeInfo() {
        return typeInfo;
    }
}
