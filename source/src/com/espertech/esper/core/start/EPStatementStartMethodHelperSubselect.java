/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.start;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.annotation.HintEnum;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.core.context.activator.*;
import com.espertech.esper.core.context.subselect.*;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.context.util.ContextPropertyRegistry;
import com.espertech.esper.core.service.EPServicesContext;
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.agg.service.AggregationServiceFactoryDesc;
import com.espertech.esper.epl.agg.service.AggregationServiceFactoryFactory;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.StreamTypeServiceImpl;
import com.espertech.esper.epl.core.ViewResourceDelegateUnverified;
import com.espertech.esper.epl.core.ViewResourceDelegateVerified;
import com.espertech.esper.epl.declexpr.ExprDeclaredNode;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.join.hint.IndexHint;
import com.espertech.esper.epl.join.plan.CoercionDesc;
import com.espertech.esper.epl.join.plan.CoercionUtil;
import com.espertech.esper.epl.join.plan.QueryPlanIndexBuilder;
import com.espertech.esper.epl.join.table.*;
import com.espertech.esper.epl.join.util.QueryPlanIndexDescSubquery;
import com.espertech.esper.epl.join.util.QueryPlanIndexHook;
import com.espertech.esper.epl.join.util.QueryPlanIndexHookUtil;
import com.espertech.esper.epl.lookup.*;
import com.espertech.esper.epl.named.NamedWindowProcessor;
import com.espertech.esper.epl.named.NamedWindowProcessorInstance;
import com.espertech.esper.epl.named.NamedWindowSubqueryStopCallback;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.util.AuditPath;
import com.espertech.esper.util.CollectionUtil;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.util.*;

public class EPStatementStartMethodHelperSubselect
{
    private static final Log log = LogFactory.getLog(EPStatementStartMethodHelperSubselect.class);
    private static final Log queryPlanLog = LogFactory.getLog(AuditPath.QUERYPLAN_LOG);

    protected static SubSelectActivationCollection createSubSelectActivation(EPServicesContext services, StatementSpecCompiled statementSpecContainer, StatementContext statementContext)
            throws ExprValidationException, ViewProcessingException
    {
        SubSelectActivationCollection subSelectStreamDesc = new SubSelectActivationCollection();
        int subselectStreamNumber = 1024;

        // Process all subselect expression nodes
        for (ExprSubselectNode subselect : statementSpecContainer.getSubSelectExpressions())
        {
            StatementSpecCompiled statementSpec = subselect.getStatementSpecCompiled();
            StreamSpecCompiled streamSpec = statementSpec.getStreamSpecs().get(0);

            if (streamSpec instanceof FilterStreamSpecCompiled)
            {
                FilterStreamSpecCompiled filterStreamSpec = (FilterStreamSpecCompiled) statementSpec.getStreamSpecs().get(0);

                // A child view is required to limit the stream
                if (filterStreamSpec.getViewSpecs().size() == 0)
                {
                    throw new ExprValidationException("Subqueries require one or more views to limit the stream, consider declaring a length or time window");
                }

                subselectStreamNumber++;

                // Register filter, create view factories
                ViewableActivator activatorDeactivator = new ViewableActivatorFilterProxy(services, filterStreamSpec.getFilterSpec(), statementSpec.getAnnotations(), true);
                ViewFactoryChain viewFactoryChain = services.getViewService().createFactories(subselectStreamNumber, filterStreamSpec.getFilterSpec().getResultEventType(), filterStreamSpec.getViewSpecs(), filterStreamSpec.getOptions(), statementContext);
                subselect.setRawEventType(viewFactoryChain.getEventType());

                // Add lookup to list, for later starts
                subSelectStreamDesc.add(subselect, new SubSelectActivationHolder(subselectStreamNumber, filterStreamSpec.getFilterSpec().getResultEventType(), viewFactoryChain, activatorDeactivator, streamSpec));
            }
            else
            {
                NamedWindowConsumerStreamSpec namedSpec = (NamedWindowConsumerStreamSpec) statementSpec.getStreamSpecs().get(0);
                NamedWindowProcessor processor = services.getNamedWindowService().getProcessor(namedSpec.getWindowName());
                EventType namedWindowType = processor.getTailView().getEventType();
                if (namedSpec.getOptPropertyEvaluator() != null) {
                    namedWindowType = namedSpec.getOptPropertyEvaluator().getFragmentEventType();
                }

                // if named-window index sharing is disabled (the default) or filter expressions are provided then consume the insert-remove stream
                boolean disableIndexShare = HintEnum.DISABLE_WINDOW_SUBQUERY_INDEXSHARE.getHint(statementSpecContainer.getAnnotations()) != null;
                if (!namedSpec.getFilterExpressions().isEmpty() || !processor.isEnableSubqueryIndexShare() || disableIndexShare) {
                    ViewableActivatorNamedWindow activatorNamedWindow = new ViewableActivatorNamedWindow(processor, namedSpec.getFilterExpressions(), namedSpec.getOptPropertyEvaluator());
                    ViewFactoryChain viewFactoryChain = services.getViewService().createFactories(0, namedWindowType, namedSpec.getViewSpecs(), namedSpec.getOptions(), statementContext);
                    subselect.setRawEventType(viewFactoryChain.getEventType());
                    subSelectStreamDesc.add(subselect, new SubSelectActivationHolder(subselectStreamNumber, namedWindowType, viewFactoryChain, activatorNamedWindow, streamSpec));
                }
                // else if there are no named window stream filter expressions and index sharing is enabled
                else {
                    ViewFactoryChain viewFactoryChain = services.getViewService().createFactories(0, processor.getNamedWindowType(), namedSpec.getViewSpecs(), namedSpec.getOptions(), statementContext);
                    subselect.setRawEventType(processor.getNamedWindowType());
                    ViewableActivatorSubselectNone activator = new ViewableActivatorSubselectNone();
                    subSelectStreamDesc.add(subselect, new SubSelectActivationHolder(subselectStreamNumber, namedWindowType, viewFactoryChain, activator, streamSpec));
                }
            }
        }

        return subSelectStreamDesc;
    }

    protected static SubSelectStrategyCollection planSubSelect(EPServicesContext services,
                                                               StatementContext statementContext,
                                                               boolean queryPlanLogging,
                                                               SubSelectActivationCollection subSelectStreamDesc,
                                                               String[] outerStreamNames,
                                                               EventType[] outerEventTypesSelect,
                                                               String[] outerEventTypeNamees,
                                                               List<StopCallback> stopCallbacks,
                                                               Annotation[] annotations,
                                                               List<ExprDeclaredNode> declaredExpressions,
                                                               ContextPropertyRegistry contextPropertyRegistry)
            throws ExprValidationException, ViewProcessingException
    {
        int subqueryNum = -1;
        SubSelectStrategyCollection collection = new SubSelectStrategyCollection();
        IndexHint indexHint = IndexHint.getIndexHint(statementContext.getAnnotations());

        for (Map.Entry<ExprSubselectNode, SubSelectActivationHolder> entry : subSelectStreamDesc.getSubqueries().entrySet())
        {
            subqueryNum++;
            ExprSubselectNode subselect = entry.getKey();
            SubSelectActivationHolder subSelectActivation = entry.getValue();

            if (queryPlanLogging && queryPlanLog.isInfoEnabled()) {
                queryPlanLog.info("For statement '" + statementContext.getStatementName() + "' subquery " + subqueryNum);
            }

            StatementSpecCompiled statementSpec = subselect.getStatementSpecCompiled();
            StreamSpecCompiled filterStreamSpec = statementSpec.getStreamSpecs().get(0);

            String subselecteventTypeName = null;
            if (filterStreamSpec instanceof FilterStreamSpecCompiled)
            {
                subselecteventTypeName = ((FilterStreamSpecCompiled) filterStreamSpec).getFilterSpec().getFilterForEventTypeName();
            }
            else if (filterStreamSpec instanceof NamedWindowConsumerStreamSpec)
            {
                subselecteventTypeName = ((NamedWindowConsumerStreamSpec) filterStreamSpec).getWindowName();
            }

            ViewFactoryChain viewFactoryChain = subSelectStreamDesc.getViewFactoryChain(subselect);
            EventType eventType = viewFactoryChain.getEventType();

            // determine a stream name unless one was supplied
            String subexpressionStreamName = filterStreamSpec.getOptionalStreamName();
            int subselectStreamNumber = subSelectStreamDesc.getStreamNumber(subselect);
            if (subexpressionStreamName == null)
            {
                subexpressionStreamName = "$subselect_" + subselectStreamNumber;
            }

            // Named windows don't allow data views
            if (filterStreamSpec instanceof NamedWindowConsumerStreamSpec)
            {
                EPStatementStartMethodHelperValidate.validateNoDataWindowOnNamedWindow(viewFactoryChain.getViewFactoryChain());
            }

            // Expression declarations are copies of a predefined expression body with their own stream context.
            // Should only be invoked if the subselect belongs to that instance.
            if (!declaredExpressions.isEmpty()) {
                // Find that subselect within that declaration
                ExprNodeSubselectDeclaredDotVisitor visitor = new ExprNodeSubselectDeclaredDotVisitor();
                for (ExprDeclaredNode declaration : declaredExpressions) {
                    visitor.reset();
                    declaration.accept(visitor);
                    if (visitor.getSubselects().contains(subselect)) {
                        declaration.setSubselectOuterStreamNames(outerStreamNames, outerEventTypesSelect, outerEventTypeNamees, services.getEngineURI(), subselect, subexpressionStreamName, eventType, subselecteventTypeName);
                    }
                }
            }

            EventType[] outerEventTypes;
            StreamTypeService subselectTypeService;

            // Use the override provided by the subselect if applicable
            if (subselect.getFilterSubqueryStreamTypes() != null) {
                subselectTypeService = subselect.getFilterSubqueryStreamTypes();
                outerEventTypes = new EventType[subselectTypeService.getEventTypes().length - 1];
                System.arraycopy(subselectTypeService.getEventTypes(), 1, outerEventTypes, 0, subselectTypeService.getEventTypes().length - 1);
            }
            else {
                // Streams event types are the original stream types with the stream zero the subselect stream
                LinkedHashMap<String, Pair<EventType, String>> namesAndTypes = new LinkedHashMap<String, Pair<EventType, String>>();
                namesAndTypes.put(subexpressionStreamName, new Pair<EventType, String>(eventType, subselecteventTypeName));
                for (int i = 0; i < outerEventTypesSelect.length; i++)
                {
                    Pair<EventType, String> pair = new Pair<EventType, String>(outerEventTypesSelect[i], outerEventTypeNamees[i]);
                    namesAndTypes.put(outerStreamNames[i], pair);
                }
                subselectTypeService = new StreamTypeServiceImpl(namesAndTypes, services.getEngineURI(), true, true);
                outerEventTypes = outerEventTypesSelect;
            }
            ViewResourceDelegateUnverified viewResourceDelegateSubselect = new ViewResourceDelegateUnverified();

            // Validate select expression
            SelectClauseSpecCompiled selectClauseSpec = subselect.getStatementSpecCompiled().getSelectClauseSpec();
            AggregationServiceFactoryDesc aggregationServiceFactoryDesc = null;
            List<ExprNode> selectExpressions = new ArrayList<ExprNode>();
            List<String> assignedNames = new ArrayList<String>();
            boolean isWildcard = false;
            boolean isStreamWildcard = false;
            if (selectClauseSpec.getSelectExprList().size() > 0)
            {
                List<ExprAggregateNode> aggExprNodes = new LinkedList<ExprAggregateNode>();

                ExprEvaluatorContextStatement evaluatorContextStmt = new ExprEvaluatorContextStatement(statementContext);
                ExprValidationContext validationContext = new ExprValidationContext(subselectTypeService, statementContext.getMethodResolutionService(), viewResourceDelegateSubselect, statementContext.getSchedulingService(), statementContext.getVariableService(), evaluatorContextStmt, statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
                for (int i = 0; i < selectClauseSpec.getSelectExprList().size(); i++) {
                    SelectClauseElementCompiled element = selectClauseSpec.getSelectExprList().get(i);

                    if (element instanceof SelectClauseExprCompiledSpec)
                    {
                        // validate
                        SelectClauseExprCompiledSpec compiled = (SelectClauseExprCompiledSpec) element;
                        ExprNode selectExpression = compiled.getSelectExpression();
                        selectExpression = ExprNodeUtility.getValidatedSubtree(selectExpression, validationContext);

                        selectExpressions.add(selectExpression);
                        assignedNames.add(compiled.getAssignedName());

                        // handle aggregation
                        ExprAggregateNodeUtil.getAggregatesBottomUp(selectExpression, aggExprNodes);

                        if (aggExprNodes.size() > 0)
                        {
                            // This stream (stream 0) properties must either all be under aggregation, or all not be.
                            List<Pair<Integer, String>> propertiesNotAggregated = ExprNodeUtility.getExpressionProperties(selectExpression, false);
                            for (Pair<Integer, String> pair : propertiesNotAggregated)
                            {
                                if (pair.getFirst() == 0)
                                {
                                    throw new ExprValidationException("Subselect properties must all be within aggregation functions");
                                }
                            }
                        }
                    }
                    else if (element instanceof SelectClauseElementWildcard) {
                        isWildcard = true;
                    }
                    else if (element instanceof SelectClauseStreamCompiledSpec) {
                        isStreamWildcard = true;
                    }
                }   // end of for loop

                if (!selectExpressions.isEmpty()) {
                    subselect.setSelectClause(selectExpressions.toArray(new ExprNode[selectExpressions.size()]));
                    subselect.setSelectAsNames(assignedNames.toArray(new String[assignedNames.size()]));
                    if (isWildcard || isStreamWildcard) {
                        throw new ExprValidationException("Subquery multi-column select does not allow wildcard or stream wildcard when selecting multiple columns.");
                    }
                    if (selectExpressions.size() > 1 && !subselect.isAllowMultiColumnSelect()) {
                        throw new ExprValidationException("Subquery multi-column select is not allowed in this context.");
                    }
                    if ((selectExpressions.size() > 1 && aggExprNodes.size() > 0)) {
                        // all properties must be aggregated
                        if (!ExprNodeUtility.getNonAggregatedProps(subselectTypeService.getEventTypes(), selectExpressions, contextPropertyRegistry).isEmpty()) {
                            throw new ExprValidationException("Subquery with multi-column select requires that either all or none of the selected columns are under aggregation.");
                        }
                    }
                }

                if (aggExprNodes.size() > 0)
                {
                    List<ExprAggregateNode> havingAgg = Collections.emptyList();
                    List<ExprAggregateNode> orderByAgg = Collections.emptyList();
                    aggregationServiceFactoryDesc = AggregationServiceFactoryFactory.getService(aggExprNodes, havingAgg, orderByAgg, false, evaluatorContextStmt, annotations, statementContext.getVariableService(), false, statementSpec.getFilterRootNode(), statementSpec.getHavingExprRootNode(), statementContext.getAggregationServiceFactoryService(), subselectTypeService.getEventTypes());

                    // Other stream properties, if there is aggregation, cannot be under aggregation.
                    for (ExprAggregateNode aggNode : aggExprNodes)
                    {
                        List<Pair<Integer, String>> propertiesNodesAggregated = ExprNodeUtility.getExpressionProperties(aggNode, true);
                        for (Pair<Integer, String> pair : propertiesNodesAggregated)
                        {
                            if (pair.getFirst() != 0)
                            {
                                throw new ExprValidationException("Subselect aggregation functions cannot aggregate across correlated properties");
                            }
                        }
                    }
                }
            }

            // no aggregation functions allowed in filter
            if (statementSpec.getFilterRootNode() != null)
            {
                List<ExprAggregateNode> aggExprNodesFilter = new LinkedList<ExprAggregateNode>();
                ExprAggregateNodeUtil.getAggregatesBottomUp(statementSpec.getFilterRootNode(), aggExprNodesFilter);
                if (aggExprNodesFilter.size() > 0)
                {
                    throw new ExprValidationException("Aggregation functions are not supported within subquery filters, consider using insert-into instead");
                }
            }

            // Validate filter expression, if there is one
            ExprNode filterExpr = statementSpec.getFilterRootNode();
            boolean correlatedSubquery = false;
            if (filterExpr != null)
            {
                ExprEvaluatorContextStatement evaluatorContextStmt = new ExprEvaluatorContextStatement(statementContext);
                ExprValidationContext validationContext = new ExprValidationContext(subselectTypeService, statementContext.getMethodResolutionService(), viewResourceDelegateSubselect, statementContext.getSchedulingService(), statementContext.getVariableService(), evaluatorContextStmt, statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
                filterExpr = ExprNodeUtility.getValidatedSubtree(filterExpr, validationContext);
                if (JavaClassHelper.getBoxedType(filterExpr.getExprEvaluator().getType()) != Boolean.class)
                {
                    throw new ExprValidationException("Subselect filter expression must return a boolean value");
                }

                // check the presence of a correlated filter, not allowed with aggregation
                ExprNodeIdentifierVisitor visitor = new ExprNodeIdentifierVisitor(true);
                filterExpr.accept(visitor);
                List<Pair<Integer, String>> propertiesNodes = visitor.getExprProperties();
                for (Pair<Integer, String> pair : propertiesNodes)
                {
                    if (pair.getFirst() != 0)
                    {
                        correlatedSubquery = true;
                        break;
                    }
                }
            }

            ViewResourceDelegateVerified viewResourceDelegateVerified = EPStatementStartMethodHelperViewResources.verifyPreviousAndPriorRequirements(new ViewFactoryChain[]{viewFactoryChain}, viewResourceDelegateSubselect);
            List<ExprPriorNode> priorNodes = viewResourceDelegateVerified.getPerStream()[0].getPriorRequestsAsList();
            List<ExprPreviousNode> previousNodes = viewResourceDelegateVerified.getPerStream()[0].getPreviousRequests();

            // Set the aggregated flag
            // This must occur here as some analysis of return type depends on aggregated or not.
            subselect.setAggregatedSubquery(aggregationServiceFactoryDesc != null);

            // Set the filter.
            ExprEvaluator filterExprEval = (filterExpr == null) ? null : filterExpr.getExprEvaluator();
            ExprEvaluator assignedFilterExpr = aggregationServiceFactoryDesc != null ? null : filterExprEval;
            subselect.setFilterExpr(assignedFilterExpr);

            // validation for correlated subqueries against named windows contained-event syntax
            if ((filterStreamSpec instanceof NamedWindowConsumerStreamSpec && correlatedSubquery)) {
                NamedWindowConsumerStreamSpec namedSpec = (NamedWindowConsumerStreamSpec) filterStreamSpec;
                if (namedSpec.getOptPropertyEvaluator() != null) {
                    throw new ExprValidationException("Failed to validate named window use in subquery, contained-event is only allowed for named windows when not correlated");
                }
            }

            // Determine strategy factories
            //
            // handle named window index share first
            boolean disableIndexShare = HintEnum.DISABLE_WINDOW_SUBQUERY_INDEXSHARE.getHint(annotations) != null;
            if ((filterStreamSpec instanceof NamedWindowConsumerStreamSpec) && (!disableIndexShare)) {
                NamedWindowConsumerStreamSpec namedSpec = (NamedWindowConsumerStreamSpec) filterStreamSpec;
                if (namedSpec.getFilterExpressions().isEmpty()) {
                    NamedWindowProcessor processor = services.getNamedWindowService().getProcessor(namedSpec.getWindowName());
                    if (processor == null) {
                        throw new ExprValidationException("A named window by name '" + namedSpec.getWindowName() + "' does not exist");
                    }
                    if (processor.isEnableSubqueryIndexShare()) {
                        if (queryPlanLogging && queryPlanLog.isInfoEnabled()) {
                            queryPlanLog.info("prefering shared index");
                        }
                        boolean fullTableScan = HintEnum.SET_NOINDEX.getHint(annotations) != null;
                        SubordPropPlan joinedPropPlan = QueryPlanIndexBuilder.getJoinProps(filterExpr, outerEventTypes.length, subselectTypeService.getEventTypes());
                        NamedWindowProcessorInstance processorInstanceSubq = processor.getProcessorInstance(null);
                        SubordTableLookupStrategy namedWindowSubqueryLookup = processorInstanceSubq.getRootViewInstance().getAddSubqueryLookupStrategy(statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), outerEventTypesSelect, joinedPropPlan, fullTableScan, subqueryNum, indexHint);
                        stopCallbacks.add(new NamedWindowSubqueryStopCallback(processorInstanceSubq, namedWindowSubqueryLookup));
                        SubSelectStrategyFactory factory = new SubSelectStrategyFactoryNamedWinIndexShare(namedWindowSubqueryLookup, filterExprEval, aggregationServiceFactoryDesc);
                        SubSelectStrategyFactoryDesc factoryDesc = new SubSelectStrategyFactoryDesc(subSelectActivation, factory, aggregationServiceFactoryDesc, priorNodes, previousNodes);
                        collection.add(subselect, factoryDesc);
                        continue;
                    }
                }
            }

            // determine unique keys, if any
            Set<String> optionalUniqueProps = null;
            if (viewFactoryChain.getDataWindowViewFactoryCount() > 0) {
                optionalUniqueProps = ViewServiceHelper.getUniqueCandidateProperties(viewFactoryChain.getViewFactoryChain());
            }
            if (filterStreamSpec instanceof NamedWindowConsumerStreamSpec) {
                NamedWindowConsumerStreamSpec namedSpec = (NamedWindowConsumerStreamSpec) filterStreamSpec;
                NamedWindowProcessor processor = services.getNamedWindowService().getProcessor(namedSpec.getWindowName());
                optionalUniqueProps = processor.getOptionalUniqueKeyProps();
            }

            // handle local stream + named-window-stream
            boolean fullTableScan = HintEnum.SET_NOINDEX.getHint(annotations) != null;
            Pair<EventTableFactory, SubordTableLookupStrategyFactory> indexPair = determineSubqueryIndexFactory(filterExpr, eventType,
                    outerEventTypes, subselectTypeService, fullTableScan, queryPlanLogging, optionalUniqueProps, annotations, subqueryNum);

            SubSelectStrategyFactory factory = new SubSelectStrategyFactoryLocalViewPreloaded(subqueryNum, subSelectActivation, indexPair, filterExprEval, correlatedSubquery, aggregationServiceFactoryDesc, viewResourceDelegateVerified);
            SubSelectStrategyFactoryDesc factoryDesc = new SubSelectStrategyFactoryDesc(subSelectActivation, factory, aggregationServiceFactoryDesc, priorNodes, previousNodes);

            collection.add(subselect, factoryDesc);
        }

        return collection;
    }

    public static Map<ExprSubselectNode, SubSelectStrategyHolder> startSubselects(
            EPServicesContext services,
            SubSelectStrategyCollection subSelectStrategyCollection,
            final AgentInstanceContext agentInstanceContext,
            List<StopCallback> stopCallbackList) {

        Map<ExprSubselectNode, SubSelectStrategyHolder> subselectStrategies = new HashMap<ExprSubselectNode, SubSelectStrategyHolder>();

        for (Map.Entry<ExprSubselectNode, SubSelectStrategyFactoryDesc> subselectEntry : subSelectStrategyCollection.getSubqueries().entrySet()) {

            ExprSubselectNode subselectNode = subselectEntry.getKey();
            SubSelectStrategyFactoryDesc factoryDesc = subselectEntry.getValue();
            SubSelectActivationHolder holder = factoryDesc.getSubSelectActivationHolder();

            // activate view
            ViewableActivationResult subselectActivationResult = holder.getActivator().activate(agentInstanceContext, true, false);
            stopCallbackList.add(subselectActivationResult.getStopCallback());

            // apply returning the strategy instance
            SubSelectStrategyRealization result = factoryDesc.getFactory().instantiate(services, subselectActivationResult.getViewable(), agentInstanceContext, stopCallbackList);

            // set aggregation
            final SubordTableLookupStrategy lookupStrategy = result.getStrategy();
            final SubselectAggregationPreprocessor aggregationPreprocessor = result.getSubselectAggregationPreprocessor();

            // determine strategy
            ExprSubselectStrategy strategy;
            if (aggregationPreprocessor != null) {
                strategy = new ExprSubselectStrategy() {
                    public Collection<EventBean> evaluateMatching(EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
                        Collection<EventBean> matchingEvents = lookupStrategy.lookup(eventsPerStream, exprEvaluatorContext);
                        aggregationPreprocessor.evaluate(eventsPerStream, matchingEvents, exprEvaluatorContext);
                        return CollectionUtil.SINGLE_NULL_ROW_EVENT_SET;
                    }
                };
            }
            else {
                strategy = new ExprSubselectStrategy() {
                    public Collection<EventBean> evaluateMatching(EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
                        return lookupStrategy.lookup(eventsPerStream, exprEvaluatorContext);
                    }
                };
            }

            SubSelectStrategyHolder instance = new SubSelectStrategyHolder(strategy,
                    result.getSubselectAggregationService(),
                    result.getPriorNodeStrategies(),
                    result.getPreviousNodeStrategies(),
                    result.getSubselectView(),
                    result.getPostLoad());
            subselectStrategies.put(subselectNode, instance);
        }

        return subselectStrategies;
    }

    private static Pair<EventTableFactory, SubordTableLookupStrategyFactory> determineSubqueryIndexFactory(ExprNode filterExpr,
                                                                                 EventType viewableEventType,
                                                                                 EventType[] outerEventTypes,
                                                                                 StreamTypeService subselectTypeService,
                                                                                 boolean fullTableScan,
                                                                                 boolean queryPlanLogging,
                                                                                 Set<String> optionalUniqueProps,
                                                                                 Annotation[] annotations,
                                                                                 int subqueryNum)
            throws ExprValidationException
    {
        Pair<EventTableFactory, SubordTableLookupStrategyFactory> result = determineSubqueryIndexInternalFactory(filterExpr, viewableEventType, outerEventTypes, subselectTypeService, fullTableScan, optionalUniqueProps);

        if (queryPlanLogging && queryPlanLog.isInfoEnabled()) {
            queryPlanLog.info("local index");
            queryPlanLog.info("strategy " + result.getSecond().toQueryPlan());
            queryPlanLog.info("table " + result.getFirst().toQueryPlan());

            QueryPlanIndexHook hook = QueryPlanIndexHookUtil.getHook(annotations);
            if (hook != null) {
                hook.subquery(new QueryPlanIndexDescSubquery(null, result.getFirst().getEventTableClass().getSimpleName(), subqueryNum));
            }
        }

        return result;
    }

    private static Pair<EventTableFactory, SubordTableLookupStrategyFactory> determineSubqueryIndexInternalFactory(ExprNode filterExpr,
                                                                                 EventType viewableEventType,
                                                                                 EventType[] outerEventTypes,
                                                                                 StreamTypeService subselectTypeService,
                                                                                 boolean fullTableScan,
                                                                                 Set<String> optionalUniqueProps)
            throws ExprValidationException
    {
        // No filter expression means full table scan
        if ((filterExpr == null) || fullTableScan)
        {
            UnindexedEventTableFactory table = new UnindexedEventTableFactory(0);
            SubordFullTableScanLookupStrategyFactory strategy = new SubordFullTableScanLookupStrategyFactory();
            return new Pair<EventTableFactory, SubordTableLookupStrategyFactory>(table, strategy);
        }

        // Build a list of streams and indexes
        SubordPropPlan joinPropDesc = QueryPlanIndexBuilder.getJoinProps(filterExpr, outerEventTypes.length, subselectTypeService.getEventTypes());
        Map<String, SubordPropHashKey> hashKeys = joinPropDesc.getHashProps();
        Map<String, SubordPropRangeKey> rangeKeys = joinPropDesc.getRangeProps();
        List<SubordPropHashKey> hashKeyList = new ArrayList<SubordPropHashKey>(hashKeys.values());
        List<SubordPropRangeKey> rangeKeyList = new ArrayList<SubordPropRangeKey>(rangeKeys.values());
        boolean unique = false;

        // If this is a unique-view and there are unique criteria, use these
        if (optionalUniqueProps != null && !optionalUniqueProps.isEmpty()) {
            boolean found = true;
            for (String uniqueProp : optionalUniqueProps) {
                if (!hashKeys.containsKey(uniqueProp)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                String[] hashKeysArray = hashKeys.keySet().toArray(new String[hashKeys.keySet().size()]);
                for (String hashKey : hashKeysArray) {
                    if (!optionalUniqueProps.contains(hashKey)) {
                        hashKeys.remove(hashKey);
                    }
                }
                hashKeyList = new ArrayList<SubordPropHashKey>(hashKeys.values());
                unique = true;
                rangeKeyList.clear();
                rangeKeys.clear();
            }
        }

        // build table (local table)
        EventTableFactory eventTable;
        CoercionDesc hashCoercionDesc;
        CoercionDesc rangeCoercionDesc;
        if (hashKeys.size() != 0 && rangeKeys.isEmpty())
        {
            String indexedProps[] = hashKeys.keySet().toArray(new String[hashKeys.keySet().size()]);
            hashCoercionDesc = CoercionUtil.getCoercionTypesHash(viewableEventType, indexedProps, hashKeyList);
            rangeCoercionDesc = new CoercionDesc(false, null);

            if (hashKeys.size() == 1) {
                if (!hashCoercionDesc.isCoerce()) {
                    eventTable = new PropertyIndexedEventTableSingleFactory(0, viewableEventType, indexedProps[0], unique, null);
                }
                else {
                    eventTable = new PropertyIndexedEventTableSingleCoerceAddFactory(0, viewableEventType, indexedProps[0], hashCoercionDesc.getCoercionTypes()[0]);
                }
            }
            else {
                if (!hashCoercionDesc.isCoerce())
                {
                    eventTable = new PropertyIndexedEventTableFactory(0, viewableEventType, indexedProps, unique, null);
                }
                else
                {
                    eventTable = new PropertyIndexedEventTableCoerceAddFactory(0, viewableEventType, indexedProps, hashCoercionDesc.getCoercionTypes());
                }
            }
        }
        else if (hashKeys.isEmpty() && rangeKeys.isEmpty())
        {
            eventTable = new UnindexedEventTableFactory(0);
            hashCoercionDesc = new CoercionDesc(false, null);
            rangeCoercionDesc = new CoercionDesc(false, null);
        }
        else if (hashKeys.isEmpty() && rangeKeys.size() == 1)
        {
            String indexedProp = rangeKeys.keySet().iterator().next();
            CoercionDesc coercionRangeTypes = CoercionUtil.getCoercionTypesRange(viewableEventType, rangeKeys, outerEventTypes);
            if (!coercionRangeTypes.isCoerce()) {
                eventTable = new PropertySortedEventTableFactory(0, viewableEventType, indexedProp);
            }
            else {
                eventTable = new PropertySortedEventTableCoercedFactory(0, viewableEventType, indexedProp, coercionRangeTypes.getCoercionTypes()[0]);
            }
            hashCoercionDesc = new CoercionDesc(false, null);
            rangeCoercionDesc = coercionRangeTypes;
        }
        else {
            String[] indexedKeyProps = hashKeys.keySet().toArray(new String[hashKeys.keySet().size()]);
            Class[] coercionKeyTypes = SubordPropUtil.getCoercionTypes(hashKeys.values());
            String[] indexedRangeProps = rangeKeys.keySet().toArray(new String[rangeKeys.keySet().size()]);
            CoercionDesc coercionRangeTypes = CoercionUtil.getCoercionTypesRange(viewableEventType, rangeKeys, outerEventTypes);
            eventTable = new PropertyCompositeEventTableFactory(0, viewableEventType, indexedKeyProps, coercionKeyTypes, indexedRangeProps, coercionRangeTypes.getCoercionTypes());
            hashCoercionDesc = CoercionUtil.getCoercionTypesHash(viewableEventType, indexedKeyProps, hashKeyList);
            rangeCoercionDesc = coercionRangeTypes;
        }

        SubordTableLookupStrategyFactory subqTableLookupStrategyFactory = SubordinateTableLookupStrategyUtil.getLookupStrategy(outerEventTypes,
                hashKeyList, hashCoercionDesc, rangeKeyList, rangeCoercionDesc, false);

        return new Pair<EventTableFactory, SubordTableLookupStrategyFactory>(eventTable, subqTableLookupStrategyFactory);
    }
}
