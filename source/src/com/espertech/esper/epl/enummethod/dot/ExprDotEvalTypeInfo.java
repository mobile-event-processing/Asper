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

import com.espertech.esper.client.EventType;
import com.espertech.esper.util.JavaClassHelper;

import java.lang.reflect.Method;
import java.util.Collection;

public class ExprDotEvalTypeInfo {

    private Class scalar;
    private Class component;
    private EventType eventType;
    private EventType eventTypeColl;

    private ExprDotEvalTypeInfo(Class scalar, Class component, EventType eventType, EventType eventTypeColl) {
        this.scalar = scalar;
        this.component = component;
        this.eventType = eventType;
        this.eventTypeColl = eventTypeColl;
    }

    public static ExprDotEvalTypeInfo from(Class inputType, Class collectionComponentType, EventType lambdaType) {
        ExprDotEvalTypeInfo info = new ExprDotEvalTypeInfo(null, null, null, null);
        if (lambdaType != null) {
            info.eventTypeColl = lambdaType;
        }
        else if (collectionComponentType != null) {
            info.scalar = Collection.class;
            info.component = collectionComponentType;
        }
        else {
            info.scalar = inputType;
        }
        return info;
    }

    public boolean isScalar() {
        return scalar != null;
    }

    public static ExprDotEvalTypeInfo fromMethod(Method method) {
        Class returnType = method.getReturnType();
        if (JavaClassHelper.isImplementsInterface(returnType, Collection.class)) {
            Class componentType = JavaClassHelper.getGenericReturnType(method, true);
            return ExprDotEvalTypeInfo.componentColl(componentType);
        }
        return ExprDotEvalTypeInfo.scalarOrUnderlying(method.getReturnType());
    }

    public static ExprDotEvalTypeInfo scalarOrUnderlying(Class scalar) {
        return new ExprDotEvalTypeInfo(scalar, null, null, null);
    }

    public static ExprDotEvalTypeInfo componentColl(Class component) {
        return new ExprDotEvalTypeInfo(Collection.class, component, null, null);
    }

    public static ExprDotEvalTypeInfo eventColl(EventType eventColl) {
        return new ExprDotEvalTypeInfo(null, null, null, eventColl);
    }

    public static ExprDotEvalTypeInfo event(EventType theEvent) {
        return new ExprDotEvalTypeInfo(null, null, theEvent, null);
    }

    public Class getScalar() {
        return scalar;
    }

    public Class getComponent() {
        return component;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventType getEventTypeColl() {
        return eventTypeColl;
    }

    public String toTypeName() {
        if (component != null) {
            return "collection of " + component.getSimpleName();
        }
        else if (eventType != null) {
            return "event type '" + eventType.getName() + "'";
        }
        else if (eventTypeColl != null) {
            return "collection of events of type '" + eventTypeColl.getName() + "'";
        }
        else if (scalar != null) {
            return "class " + JavaClassHelper.getClassNameFullyQualPretty(scalar);
        }
        else {
            return "an incompatible type";
        }
    }
}
