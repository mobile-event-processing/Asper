/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.agg.service;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.annotation.Hint;
import com.espertech.esper.client.annotation.HintEnum;
import com.espertech.esper.epl.agg.access.AggregationAccessor;
import com.espertech.esper.epl.agg.access.AggregationAccessorSlotPair;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.util.CollectionUtil;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Factory for aggregation service instances.
 * <p>
 * Consolidates aggregation nodes such that result futures point to a single instance and
 * no re-evaluation of the same result occurs.
 */
public class AggregationServiceFactoryFactory
{
    /**
     * Produces an aggregation service for use with match-recognice.
     *
     * @param numStreams number of streams
     * @param measureExprNodesPerStream measure nodes
     * @param exprEvaluatorContext context for expression evaluatiom
     * @return service
     */
    public static AggregationServiceMatchRecognizeFactoryDesc getServiceMatchRecognize(int numStreams,
                                                                                       Map<Integer, List<ExprAggregateNode>> measureExprNodesPerStream,
                                                                                       ExprEvaluatorContext exprEvaluatorContext)
    {
        Map<Integer, List<AggregationServiceAggExpressionDesc>> equivalencyListPerStream = new TreeMap<Integer, List<AggregationServiceAggExpressionDesc>>();

        for (Map.Entry<Integer, List<ExprAggregateNode>> entry : measureExprNodesPerStream.entrySet())
        {
            List<AggregationServiceAggExpressionDesc> equivalencyList = new ArrayList<AggregationServiceAggExpressionDesc>();
            equivalencyListPerStream.put(entry.getKey(), equivalencyList);
            for (ExprAggregateNode selectAggNode : entry.getValue())
            {
                addEquivalent(selectAggNode, equivalencyList);
            }
        }

        LinkedHashMap<Integer, AggregationMethodFactory[]> aggregatorsPerStream = new LinkedHashMap<Integer, AggregationMethodFactory[]>();
        Map<Integer, ExprEvaluator[]> evaluatorsPerStream = new HashMap<Integer, ExprEvaluator[]>();

        for (Map.Entry<Integer, List<AggregationServiceAggExpressionDesc>> equivalencyPerStream : equivalencyListPerStream.entrySet())
        {
            int index = 0;
            int stream = equivalencyPerStream.getKey();

            AggregationMethodFactory[] aggregators = new AggregationMethodFactory[equivalencyPerStream.getValue().size()];
            aggregatorsPerStream.put(stream, aggregators);

            ExprEvaluator[] evaluators = new ExprEvaluator[equivalencyPerStream.getValue().size()];
            evaluatorsPerStream.put(stream, evaluators);

            for (AggregationServiceAggExpressionDesc aggregation : equivalencyPerStream.getValue())
            {
                ExprAggregateNode aggregateNode = aggregation.getAggregationNode();
                if (aggregateNode.getChildNodes().size() > 1)
                {
                    evaluators[index] = getMultiNodeEvaluator(aggregateNode.getChildNodes(), exprEvaluatorContext);
                }
                else if (!aggregateNode.getChildNodes().isEmpty())
                {
                    // Use the evaluation node under the aggregation node to obtain the aggregation value
                    evaluators[index] = aggregateNode.getChildNodes().get(0).getExprEvaluator();
                }
                // For aggregation that doesn't evaluate any particular sub-expression, return null on evaluation
                else
                {
                    evaluators[index] = new ExprEvaluator() {
                        public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
                        {
                            return null;
                        }

                        public Class getType()
                        {
                            return null;
                        }
                        public Map<String, Object> getEventType() {
                            return null;
                        }
                    };
                }

                aggregators[index] = aggregateNode.getFactory();
                index++;
            }
        }

        // Assign a column number to each aggregation node. The regular aggregation goes first followed by access-aggregation.
        int columnNumber = 0;
        List<AggregationServiceAggExpressionDesc> allExpressions = new ArrayList<AggregationServiceAggExpressionDesc>();
        for (Map.Entry<Integer, List<AggregationServiceAggExpressionDesc>> equivalencyPerStream : equivalencyListPerStream.entrySet())
        {
            for (AggregationServiceAggExpressionDesc entry : equivalencyPerStream.getValue())
            {
                entry.setColumnNum(columnNumber++);
            }
            allExpressions.addAll(equivalencyPerStream.getValue());
        }

        AggregationServiceMatchRecognizeFactory factory = new AggregationServiceMatchRecognizeFactoryImpl(numStreams, aggregatorsPerStream, evaluatorsPerStream);
        return new AggregationServiceMatchRecognizeFactoryDesc(factory, allExpressions);
    }

    /**
     * Returns an instance to handle the aggregation required by the aggregation expression nodes, depending on
     * whether there are any group-by nodes.
     *
     *
     * @param selectAggregateExprNodes - aggregation nodes extracted out of the select expression
     * @param havingAggregateExprNodes - aggregation nodes extracted out of the select expression
     * @param orderByAggregateExprNodes - aggregation nodes extracted out of the select expression
     * @param hasGroupByClause - indicator on whethere there is group-by required, or group-all
     * @param exprEvaluatorContext context for expression evaluatiom
     * @param annotations - statement annotations
     * @param variableService - variable
     * @param isJoin - true for joins
     * @param whereClause the where-clause function if any
     * @param havingClause the having-clause function if any
     * @return instance for aggregation handling
     * @throws com.espertech.esper.epl.expression.ExprValidationException if validation fails
     */
    public static AggregationServiceFactoryDesc getService(List<ExprAggregateNode> selectAggregateExprNodes,
                                                           List<ExprAggregateNode> havingAggregateExprNodes,
                                                           List<ExprAggregateNode> orderByAggregateExprNodes,
                                                           boolean hasGroupByClause,
                                                           ExprEvaluatorContext exprEvaluatorContext,
                                                           Annotation[] annotations,
                                                           VariableService variableService,
                                                           boolean isJoin,
                                                           ExprNode whereClause,
                                                           ExprNode havingClause,
                                                           AggregationServiceFactoryService factoryService,
                                                           EventType[] typesPerStream)
            throws ExprValidationException
    {
        // No aggregates used, we do not need this service
        if ((selectAggregateExprNodes.isEmpty()) && (havingAggregateExprNodes.isEmpty()))
        {
            return new AggregationServiceFactoryDesc(factoryService.getNullAggregationService(), Collections.<AggregationServiceAggExpressionDesc>emptyList());
        }

        // Validate the absence of "prev" function in where-clause:
        // Since the "previous" function does not post remove stream results, disallow when used with aggregations.
        if ((whereClause != null) || (havingClause != null)) {
            ExprNodePreviousVisitorWParent visitor = new ExprNodePreviousVisitorWParent();
            if (whereClause != null) {
                whereClause.accept(visitor);
            }
            if (havingClause != null) {
                havingClause.accept(visitor);
            }
            if ((visitor.getPrevious() != null) && (!visitor.getPrevious().isEmpty())) {
                String funcname = visitor.getPrevious().get(0).getSecond().getPreviousType().toString().toLowerCase();
                throw new ExprValidationException("The '" + funcname + "' function may not occur in the where-clause or having-clause of a statement with aggregations as 'previous' does not provide remove stream data; Use the 'first','last','window' or 'count' aggregation functions instead");
            }
        }

        // Compile a map of aggregation nodes and equivalent-to aggregation nodes.
        // Equivalent-to functions are for example "select sum(a*b), 5*sum(a*b)".
        // Reducing the total number of aggregation functions.
        List<AggregationServiceAggExpressionDesc> aggregations = new ArrayList<AggregationServiceAggExpressionDesc>();
        for (ExprAggregateNode selectAggNode : selectAggregateExprNodes)
        {
            addEquivalent(selectAggNode, aggregations);
        }
        for (ExprAggregateNode havingAggNode : havingAggregateExprNodes)
        {
            addEquivalent(havingAggNode, aggregations);
        }
        for (ExprAggregateNode orderByAggNode : orderByAggregateExprNodes)
        {
            addEquivalent(orderByAggNode, aggregations);
        }

        // Assign a column number to each aggregation node. The regular aggregation goes first followed by access-aggregation.
        int columnNumber = 0;
        for (AggregationServiceAggExpressionDesc entry : aggregations)
        {
            if (entry.getFactory().getSpec(false) == null) {
                entry.setColumnNum(columnNumber++);
            }
        }
        for (AggregationServiceAggExpressionDesc entry : aggregations)
        {
            if (entry.getFactory().getSpec(false) != null) {
                entry.setColumnNum(columnNumber++);
            }
        }

        // handle regular aggregation (function provides value(s) to aggregate)
        List<AggregationMethodFactory> aggregators = new ArrayList<AggregationMethodFactory>();
        List<ExprEvaluator> evaluators = new ArrayList<ExprEvaluator>();

        // handle accessor aggregation (direct data window by-group access to properties)
        Map<Integer, Integer> streamSlots = new TreeMap<Integer, Integer>();
        List<AggregationAccessorSlotPair> accessorPairs = new ArrayList<AggregationAccessorSlotPair>();

        // Construct a list of evaluation node for the aggregation functions (regular agg).
        // For example "sum(2 * 3)" would make the sum an evaluation node.
        // Also determine all the streams that need direct access and compute a index (slot) for each (access agg).
        int currentSlot = 0;
        for (AggregationServiceAggExpressionDesc aggregation : aggregations)
        {
            ExprAggregateNode aggregateNode = aggregation.getAggregationNode();
            if (aggregateNode.getFactory().getSpec(false) == null) {
                ExprEvaluator evaluator;
                if (aggregateNode.getChildNodes().size() > 1)
                {
                    evaluator = getMultiNodeEvaluator(aggregateNode.getChildNodes(), exprEvaluatorContext);
                }
                else if (!aggregateNode.getChildNodes().isEmpty())
                {
                    if (aggregateNode.getChildNodes().get(0) instanceof ExprNumberSetWildcardMarker) {
                        final Class returnType = typesPerStream != null && typesPerStream.length > 0 ? typesPerStream[0].getUnderlyingType() : null;
                        if (isJoin || returnType == null) {
                            throw new ExprValidationException("Invalid use of wildcard (*) for stream selection in a join or an empty from-clause, please use the stream-alias syntax to select a specific stream instead");
                        }
                        evaluator = new ExprEvaluator() {
                            public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
                                return eventsPerStream[0].getUnderlying();
                            }
                            public Class getType() {
                                return returnType;
                            }
                            public Map<String, Object> getEventType() {
                                return null;
                            }
                        };
                    }
                    else {
                        // Use the evaluation node under the aggregation node to obtain the aggregation value
                        evaluator = aggregateNode.getChildNodes().get(0).getExprEvaluator();
                    }
                }
                // For aggregation that doesn't evaluate any particular sub-expression, return null on evaluation
                else
                {
                    evaluator = new ExprEvaluator() {
                        public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
                        {
                            return null;
                        }
                        public Class getType()
                        {
                            return null;
                        }
                        public Map<String, Object> getEventType() {
                            return null;
                        }
                    };
                }
                AggregationMethodFactory aggregator = aggregateNode.getFactory();

                evaluators.add(evaluator);
                aggregators.add(aggregator);
            }
            else {
                AggregationSpec spec = aggregateNode.getFactory().getSpec(false);
                AggregationAccessor accessor = aggregateNode.getFactory().getAccessor();

                Integer slot = streamSlots.get(spec.getStreamNum());
                if (slot == null) {
                    streamSlots.put(spec.getStreamNum(), currentSlot);
                    slot = currentSlot++;
                }

                accessorPairs.add(new AggregationAccessorSlotPair(slot, accessor));
            }
        }

        // handle no group-by clause cases
        ExprEvaluator[] evaluatorsArr = evaluators.toArray(new ExprEvaluator[evaluators.size()]);
        AggregationMethodFactory[] aggregatorsArr = aggregators.toArray(new AggregationMethodFactory[aggregators.size()]);
        AggregationAccessorSlotPair[] pairs = accessorPairs.toArray(new AggregationAccessorSlotPair[accessorPairs.size()]);
        int[] accessedStreams = CollectionUtil.intArray(streamSlots.keySet());

        AggregationServiceFactory serviceFactory;

        // Handle without a group-by clause: we group all into the same pot
        if (!hasGroupByClause) {
            if ((evaluatorsArr.length > 0) && (accessorPairs.isEmpty())) {
                serviceFactory = factoryService.getNoGroupNoAccess(evaluatorsArr, aggregatorsArr);
            }
            else if ((evaluatorsArr.length == 0) && (!accessorPairs.isEmpty())) {
                serviceFactory = factoryService.getNoGroupAccessOnly(pairs, accessedStreams, isJoin);
            }
            else {
                serviceFactory = factoryService.getNoGroupAccessMixed(evaluatorsArr, aggregatorsArr, pairs, accessedStreams, isJoin);
            }
        }
        else {
            boolean hasNoReclaim = HintEnum.DISABLE_RECLAIM_GROUP.getHint(annotations) != null;
            Hint reclaimGroupAged = HintEnum.RECLAIM_GROUP_AGED.getHint(annotations);
            Hint reclaimGroupFrequency = HintEnum.RECLAIM_GROUP_AGED.getHint(annotations);
            if (hasNoReclaim)
            {
                if ((evaluatorsArr.length > 0) && (accessorPairs.isEmpty())) {
                    serviceFactory = factoryService.getGroupedNoReclaimNoAccess(evaluatorsArr, aggregatorsArr);
                }
                else if ((evaluatorsArr.length == 0) && (!accessorPairs.isEmpty())) {
                    serviceFactory = factoryService.getGroupNoReclaimAccessOnly(pairs, accessedStreams, isJoin);
                }
                else {
                    serviceFactory = factoryService.getGroupNoReclaimMixed(evaluatorsArr, aggregatorsArr, pairs, accessedStreams, isJoin);
                }
            }
            else if (reclaimGroupAged != null)
            {
                serviceFactory = factoryService.getGroupReclaimAged(evaluatorsArr, aggregatorsArr, reclaimGroupAged, reclaimGroupFrequency, variableService, pairs, accessedStreams, isJoin);
            }
            else
            {
                if ((evaluatorsArr.length > 0) && (accessorPairs.isEmpty())) {
                    serviceFactory = factoryService.getGroupReclaimNoAccess(evaluatorsArr, aggregatorsArr, pairs, accessedStreams, isJoin);
                }
                else {
                    serviceFactory = factoryService.getGroupReclaimMixable(evaluatorsArr, aggregatorsArr, pairs, accessedStreams, isJoin);
                }
            }
        }

        return new AggregationServiceFactoryDesc(serviceFactory, aggregations);
    }

    private static ExprEvaluator getMultiNodeEvaluator(List<ExprNode> childNodes, ExprEvaluatorContext exprEvaluatorContext)
    {
        final int size = childNodes.size();
        final List<ExprNode> exprNodes = childNodes;
        final Object[] prototype = new Object[size];

        // determine constant nodes
        int count = 0;
        for (ExprNode node : exprNodes)
        {
            if (node.isConstantResult())
            {
                prototype[count] = node.getExprEvaluator().evaluate(null, true, exprEvaluatorContext);
            }
            count++;
        }

        return new ExprEvaluator() {
            public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
            {
                int count = 0;
                for (ExprNode node : exprNodes)
                {
                    prototype[count] = node.getExprEvaluator().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
                    count++;
                }
                return prototype;
            }

            public Class getType()
            {
                return Object[].class;
            }
            public Map<String, Object> getEventType() {
                return null;
            }
        };
    }

    private static void addEquivalent(ExprAggregateNode aggNodeToAdd, List<AggregationServiceAggExpressionDesc> equivalencyList)
    {
        // Check any same aggregation nodes among all aggregation clauses
        boolean foundEquivalent = false;
        for (AggregationServiceAggExpressionDesc existing : equivalencyList)
        {
            ExprAggregateNode aggNode = existing.getAggregationNode();
            if (ExprNodeUtility.deepEquals(aggNode, aggNodeToAdd))
            {
                existing.addEquivalent(aggNodeToAdd);
                foundEquivalent = true;
                break;
            }
        }

        if (!foundEquivalent)
        {
            equivalencyList.add(new AggregationServiceAggExpressionDesc(aggNodeToAdd, aggNodeToAdd.getFactory()));
        }
    }
}
