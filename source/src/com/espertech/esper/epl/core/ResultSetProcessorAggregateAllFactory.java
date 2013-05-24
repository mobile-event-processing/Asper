/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.expression.ExprEvaluator;

/**
 * Result set processor prototype for the case: aggregation functions used in the select clause, and no group-by,
 * and not all of the properties in the select clause are under an aggregation function.
 */
public class ResultSetProcessorAggregateAllFactory implements ResultSetProcessorFactory
{
    private final SelectExprProcessor selectExprProcessor;
    private final ExprEvaluator optionalHavingNode;
    private final boolean isSelectRStream;
    private final boolean isUnidirectional;

    /**
     * Ctor.
     * @param selectExprProcessor - for processing the select expression and generting the final output rows
     * @param optionalHavingNode - having clause expression node
     * @param isSelectRStream - true if remove stream events should be generated
     * @param isUnidirectional - true if unidirectional join
     */
    public ResultSetProcessorAggregateAllFactory(SelectExprProcessor selectExprProcessor,
                                                 ExprEvaluator optionalHavingNode,
                                                 boolean isSelectRStream,
                                                 boolean isUnidirectional)
    {
        this.selectExprProcessor = selectExprProcessor;
        this.optionalHavingNode = optionalHavingNode;
        this.isSelectRStream = isSelectRStream;
        this.isUnidirectional = isUnidirectional;
    }

    public ResultSetProcessor instantiate(OrderByProcessor orderByProcessor, AggregationService aggregationService, AgentInstanceContext agentInstanceContext) {
        return new ResultSetProcessorAggregateAll(this, selectExprProcessor, orderByProcessor, aggregationService, agentInstanceContext);
    }

    public EventType getResultEventType()
    {
        return selectExprProcessor.getResultEventType();
    }

    public boolean hasAggregation() {
        return true;
    }

    public ExprEvaluator getOptionalHavingNode() {
        return optionalHavingNode;
    }

    public boolean isSelectRStream() {
        return isSelectRStream;
    }

    public boolean isUnidirectional() {
        return isUnidirectional;
    }
}
