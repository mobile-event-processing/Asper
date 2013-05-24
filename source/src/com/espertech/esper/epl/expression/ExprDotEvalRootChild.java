/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.enummethod.dot.ExprDotEvalTypeInfo;
import com.espertech.esper.event.EventAdapterService;

import java.util.Collection;
import java.util.Map;

public class ExprDotEvalRootChild implements ExprEvaluator, ExprEvaluatorEnumeration
{
    private final InnerEvaluator innerEvaluator;
    private final ExprDotEval[] evalIteratorEventBean;
    private final ExprDotEval[] evalUnpacking;

    public ExprDotEvalRootChild(ExprEvaluator rootNodeEvaluator, ExprEvaluatorEnumeration rootLambdaEvaluator, ExprDotEvalTypeInfo typeInfo, ExprDotEval[] evalIteratorEventBean, ExprDotEval[] evalUnpacking) {
        if (rootLambdaEvaluator != null) {
            if (typeInfo.getEventTypeColl() != null) {
                innerEvaluator = new InnerEvaluatorEventCollection(rootLambdaEvaluator, typeInfo.getEventTypeColl());
            }
            else if (typeInfo.getEventType() != null) {
                innerEvaluator = new InnerEvaluatorEventBean(rootLambdaEvaluator, typeInfo.getEventType());
            }
            else {
                innerEvaluator = new InnerEvaluatorScalarCollection(rootLambdaEvaluator, typeInfo.getComponent());
            }
        }
        else {
            innerEvaluator = new InnerEvaluatorScalar(rootNodeEvaluator);
        }
        this.evalUnpacking = evalUnpacking;
        this.evalIteratorEventBean = evalIteratorEventBean;
    }

    public Class getType()
    {
        return evalUnpacking[evalUnpacking.length - 1].getTypeInfo().getScalar();
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context)
    {
        Object inner = innerEvaluator.evaluate(eventsPerStream, isNewData, context);
        inner = evaluateChain(evalUnpacking, inner, eventsPerStream, isNewData, context);
        return inner;
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object inner = innerEvaluator.evaluateGetROCollectionEvents(eventsPerStream, isNewData, context);
        inner = evaluateChain(evalIteratorEventBean, inner, eventsPerStream, isNewData, context);
        if (inner instanceof Collection) {
            return (Collection<EventBean>) inner;
        }
        return null;
    }

    public Collection evaluateGetROCollectionScalar(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object inner = innerEvaluator.evaluateGetROCollectionScalar(eventsPerStream, isNewData, context);
        inner = evaluateChain(evalIteratorEventBean, inner, eventsPerStream, isNewData, context);
        if (inner instanceof Collection) {
            return (Collection) inner;
        }
        return null;
    }

    private static Object evaluateChain(ExprDotEval[] evaluators, Object inner, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (inner == null) {
            return null;
        }
        for (ExprDotEval methodEval : evaluators) {
            inner = methodEval.evaluate(inner, eventsPerStream, isNewData, context);
            if (inner == null) {
                break;
            }
        }
        return inner;
    }


    public EventType getEventTypeCollection(EventAdapterService eventAdapterService) throws ExprValidationException {
        return innerEvaluator.getEventTypeCollection();
    }

    public Class getComponentTypeCollection() throws ExprValidationException {
        return innerEvaluator.getComponentTypeCollection();
    }

    public Map<String, Object> getEventType() throws ExprValidationException {
        return null;
    }

    public EventType getEventTypeSingle(EventAdapterService eventAdapterService, String statementId) throws ExprValidationException {
        return null;
    }

    public EventBean evaluateGetEventBean(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    private static interface InnerEvaluator {
        public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext);
        public Collection<EventBean> evaluateGetROCollectionEvents(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context);
        public Collection evaluateGetROCollectionScalar(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context);
        public EventBean evaluateGetEventBean(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context);

        public EventType getEventTypeCollection() throws ExprValidationException;
        public EventType getEventTypeSingle() throws ExprValidationException;
        public Class getComponentTypeCollection() throws ExprValidationException;
    }

    private static class InnerEvaluatorEventBean implements InnerEvaluator {

        private final ExprEvaluatorEnumeration rootLambdaEvaluator;
        private final EventType eventType;

        private InnerEvaluatorEventBean(ExprEvaluatorEnumeration rootLambdaEvaluator, EventType eventType) {
            this.rootLambdaEvaluator = rootLambdaEvaluator;
            this.eventType = eventType;
        }

        public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return rootLambdaEvaluator.evaluateGetEventBean(eventsPerStream, isNewData, context);
        }

        public Collection<EventBean> evaluateGetROCollectionEvents(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return rootLambdaEvaluator.evaluateGetROCollectionEvents(eventsPerStream, isNewData, context);
        }

        public Collection evaluateGetROCollectionScalar(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return rootLambdaEvaluator.evaluateGetROCollectionScalar(eventsPerStream, isNewData, context);
        }

        public EventType getEventTypeCollection() throws ExprValidationException {
            return null;
        }

        public Class getComponentTypeCollection() throws ExprValidationException {
            return null;
        }

        public EventBean evaluateGetEventBean(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return rootLambdaEvaluator.evaluateGetEventBean(eventsPerStream, isNewData, context);
        }

        public EventType getEventTypeSingle() throws ExprValidationException {
            return eventType;
        }
    }

    private static class InnerEvaluatorScalarCollection implements InnerEvaluator {

        private final ExprEvaluatorEnumeration rootLambdaEvaluator;
        private final Class componentType;

        private InnerEvaluatorScalarCollection(ExprEvaluatorEnumeration rootLambdaEvaluator, Class componentType) {
            this.rootLambdaEvaluator = rootLambdaEvaluator;
            this.componentType = componentType;
        }

        public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
            return rootLambdaEvaluator.evaluateGetROCollectionScalar(eventsPerStream, isNewData, exprEvaluatorContext);
        }

        public Collection<EventBean> evaluateGetROCollectionEvents(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return rootLambdaEvaluator.evaluateGetROCollectionEvents(eventsPerStream, isNewData, context);
        }

        public Collection evaluateGetROCollectionScalar(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return rootLambdaEvaluator.evaluateGetROCollectionScalar(eventsPerStream, isNewData, context);
        }

        public EventType getEventTypeCollection() throws ExprValidationException {
            return null;
        }

        public Class getComponentTypeCollection() throws ExprValidationException {
            return componentType;
        }

        public EventBean evaluateGetEventBean(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return null;
        }

        public EventType getEventTypeSingle() throws ExprValidationException {
            return null;
        }
    }

    private static class InnerEvaluatorEventCollection implements InnerEvaluator {

        private final ExprEvaluatorEnumeration rootLambdaEvaluator;
        private final EventType eventType;

        private InnerEvaluatorEventCollection(ExprEvaluatorEnumeration rootLambdaEvaluator, EventType eventType) {
            this.rootLambdaEvaluator = rootLambdaEvaluator;
            this.eventType = eventType;
        }

        public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
            return rootLambdaEvaluator.evaluateGetROCollectionEvents(eventsPerStream, isNewData, exprEvaluatorContext);
        }

        public Collection<EventBean> evaluateGetROCollectionEvents(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return rootLambdaEvaluator.evaluateGetROCollectionEvents(eventsPerStream, isNewData, context);
        }

        public Collection evaluateGetROCollectionScalar(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return rootLambdaEvaluator.evaluateGetROCollectionEvents(eventsPerStream, isNewData, context);
        }

        public EventType getEventTypeCollection() throws ExprValidationException {
            return eventType;
        }

        public Class getComponentTypeCollection() throws ExprValidationException {
            return null;
        }

        public EventBean evaluateGetEventBean(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return null;
        }

        public EventType getEventTypeSingle() throws ExprValidationException {
            return null;
        }
    }

    private static class InnerEvaluatorScalar implements InnerEvaluator {

        private ExprEvaluator rootEvaluator;

        private InnerEvaluatorScalar(ExprEvaluator rootEvaluator) {
            this.rootEvaluator = rootEvaluator;
        }

        public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
            return rootEvaluator.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        }

        public Collection<EventBean> evaluateGetROCollectionEvents(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return null;
        }

        public Collection evaluateGetROCollectionScalar(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return null;
        }

        public EventType getEventTypeCollection() throws ExprValidationException {
            return null;
        }

        public Class getComponentTypeCollection() throws ExprValidationException {
            return null;
        }

        public EventBean evaluateGetEventBean(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            return null;
        }

        public EventType getEventTypeSingle() throws ExprValidationException {
            return null;
        }
    }
}
