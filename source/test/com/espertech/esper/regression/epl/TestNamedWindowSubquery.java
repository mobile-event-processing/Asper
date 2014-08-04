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

public class TestNamedWindowSubquery extends TestCase
{
    private EPServiceProvider epService;
    private SupportUpdateListener listenerWindow;
    private SupportUpdateListener listenerStmtOne;
    private SupportUpdateListener listenerStmtTwo;
    private SupportUpdateListener listenerStmtDelete;

    public void setUp()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        config.getEngineDefaults().getLogging().setEnableQueryPlan(true);
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

    public void testSubqueryTwoConsumerWindow() throws Exception {
        String epl =
            "\n create window MyWindowTwo.win:length(1) as (mycount long);" +
            "\n @Name('insert-count') insert into MyWindowTwo select 1L as mycount from SupportBean;" +
            "\n create variable long myvar = 0;" +
            "\n @Name('assign') on MyWindowTwo set myvar = (select mycount from MyWindowTwo);";
        EPServiceProvider engine = EPServiceProviderManager.getDefaultProvider();
        engine.getEPAdministrator().getConfiguration().addEventType(SupportBean.class);

        engine.getEPAdministrator().getDeploymentAdmin().parseDeploy(epl);

        engine.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        assertEquals(1L, engine.getEPRuntime().getVariableValue("myvar"));   // if the subquery-consumer executes first, this will be null
    }

    public void testSubqueryLateConsumerAggregation() {
        epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as SupportBean");
        epService.getEPAdministrator().createEPL("insert into MyWindow select * from SupportBean");

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 1));

        EPStatement stmt = epService.getEPAdministrator().createEPL("select * from MyWindow where (select count(*) from MyWindow) > 0");
        stmt.addListener(listenerStmtOne);
        
        epService.getEPRuntime().sendEvent(new SupportBean("E3", 1));
        assertTrue(listenerStmtOne.isInvoked());
    }

    public void testSubquerySelfCheck()
    {
        String fields[] = new String[] {"key", "value"};

        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as select theString as key, intBoxed as value from " + SupportBean.class.getName();
        EPStatement stmtCreate = epService.getEPAdministrator().createEPL(stmtTextCreate);
        stmtCreate.addListener(listenerWindow);

        // create insert into (not does insert if key already exists)
        String stmtTextInsertOne = "insert into MyWindow select theString as key, intBoxed as value from " + SupportBean.class.getName() + " as s0" +
                                    " where not exists (select * from MyWindow as win where win.key = s0.theString)";
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        sendSupportBean("E1", 1);
        EPAssertionUtil.assertProps(listenerWindow.assertOneGetNewAndReset(), fields, new Object[]{"E1", 1});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}});

        sendSupportBean("E2", 2);
        EPAssertionUtil.assertProps(listenerWindow.assertOneGetNewAndReset(), fields, new Object[]{"E2", 2});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}, {"E2", 2}});

        sendSupportBean("E1", 3);
        assertFalse(listenerWindow.isInvoked());
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}, {"E2", 2}});

        sendSupportBean("E3", 4);
        EPAssertionUtil.assertProps(listenerWindow.assertOneGetNewAndReset(), fields, new Object[]{"E3", 4});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}, {"E2", 2}, {"E3", 4}});

        // Add delete
        String stmtTextDelete = "on " + SupportBean_A.class.getName() + " delete from MyWindow where key = id";
        EPStatement stmtDelete = epService.getEPAdministrator().createEPL(stmtTextDelete);
        stmtDelete.addListener(listenerStmtDelete);

        // delete E2
        epService.getEPRuntime().sendEvent(new SupportBean_A("E2"));
        EPAssertionUtil.assertProps(listenerWindow.assertOneGetOldAndReset(), fields, new Object[]{"E2", 2});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}, {"E3", 4}});

        sendSupportBean("E2", 5);
        EPAssertionUtil.assertProps(listenerWindow.assertOneGetNewAndReset(), fields, new Object[]{"E2", 5});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}, {"E3", 4}, {"E2", 5}});
    }

    public void testSubqueryDeleteInsertReplace()
    {
        String fields[] = new String[] {"key", "value"};

        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as select theString as key, intBoxed as value from " + SupportBean.class.getName();
        EPStatement stmtCreate = epService.getEPAdministrator().createEPL(stmtTextCreate);
        stmtCreate.addListener(listenerWindow);

        // delete
        String stmtTextDelete = "on " + SupportBean.class.getName() + " delete from MyWindow where key = theString";
        EPStatement stmtDelete = epService.getEPAdministrator().createEPL(stmtTextDelete);
        stmtDelete.addListener(listenerStmtDelete);

        // create insert into
        String stmtTextInsertOne = "insert into MyWindow select theString as key, intBoxed as value from " + SupportBean.class.getName() + " as s0";
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        sendSupportBean("E1", 1);
        EPAssertionUtil.assertProps(listenerWindow.assertOneGetNewAndReset(), fields, new Object[]{"E1", 1});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}});

        sendSupportBean("E2", 2);
        EPAssertionUtil.assertProps(listenerWindow.assertOneGetNewAndReset(), fields, new Object[]{"E2", 2});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}, {"E2", 2}});

        sendSupportBean("E1", 3);
        assertEquals(2, listenerWindow.getNewDataList().size());
        EPAssertionUtil.assertProps(listenerWindow.getOldDataList().get(0)[0], fields, new Object[]{"E1", 1});
        EPAssertionUtil.assertProps(listenerWindow.getNewDataList().get(1)[0], fields, new Object[]{"E1", 3});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E2", 2}, {"E1", 3}});
    }

    public void testInvalidSubquery()
    {
        epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as " + SupportBean.class.getName());
        try
        {
            epService.getEPAdministrator().createEPL("select (select theString from MyWindow.std:lastevent()) from MyWindow");
            fail();
        }
        catch (EPException ex)
        {
            assertEquals("Error starting statement: Consuming statements to a named window cannot declare a data window view onto the named window [select (select theString from MyWindow.std:lastevent()) from MyWindow]", ex.getMessage());
        }
    }

    public void testUncorrelatedSubqueryAggregation()
    {
        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as select theString as a, longPrimitive as b from " + SupportBean.class.getName();
        EPStatement stmtCreate = epService.getEPAdministrator().createEPL(stmtTextCreate);
        stmtCreate.addListener(listenerWindow);

        // create insert into
        String stmtTextInsertOne = "insert into MyWindow select theString as a, longPrimitive as b from " + SupportBean.class.getName();
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        // create consumer
        String stmtTextSelectOne = "select irstream (select sum(b) from MyWindow) as value, symbol from " + SupportMarketDataBean.class.getName();
        EPStatement stmtSelectOne = epService.getEPAdministrator().createEPL(stmtTextSelectOne);
        stmtSelectOne.addListener(listenerStmtOne);

        sendMarketBean("M1");
        String fieldsStmt[] = new String[] {"value", "symbol"};
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fieldsStmt, new Object[]{null, "M1"});

        sendSupportBean("S1", 5L, -1L);
        sendMarketBean("M2");
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fieldsStmt, new Object[]{5L, "M2"});

        sendSupportBean("S2", 10L, -1L);
        sendMarketBean("M3");
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fieldsStmt, new Object[]{15L, "M3"});

        // create 2nd consumer
        EPStatement stmtSelectTwo = epService.getEPAdministrator().createEPL(stmtTextSelectOne); // same stmt
        stmtSelectTwo.addListener(listenerStmtTwo);

        sendSupportBean("S3", 8L, -1L);
        sendMarketBean("M4");
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fieldsStmt, new Object[]{23L, "M4"});
        EPAssertionUtil.assertProps(listenerStmtTwo.assertOneGetNewAndReset(), fieldsStmt, new Object[]{23L, "M4"});
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

    private SupportBean sendSupportBean(String theString, int intBoxed)
    {
        SupportBean bean = new SupportBean();
        bean.setTheString(theString);
        bean.setIntBoxed(intBoxed);
        epService.getEPRuntime().sendEvent(bean);
        return bean;
    }

    private void sendMarketBean(String symbol)
    {
        SupportMarketDataBean bean = new SupportMarketDataBean(symbol, 0, 0l, "");
        epService.getEPRuntime().sendEvent(bean);
    }
}
