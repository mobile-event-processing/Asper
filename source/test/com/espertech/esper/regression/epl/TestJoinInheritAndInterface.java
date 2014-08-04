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

package com.espertech.esper.regression.epl;

import com.espertech.esper.client.scopetest.SupportUpdateListener;
import junit.framework.TestCase;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.support.bean.*;
import com.espertech.esper.support.client.SupportConfigFactory;

public class TestJoinInheritAndInterface extends TestCase
{
    private EPServiceProvider epService;
    private SupportUpdateListener testListener;

    public void setUp()
    {
        epService = EPServiceProviderManager.getDefaultProvider(SupportConfigFactory.getConfiguration());
        epService.initialize();
    }
    
    protected void tearDown() throws Exception {
        testListener = null;
    }

    public void testInterfaceJoin()
    {
        String viewExpr = "select a, b from " +
                ISupportA.class.getName() + ".win:length(10), " +
                ISupportB.class.getName() + ".win:length(10)" +
                " where a = b";

        EPStatement testView = epService.getEPAdministrator().createEPL(viewExpr);
        testListener = new SupportUpdateListener();
        testView.addListener(testListener);

        epService.getEPRuntime().sendEvent(new ISupportAImpl("1", "ab1"));
        epService.getEPRuntime().sendEvent(new ISupportBImpl("2", "ab2"));
        assertFalse(testListener.isInvoked());

        epService.getEPRuntime().sendEvent(new ISupportBImpl("1", "ab3"));
        assertTrue(testListener.isInvoked());
        EventBean theEvent = testListener.getAndResetLastNewData()[0];
        assertEquals("1", theEvent.get("a"));
        assertEquals("1", theEvent.get("b"));
    }
}
