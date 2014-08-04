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

package com.espertech.esper.regression.view;

import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import junit.framework.TestCase;
import com.espertech.esper.client.*;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.support.bean.SupportBean;

public class TestViewTimeBatch extends TestCase
{
    private EPServiceProvider epService;
    private SupportUpdateListener listener;

    public void setUp()
    {
        listener = new SupportUpdateListener();
        Configuration config = SupportConfigFactory.getConfiguration();
        config.addEventType("SupportBean", SupportBean.class);
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
    }

    protected void tearDown() throws Exception {
        listener = null;
    }

    public void testStartEagerForceUpdate()
    {
        sendTimer(1000);

        EPStatement stmt = epService.getEPAdministrator().createEPL("select irstream * from SupportBean.win:time_batch(1, \"START_EAGER,FORCE_UPDATE\")");
        stmt.addListener(listener);

        sendTimer(1999);
        assertFalse(listener.getAndClearIsInvoked());
        
        sendTimer(2000);
        assertTrue(listener.getAndClearIsInvoked());

        sendTimer(2999);
        assertFalse(listener.getAndClearIsInvoked());

        sendTimer(3000);
        assertTrue(listener.getAndClearIsInvoked());
        listener.reset();

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        assertFalse(listener.getAndClearIsInvoked());

        sendTimer(4000);
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), "theString".split(","), new Object[]{"E1"});

        sendTimer(5000);
        EPAssertionUtil.assertProps(listener.assertOneGetOldAndReset(), "theString".split(","), new Object[]{"E1"});

        sendTimer(5999);
        assertFalse(listener.getAndClearIsInvoked());

        sendTimer(6000);
        assertTrue(listener.getAndClearIsInvoked());

        sendTimer(7000);
        assertTrue(listener.getAndClearIsInvoked());
    }

    private void sendTimer(long timeInMSec)
    {
        CurrentTimeEvent theEvent = new CurrentTimeEvent(timeInMSec);
        EPRuntime runtime = epService.getEPRuntime();
        runtime.sendEvent(theEvent);
    }    
}
