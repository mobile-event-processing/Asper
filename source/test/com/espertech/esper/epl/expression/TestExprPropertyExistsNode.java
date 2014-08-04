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
import com.espertech.esper.support.epl.SupportExprNode;
import com.espertech.esper.support.epl.SupportExprNodeFactory;
import com.espertech.esper.client.EventBean;

public class TestExprPropertyExistsNode extends TestCase
{
    private ExprPropertyExistsNode[] existsNodes;

    public void setUp() throws Exception
    {
        existsNodes = new ExprPropertyExistsNode[2];

        existsNodes[0] = new ExprPropertyExistsNode();
        existsNodes[0].addChildNode(SupportExprNodeFactory.makeIdentNode("dummy?", "s0"));

        existsNodes[1] = new ExprPropertyExistsNode();
        existsNodes[1].addChildNode(SupportExprNodeFactory.makeIdentNode("boolPrimitive?", "s0"));
    }

    public void testGetType() throws Exception
    {
        for (int i = 0; i < existsNodes.length; i++)
        {
            existsNodes[i].validate(ExprValidationContextFactory.makeEmpty());
            assertEquals(Boolean.class, existsNodes[i].getType());
        }
    }

    public void testValidate() throws Exception
    {
        ExprPropertyExistsNode castNode = new ExprPropertyExistsNode();

        // Test too few nodes under this node
        try
        {
            castNode.validate(ExprValidationContextFactory.makeEmpty());
            fail();
        }
        catch (ExprValidationException ex)
        {
            // Expected
        }

        castNode.addChildNode(new SupportExprNode(1));
        try
        {
            castNode.validate(ExprValidationContextFactory.makeEmpty());
            fail();
        }
        catch (ExprValidationException ex)
        {
            // Expected
        }
    }

    public void testEvaluate() throws Exception
    {
        for (int i = 0; i < existsNodes.length; i++)
        {
            existsNodes[i].validate(ExprValidationContextFactory.makeEmpty());
        }

        assertEquals(false, existsNodes[0].evaluate(new EventBean[3], false, null));
        assertEquals(false, existsNodes[1].evaluate(new EventBean[3], false, null));

        EventBean[] events = new EventBean[] {TestExprIdentNode.makeEvent(10)};
        assertEquals(false, existsNodes[0].evaluate(events, false, null));
        assertEquals(true, existsNodes[1].evaluate(events, false, null));
    }

    public void testEquals() throws Exception
    {
        assertFalse(existsNodes[0].equalsNode(new ExprEqualsNodeImpl(true, false)));
        assertTrue(existsNodes[0].equalsNode(existsNodes[1]));
    }

    public void testToExpressionString() throws Exception
    {
        existsNodes[0].validate(ExprValidationContextFactory.makeEmpty());
        assertEquals("exists(s0.dummy?)", existsNodes[0].toExpressionString());
    }
}
