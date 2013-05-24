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


import java.lang.reflect.Method;

import com.asper.sources.net.sf.cglib.reflect.FastMethod;

public class ExprNodeUtilSingleRowMethodDesc {
    private final boolean allConstants;
    private final Class[] paramTypes;
    private final ExprEvaluator[] childEvals;
    private final Method reflectionMethod;
    private final FastMethod fastMethod;

    public ExprNodeUtilSingleRowMethodDesc(boolean allConstants, Class[] paramTypes, ExprEvaluator[] childEvals, Method reflectionMethod, FastMethod fastMethod) {
        this.allConstants = allConstants;
        this.paramTypes = paramTypes;
        this.childEvals = childEvals;
        this.reflectionMethod = reflectionMethod;
        this.fastMethod = fastMethod;
    }

    public boolean isAllConstants() {
        return allConstants;
    }

    public Class[] getParamTypes() {
        return paramTypes;
    }

    public ExprEvaluator[] getChildEvals() {
        return childEvals;
    }

    public Method getReflectionMethod() {
        return reflectionMethod;
    }

    public FastMethod getFastMethod() {
        return fastMethod;
    }
}
