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

import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.support.epl.SupportExprNodeFactory;
import junit.framework.TestCase;

public class TestExprVariableNode extends TestCase
{
    private ExprVariableNodeImpl varNode;
    private VariableService variableService;

    public void setUp() throws Exception
    {
        varNode = new ExprVariableNodeImpl("var1", null);
    }

    public void testGetType()  throws Exception
    {
        SupportExprNodeFactory.validate3Stream(varNode);
        assertEquals(String.class, varNode.getType());
    }

    public void testEvaluate() throws Exception
    {
        SupportExprNodeFactory.validate3Stream(varNode);
        assertEquals("my_variable_value", varNode.evaluate(null, true, null));
    }

    public void testValidate() throws Exception
    {
        // variable doesn't exists
        tryInvalidValidate(new ExprVariableNodeImpl("dummy", null));

        // variable and property are ambigours
        tryInvalidValidate(new ExprVariableNodeImpl("intPrimitive", null));
    }

    public void testEquals()  throws Exception
    {
        ExprInNode otherInNode = SupportExprNodeFactory.makeInSetNode(false);
        ExprVariableNodeImpl otherVarOne = new ExprVariableNodeImpl("dummy", null);
        ExprVariableNodeImpl otherVarTwo = new ExprVariableNodeImpl("var1", null);
        ExprVariableNodeImpl otherVarThree = new ExprVariableNodeImpl("var1.abc", null);

        assertTrue(varNode.equalsNode(varNode));
        assertTrue(varNode.equalsNode(otherVarTwo));
        assertFalse(varNode.equalsNode(otherVarOne));
        assertFalse(varNode.equalsNode(otherInNode));
        assertFalse(otherVarTwo.equalsNode(otherVarThree));
    }

    public void testToExpressionString() throws Exception
    {
        assertEquals("var1", varNode.toExpressionString());
    }

    private void tryInvalidValidate(ExprVariableNodeImpl varNode) throws Exception
    {
        try {
            SupportExprNodeFactory.validate3Stream(varNode);
            fail();
        }
        catch (ExprValidationException ex)
        {
            // expected
        }
    }
}
