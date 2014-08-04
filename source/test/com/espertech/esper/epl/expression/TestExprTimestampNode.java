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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.core.service.ExpressionResultCacheService;
import com.espertech.esper.epl.script.AgentInstanceScriptContext;
import com.espertech.esper.core.service.StatementAgentInstanceLock;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.support.epl.SupportExprNode;
import junit.framework.TestCase;

public class TestExprTimestampNode extends TestCase
{
    private ExprTimestampNode node;
    private ExprEvaluatorContext context;

    public void setUp()
    {
        node = new ExprTimestampNode();
    }

    public void testGetType() throws Exception
    {
        assertEquals(Long.class, node.getType());
    }

    public void testValidate() throws Exception
    {
        // Test too many nodes
        node.addChildNode(new SupportExprNode(1));
        try
        {
            node.validate(ExprValidationContextFactory.makeEmpty());
            fail();
        }
        catch (ExprValidationException ex)
        {
            // Expected
        }
    }

    public void testEvaluate() throws Exception
    {
        final TimeProvider provider = new TimeProvider()
        {
            public long getTime()
            {
                return 99;
            }
        };
        context = new ExprEvaluatorContext() {
            public TimeProvider getTimeProvider() {
                return provider;
            }

            public ExpressionResultCacheService getExpressionResultCacheService() {
                return null;
            }

            public int getAgentInstanceId() {
                return -1;
            }

            public EventBean getContextProperties() {
                return null;
            }

            public AgentInstanceScriptContext getAgentInstanceScriptContext() {
                return null;
            }

            public String getStatementName() {
                return null;
            }

            public String getEngineURI() {
                return null;
            }

            public String getStatementId() {
                return null;
            }

            public StatementAgentInstanceLock getAgentInstanceLock() {
                return null;
            }
        };
        node.validate(new ExprValidationContext(null, null, null, provider, null, null, null, null, null, null, null));
        assertEquals(99L, node.evaluate(null, false, context));
    }

    public void testEquals() throws Exception
    {
        assertFalse(node.equalsNode(new ExprEqualsNodeImpl(true, false)));
        assertTrue(node.equalsNode(new ExprTimestampNode()));
    }

    public void testToExpressionString() throws Exception
    {
        assertEquals("current_timestamp()", node.toExpressionString());
    }
}
