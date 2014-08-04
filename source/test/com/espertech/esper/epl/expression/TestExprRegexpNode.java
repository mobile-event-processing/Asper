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
import com.espertech.esper.support.epl.SupportExprNodeFactory;
import com.espertech.esper.support.epl.SupportExprNode;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.event.SupportEventBeanFactory;
import com.espertech.esper.client.EventBean;

public class TestExprRegexpNode extends TestCase
{
    private ExprRegexpNode regexpNodeNormal;
    private ExprRegexpNode regexpNodeNot;

    public void setUp() throws Exception
    {
        regexpNodeNormal = SupportExprNodeFactory.makeRegexpNode(false);
        regexpNodeNot = SupportExprNodeFactory.makeRegexpNode(true);
    }

    public void testGetType()  throws Exception
    {
        assertEquals(Boolean.class, regexpNodeNormal.getType());
        assertEquals(Boolean.class, regexpNodeNot.getType());
    }

    public void testValidate() throws Exception
    {
        // No subnodes: Exception is thrown.
        tryInvalidValidate(new ExprRegexpNode(true));

        // singe child node not possible, must be 2 at least
        regexpNodeNormal = new ExprRegexpNode(false);
        regexpNodeNormal.addChildNode(new SupportExprNode(new Integer(4)));
        tryInvalidValidate(regexpNodeNormal);

        // test a type mismatch
        regexpNodeNormal = new ExprRegexpNode(true);
        regexpNodeNormal.addChildNode(new SupportExprNode("sx"));
        regexpNodeNormal.addChildNode(new SupportExprNode(4));
        tryInvalidValidate(regexpNodeNormal);

        // test numeric supported
        regexpNodeNormal = new ExprRegexpNode(false);
        regexpNodeNormal.addChildNode(new SupportExprNode(new Integer(4)));
        regexpNodeNormal.addChildNode(new SupportExprNode("sx"));
    }

    public void testEvaluate() throws Exception
    {
        assertFalse((Boolean) regexpNodeNormal.evaluate(makeEvent("bcd"), false, null));
        assertTrue((Boolean) regexpNodeNormal.evaluate(makeEvent("ab"), false, null));
        assertTrue((Boolean) regexpNodeNot.evaluate(makeEvent("bcd"), false, null));
        assertFalse((Boolean) regexpNodeNot.evaluate(makeEvent("ab"), false, null));
    }

    public void testEquals()  throws Exception
    {
        ExprRegexpNode otherRegexpNodeNot = SupportExprNodeFactory.makeRegexpNode(true);

        assertTrue(regexpNodeNot.equalsNode(otherRegexpNodeNot));
        assertFalse(regexpNodeNormal.equalsNode(otherRegexpNodeNot));
    }

    public void testToExpressionString() throws Exception
    {
        assertEquals("s0.theString regexp \"[a-z][a-z]\"", regexpNodeNormal.toExpressionString());
        assertEquals("s0.theString not regexp \"[a-z][a-z]\"", regexpNodeNot.toExpressionString());
    }

    private EventBean[] makeEvent(String stringValue)
    {
        SupportBean theEvent = new SupportBean();
        theEvent.setTheString(stringValue);
        return new EventBean[] {SupportEventBeanFactory.createObject(theEvent)};
    }

    private void tryInvalidValidate(ExprRegexpNode exprLikeRegexpNode) throws Exception
    {
        try {
            exprLikeRegexpNode.validate(ExprValidationContextFactory.makeEmpty());
            fail();
        }
        catch (ExprValidationException ex)
        {
            // expected
        }
    }
}
