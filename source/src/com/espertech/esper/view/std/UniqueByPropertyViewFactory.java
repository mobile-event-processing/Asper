/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.std;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.view.*;

import java.util.List;
import java.util.Set;

/**
 * Factory for {@link UniqueByPropertyView} instances.
 */
public class UniqueByPropertyViewFactory implements DataWindowViewFactoryUniqueCandidate, DataWindowViewFactory
{
    /**
     * View parameters.
     */
    protected List<ExprNode> viewParameters;

    /**
     * Property name to evaluate unique values.
     */
    protected ExprNode[] criteriaExpressions;
    private EventType eventType;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException
    {
        this.viewParameters = expressionParameters;
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException
    {
        criteriaExpressions = ViewFactorySupport.validate("Unique-by view", parentEventType, statementContext, viewParameters, false);

        if (criteriaExpressions.length == 0)
        {
            String errorMessage = "Unique-by view requires a one or more expressions provinding unique values as parameters";
            throw new ViewParameterException(errorMessage);
        }

        this.eventType = parentEventType;
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext)
    {
        return new UniqueByPropertyView(criteriaExpressions, agentInstanceViewFactoryContext);
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public boolean canReuse(View view)
    {
        if (!(view instanceof UniqueByPropertyView))
        {
            return false;
        }

        UniqueByPropertyView myView = (UniqueByPropertyView) view;
        if (!ExprNodeUtility.deepEquals(criteriaExpressions, myView.getCriteriaExpressions()))
        {
            return false;
        }

        return myView.isEmpty();
    }

    public Set<String> getUniquenessCandidatePropertyNames() {
        return ExprNodeUtility.getPropertyNamesIfAllProps(criteriaExpressions);
    }
}
