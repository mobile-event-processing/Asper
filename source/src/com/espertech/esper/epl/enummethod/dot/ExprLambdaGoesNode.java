/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.enummethod.dot;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.*;

import java.util.List;
import java.util.Map;

/**
 * Represents the case-when-then-else control flow function is an expression tree.
 */
public class ExprLambdaGoesNode extends ExprNodeBase implements ExprEvaluator, ExprDeclaredOrLambdaNode
{
    private static final long serialVersionUID = 5551755641199945138L;
    private List<String> goesToNames;

    public ExprLambdaGoesNode(List<String> goesToNames)
    {
        this.goesToNames = goesToNames;
    }

    public boolean validated() {
        return true;
    }

    public List<String> getGoesToNames() {
        return goesToNames;
    }

    public ExprEvaluator getExprEvaluator()
    {
        return this;
    }

    public Map<String, Object> getEventType() {
        return null;
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException {
        throw new UnsupportedOperationException();
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public Class getType()
    {
        return null;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        throw new UnsupportedOperationException();
    }

    public boolean equalsNode(ExprNode node_)
    {
        return false;
    }

    public String toExpressionString()
    {
        return null;
    }
}


