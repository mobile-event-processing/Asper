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

public class TestExprInNode extends TestCase
{
    private ExprInNode inNodeNormal;
    private ExprInNode inNodeNotIn;

    public void setUp() throws Exception
    {
        inNodeNormal = SupportExprNodeFactory.makeInSetNode(false);
        inNodeNotIn = SupportExprNodeFactory.makeInSetNode(true);
    }

    public void testGetType()  throws Exception
    {
        assertEquals(Boolean.class, inNodeNormal.getExprEvaluator().getType());
        assertEquals(Boolean.class, inNodeNotIn.getExprEvaluator().getType());
    }

    public void testValidate() throws Exception
    {
        inNodeNormal = SupportExprNodeFactory.makeInSetNode(true);
        inNodeNormal.validate(ExprValidationContextFactory.makeEmpty());

        // No subnodes: Exception is thrown.
        tryInvalidValidate(new ExprInNodeImpl(true));

        // singe child node not possible, must be 2 at least
        inNodeNormal = new ExprInNodeImpl(true);
        inNodeNormal.addChildNode(new SupportExprNode(new Integer(4)));
        tryInvalidValidate(inNodeNormal);

        // test a type mismatch
        inNodeNormal = new ExprInNodeImpl(true);
        inNodeNormal.addChildNode(new SupportExprNode("sx"));
        inNodeNormal.addChildNode(new SupportExprNode(4));
        tryInvalidValidate(inNodeNormal);
    }

    public void testEvaluate() throws Exception
    {
        assertFalse((Boolean) inNodeNormal.evaluate(makeEvent(0), false, null));
        assertTrue((Boolean) inNodeNormal.evaluate(makeEvent(1), false, null));
        assertTrue((Boolean) inNodeNormal.evaluate(makeEvent(2), false, null));
        assertFalse((Boolean) inNodeNormal.evaluate(makeEvent(3), false, null));

        assertTrue((Boolean) inNodeNotIn.evaluate(makeEvent(0), false, null));
        assertFalse((Boolean) inNodeNotIn.evaluate(makeEvent(1), false, null));
        assertFalse((Boolean) inNodeNotIn.evaluate(makeEvent(2), false, null));
        assertTrue((Boolean) inNodeNotIn.evaluate(makeEvent(3), false, null));
    }

    public void testEquals()  throws Exception
    {
        ExprInNode otherInNodeNormal = SupportExprNodeFactory.makeInSetNode(false);
        ExprInNode otherInNodeNotIn = SupportExprNodeFactory.makeInSetNode(true);

        assertTrue(inNodeNormal.equalsNode(otherInNodeNormal));
        assertTrue(inNodeNotIn.equalsNode(otherInNodeNotIn));

        assertFalse(inNodeNormal.equalsNode(otherInNodeNotIn));
        assertFalse(inNodeNotIn.equalsNode(otherInNodeNormal));
        assertFalse(inNodeNotIn.equalsNode(SupportExprNodeFactory.makeCaseSyntax1Node()));
        assertFalse(inNodeNormal.equalsNode(SupportExprNodeFactory.makeCaseSyntax1Node()));
    }

    public void testToExpressionString() throws Exception
    {
        assertEquals("s0.intPrimitive in (1,2)", inNodeNormal.toExpressionString());
        assertEquals("s0.intPrimitive not in (1,2)", inNodeNotIn.toExpressionString());
    }

    private EventBean[] makeEvent(int intPrimitive)
    {
        SupportBean theEvent = new SupportBean();
        theEvent.setIntPrimitive(intPrimitive);
        return new EventBean[] {SupportEventBeanFactory.createObject(theEvent)};
    }

    private void tryInvalidValidate(ExprInNode exprInNode) throws Exception
    {
        try {
            exprInNode.validate(ExprValidationContextFactory.makeEmpty());
            fail();
        }
        catch (ExprValidationException ex)
        {
            // expected
        }
    }
}
