/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.parse;

import com.espertech.esper.epl.expression.ExprChainedSpec;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprTimePeriod;
import com.espertech.esper.epl.generated.EsperEPL2Ast;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.pattern.EvalFactoryNode;
import com.espertech.esper.util.JavaClassHelper;
import org.antlr.runtime.tree.Tree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ASTContextHelper
{
    private static final Log log = LogFactory.getLog(ASTContextHelper.class);

    public static CreateContextDesc walkCreateContext(Tree parent, Map<Tree, ExprNode> astExprNodeMap, Map<Tree, EvalFactoryNode> astPatternNodeMap, PropertyEvalSpec propertyEvalSpec, FilterSpecRaw filterSpec)
    {
        String contextName = parent.getChild(0).getText();
        Tree detailParent = parent.getChild(1);

        ContextDetail contextDetail;

        // temporal fixed (start+end) and overlapping (initiated/terminated)
        if (detailParent.getType() == EsperEPL2Ast.CREATE_CTX_INIT || detailParent.getType() == EsperEPL2Ast.CREATE_CTX_FIXED) {
            ContextDetailCondition startEndpoint = getContextCondition(detailParent.getChild(0), astExprNodeMap, astPatternNodeMap, propertyEvalSpec);
            ContextDetailCondition endEndpoint = getContextCondition(detailParent.getChild(1), astExprNodeMap, astPatternNodeMap, propertyEvalSpec);
            boolean overlapping = detailParent.getType() == EsperEPL2Ast.CREATE_CTX_INIT;
            contextDetail = new ContextDetailInitiatedTerminated(startEndpoint, endEndpoint, overlapping);
        }
        // categorized
        else if (detailParent.getType() == EsperEPL2Ast.CREATE_CTX_CAT){
            List<ContextDetailCategoryItem> items = new ArrayList<ContextDetailCategoryItem>();
            for (int i = 0; i < detailParent.getChildCount() -1; i++) {
                Tree categoryParent = detailParent.getChild(i);
                ExprNode exprNode = astExprNodeMap.remove(categoryParent.getChild(0));
                String name = categoryParent.getChild(1).getText();
                items.add(new ContextDetailCategoryItem(exprNode, name));
            }
            filterSpec = ASTExprHelper.walkFilterSpec(detailParent.getChild(detailParent.getChildCount() - 1), propertyEvalSpec, astExprNodeMap);
            contextDetail = new ContextDetailCategory(items, filterSpec);
        }
        // partitioned
        else if (detailParent.getType() == EsperEPL2Ast.CREATE_CTX_PART){
            List<ContextDetailPartitionItem> rawSpecs = new ArrayList<ContextDetailPartitionItem>();
            for (int i = 0; i < detailParent.getChildCount(); i++) {

                Tree partitionParent = detailParent.getChild(i);
                filterSpec = ASTExprHelper.walkFilterSpec(partitionParent.getChild(0), propertyEvalSpec, astExprNodeMap);
                propertyEvalSpec = null;

                List<String> propertyNames = new ArrayList<String>();
                for (int j = 1; j < partitionParent.getChildCount(); j++) {
                    String propertyName = ASTFilterSpecHelper.getPropertyName(partitionParent.getChild(j), 0);
                    propertyNames.add(propertyName);
                }

                rawSpecs.add(new ContextDetailPartitionItem(filterSpec, propertyNames));
            }
            contextDetail = new ContextDetailPartitioned(rawSpecs);
        }
        // partitioned
        else if (detailParent.getType() == EsperEPL2Ast.CREATE_CTX_COAL){
            List<ContextDetailHashItem> rawSpecs = new ArrayList<ContextDetailHashItem>();
            int count = 0;
            for (int i = 0; i < detailParent.getChildCount(); i++) {
                Tree hashItemParent = detailParent.getChild(i);
                if (hashItemParent.getType() == EsperEPL2Ast.COALESCE) {
                    count++;
                    ExprChainedSpec func = ASTLibHelper.getLibFunctionChainSpec(hashItemParent.getChild(0), astExprNodeMap);
                    filterSpec = ASTExprHelper.walkFilterSpec(hashItemParent.getChild(1), propertyEvalSpec, astExprNodeMap);
                    propertyEvalSpec = null;
                    rawSpecs.add(new ContextDetailHashItem(func, filterSpec));
                }
            }

            String granularity = detailParent.getChild(count).getText();
            if (!granularity.toLowerCase().equals("granularity")) {
                throw new ASTWalkException("Expected 'granularity' keyword after list of coalesce items, found '" + granularity + "' instead");
            }
            Number num = (Number) ASTConstantHelper.parse(detailParent.getChild(count + 1));
            String preallocateStr = detailParent.getChildCount() - 1 < count+2 ? null : detailParent.getChild(count + 2).getText();
            if (preallocateStr != null && !preallocateStr.toLowerCase().equals("preallocate")) {
                throw new ASTWalkException("Expected 'preallocate' keyword after list of coalesce items, found '" + preallocateStr + "' instead");
            }
            if (!JavaClassHelper.isNumericNonFP(num.getClass()) || JavaClassHelper.getBoxedType(num.getClass()) == Long.class) {
                throw new ASTWalkException("Granularity provided must be an int-type number, received " + num.getClass() + " instead");
            }

            contextDetail = new ContextDetailHash(rawSpecs, num.intValue(), preallocateStr != null);
        }
        else if (detailParent.getType() == EsperEPL2Ast.CREATE_CTX_NESTED) {
            List<CreateContextDesc> contexts = new ArrayList<CreateContextDesc>();
            for (int i = 0; i < detailParent.getChildCount(); i++) {
                Tree parentCreate = detailParent.getChild(i);
                if (parentCreate.getType() != EsperEPL2Ast.CREATE_CTX) {
                    throw new IllegalStateException("Child to nested context is not a context-create but type " + parentCreate.getType());
                }
                contexts.add(walkCreateContext(parentCreate, astExprNodeMap, astPatternNodeMap, propertyEvalSpec, filterSpec));
            }
            contextDetail = new ContextDetailNested(contexts);
        }
        else {
            throw new IllegalStateException("Unrecognized context detail type '" + detailParent.getType() + "'");
        }

        return new CreateContextDesc(contextName, contextDetail);
    }

    private static ContextDetailCondition getContextCondition(Tree parent, Map<Tree, ExprNode> astExprNodeMap, Map<Tree, EvalFactoryNode> astPatternNodeMap, PropertyEvalSpec propertyEvalSpec) {
        if (parent.getType() == EsperEPL2Ast.CRONTAB_LIMIT_EXPR_PARAM) {
            List<ExprNode> crontab = ASTExprHelper.getRemoveAllChildExpr(parent, astExprNodeMap);
            return new ContextDetailConditionCrontab(crontab);
        }
        else if (parent.getType() == EsperEPL2Ast.CREATE_CTX_PATTERN) {
            EvalFactoryNode evalNode = astPatternNodeMap.remove(parent.getChild(0).getChild(0));
            boolean inclusive = false;
            if (parent.getChildCount() > 1) {
                String ident = parent.getChild(1).getText();
                if (ident != null && !ident.toLowerCase().equals("inclusive")) {
                    throw new ASTWalkException("Expected 'inclusive' keyword after '@', found '" + ident + "' instead");
                }
                inclusive = true;
            }
            return new ContextDetailConditionPattern(evalNode, inclusive);
        }
        else if (parent.getType() == EsperEPL2Ast.STREAM_EXPR) {
            FilterSpecRaw filterSpecRaw = ASTExprHelper.walkFilterSpec(parent.getChild(0), propertyEvalSpec, astExprNodeMap);
            String asName = parent.getChildCount() > 1 ? parent.getChild(1).getText() : null;
            return new ContextDetailConditionFilter(filterSpecRaw, asName);
        }
        else if (parent.getType() == EsperEPL2Ast.AFTER) {
            ExprTimePeriod timePeriod = (ExprTimePeriod) astExprNodeMap.remove(parent.getChild(0));
            return new ContextDetailConditionTimePeriod(timePeriod);
        }
        else {
            throw new IllegalStateException("Unrecognized child type " + parent.getType());
        }
    }
}
