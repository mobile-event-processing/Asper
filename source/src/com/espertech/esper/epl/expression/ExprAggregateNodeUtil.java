/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExprAggregateNodeUtil
{
    /**
     * Populates into the supplied list all aggregation functions within this expression, if any.
     * <p>Populates by going bottom-up such that nested aggregates appear first.
     * <p>I.e. sum(volume * sum(price)) would put first A then B into the list with A=sum(price) and B=sum(volume * A)
     * @param topNode is the expression node to deep inspect
     * @param aggregateNodes is a list of node to populate into
     */
    public static void getAggregatesBottomUp(ExprNode topNode, List<ExprAggregateNode> aggregateNodes)
    {
        // Map to hold per level of the node (1 to N depth) of expression node a list of aggregation expr nodes, if any
        // exist at that level
        TreeMap<Integer, List<ExprAggregateNode>> aggregateExprPerLevel = new TreeMap<Integer, List<ExprAggregateNode>>();

        if (topNode instanceof ExprNodeInnerNodeProvider) {
            ExprNodeInnerNodeProvider parameterized = (ExprNodeInnerNodeProvider) topNode;
            List<ExprNode> additionalNodes = parameterized.getAdditionalNodes();
            for (ExprNode additionalNode : additionalNodes) {
                recursiveAggregateEnter(additionalNode, aggregateExprPerLevel, 1);
            }
        }

        // Recursively enter all aggregate functions and their level into map
        recursiveAggregateEnter(topNode, aggregateExprPerLevel, 1);

        // Done if none found
        if (aggregateExprPerLevel.isEmpty())
        {
            return;
        }

        // From the deepest (highest) level to the lowest, add aggregates to list
        int deepLevel = aggregateExprPerLevel.lastKey();
        for (int i = deepLevel; i >= 1; i--)
        {
            List<ExprAggregateNode> list = aggregateExprPerLevel.get(i);
            if (list == null)
            {
                continue;
            }
            aggregateNodes.addAll(list);
        }
    }

    private static void recursiveAggregateEnter(ExprNode currentNode, Map<Integer, List<ExprAggregateNode>> aggregateExprPerLevel, int currentLevel)
    {
        // ask all child nodes to enter themselves
        for (ExprNode node : currentNode.getChildNodes())
        {
            // handle expression nodes in which have additional expression nodes as part of their parameterization and not as child nodes
            if (node instanceof ExprNodeInnerNodeProvider) {
                ExprNodeInnerNodeProvider parameterized = (ExprNodeInnerNodeProvider) node;
                List<ExprNode> additionalNodes = parameterized.getAdditionalNodes();
                for (ExprNode additionalNode : additionalNodes) {
                    recursiveAggregateEnter(additionalNode, aggregateExprPerLevel, currentLevel + 1);
                }
            }
            recursiveAggregateEnter(node, aggregateExprPerLevel, currentLevel + 1);
        }

        if (!(currentNode instanceof ExprAggregateNode))
        {
           return;
        }

        // Add myself to list, I'm an aggregate function
        List<ExprAggregateNode> aggregates = aggregateExprPerLevel.get(currentLevel);
        if (aggregates == null)
        {
            aggregates = new LinkedList<ExprAggregateNode>();
            aggregateExprPerLevel.put(currentLevel, aggregates);
        }
        aggregates.add((ExprAggregateNode)currentNode);
    }
}
