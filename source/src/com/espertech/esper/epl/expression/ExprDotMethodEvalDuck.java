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

import com.asper.sources.net.sf.cglib.reflect.FastClass;
import com.asper.sources.net.sf.cglib.reflect.FastMethod;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.enummethod.dot.ExprDotEvalTypeInfo;
import com.espertech.esper.util.JavaClassHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ExprDotMethodEvalDuck implements ExprDotEval
{
    private static final Log log = LogFactory.getLog(ExprDotMethodEvalDuck.class);

    private final String statementName;
    private final MethodResolutionService methodResolutionService;
    private final String methodName;
    private final Class[] parameterTypes;
    private final ExprEvaluator[] parameters;

    private Map<Class, FastMethod> cache;

    public ExprDotMethodEvalDuck(String statementName, MethodResolutionService methodResolutionService, String methodName, Class[] parameterTypes, ExprEvaluator[] parameters)
    {
        this.statementName = statementName;
        this.methodResolutionService = methodResolutionService;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        cache = new HashMap<Class, FastMethod>();
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (target == null) {
            return null;
        }

        FastMethod method;
        if (cache.containsKey(target.getClass())) {
            method = cache.get(target.getClass());
        }
        else {
            method = getFastMethod(target.getClass());
            cache.put(target.getClass(), method);
        }

        if (method == null) {
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

    private FastMethod getFastMethod(Class clazz)
    {
        try
        {
            Method method = methodResolutionService.resolveMethod(clazz, methodName, parameterTypes);
            FastClass declaringClass = FastClass.create(Thread.currentThread().getContextClassLoader(), method.getDeclaringClass());
            return declaringClass.getMethod(method);
        }
        catch(Exception e)
        {
            log.debug("Not resolved for class '" + clazz.getName() + "' method '" + methodName + "'");
        }
        return null;
    }

    public ExprDotEvalTypeInfo getTypeInfo() {
        return ExprDotEvalTypeInfo.scalarOrUnderlying(Object.class);
    }
}
