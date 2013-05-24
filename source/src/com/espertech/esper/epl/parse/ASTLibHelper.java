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

import com.espertech.esper.epl.enummethod.dot.ExprLambdaGoesNode;
import com.espertech.esper.epl.expression.ExprChainedSpec;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.generated.EsperEPL2Ast;
import org.antlr.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ASTLibHelper {
    public static ExprChainedSpec getLibFunctionChainSpec(Tree libFunctionParent, Map<Tree, ExprNode> astExprNodeMap) {
        if (libFunctionParent.getType() != EsperEPL2Ast.LIB_FUNCTION) {
            throw new IllegalArgumentException("Not a LIB_FUNCTION parent");
        }

        int count = 0;
        if (libFunctionParent.getChild(0).getType() == EsperEPL2Ast.CLASS_IDENT) {
            count++;
        }

        String methodName = ASTConstantHelper.removeTicks(libFunctionParent.getChild(count).getText());
        count++;

        List<ExprNode> parameters = getExprNodesLibFunc(count, libFunctionParent, astExprNodeMap);
        boolean isProperty = libFunctionParent.getChildCount() > 0 && libFunctionParent.getChild(libFunctionParent.getChildCount() - 1).getType() != EsperEPL2Ast.LPAREN;
        return new ExprChainedSpec(methodName, parameters, isProperty);
    }

    public static List<ExprNode> getExprNodesLibFunc(int start, Tree parent, Map<Tree, ExprNode> astExprNodeMap) {
        List<ExprNode> parameters = new ArrayList<ExprNode>();
        int exprNum = start;
        while (exprNum < parent.getChildCount()) {
            if (parent.getChild(exprNum).getType() == EsperEPL2Ast.GOES) {
                ExprLambdaGoesNode goes = getLambdaGoes(parent.getChild(exprNum));
                ExprNode lambdaExpr = astExprNodeMap.remove(parent.getChild(++exprNum));
                goes.addChildNode(lambdaExpr);
                parameters.add(goes);
            }
            else {
                ExprNode parameter = astExprNodeMap.remove(parent.getChild(exprNum));
                if (parameter != null) {
                    parameters.add(parameter);
                }
            }
            exprNum++;
        }
        return parameters;
    }

    private static ExprLambdaGoesNode getLambdaGoes(Tree child) {
        List<String> parameters = new ArrayList<String>();
        if (child.getChild(0).getType() == EsperEPL2Ast.IDENT) {
            parameters.add(child.getChild(0).getText());
        }
        else {
            parameters = getIdentList(child.getChild(0));
        }
        return new ExprLambdaGoesNode(parameters);
    }

    public static List<String> getIdentList(Tree node) {
        return getTextList(node, EsperEPL2Ast.IDENT);
    }

    public static List<String> getTextList(Tree node, int nodeType) {
        List<String> columsList = new ArrayList<String>();
        for (int i = 0; i < node.getChildCount(); i++)
        {
            if (node.getChild(i).getType() == nodeType)
            {
                columsList.add(node.getChild(i).getText());
            }
        }
        return columsList;
    }

    public static List<String> getTextListChild(Tree node, int nodeType) {
        List<String> columsList = new ArrayList<String>();
        for (int i = 0; i < node.getChildCount(); i++)
        {
            if (node.getChild(i).getType() == nodeType)
            {
                columsList.add(node.getChild(i).getChild(0).getText());
            }
        }
        return columsList;
    }
}
