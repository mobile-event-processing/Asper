/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.plan;

import com.espertech.esper.epl.datetime.eval.ExprDotNodeFilterAnalyzerDesc;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.join.util.Eligibility;
import com.espertech.esper.epl.join.util.EligibilityDesc;
import com.espertech.esper.epl.join.util.EligibilityUtil;
import com.espertech.esper.epl.join.util.RangeFilterAnalyzer;

/**
 * Analyzes a filter expression and builds a query graph model.
 * The 'equals', 'and' 'between' and relational operators expressions in the filter expression are extracted
 * and placed in the query graph model as navigable relationships (by key and index
 * properties as well as ranges) between streams.
 */
public class FilterExprAnalyzer
{
    /**
     * Analyzes filter expression to build query graph model.
     * @param topNode - filter top node
     * @param queryGraph - model containing relationships between streams, to be written to
     */
    public static void analyze(ExprNode topNode, QueryGraph queryGraph, boolean isOuterJoin)
    {
        // Analyze relationships between streams. Relationships are properties in AND and EQUALS nodes of joins.
        if (topNode instanceof ExprEqualsNode)
        {
            ExprEqualsNode equalsNode = (ExprEqualsNode) topNode;
            if (!equalsNode.isNotEquals())
            {
                analyzeEqualsNode(equalsNode, queryGraph, isOuterJoin);
            }
        }
        else if (topNode instanceof ExprAndNode)
        {
            ExprAndNode andNode = (ExprAndNode) topNode;
            analyzeAndNode(andNode, queryGraph, isOuterJoin);
        }
        else if (topNode instanceof ExprBetweenNode) {
            ExprBetweenNode betweenNode = (ExprBetweenNode) topNode;
            analyzeBetweenNode(betweenNode, queryGraph);
        }
        else if (topNode instanceof ExprRelationalOpNode) {
            ExprRelationalOpNode relNode = (ExprRelationalOpNode) topNode;
            analyzeRelationalOpNode(relNode, queryGraph);
        }
        else if (topNode instanceof ExprDotNode && !isOuterJoin)
        {
            ExprDotNode dotNode = (ExprDotNode) topNode;
            analyzeDotNode(dotNode, queryGraph);
        }
    }

    private static void analyzeDotNode(ExprDotNode dotNode, QueryGraph queryGraph) {
        ExprDotNodeFilterAnalyzerDesc interval = dotNode.getExprDotNodeFilterAnalyzerDesc();
        if (interval == null) {
            return;
        }
        interval.apply(queryGraph);
    }

    private static void analyzeRelationalOpNode(ExprRelationalOpNode relNode, QueryGraph queryGraph) {
        if ( ((relNode.getChildNodes().get(0) instanceof ExprIdentNode)) &&
             ((relNode.getChildNodes().get(1) instanceof ExprIdentNode)))
        {
            ExprIdentNode identNodeLeft = (ExprIdentNode) relNode.getChildNodes().get(0);
            ExprIdentNode identNodeRight = (ExprIdentNode) relNode.getChildNodes().get(1);

            if (identNodeLeft.getStreamId() != identNodeRight.getStreamId())
            {
                queryGraph.addRelationalOpStrict(identNodeLeft.getStreamId(), identNodeLeft.getResolvedPropertyName(), identNodeLeft,
                        identNodeRight.getStreamId(), identNodeRight.getResolvedPropertyName(), identNodeRight, relNode.getRelationalOpEnum());
            }
            return;
        }

        int indexedStream = -1;
        String indexedProp = null;
        ExprNode exprNodeNoIdent = null;

        if (relNode.getChildNodes().get(0) instanceof ExprIdentNode) {
            ExprIdentNode identNode = (ExprIdentNode) relNode.getChildNodes().get(0);
            indexedStream = identNode.getStreamId();
            indexedProp = identNode.getResolvedPropertyName();
            exprNodeNoIdent = relNode.getChildNodes().get(1);
        }
        else if (relNode.getChildNodes().get(1) instanceof ExprIdentNode) {
            ExprIdentNode identNode = (ExprIdentNode) relNode.getChildNodes().get(1);
            indexedStream = identNode.getStreamId();
            indexedProp = identNode.getResolvedPropertyName();
            exprNodeNoIdent = relNode.getChildNodes().get(0);
        }
        if (indexedStream == -1) {
            return;     // require property of right/left side of equals
        }

        EligibilityDesc eligibility = EligibilityUtil.verifyInputStream(exprNodeNoIdent, indexedStream);
        if (!eligibility.getEligibility().isEligible()) {
            return;
        }

        queryGraph.addRelationalOp(indexedStream, indexedProp, eligibility.getStreamNum(), exprNodeNoIdent, relNode.getRelationalOpEnum());
    }

    private static void analyzeBetweenNode(ExprBetweenNode betweenNode, QueryGraph queryGraph) {
        RangeFilterAnalyzer.apply(betweenNode.getChildNodes().get(0), betweenNode.getChildNodes().get(1), betweenNode.getChildNodes().get(2),
                betweenNode.isLowEndpointIncluded(), betweenNode.isHighEndpointIncluded(), betweenNode.isNotBetween(),
                queryGraph);
    }

    /**
     * Analye EQUALS (=) node.
     * @param equalsNode - node to analyze
     * @param queryGraph - store relationships between stream properties
     */
    protected static void analyzeEqualsNode(ExprEqualsNode equalsNode, QueryGraph queryGraph, boolean isOuterJoin)
    {
        if ( (equalsNode.getChildNodes().get(0) instanceof ExprIdentNode) &&
             (equalsNode.getChildNodes().get(1) instanceof ExprIdentNode))
        {
            ExprIdentNode identNodeLeft = (ExprIdentNode) equalsNode.getChildNodes().get(0);
            ExprIdentNode identNodeRight = (ExprIdentNode) equalsNode.getChildNodes().get(1);

            if (identNodeLeft.getStreamId() != identNodeRight.getStreamId())
            {
                queryGraph.addStrictEquals(identNodeLeft.getStreamId(), identNodeLeft.getResolvedPropertyName(), identNodeLeft,
                        identNodeRight.getStreamId(), identNodeRight.getResolvedPropertyName(), identNodeRight);
            }

            return;
        }
        if (isOuterJoin) {      // outerjoins don't use constants or one-way expression-derived information to evaluate join
            return;
        }

        // handle constant-compare or transformation case
        int indexedStream = -1;
        String indexedProp = null;
        ExprNode exprNodeNoIdent = null;

        if (equalsNode.getChildNodes().get(0) instanceof ExprIdentNode) {
            ExprIdentNode identNode = (ExprIdentNode) equalsNode.getChildNodes().get(0);
            indexedStream = identNode.getStreamId();
            indexedProp = identNode.getResolvedPropertyName();
            exprNodeNoIdent = equalsNode.getChildNodes().get(1);
        }
        else if (equalsNode.getChildNodes().get(1) instanceof ExprIdentNode) {
            ExprIdentNode identNode = (ExprIdentNode) equalsNode.getChildNodes().get(1);
            indexedStream = identNode.getStreamId();
            indexedProp = identNode.getResolvedPropertyName();
            exprNodeNoIdent = equalsNode.getChildNodes().get(0);
        }
        if (indexedStream == -1) {
            return;     // require property of right/left side of equals
        }

        EligibilityDesc eligibility = EligibilityUtil.verifyInputStream(exprNodeNoIdent, indexedStream);
        if (!eligibility.getEligibility().isEligible()) {
            return;
        }

        if (eligibility.getEligibility() == Eligibility.REQUIRE_NONE) {
            queryGraph.addUnkeyedExpression(indexedStream, indexedProp, exprNodeNoIdent);
        }
        else {
            queryGraph.addKeyedExpression(indexedStream, indexedProp, eligibility.getStreamNum(), exprNodeNoIdent);
        }
    }

    /**
     * Analyze the AND-node.
     * @param andNode - node to analyze
     * @param queryGraph - to store relationships between stream properties
     */
    protected static void analyzeAndNode(ExprAndNode andNode, QueryGraph queryGraph, boolean isOuterJoin)
    {
        for (ExprNode childNode : andNode.getChildNodes())
        {
            analyze(childNode, queryGraph, isOuterJoin);
        }
    }
}
