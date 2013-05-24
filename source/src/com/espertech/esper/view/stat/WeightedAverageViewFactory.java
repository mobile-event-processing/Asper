/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.stat;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.view.*;

import java.util.List;

/**
 * Factory for {@link WeightedAverageView} instances.
 */
public class WeightedAverageViewFactory implements ViewFactory
{
    private List<ExprNode> viewParameters;
    private int streamNumber;

    /**
     * Expression of X field.
     */
    protected ExprNode fieldNameX;
    /**
     * Expression of weight field.
     */
    protected ExprNode fieldNameWeight;

    protected StatViewAdditionalProps additionalProps;

    protected EventType eventType;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException
    {
        this.viewParameters = expressionParameters;
        this.streamNumber = viewFactoryContext.getStreamNum();
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException
    {
        ExprNode[] validated = ViewFactorySupport.validate("Weighted average view", parentEventType, statementContext, viewParameters, true);

        String errorMessage = "Weighted average view requires two expressions returning numeric values as parameters";
        if (validated.length < 2) {
            throw new ViewParameterException(errorMessage);
        }
        if ((!JavaClassHelper.isNumeric(validated[0].getExprEvaluator().getType())) || (!JavaClassHelper.isNumeric(validated[1].getExprEvaluator().getType())))
        {
            throw new ViewParameterException(errorMessage);
        }

        fieldNameX = validated[0];
        fieldNameWeight = validated[1];
        additionalProps = StatViewAdditionalProps.make(validated, 2, parentEventType);
        eventType = WeightedAverageView.createEventType(statementContext, additionalProps, streamNumber);
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext)
    {
        return new WeightedAverageView(agentInstanceViewFactoryContext.getAgentInstanceContext(), fieldNameX, fieldNameWeight, eventType, additionalProps);
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public boolean canReuse(View view)
    {
        if (!(view instanceof WeightedAverageView)) {
            return false;
        }
        if (additionalProps != null) {
            return false;
        }

        WeightedAverageView myView = (WeightedAverageView) view;
        if ((!ExprNodeUtility.deepEquals(fieldNameWeight, myView.getFieldNameWeight())) ||
            (!ExprNodeUtility.deepEquals(fieldNameX, myView.getFieldNameX())) )
        {
            return false;
        }
        return true;
    }
}
