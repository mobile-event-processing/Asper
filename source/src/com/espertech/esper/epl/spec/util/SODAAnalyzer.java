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

package com.espertech.esper.epl.spec.util;

import com.espertech.esper.client.soda.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SODAAnalyzer
{
    public static List<CreateSchemaClause> analyzeModelCreateSchema(EPStatementObjectModel model) {
        if (model.getCreateDataFlow() == null || model.getCreateDataFlow().getSchemas() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<CreateSchemaClause>(model.getCreateDataFlow().getSchemas());
    }

    public static List<EPStatementObjectModel> analyzeModelSelectStatements(EPStatementObjectModel model) {
        if (model.getCreateDataFlow() == null) {
            return Collections.emptyList();
        }
        List<EPStatementObjectModel> models = new ArrayList<EPStatementObjectModel>();
        if (model.getCreateDataFlow() != null) {
            for (DataFlowOperator op : model.getCreateDataFlow().getOperators()) {
                if (op.getParameters() == null || op.getParameters().isEmpty()) {
                    continue;
                }
                for (DataFlowOperatorParameter param : op.getParameters()) {
                    if (param.getParameterValue() instanceof EPStatementObjectModel) {
                        models.add((EPStatementObjectModel) param.getParameterValue());
                    }
                }
            }
        }
        return models;
    }

    public static List<AnnotationPart> analyzeModelAnnotations(EPStatementObjectModel model) {
        List<AnnotationPart> annotations = new ArrayList<AnnotationPart>();
        if (model.getAnnotations() != null) {
            annotations.addAll(model.getAnnotations());
        }

        if (model.getCreateDataFlow() != null) {
            for (DataFlowOperator op : model.getCreateDataFlow().getOperators()) {
                if (op.getAnnotations() != null) {
                    annotations.addAll(op.getAnnotations());
                }
            }
        }

        return annotations;
    }

    public static List<Expression> analyzeModelExpressions(EPStatementObjectModel model) {
        final List<Expression> expressions = new ArrayList<Expression>();

        if (model.getCreateExpression() != null &&
            model.getCreateExpression().getExpressionDeclaration() != null &&
            model.getCreateExpression().getExpressionDeclaration().getExpression() != null) {
            expressions.add(model.getCreateExpression().getExpressionDeclaration().getExpression());
        }

        if (model.getExpressionDeclarations() != null) {
            for (ExpressionDeclaration decl : model.getExpressionDeclarations()) {
                expressions.add(decl.getExpression());
            }
        }

        if (model.getCreateContext() != null) {
            ContextDescriptor desc = model.getCreateContext().getDescriptor();
            collectExpressionsContextDesc(desc, expressions);
        }

        if (model.getCreateVariable() != null) {
            Expression expr = model.getCreateVariable().getOptionalAssignment();
            if (expr != null) {
                expressions.add(expr);
            }
        }

        if (model.getCreateWindow() != null) {
            Expression expr = model.getCreateWindow().getInsertWhereClause();
            if (expr != null) {
                expressions.add(expr);
            }
            for (View view : model.getCreateWindow().getViews()) {
                expressions.addAll(view.getParameters());
            }
        }

        if (model.getUpdateClause() != null) {
            if (model.getUpdateClause().getOptionalWhereClause() != null) {
                expressions.add(model.getUpdateClause().getOptionalWhereClause());
            }
            if (model.getUpdateClause().getAssignments() != null) {
                for (AssignmentPair pair : model.getUpdateClause().getAssignments()) {
                    expressions.add(pair.getValue());
                } 
            }
        }
                            
        // on-expr
        if (model.getOnExpr() != null) {
            if (model.getOnExpr() instanceof OnInsertSplitStreamClause) {
                OnInsertSplitStreamClause onSplit = (OnInsertSplitStreamClause) model.getOnExpr();
                for (OnInsertSplitStreamItem item : onSplit.getItems()) {
                    if (item.getSelectClause() != null) {
                        for (SelectClauseElement selement : item.getSelectClause().getSelectList()) {
                            if (!(selement instanceof SelectClauseExpression)) {
                                continue;
                            }
                            SelectClauseExpression sexpr = (SelectClauseExpression) selement;
                            expressions.add(sexpr.getExpression());
                        }							
                    }
                    if (item.getWhereClause() != null) {
                        expressions.add(item.getWhereClause());
                    }
                }
            }
            if (model.getOnExpr() instanceof OnSetClause) {
                OnSetClause onSet = (OnSetClause) model.getOnExpr();
                if (onSet.getAssignments() != null) {
                    for (AssignmentPair aitem : onSet.getAssignments()) {
                        expressions.add(aitem.getValue());
                    }
                }
            }
            if (model.getOnExpr() instanceof OnUpdateClause) {
                OnUpdateClause onUpdate = (OnUpdateClause) model.getOnExpr();
                if (onUpdate.getAssignments() != null) {
                    for (AssignmentPair bitem : onUpdate.getAssignments()) {
                        expressions.add(bitem.getValue());
                    }
                }
            }
            if (model.getOnExpr() instanceof OnMergeClause) {
                OnMergeClause onMerge = (OnMergeClause) model.getOnExpr();
                for (OnMergeMatchItem item : onMerge.getMatchItems()) {
                    if (item.getOptionalCondition() != null) {
                        expressions.add(item.getOptionalCondition());
                    }
                    for (OnMergeMatchedAction action : item.getActions()) {
                        if (action instanceof OnMergeMatchedDeleteAction) {
                            OnMergeMatchedDeleteAction delete = (OnMergeMatchedDeleteAction) action;
                            if (delete.getWhereClause() != null) {
                                expressions.add(delete.getWhereClause());
                            }
                        }
                        else if (action instanceof OnMergeMatchedUpdateAction) {
                            OnMergeMatchedUpdateAction update = (OnMergeMatchedUpdateAction) action;
                            if (update.getWhereClause() != null) {
                                expressions.add(update.getWhereClause());
                            }
                            for (AssignmentPair assignment : update.getAssignments()) {
                                expressions.add(assignment.getValue());
                            }
                        }
                        else if (action instanceof OnMergeMatchedInsertAction) {
                            OnMergeMatchedInsertAction insert = (OnMergeMatchedInsertAction) action;
                            if (insert.getWhereClause() != null) {
                                expressions.add(insert.getWhereClause());
                            }
                            for (SelectClauseElement element : insert.getSelectList()) {
                                if (element instanceof SelectClauseExpression) {
                                    SelectClauseExpression expr = (SelectClauseExpression) element;
                                    expressions.add(expr.getExpression());
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // select clause
        if (model.getSelectClause() != null) {
            if (model.getSelectClause().getSelectList() != null) {
                for (SelectClauseElement selectItem : model.getSelectClause().getSelectList()) {
                    if (!(selectItem instanceof SelectClauseExpression)) {
                        continue;
                    }
                    SelectClauseExpression selectExpr = (SelectClauseExpression) selectItem;
                    expressions.add(selectExpr.getExpression());
                }
            }
        }

        // from clause
        if (model.getFromClause() != null) {
            for (Stream stream : model.getFromClause().getStreams()) {
                // filter stream
                if (stream instanceof FilterStream) {
                    FilterStream filterStream = (FilterStream) stream;
                    Filter filter = filterStream.getFilter();
                    if ((filter != null) && (filter.getFilter() != null)){
                        expressions.add(filterStream.getFilter().getFilter());
                    }
                    if ((filter != null) && (filter.getOptionalPropertySelects() != null)) {
                        for (ContainedEventSelect contained : filter.getOptionalPropertySelects()) {
                            for (SelectClauseElement selectItem : contained.getSelectClause().getSelectList()) {
                                if (!(selectItem instanceof SelectClauseExpression)) {
                                    continue;
                                }
                                SelectClauseExpression selectExpr = (SelectClauseExpression) selectItem;
                                expressions.add(selectExpr.getExpression());
                            }
                            if (contained.getWhereClause() != null) {
                                expressions.add(contained.getWhereClause());
                            }
                        }
                    }
                }
                // pattern stream
                if (stream instanceof PatternStream) {
                    PatternStream patternStream = (PatternStream) stream;
                    collectPatternExpressions(expressions, patternStream.getExpression());
                }
                // method stream
                if (stream instanceof MethodInvocationStream) {
                    MethodInvocationStream methodStream = (MethodInvocationStream) stream;
                    if (methodStream.getParameterExpressions() != null) {
                        expressions.addAll(methodStream.getParameterExpressions());
                    }
                }
                if (stream instanceof ProjectedStream) {
                    ProjectedStream projectedStream = (ProjectedStream) stream;
                    if (projectedStream.getViews() != null) {
                        for (View view : projectedStream.getViews()) {
                            expressions.addAll(view.getParameters());
                        }
                    }
                }
            }

            if (model.getFromClause().getOuterJoinQualifiers() != null) {
                for (OuterJoinQualifier q : model.getFromClause().getOuterJoinQualifiers()) {
                    expressions.add(q.getLeft());
                    expressions.add(q.getRight());
                    for (PropertyValueExpressionPair pair : q.getAdditionalProperties()) {
                        expressions.add(pair.getLeft());
                        expressions.add(pair.getRight());
                    }
                }
            }
        }
        
        if (model.getWhereClause() != null) {
            expressions.add(model.getWhereClause());
        }
        
        if (model.getGroupByClause() != null) {
            for (Expression groupByExpr : model.getGroupByClause().getGroupByExpressions()) {
                expressions.add(groupByExpr);
            }
        }

        if (model.getHavingClause() != null) {
            expressions.add(model.getHavingClause());
        }

        if (model.getOutputLimitClause() != null) {
            if (model.getOutputLimitClause().getWhenExpression() != null) {
                expressions.add(model.getOutputLimitClause().getWhenExpression());
            }
            if (model.getOutputLimitClause().getAndAfterTerminateAndExpr() != null) {
                expressions.add(model.getOutputLimitClause().getAndAfterTerminateAndExpr());
            }
            if (model.getOutputLimitClause().getThenAssignments() != null) {
                for (AssignmentPair thenAssign : model.getOutputLimitClause().getThenAssignments()) {
                    expressions.add(thenAssign.getValue());
                }					
            }
            if (model.getOutputLimitClause().getAndAfterTerminateThenAssignments() != null) {
                for (AssignmentPair thenAssign : model.getOutputLimitClause().getAndAfterTerminateThenAssignments()) {
                    expressions.add(thenAssign.getValue());
                }
            }
            if (model.getOutputLimitClause().getCrontabAtParameters() != null) {
                for (Expression expr : model.getOutputLimitClause().getCrontabAtParameters()) {
                    expressions.add(expr);
                }
            }
            if (model.getOutputLimitClause().getTimePeriodExpression() != null) {
                expressions.add(model.getOutputLimitClause().getTimePeriodExpression());
            }
            if (model.getOutputLimitClause().getAfterTimePeriodExpression() != null) {
                expressions.add(model.getOutputLimitClause().getAfterTimePeriodExpression());
            }
        }
        
        if (model.getOrderByClause() != null) {
            for (OrderByElement orderByElement : model.getOrderByClause().getOrderByExpressions()) {
                expressions.add(orderByElement.getExpression());
            }									
        }
        
        if (model.getMatchRecognizeClause() != null) {
            if (model.getMatchRecognizeClause().getPartitionExpressions() != null) {
                for (Expression partitionExpr : model.getMatchRecognizeClause().getPartitionExpressions()) {
                    expressions.add(partitionExpr);
                }
            }
            for (SelectClauseElement selectItemMR : model.getMatchRecognizeClause().getMeasures()) {
                if (!(selectItemMR instanceof SelectClauseExpression)) {
                    continue;
                }
                SelectClauseExpression selectExprMR = (SelectClauseExpression) selectItemMR;
                expressions.add(selectExprMR.getExpression());
            }
            for (MatchRecognizeDefine define : model.getMatchRecognizeClause().getDefines()) {
                expressions.add(define.getExpression());
            }
            if (model.getMatchRecognizeClause().getIntervalClause() != null) {
                if (model.getMatchRecognizeClause().getIntervalClause().getExpression() != null) {
                    expressions.add(model.getMatchRecognizeClause().getIntervalClause().getExpression());
                }
            }
        }

        if (model.getForClause() != null) {
            for (ForClauseItem item : model.getForClause().getItems()) {
                if (item.getExpressions() != null) {
                    expressions.addAll(item.getExpressions());
                }
            }
        }

        if (model.getCreateDataFlow() != null) {
            for (DataFlowOperator op : model.getCreateDataFlow().getOperators()) {
                if (op.getParameters() == null || op.getParameters().isEmpty()) {
                    continue;
                }
                for (DataFlowOperatorParameter param : op.getParameters()) {
                    if (param.getParameterValue() instanceof Expression) {
                        expressions.add((Expression) param.getParameterValue());
                    }
                }
            }
        }

        return expressions;
    }

    private static void collectExpressionsContextDesc(ContextDescriptor desc, List<Expression> expressions) {
        if (desc instanceof ContextDescriptorKeyedSegmented) {
            ContextDescriptorKeyedSegmented ks = (ContextDescriptorKeyedSegmented) desc;
            for (ContextDescriptorKeyedSegmentedItem item : ks.getItems()) {
                if (item.getFilter().getFilter() != null) {
                    expressions.add(item.getFilter().getFilter());
                }
            }
        }
        if (desc instanceof ContextDescriptorCategory) {
            ContextDescriptorCategory cat = (ContextDescriptorCategory) desc;
            for (ContextDescriptorCategoryItem item : cat.getItems()) {
                if (item.getExpression() != null) {
                    expressions.add(item.getExpression());
                }
            }
            if (cat.getFilter().getFilter() != null) {
                expressions.add(cat.getFilter().getFilter());
            }
        }
        if (desc instanceof ContextDescriptorInitiatedTerminated) {
            ContextDescriptorInitiatedTerminated ts = (ContextDescriptorInitiatedTerminated) desc;
            collectContextConditionExpressions(expressions, ts.getStartCondition());
            collectContextConditionExpressions(expressions, ts.getEndCondition());
        }
        if (desc instanceof ContextDescriptorHashSegmented) {
            ContextDescriptorHashSegmented hs = (ContextDescriptorHashSegmented) desc;
            for (ContextDescriptorHashSegmentedItem item : hs.getItems()) {
                if (item.getFilter().getFilter() != null) {
                    expressions.add(item.getFilter().getFilter());
                }
                if (item.getHashFunction() != null) {
                    expressions.add(item.getHashFunction());
                }
            }
        }
        if (desc instanceof ContextDescriptorNested) {
            ContextDescriptorNested nested = (ContextDescriptorNested) desc;
            for (CreateContextClause createCtx : nested.getContexts()) {
                collectExpressionsContextDesc(createCtx.getDescriptor(), expressions);
            }
        }
    }

    private static void collectContextConditionExpressions(List<Expression> expressions, ContextDescriptorCondition condition) {
        if (condition instanceof ContextDescriptorConditionCrontab) {
            ContextDescriptorConditionCrontab crontab = (ContextDescriptorConditionCrontab) condition;
            expressions.addAll(crontab.getCrontabExpressions());
        }
        else if (condition instanceof ContextDescriptorConditionFilter) {
            ContextDescriptorConditionFilter filter = (ContextDescriptorConditionFilter) condition;
            if (filter != null && filter.getFilter() != null && filter.getFilter().getFilter() != null) {
                expressions.add(filter.getFilter().getFilter());
            }
        }
        else if (condition instanceof ContextDescriptorConditionPattern) {
            ContextDescriptorConditionPattern pattern = (ContextDescriptorConditionPattern) condition;
            collectPatternExpressions(expressions, pattern.getPattern());
        }
        else if (condition instanceof ContextDescriptorConditionTimePeriod) {
            ContextDescriptorConditionTimePeriod ts = (ContextDescriptorConditionTimePeriod) condition;
            expressions.add(ts.getTimePeriod());
        }
    }

    private static void collectPatternExpressions(final List<Expression> expressions, PatternExpr expression) {
        SODAAnalyzerPatternCollector collector = new SODAAnalyzerPatternCollector() {
            public void visit(PatternExpr patternExpr)
            {
                if (patternExpr instanceof PatternFilterExpr) {
                    PatternFilterExpr filter = (PatternFilterExpr) patternExpr;
                    if (filter.getFilter().getFilter() != null) {
                        expressions.add(filter.getFilter().getFilter());
                    }
                }
            }
        };
        traversePatternRecursive(expression, collector);
    }

    private static void traversePatternRecursive(PatternExpr patternExpr, SODAAnalyzerPatternCollector collectorFunction) {
        collectorFunction.visit(patternExpr);
        if (patternExpr == null){
            return;
        }
        if (patternExpr.getChildren() == null) {
            return;
        }
        for (PatternExpr child : patternExpr.getChildren()) {
            traversePatternRecursive(child, collectorFunction);
        }
    }

    public static List<MatchRecognizeRegEx> analyzeModelMatchRecogRegexs(EPStatementObjectModel model)
    {
        if (model.getMatchRecognizeClause() == null) {
            return Collections.EMPTY_LIST;
        }
        return Collections.singletonList(model.getMatchRecognizeClause().getPattern());
    }

    public static List<PatternExpr> analyzeModelPatterns(EPStatementObjectModel model)
    {
        List<PatternExpr> result = new ArrayList<PatternExpr>();
        if (model.getFromClause() != null) {
            for (Stream stream : model.getFromClause().getStreams()) {
                // pattern stream
                if (stream instanceof PatternStream) {
                    PatternStream patternStream = (PatternStream) stream;
                    if (patternStream.getExpression() != null) {
                        result.add(patternStream.getExpression());
                    }
                }
            }
        }

        if (model.getCreateContext() != null) {
            ContextDescriptor desc = model.getCreateContext().getDescriptor();
            if (desc instanceof ContextDescriptorInitiatedTerminated) {
                ContextDescriptorInitiatedTerminated cat = (ContextDescriptorInitiatedTerminated) desc;
                analyzeContextCondition(cat.getStartCondition(), result);
                analyzeContextCondition(cat.getEndCondition(), result);
            }
        }

        return result;
    }

    private static void analyzeContextCondition(ContextDescriptorCondition condition, List<PatternExpr> result) {
        if (condition instanceof ContextDescriptorConditionPattern) {
            ContextDescriptorConditionPattern pattern = (ContextDescriptorConditionPattern) condition;
            if (pattern.getPattern() != null) {
                result.add(pattern.getPattern());
            }
        }
    }
}
