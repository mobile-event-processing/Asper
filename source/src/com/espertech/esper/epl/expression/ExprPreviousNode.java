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
import com.espertech.esper.client.EventType;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.util.JavaClassHelper;

import java.util.Collection;
import java.util.Map;

/**
 * Represents the 'prev' previous event function in an expression node tree.
 */
public class ExprPreviousNode extends ExprNodeBase implements ExprEvaluator, ExprEvaluatorEnumeration
{
    private static final long serialVersionUID = 0L;

    private final PreviousType previousType;

    private Class resultType;
    private int streamNumber;
    private Integer constantIndexNumber;
    private boolean isConstantIndex;
    private transient EventType enumerationMethodType;

    private transient ExprPreviousEvalStrategy evaluator;

    public ExprPreviousNode(PreviousType previousType)
    {
        this.previousType = previousType;
    }

    public ExprEvaluator getExprEvaluator()
    {
        return this;
    }

    public Map<String, Object> getEventType() {
        return null;
    }

    public void setEvaluator(ExprPreviousEvalStrategy evaluator) {
        this.evaluator = evaluator;
    }

    public int getStreamNumber() {
        return streamNumber;
    }

    public Integer getConstantIndexNumber() {
        return constantIndexNumber;
    }

    public boolean isConstantIndex() {
        return isConstantIndex;
    }

    public Class getResultType() {
        return resultType;
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException
    {
        if ((this.getChildNodes().size() > 2) || (this.getChildNodes().isEmpty()))
        {
            throw new ExprValidationException("Previous node must have 1 or 2 child nodes");
        }

        // add constant of 1 for previous index
        if (this.getChildNodes().size() == 1)
        {
            if (previousType == PreviousType.PREV) {
                this.getChildNodes().add(0, new ExprConstantNodeImpl(1));
            }
            else {
                this.getChildNodes().add(0, new ExprConstantNodeImpl(0));
            }
        }

        // the row recognition patterns allows "prev(prop, index)", we switch index the first position
        if (ExprNodeUtility.isConstantValueExpr(this.getChildNodes().get(1)))
        {
            ExprNode first = this.getChildNodes().get(0);
            ExprNode second = this.getChildNodes().get(1);
            this.getChildNodes().clear();
            this.getChildNodes().add(second);
            this.getChildNodes().add(first);
        }

        // Determine if the index is a constant value or an expression to evaluate
        if (this.getChildNodes().get(0).isConstantResult())
        {
            ExprNode constantNode = this.getChildNodes().get(0);
            Object value = constantNode.getExprEvaluator().evaluate(null, false, validationContext.getExprEvaluatorContext());
            if (!(value instanceof Number))
            {
                throw new ExprValidationException("Previous function requires an integer index parameter or expression");
            }

            Number valueNumber = (Number) value;
            if (JavaClassHelper.isFloatingPointNumber(valueNumber))
            {
                throw new ExprValidationException("Previous function requires an integer index parameter or expression");
            }

            constantIndexNumber = valueNumber.intValue();
            isConstantIndex = true;
        }

        // Determine stream number
        if (this.getChildNodes().get(1) instanceof ExprIdentNode) {
            ExprIdentNode identNode = (ExprIdentNode) this.getChildNodes().get(1);
            streamNumber = identNode.getStreamId();
            resultType = JavaClassHelper.getBoxedType(this.getChildNodes().get(1).getExprEvaluator().getType());
        }
        else if (this.getChildNodes().get(1) instanceof ExprStreamUnderlyingNode) {
            ExprStreamUnderlyingNode streamNode = (ExprStreamUnderlyingNode) this.getChildNodes().get(1);
            streamNumber = streamNode.getStreamId();
            resultType = JavaClassHelper.getBoxedType(this.getChildNodes().get(1).getExprEvaluator().getType());
            enumerationMethodType = validationContext.getStreamTypeService().getEventTypes()[streamNode.getStreamId()];
        }
        else
        {
            throw new ExprValidationException("Previous function requires an event property as parameter");
        }

        if (previousType == PreviousType.PREVCOUNT) {
            resultType = Long.class;
        }
        if (previousType == PreviousType.PREVWINDOW) {
            resultType = JavaClassHelper.getArrayType(resultType);
        }

        if (validationContext.getViewResourceDelegate() == null)
        {
            throw new ExprValidationException("Previous function cannot be used in this context");
        }
        validationContext.getViewResourceDelegate().addPreviousRequest(this);
    }

    public PreviousType getPreviousType()
    {
        return previousType;
    }

    public Class getType()
    {
        return resultType;
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (!isNewData) {
            return null;
        }
        return evaluator.evaluateGetCollEvents(eventsPerStream, context);
    }

    public EventBean evaluateGetEventBean(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (!isNewData) {
            return null;
        }
        return evaluator.evaluateGetEventBean(eventsPerStream, context);
    }

    public Collection evaluateGetROCollectionScalar(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (!isNewData) {
            return null;
        }
        return evaluator.evaluateGetCollScalar(eventsPerStream, context);
    }

    public EventType getEventTypeCollection(EventAdapterService eventAdapterService) throws ExprValidationException {
        if (previousType == PreviousType.PREV || previousType == PreviousType.PREVTAIL) {
            return null;
        }
        return enumerationMethodType;
    }

    public EventType getEventTypeSingle(EventAdapterService eventAdapterService, String statementId) throws ExprValidationException {
        if (previousType == PreviousType.PREV || previousType == PreviousType.PREVTAIL) {
            return enumerationMethodType;
        }
        return null;
    }

    public Class getComponentTypeCollection() throws ExprValidationException {
        if (resultType.isArray()) {
            return resultType.getComponentType();
        }
        return resultType;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        if (!isNewData) {
            return null;
        }

        return evaluator.evaluate(eventsPerStream, exprEvaluatorContext);
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(previousType.toString().toLowerCase());
        buffer.append("(");
        if ((previousType == PreviousType.PREVCOUNT || previousType == PreviousType.PREVWINDOW)) {
            buffer.append(this.getChildNodes().get(1).toExpressionString());
        }
        else {
            buffer.append(this.getChildNodes().get(0).toExpressionString());
            buffer.append(", ");
            buffer.append(this.getChildNodes().get(1).toExpressionString());
        }
        buffer.append(')');
        return buffer.toString();
    }

    @Override
    public int hashCode()
    {
        return previousType != null ? previousType.hashCode() : 0;
    }

    public boolean equalsNode(ExprNode node)
    {
        if (node == null || getClass() != node.getClass())
        {
            return false;
        }

        ExprPreviousNode that = (ExprPreviousNode) node;

        if (previousType != that.previousType)
        {
            return false;
        }

        return true;
    }
}
