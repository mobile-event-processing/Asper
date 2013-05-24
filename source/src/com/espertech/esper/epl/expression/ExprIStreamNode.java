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
 * Represents the RSTREAM() function in an expression tree.
 */
public class ExprIStreamNode extends ExprNodeBase implements ExprEvaluator
{
    private static final long serialVersionUID = -6911351346095189882L;

    /**
     * Ctor.
     */
    public ExprIStreamNode()
    {
    }

    public ExprEvaluator getExprEvaluator()
    {
        return this;
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException
    {
        if (this.getChildNodes().size() != 0)
        {
            throw new ExprValidationException("current_timestamp function node must have exactly 1 child node");
        }
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public Class getType()
    {
        return Boolean.class;
    }

    public Map<String, Object> getEventType() {
        return null;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        return isNewData;
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("istream()");
        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprIStreamNode))
        {
            return false;
        }
        return true;
    }
}
