/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.agg.service;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

import java.util.Collection;

/**
 * A null object implementation of the AggregationService
 * interface.
 */
public class AggregationServiceNull implements AggregationService {

    public void applyEnter(EventBean[] eventsPerStream,
                           Object optionalGroupKeyPerRow,
                           ExprEvaluatorContext exprEvaluatorContext) {
    }

    public void applyLeave(EventBean[] eventsPerStream,
                           Object optionalGroupKeyPerRow,
                           ExprEvaluatorContext exprEvaluatorContext) {
    }

    public void setCurrentAccess(Object groupKey, int agentInstanceId) {
    }

    public Object getValue(int column, int agentInstanceId) {
        return null;
    }

    public Collection<EventBean> getCollection(int column, ExprEvaluatorContext context) {
        return null;
    }

    public EventBean getEventBean(int column, ExprEvaluatorContext context) {
        return null;
    }

    public void clearResults(ExprEvaluatorContext exprEvaluatorContext)
    {
        // no state to clear
    }

    public void setRemovedCallback(AggregationRowRemovedCallback callback) {
        // not applicable
    }

}
