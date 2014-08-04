/*
 * *************************************************************************************
 *  Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 *  http://esper.codehaus.org                                                          *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 * *************************************************************************************
 */

package com.espertech.esper.support.epl;

import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationMethodFactory;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.expression.ExprAggregateNode;
import com.espertech.esper.epl.expression.ExprAggregateNodeBase;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.client.EventBean;

public class SupportAggregateExprNode extends ExprAggregateNodeBase
{
    private static int validateCount;

    private Class type;
    private Object value;
    private int validateCountSnapshot;

    public static void setValidateCount(int validateCount)
    {
        SupportAggregateExprNode.validateCount = validateCount;
    }

    public SupportAggregateExprNode(Class type)
    {
        super(false);
        this.type = type;
        this.value = null;
    }

    public SupportAggregateExprNode(Object value)
    {
        super(false);
        this.type = value.getClass();
        this.value = value;
    }

    public SupportAggregateExprNode(Object value, Class type)
    {
        super(false);
        this.value = value;
        this.type = type;
    }

    protected AggregationMethodFactory validateAggregationChild(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ExprEvaluatorContext exprEvaluatorContext) throws ExprValidationException
    {
        // Keep a count for if and when this was validated
        validateCount++;
        validateCountSnapshot = validateCount;
        return null;
    }

    public Class getType()
    {
        return type;
    }

    public int getValidateCountSnapshot()
    {
        return validateCountSnapshot;
    }

    public AggregationMethod getAggregationFunction()
    {
        return null;
    }

    protected String getAggregationFunctionName()
    {
        return "support";
    }

    public boolean equalsNodeAggregate(ExprAggregateNode node)
    {
        throw new UnsupportedOperationException("not implemented");
    }

    public void evaluateEnter(EventBean[] eventsPerStream)
    {
    }

    public void evaluateLeave(EventBean[] eventsPerStream)
    {
    }

    public void setValue(Object value)
    {
        this.value = value;
    }
}
