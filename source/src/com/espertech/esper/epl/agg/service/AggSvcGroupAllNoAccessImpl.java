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
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

import java.util.Collection;

/**
 * Implementation for handling aggregation without any grouping (no group-by).
 */
public class AggSvcGroupAllNoAccessImpl extends AggregationServiceBaseUngrouped
{
    /**
     * Ctor.
     * @param evaluators - evaluate the sub-expression within the aggregate function (ie. sum(4*myNum))
     * @param aggregators - collect the aggregation state that evaluators evaluate to
     */
    public AggSvcGroupAllNoAccessImpl(ExprEvaluator evaluators[], AggregationMethod aggregators[])
    {
        super(evaluators, aggregators);
    }

    public void applyEnter(EventBean[] eventsPerStream, Object optionalGroupKeyPerRow, ExprEvaluatorContext exprEvaluatorContext)
    {
        for (int j = 0; j < evaluators.length; j++)
        {
            Object columnResult = evaluators[j].evaluate(eventsPerStream, true, exprEvaluatorContext);
            aggregators[j].enter(columnResult);
        }
    }

    public void applyLeave(EventBean[] eventsPerStream, Object optionalGroupKeyPerRow, ExprEvaluatorContext exprEvaluatorContext)
    {
        for (int j = 0; j < evaluators.length; j++)
        {
            Object columnResult = evaluators[j].evaluate(eventsPerStream, false, exprEvaluatorContext);
            aggregators[j].leave(columnResult);
        }
    }

    public void setCurrentAccess(Object groupKey, int agentInstanceId)
    {
        // no action needed - this implementation does not group and the current row is the single group
    }

    public Object getValue(int column, int agentInstanceId)
    {
        return aggregators[column].getValue();
    }

    public Collection<EventBean> getCollection(int column, ExprEvaluatorContext context) {
        return null;
    }

    public EventBean getEventBean(int column, ExprEvaluatorContext context) {
        return null;
    }

    public void clearResults(ExprEvaluatorContext exprEvaluatorContext)
    {
        for (AggregationMethod aggregator : aggregators)
        {
            aggregator.clear();
        }
    }

    public void setRemovedCallback(AggregationRowRemovedCallback callback) {
        // not applicable
    }
}
