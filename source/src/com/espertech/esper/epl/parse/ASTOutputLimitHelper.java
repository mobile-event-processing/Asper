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
import com.espertech.esper.epl.core.StreamTypeServiceImpl;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.generated.EsperEPL2GrammarParser;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.type.IntValue;
import org.antlr.runtime.tree.Tree;

import java.util.List;
import java.util.Map;

/**
 * Builds an output limit spec from an output limit AST node.
 */
public class ASTOutputLimitHelper
{
    /**
     * Build an output limit spec from the AST node supplied.
     *
     * @param node - parse node
     * @param astExprNodeMap is the map of current AST tree nodes to their respective expression root node
     * @param engineURI the engine uri
     * @param timeProvider provides time
     * @param variableService provides variable resolution
     * @param exprEvaluatorContext context for expression evaluatiom
     * @return output limit spec
     */
    public static OutputLimitSpec buildOutputLimitSpec(Tree node, Map<Tree, ExprNode> astExprNodeMap, VariableService variableService, String engineURI, TimeProvider timeProvider, ExprEvaluatorContext exprEvaluatorContext)
    {
        int count = 0;
        Tree child = node.getChild(count);

        // parse type
        OutputLimitLimitType displayLimit = OutputLimitLimitType.DEFAULT;
        if (child.getType() == EsperEPL2GrammarParser.FIRST)
        {
            displayLimit = OutputLimitLimitType.FIRST;
            child = node.getChild(++count);
        }
        else if (child.getType() == EsperEPL2GrammarParser.LAST)
        {
            displayLimit = OutputLimitLimitType.LAST;
            child = node.getChild(++count);
        }
        else if (child.getType() == EsperEPL2GrammarParser.SNAPSHOT)
        {
            displayLimit = OutputLimitLimitType.SNAPSHOT;
            child = node.getChild(++count);
        }
        else if (child.getType() == EsperEPL2GrammarParser.ALL)
        {
            displayLimit = OutputLimitLimitType.ALL;
            child = node.getChild(++count);
        }

        // next is a variable, or time period, or number
        String variableName = null;
        double rate = -1;
        ExprNode whenExpression = null;
        List<ExprNode> crontabScheduleSpec = null;
        List<OnTriggerSetAssignment> thenExpressions = null;
        ExprTimePeriod timePeriodExpr = null;

        ExprNode andAfterTerminateExpr = null;
        List<OnTriggerSetAssignment> andAfterTerminateSetExpressions = null;
        if (node.getType() == EsperEPL2GrammarParser.TERM_LIMIT_EXPR) {
            Tree terminated = ASTUtil.findFirstNode(node, EsperEPL2GrammarParser.TERMINATED);
            if (terminated.getChildCount() != 0) {
                andAfterTerminateExpr = astExprNodeMap.remove(terminated.getChild(0));
                Tree setExprNode = ASTUtil.findFirstNode(terminated, EsperEPL2GrammarParser.ON_SET_EXPR);
                if (setExprNode != null) {
                    andAfterTerminateSetExpressions = ASTExprHelper.getOnTriggerSetAssignments(setExprNode, astExprNodeMap);
                }
            }

            // no action to walk any other expression
        }
        else if (node.getType() == EsperEPL2GrammarParser.WHEN_LIMIT_EXPR)
        {
            Tree expressionNode = node.getChild(count);
            whenExpression = astExprNodeMap.remove(expressionNode);
            if (node.getChildCount() > count+1)
            {
                Tree setExpr = ASTUtil.findFirstNode(node, EsperEPL2GrammarParser.ON_SET_EXPR);
                thenExpressions = ASTExprHelper.getOnTriggerSetAssignments(setExpr, astExprNodeMap);
            }
        }
        else if (node.getType() == EsperEPL2GrammarParser.CRONTAB_LIMIT_EXPR)
        {
            Tree parent = node.getChild(0);
            if (parent.getType() != EsperEPL2GrammarParser.CRONTAB_LIMIT_EXPR_PARAM)
            {
                parent = node.getChild(1);
            }

            crontabScheduleSpec = ASTExprHelper.getRemoveAllChildExpr(parent, astExprNodeMap);
        }
        else if (node.getType() == EsperEPL2GrammarParser.AFTER_LIMIT_EXPR)
        {
            // no action here, since AFTER time may occur in all
        }
        else
        {
            if (child.getType() == EsperEPL2GrammarParser.IDENT)
            {
                variableName = child.getText();
            }
            else if (child.getType() == EsperEPL2GrammarParser.TIME_PERIOD)
            {
                timePeriodExpr = (ExprTimePeriod) astExprNodeMap.remove(child);
            }
            else
            {
                rate = Double.parseDouble(child.getText());
            }
        }

        // get the AFTER time period
        ExprTimePeriod afterTimePeriodExpr = null;
        Integer afterNumberOfEvents = null;
        for (int i = 0; i < node.getChildCount(); i++)
        {
            if (node.getChild(i).getType() == EsperEPL2GrammarParser.AFTER)
            {
                ExprNode expression = astExprNodeMap.remove(node.getChild(i).getChild(0));
                if (expression != null)
                {
                    afterTimePeriodExpr = (ExprTimePeriod) expression;
                }
                else
                {
                    Object constant = ASTConstantHelper.parse(node.getChild(i).getChild(0));
                    afterNumberOfEvents = ((Number) constant).intValue();
                }
            }
        }

        Tree lastNode = node.getChild(node.getChildCount() - 1);
        boolean andAfterTerminate = (lastNode.getType() == EsperEPL2GrammarParser.TERMINATED) && andAfterTerminateExpr == null && node.getType() != EsperEPL2GrammarParser.TERM_LIMIT_EXPR;
        if (andAfterTerminate && lastNode.getChildCount() > 0) {
            andAfterTerminateExpr = astExprNodeMap.remove(lastNode.getChild(0));
            Tree setExprNode = ASTUtil.findFirstNode(lastNode, EsperEPL2GrammarParser.ON_SET_EXPR);
            if (setExprNode != null) {
                andAfterTerminateSetExpressions = ASTExprHelper.getOnTriggerSetAssignments(setExprNode, astExprNodeMap);
            }
        }

        switch (node.getType())
        {
            case EsperEPL2GrammarParser.EVENT_LIMIT_EXPR:
                return new OutputLimitSpec(rate, variableName, OutputLimitRateType.EVENTS, displayLimit, null, null, null, null, afterTimePeriodExpr, afterNumberOfEvents, andAfterTerminate, andAfterTerminateExpr, andAfterTerminateSetExpressions);
            case EsperEPL2GrammarParser.TIMEPERIOD_LIMIT_EXPR:
                return new OutputLimitSpec(null, null, OutputLimitRateType.TIME_PERIOD, displayLimit, null, null, null, timePeriodExpr, afterTimePeriodExpr, afterNumberOfEvents, andAfterTerminate, andAfterTerminateExpr, andAfterTerminateSetExpressions);
            case EsperEPL2GrammarParser.CRONTAB_LIMIT_EXPR:
                return new OutputLimitSpec(null, null, OutputLimitRateType.CRONTAB, displayLimit, null, null, crontabScheduleSpec, null, afterTimePeriodExpr, afterNumberOfEvents, andAfterTerminate, andAfterTerminateExpr, andAfterTerminateSetExpressions);
            case EsperEPL2GrammarParser.WHEN_LIMIT_EXPR:
                return new OutputLimitSpec(null, null, OutputLimitRateType.WHEN_EXPRESSION, displayLimit, whenExpression, thenExpressions, null, null, afterTimePeriodExpr, afterNumberOfEvents, andAfterTerminate, andAfterTerminateExpr, andAfterTerminateSetExpressions);
            case EsperEPL2GrammarParser.AFTER_LIMIT_EXPR:
                return new OutputLimitSpec(null, null, OutputLimitRateType.AFTER, displayLimit, null, null, null, null, afterTimePeriodExpr, afterNumberOfEvents, andAfterTerminate, andAfterTerminateExpr, andAfterTerminateSetExpressions);
            case EsperEPL2GrammarParser.TERM_LIMIT_EXPR:
                return new OutputLimitSpec(null, null, OutputLimitRateType.TERM, displayLimit, null, null, null, null, afterTimePeriodExpr, afterNumberOfEvents, andAfterTerminate, andAfterTerminateExpr, andAfterTerminateSetExpressions);
            default:
                throw new IllegalArgumentException("Node type " + node.getType() + " not a recognized output limit type");
		 }
	}

    /**
     * Builds a row limit specification.
     * @param node to interrogate
     * @return row limit spec
     */
    public static RowLimitSpec buildRowLimitSpec(Tree node)
    {
        Object numRows;
        Object offset;

        if (node.getChildCount() == 1)
        {
            numRows = parseNumOrVariableIdent(node.getChild(0));
            offset = null;
        }
        else
        {
            if (node.getChild(node.getChildCount()- 1).getType() == EsperEPL2GrammarParser.COMMA)
            {
                offset = parseNumOrVariableIdent(node.getChild(0));
                numRows = parseNumOrVariableIdent(node.getChild(1));
            }
            else
            {
                numRows = parseNumOrVariableIdent(node.getChild(0));
                offset = parseNumOrVariableIdent(node.getChild(1));
            }
        }

        Integer numRowsInt = null;
        String numRowsVariable = null;
        if (numRows instanceof String)
        {
            numRowsVariable = (String) numRows;
        }
        else
        {
            numRowsInt = (Integer) numRows;
        }

        Integer offsetInt = null;
        String offsetVariable = null;
        if (offset instanceof String)
        {
            offsetVariable = (String) offset;
        }
        else
        {
            offsetInt = (Integer) offset;
        }

        return new RowLimitSpec(numRowsInt, offsetInt, numRowsVariable, offsetVariable);
    }

    private static Object parseNumOrVariableIdent(Tree child)
    {
        if (child.getType() == EsperEPL2GrammarParser.IDENT)
        {
            return child.getText();
        }
        else
        {
            return IntValue.parseString(child.getText());
        }
    }
}
