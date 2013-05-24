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

package com.espertech.esper.epl.parse;

import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprTimePeriod;
import com.espertech.esper.epl.expression.ExprTimePeriodImpl;
import com.espertech.esper.epl.generated.EsperEPL2Ast;
import com.espertech.esper.epl.spec.FilterSpecRaw;
import com.espertech.esper.epl.spec.OnTriggerSetAssignment;
import com.espertech.esper.epl.spec.PropertyEvalSpec;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ASTExprHelper {

    /**
     * For the given child, return the expression for that child and remove from node-to-expression map
     * @param child to ask for expression
     * @param astExprNodeMap map to remove node from
     * @return expression
     */
    public static ExprNode getRemoveExpr(Tree child, Map<Tree, ExprNode> astExprNodeMap)
    {
        ExprNode thisEvalNode = astExprNodeMap.get(child);
        astExprNodeMap.remove(child);
        return thisEvalNode;
    }

    public static String getExpressionText(CommonTokenStream tokenStream, Tree node) {
        int startIndex = node.getTokenStartIndex();
        int stopIndex = node.getTokenStopIndex();
        return tokenStream.toString(startIndex, stopIndex);
    }

    public static List<ExprNode> getRemoveAllChildExpr(Tree parent, Map<Tree, ExprNode> astExprNodeMap) {
        ArrayList<ExprNode> expressions = new ArrayList<ExprNode>(parent.getChildCount());
        for (int i = 0; i < parent.getChildCount(); i++)
        {
            expressions.add(astExprNodeMap.remove(parent.getChild(i)));
        }
        return expressions;
    }

    public static FilterSpecRaw walkFilterSpec(Tree node, PropertyEvalSpec propertyEvalSpec, Map<Tree, ExprNode> astExprNodeMap) {
        int count = 0;
        Tree startNode = node.getChild(0);
        if (startNode.getType() == EsperEPL2Ast.IDENT)
        {
            startNode = node.getChild(++count);
        }

        // Determine event type
        String eventName = startNode.getText();
        count++;

        // get property expression if any
        if ((node.getChildCount() > count) && (node.getChild(count).getType() == EsperEPL2Ast.EVENT_FILTER_PROPERTY_EXPR))
        {
            ++count;
        }

        List<ExprNode> exprNodes = getExprNodes(node, count, astExprNodeMap);

        return new FilterSpecRaw(eventName, exprNodes, propertyEvalSpec);
    }

    public static List<ExprNode> getExprNodes(Tree parentNode, int startIndex, Map<Tree, ExprNode> astExprNodeMap)
    {
        List<ExprNode> exprNodes = new LinkedList<ExprNode>();

        for (int i = startIndex; i < parentNode.getChildCount(); i++)
        {
        	Tree currentNode = parentNode.getChild(i);
            ExprNode exprNode = astExprNodeMap.get(currentNode);
            if (exprNode == null)
            {
                throw new IllegalStateException("Expression node for AST node not found for type " + currentNode.getType() + " and text " + currentNode.getText());
            }
            exprNodes.add(exprNode);
            astExprNodeMap.remove(currentNode);
        }
        return exprNodes;
    }

    public static ExprTimePeriod getTimePeriodExpr(Tree node, Map<Tree, ExprNode> astExprNodeMap) {

        ExprNode nodes[] = new ExprNode[8];
        for (int i = 0; i < node.getChildCount(); i++)
        {
            Tree child = node.getChild(i);
            if (child.getType() == EsperEPL2Ast.MILLISECOND_PART)
            {
                nodes[7] = astExprNodeMap.remove(child.getChild(0));
            }
            if (child.getType() == EsperEPL2Ast.SECOND_PART)
            {
                nodes[6] = astExprNodeMap.remove(child.getChild(0));
            }
            if (child.getType() == EsperEPL2Ast.MINUTE_PART)
            {
                nodes[5] = astExprNodeMap.remove(child.getChild(0));
            }
            if (child.getType() == EsperEPL2Ast.HOUR_PART)
            {
                nodes[4] = astExprNodeMap.remove(child.getChild(0));
            }
            if (child.getType() == EsperEPL2Ast.DAY_PART)
            {
                nodes[3] = astExprNodeMap.remove(child.getChild(0));
            }
            if (child.getType() == EsperEPL2Ast.WEEK_PART)
            {
                nodes[2] = astExprNodeMap.remove(child.getChild(0));
            }
            if (child.getType() == EsperEPL2Ast.MONTH_PART)
            {
                nodes[1] = astExprNodeMap.remove(child.getChild(0));
            }
            if (child.getType() == EsperEPL2Ast.YEAR_PART)
            {
                nodes[0] = astExprNodeMap.remove(child.getChild(0));
            }
        }
        ExprTimePeriod timeNode = new ExprTimePeriodImpl(nodes[0] != null, nodes[1]!= null, nodes[2]!= null, nodes[3]!= null, nodes[4]!= null, nodes[5]!= null, nodes[6]!= null, nodes[7]!= null);
        if (nodes[0] != null) timeNode.addChildNode(nodes[0]);
        if (nodes[1] != null) timeNode.addChildNode(nodes[1]);
        if (nodes[2] != null) timeNode.addChildNode(nodes[2]);
        if (nodes[3] != null) timeNode.addChildNode(nodes[3]);
        if (nodes[4] != null) timeNode.addChildNode(nodes[4]);
        if (nodes[5] != null) timeNode.addChildNode(nodes[5]);
        if (nodes[6] != null) timeNode.addChildNode(nodes[6]);
        if (nodes[7] != null) timeNode.addChildNode(nodes[7]);
        return timeNode;
    }

    /**
     * Returns the list of set-variable assignments under the given node.
     * @param node node to inspect
     * @param astExprNodeMap map of AST to expression
     * @return list of assignments
     */
    protected static List<OnTriggerSetAssignment> getOnTriggerSetAssignments(Tree node, Map<Tree, ExprNode> astExprNodeMap)
    {
        List<OnTriggerSetAssignment> assignments = new ArrayList<OnTriggerSetAssignment>();
        if (node == null) {
            return assignments;
        }

        for (int i = 0; i < node.getChildCount(); i++)
        {
            if (node.getChild(i).getType() != EsperEPL2Ast.ON_SET_EXPR_ITEM)
            {
                continue;
            }

            Tree childNode = node.getChild(i);
            String variableName = ASTFilterSpecHelper.getPropertyName(childNode.getChild(0), 0);
            ExprNode childEvalNode = astExprNodeMap.get(childNode.getChild(1));
            astExprNodeMap.remove(childNode.getChild(1));
            assignments.add(new OnTriggerSetAssignment(variableName, childEvalNode));
        }
        return assignments;
    }
}
