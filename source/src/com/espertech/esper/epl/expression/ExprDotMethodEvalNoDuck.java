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
import com.espertech.esper.epl.enummethod.dot.ExprDotEvalTypeInfo;
import com.espertech.esper.util.JavaClassHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;

public class ExprDotMethodEvalNoDuck implements ExprDotEval
{
    private static final Log log = LogFactory.getLog(ExprDotMethodEvalNoDuck.class);

    protected final String statementName;
    protected final FastMethod method;
    private final ExprEvaluator[] parameters;

    public ExprDotMethodEvalNoDuck(String statementName, FastMethod method, ExprEvaluator[] parameters)
    {
        this.statementName = statementName;
        this.method = method;
        this.parameters = parameters;
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (target == null) {
            return null;
        }

		Object[] args = new Object[parameters.length];
		for(int i = 0; i < args.length; i++)
		{
			args[i] = parameters[i].evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
		}

		try
		{
            return method.invoke(target, args);
		}
		catch (InvocationTargetException e)
		{
            String message = JavaClassHelper.getMessageInvocationTarget(statementName, method.getJavaMethod(), target.getClass().getName(), args, e);
            log.error(message, e.getTargetException());
		}
        return null;
    }

    public ExprDotEvalTypeInfo getTypeInfo() {
        return ExprDotEvalTypeInfo.fromMethod(method.getJavaMethod());
    }
}
