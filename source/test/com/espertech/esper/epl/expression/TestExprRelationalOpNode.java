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

import com.espertech.esper.support.epl.SupportExprNodeUtil;
import junit.framework.TestCase;
import com.espertech.esper.support.epl.SupportExprNode;
import com.espertech.esper.type.RelationalOpEnum;

public class TestExprRelationalOpNode extends TestCase
{
    private ExprRelationalOpNode opNode;

    public void setUp()
    {
        opNode = new ExprRelationalOpNodeImpl(RelationalOpEnum.GE);
    }

    public void testGetType() throws Exception
    {
        opNode.addChildNode(new SupportExprNode(Long.class));
        opNode.addChildNode(new SupportExprNode(int.class));
        assertEquals(Boolean.class, opNode.getType());
    }

    public void testValidate() throws Exception
    {
        // Test success
        opNode.addChildNode(new SupportExprNode(String.class));
        opNode.addChildNode(new SupportExprNode(String.class));
        opNode.validate(ExprValidationContextFactory.makeEmpty());

        opNode.getChildNodes().clear();
        opNode.addChildNode(new SupportExprNode(String.class));

        // Test too few nodes under this node
        try
        {
            opNode.validate(ExprValidationContextFactory.makeEmpty());
            fail();
        }
        catch (IllegalStateException ex)
        {
            // Expected
        }

        // Test mismatch type
        opNode.addChildNode(new SupportExprNode(Integer.class));
        try
        {
            opNode.validate(ExprValidationContextFactory.makeEmpty());
            fail();
        }
        catch (ExprValidationException ex)
        {
            // Expected
        }

        // Test type cannot be compared
        opNode.getChildNodes().clear();
        opNode.addChildNode(new SupportExprNode(Boolean.class));
        opNode.addChildNode(new SupportExprNode(Boolean.class));

        try
        {
            opNode.validate(ExprValidationContextFactory.makeEmpty());
            fail();
        }
        catch (ExprValidationException ex)
        {
            // Expected
        }
    }

    public void testEvaluate() throws Exception
    {
        SupportExprNode childOne = new SupportExprNode("d");
        SupportExprNode childTwo = new SupportExprNode("c");
        opNode.addChildNode(childOne);
        opNode.addChildNode(childTwo);
        opNode.validate(ExprValidationContextFactory.makeEmpty());       // Type initialization

        assertEquals(true, opNode.evaluate(null, false, null));

        childOne.setValue("c");
        assertEquals(true, opNode.evaluate(null, false, null));

        childOne.setValue("b");
        assertEquals(false, opNode.evaluate(null, false, null));

        opNode = makeNode(null, Integer.class, 2, Integer.class);
        assertEquals(null, opNode.evaluate(null, false, null));
        opNode = makeNode(1, Integer.class, null, Integer.class);
        assertEquals(null, opNode.evaluate(null, false, null));
        opNode = makeNode(null, Integer.class, null, Integer.class);
        assertEquals(null, opNode.evaluate(null, false, null));
    }

    public void testToExpressionString() throws Exception
    {
        opNode.addChildNode(new SupportExprNode(10));
        opNode.addChildNode(new SupportExprNode(5));
        assertEquals("10>=5", opNode.toExpressionString());
    }

    private ExprRelationalOpNode makeNode(Object valueLeft, Class typeLeft, Object valueRight, Class typeRight) throws Exception
    {
        ExprRelationalOpNode relOpNode = new ExprRelationalOpNodeImpl(RelationalOpEnum.GE);
        relOpNode.addChildNode(new SupportExprNode(valueLeft, typeLeft));
        relOpNode.addChildNode(new SupportExprNode(valueRight, typeRight));
        SupportExprNodeUtil.validate(relOpNode);
        return relOpNode;
    }

    public void testEqualsNode() throws Exception
    {
        assertTrue(opNode.equalsNode(opNode));
        assertFalse(opNode.equalsNode(new ExprRelationalOpNodeImpl(RelationalOpEnum.LE)));
        assertFalse(opNode.equalsNode(new ExprOrNode()));
    }
}
