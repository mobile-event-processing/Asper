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

package com.espertech.esper.regression.client;

import com.espertech.esper.client.*;
import com.espertech.esper.regression.event.SupportXML;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.support.epl.SupportStaticMethodLib;
import com.espertech.esper.core.service.EPServiceProviderSPI;
import junit.framework.TestCase;

import java.util.HashMap;

public class TestThreadedConfigInbound extends TestCase
{
    public void testOp() throws Exception
    {
        Configuration config = new Configuration();
        config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
        config.getEngineDefaults().getThreading().setThreadPoolInbound(true);
        config.getEngineDefaults().getThreading().setThreadPoolInboundNumThreads(4);
        config.getEngineDefaults().getExpression().setUdfCache(false);
        config.addEventType("MyMap", new HashMap<String, Object>());
        config.addEventType("SupportBean", SupportBean.class);
        config.addImport(SupportStaticMethodLib.class.getName());

        ConfigurationEventTypeXMLDOM xmlDOMEventTypeDesc = new ConfigurationEventTypeXMLDOM();
        xmlDOMEventTypeDesc.setRootElementName("myevent");
        config.addEventType("XMLType", xmlDOMEventTypeDesc);

        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();

        SupportListenerTimerHRes listenerOne = new SupportListenerTimerHRes();
        SupportListenerTimerHRes listenerTwo = new SupportListenerTimerHRes();
        SupportListenerTimerHRes listenerThree = new SupportListenerTimerHRes();
        EPStatement stmtOne = epService.getEPAdministrator().createEPL("select SupportStaticMethodLib.sleep(100) from MyMap");
        stmtOne.addListener(listenerOne);
        EPStatement stmtTwo = epService.getEPAdministrator().createEPL("select SupportStaticMethodLib.sleep(100) from SupportBean");
        stmtTwo.addListener(listenerTwo);
        EPStatement stmtThree = epService.getEPAdministrator().createEPL("select SupportStaticMethodLib.sleep(100) from XMLType");
        stmtThree.addListener(listenerThree);

        EventSender senderOne = epService.getEPRuntime().getEventSender("MyMap");
        EventSender senderTwo = epService.getEPRuntime().getEventSender("SupportBean");
        EventSender senderThree = epService.getEPRuntime().getEventSender("XMLType");

        long start = System.nanoTime();
        for (int i = 0; i < 2; i++)
        {
            epService.getEPRuntime().sendEvent(new HashMap<String, Object>(), "MyMap");
            senderOne.sendEvent(new HashMap<String, Object>());
            epService.getEPRuntime().sendEvent(new SupportBean());
            senderTwo.sendEvent(new SupportBean());
            epService.getEPRuntime().sendEvent(SupportXML.getDocument("<myevent/>"));
            senderThree.sendEvent(SupportXML.getDocument("<myevent/>"));
        }
        long end = System.nanoTime();
        long delta = (end - start) / 1000000;
        assertTrue(delta < 500);

        Thread.sleep(1000);
        assertEquals(4, listenerOne.getNewEvents().size());
        assertEquals(4, listenerTwo.getNewEvents().size());
        assertEquals(4, listenerThree.getNewEvents().size());

        EPServiceProviderSPI spi = (EPServiceProviderSPI) epService;
        assertEquals(0, spi.getThreadingService().getInboundQueue().size());
        assertNotNull(spi.getThreadingService().getInboundThreadPool());

        stmtOne.destroy();
        stmtTwo.destroy();
        stmtThree.destroy();

        epService.destroy();
    }
}
