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
import com.espertech.esper.epl.agg.service.AggregationMethodFactory;
import com.espertech.esper.epl.agg.service.AggregationResultFuture;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.util.JavaClassHelper;

import java.util.Map;

/**
 * Base expression node that represents an aggregation function such as 'sum' or 'count'.
 * <p>
 * In terms of validation each concrete aggregation node must implement it's own validation.
 * <p>
 * In terms of evaluation this base class will ask the assigned {@link com.espertech.esper.epl.agg.service.AggregationResultFuture} for the current state,
 * using a column number assigned to the node.
 * <p>
 * Concrete subclasses must supply an aggregation state prototype node {@link com.espertech.esper.epl.agg.aggregator.AggregationMethod} that reflects
 * each group's (there may be group-by critera) current aggregation state.
 */
public abstract class ExprAggregateNodeBase extends ExprNodeBase implements ExprEvaluator, ExprAggregateNode
{
	private static final long serialVersionUID = 4859196214837888423L;

    protected transient AggregationResultFuture aggregationResultFuture;
	protected int column;
    private transient AggregationMethodFactory aggregationMethodFactory;

    /**
     * Indicator for whether the aggregation is distinct - i.e. only unique values are considered.
     */
    protected boolean isDistinct;

    /**
     * Returns the aggregation function name for representation in a generate expression string.
     * @return aggregation function name
     */
    protected abstract String getAggregationFunctionName();

    /**
     * Return true if a expression aggregate node semantically equals the current node, or false if not.
     * <p>For use by the equalsNode implementation which compares the distinct flag.
     * @param node to compare to
     * @return true if semantically equal, or false if not equals
     */
    protected abstract boolean equalsNodeAggregate(ExprAggregateNode node);

    /**
     * Gives the aggregation node a chance to validate the sub-expression types.
     * @param streamTypeService is the types per stream
     * @param methodResolutionService used for resolving method and function names
     * @param exprEvaluatorContext context for expression evaluation
     * @return aggregation function factory to use
     * @throws com.espertech.esper.epl.expression.ExprValidationException when expression validation failed
     */
    protected abstract AggregationMethodFactory validateAggregationChild(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ExprEvaluatorContext exprEvaluatorContext)
        throws ExprValidationException;

    /**
     * Ctor.
     * @param distinct - sets the flag indicatating whether only unique values should be aggregated
     */
    protected ExprAggregateNodeBase(boolean distinct)
    {
        isDistinct = distinct;
    }

    public ExprEvaluator getExprEvaluator()
    {
        return this;
    }

    public boolean isConstantResult()
    {
        return false;
    }

    @Override
    public Map<String, Object> getEventType() {
        return null;
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException
    {
        aggregationMethodFactory = validateAggregationChild(validationContext.getStreamTypeService(), validationContext.getMethodResolutionService(), validationContext.getExprEvaluatorContext());
    }

    public Class getType()
    {
        if (aggregationMethodFactory == null)
        {
            throw new IllegalStateException("Aggregation method has not been set");
        }
        return aggregationMethodFactory.getResultType();
    }

    /**
     * Returns the aggregation state factory for use in grouping aggregation states per group-by keys.
     * @return prototype aggregation state as a factory for aggregation states per group-by key value
     */
    public AggregationMethodFactory getFactory()
    {
        if (aggregationMethodFactory == null)
        {
            throw new IllegalStateException("Aggregation method has not been set");
        }
        return aggregationMethodFactory;
    }

    /**
     * Assigns to the node the future which can be queried for the current aggregation state at evaluation time.
     * @param aggregationResultFuture - future containing state
     * @param column - column to hand to future for easy access
     */
	public void setAggregationResultFuture(AggregationResultFuture aggregationResultFuture, int column)
    {
        this.aggregationResultFuture = aggregationResultFuture;
        this.column = column;
    }

	public final Object evaluate(EventBean[] events, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
	{
		return aggregationResultFuture.getValue(column, exprEvaluatorContext.getAgentInstanceId());
	}

    /**
     * Returns true if the aggregation node is only aggregatig distinct values, or false if
     * aggregating all values.
     * @return true if 'distinct' keyword was given, false if not
     */
    public boolean isDistinct()
    {
        return isDistinct;
    }

    public final boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprAggregateNode))
        {
            return false;
        }

        ExprAggregateNode other = (ExprAggregateNode) node;

        if (other.isDistinct() != this.isDistinct)
        {
            return false;
        }

        return this.equalsNodeAggregate(other);
    }

    /**
     * For use by implementing classes, validates the aggregation node expecting
     * a single numeric-type child node.
     * @param streamTypeService - types represented in streams
     * @return numeric type of single child
     * @throws com.espertech.esper.epl.expression.ExprValidationException if the validation failed
     */
    protected final Class validateNumericChildAllowFilter(StreamTypeService streamTypeService, boolean hasFilter)
        throws ExprValidationException
    {
        if (this.getChildNodes().size() == 0 || this.getChildNodes().size() > 2)
        {
            throw new ExprValidationException(getAggregationFunctionName() + " node must have at least 1 or maximum 2 child nodes");
        }

        // validate child expression (filter expression is actually always the first expression)
        ExprNode child = this.getChildNodes().get(0);
        if (hasFilter) {
            validateFilter(getChildNodes().get(1).getExprEvaluator());
        }

        Class childType = child.getExprEvaluator().getType();
        if (!JavaClassHelper.isNumeric(childType))
        {
            throw new ExprValidationException("Implicit conversion from datatype '" +
                    childType.getSimpleName() +
                    "' to numeric is not allowed for aggregation function '" + getAggregationFunctionName() + "'");
        }

        return childType;
    }

    /**
     * Renders the aggregation function expression.
     * @return expression string is the textual rendering of the aggregation function and it's sub-expression
     */
    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getAggregationFunctionName());
        buffer.append('(');

        if (isDistinct)
        {
            buffer.append("distinct ");
        }

        if (!this.getChildNodes().isEmpty())
        {
            buffer.append(this.getChildNodes().get(0).toExpressionString());
        }
        else
        {
            buffer.append('*');
        }

        buffer.append(')');

        return buffer.toString();
    }

    public void validateFilter(ExprEvaluator filterEvaluator) throws ExprValidationException{
        if (JavaClassHelper.getBoxedType(filterEvaluator.getType()) != Boolean.class) {
            throw new ExprValidationException("Invalid filter expression parameter to the aggregation function '" +
                    getAggregationFunctionName() +
                    "' is expected to return a boolean value but returns " + JavaClassHelper.getClassNameFullyQualPretty(filterEvaluator.getType()));
        }
    }
}
