/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.declexpr;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.core.service.ExpressionResultCacheEntry;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprEvaluatorEnumeration;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.ExpressionDeclItem;
import com.espertech.esper.event.EventAdapterService;

import java.util.Collection;
import java.util.Map;

public abstract class ExprDeclaredEvalBase implements ExprEvaluator, ExprEvaluatorEnumeration {
    private final ExprEvaluator innerEvaluator;
    private final ExprEvaluatorEnumeration innerEvaluatorLambda;
    private final ExpressionDeclItem prototype;
    private final boolean isCache;

    public abstract EventBean[] getEventsPerStreamRewritten(EventBean[] eventsPerStream);

    public ExprDeclaredEvalBase(ExprEvaluator innerEvaluator, ExpressionDeclItem prototype, boolean isCache) {
        this.innerEvaluator = innerEvaluator;
        this.prototype = prototype;
        if (innerEvaluator instanceof ExprEvaluatorEnumeration) {
            innerEvaluatorLambda = (ExprEvaluatorEnumeration) innerEvaluator;
        }
        else {
            innerEvaluatorLambda = null;
        }
        this.isCache = isCache;
    }

    public Map<String, Object> getEventType() throws ExprValidationException {
        return innerEvaluator.getEventType();
    }

    public Class getType() {
        return innerEvaluator.getType();
    }

    public final Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {

        // rewrite streams
        EventBean[] events = getEventsPerStreamRewritten(eventsPerStream);

        Object result;
        if (isCache) {      // no the same cache as for iterator
            ExpressionResultCacheEntry<EventBean[], Object> entry = context.getExpressionResultCacheService().getDeclaredExpressionLastValue(prototype, events);
            if (entry != null) {
                return entry.getResult();
            }
            result = innerEvaluator.evaluate(events, isNewData, context);
            context.getExpressionResultCacheService().saveDeclaredExpressionLastValue(prototype, events, result);
        }
        else {
            result = innerEvaluator.evaluate(events, isNewData, context);
        }

        return result;
    }

    public final Collection<EventBean> evaluateGetROCollectionEvents(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {

        // rewrite streams
        EventBean[] events = getEventsPerStreamRewritten(eventsPerStream);

        Collection<EventBean> result;
        if (isCache) {
            ExpressionResultCacheEntry<EventBean[], Collection<EventBean>> entry = context.getExpressionResultCacheService().getDeclaredExpressionLastColl(prototype, events);
            if (entry != null) {
                return entry.getResult();
            }

            result = innerEvaluatorLambda.evaluateGetROCollectionEvents(events, isNewData, context);
            context.getExpressionResultCacheService().saveDeclaredExpressionLastColl(prototype, events, result);
            return result;
        }
        else {
            result = innerEvaluatorLambda.evaluateGetROCollectionEvents(events, isNewData, context);
        }

        return result;
    }

    public Collection evaluateGetROCollectionScalar(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {

        // rewrite streams
        EventBean[] events = getEventsPerStreamRewritten(eventsPerStream);

        Collection result;
        if (isCache) {
            ExpressionResultCacheEntry<EventBean[], Collection<EventBean>> entry = context.getExpressionResultCacheService().getDeclaredExpressionLastColl(prototype, events);
            if (entry != null) {
                return entry.getResult();
            }

            result = innerEvaluatorLambda.evaluateGetROCollectionScalar(events, isNewData, context);
            context.getExpressionResultCacheService().saveDeclaredExpressionLastColl(prototype, events, result);
            return result;
        }
        else {
            result = innerEvaluatorLambda.evaluateGetROCollectionScalar(events, isNewData, context);
        }

        return result;
    }

    public Class getComponentTypeCollection() throws ExprValidationException {
        if (innerEvaluatorLambda != null) {
            return innerEvaluatorLambda.getComponentTypeCollection();
        }
        return null;
    }

    public EventType getEventTypeCollection(EventAdapterService eventAdapterService) throws ExprValidationException {
        if (innerEvaluatorLambda != null) {
            return innerEvaluatorLambda.getEventTypeCollection(eventAdapterService);
        }
        return null;
    }

    public EventType getEventTypeSingle(EventAdapterService eventAdapterService, String statementId) throws ExprValidationException {
        if (innerEvaluatorLambda != null) {
            return innerEvaluatorLambda.getEventTypeSingle(eventAdapterService, statementId);
        }
        return null;
    }

    public EventBean evaluateGetEventBean(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return innerEvaluatorLambda.evaluateGetEventBean(eventsPerStream, isNewData, context);
    }
}