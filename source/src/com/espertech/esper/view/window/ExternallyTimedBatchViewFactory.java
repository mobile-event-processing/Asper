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
 * Factory for {@link com.espertech.esper.view.window.ExternallyTimedBatchView}.
 */
public class ExternallyTimedBatchViewFactory implements DataWindowBatchingViewFactory, DataWindowViewFactory, DataWindowViewWithPrevious
{
    private List<ExprNode> viewParameters;

    private EventType eventType;

    /**
     * The timestamp property name.
     */
    protected ExprNode timestampExpression;
    protected ExprEvaluator timestampExpressionEval;
    protected Long optionalReferencePoint;

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
        final String windowName = "Externally-timed batch window";
        ExprNode[] validated = ViewFactorySupport.validate(windowName, parentEventType, statementContext, viewParameters, true);
        String errorMessage = "Externally-timed batch window view requires a timestamp expression and a numeric or time period parameter for window size and an optional long-typed reference point in msec, and an optional list of control keywords as a string parameter (please see the documentation)";
        if (viewParameters.size() < 2 || viewParameters.size() > 3) {
            throw new ViewParameterException(errorMessage);
        }

        // validate first parameter: timestamp expression
        if (!JavaClassHelper.isNumeric(validated[0].getExprEvaluator().getType()))
        {
            throw new ViewParameterException(errorMessage);
        }
        timestampExpression = validated[0];
        timestampExpressionEval = timestampExpression.getExprEvaluator();

        // validate second: window size
        ViewFactorySupport.assertReturnsNonConstant(windowName, validated[0], 0);
        Object parameter = ViewFactorySupport.evaluateAssertNoProperties(windowName, validated[1], 1, new ExprEvaluatorContextStatement(statementContext));
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

        // validate optional parameters
        if (validated.length == 3) {
            Object constant = ViewFactorySupport.validateAndEvaluate(windowName, statementContext, validated[2]);
            if ((!(constant instanceof Number)) || (JavaClassHelper.isFloatingPointNumber((Number)constant)))
            {
                throw new ViewParameterException("Externally-timed batch view requires a Long-typed reference point in msec as a third parameter");
            }
            optionalReferencePoint = ((Number) constant).longValue();
        }

        this.eventType = parentEventType;
    }

    public Object makePreviousGetter() {
        return new RelativeAccessByEventNIndexMap();
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext)
    {
        IStreamRelativeAccess relativeAccessByEvent = ViewServiceHelper.getOptPreviousExprRelativeAccess(agentInstanceViewFactoryContext);
        return new ExternallyTimedBatchView(this, timestampExpression, timestampExpressionEval, millisecondsBeforeExpiry, optionalReferencePoint, relativeAccessByEvent, agentInstanceViewFactoryContext);
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public boolean canReuse(View view)
    {
        if (!(view instanceof ExternallyTimedBatchView))
        {
            return false;
        }

        ExternallyTimedBatchView myView = (ExternallyTimedBatchView) view;
        if ((myView.getMillisecondsBeforeExpiry() != millisecondsBeforeExpiry) ||
            (!ExprNodeUtility.deepEquals(myView.getTimestampExpression(), timestampExpression)))
        {
            return false;
        }
        return myView.isEmpty();
    }
}
