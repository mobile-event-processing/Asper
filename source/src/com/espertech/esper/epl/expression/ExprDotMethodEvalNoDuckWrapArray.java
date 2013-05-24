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

import com.asper.sources.net.sf.cglib.reflect.FastMethod;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.enummethod.dot.ArrayWrappingCollection;
import com.espertech.esper.epl.enummethod.dot.ExprDotEvalTypeInfo;
import com.espertech.esper.util.JavaClassHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;

public class ExprDotMethodEvalNoDuckWrapArray extends ExprDotMethodEvalNoDuck
{
    public ExprDotMethodEvalNoDuckWrapArray(String statementName, FastMethod method, ExprEvaluator[] parameters) {
        super(statementName, method, parameters);
    }

    @Override
    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object result = super.evaluate(target, eventsPerStream, isNewData, exprEvaluatorContext);
        if (result == null || !result.getClass().isArray()) {
            return null;
        }
        return new ArrayWrappingCollection(result);
    }

    @Override
    public ExprDotEvalTypeInfo getTypeInfo() {
        return ExprDotEvalTypeInfo.componentColl(method.getReturnType().getComponentType());
    }
}
