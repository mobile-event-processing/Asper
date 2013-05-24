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

/**
 * Represents the median(...) aggregate function is an expression tree.
 */
public class ExprMedianNode extends ExprAggregateNodeBase
{
    private static final long serialVersionUID = 1762260589769465944L;

    private final boolean hasFilter;

    /**
     * Ctor.
     * @param distinct - flag indicating unique or non-unique value aggregation
     */
    public ExprMedianNode(boolean distinct, boolean hasFilter)
    {
        super(distinct);
        this.hasFilter = hasFilter;
    }

    public AggregationMethodFactory validateAggregationChild(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ExprEvaluatorContext exprEvaluatorContext) throws ExprValidationException
    {
        Class childType = super.validateNumericChildAllowFilter(streamTypeService, hasFilter);
        return new ExprMedianNodeFactory(super.isDistinct, childType, hasFilter);
    }

    protected String getAggregationFunctionName()
    {
        return "median";
    }

    protected boolean equalsNodeAggregate(ExprAggregateNode node)
    {
        if (!(node instanceof ExprMedianNode))
        {
            return false;
        }

        return true;
    }
}
