/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.agg.service;

import com.espertech.esper.epl.expression.ExprAggregateNode;

import java.util.ArrayList;
import java.util.List;

public class AggregationServiceAggExpressionDesc
{
    private ExprAggregateNode aggregationNode;
    private AggregationMethodFactory factory;

    private List<ExprAggregateNode> equivalentNodes;
    private Integer columnNum;

    /**
     * Ctor.
     * @param aggregationNode expression
     * @param factory method factory
     */
    public AggregationServiceAggExpressionDesc(ExprAggregateNode aggregationNode, AggregationMethodFactory factory)
    {
        this.aggregationNode = aggregationNode;
        this.factory = factory;
    }

    /**
     * Returns the equivalent aggregation functions.
     * @return list of agg nodes
     */
    public List<ExprAggregateNode> getEquivalentNodes()
    {
        return equivalentNodes;
    }

    /**
     * Returns the method factory.
     * @return factory
     */
    public AggregationMethodFactory getFactory()
    {
        return factory;
    }

    /**
     * Returns the column number assigned.
     * @return column number
     */
    public Integer getColumnNum()
    {
        return columnNum;
    }

    /**
     * Assigns a column number.
     * @param columnNum column number
     */
    public void setColumnNum(Integer columnNum)
    {
        this.columnNum = columnNum;
    }

    /**
     * Add an equivalent aggregation function node
     * @param aggNodeToAdd node to add
     */
    public void addEquivalent(ExprAggregateNode aggNodeToAdd)
    {
        if (equivalentNodes == null) {
            equivalentNodes = new ArrayList<ExprAggregateNode>();
        }
        equivalentNodes.add(aggNodeToAdd);
    }

    /**
     * Returns the expression.
     * @return expression
     */
    public ExprAggregateNode getAggregationNode()
    {
        return aggregationNode;
    }

    /**
     * Assigns a future to the expression
     * @param service the future
     */
    public void assignFuture(AggregationResultFuture service)
    {
        aggregationNode.setAggregationResultFuture(service, columnNum);
        if (equivalentNodes == null) {
            return;
        }
        for (ExprAggregateNode equivalentAggNode : equivalentNodes)
        {
            equivalentAggNode.setAggregationResultFuture(service, columnNum);
        }
    }
}
