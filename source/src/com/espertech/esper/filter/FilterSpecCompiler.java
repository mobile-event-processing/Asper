/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.filter;

import com.espertech.esper.client.ConfigurationInformation;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.core.context.util.ContextDescriptor;
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.core.start.EPStatementStartMethodHelperValidate;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.StreamTypeServiceImpl;
import com.espertech.esper.epl.core.ViewResourceDelegateUnverified;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.named.NamedWindowProcessor;
import com.espertech.esper.epl.property.PropertyEvaluator;
import com.espertech.esper.epl.property.PropertyEvaluatorFactory;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.property.IndexedProperty;
import com.espertech.esper.event.property.NestedProperty;
import com.espertech.esper.event.property.Property;
import com.espertech.esper.event.property.PropertyParser;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.type.RelationalOpEnum;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.util.SimpleNumberCoercer;
import com.espertech.esper.util.SimpleNumberCoercerFactory;
import com.espertech.esper.view.ViewFactoryChain;
import com.espertech.esper.view.ViewProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Helper to compile (validate and optimize) filter expressions as used in pattern and filter-based streams.
 */
public final class FilterSpecCompiler
{
    private static final Log log = LogFactory.getLog(FilterSpecCompiler.class);

    /**
     * Assigned for filter parameters that are based on boolean expression and not on
     * any particular property name.
     * <p>
     * Keeping this artificial property name is a simplification as optimized filter parameters
     * generally keep a property name.
     */
    public final static String PROPERTY_NAME_BOOLEAN_EXPRESSION = ".boolean_expression";

    /**
     * Factory method for compiling filter expressions into a filter specification
     * for use with filter service.
     * @param eventType is the filtered-out event type
     * @param eventTypeName is the name of the event type
     * @param filterExpessions is a list of filter expressions
     * @param taggedEventTypes is a map of stream names (tags) and event types available
     * @param arrayEventTypes is a map of name tags and event type per tag for repeat-expressions that generate an array of events
     * @param streamTypeService is used to set rules for resolving properties
     * @param optionalStreamName - the stream name, if provided
     * @param optionalPropertyEvalSpec - specification for evaluating properties
     * @param statementContext context for statement
     * @return compiled filter specification
     * @throws ExprValidationException if the expression or type validations failed
     */
    public static FilterSpecCompiled makeFilterSpec(EventType eventType,
                                                    String eventTypeName,
                                                    List<ExprNode> filterExpessions,
                                                    PropertyEvalSpec optionalPropertyEvalSpec,
                                                    LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes,
                                                    LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes,
                                                    StreamTypeService streamTypeService,
                                                    String optionalStreamName,
                                                    StatementContext statementContext,
                                                    Collection<Integer> assignedTypeNumberStack)
            throws ExprValidationException
    {
        // Validate all nodes, make sure each returns a boolean and types are good;
        // Also decompose all AND super nodes into individual expressions
        List<ExprNode> validatedNodes = validateAllowSubquery(filterExpessions, streamTypeService, statementContext, taggedEventTypes, arrayEventTypes);
        return build(validatedNodes, eventType, eventTypeName, optionalPropertyEvalSpec, taggedEventTypes, arrayEventTypes, streamTypeService, optionalStreamName, statementContext, assignedTypeNumberStack);
    }

    public static FilterSpecCompiled build(List<ExprNode> validatedNodes,
                                            EventType eventType,
                                            String eventTypeName,
                                            PropertyEvalSpec optionalPropertyEvalSpec,
                                            LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes,
                                            LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes,
                                            StreamTypeService streamTypeService,
                                            String optionalStreamName,
                                            StatementContext stmtContext,
                                            Collection<Integer> assignedTypeNumberStack) throws ExprValidationException {

        ExprEvaluatorContextStatement evaluatorContextStmt = new ExprEvaluatorContextStatement(stmtContext);

        return buildNoStmtCtx(validatedNodes, eventType, eventTypeName, optionalPropertyEvalSpec, taggedEventTypes, arrayEventTypes, streamTypeService,
                optionalStreamName, assignedTypeNumberStack,
                evaluatorContextStmt, stmtContext.getStatementId(), stmtContext.getStatementName(), stmtContext.getAnnotations(), stmtContext.getContextDescriptor(),
                stmtContext.getMethodResolutionService(), stmtContext.getEventAdapterService(), stmtContext.getTimeProvider(), stmtContext.getVariableService(), stmtContext.getConfigSnapshot());
    }

    public static FilterSpecCompiled buildNoStmtCtx(List<ExprNode> validatedNodes,
                                            EventType eventType,
                                            String eventTypeName,
                                            PropertyEvalSpec optionalPropertyEvalSpec,
                                            LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes,
                                            LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes,
                                            StreamTypeService streamTypeService,
                                            String optionalStreamName,
                                            Collection<Integer> assignedTypeNumberStack,
                                            ExprEvaluatorContext exprEvaluatorContext,
                                            String statementId,
                                            String statementName,
                                            Annotation[] annotations,
                                            ContextDescriptor contextDescriptor,
                                            MethodResolutionService methodResolutionService,
                                            EventAdapterService eventAdapterService,
                                            TimeProvider timeProvider,
                                            VariableService variableService,
                                            ConfigurationInformation configurationInformation) throws ExprValidationException {

        List<ExprNode> constituents = decomposeCheckAggregation(validatedNodes);

        // From the constituents make a filter specification
        FilterParamExprMap filterParamExprMap = new FilterParamExprMap();

        // Make filter parameter for each expression node, if it can be optimized

        for (ExprNode constituent : constituents)
        {
            FilterSpecParam param = makeFilterParam(constituent, taggedEventTypes, arrayEventTypes, exprEvaluatorContext, statementName);
            filterParamExprMap.put(constituent, param); // accepts null values as the expression may not be optimized
        }

        // Consolidate entries as possible, i.e. (a != 5 and a != 6) is (a not in (5,6))
        // Removes duplicates for same property and same filter operator for filter service index optimizations
        consolidate(filterParamExprMap, statementName);

        // Use all filter parameter and unassigned expressions
        List<FilterSpecParam> filterParams = new ArrayList<FilterSpecParam>();
        filterParams.addAll(filterParamExprMap.getFilterParams());
        List<ExprNode> remainingExprNodes = filterParamExprMap.getUnassignedExpressions();

        // any unoptimized expression nodes are put under one AND
        ExprNode exprNode = null;
        if (!remainingExprNodes.isEmpty())
        {
            if (remainingExprNodes.size() == 1)
            {
                exprNode = remainingExprNodes.get(0);
            }
            else
            {
                ExprAndNode andNode = ExprNodeUtility.connectExpressionsByLogicalAnd(remainingExprNodes);
                ExprValidationContext validationContext = new ExprValidationContext(streamTypeService, methodResolutionService, null, timeProvider, variableService, exprEvaluatorContext, eventAdapterService, statementName, statementId, annotations, contextDescriptor);
                andNode.validate(validationContext);
                exprNode = andNode;
            }
        }

        // if there are boolean expressions, add
        if (exprNode != null)
        {
            boolean hasSubselectFilterStream = determineSubselectFilterStream(exprNode);
            FilterSpecLookupable lookupable = new FilterSpecLookupable(PROPERTY_NAME_BOOLEAN_EXPRESSION, null, exprNode.getExprEvaluator().getType());
            FilterSpecParamExprNode param = new FilterSpecParamExprNode(lookupable, FilterOperator.BOOLEAN_EXPRESSION, exprNode, taggedEventTypes, arrayEventTypes, variableService, eventAdapterService, configurationInformation, statementName, hasSubselectFilterStream);
            filterParams.add(param);
        }

        PropertyEvaluator optionalPropertyEvaluator = null;
        if (optionalPropertyEvalSpec != null)
        {
            optionalPropertyEvaluator = PropertyEvaluatorFactory.makeEvaluator(optionalPropertyEvalSpec, eventType, optionalStreamName, eventAdapterService, methodResolutionService, timeProvider, variableService, streamTypeService.getEngineURIQualifier(), statementId, statementName, annotations, assignedTypeNumberStack, configurationInformation);
        }

        FilterSpecCompiled spec = new FilterSpecCompiled(eventType, eventTypeName, filterParams, optionalPropertyEvaluator);

        if (log.isDebugEnabled())
        {
            log.debug(".makeFilterSpec spec=" + spec);
        }

        return spec;
    }

    private static boolean determineSubselectFilterStream(ExprNode exprNode) {
        ExprNodeSubselectDeclaredDotVisitor visitor = new ExprNodeSubselectDeclaredDotVisitor();
        exprNode.accept(visitor);
        if (visitor.getSubselects().isEmpty()) {
            return false;
        }
        for (ExprSubselectNode subselectNode : visitor.getSubselects()) {
            if (subselectNode.isFilterStreamSubselect()) {
                return true;
            }
        }
        return false;
    }

    // remove duplicate propertyName + filterOperator items making a judgement to optimize or simply remove the optimized form
    private static void consolidate(List<FilterSpecParam> items, FilterParamExprMap filterParamExprMap, String statementName)
    {
        FilterOperator op = items.get(0).getFilterOperator();
        if (op == FilterOperator.NOT_EQUAL)
        {
            handleConsolidateNotEqual(items, filterParamExprMap, statementName);
        }
        else
        {
            // for all others we simple remove the second optimized form (filter param with same prop name and filter op)
            // and thus the boolean expression that started this is included
            for (int i = 1; i < items.size(); i++)
            {
                filterParamExprMap.removeValue(items.get(i));
            }
        }
    }

    /**
     * Validates expression nodes and returns a list of validated nodes.
     * @param exprNodes is the nodes to validate
     * @param streamTypeService is provding type information for each stream
     * @param taggedEventTypes pattern tagged types
     * @param arrayEventTypes @return list of validated expression nodes
     * @return expr nodes
     * @param statementContext context
     * @throws ExprValidationException for validation errors
     */
    public static List<ExprNode> validateAllowSubquery(List<ExprNode> exprNodes, StreamTypeService streamTypeService,
                                                       StatementContext statementContext,
                                                       LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes,
                                                       LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes)
            throws ExprValidationException
    {
        List<ExprNode> validatedNodes = new ArrayList<ExprNode>();

        ExprEvaluatorContextStatement evaluatorContextStmt = new ExprEvaluatorContextStatement(statementContext);
        ExprValidationContext validationContext = new ExprValidationContext(streamTypeService, statementContext.getMethodResolutionService(), null, statementContext.getTimeProvider(), statementContext.getVariableService(), evaluatorContextStmt, statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
        for (ExprNode node : exprNodes)
        {
            // Determine subselects
            ExprNodeSubselectDeclaredDotVisitor visitor = new ExprNodeSubselectDeclaredDotVisitor();
            node.accept(visitor);

            // Compile subselects
            if (!visitor.getSubselects().isEmpty()) {

                // The outer event type is the filtered-type itself
                int subselectStreamNumber = 2048;
                for (ExprSubselectNode subselect : visitor.getSubselects()) {
                    subselectStreamNumber++;
                    handleSubselectSelectClauses(subselectStreamNumber, statementContext, subselect,
                            streamTypeService.getEventTypes()[0], streamTypeService.getStreamNames()[0], streamTypeService.getStreamNames()[0],
                            taggedEventTypes, arrayEventTypes);
                }
            }

            ExprNode validated = ExprNodeUtility.getValidatedSubtree(node, validationContext);
            validatedNodes.add(validated);

            if ((validated.getExprEvaluator().getType() != Boolean.class) && ((validated.getExprEvaluator().getType() != boolean.class)))
            {
                throw new ExprValidationException("Filter expression not returning a boolean value: '" + validated.toExpressionString() + "'");
            }
        }

        return validatedNodes;
    }

    private static void handleSubselectSelectClauses(int subselectStreamNumber, StatementContext statementContext, ExprSubselectNode subselect, EventType outerEventType, String outerEventTypeName, String outerStreamName,
            LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes)
        throws ExprValidationException {

        StatementSpecCompiled statementSpec = subselect.getStatementSpecCompiled();
        StreamSpecCompiled filterStreamSpec = statementSpec.getStreamSpecs().get(0);

        ViewFactoryChain viewFactoryChain;
        String subselecteventTypeName = null;

        // construct view factory chain
        try {
            if (statementSpec.getStreamSpecs().get(0) instanceof FilterStreamSpecCompiled)
            {
                FilterStreamSpecCompiled filterStreamSpecCompiled = (FilterStreamSpecCompiled) statementSpec.getStreamSpecs().get(0);
                subselecteventTypeName = filterStreamSpecCompiled.getFilterSpec().getFilterForEventTypeName();

                // A child view is required to limit the stream
                if (filterStreamSpec.getViewSpecs().size() == 0)
                {
                    throw new ExprValidationException("Subqueries require one or more views to limit the stream, consider declaring a length or time window");
                }

                // Register filter, create view factories
                viewFactoryChain = statementContext.getViewService().createFactories(subselectStreamNumber, filterStreamSpecCompiled.getFilterSpec().getResultEventType(), filterStreamSpec.getViewSpecs(), filterStreamSpec.getOptions(), statementContext);
                subselect.setRawEventType(viewFactoryChain.getEventType());
            }
            else
            {
                NamedWindowConsumerStreamSpec namedSpec = (NamedWindowConsumerStreamSpec) statementSpec.getStreamSpecs().get(0);
                NamedWindowProcessor processor = statementContext.getNamedWindowService().getProcessor(namedSpec.getWindowName());
                viewFactoryChain = statementContext.getViewService().createFactories(0, processor.getNamedWindowType(), namedSpec.getViewSpecs(), namedSpec.getOptions(), statementContext);
                subselecteventTypeName = namedSpec.getWindowName();
            }
        }
        catch (ViewProcessingException ex) {
            throw new ExprValidationException("Error validating subexpression: " + ex.getMessage(), ex);
        }

        // the final event type
        EventType eventType = viewFactoryChain.getEventType();

        // determine a stream name unless one was supplied
        String subexpressionStreamName = filterStreamSpec.getOptionalStreamName();
        if (subexpressionStreamName == null)
        {
            subexpressionStreamName = "$subselect_" + subselectStreamNumber;
        }

        // Named windows don't allow data views
        if (filterStreamSpec instanceof NamedWindowConsumerStreamSpec)
        {
            EPStatementStartMethodHelperValidate.validateNoDataWindowOnNamedWindow(viewFactoryChain.getViewFactoryChain());
        }

        // Streams event types are the original stream types with the stream zero the subselect stream
        LinkedHashMap<String, Pair<EventType, String>> namesAndTypes = new LinkedHashMap<String, Pair<EventType, String>>();
        namesAndTypes.put(subexpressionStreamName, new Pair<EventType, String>(eventType, subselecteventTypeName));
        namesAndTypes.put(outerStreamName, new Pair<EventType, String>(outerEventType, outerEventTypeName));
        if (taggedEventTypes != null) {
            for (Map.Entry<String, Pair<EventType, String>> entry : taggedEventTypes.entrySet()) {
                namesAndTypes.put(entry.getKey(), new Pair<EventType, String>(entry.getValue().getFirst(), entry.getValue().getSecond()));
            }
        }
        if (arrayEventTypes != null) {
            for (Map.Entry<String, Pair<EventType, String>> entry : arrayEventTypes.entrySet()) {
                namesAndTypes.put(entry.getKey(), new Pair<EventType, String>(entry.getValue().getFirst(), entry.getValue().getSecond()));
            }
        }
        StreamTypeService subselectTypeService = new StreamTypeServiceImpl(namesAndTypes, statementContext.getEngineURI(), true, true);
        ViewResourceDelegateUnverified viewResourceDelegateSubselect = new ViewResourceDelegateUnverified();
        subselect.setFilterSubqueryStreamTypes(subselectTypeService);

        // Validate select expression
        SelectClauseSpecCompiled selectClauseSpec = subselect.getStatementSpecCompiled().getSelectClauseSpec();
        if (selectClauseSpec.getSelectExprList().size() > 0)
        {
            if (selectClauseSpec.getSelectExprList().size() > 1) {
                throw new ExprValidationException("Subquery multi-column select is not allowed in this context.");
            }

            SelectClauseElementCompiled element = selectClauseSpec.getSelectExprList().get(0);
            if (element instanceof SelectClauseExprCompiledSpec)
            {
                // validate
                SelectClauseExprCompiledSpec compiled = (SelectClauseExprCompiledSpec) element;
                ExprNode selectExpression = compiled.getSelectExpression();
                ExprEvaluatorContextStatement evaluatorContextStmt = new ExprEvaluatorContextStatement(statementContext);
                ExprValidationContext validationContext = new ExprValidationContext(subselectTypeService, statementContext.getMethodResolutionService(), viewResourceDelegateSubselect, statementContext.getSchedulingService(), statementContext.getVariableService(), evaluatorContextStmt, statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
                selectExpression = ExprNodeUtility.getValidatedSubtree(selectExpression, validationContext);
                subselect.setSelectClause(new ExprNode[] {selectExpression});
                subselect.setSelectAsNames(new String[] {compiled.getAssignedName()});

                // handle aggregation
                List<ExprAggregateNode> aggExprNodes = new LinkedList<ExprAggregateNode>();
                ExprAggregateNodeUtil.getAggregatesBottomUp(selectExpression, aggExprNodes);
                if (aggExprNodes.size() > 0)
                {
                    // Other stream properties, if there is aggregation, cannot be under aggregation.
                    for (ExprAggregateNode aggNode : aggExprNodes)
                    {
                        List<Pair<Integer, String>> propertiesNodesAggregated = ExprNodeUtility.getExpressionProperties(aggNode, true);
                        for (Pair<Integer, String> pair : propertiesNodesAggregated)
                        {
                            if (pair.getFirst() != 0)
                            {
                                throw new ExprValidationException("Subselect aggregation function cannot aggregate across correlated properties");
                            }
                        }
                    }

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
        }
    }

    public static List<ExprNode> decomposeCheckAggregation(List<ExprNode> validatedNodes) throws ExprValidationException
    {
        // Break a top-level AND into constituent expression nodes
        List<ExprNode> constituents = new ArrayList<ExprNode>();
        for (ExprNode validated : validatedNodes)
        {
            if (validated instanceof ExprAndNode)
            {
                recursiveAndConstituents(constituents, validated);
            }
            else
            {
                constituents.add(validated);
            }

            // Ensure there is no aggregation nodes
            List<ExprAggregateNode> aggregateExprNodes = new LinkedList<ExprAggregateNode>();
            ExprAggregateNodeUtil.getAggregatesBottomUp(validated, aggregateExprNodes);
            if (!aggregateExprNodes.isEmpty())
            {
                throw new ExprValidationException("Aggregation functions not allowed within filters");
            }
        }

        return constituents;
    }

    private static void consolidate(FilterParamExprMap filterParamExprMap, String statementName)
    {
        // consolidate or place in a boolean expression (by removing filter spec param from the map)
        // any filter parameter that feature the same property name and filter operator,
        // i.e. we are looking for "a!=5 and a!=6"  to transform to "a not in (5,6)" which can match faster
        // considering that "a not in (5,6) and a not in (7,8)" is "a not in (5, 6, 7, 8)" therefore
        // we need to consolidate until there is no more work to do
        Map<Pair<FilterSpecLookupable, FilterOperator>, List<FilterSpecParam>> mapOfParams = new HashMap<Pair<FilterSpecLookupable, FilterOperator>, List<FilterSpecParam>>();

        boolean haveConsolidated;
        do
        {
            haveConsolidated = false;
            mapOfParams.clear();

            // sort into buckets of propertyName + filterOperator combination
            for (FilterSpecParam currentParam : filterParamExprMap.getFilterParams())
            {
                FilterSpecLookupable lookupable = currentParam.getLookupable();
                FilterOperator op = currentParam.getFilterOperator();
                Pair<FilterSpecLookupable, FilterOperator> key = new Pair<FilterSpecLookupable, FilterOperator>(lookupable, op);

                List<FilterSpecParam> existingParam = mapOfParams.get(key);
                if (existingParam == null)
                {
                    existingParam = new ArrayList<FilterSpecParam>();
                    mapOfParams.put(key, existingParam);
                }
                existingParam.add(currentParam);
            }

            for (List<FilterSpecParam> entry : mapOfParams.values())
            {
                if (entry.size() > 1)
                {
                    haveConsolidated = true;
                    consolidate(entry, filterParamExprMap, statementName);
                }
            }
        }
        while(haveConsolidated);
    }

    // consolidate "val != 3 and val != 4 and val != 5"
    // to "val not in (3, 4, 5)"
    private static void handleConsolidateNotEqual(List<FilterSpecParam> parameters, FilterParamExprMap filterParamExprMap, String statementName)
    {
        List<FilterSpecParamInValue> values = new ArrayList<FilterSpecParamInValue>();

        ExprNode lastNotEqualsExprNode = null;
        for (FilterSpecParam param : parameters)
        {
            if (param instanceof FilterSpecParamConstant)
            {
                FilterSpecParamConstant constantParam = (FilterSpecParamConstant) param;
                Object constant = constantParam.getFilterConstant();
                values.add(new InSetOfValuesConstant(constant));
            }
            else if (param instanceof FilterSpecParamEventProp)
            {
                FilterSpecParamEventProp eventProp = (FilterSpecParamEventProp) param;
                values.add(new InSetOfValuesEventProp(eventProp.getResultEventAsName(), eventProp.getResultEventProperty(),
                        eventProp.isMustCoerce(), JavaClassHelper.getBoxedType(eventProp.getCoercionType())));
            }
            else if (param instanceof FilterSpecParamEventPropIndexed)
            {
                FilterSpecParamEventPropIndexed eventProp = (FilterSpecParamEventPropIndexed) param;
                values.add(new InSetOfValuesEventPropIndexed(eventProp.getResultEventAsName(), eventProp.getResultEventIndex(), eventProp.getResultEventProperty(),
                        eventProp.isMustCoerce(), JavaClassHelper.getBoxedType(eventProp.getCoercionType()), statementName));
            }
            else
            {
                throw new IllegalArgumentException("Unknown filter parameter:" + param.toString());
            }

            lastNotEqualsExprNode = filterParamExprMap.removeEntry(param);
        }

        FilterSpecParamIn param = new FilterSpecParamIn(parameters.get(0).getLookupable(), FilterOperator.NOT_IN_LIST_OF_VALUES, values);
        filterParamExprMap.put(lastNotEqualsExprNode, param);
    }

    /**
     * For a given expression determine if this is optimizable and create the filter parameter
     * representing the expression, or null if not optimizable.
     * @param constituent is the expression to look at
     * @param taggedEventTypes event types and their tags
     * @param arrayEventTypes @return filter parameter representing the expression, or null
     * @throws ExprValidationException if the expression is invalid
     * @return FilterSpecParam filter param
     */
    protected static FilterSpecParam makeFilterParam(ExprNode constituent, LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes, ExprEvaluatorContext exprEvaluatorContext, String statementName)
            throws ExprValidationException
    {
        // Is this expresson node a simple compare, i.e. a=5 or b<4; these can be indexed
        if ((constituent instanceof ExprEqualsNode) ||
            (constituent instanceof ExprRelationalOpNode))
        {
            FilterSpecParam param = handleEqualsAndRelOp(constituent, arrayEventTypes, exprEvaluatorContext, statementName);
            if (param != null)
            {
                return param;
            }
        }

        // Is this expresson node a simple compare, i.e. a=5 or b<4; these can be indexed
        if (constituent instanceof ExprInNode)
        {
            FilterSpecParam param = handleInSetNode((ExprInNode)constituent, arrayEventTypes, exprEvaluatorContext, statementName);
            if (param != null)
            {
                return param;
            }
        }

        if (constituent instanceof ExprBetweenNode)
        {
            FilterSpecParam param = handleRangeNode((ExprBetweenNode)constituent, arrayEventTypes, exprEvaluatorContext, statementName);
            if (param != null)
            {
                return param;
            }
        }

        if (constituent instanceof ExprPlugInSingleRowNode) {
            FilterSpecParam param = handlePlugInSingleRow((ExprPlugInSingleRowNode) constituent, exprEvaluatorContext, statementName);
            if (param != null)
            {
                return param;
            }
        }

        return null;
    }

    private static FilterSpecParam handlePlugInSingleRow(ExprPlugInSingleRowNode constituent, ExprEvaluatorContext exprEvaluatorContext, String statementName) {
        if (JavaClassHelper.getBoxedType(constituent.getExprEvaluator().getType()) != Boolean.class) {
            return null;
        }
        if (!constituent.getFilterLookupEligible()) {
            return null;
        }
        FilterSpecLookupable lookupable = constituent.getFilterLookupable();
        return new FilterSpecParamConstant(lookupable, FilterOperator.EQUAL, true);
    }

    private static FilterSpecParam handleRangeNode(ExprBetweenNode betweenNode, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes, ExprEvaluatorContext exprEvaluatorContext, String statementName)
    {
        ExprNode left = betweenNode.getChildNodes().get(0);
        if (left instanceof ExprFilterOptimizableNode)
        {
            ExprFilterOptimizableNode filterOptimizableNode = (ExprFilterOptimizableNode) left;
            FilterSpecLookupable lookupable = filterOptimizableNode.getFilterLookupable();
            FilterOperator op = FilterOperator.parseRangeOperator(betweenNode.isLowEndpointIncluded(), betweenNode.isHighEndpointIncluded(),
                    betweenNode.isNotBetween());

            FilterSpecParamRangeValue low = handleRangeNodeEndpoint(betweenNode.getChildNodes().get(1), arrayEventTypes, exprEvaluatorContext, statementName);
            FilterSpecParamRangeValue high = handleRangeNodeEndpoint(betweenNode.getChildNodes().get(2), arrayEventTypes, exprEvaluatorContext, statementName);

            if ((low != null) && (high != null))
            {
                return new FilterSpecParamRange(lookupable, op, low, high);
            }
        }
        return null;
    }

    private static FilterSpecParamRangeValue handleRangeNodeEndpoint(ExprNode endpoint, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes, ExprEvaluatorContext exprEvaluatorContext, String statementName)
    {
        // constant
        if (ExprNodeUtility.isConstantValueExpr(endpoint))
        {
            ExprConstantNode node = (ExprConstantNode) endpoint;
            Object value = node.evaluate(null, true, exprEvaluatorContext);
            if (value instanceof String) {
                return new RangeValueString((String)value);
            }
            else {
                return new RangeValueDouble(((Number)value).doubleValue());
            }
        }

        if (endpoint instanceof ExprContextPropertyNode) {
            ExprContextPropertyNode node = (ExprContextPropertyNode) endpoint;
            return new RangeValueContextProp(node.getGetter());
        }

        // or property
        if (endpoint instanceof ExprIdentNode)
        {
            ExprIdentNode identNodeInner = (ExprIdentNode) endpoint;
            if (identNodeInner.getStreamId() == 0)
            {
                return null;
            }

            if (arrayEventTypes != null && !arrayEventTypes.isEmpty() && arrayEventTypes.containsKey(identNodeInner.getResolvedStreamName()))
            {
                Pair<Integer, String> indexAndProp = getStreamIndex(identNodeInner.getResolvedPropertyName());
                return new RangeValueEventPropIndexed(identNodeInner.getResolvedStreamName(), indexAndProp.getFirst(), indexAndProp.getSecond(), statementName);
            }
            else
            {
                return new RangeValueEventProp(identNodeInner.getResolvedStreamName(), identNodeInner.getResolvedPropertyName());
            }
        }

        return null;
    }

    private static FilterSpecParam handleInSetNode(ExprInNode constituent, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes, ExprEvaluatorContext exprEvaluatorContext, String statementName)
            throws ExprValidationException
    {
        ExprNode left = constituent.getChildNodes().get(0);
        if (!(left instanceof ExprFilterOptimizableNode)) {
            return null;
        }

        ExprFilterOptimizableNode filterOptimizableNode = (ExprFilterOptimizableNode) left;
        FilterSpecLookupable lookupable = filterOptimizableNode.getFilterLookupable();
        FilterOperator op = FilterOperator.IN_LIST_OF_VALUES;
        if (constituent.isNotIn())
        {
            op = FilterOperator.NOT_IN_LIST_OF_VALUES;
        }

        int expectedNumberOfConstants = constituent.getChildNodes().size() - 1;
        List<FilterSpecParamInValue> listofValues = new ArrayList<FilterSpecParamInValue>();
        Iterator<ExprNode> it = constituent.getChildNodes().iterator();
        it.next();  // ignore the first node as it's the identifier
        while (it.hasNext())
        {
            ExprNode subNode = it.next();
            if (ExprNodeUtility.isConstantValueExpr(subNode))
            {
                ExprConstantNode constantNode = (ExprConstantNode) subNode;
                Object constant = constantNode.evaluate(null, true, exprEvaluatorContext);
                if (constant instanceof Collection) {
                    return null;
                }
                if (constant instanceof Map) {
                    return null;
                }
                if ((constant != null) && (constant.getClass().isArray())) {
                    for (int i = 0; i < Array.getLength(constant); i++) {
                        Object arrayElement = Array.get(constant, i);
                        Object arrayElementCoerced = handleConstantsCoercion(lookupable, arrayElement);
                        listofValues.add(new InSetOfValuesConstant(arrayElementCoerced));
                        if (i > 0) {
                            expectedNumberOfConstants++;
                        }
                    }
                }
                else {
                    constant = handleConstantsCoercion(lookupable, constant);
                    listofValues.add(new InSetOfValuesConstant(constant));
                }
            }
            if (subNode instanceof ExprContextPropertyNode)
            {
                ExprContextPropertyNode contextPropertyNode = (ExprContextPropertyNode) subNode;
                Class returnType = contextPropertyNode.getType();
                if (JavaClassHelper.isImplementsInterface(contextPropertyNode.getType(), Collection.class) ||
                    JavaClassHelper.isImplementsInterface(contextPropertyNode.getType(), Map.class)) {
                    return null;
                }
                if ((returnType != null) && (returnType.getClass().isArray())) {
                    return null;
                }
                SimpleNumberCoercer coercer = getNumberCoercer(left.getExprEvaluator().getType(), contextPropertyNode.getType(), lookupable.getExpression());
                listofValues.add(new InSetOfValuesContextProp(contextPropertyNode.getPropertyName(), contextPropertyNode.getGetter(), coercer));
            }
            if (subNode instanceof ExprIdentNode)
            {
                ExprIdentNode identNodeInner = (ExprIdentNode) subNode;
                if (identNodeInner.getStreamId() == 0)
                {
                    break; // for same event evals use the boolean expression, via count compare failing below
                }

                boolean isMustCoerce = false;
                Class numericCoercionType = JavaClassHelper.getBoxedType(lookupable.getReturnType());
                if (identNodeInner.getExprEvaluator().getType() != lookupable.getReturnType())
                {
                    if (JavaClassHelper.isNumeric(lookupable.getReturnType()))
                    {
                        if (!JavaClassHelper.canCoerce(identNodeInner.getExprEvaluator().getType(), lookupable.getReturnType()))
                        {
                            throwConversionError(identNodeInner.getExprEvaluator().getType(), lookupable.getReturnType(), lookupable.getExpression());
                        }
                        isMustCoerce = true;
                    }
                    else {
                        break;  // assumed not compatible
                    }
                }

                FilterSpecParamInValue inValue;
                String streamName = identNodeInner.getResolvedStreamName();
                if (arrayEventTypes != null && !arrayEventTypes.isEmpty() && arrayEventTypes.containsKey(streamName))
                {
                    Pair<Integer, String> indexAndProp = getStreamIndex(identNodeInner.getResolvedPropertyName());
                    inValue = new InSetOfValuesEventPropIndexed(identNodeInner.getResolvedStreamName(), indexAndProp.getFirst(),
                            indexAndProp.getSecond(), isMustCoerce, numericCoercionType, statementName);
                }
                else
                {
                    inValue = new InSetOfValuesEventProp(identNodeInner.getResolvedStreamName(), identNodeInner.getResolvedPropertyName(), isMustCoerce, numericCoercionType);
                }

                listofValues.add(inValue);
            }
        }

        // Fallback if not all values in the in-node can be resolved to properties or constants
        if (listofValues.size() == expectedNumberOfConstants)
        {
            return new FilterSpecParamIn(lookupable, op, listofValues);
        }
        return null;
    }

    private static FilterSpecParam handleEqualsAndRelOp(ExprNode constituent, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes, ExprEvaluatorContext exprEvaluatorContext, String statementName)
            throws ExprValidationException
    {
        FilterOperator op;
        if (constituent instanceof ExprEqualsNode)
        {
            ExprEqualsNode equalsNode = (ExprEqualsNode) constituent;
            if (!equalsNode.isIs()) {
                op = FilterOperator.EQUAL;
                if (equalsNode.isNotEquals())
                {
                    op = FilterOperator.NOT_EQUAL;
                }
            }
            else {
                op = FilterOperator.IS;
                if (equalsNode.isNotEquals())
                {
                    op = FilterOperator.IS_NOT;
                }
            }
        }
        else
        {
            ExprRelationalOpNode relNode = (ExprRelationalOpNode) constituent;
            if (relNode.getRelationalOpEnum() == RelationalOpEnum.GT)
            {
                op = FilterOperator.GREATER;
            }
            else if (relNode.getRelationalOpEnum() == RelationalOpEnum.LT)
            {
                op = FilterOperator.LESS;
            }
            else if (relNode.getRelationalOpEnum() == RelationalOpEnum.LE)
            {
                op = FilterOperator.LESS_OR_EQUAL;
            }
            else if (relNode.getRelationalOpEnum() == RelationalOpEnum.GE)
            {
                op = FilterOperator.GREATER_OR_EQUAL;
            }
            else
            {
                throw new IllegalStateException("Opertor '" + relNode.getRelationalOpEnum() + "' not mapped");
            }
        }

        ExprNode left = constituent.getChildNodes().get(0);
        ExprNode right = constituent.getChildNodes().get(1);

        // check identifier and constant combination
        if ((ExprNodeUtility.isConstantValueExpr(right)) && (left instanceof ExprFilterOptimizableNode))
        {
            ExprFilterOptimizableNode filterOptimizableNode = (ExprFilterOptimizableNode) left;
            if (filterOptimizableNode.getFilterLookupEligible()) {
                ExprConstantNode constantNode = (ExprConstantNode) right;
                FilterSpecLookupable lookupable = filterOptimizableNode.getFilterLookupable();
                Object constant = constantNode.evaluate(null, true, exprEvaluatorContext);
                constant = handleConstantsCoercion(lookupable, constant);
                return new FilterSpecParamConstant(lookupable, op, constant);
            }
        }
        if ((ExprNodeUtility.isConstantValueExpr(left)) && (right instanceof ExprFilterOptimizableNode))
        {
            ExprFilterOptimizableNode filterOptimizableNode = (ExprFilterOptimizableNode) right;
            if (filterOptimizableNode.getFilterLookupEligible()) {
                ExprConstantNode constantNode = (ExprConstantNode) left;
                FilterSpecLookupable lookupable = filterOptimizableNode.getFilterLookupable();
                Object constant = constantNode.evaluate(null, true, exprEvaluatorContext);
                constant = handleConstantsCoercion(lookupable, constant);
                FilterOperator opReversed = op.isComparisonOperator() ? op.reversedRelationalOp() : op;
                return new FilterSpecParamConstant(lookupable, opReversed, constant);
            }
        }
        // check identifier and expression containing other streams
        if ((left instanceof ExprIdentNode) && (right instanceof ExprIdentNode))
        {
            ExprIdentNode identNodeLeft = (ExprIdentNode) left;
            ExprIdentNode identNodeRight = (ExprIdentNode) right;

            if ((identNodeLeft.getStreamId() == 0) && (identNodeLeft.getFilterLookupEligible()) && (identNodeRight.getStreamId() != 0))
            {
                return handleProperty(op, identNodeLeft, identNodeRight, arrayEventTypes, statementName);
            }
            if ((identNodeRight.getStreamId() == 0) && (identNodeRight.getFilterLookupEligible()) && (identNodeLeft.getStreamId() != 0))
            {
                op = getReversedOperator(constituent, op); // reverse operators, as the expression is "stream1.prop xyz stream0.prop"
                return handleProperty(op, identNodeRight, identNodeLeft, arrayEventTypes, statementName);
            }
        }

        if ((left instanceof ExprFilterOptimizableNode) && (right instanceof ExprContextPropertyNode)) {
            ExprFilterOptimizableNode filterOptimizableNode = (ExprFilterOptimizableNode) left;
            ExprContextPropertyNode ctxNode = (ExprContextPropertyNode) right;
            FilterSpecLookupable lookupable = filterOptimizableNode.getFilterLookupable();
            if (filterOptimizableNode.getFilterLookupEligible()) {
                SimpleNumberCoercer numberCoercer = getNumberCoercer(lookupable.getReturnType(), ctxNode.getType(), lookupable.getExpression());
                return new FilterSpecParamContextProp(lookupable, op, ctxNode.getPropertyName(), ctxNode.getGetter(), numberCoercer);
            }
        }
        if ((left instanceof ExprContextPropertyNode) && (right instanceof ExprFilterOptimizableNode)) {
            ExprFilterOptimizableNode filterOptimizableNode = (ExprFilterOptimizableNode) right;
            ExprContextPropertyNode ctxNode = (ExprContextPropertyNode) left;
            FilterSpecLookupable lookupable = filterOptimizableNode.getFilterLookupable();
            if (filterOptimizableNode.getFilterLookupEligible()) {
                op = getReversedOperator(constituent, op); // reverse operators, as the expression is "stream1.prop xyz stream0.prop"
                SimpleNumberCoercer numberCoercer = getNumberCoercer(lookupable.getReturnType(), ctxNode.getType(), lookupable.getExpression());
                return new FilterSpecParamContextProp(lookupable, op, ctxNode.getPropertyName(), ctxNode.getGetter(), numberCoercer);
            }
        }
        return null;
    }

    private static FilterOperator getReversedOperator(ExprNode constituent, FilterOperator op) {
        if (!(constituent instanceof ExprRelationalOpNode)) {
            return op;
        }

        ExprRelationalOpNode relNode = (ExprRelationalOpNode) constituent;
        RelationalOpEnum relationalOpEnum = relNode.getRelationalOpEnum();

        if (relationalOpEnum == RelationalOpEnum.GT)
        {
            return FilterOperator.LESS;
        }
        else if (relationalOpEnum == RelationalOpEnum.LT)
        {
            return FilterOperator.GREATER;
        }
        else if (relationalOpEnum == RelationalOpEnum.LE)
        {
            return FilterOperator.GREATER_OR_EQUAL;
        }
        else if (relationalOpEnum == RelationalOpEnum.GE)
        {
            return FilterOperator.LESS_OR_EQUAL;
        }
        return op;
    }

    private static FilterSpecParam handleProperty(FilterOperator op, ExprIdentNode identNodeLeft, ExprIdentNode identNodeRight, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes, String statementName)
            throws ExprValidationException
    {
        String propertyName = identNodeLeft.getResolvedPropertyName();

        Class leftType = identNodeLeft.getExprEvaluator().getType();
        Class rightType = identNodeRight.getExprEvaluator().getType();

        SimpleNumberCoercer numberCoercer = getNumberCoercer(leftType, rightType, propertyName);
        boolean isMustCoerce = numberCoercer != null;
        Class numericCoercionType = JavaClassHelper.getBoxedType(leftType);

        String streamName = identNodeRight.getResolvedStreamName();
        if (arrayEventTypes != null && !arrayEventTypes.isEmpty() && arrayEventTypes.containsKey(streamName))
        {
            Pair<Integer, String> indexAndProp = getStreamIndex(identNodeRight.getResolvedPropertyName());
            return new FilterSpecParamEventPropIndexed(identNodeLeft.getFilterLookupable(), op, identNodeRight.getResolvedStreamName(), indexAndProp.getFirst(),
                    indexAndProp.getSecond(), isMustCoerce, numberCoercer, numericCoercionType, statementName);
        }
        return new FilterSpecParamEventProp(identNodeLeft.getFilterLookupable(), op, identNodeRight.getResolvedStreamName(), identNodeRight.getResolvedPropertyName(),
                isMustCoerce, numberCoercer, numericCoercionType, statementName);
    }

    private static SimpleNumberCoercer getNumberCoercer(Class leftType, Class rightType, String expression) throws ExprValidationException {
        Class numericCoercionType = JavaClassHelper.getBoxedType(leftType);
        if (rightType != leftType)
        {
            if (JavaClassHelper.isNumeric(rightType))
            {
                if (!JavaClassHelper.canCoerce(rightType, leftType))
                {
                    throwConversionError(rightType, leftType, expression);
                }
                return SimpleNumberCoercerFactory.getCoercer(rightType, numericCoercionType);
            }
        }
        return null;
    }

    private static Pair<Integer, String> getStreamIndex(String resolvedPropertyName)
    {
        Property property = PropertyParser.parse(resolvedPropertyName, false);
        if (!(property instanceof NestedProperty))
        {
            throw new IllegalStateException("Expected a nested property providing an index for array match '" + resolvedPropertyName + "'");
        }
        NestedProperty nested = ((NestedProperty) property);
        if (nested.getProperties().size() < 2)
        {
            throw new IllegalStateException("Expected a nested property name for array match '" + resolvedPropertyName + "', none found");
        }
        if (!(nested.getProperties().get(0) instanceof IndexedProperty))
        {
            throw new IllegalStateException("Expected an indexed property for array match '" + resolvedPropertyName + "', please provide an index");
        }
        int index = ((IndexedProperty) nested.getProperties().get(0)).getIndex();
        nested.getProperties().remove(0);
        StringWriter writer = new StringWriter();
        nested.toPropertyEPL(writer);
        return new Pair<Integer, String>(index, writer.toString());
    }

    private static void throwConversionError(Class fromType, Class toType, String propertyName)
            throws ExprValidationException
    {
        String text = "Implicit conversion from datatype '" +
                fromType.getSimpleName() +
                "' to '" +
                toType.getSimpleName() +
                "' for property '" +
                propertyName +
                "' is not allowed (strict filter type coercion)";
        throw new ExprValidationException(text);
    }

    // expressions automatically coerce to the most upwards type
    // filters require the same type
    private static Object handleConstantsCoercion(FilterSpecLookupable lookupable, Object constant)
            throws ExprValidationException
    {
        Class identNodeType = lookupable.getReturnType();
        if (!JavaClassHelper.isNumeric(identNodeType))
        {
            return constant;    // no coercion required, other type checking performed by expression this comes from
        }

        if (constant == null)  // null constant type
        {
            return null;
        }

        if (!JavaClassHelper.canCoerce(constant.getClass(), identNodeType))
        {
            throwConversionError(constant.getClass(), identNodeType, lookupable.getExpression());
        }

        Class identNodeTypeBoxed = JavaClassHelper.getBoxedType(identNodeType);
        return JavaClassHelper.coerceBoxed((Number) constant, identNodeTypeBoxed);
    }

    private static void recursiveAndConstituents(List<ExprNode> constituents, ExprNode exprNode)
    {
        for (ExprNode inner : exprNode.getChildNodes())
        {
            if (inner instanceof ExprAndNode)
            {
                recursiveAndConstituents(constituents, inner);
            }
            else
            {
                constituents.add(inner);
            }
        }
    }
}
