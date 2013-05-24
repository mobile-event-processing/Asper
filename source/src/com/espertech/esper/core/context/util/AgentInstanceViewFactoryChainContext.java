/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.context.util;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.collection.ViewUpdatedCollection;
import com.espertech.esper.core.service.ExpressionResultCacheService;
import com.espertech.esper.core.service.StatementAgentInstanceLock;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.core.start.EPStatementStartMethodHelperPrevious;
import com.espertech.esper.core.start.EPStatementStartMethodHelperPrior;
import com.espertech.esper.epl.core.ViewResourceDelegateVerifiedStream;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprPriorNode;
import com.espertech.esper.epl.script.AgentInstanceScriptContext;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.DataWindowViewFactory;
import com.espertech.esper.view.DataWindowViewWithPrevious;
import com.espertech.esper.view.ViewFactory;
import com.espertech.esper.view.internal.PriorEventViewFactory;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;

public class AgentInstanceViewFactoryChainContext implements ExprEvaluatorContext
{
    private final AgentInstanceContext agentInstanceContext;
    private boolean isRemoveStream;
    private final Object previousNodeGetter;
    private final ViewUpdatedCollection priorViewUpdatedCollection;

    public AgentInstanceViewFactoryChainContext(AgentInstanceContext agentInstanceContext, boolean isRemoveStream, Object previousNodeGetter, ViewUpdatedCollection priorViewUpdatedCollection) {
        this.agentInstanceContext = agentInstanceContext;
        this.isRemoveStream = isRemoveStream;
        this.previousNodeGetter = previousNodeGetter;
        this.priorViewUpdatedCollection = priorViewUpdatedCollection;
    }

    public StatementAgentInstanceLock getAgentInstanceLock() {
        return agentInstanceContext.getAgentInstanceLock();
    }

    public AgentInstanceContext getAgentInstanceContext() {
        return agentInstanceContext;
    }

    public AgentInstanceScriptContext getAgentInstanceScriptContext() {
        return agentInstanceContext.getAgentInstanceScriptContext();
    }

    public boolean isRemoveStream() {
        return isRemoveStream;
    }

    public void setRemoveStream(boolean removeStream) {
        isRemoveStream = removeStream;
    }

    public Object getPreviousNodeGetter() {
        return previousNodeGetter;
    }

    public ViewUpdatedCollection getPriorViewUpdatedCollection() {
        return priorViewUpdatedCollection;
    }

    public StatementContext getStatementContext() {
        return agentInstanceContext.getStatementContext();
    }

    public TimeProvider getTimeProvider() {
        return agentInstanceContext.getTimeProvider();
    }

    public ExpressionResultCacheService getExpressionResultCacheService() {
        return agentInstanceContext.getExpressionResultCacheService();
    }

    public int getAgentInstanceId() {
        return agentInstanceContext.getAgentInstanceId();
    }

    public EventBean getContextProperties() {
        return agentInstanceContext.getContextProperties();
    }

    public EPStatementAgentInstanceHandle getEpStatementAgentInstanceHandle() {
        return agentInstanceContext.getEpStatementAgentInstanceHandle();
    }

    public Set<StopCallback> getTerminationCallbacks() {
        return agentInstanceContext.getTerminationCallbacks();
    }

    public static AgentInstanceViewFactoryChainContext create(List<ViewFactory> viewFactoryChain, AgentInstanceContext agentInstanceContext, ViewResourceDelegateVerifiedStream viewResourceDelegate) {

        Object previousNodeGetter = null;
        if (viewResourceDelegate.getPreviousRequests() != null && !viewResourceDelegate.getPreviousRequests().isEmpty()) {
            DataWindowViewWithPrevious factoryFound = EPStatementStartMethodHelperPrevious.findPreviousViewFactory(viewFactoryChain);
            previousNodeGetter = factoryFound.makePreviousGetter();
        }

        ViewUpdatedCollection priorViewUpdatedCollection = null;
        if (viewResourceDelegate.getPriorRequests() != null && !viewResourceDelegate.getPriorRequests().isEmpty()) {
            PriorEventViewFactory priorEventViewFactory = EPStatementStartMethodHelperPrior.findPriorViewFactory(viewFactoryChain);
            SortedMap<Integer, List<ExprPriorNode>> callbacksPerIndex = viewResourceDelegate.getPriorRequests();
            priorViewUpdatedCollection = priorEventViewFactory.makeViewUpdatedCollection(callbacksPerIndex, agentInstanceContext.getAgentInstanceId());
        }

        boolean removedStream = false;
        if (viewFactoryChain.size() > 1) {
            int countDataWindow = 0;
            for (ViewFactory viewFactory : viewFactoryChain) {
                if (viewFactory instanceof DataWindowViewFactory) {
                    countDataWindow++;
                }
            }
            removedStream = countDataWindow > 1;
        }

        return new AgentInstanceViewFactoryChainContext(agentInstanceContext, removedStream, previousNodeGetter, priorViewUpdatedCollection);
    }

    public String getStatementName() {
        return agentInstanceContext.getStatementName();
    }

    public String getEngineURI() {
        return agentInstanceContext.getEngineURI();
    }

    public String getStatementId() {
        return agentInstanceContext.getStatementId();
    }
}
