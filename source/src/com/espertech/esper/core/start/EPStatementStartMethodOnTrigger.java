/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.start;

import com.espertech.esper.client.EventType;
import com.espertech.esper.client.VariableValueException;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.core.context.activator.ViewableActivator;
import com.espertech.esper.core.context.activator.ViewableActivatorFilterProxy;
import com.espertech.esper.core.context.activator.ViewableActivatorNamedWindow;
import com.espertech.esper.core.context.activator.ViewableActivatorPattern;
import com.espertech.esper.core.context.factory.StatementAgentInstanceFactoryOnTrigger;
import com.espertech.esper.core.context.factory.StatementAgentInstanceFactoryOnTriggerResult;
import com.espertech.esper.core.context.factory.StatementAgentInstanceFactoryOnTriggerSplitDesc;
import com.espertech.esper.core.context.mgr.ContextManagedStatementOnTriggerDesc;
import com.espertech.esper.core.context.stmt.AIRegistryExpr;
import com.espertech.esper.core.context.stmt.AIRegistrySubselect;
import com.espertech.esper.core.context.subselect.SubSelectActivationCollection;
import com.espertech.esper.core.context.subselect.SubSelectStrategyCollection;
import com.espertech.esper.core.context.subselect.SubSelectStrategyHolder;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.context.util.ContextMergeView;
import com.espertech.esper.core.context.util.ContextPropertyRegistry;
import com.espertech.esper.core.service.*;
import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.core.ResultSetProcessorFactoryDesc;
import com.espertech.esper.epl.core.ResultSetProcessorFactoryFactory;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.StreamTypeServiceImpl;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.metric.StatementMetricHandle;
import com.espertech.esper.epl.named.NamedWindowOnExprFactory;
import com.espertech.esper.epl.named.NamedWindowOnExprFactoryFactory;
import com.espertech.esper.epl.named.NamedWindowProcessor;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.epl.variable.OnSetVariableViewFactory;
import com.espertech.esper.epl.view.OutputProcessViewFactory;
import com.espertech.esper.epl.view.OutputProcessViewFactoryFactory;
import com.espertech.esper.event.EventTypeMetadata;
import com.espertech.esper.event.map.MapEventType;
import com.espertech.esper.pattern.EvalRootFactoryNode;
import com.espertech.esper.pattern.PatternContext;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.util.UuidGenerator;
import com.espertech.esper.view.ViewProcessingException;
import com.espertech.esper.view.Viewable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Starts and provides the stop method for EPL statements.
 */
public class EPStatementStartMethodOnTrigger extends EPStatementStartMethodBase
{
    private static final Log log = LogFactory.getLog(EPStatementStartMethodOnTrigger.class);

    public static final String INITIAL_VALUE_STREAM_NAME = "initial";

    public EPStatementStartMethodOnTrigger(StatementSpecCompiled statementSpec) {
        super(statementSpec);
    }

    public EPStatementStartResult startInternal(final EPServicesContext services, final StatementContext statementContext, boolean isNewStatement, boolean isRecoveringStatement, boolean isRecoveringResilient) throws ExprValidationException, ViewProcessingException {
        // define stop
        final List<StopCallback> stopCallbacks = new LinkedList<StopCallback>();
        final EPStatementStopMethod stopMethod = new EPStatementStopMethodImpl(statementContext, stopCallbacks);

        // determine context
        final String contextName = statementSpec.getOptionalContextName();
        final ContextPropertyRegistry contextPropertyRegistry = (contextName != null) ? services.getContextManagementService().getContextDescriptor(contextName).getContextPropertyRegistry() : null;

        // create subselect information
        SubSelectActivationCollection subSelectStreamDesc = EPStatementStartMethodHelperSubselect.createSubSelectActivation(services, statementSpec, statementContext);

        // obtain activator
        final StreamSpecCompiled streamSpec = statementSpec.getStreamSpecs().get(0);
        ViewableActivator activator;
        String triggereventTypeName = null;
        EventType activatorResultEventType;
        if (streamSpec instanceof FilterStreamSpecCompiled)
        {
            FilterStreamSpecCompiled filterStreamSpec = (FilterStreamSpecCompiled) streamSpec;
            triggereventTypeName = filterStreamSpec.getFilterSpec().getFilterForEventTypeName();
            activator = new ViewableActivatorFilterProxy(services, filterStreamSpec.getFilterSpec(), statementContext.getAnnotations(), false);
            activatorResultEventType = filterStreamSpec.getFilterSpec().getResultEventType();
        }
        else if (streamSpec instanceof PatternStreamSpecCompiled)
        {
            PatternStreamSpecCompiled patternStreamSpec = (PatternStreamSpecCompiled) streamSpec;
            boolean usedByChildViews = !streamSpec.getViewSpecs().isEmpty() || (statementSpec.getInsertIntoDesc() != null);
            String patternTypeName = statementContext.getStatementId() + "_patternon";
            final EventType eventType = services.getEventAdapterService().createSemiAnonymousMapType(patternTypeName, patternStreamSpec.getTaggedEventTypes(), patternStreamSpec.getArrayEventTypes(), usedByChildViews);

            EvalRootFactoryNode rootNode = services.getPatternNodeFactory().makeRootNode();
            rootNode.addChildNode(patternStreamSpec.getEvalFactoryNode());
            PatternContext patternContext = statementContext.getPatternContextFactory().createContext(statementContext, 0, rootNode, patternStreamSpec.getMatchedEventMapMeta(), true);
            activator = new ViewableActivatorPattern(patternContext, rootNode, eventType, EPStatementStartMethodHelperUtil.isConsumingFilters(patternStreamSpec.getEvalFactoryNode()));
            activatorResultEventType = eventType;
        }
        else if (streamSpec instanceof NamedWindowConsumerStreamSpec)
        {
            NamedWindowConsumerStreamSpec namedSpec = (NamedWindowConsumerStreamSpec) streamSpec;
            NamedWindowProcessor processor = services.getNamedWindowService().getProcessor(namedSpec.getWindowName());
            if (processor == null) {
                throw new ExprValidationException("A named window by name '" + namedSpec.getWindowName() + "' does not exist");
            }
            triggereventTypeName = namedSpec.getWindowName();
            activator = new ViewableActivatorNamedWindow(processor, namedSpec.getFilterExpressions(), namedSpec.getOptPropertyEvaluator());
            activatorResultEventType = processor.getNamedWindowType();
            if (namedSpec.getOptPropertyEvaluator() != null) {
                activatorResultEventType = namedSpec.getOptPropertyEvaluator().getFragmentEventType();
            }
        }
        else
        {
            throw new ExprValidationException("Unknown stream specification type: " + streamSpec);
        }

        // validation
        SubSelectStrategyCollection subSelectStrategyCollection;
        ResultSetProcessorFactoryDesc resultSetProcessorPrototype = null;
        ExprNode validatedJoin = null;
        StatementAgentInstanceFactoryOnTriggerSplitDesc splitDesc = null;
        ResultSetProcessorFactoryDesc outputResultSetProcessorPrototype = null;
        EventType outputEventType = null;
        OnSetVariableViewFactory onSetVariableViewFactory = null;
        NamedWindowOnExprFactory onExprFactory = null;

        // validation: For on-delete and on-select and on-update triggers
        if (statementSpec.getOnTriggerDesc() instanceof OnTriggerWindowDesc)
        {
            // Determine event types
            OnTriggerWindowDesc onTriggerDesc = (OnTriggerWindowDesc) statementSpec.getOnTriggerDesc();
            NamedWindowProcessor processor = services.getNamedWindowService().getProcessor(onTriggerDesc.getWindowName());
            if (processor == null) {
                throw new ExprValidationException("A named window by name '" + onTriggerDesc.getWindowName() + "' does not exist");
            }

            // validate context
            processor.validateOnExpressionContext(contextName);

            EventType namedWindowType = processor.getNamedWindowType();
            outputEventType = namedWindowType;
            statementContext.getDynamicReferenceEventTypes().add(onTriggerDesc.getWindowName());

            String namedWindowName = onTriggerDesc.getOptionalAsName();
            if (namedWindowName == null)
            {
                namedWindowName = "stream_0";
            }
            String streamName = streamSpec.getOptionalStreamName();
            if (streamName == null)
            {
                streamName = "stream_1";
            }
            String namedWindowTypeName = onTriggerDesc.getWindowName();

            // Materialize sub-select views
            // 0 - named window stream
            // 1 - arriving stream
            // 2 - initial value before update
            subSelectStrategyCollection = EPStatementStartMethodHelperSubselect.planSubSelect(services, statementContext, isQueryPlanLogging(services), subSelectStreamDesc, new String[]{namedWindowName, streamSpec.getOptionalStreamName()}, new EventType[]{processor.getNamedWindowType(), activatorResultEventType}, new String[]{namedWindowTypeName, triggereventTypeName}, stopCallbacks, statementSpec.getAnnotations(), statementSpec.getDeclaredExpressions(), contextPropertyRegistry);

            StreamTypeServiceImpl typeService = new StreamTypeServiceImpl(new EventType[] {namedWindowType, activatorResultEventType}, new String[] {namedWindowName, streamName}, new boolean[] {false, true}, services.getEngineURI(), true);

            // allow "initial" as a prefix to properties
            StreamTypeServiceImpl assignmentTypeService;
            if (namedWindowName.equals(INITIAL_VALUE_STREAM_NAME) || streamName.equals(INITIAL_VALUE_STREAM_NAME)) {
                assignmentTypeService = typeService;
            }
            else {
                assignmentTypeService = new StreamTypeServiceImpl(new EventType[] {namedWindowType, activatorResultEventType, namedWindowType}, new String[] {namedWindowName, streamName, INITIAL_VALUE_STREAM_NAME}, new boolean[] {false, true, true}, services.getEngineURI(), false);
                assignmentTypeService.setStreamZeroUnambigous(true);
            }

            if (onTriggerDesc instanceof OnTriggerWindowUpdateDesc) {
                OnTriggerWindowUpdateDesc updateDesc = (OnTriggerWindowUpdateDesc) onTriggerDesc;
                ExprValidationContext validationContext = new ExprValidationContext(assignmentTypeService, statementContext.getMethodResolutionService(), null, statementContext.getSchedulingService(), statementContext.getVariableService(), getDefaultAgentInstanceContext(statementContext), statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
                for (OnTriggerSetAssignment assignment : updateDesc.getAssignments())
                {
                    ExprNode validated = ExprNodeUtility.getValidatedSubtree(assignment.getExpression(), validationContext);
                    assignment.setExpression(validated);
                    EPStatementStartMethodHelperValidate.validateNoAggregations(validated, "Aggregation functions may not be used within an on-update-clause");
                }
            }
            if (onTriggerDesc instanceof OnTriggerMergeDesc) {
                OnTriggerMergeDesc mergeDesc = (OnTriggerMergeDesc) onTriggerDesc;
                validateMergeDesc(mergeDesc, statementContext, processor.getNamedWindowType(), namedWindowName,  activatorResultEventType, streamName);
            }

            // validate join expression
            validatedJoin = validateJoinNamedWindow(services.getEngineURI(), statementContext, statementSpec.getFilterRootNode(),
                    namedWindowType, namedWindowName, namedWindowTypeName,
                    activatorResultEventType, streamName, triggereventTypeName);

            // validate filter, output rate limiting
            EPStatementStartMethodHelperValidate.validateNodes(statementSpec, statementContext, typeService, null);

            // Construct a processor for results; for use in on-select to process selection results
            // Use a wildcard select if the select-clause is empty, such as for on-delete.
            // For on-select the select clause is not empty.
            if (statementSpec.getSelectClauseSpec().getSelectExprList().size() == 0) {
                statementSpec.getSelectClauseSpec().add(new SelectClauseElementWildcard());
            }
            resultSetProcessorPrototype = ResultSetProcessorFactoryFactory.getProcessorPrototype(
                    statementSpec, statementContext, typeService, null, new boolean[0], true, contextPropertyRegistry, null);

            InternalEventRouter routerService = null;
            boolean addToFront = false;
            if (statementSpec.getInsertIntoDesc() != null || onTriggerDesc instanceof OnTriggerMergeDesc) {
                routerService = services.getInternalEventRouter();
            }
            if (statementSpec.getInsertIntoDesc() != null) {
                addToFront = statementContext.getNamedWindowService().isNamedWindow(statementSpec.getInsertIntoDesc().getEventTypeName());
            }
            boolean isDistinct = statementSpec.getSelectClauseSpec().isDistinct();
            EventType selectResultEventType = resultSetProcessorPrototype.getResultSetProcessorFactory().getResultEventType();
            StatementMetricHandle createNamedWindowMetricsHandle = processor.getCreateNamedWindowMetricsHandle();

            onExprFactory = NamedWindowOnExprFactoryFactory.make(namedWindowType, onTriggerDesc.getWindowName(), namedWindowName,
                    onTriggerDesc,
                    activatorResultEventType, streamSpec.getOptionalStreamName(), addToFront, routerService,
                    selectResultEventType,
                    statementContext, createNamedWindowMetricsHandle, isDistinct);
        }
        // variable assignments
        else if (statementSpec.getOnTriggerDesc() instanceof OnTriggerSetDesc)
        {
            OnTriggerSetDesc desc = (OnTriggerSetDesc) statementSpec.getOnTriggerDesc();
            StreamTypeService typeService = new StreamTypeServiceImpl(new EventType[] {activatorResultEventType}, new String[] {streamSpec.getOptionalStreamName()}, new boolean[] {true}, services.getEngineURI(), false);
            ExprValidationContext validationContext = new ExprValidationContext(typeService, statementContext.getMethodResolutionService(), null, statementContext.getSchedulingService(), statementContext.getVariableService(), getDefaultAgentInstanceContext(statementContext), statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());

            // Materialize sub-select views
            subSelectStrategyCollection = EPStatementStartMethodHelperSubselect.planSubSelect(services, statementContext, isQueryPlanLogging(services), subSelectStreamDesc, new String[]{streamSpec.getOptionalStreamName()}, new EventType[]{activatorResultEventType}, new String[]{triggereventTypeName}, stopCallbacks, statementSpec.getAnnotations(), statementSpec.getDeclaredExpressions(), contextPropertyRegistry);

            for (OnTriggerSetAssignment assignment : desc.getAssignments()) {
                ExprNode validated = ExprNodeUtility.getValidatedSubtree(assignment.getExpression(), validationContext);
                assignment.setExpression(validated);
            }

            try {
                ExprEvaluatorContextStatement exprEvaluatorContext = new ExprEvaluatorContextStatement(statementContext);
                onSetVariableViewFactory = new OnSetVariableViewFactory(statementContext.getStatementId(), desc, statementContext.getEventAdapterService(), statementContext.getVariableService(), statementContext.getStatementResultService(), exprEvaluatorContext);
            }
            catch (VariableValueException ex) {
                throw new ExprValidationException("Error in variable assignment: " + ex.getMessage(), ex);
            }

            outputEventType = onSetVariableViewFactory.getEventType();
        }
        // split-stream use case
        else
        {
            OnTriggerSplitStreamDesc desc = (OnTriggerSplitStreamDesc) statementSpec.getOnTriggerDesc();
            String streamName = streamSpec.getOptionalStreamName();
            if (streamName == null)
            {
                streamName = "stream_0";
            }
            StreamTypeService typeService = new StreamTypeServiceImpl(new EventType[] {activatorResultEventType}, new String[] {streamName}, new boolean[] {true}, services.getEngineURI(), false);
            if (statementSpec.getInsertIntoDesc() == null)
            {
                throw new ExprValidationException("Required insert-into clause is not provided, the clause is required for split-stream syntax");
            }
            if ((!statementSpec.getGroupByExpressions().isEmpty()) || (statementSpec.getHavingExprRootNode() != null) || (!statementSpec.getOrderByList().isEmpty()))
            {
                throw new ExprValidationException("A group-by clause, having-clause or order-by clause is not allowed for the split stream syntax");
            }

            // Materialize sub-select views
            subSelectStrategyCollection = EPStatementStartMethodHelperSubselect.planSubSelect(services, statementContext, isQueryPlanLogging(services), subSelectStreamDesc, new String[]{streamSpec.getOptionalStreamName()}, new EventType[]{activatorResultEventType}, new String[]{triggereventTypeName}, stopCallbacks, statementSpec.getAnnotations(), statementSpec.getDeclaredExpressions(), contextPropertyRegistry);

            EPStatementStartMethodHelperValidate.validateNodes(statementSpec, statementContext, typeService, null);

            ResultSetProcessorFactoryDesc[] processorFactories = new ResultSetProcessorFactoryDesc[desc.getSplitStreams().size() + 1];
            ExprNode[] whereClauses = new ExprNode[desc.getSplitStreams().size() + 1];
            processorFactories[0] = ResultSetProcessorFactoryFactory.getProcessorPrototype(
                    statementSpec, statementContext, typeService, null, new boolean[0], false, contextPropertyRegistry, null);
            whereClauses[0] = statementSpec.getFilterRootNode();
            boolean[] isNamedWindowInsert = new boolean[desc.getSplitStreams().size() + 1];
            isNamedWindowInsert[0] = false;

            int index = 1;
            for (OnTriggerSplitStream splits : desc.getSplitStreams())
            {
                StatementSpecCompiled splitSpec = new StatementSpecCompiled();
                splitSpec.setInsertIntoDesc(splits.getInsertInto());
                splitSpec.setSelectClauseSpec(StatementLifecycleSvcImpl.compileSelectAllowSubselect(splits.getSelectClause()));
                splitSpec.setFilterExprRootNode(splits.getWhereClause());
                EPStatementStartMethodHelperValidate.validateNodes(splitSpec, statementContext, typeService, null);

                processorFactories[index] = ResultSetProcessorFactoryFactory.getProcessorPrototype(
                        splitSpec, statementContext, typeService, null, new boolean[0], false, contextPropertyRegistry, null);
                whereClauses[index] = splitSpec.getFilterRootNode();
                isNamedWindowInsert[index] = statementContext.getNamedWindowService().isNamedWindow(splits.getInsertInto().getEventTypeName());

                index++;
            }

            splitDesc = new StatementAgentInstanceFactoryOnTriggerSplitDesc(processorFactories, whereClauses, isNamedWindowInsert);
        }

        // For on-delete/set/update/merge, create an output processor that passes on as a wildcard the underlying event
        if ((statementSpec.getOnTriggerDesc().getOnTriggerType() == OnTriggerType.ON_DELETE) ||
            (statementSpec.getOnTriggerDesc().getOnTriggerType() == OnTriggerType.ON_SET) ||
            (statementSpec.getOnTriggerDesc().getOnTriggerType() == OnTriggerType.ON_UPDATE) ||
            (statementSpec.getOnTriggerDesc().getOnTriggerType() == OnTriggerType.ON_MERGE))
        {
            StatementSpecCompiled defaultSelectAllSpec = new StatementSpecCompiled();
            defaultSelectAllSpec.getSelectClauseSpec().add(new SelectClauseElementWildcard());

            StreamTypeService streamTypeService = new StreamTypeServiceImpl(new EventType[] {outputEventType}, new String[] {"trigger_stream"}, new boolean[] {true}, services.getEngineURI(), false);
            outputResultSetProcessorPrototype = ResultSetProcessorFactoryFactory.getProcessorPrototype(defaultSelectAllSpec, statementContext, streamTypeService, null, new boolean[0], true, contextPropertyRegistry, null);
        }

        EventType resultEventType = resultSetProcessorPrototype == null ? null : resultSetProcessorPrototype.getResultSetProcessorFactory().getResultEventType();
        OutputProcessViewFactory outputViewFactory = OutputProcessViewFactoryFactory.make(statementSpec, services.getInternalEventRouter(), statementContext, resultEventType, null);

        // create context factory
        StatementAgentInstanceFactoryOnTrigger contextFactory = new StatementAgentInstanceFactoryOnTrigger(statementContext, statementSpec, services, activator, subSelectStrategyCollection, resultSetProcessorPrototype, validatedJoin, activatorResultEventType, splitDesc, outputResultSetProcessorPrototype, onSetVariableViewFactory, onExprFactory, outputViewFactory, isRecoveringStatement);

        // perform start of hook-up to start
        Viewable finalViewable;
        EPStatementStopMethod stopStatementMethod;
        EPStatementDestroyMethod destroyStatementMethod;
        Map<ExprSubselectNode, SubSelectStrategyHolder> subselectStrategyInstances;
        AggregationService aggregationService;

        // With context - delegate instantiation to context
        if (statementSpec.getOptionalContextName() != null) {

            // use statement-wide agent-instance-specific aggregation service
            aggregationService = statementContext.getStatementAgentInstanceRegistry().getAgentInstanceAggregationService();

            // use statement-wide agent-instance-specific subselects
            AIRegistryExpr aiRegistryExpr = statementContext.getStatementAgentInstanceRegistry().getAgentInstanceExprService();
            subselectStrategyInstances = new HashMap<ExprSubselectNode, SubSelectStrategyHolder>();
            for (ExprSubselectNode node : subSelectStrategyCollection.getSubqueries().keySet()) {
                AIRegistrySubselect specificService = aiRegistryExpr.allocateSubselect(node);
                node.setStrategy(specificService);
                subselectStrategyInstances.put(node, new SubSelectStrategyHolder(specificService, null, null, null, null, null));
            }

            ContextMergeView mergeView = new ContextMergeView(resultSetProcessorPrototype.getResultSetProcessorFactory().getResultEventType());
            finalViewable = mergeView;

            ContextManagedStatementOnTriggerDesc statement = new ContextManagedStatementOnTriggerDesc(statementSpec, statementContext, mergeView, contextFactory);
            services.getContextManagementService().addStatement(contextName, statement, isRecoveringResilient);
            stopStatementMethod = new EPStatementStopMethod(){
                public void stop()
                {
                    services.getContextManagementService().stoppedStatement(contextName, statementContext.getStatementName(), statementContext.getStatementId());
                    stopMethod.stop();
                }
            };

            destroyStatementMethod = new EPStatementDestroyMethod(){
                public void destroy() {
                    services.getContextManagementService().destroyedStatement(contextName, statementContext.getStatementName(), statementContext.getStatementId());
                }
            };
        }
        // Without context - start here
        else {
            AgentInstanceContext agentInstanceContext = getDefaultAgentInstanceContext(statementContext);
            final StatementAgentInstanceFactoryOnTriggerResult resultOfStart = contextFactory.newContext(agentInstanceContext, false);
            finalViewable = resultOfStart.getFinalView();
            stopStatementMethod = new EPStatementStopMethod() {
                public void stop() {
                    resultOfStart.getStopCallback().stop();
                    stopMethod.stop();
                }
            };
            destroyStatementMethod = null;
            aggregationService = resultOfStart.getOptionalAggegationService();
            subselectStrategyInstances = resultOfStart.getSubselectStrategies();

            if (statementContext.getExtensionServicesContext() != null) {
                statementContext.getExtensionServicesContext().startContextPartition(resultOfStart, 0);
            }
        }

        // initialize aggregation expression nodes
        if (resultSetProcessorPrototype != null && resultSetProcessorPrototype.getAggregationServiceFactoryDesc() != null) {
            EPStatementStartMethodHelperAssignExpr.assignAggregations(aggregationService, resultSetProcessorPrototype.getAggregationServiceFactoryDesc().getExpressions());
        }

        // assign subquery nodes
        EPStatementStartMethodHelperAssignExpr.assignSubqueryStrategies(subSelectStrategyCollection, subselectStrategyInstances);        

        return new EPStatementStartResult(finalViewable, stopStatementMethod, destroyStatementMethod);
    }

    private void validateMergeDesc(OnTriggerMergeDesc mergeDesc, StatementContext statementContext, EventType namedWindowType, String namedWindowName, EventType triggerStreamType, String triggerStreamName)
        throws ExprValidationException
    {
        String exprNodeErrorMessage = "Aggregation functions may not be used within an merge-clause";
        ExprEvaluatorContextStatement evaluatorContextStmt = new ExprEvaluatorContextStatement(statementContext);

        for (OnTriggerMergeMatched matchedItem : mergeDesc.getItems()) {

            EventType dummyTypeNoProperties = new MapEventType(EventTypeMetadata.createAnonymous("merge_named_window_insert"), "merge_named_window_insert", 0, null, Collections.<String, Object>emptyMap(), null, null, null);
            StreamTypeServiceImpl twoStreamTypeSvc = new StreamTypeServiceImpl(new EventType[] {namedWindowType, triggerStreamType},
                    new String[] {namedWindowName, triggerStreamName}, new boolean[] {true, true}, statementContext.getEngineURI(), true);
            StreamTypeService insertOnlyTypeSvc = new StreamTypeServiceImpl(new EventType[] {dummyTypeNoProperties, triggerStreamType},
                    new String[] {UuidGenerator.generate(), triggerStreamName}, new boolean[] {true, true}, statementContext.getEngineURI(), true);

            // we may provide an additional stream "initial" for the prior value, unless already defined
            StreamTypeServiceImpl assignmentStreamTypeSvc;
            if (namedWindowName.equals(INITIAL_VALUE_STREAM_NAME) || triggerStreamName.equals(INITIAL_VALUE_STREAM_NAME)) {
                assignmentStreamTypeSvc = twoStreamTypeSvc;
            }
            else {
                assignmentStreamTypeSvc = new StreamTypeServiceImpl(new EventType[] {namedWindowType, triggerStreamType, namedWindowType},
                        new String[] {namedWindowName, triggerStreamName, INITIAL_VALUE_STREAM_NAME}, new boolean[] {true, true, true}, statementContext.getEngineURI(), false);
                assignmentStreamTypeSvc.setStreamZeroUnambigous(true);
            }

            if (matchedItem.getOptionalMatchCond() != null) {
                StreamTypeService matchValidStreams = matchedItem.isMatchedUnmatched() ? twoStreamTypeSvc : insertOnlyTypeSvc;
                matchedItem.setOptionalMatchCond(EPStatementStartMethodHelperValidate.validateExprNoAgg(matchedItem.getOptionalMatchCond(), matchValidStreams, statementContext, evaluatorContextStmt, exprNodeErrorMessage));
                if (!matchedItem.isMatchedUnmatched()) {
                    EPStatementStartMethodHelperValidate.validateSubqueryExcludeOuterStream(matchedItem.getOptionalMatchCond());
                }
            }

            for (OnTriggerMergeAction item : matchedItem.getActions()) {
                if (item instanceof OnTriggerMergeActionDelete) {
                    OnTriggerMergeActionDelete delete = (OnTriggerMergeActionDelete) item;
                    if (delete.getOptionalWhereClause() != null) {
                        delete.setOptionalWhereClause(EPStatementStartMethodHelperValidate.validateExprNoAgg(delete.getOptionalWhereClause(), twoStreamTypeSvc, statementContext, evaluatorContextStmt, exprNodeErrorMessage));
                    }
                }
                else if (item instanceof OnTriggerMergeActionUpdate) {
                    OnTriggerMergeActionUpdate update = (OnTriggerMergeActionUpdate) item;
                    if (update.getOptionalWhereClause() != null) {
                        update.setOptionalWhereClause(EPStatementStartMethodHelperValidate.validateExprNoAgg(update.getOptionalWhereClause(), twoStreamTypeSvc, statementContext, evaluatorContextStmt, exprNodeErrorMessage));
                    }
                    for (OnTriggerSetAssignment assignment : update.getAssignments())
                    {
                        assignment.setExpression(EPStatementStartMethodHelperValidate.validateExprNoAgg(assignment.getExpression(), assignmentStreamTypeSvc, statementContext, evaluatorContextStmt, exprNodeErrorMessage));
                    }
                }
                else if (item instanceof OnTriggerMergeActionInsert) {
                    OnTriggerMergeActionInsert insert = (OnTriggerMergeActionInsert) item;

                    StreamTypeService insertTypeSvc;
                    if (insert.getOptionalStreamName() == null || insert.getOptionalStreamName().equals(namedWindowName)) {
                        insertTypeSvc = insertOnlyTypeSvc;
                    }
                    else {
                        insertTypeSvc = twoStreamTypeSvc;
                    }

                    List<SelectClauseElementCompiled> compiledSelect = new ArrayList<SelectClauseElementCompiled>();
                    if (insert.getOptionalWhereClause() != null) {
                        insert.setOptionalWhereClause(EPStatementStartMethodHelperValidate.validateExprNoAgg(insert.getOptionalWhereClause(), insertTypeSvc, statementContext, evaluatorContextStmt, exprNodeErrorMessage));
                    }
                    int colIndex = 0;
                    for (SelectClauseElementRaw raw : insert.getSelectClause())
                    {
                        if (raw instanceof SelectClauseStreamRawSpec)
                        {
                            SelectClauseStreamRawSpec rawStreamSpec = (SelectClauseStreamRawSpec) raw;
                            Integer foundStreamNum = null;
                            for (int s = 0; s < insertTypeSvc.getStreamNames().length; s++) {
                                if (rawStreamSpec.getStreamName().equals(insertTypeSvc.getStreamNames()[s])) {
                                    foundStreamNum = s;
                                    break;
                                }
                            }
                            if (foundStreamNum == null) {
                                throw new ExprValidationException("Stream by name '" + rawStreamSpec.getStreamName() + "' was not found");
                            }
                            SelectClauseStreamCompiledSpec streamSelectSpec = new SelectClauseStreamCompiledSpec(rawStreamSpec.getStreamName(), rawStreamSpec.getOptionalAsName());
                            streamSelectSpec.setStreamNumber(foundStreamNum);
                            compiledSelect.add(streamSelectSpec);
                        }
                        else if (raw instanceof SelectClauseExprRawSpec)
                        {
                            SelectClauseExprRawSpec exprSpec = (SelectClauseExprRawSpec) raw;
                            ExprValidationContext validationContext = new ExprValidationContext(insertTypeSvc, statementContext.getMethodResolutionService(), null, statementContext.getTimeProvider(), statementContext.getVariableService(), evaluatorContextStmt, statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
                            ExprNode exprCompiled = ExprNodeUtility.getValidatedSubtree(exprSpec.getSelectExpression(), validationContext);
                            String resultName = exprSpec.getOptionalAsName();
                            if (resultName == null)
                            {
                                if (insert.getColumns().size() > colIndex) {
                                    resultName = insert.getColumns().get(colIndex);
                                }
                                else {
                                    resultName = exprCompiled.toExpressionString();
                                }
                            }
                            compiledSelect.add(new SelectClauseExprCompiledSpec(exprCompiled, resultName, exprSpec.getOptionalAsName()));
                            EPStatementStartMethodHelperValidate.validateNoAggregations(exprCompiled, "Expression in a merge-selection may not utilize aggregation functions");
                        }
                        else if (raw instanceof SelectClauseElementWildcard)
                        {
                            compiledSelect.add(new SelectClauseElementWildcard());
                        }
                        else
                        {
                            throw new IllegalStateException("Unknown select clause item:" + raw);
                        }
                        colIndex++;
                    }
                    insert.setSelectClauseCompiled(compiledSelect);
                }
                else {
                    throw new IllegalArgumentException("Unrecognized merge item '" + item.getClass().getName() + "'");
                }
            }
        }
    }

    // For delete actions from named windows
    protected ExprNode validateJoinNamedWindow(String engineURI,
                                               StatementContext statementContext,
                                         ExprNode deleteJoinExpr,
                                         EventType namedWindowType,
                                         String namedWindowStreamName,
                                         String namedWindowName,
                                         EventType filteredType,
                                         String filterStreamName,
                                         String filteredTypeName) throws ExprValidationException
    {
        if (deleteJoinExpr == null)
        {
            return null;
        }

        LinkedHashMap<String, Pair<EventType, String>> namesAndTypes = new LinkedHashMap<String, Pair<EventType, String>>();
        namesAndTypes.put(namedWindowStreamName, new Pair<EventType, String>(namedWindowType, namedWindowName));
        namesAndTypes.put(filterStreamName, new Pair<EventType, String>(filteredType, filteredTypeName));
        StreamTypeService typeService = new StreamTypeServiceImpl(namesAndTypes, engineURI, false, false);

        ExprEvaluatorContextStatement evaluatorContextStmt = new ExprEvaluatorContextStatement(statementContext);
        ExprValidationContext validationContext = new ExprValidationContext(typeService, statementContext.getMethodResolutionService(), null, statementContext.getSchedulingService(), statementContext.getVariableService(), evaluatorContextStmt, statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
        return ExprNodeUtility.getValidatedSubtree(deleteJoinExpr, validationContext);
    }
}
