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

import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.core.context.activator.ViewableActivationResult;
import com.espertech.esper.core.context.activator.ViewableActivator;
import com.espertech.esper.core.context.subselect.SubSelectStrategyCollection;
import com.espertech.esper.core.context.subselect.SubSelectStrategyHolder;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.context.util.StatementAgentInstanceUtil;
import com.espertech.esper.core.service.EPServicesContext;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.core.start.EPStatementStartMethodCreateWindow;
import com.espertech.esper.core.start.EPStatementStartMethodHelperSubselect;
import com.espertech.esper.core.start.EPStatementStartMethodHelperUtil;
import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.core.ResultSetProcessorFactoryDesc;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprSubselectNode;
import com.espertech.esper.epl.named.NamedWindowOnExprBaseView;
import com.espertech.esper.epl.named.NamedWindowOnExprFactory;
import com.espertech.esper.epl.named.NamedWindowProcessor;
import com.espertech.esper.epl.named.NamedWindowProcessorInstance;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.epl.variable.OnSetVariableViewFactory;
import com.espertech.esper.epl.view.OutputProcessViewFactory;
import com.espertech.esper.pattern.EvalRootState;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.View;
import com.espertech.esper.view.internal.RouteResultView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatementAgentInstanceFactoryOnTrigger implements StatementAgentInstanceFactory {
    private static final Log log = LogFactory.getLog(EPStatementStartMethodCreateWindow.class);

    private final StatementContext statementContext;
    private final StatementSpecCompiled statementSpec;
    private final EPServicesContext services;
    private final ViewableActivator activator;
    private final SubSelectStrategyCollection subSelectStrategyCollection;
    private final ResultSetProcessorFactoryDesc resultSetProcessorPrototype;
    private final ExprNode validatedJoin;
    private final EventType activatorResultEventType;
    private final StatementAgentInstanceFactoryOnTriggerSplitDesc splitDesc;
    private final ResultSetProcessorFactoryDesc outputResultSetProcessorPrototype;
    private final OnSetVariableViewFactory onSetVariableViewFactory;
    private final NamedWindowOnExprFactory onExprFactory;
    private final OutputProcessViewFactory outputProcessViewFactory;

    public StatementAgentInstanceFactoryOnTrigger(StatementContext statementContext, StatementSpecCompiled statementSpec, EPServicesContext services, ViewableActivator activator, SubSelectStrategyCollection subSelectStrategyCollection, ResultSetProcessorFactoryDesc resultSetProcessorPrototype, ExprNode validatedJoin, EventType activatorResultEventType, StatementAgentInstanceFactoryOnTriggerSplitDesc splitDesc, ResultSetProcessorFactoryDesc outputResultSetProcessorPrototype, OnSetVariableViewFactory onSetVariableViewFactory, NamedWindowOnExprFactory onExprFactory, OutputProcessViewFactory outputProcessViewFactory, boolean recoveringStatement) {
        this.statementContext = statementContext;
        this.statementSpec = statementSpec;
        this.services = services;
        this.activator = activator;
        this.subSelectStrategyCollection = subSelectStrategyCollection;
        this.resultSetProcessorPrototype = resultSetProcessorPrototype;
        this.validatedJoin = validatedJoin;
        this.activatorResultEventType = activatorResultEventType;
        this.splitDesc = splitDesc;
        this.outputResultSetProcessorPrototype = outputResultSetProcessorPrototype;
        this.onSetVariableViewFactory = onSetVariableViewFactory;
        this.onExprFactory = onExprFactory;
        this.outputProcessViewFactory = outputProcessViewFactory;
    }

    public StatementAgentInstanceFactoryOnTriggerResult newContext(final AgentInstanceContext agentInstanceContext, boolean isRecoveringResilient)
    {
        final List<StopCallback> stopCallbacks = new ArrayList<StopCallback>();
        StopCallback stopCallback = new StopCallback() {
            public void stop() {
                StatementAgentInstanceUtil.stopSafe(agentInstanceContext.getTerminationCallbacks(), stopCallbacks, statementContext);
            }
        };

        View onExprView;
        Map<ExprSubselectNode, SubSelectStrategyHolder> subselectStrategies;
        AggregationService aggregationService = null;
        EvalRootState optPatternRoot;

        try {
            if (services.getSchedulableAgentInstanceDirectory() != null) {
                services.getSchedulableAgentInstanceDirectory().add(agentInstanceContext.getEpStatementAgentInstanceHandle());
            }
            OnTriggerDesc onTriggerDesc = statementSpec.getOnTriggerDesc();

            // Start: for on-delete and on-select and on-update triggers
            if (onTriggerDesc instanceof OnTriggerWindowDesc)
            {
                OnTriggerWindowDesc onTriggerWindowDesc = (OnTriggerWindowDesc) onTriggerDesc;

                // get result set processor and aggregation services
                Pair<ResultSetProcessor, AggregationService> pair = EPStatementStartMethodHelperUtil.startResultSetAndAggregation(resultSetProcessorPrototype, agentInstanceContext);
                aggregationService = pair.getSecond();

                // get named window processor instance
                NamedWindowProcessor processor = services.getNamedWindowService().getProcessor(onTriggerWindowDesc.getWindowName());
                NamedWindowProcessorInstance processorInstance = processor.getProcessorInstance(agentInstanceContext);

                // obtain on-expr view
                NamedWindowOnExprBaseView onExprBaseView = processorInstance.getRootViewInstance().addOnExpr(onExprFactory, agentInstanceContext, validatedJoin, activatorResultEventType, pair.getFirst());
                onExprView = onExprBaseView;
                stopCallbacks.add(onExprBaseView);
            }
            // variable assignments
            else if (statementSpec.getOnTriggerDesc() instanceof OnTriggerSetDesc)
            {
                onExprView = onSetVariableViewFactory.instantiate(agentInstanceContext);
            }
            // split-stream use case
            else
            {
                ResultSetProcessor[] processors = new ResultSetProcessor[splitDesc.getProcessorFactories().length];
                for (int i = 0; i < processors.length; i++) {
                    ResultSetProcessorFactoryDesc factory = splitDesc.getProcessorFactories()[i];
                    ResultSetProcessor processor = factory.getResultSetProcessorFactory().instantiate(null, null, agentInstanceContext);
                    processors[i] = processor;
                }

                OnTriggerSplitStreamDesc desc = (OnTriggerSplitStreamDesc) statementSpec.getOnTriggerDesc();
                onExprView = new RouteResultView(desc.isFirst(), activatorResultEventType, statementContext.getEpStatementHandle(), services.getInternalEventRouter(), splitDesc.getNamedWindowInsert(), processors, splitDesc.getWhereClauses(), statementContext);
            }

            // attach stream to view
            final ViewableActivationResult activationResult = activator.activate(agentInstanceContext, false, isRecoveringResilient);
            activationResult.getViewable().addView(onExprView);
            stopCallbacks.add(activationResult.getStopCallback());
            optPatternRoot = activationResult.getOptionalPatternRoot();

            // start subselects
            subselectStrategies = EPStatementStartMethodHelperSubselect.startSubselects(services, subSelectStrategyCollection, agentInstanceContext, stopCallbacks);

            // attach view to output: for on-delete, create an output processor that passes on as a wildcard the underlying event
            if ((statementSpec.getOnTriggerDesc().getOnTriggerType() == OnTriggerType.ON_DELETE) ||
                (statementSpec.getOnTriggerDesc().getOnTriggerType() == OnTriggerType.ON_SET) ||
                (statementSpec.getOnTriggerDesc().getOnTriggerType() == OnTriggerType.ON_UPDATE) ||
                (statementSpec.getOnTriggerDesc().getOnTriggerType() == OnTriggerType.ON_MERGE))
            {
                ResultSetProcessor outputResultSetProcessor = outputResultSetProcessorPrototype.getResultSetProcessorFactory().instantiate(null, null, agentInstanceContext);

                // Attach output view
                View outputView = outputProcessViewFactory.makeView(outputResultSetProcessor, agentInstanceContext);
                onExprView.addView(outputView);
                onExprView = outputView;
            }
        }
        catch (RuntimeException ex) {
            StatementAgentInstanceUtil.stopSafe(stopCallback, statementContext);
            throw ex;
        }

        log.debug(".start Statement start completed");
        return new StatementAgentInstanceFactoryOnTriggerResult(onExprView, stopCallback, agentInstanceContext, aggregationService, subselectStrategies, optPatternRoot);
    }
}
