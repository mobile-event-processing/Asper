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

package com.espertech.esper.epl.expression;

import com.espertech.esper.support.epl.SupportExprNode;
import com.espertech.esper.support.epl.SupportExprNodeFactory;

public class TestExprCountNode extends TestExprAggregateNodeAdapter
{
    private ExprCountNode wildcardCount;

    public void setUp() throws Exception
    {
        super.validatedNodeToTest = makeNode(5, Integer.class);

        wildcardCount = new ExprCountNode(false, false);
        SupportExprNodeFactory.validate3Stream(wildcardCount);
    }

    public void testGetType() throws Exception
    {
        assertEquals(Long.class, validatedNodeToTest.getType());
        assertEquals(Long.class, wildcardCount.getType());
    }

    public void testToExpressionString() throws Exception
    {
        assertEquals("count(5)", validatedNodeToTest.toExpressionString());
        assertEquals("count(*)", wildcardCount.toExpressionString());
    }

    public void testEqualsNode() throws Exception
    {
        assertTrue(validatedNodeToTest.equalsNode(validatedNodeToTest));
        assertFalse(validatedNodeToTest.equalsNode(new ExprSumNode(false, false)));
        assertTrue(wildcardCount.equalsNode(wildcardCount));
    }

    private ExprCountNode makeNode(Object value, Class type) throws Exception
    {
        ExprCountNode countNode = new ExprCountNode(false, false);
        countNode.addChildNode(new SupportExprNode(value, type));
        SupportExprNodeFactory.validate3Stream(countNode);
        return countNode;
    }
}
