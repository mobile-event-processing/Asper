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
import com.espertech.esper.epl.spec.StatementSpecRaw;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.util.JavaClassHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Represents a subselect in an expression tree.
 */
public class ExprSubselectRowNode extends ExprSubselectNode
{
    private static final Log log = LogFactory.getLog(ExprSubselectRowNode.class);
    private static final long serialVersionUID = -7865711714805807559L;

    private transient SubselectMultirowType subselectMultirowType;

    /**
     * Ctor.
     * @param statementSpec is the lookup statement spec from the parser, unvalidated
     */
    public ExprSubselectRowNode(StatementSpecRaw statementSpec)
    {
        super(statementSpec);
    }

    public Class getType()
    {
        if (selectClause == null)   // wildcards allowed
        {
            return rawEventType.getUnderlyingType();
        }
        if (selectClause.length == 1) {
            return JavaClassHelper.getBoxedType(selectClause[0].getExprEvaluator().getType());
        }
        return null;
    }

    public Map<String, Object> getEventType() throws ExprValidationException {
        if ((selectClause == null) || (selectClause.length < 2)) {
            return null;
        }
        return getRowType();
    }

    public void validateSubquery(ExprValidationContext validationContext) throws ExprValidationException
    {
    }

    public Collection<EventBean> evaluateGetCollEvents(EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context) {
        if (matchingEvents == null)
        {
            return null;
        }
        if (matchingEvents.size() == 0)
        {
            return Collections.emptyList();
        }

        if (filterExpr == null && selectClause == null) {
            return matchingEvents;
        }

        // Evaluate filter
        if (filterExpr != null)
        {
            EventBean[] events = new EventBean[eventsPerStream.length + 1];
            System.arraycopy(eventsPerStream, 0, events, 1, eventsPerStream.length);

            ArrayDeque<EventBean> filtered = new ArrayDeque<EventBean>();
            for (EventBean subselectEvent : matchingEvents)
            {
                // Prepare filter expression event list
                events[0] = subselectEvent;

                Boolean pass = (Boolean) filterExpr.evaluate(events, true, context);
                if ((pass != null) && (pass))
                {
                    filtered.add(subselectEvent);
                }
            }

            if (selectClause == null)
            {
                return filtered;
            }
        }

        return null;    // should not get here, as there is no event type returned when there is a select-clause
    }

    public Collection evaluateGetCollScalar(EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context) {
        if (matchingEvents == null)
        {
            return null;
        }
        if (matchingEvents.size() == 0)
        {
            return Collections.emptyList();
        }
        if (selectClauseEvaluator == null || selectClauseEvaluator.length < 1) {
            return null;
        }

        List result = new ArrayList();
        EventBean[] events = new EventBean[eventsPerStream.length + 1];
        System.arraycopy(eventsPerStream, 0, events, 1, eventsPerStream.length);
        if (filterExpr != null)
        {
            for (EventBean subselectEvent : matchingEvents)
            {
                // Prepare filter expression event list
                events[0] = subselectEvent;

                Boolean pass = (Boolean) filterExpr.evaluate(events, true, context);
                if ((pass != null) && (pass))
                {
                    result.add(selectClauseEvaluator[0].evaluate(events, isNewData, context));
                }
            }
        }
        else {
            for (EventBean subselectEvent : matchingEvents)
            {
                // Prepare filter expression event list
                events[0] = subselectEvent;
                result.add(selectClauseEvaluator[0].evaluate(events, isNewData, context));
            }
        }

        return result;
    }

    public EventType getEventTypeSingle(EventAdapterService eventAdapterService, String statementId) throws ExprValidationException {
        if (!this.isAggregatedSubquery() || selectClause == null) {
            return null;
        }
        Map<String, Object> rowType = getRowType();
        EventType resultEventType = eventAdapterService.createAnonymousMapType(statementId + "_subquery_" + this.getSubselectNumber(), rowType);
        subselectMultirowType = new SubselectMultirowType(resultEventType, eventAdapterService);
        return resultEventType;
    }

    public EventBean evaluateGetEventBean(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Map<String, Object> row = evaluateRow(eventsPerStream, true, context);
        return subselectMultirowType.getEventAdapterService().adapterForTypedMap(row, subselectMultirowType.getEventType());
    }

    public EventType getEventTypeCollection(EventAdapterService eventAdapterService) throws ExprValidationException {
        if (selectClause == null)   // wildcards allowed
        {
            return rawEventType;
        }

        // only if selecting wildcard do we allow lambda functions
        return null;
    }

    public Class getComponentTypeCollection() throws ExprValidationException {
        if (selectClause == null)   // wildcards allowed
        {
            return null;
        }
        if (selectClauseEvaluator.length > 1) {
            return null;
        }
        return selectClauseEvaluator[0].getType();
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext)
    {
        if (matchingEvents == null)
        {
            return null;
        }
        if (matchingEvents.size() == 0)
        {
            return null;
        }
        if ((filterExpr == null) && (matchingEvents.size() > 1))
        {
            log.warn(getMultirowMessage());
            return null;
        }

        // Evaluate filter
        EventBean subSelectResult = null;
        EventBean[] events = new EventBean[eventsPerStream.length + 1];
        System.arraycopy(eventsPerStream, 0, events, 1, eventsPerStream.length);

        if (filterExpr != null)
        {
            for (EventBean subselectEvent : matchingEvents)
            {
                // Prepare filter expression event list
                events[0] = subselectEvent;

                Boolean pass = (Boolean) filterExpr.evaluate(events, true, exprEvaluatorContext);
                if ((pass != null) && (pass))
                {
                    if (subSelectResult != null)
                    {
                        log.warn(getMultirowMessage());
                        return null;
                    }
                    subSelectResult = subselectEvent;
                }
            }

            if (subSelectResult == null)
            {
                return null;
            }
        }
        else
        {
            subSelectResult = matchingEvents.iterator().next();
        }

        events[0] = subSelectResult;
        Object result;

        if (selectClause != null)
        {
            if (selectClause.length == 1) {
                result = selectClauseEvaluator[0].evaluate(events, true, exprEvaluatorContext);
            }
            else {
                result = evaluateRow(events, true, exprEvaluatorContext);
            }
        }
        else
        {
            result = events[0].getUnderlying();
        }

        return result;
    }

    @Override
    public boolean isAllowMultiColumnSelect() {
        return true;
    }

    private Map<String, Object> evaluateRow(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < selectClauseEvaluator.length; i++) {
            Object resultEntry = selectClauseEvaluator[i].evaluate(eventsPerStream, isNewData, context);
            map.put(this.selectAsNames[i], resultEntry);
        }
        return map;
    }

    private Map<String, Object> getRowType() throws ExprValidationException {
        Set<String> uniqueNames = new HashSet<String>();
        Map<String, Object> type = new LinkedHashMap<String, Object>();

        for (int i = 0; i < selectClause.length; i++) {
            String assignedName = this.selectAsNames[i];
            if (assignedName == null) {
                assignedName = selectClause[i].toExpressionString();
            }
            if (uniqueNames.add(assignedName)) {
                type.put(assignedName, selectClause[i].getExprEvaluator().getType());
            }
            else {
                throw new ExprValidationException("Column " + i + " in subquery does not have a unique column name assigned");
            }
        }
        return type;
    }

    public Object getMultirowMessage() {
        return "Subselect of statement '" + statementName + "' returned more then one row in subselect " + subselectNumber + " '" + toExpressionString() + "', returning null result";
    }

    private static class SubselectMultirowType {
        private final EventType eventType;
        private final EventAdapterService eventAdapterService;

        private SubselectMultirowType(EventType eventType, EventAdapterService eventAdapterService) {
            this.eventType = eventType;
            this.eventAdapterService = eventAdapterService;
        }

        public EventType getEventType() {
            return eventType;
        }

        public EventAdapterService getEventAdapterService() {
            return eventAdapterService;
        }
    }
}
