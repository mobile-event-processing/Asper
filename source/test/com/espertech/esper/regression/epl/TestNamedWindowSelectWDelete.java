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

import com.espertech.esper.client.*;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.support.bean.*;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.support.util.IndexBackingTableInfo;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestNamedWindowSelectWDelete extends TestCase implements IndexBackingTableInfo
{
    private static Log log = LogFactory.getLog(TestNamedWindowSelectWDelete.class);

    private EPServiceProvider epService;
    private SupportUpdateListener listener;

    public void setUp()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        config.getEngineDefaults().getLogging().setEnableQueryPlan(true);
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
        listener = new SupportUpdateListener();
    }

    protected void tearDown() throws Exception {
        listener = null;
    }

    public void testWindowAgg() {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);
        epService.getEPAdministrator().getConfiguration().addEventType("S0", SupportBean_S0.class);

        String[] fieldsWin = "theString,intPrimitive".split(",");
        String[] fieldsSelect = "c0".split(",");
        EPStatement stmtWin = epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as SupportBean");
        epService.getEPAdministrator().createEPL("insert into MyWindow select * from SupportBean");
        String eplSelectDelete = "on S0 as s0 " +
                "select and delete (window(win.*)).aggregate(0, (result, value) => result + value.intPrimitive) as c0 " +
                "from MyWindow as win where s0.p00 = win.theString";
        EPStatement stmt = epService.getEPAdministrator().createEPL(eplSelectDelete);
        stmt.addListener(listener);

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 2));
        EPAssertionUtil.assertPropsPerRow(stmtWin.iterator(), fieldsWin, new Object[][]{{"E1", 1}, {"E2", 2}});

        // select and delete bean E1
        epService.getEPRuntime().sendEvent(new SupportBean_S0(100, "E1"));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fieldsSelect, new Object[]{1});
        EPAssertionUtil.assertPropsPerRow(stmtWin.iterator(), fieldsWin, new Object[][]{{"E2", 2}});

        // add some E2 events
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 3));
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 4));
        EPAssertionUtil.assertPropsPerRow(stmtWin.iterator(), fieldsWin, new Object[][]{{"E2", 2}, {"E2", 3}, {"E2", 4}});

        // select and delete beans E2
        epService.getEPRuntime().sendEvent(new SupportBean_S0(101, "E2"));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fieldsSelect, new Object[]{2 + 3 + 4});
        EPAssertionUtil.assertPropsPerRow(stmtWin.iterator(), fieldsWin, new Object[0][]);

        // test SODA
        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(eplSelectDelete);
        assertEquals(eplSelectDelete, model.toEPL());
        EPStatement stmtSD = epService.getEPAdministrator().create(model);
        assertEquals(eplSelectDelete, stmtSD.getText());
    }
}
