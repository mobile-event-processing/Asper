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

import com.espertech.esper.epl.expression.*;
import com.espertech.esper.client.EventBean;

import java.util.Map;

public class SupportExprNode extends ExprNodeBase implements ExprEvaluator
{
    private static int validateCount;

    private Class type;
    private Object value;
    private int validateCountSnapshot;

    public static void setValidateCount(int validateCount)
    {
        SupportExprNode.validateCount = validateCount;
    }

    public SupportExprNode(Class type)
    {
        this.type = type;
        this.value = null;
    }

    public SupportExprNode(Object value)
    {
        this.type = value.getClass();
        this.value = value;
    }

    public SupportExprNode(Object value, Class type)
    {
        this.value = value;
        this.type = type;
    }

    public Map<String, Object> getEventType() {
        return null;
    }

    public ExprEvaluator getExprEvaluator()
    {
        return this;
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException {
        // Keep a count for if and when this was validated
        validateCount++;
        validateCountSnapshot = validateCount;
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public Class getType()
    {
        return type;
    }

    public int getValidateCountSnapshot()
    {
        return validateCountSnapshot;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context)
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public String toExpressionString()
    {
        if (value instanceof String)
        {
            return "\"" + value + "\"";
        }
        else
        {
            if (value == null)
            {
                return "null";
            }
        }
        return value.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        throw new UnsupportedOperationException("not implemented");
    }
}
