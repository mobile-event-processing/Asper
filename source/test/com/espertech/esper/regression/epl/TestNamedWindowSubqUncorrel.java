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
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.bean.SupportBean_A;
import com.espertech.esper.support.bean.SupportBean_S0;
import com.espertech.esper.support.bean.SupportMarketDataBean;
import com.espertech.esper.support.client.SupportConfigFactory;
import junit.framework.TestCase;

public class TestNamedWindowSubqUncorrel extends TestCase
{
    private EPServiceProvider epService;
    private SupportUpdateListener listenerWindow;
    private SupportUpdateListener listenerStmtOne;
    private SupportUpdateListener listenerStmtTwo;
    private SupportUpdateListener listenerStmtDelete;

    public void setUp()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);
        epService.getEPAdministrator().getConfiguration().addEventType("ABean", SupportBean_S0.class);
        listenerWindow = new SupportUpdateListener();
        listenerStmtOne = new SupportUpdateListener();
        listenerStmtTwo = new SupportUpdateListener();
        listenerStmtDelete = new SupportUpdateListener();
    }

    protected void tearDown() throws Exception {
        listenerWindow = null;
        listenerStmtOne = null;
        listenerStmtTwo = null;
        listenerStmtDelete = null;
    }

    public void testNoShare() {
        runAssertion(false, false);
    }

    public void testShare() {
        runAssertion(true, false);
    }

    public void testDisableShare() {
        runAssertion(true, true);
    }

    private void runAssertion(boolean enableIndexShareCreate, boolean disableIndexShareConsumer)
    {
        String stmtTextCreate = "create window MyWindow.win:keepall() as select theString as a, longPrimitive as b, longBoxed as c from " + SupportBean.class.getName();
        if (enableIndexShareCreate) {
            stmtTextCreate = "@Hint('enable_window_subquery_indexshare') " + stmtTextCreate;
        }
        // create window
        EPStatement stmtCreate = epService.getEPAdministrator().createEPL(stmtTextCreate);
        stmtCreate.addListener(listenerWindow);

        // create insert into
        String stmtTextInsertOne = "insert into MyWindow select theString as a, longPrimitive as b, longBoxed as c from " + SupportBean.class.getName();
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        // create consumer
        String stmtTextSelectOne = "select irstream (select a from MyWindow) as value, symbol from " + SupportMarketDataBean.class.getName();
        if (disableIndexShareConsumer) {
            stmtTextSelectOne = "@Hint('disable_window_subquery_indexshare') " + stmtTextSelectOne;
        }
        EPStatement stmtSelectOne = epService.getEPAdministrator().createEPL(stmtTextSelectOne);
        stmtSelectOne.addListener(listenerStmtOne);
        EPAssertionUtil.assertEqualsAnyOrder(stmtSelectOne.getEventType().getPropertyNames(), new String[]{"value", "symbol"});
        assertEquals(String.class, stmtSelectOne.getEventType().getPropertyType("value"));
        assertEquals(String.class, stmtSelectOne.getEventType().getPropertyType("symbol"));

        sendMarketBean("M1");
        String fieldsStmt[] = new String[] {"value", "symbol"};
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fieldsStmt, new Object[]{null, "M1"});

        sendSupportBean("S1", 1L, 2L);
        assertFalse(listenerStmtOne.isInvoked());
        String fieldsWin[] = new String[] {"a", "b", "c"};
        EPAssertionUtil.assertProps(listenerWindow.assertOneGetNewAndReset(), fieldsWin, new Object[]{"S1", 1L, 2L});

        // create consumer 2 -- note that this one should not start empty now
        String stmtTextSelectTwo = "select irstream (select a from MyWindow) as value, symbol from " + SupportMarketDataBean.class.getName();
        if (disableIndexShareConsumer) {
            stmtTextSelectTwo = "@Hint('disable_window_subquery_indexshare') " + stmtTextSelectTwo;
        }
        EPStatement stmtSelectTwo = epService.getEPAdministrator().createEPL(stmtTextSelectTwo);
        stmtSelectTwo.addListener(listenerStmtTwo);

        sendMarketBean("M1");
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fieldsStmt, new Object[]{"S1", "M1"});
        EPAssertionUtil.assertProps(listenerStmtTwo.assertOneGetNewAndReset(), fieldsStmt, new Object[]{"S1", "M1"});

        sendSupportBean("S2", 10L, 20L);
        assertFalse(listenerStmtOne.isInvoked());
        EPAssertionUtil.assertProps(listenerWindow.assertOneGetNewAndReset(), fieldsWin, new Object[]{"S2", 10L, 20L});

        sendMarketBean("M2");
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fieldsStmt, new Object[]{null, "M2"});
        assertFalse(listenerWindow.isInvoked());
        EPAssertionUtil.assertProps(listenerStmtTwo.assertOneGetNewAndReset(), fieldsStmt, new Object[]{null, "M2"});

        // create delete stmt
        String stmtTextDelete = "on " + SupportBean_A.class.getName() + " delete from MyWindow where id = a";
        EPStatement stmtDelete = epService.getEPAdministrator().createEPL(stmtTextDelete);
        stmtDelete.addListener(listenerStmtDelete);

        // delete S1
        epService.getEPRuntime().sendEvent(new SupportBean_A("S1"));
        EPAssertionUtil.assertProps(listenerWindow.assertOneGetOldAndReset(), fieldsWin, new Object[]{"S1", 1L, 2L});

        sendMarketBean("M3");
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fieldsStmt, new Object[]{"S2", "M3"});
        EPAssertionUtil.assertProps(listenerStmtTwo.assertOneGetNewAndReset(), fieldsStmt, new Object[]{"S2", "M3"});

        // delete S2
        epService.getEPRuntime().sendEvent(new SupportBean_A("S2"));
        EPAssertionUtil.assertProps(listenerWindow.assertOneGetOldAndReset(), fieldsWin, new Object[]{"S2", 10L, 20L});

        sendMarketBean("M4");
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fieldsStmt, new Object[]{null, "M4"});
        EPAssertionUtil.assertProps(listenerStmtTwo.assertOneGetNewAndReset(), fieldsStmt, new Object[]{null, "M4"});

        sendSupportBean("S3", 100L, 200L);
        EPAssertionUtil.assertProps(listenerWindow.assertOneGetNewAndReset(), fieldsWin, new Object[]{"S3", 100L, 200L});

        sendMarketBean("M5");
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fieldsStmt, new Object[]{"S3", "M5"});
        EPAssertionUtil.assertProps(listenerStmtTwo.assertOneGetNewAndReset(), fieldsStmt, new Object[]{"S3", "M5"});
        epService.getEPAdministrator().destroyAllStatements();
    }

    private SupportBean sendSupportBean(String theString, long longPrimitive, Long longBoxed)
    {
        SupportBean bean = new SupportBean();
        bean.setTheString(theString);
        bean.setLongPrimitive(longPrimitive);
        bean.setLongBoxed(longBoxed);
        epService.getEPRuntime().sendEvent(bean);
        return bean;
    }

    private void sendMarketBean(String symbol)
    {
        SupportMarketDataBean bean = new SupportMarketDataBean(symbol, 0, 0l, "");
        epService.getEPRuntime().sendEvent(bean);
    }
}
