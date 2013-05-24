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
 * Represents the 'prior' prior event function in an expression node tree.
 */
public class ExprPriorNode extends ExprNodeBase implements ExprEvaluator
{
    private Class resultType;
    private int streamNumber;
    private int constantIndexNumber;
    private transient ExprPriorEvalStrategy priorStrategy;
    private transient ExprEvaluator innerEvaluator;
    private static final long serialVersionUID = -2115346817501589366L;

    @Override
    public ExprEvaluator getExprEvaluator() {
        return this;
    }

    public Map<String, Object> getEventType() {
        return null;
    }

    public int getStreamNumber() {
        return streamNumber;
    }

    public int getConstantIndexNumber() {
        return constantIndexNumber;
    }

    public void setPriorStrategy(ExprPriorEvalStrategy priorStrategy) {
        this.priorStrategy = priorStrategy;
    }

    public ExprEvaluator getInnerEvaluator() {
        return innerEvaluator;
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException
    {
        if (this.getChildNodes().size() != 2)
        {
            throw new ExprValidationException("Prior node must have 2 child nodes");
        }
        if (!(this.getChildNodes().get(0).isConstantResult()))
        {
            throw new ExprValidationException("Prior function requires an integer index parameter");
        }
        ExprNode constantNode = this.getChildNodes().get(0);
        if (constantNode.getExprEvaluator().getType() != Integer.class)
        {
            throw new ExprValidationException("Prior function requires an integer index parameter");
        }

        Object value = constantNode.getExprEvaluator().evaluate(null, false, validationContext.getExprEvaluatorContext());
        constantIndexNumber = ((Number) value).intValue();
        innerEvaluator = this.getChildNodes().get(1).getExprEvaluator();

        // Determine stream number
        // Determine stream number
        if (this.getChildNodes().get(1) instanceof ExprIdentNode) {
            ExprIdentNode identNode = (ExprIdentNode) this.getChildNodes().get(1);
            streamNumber = identNode.getStreamId();
            resultType = innerEvaluator.getType();
        }
        else if (this.getChildNodes().get(1) instanceof ExprStreamUnderlyingNode) {
            ExprStreamUnderlyingNode streamNode = (ExprStreamUnderlyingNode) this.getChildNodes().get(1);
            streamNumber = streamNode.getStreamId();
            resultType = innerEvaluator.getType();
        }
        else
        {
            throw new ExprValidationException("Previous function requires an event property as parameter");
        }

        // add request
        if (validationContext.getViewResourceDelegate() == null) {
            throw new ExprValidationException("Prior function cannot be used in this context");
        }
        validationContext.getViewResourceDelegate().addPriorNodeRequest(this);
    }

    public Class getType()
    {
        return resultType;
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        return priorStrategy.evaluate(eventsPerStream, isNewData, exprEvaluatorContext, streamNumber, innerEvaluator, constantIndexNumber);
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("prior(");
        buffer.append(this.getChildNodes().get(0).toExpressionString());
        buffer.append(',');
        buffer.append(this.getChildNodes().get(1).toExpressionString());
        buffer.append(')');
        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprPriorNode))
        {
            return false;
        }

        return true;
    }
}
