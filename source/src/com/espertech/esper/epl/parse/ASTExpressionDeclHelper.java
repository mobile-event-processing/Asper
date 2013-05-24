/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.parse;

import com.espertech.esper.antlr.ASTUtil;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.generated.EsperEPL2GrammarParser;
import com.espertech.esper.epl.spec.ExpressionDeclItem;
import com.espertech.esper.epl.spec.ExpressionScriptProvided;
import org.antlr.runtime.tree.Tree;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ASTExpressionDeclHelper
{
    public static Pair<ExpressionDeclItem, ExpressionScriptProvided> walkExpressionDecl(Tree node, List<String> scriptBodies, Map<Tree, ExprNode> astExprNodeMap) {

        String name = node.getChild(0).getText();
        if (node.getChild(1).getType() == EsperEPL2GrammarParser.EXPRESSIONDECL) {
            String expressionText = scriptBodies.remove(0);
            Tree parametersNode = ASTUtil.findFirstNode(node, EsperEPL2GrammarParser.EXPRCOL);
            List<String> parameters = Collections.emptyList();
            if (parametersNode != null) {
                parameters = ASTLibHelper.getIdentList(parametersNode);
            }
            Tree optionalReturnTypeNode = ASTUtil.findFirstNode(node, EsperEPL2GrammarParser.CLASS_IDENT);
            boolean optionalReturnTypeArray = ASTUtil.findFirstNode(node, EsperEPL2GrammarParser.LBRACK) != null;
            Tree optionalDialectNode = ASTUtil.findFirstNode(node, EsperEPL2GrammarParser.COLON);
            ExpressionScriptProvided script = new ExpressionScriptProvided(name, expressionText, parameters,
                    optionalReturnTypeNode != null ? optionalReturnTypeNode.getText() : null,
                    optionalReturnTypeArray,
                    optionalDialectNode != null ? optionalDialectNode.getChild(0).getText() : null);
            return new Pair<ExpressionDeclItem, ExpressionScriptProvided>(null, script);
        }

        Tree parentGoes = node.getChild(1);
        ExprNode inner = ASTExprHelper.getRemoveExpr(parentGoes.getChild(0), astExprNodeMap);

        List<String> parametersNames = Collections.emptyList();
        if (parentGoes.getChildCount() > 1) {
            if (parentGoes.getChild(1).getType() == EsperEPL2GrammarParser.GOES) {
                Tree paramParent = parentGoes.getChild(1);
                if (paramParent.getChild(0).getType() == EsperEPL2GrammarParser.EXPRCOL) {
                    parametersNames = ASTLibHelper.getIdentList(paramParent.getChild(0));
                }
                else {
                    parametersNames = Collections.singletonList(paramParent.getChild(0).getText());
                }
            }
        }

        ExpressionDeclItem expr = new ExpressionDeclItem(name, parametersNames, inner);
        return new Pair<ExpressionDeclItem, ExpressionScriptProvided>(expr, null);
    }
}
