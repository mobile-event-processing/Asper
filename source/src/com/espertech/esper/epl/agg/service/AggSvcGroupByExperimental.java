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
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

import java.util.Collection;

/**
 * Implementation for handling aggregation with grouping by group-keys.
 */
public class AggSvcGroupByExperimental extends AggregationServiceBaseGrouped
{
    // maintain a current row for random access into the aggregator state table
    // (row=groups, columns=expression nodes that have aggregation functions)
    private AggregationMethod[] currentAggregatorRow;

    private MethodResolutionService methodResolutionService;
    private Object lastrow;

    /**
     * Ctor.
     * @param evaluators - evaluate the sub-expression within the aggregate function (ie. sum(4*myNum))
     * @param prototypes - collect the aggregation state that evaluators evaluate to, act as prototypes for new aggregations
     * aggregation states for each group
     * @param methodResolutionService - factory for creating additional aggregation method instances per group key
     */
    public AggSvcGroupByExperimental(ExprEvaluator evaluators[],
                                     AggregationMethodFactory prototypes[],
                                     MethodResolutionService methodResolutionService)
    {
        super(evaluators, prototypes);
        this.methodResolutionService = methodResolutionService;
    }

    public void clearResults(ExprEvaluatorContext exprEvaluatorContext)
    {
    }

    public void applyEnter(EventBean[] eventsPerStream, Object groupByKey, ExprEvaluatorContext exprEvaluatorContext)
    {
        if (lastrow != null && lastrow.equals(groupByKey)) {
            // no action
        }
        else {
            AggregationMethod[] groupAggregators = methodResolutionService.newAggregators(aggregators, exprEvaluatorContext.getAgentInstanceId(), groupByKey);
            currentAggregatorRow = groupAggregators;
        }
        lastrow = groupByKey;

        // For this row, evaluate sub-expressions, enter result
        for (int j = 0; j < evaluators.length; j++)
        {
            Object columnResult = evaluators[j].evaluate(eventsPerStream, true, exprEvaluatorContext);
            currentAggregatorRow[j].enter(columnResult);
        }
    }

    public void applyLeave(EventBean[] eventsPerStream, Object groupByKey, ExprEvaluatorContext exprEvaluatorContext)
    {
        if (lastrow != null && lastrow.equals(groupByKey)) {
            // no action
        }
        else {
            AggregationMethod[] groupAggregators = methodResolutionService.newAggregators(aggregators, exprEvaluatorContext.getAgentInstanceId(), groupByKey);
            currentAggregatorRow = groupAggregators;
        }
        lastrow = groupByKey;

        // For this row, evaluate sub-expressions, enter result
        for (int j = 0; j < evaluators.length; j++)
        {
            Object columnResult = evaluators[j].evaluate(eventsPerStream, false, exprEvaluatorContext);
            currentAggregatorRow[j].leave(columnResult);
        }
    }

    public void setCurrentAccess(Object groupByKey, int agentInstanceId)
    {
        if (lastrow != null && lastrow.equals(groupByKey)) {
            // no action
        }
        else {
            AggregationMethod[] groupAggregators = methodResolutionService.newAggregators(aggregators, agentInstanceId, groupByKey);
            currentAggregatorRow = groupAggregators;
        }
        lastrow = groupByKey;
    }

    public Object getValue(int column, int agentInstanceId)
    {
        return currentAggregatorRow[column].getValue();
    }

    public Collection<EventBean> getCollection(int column, ExprEvaluatorContext context) {
        return null;
    }

    public EventBean getEventBean(int column, ExprEvaluatorContext context) {
        return null;
    }

    public void setRemovedCallback(AggregationRowRemovedCallback callback) {
        // not applicable
    }

}
