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

package com.espertech.esper.epl.core.eval;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.core.SelectExprProcessor;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.WrapperEventType;
import com.espertech.esper.util.JavaClassHelper;

import java.util.Collections;

public class EvalInsertBeanWrapRecast implements SelectExprProcessor {

    private final WrapperEventType eventType;
    private final EventAdapterService eventAdapterService;
    private final int streamNumber;

    public EvalInsertBeanWrapRecast(WrapperEventType targetType, EventAdapterService eventAdapterService, int streamNumber, EventType[] typesPerStream)
        throws ExprValidationException
    {
        this.eventType = targetType;
        this.eventAdapterService = eventAdapterService;
        this.streamNumber = streamNumber;

        EventType sourceType = typesPerStream[streamNumber];
        Class sourceClass = sourceType.getUnderlyingType();
        Class targetClass = targetType.getUnderlyingEventType().getUnderlyingType();
        if (!JavaClassHelper.isSubclassOrImplementsInterface(sourceClass, targetClass)) {
            throw new ExprValidationException("Expression-returned event type '" + sourceType.getName() +
                    "' with underlying type '" + sourceType.getUnderlyingType().getName() +
                    "' cannot be converted target event type '" + targetType.getName() +
                    "' with underlying type '" + targetType.getUnderlyingType().getName() + "'");
        }
    }

    public EventBean process(EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        EventBean theEvent = eventsPerStream[streamNumber];
        EventBean recast = eventAdapterService.adapterForTypedBean(theEvent.getUnderlying(), eventType.getUnderlyingEventType());
        return eventAdapterService.adapterForTypedWrapper(recast, Collections.<String, Object>emptyMap(), eventType);
    }

    public EventType getResultEventType() {
        return eventType;
    }
}
