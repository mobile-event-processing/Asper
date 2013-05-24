/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.window;

import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.view.View;
import com.espertech.esper.view.ViewServiceHelper;

/**
 * Factory for {@link com.espertech.esper.view.window.ExpressionWindowView}.
 */
public class ExpressionWindowViewFactory extends ExpressionViewFactoryBase
{
    public View makeView(final AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext)
    {
        IStreamRandomAccess randomAccess = ViewServiceHelper.getOptPreviousExprRandomAccess(agentInstanceViewFactoryContext);
        return new ExpressionWindowView(this, randomAccess, expiryExpression.getExprEvaluator(), aggregationServiceFactoryDesc, builtinMapBean, variableNames, agentInstanceViewFactoryContext);
    }

    public Object makePreviousGetter() {
        return new RandomAccessByIndexGetter();
    }
}
