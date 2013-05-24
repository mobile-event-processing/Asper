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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;

public class ExprDotStaticMethodWrapCollection implements ExprDotStaticMethodWrap {
    private static final Log log = LogFactory.getLog(ExprDotStaticMethodWrapArrayScalar.class);

    private final String methodName;
    private final Class componentType;

    public ExprDotStaticMethodWrapCollection(String methodName, Class componentType) {
        this.methodName = methodName;
        this.componentType = componentType;
    }

    public ExprDotEvalTypeInfo getTypeInfo() {
        return ExprDotEvalTypeInfo.componentColl(componentType);
    }

    public Collection convert(Object result) {
        if (result == null) {
            return null;
        }
        if (!(result instanceof Collection)) {
            log.warn("Expected collection-type input from method '" + methodName + "' but received " + result.getClass());
            return null;
        }
        return (Collection) result;
    }
}
