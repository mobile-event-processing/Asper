/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.ext;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.view.*;
import com.espertech.esper.view.window.RandomAccessByIndexGetter;

import java.util.List;

/**
 * Factory for views for time-ordering events.
 */
public class TimeOrderViewFactory implements DataWindowViewFactory, DataWindowViewWithPrevious
{
    private List<ExprNode> viewParameters;

    /**
     * The timestamp expression.
     */
    protected ExprNode timestampExpression;

    /**
     * The interval to wait for newer events to arrive.
     */
    protected long intervalSize;

    private EventType eventType;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException
    {
        viewParameters = expressionParameters;
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException
    {
        ExprNode[] validated = ViewFactorySupport.validate("Time order view", parentEventType, statementContext, viewParameters, true);

        String errorMessage = "Time order view requires the expression supplying timestamp values, and a numeric or time period parameter for interval size";
        if (viewParameters.size() != 2)
        {
            throw new ViewParameterException(errorMessage);
        }

        if (!JavaClassHelper.isNumeric(validated[0].getExprEvaluator().getType()))
        {
            throw new ViewParameterException(errorMessage);
        }
        timestampExpression = validated[0];

        ExprEvaluatorContextStatement exprEvaluatorContext = new ExprEvaluatorContextStatement(statementContext);
        Object parameter = ViewFactorySupport.evaluateAssertNoProperties("Externally-timed window", validated[1], 1, exprEvaluatorContext);
        if (!(parameter instanceof Number))
        {
            throw new ViewParameterException(errorMessage);
        }
        else
        {
            Number param = (Number) parameter;
            if (JavaClassHelper.isFloatingPointNumber(param))
            {
                intervalSize = Math.round(1000d * param.doubleValue());
            }
            else
            {
                intervalSize = 1000 * param.longValue();
            }
        }

        if (intervalSize < 1)
        {
            throw new ViewParameterException("Time order view requires a size of at least 1 msec");
        }

        eventType = parentEventType;
    }

    public Object makePreviousGetter() {
        return new RandomAccessByIndexGetter();
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext)
    {
        IStreamSortRankRandomAccess sortedRandomAccess = ViewServiceHelper.getOptPreviousExprSortedRankedAccess(agentInstanceViewFactoryContext);
        return new TimeOrderView(agentInstanceViewFactoryContext, this, timestampExpression, timestampExpression.getExprEvaluator(), intervalSize, sortedRandomAccess);
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public boolean canReuse(View view)
    {
        if (!(view instanceof TimeOrderView))
        {
            return false;
        }

        TimeOrderView other = (TimeOrderView) view;
        if ((other.getIntervalSize() != intervalSize) ||
            (!ExprNodeUtility.deepEquals(other.getTimestampExpression(), timestampExpression)))
        {
            return false;
        }

        return other.isEmpty();
    }
}
