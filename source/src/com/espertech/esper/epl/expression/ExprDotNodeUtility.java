/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.asper.sources.net.sf.cglib.reflect.FastClass;
import com.asper.sources.net.sf.cglib.reflect.FastMethod;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.datetime.eval.DatetimeMethodEnum;
import com.espertech.esper.epl.datetime.eval.ExprDotEvalDTFactory;
import com.espertech.esper.epl.datetime.eval.ExprDotEvalDTMethodDesc;
import com.espertech.esper.epl.datetime.eval.ExprDotNodeFilterAnalyzerDesc;
import com.espertech.esper.epl.enummethod.dot.*;
import com.espertech.esper.event.EventTypeMetadata;
import com.espertech.esper.event.arr.ObjectArrayEventType;
import com.espertech.esper.event.map.MapEventType;
import com.espertech.esper.util.JavaClassHelper;

import java.lang.reflect.Method;
import java.util.*;

public class ExprDotNodeUtility
{
    public static boolean isDatetimeOrEnumMethod(String name) {
        return EnumMethodEnum.isEnumerationMethod(name) || DatetimeMethodEnum.isDateTimeMethod(name);
    }

    public static ExprDotNodeRealizedChain getChainEvaluators(ExprDotEvalTypeInfo inputType,
                                                   List<ExprChainedSpec> chainSpec,
                                                   ExprValidationContext validationContext,
                                                   boolean isDuckTyping,
                                                   ExprDotNodeFilterAnalyzerInput inputDesc)
            throws ExprValidationException
    {
        List<ExprDotEval> methodEvals = new ArrayList<ExprDotEval>();
        ExprDotEvalTypeInfo currentInputType = inputType;
        EnumMethodEnum lastLambdaFunc = null;
        ExprChainedSpec lastElement = chainSpec.isEmpty() ? null : chainSpec.get(chainSpec.size() - 1);
        ExprDotNodeFilterAnalyzerDesc filterAnalyzerDesc = null;

        Deque<ExprChainedSpec> chainSpecStack = new ArrayDeque<ExprChainedSpec>(chainSpec);
        while (!chainSpecStack.isEmpty()) {
            ExprChainedSpec chainElement = chainSpecStack.removeFirst();
            lastLambdaFunc = null;  // reset

            // compile parameters for chain element
            ExprEvaluator[] paramEvals = new ExprEvaluator[chainElement.getParameters().size()];
            Class[] paramTypes = new Class[chainElement.getParameters().size()];
            for (int i = 0; i < chainElement.getParameters().size(); i++) {
                paramEvals[i] = chainElement.getParameters().get(i).getExprEvaluator();
                paramTypes[i] = paramEvals[i].getType();
            }

            // check if special 'size' method
            if ( (currentInputType.isScalar() && currentInputType.getScalar().isArray()) ||
                 (currentInputType.getComponent() != null)) {
                if (chainElement.getName().toLowerCase().equals("size") && paramTypes.length == 0 && lastElement == chainElement) {
                    ExprDotEvalArraySize sizeExpr = new ExprDotEvalArraySize();
                    methodEvals.add(sizeExpr);
                    currentInputType = sizeExpr.getTypeInfo();
                    continue;
                }
                if (chainElement.getName().toLowerCase().equals("get") && paramTypes.length == 1 && JavaClassHelper.getBoxedType(paramTypes[0]) == Integer.class) {
                    Class componentType = currentInputType.getComponent() != null ? currentInputType.getComponent() : currentInputType.getScalar().getComponentType();
                    ExprDotEvalArrayGet get = new ExprDotEvalArrayGet(paramEvals[0], componentType);
                    methodEvals.add(get);
                    currentInputType = get.getTypeInfo();
                    continue;
                }
            }

            // resolve lambda
            if (EnumMethodEnum.isEnumerationMethod(chainElement.getName())) {
                EnumMethodEnum enumerationMethod = EnumMethodEnum.fromName(chainElement.getName());
                ExprDotEvalEnumMethod eval = (ExprDotEvalEnumMethod) JavaClassHelper.instantiate(ExprDotEvalEnumMethod.class, enumerationMethod.getImplementation().getName());
                eval.init(enumerationMethod, chainElement.getName(), currentInputType, chainElement.getParameters(), validationContext);
                currentInputType = eval.getTypeInfo();
                if (currentInputType == null) {
                    throw new IllegalStateException("Enumeration method '" + chainElement.getName() + "' has not returned type information");
                }
                methodEvals.add(eval);
                lastLambdaFunc = enumerationMethod;
                continue;
            }

            // resolve datetime
            if (DatetimeMethodEnum.isDateTimeMethod(chainElement.getName())) {
                DatetimeMethodEnum datetimeMethod = DatetimeMethodEnum.fromName(chainElement.getName());
                ExprDotEvalDTMethodDesc datetimeImpl = ExprDotEvalDTFactory.validateMake(validationContext.getStreamTypeService(), chainSpecStack, datetimeMethod, chainElement.getName(), currentInputType, chainElement.getParameters(), inputDesc);
                currentInputType = datetimeImpl.getReturnType();
                if (currentInputType == null) {
                    throw new IllegalStateException("Date-time method '" + chainElement.getName() + "' has not returned type information");
                }
                methodEvals.add(datetimeImpl.getEval());
                filterAnalyzerDesc = datetimeImpl.getIntervalFilterDesc();
                continue;
            }

            // try to resolve as property if the last method returned a type
            if (currentInputType.getEventType() != null) {
                Class type = currentInputType.getEventType().getPropertyType(chainElement.getName());
                EventPropertyGetter getter = currentInputType.getEventType().getGetter(chainElement.getName());
                if (type != null && getter != null) {
                    ExprDotEvalProperty noduck = new ExprDotEvalProperty(getter, ExprDotEvalTypeInfo.scalarOrUnderlying(JavaClassHelper.getBoxedType(type)));
                    methodEvals.add(noduck);
                    currentInputType = ExprDotEvalTypeInfo.scalarOrUnderlying(noduck.getTypeInfo().getScalar());
                    continue;
                }

                // preresolve as method
                try {
                    if (currentInputType.isScalar()) {
                        validationContext.getMethodResolutionService().resolveMethod(currentInputType.getScalar(), chainElement.getName(), paramTypes);
                    }
                }
                catch (Exception ex) {
                    throw new ExprValidationException("Could not resolve '" + chainElement.getName() + "' to a property of event type '" + currentInputType.getEventType().getName() + "' or method on type '" + currentInputType + "'");
                }
            }

            // Try to resolve the method
            if (currentInputType.isScalar() || currentInputType.getEventType() != null) {
                try
                {
                    Class target;
                    if (currentInputType.isScalar()) {
                        target = currentInputType.getScalar();
                    }
                    else {
                        target = currentInputType.getEventType().getUnderlyingType();
                    }
                    Method method = validationContext.getMethodResolutionService().resolveMethod(target, chainElement.getName(), paramTypes);
                    FastClass declaringClass = FastClass.create(Thread.currentThread().getContextClassLoader(), method.getDeclaringClass());
                    FastMethod fastMethod = declaringClass.getMethod(method);

                    ExprDotEval eval;
                    if (currentInputType.isScalar()) {
                        // if followed by an enumeration method, convert array to collection
                        if (fastMethod.getReturnType().isArray() && !chainSpecStack.isEmpty() && EnumMethodEnum.isEnumerationMethod(chainSpecStack.getFirst().getName())) {
                            eval = new ExprDotMethodEvalNoDuckWrapArray(validationContext.getStatementName(), fastMethod, paramEvals);
                        }
                        else {
                            eval = new ExprDotMethodEvalNoDuck(validationContext.getStatementName(), fastMethod, paramEvals);
                        }
                    }
                    else {
                        eval = new ExprDotMethodEvalNoDuckUnderlying(validationContext.getStatementName(), fastMethod, paramEvals);
                    }
                    methodEvals.add(eval);
                    currentInputType = eval.getTypeInfo();
                }
                catch(Exception e)
                {
                    if (!isDuckTyping) {
                        throw new ExprValidationException(e.getMessage(), e);
                    }
                    else {
                        ExprDotMethodEvalDuck duck = new ExprDotMethodEvalDuck(validationContext.getStatementName(), validationContext.getMethodResolutionService(), chainElement.getName(), paramTypes, paramEvals);
                        methodEvals.add(duck);
                        currentInputType = duck.getTypeInfo();
                    }
                }
                continue;
            }

            String message = "Could not find event property, enumeration method or instance method named '" +
                    chainElement.getName() + "' in " + currentInputType.toTypeName();
            throw new ExprValidationException(message);
        }

        ExprDotEval[] intermediateEvals = methodEvals.toArray(new ExprDotEval[methodEvals.size()]);

        if (lastLambdaFunc != null) {
            if (currentInputType.getEventTypeColl() != null) {
                methodEvals.add(new ExprDotEvalUnpackCollEventBean(currentInputType.getEventTypeColl()));
            }
            else if (currentInputType.getEventType() != null) {
                methodEvals.add(new ExprDotEvalUnpackBean(currentInputType.getEventType()));
            }
        }

        ExprDotEval[] unpackingEvals = methodEvals.toArray(new ExprDotEval[methodEvals.size()]);
        return new ExprDotNodeRealizedChain(intermediateEvals, unpackingEvals, filterAnalyzerDesc);
    }

    public static ObjectArrayEventType makeTransientOAType(String enumMethod, String propertyName, Class type) {
        Map<String, Object> propsResult = new HashMap<String, Object>();
        propsResult.put(propertyName, type);
        String typeName = enumMethod + "__" + propertyName;
        return new ObjectArrayEventType(EventTypeMetadata.createAnonymous(typeName), typeName, 0, null, propsResult, null, null, null);
    }

    public static EventType[] getSingleLambdaParamEventType(String enumMethodUsedName, List<String> goesToNames, EventType inputEventType, Class collectionComponentType) {
        if (inputEventType != null) {
            return new EventType[] {inputEventType};
        }
        else {
            return new EventType[] {ExprDotNodeUtility.makeTransientOAType(enumMethodUsedName, goesToNames.get(0), collectionComponentType)};
        }
    }
}
