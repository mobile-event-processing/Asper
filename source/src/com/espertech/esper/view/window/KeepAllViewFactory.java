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
import com.espertech.esper.view.*;

import java.util.List;

/**
 * Factory for {@link com.espertech.esper.view.window.KeepAllView}.
 */
public class KeepAllViewFactory implements DataWindowViewFactory, DataWindowViewWithPrevious
{
    private EventType eventType;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException
    {
        List<Object> viewParameters = ViewFactorySupport.validateAndEvaluate("Keep-all data window view", viewFactoryContext.getStatementContext(), expressionParameters);
        String errorMessage = "Keep-all data window view requires an empty parameter list";
        if (viewParameters.size() != 0)
        {
            throw new ViewParameterException(errorMessage);
        }
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException
    {
        this.eventType = parentEventType;
    }

    public Object makePreviousGetter() {
        return new RandomAccessByIndexGetter();
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext)
    {
        IStreamRandomAccess randomAccess = ViewServiceHelper.getOptPreviousExprRandomAccess(agentInstanceViewFactoryContext);
        return new KeepAllView(agentInstanceViewFactoryContext, this, randomAccess);
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public boolean canReuse(View view)
    {
        if (!(view instanceof KeepAllView))
        {
            return false;
        }

        KeepAllView myView = (KeepAllView) view;
        return myView.isEmpty();
    }
}
