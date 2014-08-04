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
import com.espertech.esper.client.deploy.DeploymentOptions;
import com.espertech.esper.client.deploy.EPDeploymentAdmin;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.bean.SupportBean_A;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.util.EventRepresentationEnum;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestNamedWindowExecuteQuery extends TestCase
{
    private EPServiceProvider epService;
    private String[] fields = new String[] {"theString", "intPrimitive"};

    // test expressions + current timestamp
    public void setUp()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        config.addEventType("SupportBean", SupportBean.class.getName());
        config.addEventType("SupportBean_A", SupportBean_A.class.getName());
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();

        String namedWin = "create window MyWindow.win:keepall() as select * from SupportBean";
        epService.getEPAdministrator().createEPL(namedWin);
        String insert = "insert into MyWindow select * from SupportBean";
        epService.getEPAdministrator().createEPL(insert);
    }

    public void test3StreamInnerJoin() throws Exception {
        runAssertion3StreamInnerJoin(EventRepresentationEnum.OBJECTARRAY);
        runAssertion3StreamInnerJoin(EventRepresentationEnum.MAP);
        runAssertion3StreamInnerJoin(EventRepresentationEnum.DEFAULT);
    }

    private void runAssertion3StreamInnerJoin(EventRepresentationEnum eventRepresentationEnum) throws Exception {
        Configuration config = new Configuration();
        config.getEngineDefaults().getLogging().setEnableQueryPlan(true);
        EPServiceProvider epService = EPServiceProviderManager.getProvider("ESPER1", config);

        String epl = "" +
        eventRepresentationEnum.getAnnotationText() + " create schema Product (productId string, categoryId string);" +
        eventRepresentationEnum.getAnnotationText() + " create schema Category (categoryId string, owner string);" +
        eventRepresentationEnum.getAnnotationText() + " create schema ProductOwnerDetails (productId string, owner string);" +
        eventRepresentationEnum.getAnnotationText() + " create window WinProduct.win:keepall() as select * from Product;" +
        eventRepresentationEnum.getAnnotationText() + " create window WinCategory.win:keepall() as select * from Category;" +
        eventRepresentationEnum.getAnnotationText() + " create window WinProductOwnerDetails.win:keepall() as select * from ProductOwnerDetails;" +
        "insert into WinProduct select * from Product;" +
        "insert into WinCategory select * from Category;" +
        "insert into WinProductOwnerDetails select * from ProductOwnerDetails;";
        EPDeploymentAdmin dAdmin = epService.getEPAdministrator().getDeploymentAdmin();
        dAdmin.deploy(dAdmin.parse(epl), new DeploymentOptions());

        sendEvent(eventRepresentationEnum, epService, "Product", new String[] {"productId=Product1", "categoryId=Category1"});
        sendEvent(eventRepresentationEnum, epService, "Product", new String[] {"productId=Product2", "categoryId=Category1"});
        sendEvent(eventRepresentationEnum, epService, "Product", new String[] {"productId=Product3", "categoryId=Category1"});
        sendEvent(eventRepresentationEnum, epService, "Category", new String[] {"categoryId=Category1", "owner=Petar"});
        sendEvent(eventRepresentationEnum, epService, "ProductOwnerDetails", new String[] {"productId=Product1", "owner=Petar"});

        String[] fields = "WinProduct.productId".split(",");
        EventBean[] queryResults;
        queryResults = epService.getEPRuntime().executeQuery("" +
                "select WinProduct.productId " +
                " from WinProduct" +
                " inner join WinCategory on WinProduct.categoryId=WinCategory.categoryId" +
                " inner join WinProductOwnerDetails on WinProduct.productId=WinProductOwnerDetails.productId"
                ).getArray();
        EPAssertionUtil.assertPropsPerRow(queryResults, fields, new Object[][]{{"Product1"}});

        queryResults = epService.getEPRuntime().executeQuery("" +
                "select WinProduct.productId " +
                " from WinProduct" +
                " inner join WinCategory on WinProduct.categoryId=WinCategory.categoryId" +
                " inner join WinProductOwnerDetails on WinProduct.productId=WinProductOwnerDetails.productId" +
                " where WinCategory.owner=WinProductOwnerDetails.owner"
                ).getArray();
        EPAssertionUtil.assertPropsPerRow(queryResults, fields, new Object[][]{{"Product1"}});

        queryResults = epService.getEPRuntime().executeQuery("" +
                "select WinProduct.productId " +
                " from WinProduct, WinCategory, WinProductOwnerDetails" +
                " where WinCategory.owner=WinProductOwnerDetails.owner" +
                " and WinProduct.categoryId=WinCategory.categoryId" +
                " and WinProduct.productId=WinProductOwnerDetails.productId"
                ).getArray();
        EPAssertionUtil.assertPropsPerRow(queryResults, fields, new Object[][]{{"Product1"}});

        queryResults = epService.getEPRuntime().executeQuery("" +
                "select WinProduct.productId " +
                " from WinProduct" +
                " inner join WinCategory on WinProduct.categoryId=WinCategory.categoryId" +
                " inner join WinProductOwnerDetails on WinProduct.productId=WinProductOwnerDetails.productId" +
                " having WinCategory.owner=WinProductOwnerDetails.owner"
                ).getArray();
        EPAssertionUtil.assertPropsPerRow(queryResults, fields, new Object[][]{{"Product1"}});

        epService.destroy();
    }
	
    private void sendEvent(EventRepresentationEnum eventRepresentationEnum, EPServiceProvider epService, String eventName, String[] attributes) {
        Map<String, Object> eventMap = new HashMap<String, Object>();
        List<Object> eventObjectArray = new ArrayList<Object>();
        for (String attribute: attributes) {
            String key = attribute.split("=")[0];
            String value = attribute.split("=")[1];
            eventMap.put(key, value);
            eventObjectArray.add(value);
        }
        if (eventRepresentationEnum.isObjectArrayEvent()) {
            epService.getEPRuntime().sendEvent(eventObjectArray.toArray(), eventName);
        }
        else {
            epService.getEPRuntime().sendEvent(eventMap, eventName);
        }
    }

    public void testNamedWindowJoinWhere() throws Exception
    {
        epService.getEPAdministrator().createEPL(EventRepresentationEnum.MAP.getAnnotationText() + " create window Win1.win:keepall() (key String, keyJoin String)");
        epService.getEPAdministrator().createEPL(EventRepresentationEnum.MAP.getAnnotationText() + " create window Win2.win:keepall() (keyJoin String, value double)");
        String queryAgg = "select w1.key, sum(value) from Win1 w1, Win2 w2 WHERE w1.keyJoin = w2.keyJoin GROUP BY w1.key order by w1.key";
        String[] fieldsAgg = "w1.key,sum(value)".split(",");
        String queryNoagg = "select w1.key, w2.value from Win1 w1, Win2 w2 where w1.keyJoin = w2.keyJoin and value = 1 order by w1.key";
        String[] fieldsNoagg = "w1.key,w2.value".split(",");

        EventBean[] result = epService.getEPRuntime().executeQuery(queryAgg).getArray();
        assertEquals(0, result.length);
        result = epService.getEPRuntime().executeQuery(queryNoagg).getArray();
        assertNull(result);

        sendWin1Event("key1", "keyJoin1");

        result = epService.getEPRuntime().executeQuery(queryAgg).getArray();
        assertEquals(0, result.length);
        result = epService.getEPRuntime().executeQuery(queryNoagg).getArray();
        assertNull(result);

        sendWin2Event("keyJoin1", 1d);

        result = epService.getEPRuntime().executeQuery(queryAgg).getArray();
        EPAssertionUtil.assertPropsPerRow(result, fieldsAgg, new Object[][]{{"key1", 1d}});
        result = epService.getEPRuntime().executeQuery(queryNoagg).getArray();
        EPAssertionUtil.assertPropsPerRow(result, fieldsNoagg, new Object[][]{{"key1", 1d}});

        sendWin2Event("keyJoin2", 2d);

        result = epService.getEPRuntime().executeQuery(queryAgg).getArray();
        EPAssertionUtil.assertPropsPerRow(result, fieldsAgg, new Object[][]{{"key1", 1d}});
        result = epService.getEPRuntime().executeQuery(queryNoagg).getArray();
        EPAssertionUtil.assertPropsPerRow(result, fieldsNoagg, new Object[][]{{"key1", 1d}});

        sendWin1Event("key2", "keyJoin2");

        result = epService.getEPRuntime().executeQuery(queryAgg).getArray();
        EPAssertionUtil.assertPropsPerRow(result, fieldsAgg, new Object[][]{{"key1", 1d}, {"key2", 2d}});
        result = epService.getEPRuntime().executeQuery(queryNoagg).getArray();
        EPAssertionUtil.assertPropsPerRow(result, fieldsNoagg, new Object[][]{{"key1", 1d}});

        sendWin2Event("keyJoin2", 1d);

        result = epService.getEPRuntime().executeQuery(queryAgg).getArray();
        EPAssertionUtil.assertPropsPerRow(result, fieldsAgg, new Object[][]{{"key1", 1d}, {"key2", 3d}});
        result = epService.getEPRuntime().executeQuery(queryNoagg).getArray();
        EPAssertionUtil.assertPropsPerRow(result, fieldsNoagg, new Object[][]{{"key1", 1d}, {"key2", 1d}});
    }

    private void sendWin1Event(String key, String keyJoin) {
        Map<String, Object> theEvent = new HashMap<String, Object>();
        theEvent.put("key", key);
        theEvent.put("keyJoin", keyJoin);
        epService.getEPRuntime().sendEvent(theEvent, "Win1");
    }

    private void sendWin2Event(String keyJoin, double value) {
        Map<String, Object> theEvent = new HashMap<String, Object>();
        theEvent.put("keyJoin", keyJoin);
        theEvent.put("value", value);
        epService.getEPRuntime().sendEvent(theEvent, "Win2");
    }

    public void testExecuteSimple() throws Exception
    {
        String query = "select * from MyWindow";
        EPOnDemandPreparedQuery prepared = epService.getEPRuntime().prepareQuery(query);
        EPOnDemandQueryResult result = epService.getEPRuntime().executeQuery(query);
        for (EventBean row : result.getArray()) {
            // System.out.println("name=" + row.get("name"));
        }
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, null);
        EPAssertionUtil.assertPropsPerRow(prepared.execute().iterator(), fields, null);

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{"E1", 1}});
        EPAssertionUtil.assertPropsPerRow(prepared.execute().iterator(), fields, new Object[][]{{"E1", 1}});

        epService.getEPRuntime().sendEvent(new SupportBean("E2", 2));
        result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{"E1", 1}, {"E2", 2}});
        EPAssertionUtil.assertPropsPerRow(prepared.execute().iterator(), fields, new Object[][]{{"E1", 1}, {"E2", 2}});
    }

    public void testExecuteCount() throws Exception
    {
        fields = new String[] {"cnt"};
        String query = "select count(*) as cnt from MyWindow";
        EPOnDemandPreparedQuery prepared = epService.getEPRuntime().prepareQuery(query);

        EPOnDemandQueryResult result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{0L}});
        EPAssertionUtil.assertPropsPerRow(prepared.execute().iterator(), fields, new Object[][]{{0L}});

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{1L}});
        EPAssertionUtil.assertPropsPerRow(prepared.execute().iterator(), fields, new Object[][]{{1L}});
        result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{1L}});
        EPAssertionUtil.assertPropsPerRow(prepared.execute().iterator(), fields, new Object[][]{{1L}});

        epService.getEPRuntime().sendEvent(new SupportBean("E2", 2));
        result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{2L}});
        EPAssertionUtil.assertPropsPerRow(prepared.execute().iterator(), fields, new Object[][]{{2L}});
    }

    public void testExecuteFilter() throws Exception
    {
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 0));
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 11));
        epService.getEPRuntime().sendEvent(new SupportBean("E3", 5));

        String query = "select * from MyWindow(intPrimitive > 1, intPrimitive < 10)";
        runAssertionFilter(query);

        query = "select * from MyWindow(intPrimitive > 1) where intPrimitive < 10";
        runAssertionFilter(query);

        query = "select * from MyWindow where intPrimitive < 10 and intPrimitive > 1";
        runAssertionFilter(query);
    }

    public void testAggUngroupedRowForAll() throws Exception
    {
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 0));
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 11));
        epService.getEPRuntime().sendEvent(new SupportBean("E3", 5));
        String fields[] = new String[] {"total"};

        String query = "select sum(intPrimitive) as total from MyWindow";
        EPOnDemandQueryResult result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{16}});

        epService.getEPRuntime().sendEvent(new SupportBean("E4", -2));
        result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{14}});
    }

    public void testAggUngroupedRowForEvent() throws Exception
    {
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 0));
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 11));
        epService.getEPRuntime().sendEvent(new SupportBean("E3", 5));
        String fields[] = new String[] {"theString", "total"};

        String query = "select theString, sum(intPrimitive) as total from MyWindow";
        EPOnDemandQueryResult result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{"E1", 16}, {"E2", 16}, {"E3", 16}});

        epService.getEPRuntime().sendEvent(new SupportBean("E4", -2));
        result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{"E1", 14}, {"E2", 14}, {"E3", 14}, {"E4", 14}});
    }

    public void testAggUngroupedRowForGroup() throws Exception
    {
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 11));
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 5));
        String fields[] = new String[] {"theString", "total"};

        String query = "select theString, sum(intPrimitive) as total from MyWindow group by theString order by theString asc";
        EPOnDemandQueryResult result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{"E1", 6}, {"E2", 11}});

        epService.getEPRuntime().sendEvent(new SupportBean("E2", -2));
        epService.getEPRuntime().sendEvent(new SupportBean("E3", 3));
        result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{"E1", 6}, {"E2", 9}, {"E3", 3}});
    }

    public void testJoin() throws Exception
    {
        String namedWin = "create window MySecondWindow.win:keepall() as select * from SupportBean_A";
        epService.getEPAdministrator().createEPL(namedWin);
        String insert = "insert into MySecondWindow select * from SupportBean_A";
        epService.getEPAdministrator().createEPL(insert);

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 11));
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 5));
        epService.getEPRuntime().sendEvent(new SupportBean_A("E2"));
        String fields[] = new String[] {"theString", "intPrimitive", "id"};

        String query = "select theString, intPrimitive, id from MyWindow nw1, " +
                            "MySecondWindow nw2 where nw1.theString = nw2.id";
        EPOnDemandQueryResult result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{"E2", 11, "E2"}});

        epService.getEPRuntime().sendEvent(new SupportBean("E3", 1));
        epService.getEPRuntime().sendEvent(new SupportBean("E3", 2));
        epService.getEPRuntime().sendEvent(new SupportBean_A("E3"));

        result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{"E2", 11, "E2"}, {"E3", 1, "E3"}, {"E3", 2, "E3"}});
    }

    public void testInvalid()
    {
        String epl = "selectoo man";
        tryInvalidSyntax(epl, "Incorrect syntax near 'selectoo' [selectoo man]");

        epl = "select (select * from MyWindow) from MyWindow";
        tryInvalid(epl, "Error executing statement: Subqueries are not a supported feature of on-demand queries [select (select * from MyWindow) from MyWindow]");

        epl = "select * from MyWindow.stat:uni(intPrimitive)";
        tryInvalid(epl, "Error executing statement: Views are not a supported feature of on-demand queries [select * from MyWindow.stat:uni(intPrimitive)]");

        epl = "select * from MyWindow output every 10 seconds";
        tryInvalid(epl, "Error executing statement: Output rate limiting is not a supported feature of on-demand queries [select * from MyWindow output every 10 seconds]");

        epl = "insert into AStream select * from MyWindow";
        tryInvalid(epl, "Error executing statement: Insert-into is not a supported feature of on-demand queries [insert into AStream select * from MyWindow]");

        epl = "select * from pattern [every MyWindow]";
        tryInvalid(epl, "Error executing statement: On-demand queries require named windows and do not allow event streams or patterns [select * from pattern [every MyWindow]]");

        epl = "select prev(1, theString) from MyWindow";
        tryInvalid(epl, "Error executing statement: Previous function cannot be used in this context [select prev(1, theString) from MyWindow]");
    }

    private void tryInvalid(String epl, String message)
    {
        try
        {
            epService.getEPRuntime().executeQuery(epl);
            fail();
        }
        catch(EPStatementException ex)
        {
            assertEquals(message, ex.getMessage());
        }
    }

    private void tryInvalidSyntax(String epl, String message)
    {
        try
        {
            epService.getEPRuntime().executeQuery(epl);
            fail();
        }
        catch(EPStatementSyntaxException ex)
        {
            assertEquals(message, ex.getMessage());
        }
    }

    private void runAssertionFilter(String query)
    {
        EPOnDemandQueryResult result = epService.getEPRuntime().executeQuery(query);
        EPAssertionUtil.assertPropsPerRow(result.iterator(), fields, new Object[][]{{"E3", 5}});

        EPOnDemandPreparedQuery prepared = epService.getEPRuntime().prepareQuery(query);
        EPAssertionUtil.assertPropsPerRow(prepared.execute().iterator(), fields, new Object[][]{{"E3", 5}});
    }
}
