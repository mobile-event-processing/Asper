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

import junit.framework.TestCase;

public class TestExprConstantNode extends TestCase
{
    private ExprConstantNode constantNode;

    public void setUp()
    {
        constantNode = new ExprConstantNodeImpl("5");
    }

    public void testGetType() throws Exception
    {
        assertEquals(String.class, constantNode.getType());

        constantNode = new ExprConstantNodeImpl(null);
        assertNull(constantNode.getType());
    }

    public void testValidate() throws Exception
    {
        constantNode.validate(ExprValidationContextFactory.makeEmpty());
    }

    public void testEvaluate()
    {
        assertEquals("5", constantNode.evaluate(null, false, null));
    }

    public void testToExpressionString() throws Exception
    {
        constantNode = new ExprConstantNodeImpl("5");
        assertEquals("\"5\"", constantNode.toExpressionString());

        constantNode = new ExprConstantNodeImpl(10);
        assertEquals("10", constantNode.toExpressionString());        
    }

    public void testEqualsNode()
    {
        assertTrue(constantNode.equalsNode(new ExprConstantNodeImpl("5")));
        assertFalse(constantNode.equalsNode(new ExprOrNode()));
        assertFalse(constantNode.equalsNode(new ExprConstantNodeImpl(null)));
        assertFalse(constantNode.equalsNode(new ExprConstantNodeImpl(3)));

        constantNode = new ExprConstantNodeImpl(null);
        assertTrue(constantNode.equalsNode(new ExprConstantNodeImpl(null)));
    }
}
