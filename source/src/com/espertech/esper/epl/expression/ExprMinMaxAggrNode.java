/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.agg.service.AggregationMethodFactory;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.type.MinMaxTypeEnum;

/**
 * Represents the min/max(distinct? ...) aggregate function is an expression tree.
 */
public class ExprMinMaxAggrNode extends ExprAggregateNodeBase
{
    private final MinMaxTypeEnum minMaxTypeEnum;
    private static final long serialVersionUID = -7828413362615586145L;

    private final boolean hasFilter;

    /**
     * Ctor.
     * @param distinct - indicator whether distinct values of all values min/max
     * @param minMaxTypeEnum - enum for whether to minimum or maximum compute
     */
    public ExprMinMaxAggrNode(boolean distinct, MinMaxTypeEnum minMaxTypeEnum, boolean hasFilter)
    {
        super(distinct);
        this.minMaxTypeEnum = minMaxTypeEnum;
        this.hasFilter = hasFilter;
    }

    public AggregationMethodFactory validateAggregationChild(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ExprEvaluatorContext exprEvaluatorContext) throws ExprValidationException
    {
        if (this.getChildNodes().size() == 0 || this.getChildNodes().size() > 2)
        {
            throw new ExprValidationException(minMaxTypeEnum.toString() + " node must have either 1 or 2 child nodes");
        }

        ExprNode child = this.getChildNodes().get(0);
        boolean hasDataWindows = ExprNodeUtility.hasRemoveStream(child, streamTypeService);
        if (hasFilter) {
            if (this.getChildNodes().size() < 2) {
                throw new ExprValidationException(minMaxTypeEnum.toString() + "-filtered aggregation function must have a filter expression as a second parameter");
            }
            super.validateFilter(this.getChildNodes().get(1).getExprEvaluator());
        }
        return new ExprMinMaxAggrNodeFactory(minMaxTypeEnum, child.getExprEvaluator().getType(), hasDataWindows, super.isDistinct(), hasFilter);
    }

    public final boolean equalsNodeAggregate(ExprAggregateNode node)
    {
        if (!(node instanceof ExprMinMaxAggrNode))
        {
            return false;
        }

        ExprMinMaxAggrNode other = (ExprMinMaxAggrNode) node;
        return other.minMaxTypeEnum == this.minMaxTypeEnum;
    }

    /**
     * Returns the indicator for minimum or maximum.
     * @return min/max indicator
     */
    public MinMaxTypeEnum getMinMaxTypeEnum()
    {
        return minMaxTypeEnum;
    }

    protected String getAggregationFunctionName()
    {
        return minMaxTypeEnum.getExpressionText();
    }
}
