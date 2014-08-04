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

import com.espertech.esper.support.epl.SupportPluginAggregationMethodOne;
import com.espertech.esper.type.MinMaxTypeEnum;
import junit.framework.TestCase;

public class TestExprPlugInAggFunctionNode extends TestCase
{
    private ExprPlugInAggFunctionNode plugInNode;

    public void setUp()
    {
        plugInNode = new ExprPlugInAggFunctionNode(false, new SupportPluginAggregationMethodOne(), "matrix");
    }

    public void testGetType() throws Exception
    {
        plugInNode.validate(ExprValidationContextFactory.makeEmpty());
        assertEquals(int.class, plugInNode.getType());
    }

    public void testEqualsNode() throws Exception
    {
        ExprPlugInAggFunctionNode otherOne = new ExprPlugInAggFunctionNode(false, new SupportPluginAggregationMethodOne(), "matrix");
        ExprPlugInAggFunctionNode otherTwo = new ExprPlugInAggFunctionNode(false, new SupportPluginAggregationMethodOne(), "matrix2");

        assertTrue(plugInNode.equalsNode(plugInNode));
        assertFalse(plugInNode.equalsNode(new ExprMinMaxRowNode(MinMaxTypeEnum.MIN)));
        assertTrue(otherOne.equalsNode(plugInNode));
        assertFalse(otherTwo.equalsNode(plugInNode));
    }
}
