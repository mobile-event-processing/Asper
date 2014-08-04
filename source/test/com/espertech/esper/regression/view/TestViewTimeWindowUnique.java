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
import com.espertech.esper.support.bean.SupportMarketDataBean;
import com.espertech.esper.support.client.SupportConfigFactory;

public class TestViewTimeWindowUnique extends TestCase
{
    private EPServiceProvider epService;
    private SupportUpdateListener listener;

    public void setUp()
    {
        listener = new SupportUpdateListener();
        Configuration config = SupportConfigFactory.getConfiguration();
        config.getEngineDefaults().getViewResources().setAllowMultipleExpiryPolicies(true);
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
    }

    protected void tearDown() throws Exception {
        listener = null;
    }

    // Make sure the timer and dispatch works for externally timed events and views
    public void testWindowUnique()
    {
        // Set up a time window with a unique view attached
        EPStatement windowUniqueView = epService.getEPAdministrator().createEPL(
                "select irstream * from " + SupportMarketDataBean.class.getName() +
                ".win:time(3.0).std:unique(symbol)");
        windowUniqueView.addListener(listener);

        sendTimer(0);

        sendEvent("IBM");

        assertNull(listener.getLastOldData());
        sendTimer(4000);
        assertEquals(1, listener.getLastOldData().length);
    }

    // Make sure the timer and dispatch works for externally timed events and views
    public void testWindowUniqueMultiKey()
    {
        sendTimer(0);

        // Set up a time window with a unique view attached
        EPStatement windowUniqueView = epService.getEPAdministrator().createEPL(
                "select irstream * from " + SupportMarketDataBean.class.getName() +
                ".win:time(3.0).std:unique(symbol, price)");
        windowUniqueView.addListener(listener);
        String[] fields = new String[] {"symbol", "price", "volume"};

        sendEvent("IBM", 10, 1L);
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{"IBM", 10.0, 1L});

        sendEvent("IBM", 11, 2L);
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{"IBM", 11.0, 2L});

        sendEvent("IBM", 10, 3L);
        EPAssertionUtil.assertProps(listener.getLastNewData()[0], fields, new Object[]{"IBM", 10.0, 3L});
        EPAssertionUtil.assertProps(listener.getLastOldData()[0], fields, new Object[]{"IBM", 10.0, 1L});
        listener.reset();

        sendEvent("IBM", 11, 4L);
        EPAssertionUtil.assertProps(listener.getLastNewData()[0], fields, new Object[]{"IBM", 11.0, 4L});
        EPAssertionUtil.assertProps(listener.getLastOldData()[0], fields, new Object[]{"IBM", 11.0, 2L});
        listener.reset();

        sendTimer(2000);
        sendEvent(null, 11, 5L);
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{null, 11.0, 5L});

        sendTimer(3000);
        assertEquals(2, listener.getLastOldData().length);
        EPAssertionUtil.assertProps(listener.getLastOldData()[0], fields, new Object[]{"IBM", 10.0, 3L});
        EPAssertionUtil.assertProps(listener.getLastOldData()[1], fields, new Object[]{"IBM", 11.0, 4L});
        listener.reset();

        sendEvent(null, 11, 6L);
        EPAssertionUtil.assertProps(listener.getLastNewData()[0], fields, new Object[]{null, 11.0, 6L});
        EPAssertionUtil.assertProps(listener.getLastOldData()[0], fields, new Object[]{null, 11.0, 5L});
        listener.reset();

        sendTimer(6000);
        assertEquals(1, listener.getLastOldData().length);
        EPAssertionUtil.assertProps(listener.getLastOldData()[0], fields, new Object[]{null, 11.0, 6L});
        listener.reset();
    }

    private void sendEvent(String symbol)
    {
        SupportMarketDataBean theEvent = new SupportMarketDataBean(symbol, 0, 0L, "");
        epService.getEPRuntime().sendEvent(theEvent);
    }

    private void sendEvent(String symbol, double price, Long volume)
    {
        SupportMarketDataBean theEvent = new SupportMarketDataBean(symbol, price, volume, "");
        epService.getEPRuntime().sendEvent(theEvent);
    }

    private void sendTimer(long time)
    {
        CurrentTimeEvent theEvent = new CurrentTimeEvent(time);
        EPRuntime runtime = epService.getEPRuntime();
        runtime.sendEvent(theEvent);
    }
}
