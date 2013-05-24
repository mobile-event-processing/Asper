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

import java.util.Map;

/**
 * Represents a constant in an expressiun tree.
 */
public class ExprConstantNodeImpl extends ExprNodeBase implements ExprConstantNode, ExprEvaluator
{
    private Object value;
    private final Class clazz;
    private static final long serialVersionUID = 3154169410675962539L;

    /**
     * Ctor.
     * @param value is the constant's value.
     */
    public ExprConstantNodeImpl(Object value)
    {
        this.value = value;
        if (value == null)
        {
            clazz = null;
        }
        else
        {
            clazz = value.getClass();
        }
    }

    public boolean isConstantValue() {
        return true;
    }

    /**
     * Ctor.
     * @param value is the constant's value.
     * @param valueType is the constant's value type.
     */
    public ExprConstantNodeImpl(Object value, Class valueType)
    {
        this.value = value;
        if (value == null)
        {
            clazz = valueType;
        }
        else
        {
            clazz = value.getClass();
        }
    }

    /**
     * Ctor - for use when the constant should return a given type and the actual value is always null.
     * @param clazz the type of the constant null.
     */
    public ExprConstantNodeImpl(Class clazz)
    {
        this.clazz = clazz;
        this.value = null;
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException
    {
    }

    public boolean isConstantResult()
    {
        return true;
    }

    public Map<String, Object> getEventType() {
        return null;
    }

    /**
     * Returns the constant's value.
     * @return value of constant
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Sets the value of the constant.
     * @param value to set
     */
    public void setValue(Object value)
    {
        this.value = value;
    }

    public Class getType()
    {
        return clazz;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        return value;
    }

    public ExprEvaluator getExprEvaluator()
    {
        return this;
    }

    public String toExpressionString()
    {
        if (value instanceof String)
        {
            return "\"" + value + '\"';
        }
        if (value == null)
        {
            return "null";
        }
        return value.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprConstantNodeImpl))
        {
            return false;
        }

        ExprConstantNodeImpl other = (ExprConstantNodeImpl) node;

        if ((other.value == null) && (this.value != null))
        {
            return false;
        }
        if ((other.value != null) && (this.value == null))
        {
            return false;
        }
        if ((other.value == null) && (this.value == null))
        {
            return true;
        }
        return other.value.equals(this.value);
    }
}
