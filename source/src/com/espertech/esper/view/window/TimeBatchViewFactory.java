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
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.view.*;

import java.util.List;

/**
 * Factory for {@link TimeBatchView}.
 */
public class TimeBatchViewFactory extends TimeBatchViewFactoryParams implements DataWindowViewFactory, DataWindowViewWithPrevious, DataWindowBatchingViewFactory
{
    /**
     * The reference point, or null if none supplied.
     */
    protected Long optionalReferencePoint;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException
    {
        List<Object> viewParameters = ViewFactorySupport.validateAndEvaluate("Time batch view", viewFactoryContext.getStatementContext(), expressionParameters);
        String errorMessage = "Time batch view requires a single numeric or time period parameter, and an optional long-typed reference point in msec, and an optional list of control keywords as a string parameter (please see the documentation)";
        if ((viewParameters.size() < 1) || (viewParameters.size() > 3))
        {
            throw new ViewParameterException(errorMessage);
        }

        processExpiry(viewParameters.get(0), errorMessage, "Time batch view requires a size of at least 1 msec");

        if ((viewParameters.size() == 2) && (viewParameters.get(1) instanceof String))
        {
            processKeywords(viewParameters.get(1), errorMessage);
        }
        else
        {
            if (viewParameters.size() >= 2)
            {
                Object paramRef = viewParameters.get(1);
                if ((!(paramRef instanceof Number)) || (JavaClassHelper.isFloatingPointNumber((Number)paramRef)))
                {
                    throw new ViewParameterException("Time batch view requires a Long-typed reference point in msec as a second parameter");
                }
                optionalReferencePoint = ((Number) paramRef).longValue();
            }

            if (viewParameters.size() == 3)
            {
                processKeywords(viewParameters.get(2), errorMessage);
            }
        }
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException
    {
        this.eventType = parentEventType;
    }

    public Object makePreviousGetter() {
        return new RelativeAccessByEventNIndexMap();
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext)
    {
        IStreamRelativeAccess relativeAccessByEvent = ViewServiceHelper.getOptPreviousExprRelativeAccess(agentInstanceViewFactoryContext);
        if (agentInstanceViewFactoryContext.isRemoveStream())
        {
            return new TimeBatchViewRStream(this, agentInstanceViewFactoryContext, millisecondsBeforeExpiry, optionalReferencePoint, isForceUpdate, isStartEager);
        }
        else
        {
            return new TimeBatchView(this, agentInstanceViewFactoryContext, millisecondsBeforeExpiry, optionalReferencePoint, isForceUpdate, isStartEager, relativeAccessByEvent);
        }
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public boolean canReuse(View view)
    {
        if (!(view instanceof TimeBatchView))
        {
            return false;
        }

        TimeBatchView myView = (TimeBatchView) view;
        if (myView.getMsecIntervalSize() != millisecondsBeforeExpiry)
        {
            return false;
        }

        if ((myView.getInitialReferencePoint() != null) && (optionalReferencePoint != null))
        {
            if (!myView.getInitialReferencePoint().equals(optionalReferencePoint.longValue()))
            {
                return false;
            }
        }
        if ( ((myView.getInitialReferencePoint() == null) && (optionalReferencePoint != null)) ||
             ((myView.getInitialReferencePoint() != null) && (optionalReferencePoint == null)) )
        {
            return false;
        }

        if (myView.isForceOutput() != isForceUpdate)
        {
            return false;
        }

        if (myView.isStartEager())  // since it's already started
        {
            return false;
        }

        return myView.isEmpty();
    }
}
