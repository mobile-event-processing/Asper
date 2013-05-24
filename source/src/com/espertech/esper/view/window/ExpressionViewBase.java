/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.window;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.ViewUpdatedCollection;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.core.service.EPStatementHandleCallback;
import com.espertech.esper.core.service.ExtensionServicesContext;
import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.agg.service.AggregationServiceAggExpressionDesc;
import com.espertech.esper.epl.agg.service.AggregationServiceFactoryDesc;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.variable.VariableChangeCallback;
import com.espertech.esper.epl.variable.VariableReader;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.event.map.MapEventBean;
import com.espertech.esper.schedule.ScheduleHandleCallback;
import com.espertech.esper.schedule.ScheduleSlot;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.CloneableView;
import com.espertech.esper.view.DataWindowView;
import com.espertech.esper.view.StoppableView;
import com.espertech.esper.view.ViewSupport;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * This view is a moving window extending the into the past until the expression passed to it returns false.
 */
public abstract class ExpressionViewBase extends ViewSupport implements DataWindowView, CloneableView, StoppableView, VariableChangeCallback, StopCallback {

    protected final ViewUpdatedCollection viewUpdatedCollection;
    protected final ExprEvaluator expiryExpression;
    protected final MapEventBean builtinEventProps;
    protected final EventBean[] eventsPerStream;
    protected final Set<String> variableNames;
    protected final AgentInstanceViewFactoryChainContext agentInstanceContext;
    protected final ScheduleSlot scheduleSlot;
    protected final EPStatementHandleCallback scheduleHandle;
    protected final AggregationService aggregationService;
    protected final List<AggregationServiceAggExpressionDesc> aggregateNodes;

    /**
     * Implemented to check the expiry expression.
     */
    public abstract void scheduleCallback();

    public ExpressionViewBase(ViewUpdatedCollection viewUpdatedCollection,
                              ExprEvaluator expiryExpression,
                              AggregationServiceFactoryDesc aggregationServiceFactoryDesc,
                              MapEventBean builtinEventProps,
                              Set<String> variableNames,
                              AgentInstanceViewFactoryChainContext agentInstanceContext)
    {
        this.viewUpdatedCollection = viewUpdatedCollection;
        this.expiryExpression = expiryExpression;
        this.builtinEventProps = builtinEventProps;
        this.eventsPerStream = new EventBean[] {null, builtinEventProps};
        this.variableNames = variableNames;
        this.agentInstanceContext = agentInstanceContext;

        if (variableNames != null && !variableNames.isEmpty()) {
            for (String variable : variableNames) {
                final VariableService variableService = agentInstanceContext.getStatementContext().getVariableService();
                final VariableReader reader = variableService.getReader(variable);
                agentInstanceContext.getStatementContext().getVariableService().registerCallback(reader.getVariableNumber(), this);
                agentInstanceContext.getTerminationCallbacks().add(new StopCallback() {
                    public void stop() {
                        variableService.unregisterCallback(reader.getVariableNumber(), ExpressionViewBase.this);
                    }
                });
            }

            ScheduleHandleCallback callback = new ScheduleHandleCallback() {
                public void scheduledTrigger(ExtensionServicesContext extensionServicesContext)
                {
                    scheduleCallback();
                }
            };
            scheduleSlot = agentInstanceContext.getStatementContext().getScheduleBucket().allocateSlot();
            scheduleHandle = new EPStatementHandleCallback(agentInstanceContext.getEpStatementAgentInstanceHandle(), callback);
            agentInstanceContext.getTerminationCallbacks().add(this);
        }
        else {
            scheduleSlot = null;
            scheduleHandle = null;
        }

        if (aggregationServiceFactoryDesc != null) {
            aggregationService = aggregationServiceFactoryDesc.getAggregationServiceFactory().makeService(agentInstanceContext.getAgentInstanceContext(), agentInstanceContext.getAgentInstanceContext().getStatementContext().getMethodResolutionService());
            aggregateNodes = aggregationServiceFactoryDesc.getExpressions();
        }
        else {
            aggregationService = null;
            aggregateNodes = Collections.emptyList();
        }
    }

    public final EventType getEventType()
    {
        // The event type is the parent view's event type
        return parent.getEventType();
    }

    public final String toString()
    {
        return this.getClass().getName();
    }

    public void stopView() {
        stopScheduleAndVar();
        agentInstanceContext.getTerminationCallbacks().remove(this);
    }

    public void stop() {
        stopScheduleAndVar();
    }

    public void stopScheduleAndVar() {
        if (variableNames != null && !variableNames.isEmpty()) {
            for (String variable : variableNames) {
                VariableReader reader = agentInstanceContext.getStatementContext().getVariableService().getReader(variable);
                if (reader != null) {
                    agentInstanceContext.getStatementContext().getVariableService().unregisterCallback(reader.getVariableNumber(), this);
                }
            }

            if (agentInstanceContext.getStatementContext().getSchedulingService().isScheduled(scheduleHandle)) {
                agentInstanceContext.getStatementContext().getSchedulingService().remove(scheduleHandle, scheduleSlot);
            }
        }
    }

    // Handle variable updates by scheduling a re-evaluation with timers
    public void update(Object newValue, Object oldValue) {
        if (!agentInstanceContext.getStatementContext().getSchedulingService().isScheduled(scheduleHandle)) {
            agentInstanceContext.getStatementContext().getSchedulingService().add(0, scheduleHandle, scheduleSlot);
        }
    }

    public ViewUpdatedCollection getViewUpdatedCollection() {
        return viewUpdatedCollection;
    }

    public AggregationService getAggregationService() {
        return aggregationService;
    }
}
