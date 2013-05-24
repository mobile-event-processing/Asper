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
import com.espertech.esper.util.JavaClassHelper;

public class EvalInsertBeanRecast implements SelectExprProcessor {

    private final EventType eventType;
    private final EventAdapterService eventAdapterService;
    private final int streamNumber;

    public EvalInsertBeanRecast(EventType targetType, EventAdapterService eventAdapterService, int streamNumber, EventType[] typesPerStream)
        throws ExprValidationException
    {
        this.eventType = targetType;
        this.eventAdapterService = eventAdapterService;
        this.streamNumber = streamNumber;

        EventType sourceType = typesPerStream[streamNumber];
        Class sourceClass = sourceType.getUnderlyingType();
        Class targetClass = targetType.getUnderlyingType();
        if (!JavaClassHelper.isSubclassOrImplementsInterface(sourceClass, targetClass)) {
            throw new ExprValidationException("Expression-returned event type '" + sourceType.getName() +
                    "' with underlying type '" + sourceType.getUnderlyingType().getName() +
                    "' cannot be converted target event type '" + targetType.getName() +
                    "' with underlying type '" + targetType.getUnderlyingType().getName() + "'");
        }
    }

    public EventBean process(EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        EventBean theEvent = eventsPerStream[streamNumber];
        return eventAdapterService.adapterForTypedBean(theEvent.getUnderlying(), eventType);
    }

    public EventType getResultEventType() {
        return eventType;
    }
}
