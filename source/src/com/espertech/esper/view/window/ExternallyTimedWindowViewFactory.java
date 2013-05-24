/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.window;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.view.*;

import java.util.List;

/**
 * Factory for {@link ExternallyTimedWindowView}.
 */
public class ExternallyTimedWindowViewFactory implements DataWindowViewFactory, DataWindowViewWithPrevious
{
    private List<ExprNode> viewParameters;

    private EventType eventType;

    /**
     * The timestamp property name.
     */
    protected ExprNode timestampExpression;
    protected ExprEvaluator timestampExpressionEval;

    /**
     * The number of msec to expire.
     */
    protected long millisecondsBeforeExpiry;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException
    {
        this.viewParameters = expressionParameters;
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException
    {
        ExprNode[] validated = ViewFactorySupport.validate("Externally-timed window", parentEventType, statementContext, viewParameters, true);
        String errorMessage = "Externally-timed window view requires a timestamp expression and a numeric or time period parameter for window size";
        if (viewParameters.size() != 2)
        {
            throw new ViewParameterException(errorMessage);
        }

        if (!JavaClassHelper.isNumeric(validated[0].getExprEvaluator().getType()))
        {
            throw new ViewParameterException(errorMessage);
        }
        timestampExpression = validated[0];
        timestampExpressionEval = timestampExpression.getExprEvaluator();

        ViewFactorySupport.assertReturnsNonConstant("Externally-timed window", validated[0], 0);
        Object parameter = ViewFactorySupport.evaluateAssertNoProperties("Externally-timed window", validated[1], 1, new ExprEvaluatorContextStatement(statementContext));
        if (!(parameter instanceof Number))
        {
            throw new ViewParameterException(errorMessage);
        }
        else
        {
            Number param = (Number) parameter;
            if (JavaClassHelper.isFloatingPointNumber(param))
            {
                millisecondsBeforeExpiry = Math.round(1000d * param.doubleValue());
            }
            else
            {
                millisecondsBeforeExpiry = 1000 * param.longValue();
            }
        }

        this.eventType = parentEventType;
    }

    public Object makePreviousGetter() {
        return new RandomAccessByIndexGetter();
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext)
    {
        IStreamRandomAccess randomAccess = ViewServiceHelper.getOptPreviousExprRandomAccess(agentInstanceViewFactoryContext);
        return new ExternallyTimedWindowView(this, timestampExpression, timestampExpressionEval, millisecondsBeforeExpiry, randomAccess, agentInstanceViewFactoryContext);
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public boolean canReuse(View view)
    {
        if (!(view instanceof ExternallyTimedWindowView))
        {
            return false;
        }

        ExternallyTimedWindowView myView = (ExternallyTimedWindowView) view;
        if ((myView.getMillisecondsBeforeExpiry() != millisecondsBeforeExpiry) ||
            (!ExprNodeUtility.deepEquals(myView.getTimestampExpression(), timestampExpression)))
        {
            return false;
        }
        return myView.isEmpty();
    }
}
