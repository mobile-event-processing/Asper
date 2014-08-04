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

package com.espertech.esper.support.epl;

import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.spec.SelectClauseElementCompiled;
import com.espertech.esper.epl.spec.SelectClauseExprCompiledSpec;
import com.espertech.esper.epl.spec.SelectClauseExprRawSpec;
import com.espertech.esper.type.MathArithTypeEnum;

import java.util.LinkedList;
import java.util.List;

public class SupportSelectExprFactory
{
    public static List<SelectClauseElementCompiled> makeInvalidSelectList() throws Exception
    {
        List<SelectClauseElementCompiled> selectionList = new LinkedList<SelectClauseElementCompiled>();
        ExprIdentNode node = new ExprIdentNodeImpl("xxxx", "s0");
        selectionList.add(new SelectClauseExprCompiledSpec(node, null, null));
        return selectionList;
    }

    public static List<SelectClauseExprCompiledSpec> makeSelectListFromIdent(String propertyName, String streamName) throws Exception
    {
        List<SelectClauseExprCompiledSpec> selectionList = new LinkedList<SelectClauseExprCompiledSpec>();
        ExprNode identNode = SupportExprNodeFactory.makeIdentNode(propertyName, streamName);
        selectionList.add(new SelectClauseExprCompiledSpec(identNode, "propertyName", null));
        return selectionList;
    }

    public static List<SelectClauseExprCompiledSpec> makeNoAggregateSelectList() throws Exception
    {
        List<SelectClauseExprCompiledSpec> selectionList = new LinkedList<SelectClauseExprCompiledSpec>();
        ExprNode identNode = SupportExprNodeFactory.makeIdentNode("doubleBoxed", "s0");
        ExprNode mathNode = SupportExprNodeFactory.makeMathNode();
        selectionList.add(new SelectClauseExprCompiledSpec(identNode, "resultOne", null));
        selectionList.add(new SelectClauseExprCompiledSpec(mathNode, "resultTwo", null));
        return selectionList;
    }

    public static List<SelectClauseElementCompiled> makeNoAggregateSelectListUnnamed() throws Exception
    {
        List<SelectClauseElementCompiled> selectionList = new LinkedList<SelectClauseElementCompiled>();
        ExprNode identNode = SupportExprNodeFactory.makeIdentNode("doubleBoxed", "s0");
        ExprNode mathNode = SupportExprNodeFactory.makeMathNode();
        selectionList.add(new SelectClauseExprCompiledSpec(identNode, null, null));
        selectionList.add(new SelectClauseExprCompiledSpec(mathNode, "result", null));
        return selectionList;
    }

    public static List<SelectClauseElementCompiled> makeAggregateSelectListWithProps() throws Exception
    {
        ExprNode top = new ExprSumNode(false, false);
        ExprNode identNode = SupportExprNodeFactory.makeIdentNode("doubleBoxed", "s0");
        top.addChildNode(identNode);

        List<SelectClauseElementCompiled> selectionList = new LinkedList<SelectClauseElementCompiled>();
        selectionList.add(new SelectClauseExprCompiledSpec(top, null, null));
        return selectionList;
    }

    public static List<SelectClauseElementCompiled> makeAggregatePlusNoAggregate() throws Exception
    {
        ExprNode top = new ExprSumNode(false, false);
        ExprNode identNode = SupportExprNodeFactory.makeIdentNode("doubleBoxed", "s0");
        top.addChildNode(identNode);

        ExprNode identNode2 = SupportExprNodeFactory.makeIdentNode("doubleBoxed", "s0");

        List<SelectClauseElementCompiled> selectionList = new LinkedList<SelectClauseElementCompiled>();
        selectionList.add(new SelectClauseExprCompiledSpec(top, null, null));
        selectionList.add(new SelectClauseExprCompiledSpec(identNode2, null, null));
        return selectionList;
    }

    public static List<SelectClauseElementCompiled> makeAggregateMixed() throws Exception
    {
        // make a "select doubleBoxed, sum(intPrimitive)" -equivalent
        List<SelectClauseElementCompiled> selectionList = new LinkedList<SelectClauseElementCompiled>();

        ExprNode identNode = SupportExprNodeFactory.makeIdentNode("doubleBoxed", "s0");
        selectionList.add(new SelectClauseExprCompiledSpec(identNode, null, null));

        ExprNode top = new ExprSumNode(false, false);
        identNode = SupportExprNodeFactory.makeIdentNode("intPrimitive", "s0");
        top.addChildNode(identNode);
        selectionList.add(new SelectClauseExprCompiledSpec(top, null, null));

        return selectionList;
    }

    public static List<SelectClauseExprRawSpec> makeAggregateSelectListNoProps() throws Exception
    {
        /*
                                    top (*)
                  c1 (sum)                            c2 (10)
                  c1_1 (5)
        */

        ExprNode top = new ExprMathNode(MathArithTypeEnum.MULTIPLY, false, false);
        ExprNode c1 = new ExprSumNode(false, false);
        ExprNode c1_1 = new SupportExprNode(5);
        ExprNode c2 = new SupportExprNode(10);

        top.addChildNode(c1);
        top.addChildNode(c2);
        c1.addChildNode(c1_1);

        ExprNodeUtility.getValidatedSubtree(top, ExprValidationContextFactory.makeEmpty());

        List<SelectClauseExprRawSpec> selectionList = new LinkedList<SelectClauseExprRawSpec>();
        selectionList.add(new SelectClauseExprRawSpec(top, null));
        return selectionList;
    }
}
