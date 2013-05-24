/*
 * *************************************************************************************
 *  Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 *  http://esper.codehaus.org                                                          *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 * *************************************************************************************
 */

package com.espertech.esper.view.internal;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.view.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Factory for union-views.
 */
public class IntersectViewFactory implements ViewFactory, DataWindowViewFactory, DataWindowViewFactoryUniqueCandidate
{
    /**
     * The event type.
     */
    protected EventType parentEventType;

    /**
     * The view factories.
     */
    protected List<ViewFactory> viewFactories;

    /**
     * Ctor.
     */
    public IntersectViewFactory()
    {
    }

    /**
     * Sets the parent event type.
     * @param parentEventType type
     */
    public void setParentEventType(EventType parentEventType)
    {
        this.parentEventType = parentEventType;
    }

    /**
     * Sets the view factories.
     * @param viewFactories factories
     */
    public void setViewFactories(List<ViewFactory> viewFactories)
    {
        this.viewFactories = viewFactories;
        int batchCount = 0;
        for (ViewFactory viewFactory : viewFactories) {
            batchCount += viewFactory instanceof DataWindowBatchingViewFactory ? 1 : 0;
        }
        if (batchCount > 1) {
            throw new ViewProcessingException("Cannot combined multiple batch data windows into an intersection");
        }
    }

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> viewParameters) throws ViewParameterException
    {
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException
    {
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext)
    {
        List<View> views = new ArrayList<View>();
        boolean hasAsymetric = false;
        boolean hasBatch = false;
        for (ViewFactory viewFactory : viewFactories)
        {
            agentInstanceViewFactoryContext.setRemoveStream(true);
            views.add(viewFactory.makeView(agentInstanceViewFactoryContext));
            hasAsymetric |= viewFactory instanceof AsymetricDataWindowViewFactory;
            hasBatch |= viewFactory instanceof DataWindowBatchingViewFactory;
        }
        if (hasBatch) {
            return new IntersectBatchView(agentInstanceViewFactoryContext, this, parentEventType, views, viewFactories, hasAsymetric);
        }
        else if (hasAsymetric) {
            return new IntersectAsymetricView(agentInstanceViewFactoryContext, this, parentEventType, views);
        }
        return new IntersectView(agentInstanceViewFactoryContext, this, parentEventType, views);
    }

    public EventType getEventType()
    {
        return parentEventType;
    }

    public boolean canReuse(View view)
    {
        return false;
    }

    public Set<String> getUniquenessCandidatePropertyNames() {
        for (ViewFactory viewFactory : viewFactories) {
            if (viewFactory instanceof DataWindowViewFactoryUniqueCandidate) {
                DataWindowViewFactoryUniqueCandidate unique = (DataWindowViewFactoryUniqueCandidate) viewFactory;
                Set<String> props = unique.getUniquenessCandidatePropertyNames();
                if (props != null) {
                    return props;
                }
            }
        }
        return null;
    }
}
