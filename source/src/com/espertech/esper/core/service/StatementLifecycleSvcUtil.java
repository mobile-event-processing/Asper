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

package com.espertech.esper.core.service;

import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeSubselectDeclaredDotVisitor;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.pattern.*;

import java.util.ArrayList;
import java.util.List;

public class StatementLifecycleSvcUtil {

    public static void walkStatement(StatementSpecRaw spec, ExprNodeSubselectDeclaredDotVisitor visitor) throws ExprValidationException {

        // Look for expressions with sub-selects in select expression list and filter expression
        // Recursively compile the statement within the statement.
        for (SelectClauseElementRaw raw : spec.getSelectClauseSpec().getSelectExprList())
        {
            if (raw instanceof SelectClauseExprRawSpec)
            {
                SelectClauseExprRawSpec rawExpr = (SelectClauseExprRawSpec) raw;
                rawExpr.getSelectExpression().accept(visitor);
            }
            else
            {
                continue;
            }
        }
        if (spec.getFilterRootNode() != null)
        {
            spec.getFilterRootNode().accept(visitor);
        }
        if (spec.getUpdateDesc() != null)
        {
            if (spec.getUpdateDesc().getOptionalWhereClause() != null)
            {
                spec.getUpdateDesc().getOptionalWhereClause().accept(visitor);
            }
            for (OnTriggerSetAssignment assignment : spec.getUpdateDesc().getAssignments())
            {
                assignment.getExpression().accept(visitor);
            }
        }
        if (spec.getOnTriggerDesc() != null) {
            visitSubselectOnTrigger(spec.getOnTriggerDesc(), visitor);
        }
        // Determine pattern-filter subqueries
        for (StreamSpecRaw streamSpecRaw : spec.getStreamSpecs()) {
            if (streamSpecRaw instanceof PatternStreamSpecRaw) {
                PatternStreamSpecRaw patternStreamSpecRaw = (PatternStreamSpecRaw) streamSpecRaw;
                EvalNodeAnalysisResult analysisResult = EvalNodeUtil.recursiveAnalyzeChildNodes(patternStreamSpecRaw.getEvalFactoryNode());
                for (EvalFactoryNode evalNode : analysisResult.getActiveNodes()) {
                    if (evalNode instanceof EvalFilterFactoryNode) {
                        EvalFilterFactoryNode filterNode = (EvalFilterFactoryNode) evalNode;
                        for (ExprNode filterExpr : filterNode.getRawFilterSpec().getFilterExpressions()) {
                            filterExpr.accept(visitor);
                        }
                    }
                    else if (evalNode instanceof EvalObserverFactoryNode) {
                        int beforeCount = visitor.getSubselects().size();
                        EvalObserverFactoryNode observerNode = (EvalObserverFactoryNode) evalNode;
                        for (ExprNode param : observerNode.getPatternObserverSpec().getObjectParameters()) {
                            param.accept(visitor);
                        }
                        if (visitor.getSubselects().size() != beforeCount) {
                            throw new ExprValidationException("Subselects are not allowed within pattern observer parameters, please consider using a variable instead");
                        }
                    }
                }
            }
        }

        // walk streams
        walkStreamSpecs(spec, visitor);
    }

    public static void walkStreamSpecs(StatementSpecRaw spec, ExprNodeSubselectDeclaredDotVisitor visitor) {

        // Determine filter streams
        for (StreamSpecRaw rawSpec : spec.getStreamSpecs())
        {
            if (rawSpec instanceof FilterStreamSpecRaw) {
                FilterStreamSpecRaw raw = (FilterStreamSpecRaw) rawSpec;
                for (ExprNode filterExpr : raw.getRawFilterSpec().getFilterExpressions()) {
                    filterExpr.accept(visitor);
                }
            }
            if (rawSpec instanceof PatternStreamSpecRaw) {
                PatternStreamSpecRaw patternStreamSpecRaw = (PatternStreamSpecRaw) rawSpec;
                EvalNodeAnalysisResult analysisResult = EvalNodeUtil.recursiveAnalyzeChildNodes(patternStreamSpecRaw.getEvalFactoryNode());
                for (EvalFactoryNode evalNode : analysisResult.getActiveNodes()) {
                    if (evalNode instanceof EvalFilterFactoryNode) {
                        EvalFilterFactoryNode filterNode = (EvalFilterFactoryNode) evalNode;
                        for (ExprNode filterExpr : filterNode.getRawFilterSpec().getFilterExpressions()) {
                            filterExpr.accept(visitor);
                        }
                    }
                }
            }
        }
    }

    private static void visitSubselectOnTrigger(OnTriggerDesc onTriggerDesc, ExprNodeSubselectDeclaredDotVisitor visitor) {
        if (onTriggerDesc instanceof OnTriggerWindowUpdateDesc) {
            OnTriggerWindowUpdateDesc updates = (OnTriggerWindowUpdateDesc) onTriggerDesc;
            for (OnTriggerSetAssignment assignment : updates.getAssignments())
            {
                assignment.getExpression().accept(visitor);
            }
        }
        else if (onTriggerDesc instanceof OnTriggerSetDesc) {
            OnTriggerSetDesc sets = (OnTriggerSetDesc) onTriggerDesc;
            for (OnTriggerSetAssignment assignment : sets.getAssignments())
            {
                assignment.getExpression().accept(visitor);
            }
        }
        else if (onTriggerDesc instanceof OnTriggerSplitStreamDesc) {
            OnTriggerSplitStreamDesc splits = (OnTriggerSplitStreamDesc) onTriggerDesc;
            for (OnTriggerSplitStream split : splits.getSplitStreams())
            {
                if (split.getWhereClause() != null) {
                    split.getWhereClause().accept(visitor);
                }
                if (split.getSelectClause().getSelectExprList() != null) {
                    for (SelectClauseElementRaw element : split.getSelectClause().getSelectExprList()) {
                        if (element instanceof SelectClauseExprRawSpec) {
                            SelectClauseExprRawSpec selectExpr = (SelectClauseExprRawSpec) element;
                            selectExpr.getSelectExpression().accept(visitor);
                        }
                    }
                }
            }
        }
        else if (onTriggerDesc instanceof OnTriggerMergeDesc) {
            OnTriggerMergeDesc merge = (OnTriggerMergeDesc) onTriggerDesc;
            for (OnTriggerMergeMatched matched : merge.getItems()) {
                if (matched.getOptionalMatchCond() != null) {
                    matched.getOptionalMatchCond().accept(visitor);
                }
                for (OnTriggerMergeAction action : matched.getActions())
                {
                    if (action.getOptionalWhereClause() != null) {
                        action.getOptionalWhereClause().accept(visitor);
                    }

                    if (action instanceof OnTriggerMergeActionUpdate) {
                        OnTriggerMergeActionUpdate update = (OnTriggerMergeActionUpdate) action;
                        for (OnTriggerSetAssignment assignment : update.getAssignments())
                        {
                            assignment.getExpression().accept(visitor);
                        }
                    }
                    if (action instanceof OnTriggerMergeActionInsert) {
                        OnTriggerMergeActionInsert insert = (OnTriggerMergeActionInsert) action;
                        for (SelectClauseElementRaw element : insert.getSelectClause()) {
                            if (element instanceof SelectClauseExprRawSpec) {
                                SelectClauseExprRawSpec selectExpr = (SelectClauseExprRawSpec) element;
                                selectExpr.getSelectExpression().accept(visitor);
                            }
                        }
                    }
                }
            }
        }
    }

    public static SelectClauseSpecCompiled compileSelectClause(SelectClauseSpecRaw spec) {
        List<SelectClauseElementCompiled> selectElements = new ArrayList<SelectClauseElementCompiled>();
        SelectClauseSpecCompiled selectClauseCompiled = new SelectClauseSpecCompiled(selectElements, spec.isDistinct());
        for (SelectClauseElementRaw raw : spec.getSelectExprList())
        {
            if (raw instanceof SelectClauseExprRawSpec)
            {
                SelectClauseExprRawSpec rawExpr = (SelectClauseExprRawSpec) raw;
                selectElements.add(new SelectClauseExprCompiledSpec(rawExpr.getSelectExpression(), rawExpr.getOptionalAsName(), rawExpr.getOptionalAsName()));
            }
            else if (raw instanceof SelectClauseStreamRawSpec)
            {
                SelectClauseStreamRawSpec rawExpr = (SelectClauseStreamRawSpec) raw;
                selectElements.add(new SelectClauseStreamCompiledSpec(rawExpr.getStreamName(), rawExpr.getOptionalAsName()));
            }
            else if (raw instanceof SelectClauseElementWildcard)
            {
                SelectClauseElementWildcard wildcard = (SelectClauseElementWildcard) raw;
                selectElements.add(wildcard);
            }
            else
            {
                throw new IllegalStateException("Unexpected select clause element class : " + raw.getClass().getName());
            }
        }
        return selectClauseCompiled;
    }
}
