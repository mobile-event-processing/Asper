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
import com.espertech.esper.client.hook.VirtualDataWindow;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.core.service.EPServiceProviderSPI;
import com.espertech.esper.epl.lookup.SubordTableLookupStrategy;
import com.espertech.esper.epl.named.NamedWindowProcessor;
import com.espertech.esper.epl.named.NamedWindowProcessorInstance;
import com.espertech.esper.epl.virtualdw.VirtualDataWindowLookupContextSPI;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.bean.SupportBean_S0;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.support.virtualdw.SupportVirtualDW;
import com.espertech.esper.support.virtualdw.SupportVirtualDWFactory;
import junit.framework.TestCase;

import javax.naming.NamingException;
import java.util.Collection;
import java.util.Collections;

public class TestVirtualDataWindowToLookup extends TestCase {

    private EPServiceProvider epService;
    private EPServiceProviderSPI spi;
    private SupportUpdateListener listener;

    public void setUp()
    {
        listener = new SupportUpdateListener();

        Configuration configuration = SupportConfigFactory.getConfiguration();
        configuration.addPlugInVirtualDataWindow("test", "vdw", SupportVirtualDWFactory.class.getName());
        configuration.addEventType("SupportBean", SupportBean.class);
        configuration.addEventType("SupportBean_S0", SupportBean_S0.class);
        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        epService.initialize();
        spi = (EPServiceProviderSPI) epService;
    }

    public void tearDown()
    {
        spi = null;
        listener = null;
    }

    public void testLateConsumerNoIterate() throws Exception {

        // client-side
        epService.getEPAdministrator().createEPL("create window MyVDW.test:vdw() as SupportBean");
        SupportVirtualDW window = (SupportVirtualDW) getFromContext("/virtualdw/MyVDW");
        SupportBean supportBean = new SupportBean("E1", 100);
        window.setData(Collections.singleton(supportBean));

        EPStatement stmt = epService.getEPAdministrator().createEPL("select (select sum(intPrimitive) from MyVDW vdw where vdw.theString = s0.p00) from SupportBean_S0 s0");
        stmt.addListener(listener);
        VirtualDataWindowLookupContextSPI spiContext = (VirtualDataWindowLookupContextSPI) window.getLastRequestedIndex();

        // CM side
        epService.getEPAdministrator().createEPL("create window MyWin.std:unique(theString) as SupportBean");
        epService.getEPAdministrator().createEPL("insert into MyWin select * from SupportBean");
        NamedWindowProcessor processor = spi.getNamedWindowService().getProcessor("MyWin");
        NamedWindowProcessorInstance processorInstance = processor.getProcessorInstance(null);
        SubordTableLookupStrategy strategy = processorInstance.getRootViewInstance().getAddSubqueryLookupStrategy("ABC", "001", null, spiContext.getOuterTypePerStream(), spiContext.getJoinDesc(), spiContext.isForceTableScan(), 0, null);
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 200));

        // trigger
        epService.getEPRuntime().sendEvent(new SupportBean_S0(1, "E2"));
        EventBean[] outerEvents = window.getLastAccessEvents();
        Collection<EventBean> result = strategy.lookup(outerEvents, null);
        assertTrue(!result.isEmpty());
    }

    private VirtualDataWindow getFromContext(String name) {
        try {
            return (VirtualDataWindow) epService.getContext().lookup(name);
        }
        catch (Exception e) {
            throw new RuntimeException("Name '" + name + "' could not be looked up");
        }
    }
}
