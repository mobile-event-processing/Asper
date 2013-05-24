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
import com.espertech.esper.util.CoercionException;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.util.SimpleNumberCoercer;
import com.espertech.esper.util.SimpleNumberCoercerFactory;

import java.util.Map;

/**
 * Represents an equals (=) comparator in a filter expressiun tree.
 */
public class ExprEqualsNodeImpl extends ExprNodeBase implements ExprEqualsNode
{
    private final boolean isNotEquals;
    private final boolean isIs;
    private transient ExprEvaluator evaluator;

    private static final long serialVersionUID = 5504809379222369952L;

    /**
     * Ctor.
     * @param isNotEquals - true if this is a (!=) not equals rather then equals, false if its a '=' equals
     * @param isIs - true when "is" or "is not" (instead of = or <>)
     */
    public ExprEqualsNodeImpl(boolean isNotEquals, boolean isIs)
    {
        this.isNotEquals = isNotEquals;
        this.isIs = isIs;
    }

    public ExprEvaluator getExprEvaluator()
    {
        return evaluator;
    }

    public boolean isNotEquals()
    {
        return isNotEquals;
    }

    public boolean isIs() {
        return isIs;
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException
    {
        // Must have 2 child nodes
        if (this.getChildNodes().size() != 2)
        {
            throw new IllegalStateException("Equals node does not have exactly 2 child nodes");
        }
        ExprEvaluator[] evaluators = ExprNodeUtility.getEvaluators(this.getChildNodes());

        // Must be the same boxed type returned by expressions under this
        Class typeOne = JavaClassHelper.getBoxedType(evaluators[0].getType());
        Class typeTwo = JavaClassHelper.getBoxedType(evaluators[1].getType());

        // Null constants can be compared for any type
        if ((typeOne == null) || (typeTwo == null))
        {
            evaluator = getEvaluator(evaluators[0], evaluators[1]);
            return;
        }

        if (typeOne.equals(typeTwo) || typeOne.isAssignableFrom(typeTwo))
        {
            evaluator = getEvaluator(evaluators[0], evaluators[1]);
            return;
        }

        // Get the common type such as Bool, String or Double and Long
        Class coercionType;
        try
        {
            coercionType = JavaClassHelper.getCompareToCoercionType(typeOne, typeTwo);
        }
        catch (CoercionException ex)
        {
            throw new ExprValidationException("Implicit conversion from datatype '" +
                    typeTwo.getSimpleName() +
                    "' to '" +
                    typeOne.getSimpleName() +
                    "' is not allowed");
        }

        // Check if we need to coerce
        if ((coercionType == JavaClassHelper.getBoxedType(typeOne)) &&
            (coercionType == JavaClassHelper.getBoxedType(typeTwo)))
        {
            evaluator = getEvaluator(evaluators[0], evaluators[1]);
        }
        else
        {
            if (!JavaClassHelper.isNumeric(coercionType))
            {
                throw new ExprValidationException("Cannot convert datatype '" + coercionType.getName() + "' to a numeric value");
            }
            SimpleNumberCoercer numberCoercerLHS = SimpleNumberCoercerFactory.getCoercer(typeOne, coercionType);
            SimpleNumberCoercer numberCoercerRHS = SimpleNumberCoercerFactory.getCoercer(typeTwo, coercionType);
            evaluator = new ExprEqualsEvaluatorCoercing(isIs, isNotEquals, evaluators[0], evaluators[1], numberCoercerLHS, numberCoercerRHS);
        }
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public Map<String, Object> getEventType() {
        return null;
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();

        buffer.append(this.getChildNodes().get(0).toExpressionString());
        buffer.append(" = ");
        buffer.append(this.getChildNodes().get(1).toExpressionString());

        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprEqualsNodeImpl))
        {
            return false;
        }

        ExprEqualsNodeImpl other = (ExprEqualsNodeImpl) node;
        return other.isNotEquals == this.isNotEquals;
    }

    private ExprEvaluator getEvaluator(ExprEvaluator lhs, ExprEvaluator rhs) {
        if (isIs) {
            return new ExprEqualsEvaluatorIs(isNotEquals, lhs, rhs);
        }
        else {
            return new ExprEqualsEvaluatorEquals(isNotEquals, lhs, rhs);
        }
    }

    public static class ExprEqualsEvaluatorCoercing implements ExprEvaluator {
        private transient boolean isIs;
        private transient boolean isNotEquals;
        private transient ExprEvaluator lhs;
        private transient ExprEvaluator rhs;
        private transient SimpleNumberCoercer numberCoercerLHS;
        private transient SimpleNumberCoercer numberCoercerRHS;

        public ExprEqualsEvaluatorCoercing(boolean isIs, boolean isNotEquals, ExprEvaluator lhs, ExprEvaluator rhs, SimpleNumberCoercer numberCoercerLHS, SimpleNumberCoercer numberCoercerRHS) {
            this.isIs = isIs;
            this.isNotEquals = isNotEquals;
            this.lhs = lhs;
            this.rhs = rhs;
            this.numberCoercerLHS = numberCoercerLHS;
            this.numberCoercerRHS = numberCoercerRHS;
        }

        public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            Object leftResult = lhs.evaluate(eventsPerStream, isNewData, context);
            Object rightResult = rhs.evaluate(eventsPerStream, isNewData, context);

            if (!isIs) {
                if (leftResult == null || rightResult == null)  // null comparison
                {
                    return null;
                }
            }
            else {
                if (leftResult == null) {
                    return rightResult == null;
                }
                if (rightResult == null) {
                    return false;
                }
            }

            Number left = numberCoercerLHS.coerceBoxed((Number) leftResult);
            Number right = numberCoercerRHS.coerceBoxed((Number) rightResult);
            return left.equals(right) ^ isNotEquals;
        }

        public Class getType() {
            return Boolean.class;
        }

        public Map<String, Object> getEventType() throws ExprValidationException {
            return null;
        }
    }

    public static class ExprEqualsEvaluatorEquals implements ExprEvaluator {
        private transient boolean isNotEquals;
        private transient ExprEvaluator lhs;
        private transient ExprEvaluator rhs;

        public ExprEqualsEvaluatorEquals(boolean notEquals, ExprEvaluator lhs, ExprEvaluator rhs) {
            isNotEquals = notEquals;
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            Object leftResult = lhs.evaluate(eventsPerStream, isNewData, context);
            Object rightResult = rhs.evaluate(eventsPerStream, isNewData, context);

            if (leftResult == null || rightResult == null)  // null comparison
            {
                return null;
            }

            return leftResult.equals(rightResult) ^ isNotEquals;
        }

        public Class getType() {
            return Boolean.class;
        }

        public Map<String, Object> getEventType() throws ExprValidationException {
            return null;
        }
    }

    public static class ExprEqualsEvaluatorIs implements ExprEvaluator {
        private transient boolean isNotEquals;
        private transient ExprEvaluator lhs;
        private transient ExprEvaluator rhs;

        public ExprEqualsEvaluatorIs(boolean notEquals, ExprEvaluator lhs, ExprEvaluator rhs) {
            isNotEquals = notEquals;
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            Object leftResult = lhs.evaluate(eventsPerStream, isNewData, context);
            Object rightResult = rhs.evaluate(eventsPerStream, isNewData, context);

            if (leftResult == null) {
                return rightResult == null ^ isNotEquals;
            }
            return (rightResult != null && leftResult.equals(rightResult)) ^ isNotEquals;

        }

        public Class getType() {
            return Boolean.class;
        }

        public Map<String, Object> getEventType() throws ExprValidationException {
            return null;
        }
    }
}
