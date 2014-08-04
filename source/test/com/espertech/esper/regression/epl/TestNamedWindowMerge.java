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
import com.espertech.esper.client.scopetest.SupportSubscriberMRD;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.client.soda.EPStatementFormatter;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.core.service.EPStatementSPI;
import com.espertech.esper.regression.view.TestFilterPropertySimple;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.bean.SupportBean_A;
import com.espertech.esper.support.bean.SupportBean_S0;
import com.espertech.esper.support.bean.SupportBean_ST0;
import com.espertech.esper.support.bean.bookexample.OrderBean;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.util.EventRepresentationEnum;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestNamedWindowMerge extends TestCase {

    private String NEWLINE = System.getProperty("line.separator");

    private EPServiceProvider epService;
    private SupportUpdateListener nwListener;
    private SupportUpdateListener mergeListener;

    protected void setUp()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        config.addEventType("SupportBean", SupportBean.class);
        config.addEventType("SupportBean_A", SupportBean_A.class);
        config.addEventType("SupportBean_S0", SupportBean_S0.class);

        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
        nwListener = new SupportUpdateListener();
        mergeListener = new SupportUpdateListener();
    }

    protected void tearDown() throws Exception {
        nwListener = null;
        mergeListener = null;
    }

    public void testUpdateOrderOfFields() throws Exception {
        epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as SupportBean");
        epService.getEPAdministrator().createEPL("insert into MyWindow select * from SupportBean");
        EPStatement stmt = epService.getEPAdministrator().createEPL("on SupportBean_S0 as sb " +
                "merge MyWindow as mywin where mywin.theString = sb.p00 when matched then " +
                "update set intPrimitive=id, intBoxed=mywin.intPrimitive, doublePrimitive=initial.intPrimitive");
        stmt.addListener(mergeListener);
        String[] fields = "intPrimitive,intBoxed,doublePrimitive".split(",");

        epService.getEPRuntime().sendEvent(makeSupportBean("E1", 1, 2));
        epService.getEPRuntime().sendEvent(new SupportBean_S0(5, "E1"));
        EPAssertionUtil.assertProps(mergeListener.getAndResetLastNewData()[0], fields, new Object[]{5, 5, 1.0});

        epService.getEPRuntime().sendEvent(makeSupportBean("E2", 10, 20));
        epService.getEPRuntime().sendEvent(new SupportBean_S0(6, "E2"));
        EPAssertionUtil.assertProps(mergeListener.getAndResetLastNewData()[0], fields, new Object[]{6, 6, 10.0});

        epService.getEPRuntime().sendEvent(new SupportBean_S0(7, "E1"));
        EPAssertionUtil.assertProps(mergeListener.getAndResetLastNewData()[0], fields, new Object[]{7, 7, 5.0});
    }

    public void testInsertOtherStream() throws Exception {
        runAssertionInsertOtherStream(EventRepresentationEnum.OBJECTARRAY);
        runAssertionInsertOtherStream(EventRepresentationEnum.MAP);
        runAssertionInsertOtherStream(EventRepresentationEnum.DEFAULT);
    }

    private void runAssertionInsertOtherStream(EventRepresentationEnum eventRepresentationEnum) throws Exception {
        String epl = eventRepresentationEnum.getAnnotationText() + " create schema MyEvent as (name string, value double);\n" +
                     eventRepresentationEnum.getAnnotationText() + " create window MyWin.std:unique(name) as MyEvent;\n" +
                     "insert into MyWin select * from MyEvent;\n" +
                     eventRepresentationEnum.getAnnotationText() + " create schema InputEvent as (col1 string, col2 double);\n" +
                "\n" +
                "on MyEvent as eme\n" +
                "merge MyWin as mywin\n" +
                "where mywin.name = eme.name\n" +
                "when matched then\n" +
                "insert into OtherStreamOne\n" +
                "select eme.name as event_name, mywin.value as status\n" +
                "when not matched then\n" +
                "insert into OtherStreamOne\n" +
                "select eme.name as event_name, 0d as status\n" +
                ";";
        epService.getEPAdministrator().getDeploymentAdmin().parseDeploy(epl, null, null, null);
        epService.getEPAdministrator().createEPL("select * from OtherStreamOne").addListener(mergeListener);

        makeSendNameValueEvent(epService, eventRepresentationEnum, "MyEvent", "name1", 10d);
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNewAndReset(), "event_name,status".split(","), new Object[]{"name1", 0d});

        makeSendNameValueEvent(epService, eventRepresentationEnum, "MyEvent", "name1", 11d);
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNewAndReset(), "event_name,status".split(","), new Object[]{"name1", 10d});

        makeSendNameValueEvent(epService, eventRepresentationEnum, "MyEvent", "name1", 12d);
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNewAndReset(), "event_name,status".split(","), new Object[]{"name1", 11d});
        
        epService.initialize();
    }

    public void testMergeTriggeredByAnotherWindow() {

        // test dispatch between named windows
        epService.getEPAdministrator().createEPL("@Name('A') create window A.std:unique(id) as (id int)");
        epService.getEPAdministrator().createEPL("@Name('B') create window B.std:unique(id) as (id int)");
        epService.getEPAdministrator().createEPL("@Name('C') on A merge B when not matched then insert select 1 as id when matched then insert select 1 as id");

        epService.getEPAdministrator().createEPL("@Name('D') select * from B").addListener(nwListener);
        epService.getEPAdministrator().createEPL("@Name('E') insert into A select intPrimitive as id FROM SupportBean");

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        assertTrue(nwListener.isInvoked());
    }

	public void testSubqueryNotMatched() {
        EPStatementSPI stmt = (EPStatementSPI) epService.getEPAdministrator().createEPL("create window WindowOne.std:unique(string) (string string, intPrimitive int)");
        assertFalse(stmt.getStatementContext().isStatelessSelect());
        epService.getEPAdministrator().createEPL("create window WindowTwo.std:unique(val0) (val0 string, val1 int)");
        epService.getEPAdministrator().createEPL("insert into WindowTwo select 'W2' as val0, id as val1 from SupportBean_S0");

        String epl = "on SupportBean sb merge WindowOne w1 " +
                "where sb.theString = w1.string " +
                "when not matched then insert select 'Y' as string, (select val1 from WindowTwo as w2 where w2.val0 = sb.theString) as intPrimitive";
        epService.getEPAdministrator().createEPL(epl);

        epService.getEPRuntime().sendEvent(new SupportBean_S0(50));  // WindowTwo now has a row {W2, 1}
        epService.getEPRuntime().sendEvent(new SupportBean("W2", 1));
        EPAssertionUtil.assertPropsPerRow(stmt.iterator(), "string,intPrimitive".split(","), new Object[][]{{"Y", 50}});

        epService.getEPRuntime().sendEvent(new SupportBean_S0(51));  // WindowTwo now has a row {W2, 1}
        epService.getEPRuntime().sendEvent(new SupportBean("W2", 2));
        EPAssertionUtil.assertPropsPerRow(stmt.iterator(), "string,intPrimitive".split(","), new Object[][]{{"Y", 51}});
    }

    public void testMultiactionDeleteUpdate() {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean_ST0", SupportBean_ST0.class);

        EPStatement nmStmt = epService.getEPAdministrator().createEPL("create window Win.win:keepall() as SupportBean");
        epService.getEPAdministrator().createEPL("insert into Win select * from SupportBean");
        String epl = "on SupportBean_ST0 as st0 merge Win as win where st0.key0 = win.theString " +
                "when matched " +
                "then delete where intPrimitive < 0 " +
                "then update set intPrimitive = st0.p00 where intPrimitive = 3000 or p00 = 3000 " +
                "then delete where intPrimitive = 1000 " +
                "then update set intPrimitive = 999 where intPrimitive = 1000 " +
                "then update set intPrimitive = 1999 where intPrimitive = 2000 " +
                "then delete where intPrimitive = 2000 ";
        String eplFormatted = "on SupportBean_ST0 as st0" + NEWLINE +
                "merge Win as win" + NEWLINE +
                "where st0.key0 = win.theString" + NEWLINE +
                "when matched" + NEWLINE +
                "then delete where intPrimitive < 0" + NEWLINE +
                "then update set intPrimitive = st0.p00 where intPrimitive = 3000 or p00 = 3000" + NEWLINE +
                "then delete where intPrimitive = 1000" + NEWLINE +
                "then update set intPrimitive = 999 where intPrimitive = 1000" + NEWLINE +
                "then update set intPrimitive = 1999 where intPrimitive = 2000" + NEWLINE +
                "then delete where intPrimitive = 2000";
        epService.getEPAdministrator().createEPL(epl);
        String[] fields = "theString,intPrimitive".split(",");

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", "E1", 0));
        EPAssertionUtil.assertPropsPerRow(nmStmt.iterator(), fields, new Object[][]{{"E1", 1}});

        epService.getEPRuntime().sendEvent(new SupportBean("E2", -1));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", "E2", 0));
        EPAssertionUtil.assertPropsPerRow(nmStmt.iterator(), fields, new Object[][]{{"E1", 1}});

        epService.getEPRuntime().sendEvent(new SupportBean("E3", 3000));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", "E3", 3));
        EPAssertionUtil.assertPropsPerRow(nmStmt.iterator(), fields, new Object[][]{{"E1", 1}, {"E3", 3}});

        epService.getEPRuntime().sendEvent(new SupportBean("E4", 4));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", "E4", 3000));
        EPAssertionUtil.assertPropsPerRow(nmStmt.iterator(), fields, new Object[][]{{"E1", 1}, {"E3", 3}, {"E4", 3000}});

        epService.getEPRuntime().sendEvent(new SupportBean("E5", 1000));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", "E5", 0));
        EPAssertionUtil.assertPropsPerRow(nmStmt.iterator(), fields, new Object[][]{{"E1", 1}, {"E3", 3}, {"E4", 3000}, {"E5", 999}});

        epService.getEPRuntime().sendEvent(new SupportBean("E6", 2000));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", "E6", 0));
        EPAssertionUtil.assertPropsPerRow(nmStmt.iterator(), fields, new Object[][]{{"E1", 1}, {"E3", 3}, {"E4", 3000}, {"E5", 999}, {"E6", 1999}});

        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(epl);
        assertEquals(epl.trim(), model.toEPL().trim());
        assertEquals(eplFormatted.trim(), model.toEPL(new EPStatementFormatter(true)));
        EPStatement merged = epService.getEPAdministrator().create(model);
        assertEquals(merged.getText().trim(), model.toEPL().trim());
     }

    public void testOnMergeInsertStream() throws Exception {
        SupportUpdateListener listenerOne = new SupportUpdateListener();
        SupportUpdateListener listenerTwo = new SupportUpdateListener();
        SupportUpdateListener listenerThree = new SupportUpdateListener();
        SupportUpdateListener listenerFour = new SupportUpdateListener();

        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean_ST0", SupportBean_ST0.class);

        epService.getEPAdministrator().createEPL("create schema WinSchema as (v1 string, v2 int)");
        EPStatement nmStmt = epService.getEPAdministrator().createEPL("create window Win.win:keepall() as WinSchema");
        String epl = "on SupportBean_ST0 as st0 merge Win as win where win.v1 = st0.key0 " +
                "when not matched " +
                "then insert into StreamOne select * " +
                "then insert into StreamTwo select st0.id as id, st0.key0 as key0 " +
                "then insert into StreamThree(id, key0) select st0.id, st0.key0 " +
                "then insert into StreamFour select id, key0 where key0 = \"K2\" " +
                "then insert into Win select key0 as v1, p00 as v2";
        epService.getEPAdministrator().createEPL(epl);

        epService.getEPAdministrator().createEPL("select * from StreamOne").addListener(listenerOne);
        epService.getEPAdministrator().createEPL("select * from StreamTwo").addListener(listenerTwo);
        epService.getEPAdministrator().createEPL("select * from StreamThree").addListener(listenerThree);
        epService.getEPAdministrator().createEPL("select * from StreamFour").addListener(listenerFour);

        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ID1", "K1", 1));
        EPAssertionUtil.assertProps(listenerOne.assertOneGetNewAndReset(), "id,key0".split(","), new Object[]{"ID1", "K1"});
        EPAssertionUtil.assertProps(listenerTwo.assertOneGetNewAndReset(), "id,key0".split(","), new Object[]{"ID1", "K1"});
        EPAssertionUtil.assertProps(listenerThree.assertOneGetNewAndReset(), "id,key0".split(","), new Object[]{"ID1", "K1"});
        assertFalse(listenerFour.isInvoked());

        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ID1", "K2", 2));
        EPAssertionUtil.assertProps(listenerFour.assertOneGetNewAndReset(), "id,key0".split(","), new Object[]{"ID1", "K2"});

        EPAssertionUtil.assertPropsPerRow(nmStmt.iterator(), "v1,v2".split(","), new Object[][]{{"K1", 1}, {"K2", 2}});

        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(epl);
        assertEquals(epl.trim(), model.toEPL().trim());
        EPStatement merged = epService.getEPAdministrator().create(model);
        assertEquals(merged.getText().trim(), model.toEPL().trim());
    }

    public void testDocExample() throws Exception {
        runAssertionDocExample(EventRepresentationEnum.OBJECTARRAY);
        runAssertionDocExample(EventRepresentationEnum.MAP);
        runAssertionDocExample(EventRepresentationEnum.DEFAULT);
    }

    public void runAssertionDocExample(EventRepresentationEnum eventRepresentationEnum) throws Exception {

        String baseModule = eventRepresentationEnum.getAnnotationText() + " create schema OrderEvent as (orderId string, productId string, price double, quantity int, deletedFlag boolean)";
        epService.getEPAdministrator().getDeploymentAdmin().parseDeploy(baseModule, null, null, null);

        String appModuleOne = eventRepresentationEnum.getAnnotationText() + " create schema ProductTotalRec as (productId string, totalPrice double);" +
                "" +
                eventRepresentationEnum.getAnnotationText() + " @Name('nwProd') create window ProductWindow.std:unique(productId) as ProductTotalRec;" +
                "" +
                "on OrderEvent oe\n" +
                "merge ProductWindow pw\n" +
                "where pw.productId = oe.productId\n" +
                "when matched\n" +
                "then update set totalPrice = totalPrice + oe.price\n" +
                "when not matched\n" +
                "then insert select productId, price as totalPrice;";
        epService.getEPAdministrator().getDeploymentAdmin().parseDeploy(appModuleOne, null, null, null);

        String appModuleTwo = eventRepresentationEnum.getAnnotationText() + " @Name('nwOrd') create window OrderWindow.win:keepall() as OrderEvent;" +
                "" +
                "on OrderEvent oe\n" +
                "  merge OrderWindow pw\n" +
                "  where pw.orderId = oe.orderId\n" +
                "  when not matched \n" +
                "    then insert select *\n" +
                "  when matched and oe.deletedFlag=true\n" +
                "    then delete\n" +
                "  when matched\n" +
                "    then update set pw.quantity = oe.quantity, pw.price = oe.price";

        epService.getEPAdministrator().getDeploymentAdmin().parseDeploy(appModuleTwo, null, null, null);

        sendOrderEvent(eventRepresentationEnum, "O1", "P1", 10, 100, false);
        sendOrderEvent(eventRepresentationEnum, "O1", "P1", 11, 200, false);
        sendOrderEvent(eventRepresentationEnum, "O2", "P2", 3, 300, false);
        EPAssertionUtil.assertPropsPerRowAnyOrder(epService.getEPAdministrator().getStatement("nwProd").iterator(), "productId,totalPrice".split(","), new Object[][]{{"P1", 21d}, {"P2", 3d}});
        EPAssertionUtil.assertPropsPerRowAnyOrder(epService.getEPAdministrator().getStatement("nwOrd").iterator(), "orderId,quantity".split(","), new Object[][]{{"O1", 200}, {"O2", 300}});

        String module = "create schema StreetCarCountSchema (streetid string, carcount int);" +
                "    create schema StreetChangeEvent (streetid string, action string);" +
                "    create window StreetCarCountWindow.std:unique(streetid) as StreetCarCountSchema;" +
                "    on StreetChangeEvent ce merge StreetCarCountWindow w where ce.streetid = w.streetid\n" +
                "    when not matched and ce.action = 'ENTER' then insert select streetid, 1 as carcount\n" +
                "    when matched and ce.action = 'ENTER' then update set StreetCarCountWindow.carcount = carcount + 1\n" +
                "    when matched and ce.action = 'LEAVE' then update set StreetCarCountWindow.carcount = carcount - 1;" +
                "    select * from StreetCarCountWindow;";
        epService.getEPAdministrator().getDeploymentAdmin().parseDeploy(module, null, null, null);

        epService.initialize();
    }

    private void sendOrderEvent(EventRepresentationEnum eventRepresentationEnum, String orderId, String productId, double price, int quantity, boolean deletedFlag) {
        Map<String, Object> theEvent = new LinkedHashMap<String, Object>();
        theEvent.put("orderId", orderId);
        theEvent.put("productId", productId);
        theEvent.put("price", price);
        theEvent.put("quantity", quantity);
        theEvent.put("deletedFlag", deletedFlag);
        if (eventRepresentationEnum.isObjectArrayEvent()) {
            epService.getEPRuntime().sendEvent(theEvent.values().toArray(), "OrderEvent");
        }
        else {
            epService.getEPRuntime().sendEvent(theEvent, "OrderEvent");
        }
    }

    public void testTypeReference() {
        ConfigurationOperations configOps = epService.getEPAdministrator().getConfiguration();

        epService.getEPAdministrator().createEPL("@Name('ces') create schema EventSchema(in1 string, in2 int)");
        epService.getEPAdministrator().createEPL("@Name('cnws') create schema WindowSchema(in1 string, in2 int)");

        EPAssertionUtil.assertEqualsAnyOrder(new String[]{"ces"}, configOps.getEventTypeNameUsedBy("EventSchema").toArray());
        EPAssertionUtil.assertEqualsAnyOrder(new String[]{"cnws"}, configOps.getEventTypeNameUsedBy("WindowSchema").toArray());

        epService.getEPAdministrator().createEPL("@Name('cnw') create window MyWindow.win:keepall() as WindowSchema");
        EPAssertionUtil.assertEqualsAnyOrder("cnws,cnw".split(","), configOps.getEventTypeNameUsedBy("WindowSchema").toArray());
        EPAssertionUtil.assertEqualsAnyOrder(new String[]{"cnw"}, configOps.getEventTypeNameUsedBy("MyWindow").toArray());

        epService.getEPAdministrator().createEPL("@Name('om') on EventSchema merge into MyWindow " +
                "when not matched then insert select in1, in2");
        EPAssertionUtil.assertEqualsAnyOrder("ces,om".split(","), configOps.getEventTypeNameUsedBy("EventSchema").toArray());
        EPAssertionUtil.assertEqualsAnyOrder("cnws,cnw".split(","), configOps.getEventTypeNameUsedBy("WindowSchema").toArray());
    }

    public void testPerformance() {
        runAssertionPerformance(EventRepresentationEnum.OBJECTARRAY);
        runAssertionPerformance(EventRepresentationEnum.MAP);
        runAssertionPerformance(EventRepresentationEnum.DEFAULT);
    }

    private void runAssertionPerformance(EventRepresentationEnum outputType) {

        EPStatement stmtNamedWindow = epService.getEPAdministrator().createEPL(outputType.getAnnotationText() + " create window MyWindow.win:keepall() as (c1 string, c2 int)");
        assertEquals(outputType.getOutputClass(), stmtNamedWindow.getEventType().getUnderlyingType());

        // preload events
        EPStatement stmt = epService.getEPAdministrator().createEPL("insert into MyWindow select theString as c1, intPrimitive as c2 from SupportBean");
        final int totalUpdated = 10000;
        for (int i = 0; i < totalUpdated; i++) {
            epService.getEPRuntime().sendEvent(new SupportBean("E" + i, 0));
        }
        stmt.destroy();

        String epl =  "on SupportBean sb " +
                      "merge MyWindow nw " +
                      "where nw.c1 = sb.theString " +
                      "when not matched then " +
                      "insert select theString as c1, intPrimitive as c2 " +
                      "when matched then " +
                      "update set nw.c2=sb.intPrimitive";
        stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(mergeListener);

        // prime
        for (int i = 0; i < 100; i++) {
            epService.getEPRuntime().sendEvent(new SupportBean("E" + i, 1));
        }
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < totalUpdated; i++) {
            epService.getEPRuntime().sendEvent(new SupportBean("E" + i, 1));
        }
        long endTime = System.currentTimeMillis();
        long delta = endTime - startTime;

        // verify
        Iterator<EventBean> events = stmtNamedWindow.iterator();
        int count = 0;
        for (;events.hasNext();) {
            EventBean next = events.next();
            assertEquals(1, next.get("c2"));
            count++;
        }
        assertEquals(totalUpdated, count);
        assertTrue(delta < 500);
        
        epService.getEPAdministrator().destroyAllStatements();
        epService.getEPAdministrator().getConfiguration().removeEventType("MyWindow", true);
    }

    public void testPropertyEval() {
        epService.getEPAdministrator().getConfiguration().addEventType("OrderBean", OrderBean.class);

        String[] fields = "c1,c2".split(",");
        epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as (c1 string, c2 string)");

        String epl =  "on OrderBean[books] " +
                      "merge MyWindow mw " +
                      "when not matched then " +
                      "insert select bookId as c1, title as c2 ";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(mergeListener);

        epService.getEPRuntime().sendEvent(TestFilterPropertySimple.makeEventOne());
        EPAssertionUtil.assertPropsPerRow(mergeListener.getLastNewData(), fields, new Object[][]{{"10020", "Enders Game"},
                {"10021", "Foundation 1"}, {"10022", "Stranger in a Strange Land"}});
    }

    public void testPatternMultimatch() {
        String[] fields = "c1,c2".split(",");
        EPStatement namedWindowStmt = epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as (c1 string, c2 string)");

        String epl =  "on pattern[every a=SupportBean(theString like 'A%') -> b=SupportBean(theString like 'B%', intPrimitive = a.intPrimitive)] me " +
                      "merge MyWindow mw " +
                      "where me.a.theString = mw.c1 and me.b.theString = mw.c2 " +
                      "when not matched then " +
                      "insert select me.a.theString as c1, me.b.theString as c2 ";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(mergeListener);

        epService.getEPRuntime().sendEvent(new SupportBean("A1", 1));
        epService.getEPRuntime().sendEvent(new SupportBean("A2", 1));
        epService.getEPRuntime().sendEvent(new SupportBean("B1", 1));
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"A1", "B1"}, {"A2", "B1"}});

        epService.getEPRuntime().sendEvent(new SupportBean("A3", 2));
        epService.getEPRuntime().sendEvent(new SupportBean("A4", 2));
        epService.getEPRuntime().sendEvent(new SupportBean("B2", 2));
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"A1", "B1"}, {"A2", "B1"}, {"A3", "B2"}, {"A4", "B2"}});
    }

    public void testInnerTypeAndVariable() {
        runAssertionInnerTypeAndVariable(EventRepresentationEnum.OBJECTARRAY);
        runAssertionInnerTypeAndVariable(EventRepresentationEnum.MAP);
        runAssertionInnerTypeAndVariable(EventRepresentationEnum.DEFAULT);
    }

    private void runAssertionInnerTypeAndVariable(EventRepresentationEnum eventRepresentationEnum) {

        epService.getEPAdministrator().createEPL(eventRepresentationEnum.getAnnotationText() + " create schema MyInnerSchema(in1 string, in2 int)");
        epService.getEPAdministrator().createEPL(eventRepresentationEnum.getAnnotationText() + " create schema MyEventSchema(col1 string, col2 MyInnerSchema)");
        epService.getEPAdministrator().createEPL(eventRepresentationEnum.getAnnotationText() + " create window MyWindow.win:keepall() as (c1 string, c2 MyInnerSchema)");
        epService.getEPAdministrator().createEPL("create variable boolean myvar");

        String epl =  "on MyEventSchema me " +
                      "merge MyWindow mw " +
                      "where me.col1 = mw.c1 " +
                      "when not matched and myvar then " +
                      "insert select col1 as c1, col2 as c2 " +
                      "when not matched and myvar = false then " +
                      "insert select 'A' as c1, null as c2 " +
                      "when not matched and myvar is null then " +
                      "insert select 'B' as c1, me.col2 as c2 " +
                      "when matched then " +
                      "delete";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(mergeListener);
        String[] fields = "c1,c2.in1,c2.in2".split(",");

        sendMyInnerSchemaEvent(eventRepresentationEnum, "X1", "Y1", 10);
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNewAndReset(), fields, new Object[]{"B", "Y1", 10});

        sendMyInnerSchemaEvent(eventRepresentationEnum, "B", "0", 0);    // delete
        EPAssertionUtil.assertProps(mergeListener.assertOneGetOldAndReset(), fields, new Object[]{"B", "Y1", 10});

        epService.getEPRuntime().setVariableValue("myvar", true);
        sendMyInnerSchemaEvent(eventRepresentationEnum, "X2", "Y2", 11);
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNewAndReset(), fields, new Object[]{"X2", "Y2", 11});

        epService.getEPRuntime().setVariableValue("myvar", false);
        sendMyInnerSchemaEvent(eventRepresentationEnum, "X3", "Y3", 12);
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNewAndReset(), fields, new Object[]{"A", null, null});

        stmt.destroy();
        stmt = epService.getEPAdministrator().createEPL(epl);
        SupportSubscriberMRD subscriber = new SupportSubscriberMRD();
        stmt.setSubscriber(subscriber);
        epService.getEPRuntime().setVariableValue("myvar", true);

        sendMyInnerSchemaEvent(eventRepresentationEnum, "X4", "Y4", 11);
        Object[][] result = subscriber.getInsertStreamList().get(0);
        if (!eventRepresentationEnum.isObjectArrayEvent()) {
            Map map = (Map) result[0][0];
            assertEquals("X4", map.get("c1"));
            EventBean theEvent = (EventBean) map.get("c2");
            assertEquals("Y4", theEvent.get("in1"));
        }
        else {
            Object[] row = (Object[]) result[0][0];
            assertEquals("X4", row[0]);
            EventBean theEvent = (EventBean) row[1];
            assertEquals("Y4", theEvent.get("in1"));
        }

        epService.initialize();
    }

    public void testPropertyInsertBean() {
        EPStatement stmtWindow = epService.getEPAdministrator().createEPL("create window MergeWindow.std:unique(theString) as SupportBean");

        String epl = "on SupportBean as up merge MergeWindow as mv where mv.theString=up.theString when not matched then insert select intPrimitive";
        EPStatement stmtMerge = epService.getEPAdministrator().createEPL(epl);
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 10));

        EventBean theEvent = stmtWindow.iterator().next();
        EPAssertionUtil.assertProps(theEvent, "theString,intPrimitive".split(","), new Object[]{null, 10});
        stmtMerge.destroy();

        epl = "on SupportBean as up merge MergeWindow as mv where mv.theString=up.theString when not matched then insert select theString, intPrimitive";
        epService.getEPAdministrator().createEPL(epl);
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 20));

        EPAssertionUtil.assertPropsPerRow(stmtWindow.iterator(), "theString,intPrimitive".split(","), new Object[][]{{null, 10}, {"E2", 20}});
    }

    public void testInvalid() {
        String epl;
        epService.getEPAdministrator().createEPL("create window MergeWindow.std:unique(theString) as SupportBean");
        epService.getEPAdministrator().createEPL("create schema ABCSchema as (val int)");
        epService.getEPAdministrator().createEPL("create window ABCWindow.win:keepall() as ABCSchema");

        epl = "on SupportBean_A merge MergeWindow as windowevent where id = theString when not matched and exists(select * from MergeWindow mw where mw.theString = windowevent.theString) is not null then insert into ABC select '1'";
        tryInvalid(epl, "Error starting statement: On-Merge not-matched filter expression may not use properties that are provided by the named window event [on SupportBean_A merge MergeWindow as windowevent where id = theString when not matched and exists(select * from MergeWindow mw where mw.theString = windowevent.theString) is not null then insert into ABC select '1']");

        epl = "on SupportBean_A as up merge ABCWindow as mv when not matched then insert (col) select 1";
        tryInvalid(epl, "Error starting statement: Exception encountered in when-not-matched (clause 1): Event type named 'ABCWindow' has already been declared with differing column name or type information: The property 'val' is not provided but required [on SupportBean_A as up merge ABCWindow as mv when not matched then insert (col) select 1]");

        epl = "on SupportBean_A as up merge MergeWindow as mv where mv.boolPrimitive=true when not matched then update set intPrimitive = 1";
        tryInvalid(epl, "Incorrect syntax near 'update' (a reserved keyword) expecting 'insert' but found 'update' at line 1 column 97 [on SupportBean_A as up merge MergeWindow as mv where mv.boolPrimitive=true when not matched then update set intPrimitive = 1]");

        epl = "on SupportBean_A as up merge MergeWindow as mv where mv.theString=id when matched then insert select *";
        tryInvalid(epl, "Error starting statement: Exception encountered in when-not-matched (clause 1): Expression-returned event type 'SupportBean_A' with underlying type 'com.espertech.esper.support.bean.SupportBean_A' cannot be converted target event type 'MergeWindow' with underlying type 'com.espertech.esper.support.bean.SupportBean' [on SupportBean_A as up merge MergeWindow as mv where mv.theString=id when matched then insert select *]");

        epl = "on SupportBean as up merge MergeWindow as mv";
        tryInvalid(epl, "Unexpected end of input string, check for an invalid identifier or missing additional keywords near 'mv' at line 1 column 42  [on SupportBean as up merge MergeWindow as mv]");

        epl = "on SupportBean as up merge MergeWindow as mv where a=b when matched";
        tryInvalid(epl, "Unexpected end of input string, check for an invalid identifier or missing additional keywords near 'matched' (a reserved keyword) at line 1 column 60  [on SupportBean as up merge MergeWindow as mv where a=b when matched]");

        epl = "on SupportBean as up merge MergeWindow as mv where a=b when matched and then delete";
        tryInvalid(epl, "Incorrect syntax near 'then' (a reserved keyword) at line 1 column 72 [on SupportBean as up merge MergeWindow as mv where a=b when matched and then delete]");

        epl = "on SupportBean as up merge MergeWindow as mv where boolPrimitive=true when not matched then insert select *";
        tryInvalid(epl, "Error starting statement: Property named 'boolPrimitive' is ambigous as is valid for more then one stream [on SupportBean as up merge MergeWindow as mv where boolPrimitive=true when not matched then insert select *]");

        epl = "on SupportBean_A as up merge MergeWindow as mv where mv.boolPrimitive=true when not matched then insert select intPrimitive";
        tryInvalid(epl, "Error starting statement: Property named 'intPrimitive' is not valid in any stream [on SupportBean_A as up merge MergeWindow as mv where mv.boolPrimitive=true when not matched then insert select intPrimitive]");

        epl = "on SupportBean_A as up merge MergeWindow as mv where mv.boolPrimitive=true when not matched then insert select * where theString = 'A'";
        tryInvalid(epl, "Error starting statement: Property named 'theString' is not valid in any stream [on SupportBean_A as up merge MergeWindow as mv where mv.boolPrimitive=true when not matched then insert select * where theString = 'A']");
    }

    public void tryInvalid(String epl, String expected) {
        try {
            epService.getEPAdministrator().createEPL(epl);
            fail();
        }
        catch(EPStatementException ex) {
            assertEquals(expected, ex.getMessage());
        }
    }

    public void testSubselect() {
        runAssertionSubselect(EventRepresentationEnum.OBJECTARRAY);
        runAssertionSubselect(EventRepresentationEnum.MAP);
        runAssertionSubselect(EventRepresentationEnum.DEFAULT);
    }

    private void runAssertionSubselect(EventRepresentationEnum eventRepresentationEnum) {
        String[] fields = "col1,col2".split(",");
        epService.getEPAdministrator().createEPL(eventRepresentationEnum.getAnnotationText() + " create schema MyEvent as (in1 string, in2 int)");
        epService.getEPAdministrator().createEPL(eventRepresentationEnum.getAnnotationText() + " create schema MySchema as (col1 string, col2 int)");
        EPStatement namedWindowStmt = epService.getEPAdministrator().createEPL(eventRepresentationEnum.getAnnotationText() + " create window MyWindow.std:lastevent() as MySchema");
        epService.getEPAdministrator().createEPL("on SupportBean_A delete from MyWindow");

        String epl =  "on MyEvent me " +
                      "merge MyWindow mw " +
                      "when not matched and (select intPrimitive>0 from SupportBean(theString like 'A%').std:lastevent()) then " +
                      "insert(col1, col2) select (select theString from SupportBean(theString like 'A%').std:lastevent()), (select intPrimitive from SupportBean(theString like 'A%').std:lastevent()) " +
                      "when matched and (select intPrimitive>0 from SupportBean(theString like 'B%').std:lastevent()) then " +
                      "update set col1=(select theString from SupportBean(theString like 'B%').std:lastevent()), col2=(select intPrimitive from SupportBean(theString like 'B%').std:lastevent()) " +
                      "when matched and (select intPrimitive>0 from SupportBean(theString like 'C%').std:lastevent()) then " +
                      "delete";
        epService.getEPAdministrator().createEPL(epl);

        // no action tests
        sendMyEvent(eventRepresentationEnum, "X1", 1);
        epService.getEPRuntime().sendEvent(new SupportBean("A1", 0));   // ignored
        sendMyEvent(eventRepresentationEnum, "X2", 2);
        epService.getEPRuntime().sendEvent(new SupportBean("A2", 20));
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, null);

        sendMyEvent(eventRepresentationEnum, "X3", 3);
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"A2", 20}});

        epService.getEPRuntime().sendEvent(new SupportBean_A("Y1"));
        epService.getEPRuntime().sendEvent(new SupportBean("A3", 30));
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, null);

        sendMyEvent(eventRepresentationEnum, "X4", 4);
        epService.getEPRuntime().sendEvent(new SupportBean("A4", 40));
        sendMyEvent(eventRepresentationEnum, "X5", 5);   // ignored as matched (no where clause, no B event)
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"A3", 30}});

        epService.getEPRuntime().sendEvent(new SupportBean("B1", 50));
        sendMyEvent(eventRepresentationEnum, "X6", 6);
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"B1", 50}});

        epService.getEPRuntime().sendEvent(new SupportBean("B2", 60));
        sendMyEvent(eventRepresentationEnum, "X7", 7);
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"B2", 60}});

        epService.getEPRuntime().sendEvent(new SupportBean("B2", 0));
        sendMyEvent(eventRepresentationEnum, "X8", 8);
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"B2", 60}});

        epService.getEPRuntime().sendEvent(new SupportBean("C1", 1));
        sendMyEvent(eventRepresentationEnum, "X9", 9);
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, null);

        epService.getEPRuntime().sendEvent(new SupportBean("C1", 0));
        sendMyEvent(eventRepresentationEnum, "X10", 10);
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"A4", 40}});

        epService.initialize();
    }

    public void testNoWhereClause() {
        String[] fields = "col1,col2".split(",");
        epService.getEPAdministrator().createEPL("create schema MyEvent as (in1 string, in2 int)");
        epService.getEPAdministrator().createEPL("create schema MySchema as (col1 string, col2 int)");
        EPStatement namedWindowStmt = epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as MySchema");
        epService.getEPAdministrator().createEPL("on SupportBean_A delete from MyWindow");

        String epl =  "on MyEvent me " +
                      "merge MyWindow mw " +
                      "when not matched and me.in1 like \"A%\" then " +
                      "insert(col1, col2) select me.in1, me.in2 " +
                      "when not matched and me.in1 like \"B%\" then " +
                      "insert select me.in1 as col1, me.in2 as col2 " +
                      "when matched and me.in1 like \"C%\" then " +
                      "update set col1='Z', col2=-1 " +
                      "when not matched then " +
                      "insert select \"x\" || me.in1 || \"x\" as col1, me.in2 * -1 as col2 ";
        epService.getEPAdministrator().createEPL(epl);

        sendMyEvent(EventRepresentationEnum.getEngineDefault(epService), "E1", 2);
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"xE1x", -2}});

        sendMyEvent(EventRepresentationEnum.getEngineDefault(epService), "A1", 3);   // matched : no where clause
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"xE1x", -2}});

        epService.getEPRuntime().sendEvent(new SupportBean_A("Ax1"));
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, null);

        sendMyEvent(EventRepresentationEnum.getEngineDefault(epService), "A1", 4);
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"A1", 4}});

        sendMyEvent(EventRepresentationEnum.getEngineDefault(epService), "B1", 5);   // matched : no where clause
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"A1", 4}});

        epService.getEPRuntime().sendEvent(new SupportBean_A("Ax1"));
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, null);

        sendMyEvent(EventRepresentationEnum.getEngineDefault(epService), "B1", 5);
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"B1", 5}});

        sendMyEvent(EventRepresentationEnum.getEngineDefault(epService), "C", 6);
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"Z", -1}});
    }

    public void testMultipleInsert() {

        String[] fields = "col1,col2".split(",");
        epService.getEPAdministrator().createEPL("create schema MyEvent as (in1 string, in2 int)");
        epService.getEPAdministrator().createEPL("create schema MySchema as (col1 string, col2 int)");
        epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as MySchema");

        String epl =  "on MyEvent " +
                      "merge MyWindow " +
                      "where col1 = in1 " +
                      "when not matched and in1 like \"A%\" then " +
                      "insert(col1, col2) select in1, in2 " +
                      "when not matched and in1 like \"B%\" then " +
                      "insert select in1 as col1, in2 as col2 " +
                      "when not matched and in1 like \"C%\" then " +
                      "insert select \"Z\" as col1, -1 as col2 " +
                      "when not matched and in1 like \"D%\" then " +
                      "insert select \"x\" || in1 || \"x\" as col1, in2 * -1 as col2 ";
        EPStatement merged = epService.getEPAdministrator().createEPL(epl);
        merged.addListener(mergeListener);

        sendMyEvent(EventRepresentationEnum.getEngineDefault(epService), "E1", 0);
        assertFalse(mergeListener.isInvoked());

        sendMyEvent(EventRepresentationEnum.getEngineDefault(epService), "A1", 1);
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNewAndReset(), fields, new Object[]{"A1", 1});

        sendMyEvent(EventRepresentationEnum.getEngineDefault(epService), "B1", 2);
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNewAndReset(), fields, new Object[]{"B1", 2});

        sendMyEvent(EventRepresentationEnum.getEngineDefault(epService), "C1", 3);
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNewAndReset(), fields, new Object[]{"Z", -1});

        sendMyEvent(EventRepresentationEnum.getEngineDefault(epService), "D1", 4);
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNewAndReset(), fields, new Object[]{"xD1x", -4});

        sendMyEvent(EventRepresentationEnum.getEngineDefault(epService), "B1", 2);
        assertFalse(mergeListener.isInvoked());

        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(epl);
        assertEquals(epl.trim(), model.toEPL().trim());
        merged = epService.getEPAdministrator().create(model);
        assertEquals(merged.getText().trim(), model.toEPL().trim());
    }

    private void sendMyEvent(EventRepresentationEnum eventRepresentationEnum, String in1, int in2) {
        Map<String, Object> theEvent = new LinkedHashMap<String, Object>();
        theEvent.put("in1", in1);
        theEvent.put("in2", in2);
        if (eventRepresentationEnum.isObjectArrayEvent()) {
            epService.getEPRuntime().sendEvent(theEvent.values().toArray(), "MyEvent");
        }
        else {
            epService.getEPRuntime().sendEvent(theEvent, "MyEvent");
        }
    }

    private void sendMyInnerSchemaEvent(EventRepresentationEnum eventRepresentationEnum, String col1, String col2in1, int col2in2) {
        if (eventRepresentationEnum.isObjectArrayEvent()) {
            epService.getEPRuntime().sendEvent(new Object[] {col1, new Object[] {col2in1, col2in2}}, "MyEventSchema");
        }
        else {
            Map<String, Object> inner = new HashMap<String, Object>();
            inner.put("in1", col2in1);
            inner.put("in2", col2in2);
            Map<String, Object> theEvent = new HashMap<String, Object>();
            theEvent.put("col1", col1);
            theEvent.put("col2", inner);
            epService.getEPRuntime().sendEvent(theEvent, "MyEventSchema");
        }
    }

    public void testFlow() throws Exception
    {
        String[] fields = "theString,intPrimitive,intBoxed".split(",");
        EPStatement namedWindowStmt = epService.getEPAdministrator().createEPL("create window MergeWindow.std:unique(theString) as SupportBean");
        namedWindowStmt.addListener(nwListener);

        epService.getEPAdministrator().createEPL("insert into MergeWindow select * from SupportBean(boolPrimitive)");
        epService.getEPAdministrator().createEPL("on SupportBean_A delete from MergeWindow");

        String epl =  "on SupportBean(boolPrimitive = false) as up " +
                      "merge MergeWindow as mv " +
                      "where mv.theString = up.theString " +
                      "when matched and up.intPrimitive < 0 then " +
                      "delete " +
                      "when matched and up.intPrimitive = 0 then " +
                      "update set intPrimitive = 0, intBoxed = 0 " +
                      "when matched then " +
                      "update set intPrimitive = up.intPrimitive, intBoxed = up.intBoxed + mv.intBoxed " +
                      "when not matched then " +
                      "insert select *";
        EPStatement merged = epService.getEPAdministrator().createEPL(epl);
        merged.addListener(mergeListener);

        runAssertionFlow(namedWindowStmt, fields);

        merged.destroy();
        epService.getEPRuntime().sendEvent(new SupportBean_A("A1"));
        nwListener.reset();
        mergeListener.reset();

        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(epl);
        assertEquals(epl, model.toEPL().trim());
        merged = epService.getEPAdministrator().create(model);
        assertEquals(merged.getText().trim(), model.toEPL().trim());
        merged.addListener(mergeListener);

        runAssertionFlow(namedWindowStmt, fields);

        // test stream wildcard
        epService.getEPRuntime().sendEvent(new SupportBean_A("A2"));
        merged.destroy();
        epl =  "on SupportBean(boolPrimitive = false) as up " +
                      "merge MergeWindow as mv " +
                      "where mv.theString = up.theString " +
                      "when not matched then " +
                      "insert select up.*";
        merged = epService.getEPAdministrator().createEPL(epl);
        merged.addListener(mergeListener);

        sendSupportBeanEvent(false, "E99", 2, 3); // insert via merge
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"E99", 2, 3}});

        // Test ambiguous columns.
        epService.getEPAdministrator().createEPL("create schema TypeOne (id long, mylong long, mystring long)");
        epService.getEPAdministrator().createEPL("create window MyNamedWindow.std:unique(id) as select * from TypeOne");

        // The "and not matched" should not complain if "mystring" is ambiguous.
        // The "insert" should not complain as column names have been provided.
        epl =  "on TypeOne as t1 merge MyNamedWindow nm where nm.id = t1.id\n" +
                "  when not matched and mystring = 0 then insert select *\n" +
                "  when not matched then insert (id, mylong, mystring) select 0L, 0L, 0L\n" +
                " ";
        epService.getEPAdministrator().createEPL(epl);
    }

    private void runAssertionFlow(EPStatement namedWindowStmt, String[] fields) {

        sendSupportBeanEvent(true, "E1", 10, 200); // insert via insert-into
        EPAssertionUtil.assertProps(nwListener.assertOneGetNewAndReset(), fields, new Object[]{"E1", 10, 200});
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"E1", 10, 200}});
        assertFalse(mergeListener.isInvoked());

        sendSupportBeanEvent(false, "E1", 11, 201);    // update via merge
        EPAssertionUtil.assertProps(nwListener.assertOneGetNew(), fields, new Object[]{"E1", 11, 401});
        EPAssertionUtil.assertProps(nwListener.assertOneGetOld(), fields, new Object[]{"E1", 10, 200});
        nwListener.reset();
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"E1", 11, 401}});
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNew(), fields, new Object[]{"E1", 11, 401});
        EPAssertionUtil.assertProps(mergeListener.assertOneGetOld(), fields, new Object[]{"E1", 10, 200});
        mergeListener.reset();

        sendSupportBeanEvent(false, "E2", 13, 300); // insert via merge
        EPAssertionUtil.assertProps(nwListener.assertOneGetNewAndReset(), fields, new Object[]{"E2", 13, 300});
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"E1", 11, 401}, {"E2", 13, 300}});
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNewAndReset(), fields, new Object[]{"E2", 13, 300});

        sendSupportBeanEvent(false, "E2", 14, 301); // update via merge
        EPAssertionUtil.assertProps(nwListener.assertOneGetNew(), fields, new Object[]{"E2", 14, 601});
        EPAssertionUtil.assertProps(nwListener.assertOneGetOld(), fields, new Object[]{"E2", 13, 300});
        nwListener.reset();
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"E1", 11, 401}, {"E2", 14, 601}});
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNew(), fields, new Object[]{"E2", 14, 601});
        EPAssertionUtil.assertProps(mergeListener.assertOneGetOld(), fields, new Object[]{"E2", 13, 300});
        mergeListener.reset();

        sendSupportBeanEvent(false, "E2", 15, 302); // update via merge
        EPAssertionUtil.assertProps(nwListener.assertOneGetNew(), fields, new Object[]{"E2", 15, 903});
        EPAssertionUtil.assertProps(nwListener.assertOneGetOld(), fields, new Object[]{"E2", 14, 601});
        nwListener.reset();
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"E1", 11, 401}, {"E2", 15, 903}});
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNew(), fields, new Object[]{"E2", 15, 903});
        EPAssertionUtil.assertProps(mergeListener.assertOneGetOld(), fields, new Object[]{"E2", 14, 601});
        mergeListener.reset();

        sendSupportBeanEvent(false, "E3", 40, 400); // insert via merge
        EPAssertionUtil.assertProps(nwListener.assertOneGetNewAndReset(), fields, new Object[]{"E3", 40, 400});
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"E1", 11, 401}, {"E2", 15, 903}, {"E3", 40, 400}});
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNewAndReset(), fields, new Object[]{"E3", 40, 400});

        sendSupportBeanEvent(false, "E3", 0, 1000); // reset E3 via merge
        EPAssertionUtil.assertProps(nwListener.assertOneGetNew(), fields, new Object[]{"E3", 0, 0});
        EPAssertionUtil.assertProps(nwListener.assertOneGetOld(), fields, new Object[]{"E3", 40, 400});
        nwListener.reset();
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"E1", 11, 401}, {"E2", 15, 903}, {"E3", 0, 0}});
        EPAssertionUtil.assertProps(mergeListener.assertOneGetNew(), fields, new Object[]{"E3", 0, 0});
        EPAssertionUtil.assertProps(mergeListener.assertOneGetOld(), fields, new Object[]{"E3", 40, 400});
        mergeListener.reset();

        sendSupportBeanEvent(false, "E2", -1, 1000); // delete E2 via merge
        EPAssertionUtil.assertProps(nwListener.assertOneGetOldAndReset(), fields, new Object[]{"E2", 15, 903});
        EPAssertionUtil.assertProps(mergeListener.assertOneGetOldAndReset(), fields, new Object[]{"E2", 15, 903});
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"E1", 11, 401}, {"E3", 0, 0}});

        sendSupportBeanEvent(false, "E1", -1, 1000); // delete E1 via merge
        EPAssertionUtil.assertProps(nwListener.assertOneGetOldAndReset(), fields, new Object[]{"E1", 11, 401});
        EPAssertionUtil.assertProps(mergeListener.assertOneGetOldAndReset(), fields, new Object[]{"E1", 11, 401});
        EPAssertionUtil.assertPropsPerRowAnyOrder(namedWindowStmt.iterator(), fields, new Object[][]{{"E3", 0, 0}});
    }

    private void sendSupportBeanEvent(boolean boolPrimitive, String theString, int intPrimitive, Integer intBoxed) {
        SupportBean theEvent = new SupportBean(theString, intPrimitive);
        theEvent.setIntBoxed(intBoxed);
        theEvent.setBoolPrimitive(boolPrimitive);
        epService.getEPRuntime().sendEvent(theEvent);
    }

    private void makeSendNameValueEvent(EPServiceProvider engine, EventRepresentationEnum eventRepresentationEnum, String typeName, String name, double value) {
        Map<String, Object> theEvent = new HashMap<String, Object>();
        theEvent.put("name", name);
        theEvent.put("value", value);
        if (eventRepresentationEnum.isObjectArrayEvent()) {
            engine.getEPRuntime().sendEvent(theEvent.values().toArray(), typeName);
        }
        else {
            engine.getEPRuntime().sendEvent(theEvent, typeName);
        }
    }

    private SupportBean makeSupportBean(String theString, int intPrimitive, double doublePrimitive) {
        SupportBean sb = new SupportBean(theString, intPrimitive);
        sb.setDoublePrimitive(doublePrimitive);
        return sb;
    }
}
