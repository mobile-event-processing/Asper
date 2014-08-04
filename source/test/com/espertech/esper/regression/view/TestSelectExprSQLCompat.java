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

import com.espertech.esper.client.*;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.client.SupportConfigFactory;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestSelectExprSQLCompat extends TestCase
{
    private SupportUpdateListener testListener;
    private Configuration config;

    public void setUp()
    {
        testListener = new SupportUpdateListener();
        config = SupportConfigFactory.getConfiguration();
        config.addEventType("SupportBean", SupportBean.class);
    }

    protected void tearDown() throws Exception {
        testListener = null;
        config = null;
    }

    public void testQualifiedPropertyNamed()
    {
        EPServiceProvider epService = EPServiceProviderManager.getProvider("default", config);
        epService.initialize();
        runAssertionProperty(epService);
        runAssertionPrefixStream(epService);

        epService.destroy();
    }

    public void testQualifiedPropertyUnnamed()
    {
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
        runAssertionProperty(epService);
        runAssertionPrefixStream(epService);
    }

    private void runAssertionProperty(EPServiceProvider engine)
    {
        String epl = "select default.SupportBean.theString as val1, SupportBean.intPrimitive as val2 from SupportBean";
        EPStatement selectTestView = engine.getEPAdministrator().createEPL(epl);
        selectTestView.addListener(testListener);

        sendEvent(engine, "E1", 10);
        EventBean received = testListener.getAndResetLastNewData()[0];
        assertEquals("E1", received.get("val1"));
        assertEquals(10, received.get("val2"));
    }

    // Test stream name prefixed by engine URI
    private void runAssertionPrefixStream(EPServiceProvider engine)
    {
        String epl = "select theString from default.SupportBean";
        EPStatement selectTestView = engine.getEPAdministrator().createEPL(epl);
        selectTestView.addListener(testListener);

        sendEvent(engine, "E1", 10);
        EventBean received = testListener.getAndResetLastNewData()[0];
        assertEquals("E1", received.get("theString"));
    }

    private void sendEvent(EPServiceProvider engine, String s, int intPrimitive)
    {
        SupportBean bean = new SupportBean(s, intPrimitive);
        engine.getEPRuntime().sendEvent(bean);
    }

    private static final Log log = LogFactory.getLog(TestSelectExpr.class);
}
