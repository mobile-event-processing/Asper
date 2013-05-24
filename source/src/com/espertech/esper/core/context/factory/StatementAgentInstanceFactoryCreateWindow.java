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

package com.espertech.esper.core.context.factory;

import com.asper.sources.javax.naming.NamingException;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.activator.ViewableActivationResult;
import com.espertech.esper.core.context.activator.ViewableActivatorFilterProxy;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.core.context.util.StatementAgentInstanceUtil;
import com.espertech.esper.core.service.EPServicesContext;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.core.start.EPStatementStartMethodCreateWindow;
import com.espertech.esper.core.start.EPStatementStartMethodHelperAssignExpr;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.core.ResultSetProcessorFactoryDesc;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.named.NamedWindowProcessor;
import com.espertech.esper.epl.named.NamedWindowProcessorInstance;
import com.espertech.esper.epl.named.NamedWindowTailViewInstance;
import com.espertech.esper.epl.spec.StatementSpecCompiled;
import com.espertech.esper.epl.view.OutputProcessViewFactory;
import com.espertech.esper.epl.virtualdw.VirtualDWView;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatementAgentInstanceFactoryCreateWindow implements StatementAgentInstanceFactory {
    private static final Log log = LogFactory.getLog(EPStatementStartMethodCreateWindow.class);

    private final StatementContext statementContext;
    private final StatementSpecCompiled statementSpec;
    private final EPServicesContext services;
    private final ViewableActivatorFilterProxy activator;
    private final ViewFactoryChain unmaterializedViewChain;
    private final ResultSetProcessorFactoryDesc resultSetProcessorPrototype;
    private final OutputProcessViewFactory outputProcessViewFactory;
    private final boolean isRecoveringStatement;

    public StatementAgentInstanceFactoryCreateWindow(StatementContext statementContext, StatementSpecCompiled statementSpec, EPServicesContext services, ViewableActivatorFilterProxy activator, ViewFactoryChain unmaterializedViewChain, ResultSetProcessorFactoryDesc resultSetProcessorPrototype, OutputProcessViewFactory outputProcessViewFactory, boolean recoveringStatement) {
        this.statementContext = statementContext;
        this.statementSpec = statementSpec;
        this.services = services;
        this.activator = activator;
        this.unmaterializedViewChain = unmaterializedViewChain;
        this.resultSetProcessorPrototype = resultSetProcessorPrototype;
        this.outputProcessViewFactory = outputProcessViewFactory;
        isRecoveringStatement = recoveringStatement;
    }

    public StatementAgentInstanceFactoryCreateWindowResult newContext(final AgentInstanceContext agentInstanceContext, boolean isRecoveringResilient)
    {
        final List<StopCallback> stopCallbacks = new ArrayList<StopCallback>();
        StopCallback stopCallback = new StopCallback() {
            public void stop() {
                StatementAgentInstanceUtil.stopSafe(agentInstanceContext.getTerminationCallbacks(), stopCallbacks, statementContext);
            }
        };

        String windowName = statementSpec.getCreateWindowDesc().getWindowName();
        Viewable finalView;
        Viewable eventStreamParentViewable;
        StatementAgentInstancePostLoad postLoad = null;
        Viewable topView;

        try {
            // Register interest
            ViewableActivationResult activationResult = activator.activate(agentInstanceContext, false, isRecoveringResilient);
            stopCallbacks.add(activationResult.getStopCallback());
            eventStreamParentViewable = activationResult.getViewable();

            // Obtain processor for this named window
            NamedWindowProcessor processor = services.getNamedWindowService().getProcessor(windowName);

            if (processor == null) {
                throw new RuntimeException("Failed to obtain named window processor for named window '" + windowName + "'");
            }

            // Allocate processor instance
            NamedWindowProcessorInstance processorInstance = processor.addInstance(agentInstanceContext);
            View rootView = processorInstance.getRootViewInstance();
            eventStreamParentViewable.addView(rootView);

            // Materialize views
            AgentInstanceViewFactoryChainContext viewFactoryChainContext = new AgentInstanceViewFactoryChainContext(agentInstanceContext, true, null, null);
            ViewServiceCreateResult createResult = services.getViewService().createViews(rootView, unmaterializedViewChain.getViewFactoryChain(), viewFactoryChainContext, false);
            topView = createResult.getTopViewable();
            finalView = createResult.getFinalViewable();

            // If this is a virtual data window implementation, bind it to the context for easy lookup
            StopCallback envStopCallback = null;
            if (finalView instanceof VirtualDWView) {
                final String objectName = "/virtualdw/" + windowName;
                final VirtualDWView virtualDWView = (VirtualDWView) finalView;
                try {
                    services.getEngineEnvContext().bind(objectName, virtualDWView.getVirtualDataWindow());
                }
                catch (NamingException e) {
                    throw new ViewProcessingException("Invalid name for adding to context:" + e.getMessage(), e);
                }
                envStopCallback = new StopCallback() {
                    public void stop() {
                        try {
                            virtualDWView.destroy();
                            services.getEngineEnvContext().unbind(objectName);
                        } catch (NamingException e) {}
                    }
                };
            }
            final StopCallback environmentStopCallback = envStopCallback;

            // create stop method using statement stream specs
            StopCallback allInOneStopMethod = new StopCallback()
            {
                public void stop()
                {
                    String windowName = statementSpec.getCreateWindowDesc().getWindowName();
                    NamedWindowProcessor processor = services.getNamedWindowService().getProcessor(windowName);
                    if (processor == null) {
                        log.warn("Named window processor by name '" + windowName + "' has not been found");
                    }
                    else {
                        NamedWindowProcessorInstance instance = processor.getProcessorInstance(agentInstanceContext);
                        if (instance.getRootViewInstance().isVirtualDataWindow()) {
                            instance.getRootViewInstance().getVirtualDataWindow().handleStopWindow();
                        }
                        processor.removeProcessorInstance(instance);
                    }
                    if (environmentStopCallback != null) {
                        environmentStopCallback.stop();
                    }
                }
            };
            stopCallbacks.add(allInOneStopMethod);

            // Attach tail view
            NamedWindowTailViewInstance tailView = processorInstance.getTailViewInstance();
            finalView.addView(tailView);
            finalView = tailView;

            // obtain result set processor
            ResultSetProcessor resultSetProcessor = EPStatementStartMethodHelperAssignExpr.getAssignResultSetProcessor(agentInstanceContext, resultSetProcessorPrototype);

            // Attach output view
            View outputView = outputProcessViewFactory.makeView(resultSetProcessor, agentInstanceContext);
            finalView.addView(outputView);
            finalView = outputView;

            // obtain post load
            postLoad = processorInstance.getPostLoad();

            // Handle insert case
            if (statementSpec.getCreateWindowDesc().isInsert() && !isRecoveringStatement)
            {
                String insertFromWindow = statementSpec.getCreateWindowDesc().getInsertFromWindow();
                NamedWindowProcessor namedWindowProcessor = services.getNamedWindowService().getProcessor(insertFromWindow);
                NamedWindowProcessorInstance sourceWindowInstances = namedWindowProcessor.getProcessorInstance(agentInstanceContext);
                List<EventBean> events = new ArrayList<EventBean>();
                if (statementSpec.getCreateWindowDesc().getInsertFilter() != null)
                {
                    EventBean[] eventsPerStream = new EventBean[1];
                    ExprEvaluator filter = statementSpec.getCreateWindowDesc().getInsertFilter().getExprEvaluator();
                    for (Iterator<EventBean> it = sourceWindowInstances.getTailViewInstance().iterator(); it.hasNext();)
                    {
                        EventBean candidate = it.next();
                        eventsPerStream[0] = candidate;
                        Boolean result = (Boolean) filter.evaluate(eventsPerStream, true, agentInstanceContext);
                        if ((result == null) || (!result))
                        {
                            continue;
                        }
                        events.add(candidate);
                    }
                }
                else
                {
                    for (Iterator<EventBean> it = sourceWindowInstances.getTailViewInstance().iterator(); it.hasNext();)
                    {
                        events.add(it.next());
                    }
                }
                if (events.size() > 0)
                {
                    EventType rootViewType = rootView.getEventType();
                    EventBean[] convertedEvents = services.getEventAdapterService().typeCast(events, rootViewType);
                    rootView.update(convertedEvents, null);
                }
            }
        }
        catch (RuntimeException ex) {
            StatementAgentInstanceUtil.stopSafe(stopCallback, statementContext);
            throw ex;
        }

        log.debug(".start Statement start completed");
        return new StatementAgentInstanceFactoryCreateWindowResult(finalView, stopCallback, agentInstanceContext, eventStreamParentViewable, postLoad, topView);
    }
}
