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

package com.espertech.esper.regression.enummethod;

import com.espertech.esper.client.*;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.client.soda.EPStatementFormatter;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.support.bean.*;
import com.espertech.esper.support.bean.lambda.LambdaAssertionUtil;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.util.EventRepresentationEnum;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TestExpressionDef extends TestCase {

    private String NEWLINE = System.getProperty("line.separator");

    private EPServiceProvider epService;
    private SupportUpdateListener listener;

    public void setUp() {

        Configuration config = SupportConfigFactory.getConfiguration();
        config.addEventType("SupportBean", SupportBean.class);
        config.addEventType("SupportBean_ST0", SupportBean_ST0.class);
        config.addEventType("SupportBean_ST1", SupportBean_ST1.class);
        config.addEventType("SupportBean_ST0_Container", SupportBean_ST0_Container.class);
        config.addEventType("SupportCollection", SupportCollection.class);
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
        listener = new SupportUpdateListener();
    }

    protected void tearDown() throws Exception {
        listener = null;
    }

    public void testSequenceAndNested() {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean_S0", SupportBean_S0.class);
        epService.getEPAdministrator().createEPL("create window WindowOne.win:keepall() as (col1 string, col2 string)");
        epService.getEPAdministrator().createEPL("insert into WindowOne select p00 as col1, p01 as col2 from SupportBean_S0");

        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean_S1", SupportBean_S1.class);
        epService.getEPAdministrator().createEPL("create window WindowTwo.win:keepall() as (col1 string, col2 string)");
        epService.getEPAdministrator().createEPL("insert into WindowTwo select p10 as col1, p11 as col2 from SupportBean_S1");

        epService.getEPRuntime().sendEvent(new SupportBean_S0(1, "A", "B1"));
        epService.getEPRuntime().sendEvent(new SupportBean_S0(2, "A", "B2"));

        epService.getEPRuntime().sendEvent(new SupportBean_S1(11, "A", "B1"));
        epService.getEPRuntime().sendEvent(new SupportBean_S1(12, "A", "B2"));

        String epl =
                "@Audit('exprdef') " +
                "expression last2X {\n" +
                "  p => WindowOne(WindowOne.col1 = p.theString).takeLast(2)\n" +
                "} " +
                "expression last2Y {\n" +
                "  p => WindowTwo(WindowTwo.col1 = p.theString).takeLast(2).selectFrom(q => q.col2)\n" +
                "} " +
                "select last2X(sb).selectFrom(a => a.col2).sequenceEqual(last2Y(sb)) as val from SupportBean as sb";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        
        epService.getEPRuntime().sendEvent(new SupportBean("A", 1));
        assertEquals(true, listener.assertOneGetNewAndReset().get("val"));
    }

    public void testCaseNewMultiReturnNoElse() {
        
        String[] fieldsInner = "col1,col2".split(",");
        String epl = "expression gettotal {" +
                " x => case " +
                "  when theString = 'A' then new { col1 = 'X', col2 = 10 } " +
                "  when theString = 'B' then new { col1 = 'Y', col2 = 20 } " +
                "end" +
                "} " +
                "insert into OtherStream select gettotal(sb) as val0 from SupportBean sb";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        assertEquals(Map.class, stmt.getEventType().getPropertyType("val0"));

        SupportUpdateListener listenerTwo = new SupportUpdateListener();
        epService.getEPAdministrator().createEPL("select val0.col1 as c1, val0.col2 as c2 from OtherStream").addListener(listenerTwo);
        String[] fieldsConsume = "c1,c2".split(",");

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        EPAssertionUtil.assertPropsMap((Map) listener.assertOneGetNewAndReset().get("val0"), fieldsInner, new Object[]{null, null});
        EPAssertionUtil.assertProps(listenerTwo.assertOneGetNewAndReset(), fieldsConsume, new Object[]{null, null});

        epService.getEPRuntime().sendEvent(new SupportBean("A", 2));
        EPAssertionUtil.assertPropsMap((Map) listener.assertOneGetNewAndReset().get("val0"), fieldsInner, new Object[]{"X", 10});
        EPAssertionUtil.assertProps(listenerTwo.assertOneGetNewAndReset(), fieldsConsume, new Object[]{"X", 10});

        epService.getEPRuntime().sendEvent(new SupportBean("B", 3));
        EPAssertionUtil.assertPropsMap((Map) listener.assertOneGetNewAndReset().get("val0"), fieldsInner, new Object[]{"Y", 20});
        EPAssertionUtil.assertProps(listenerTwo.assertOneGetNewAndReset(), fieldsConsume, new Object[]{"Y", 20});
    }

    public void testAnnotationOrder() {
        String epl = "expression scalar {1} @Name('test') select scalar() from SupportBean_ST0";
        runAssertionAnnotation(epl);

        epl = "@Name('test') expression scalar {1} select scalar() from SupportBean_ST0";
        runAssertionAnnotation(epl);
    }

    private void runAssertionAnnotation(String epl) {
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);

        assertEquals(Integer.class, stmt.getEventType().getPropertyType("scalar()"));
        assertEquals("test", stmt.getName());

        epService.getEPRuntime().sendEvent(new SupportBean_ST0("E1", 1));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), "scalar()".split(","), new Object[]{1});

        stmt.destroy();
    }

    public void testSubqueryMultiresult() {
        String eplOne = "" +
                "expression maxi {" +
                " (select max(intPrimitive) from SupportBean.win:keepall())" +
                "} " +
                "expression mini {" +
                " (select min(intPrimitive) from SupportBean.win:keepall())" +
                "} " +
                "select p00/maxi() as val0, p00/mini() as val1 " +
                "from SupportBean_ST0.std:lastevent()";
        runAssertionMultiResult(eplOne);

        String eplTwo = "" +
                "expression subq {" +
                " (select max(intPrimitive) as maxi, min(intPrimitive) as mini from SupportBean.win:keepall())" +
                "} " +
                "select p00/subq().maxi as val0, p00/subq().mini as val1 " +
                "from SupportBean_ST0.std:lastevent()";
        runAssertionMultiResult(eplTwo);
    }

    private void runAssertionMultiResult(String epl) {
        String fields[] = new String[] {"val0","val1"};

        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 10));
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 5));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", 2));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{2 / 10d, 2 / 5d});

        epService.getEPRuntime().sendEvent(new SupportBean("E3", 20));
        epService.getEPRuntime().sendEvent(new SupportBean("E4", 2));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", 4));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{4 / 20d, 4 / 2d});

        stmt.destroy();
    }

    public void testSubqueryCross() {
        String fields[] = new String[] {"val1"};
        String epl = "" +
                "expression subq {" +
                " (x, y) => (select theString from SupportBean.win:keepall() where theString = x.id and intPrimitive = y.p10)" +
                "} " +
                "select subq(one, two) as val1 " +
                "from SupportBean_ST0.std:lastevent() as one, SupportBean_ST1.std:lastevent() as two";

        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        LambdaAssertionUtil.assertTypes(stmt.getEventType(), fields, new Class[]{String.class});

        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", 0));
        epService.getEPRuntime().sendEvent(new SupportBean_ST1("ST1", 20));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{null});

        epService.getEPRuntime().sendEvent(new SupportBean("ST0", 20));

        epService.getEPRuntime().sendEvent(new SupportBean_ST1("x", 20));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{"ST0"});
    }

    public void testSubqueryJoinSameField() {
        String fields[] = new String[] {"val1", "val2"};
        String epl = "" +
                "expression subq {" +
                " x => (select intPrimitive from SupportBean.win:keepall() where theString = x.pcommon)" +   // a common field
                "} " +
                "select subq(one) as val1, subq(two) as val2 " +
                "from SupportBean_ST0.std:lastevent() as one, SupportBean_ST1.std:lastevent() as two";

        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        LambdaAssertionUtil.assertTypes(stmt.getEventType(), fields, new Class[]{Integer.class, Integer.class});

        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", 0));
        epService.getEPRuntime().sendEvent(new SupportBean_ST1("ST1", 0));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{null, null});

        epService.getEPRuntime().sendEvent(new SupportBean("E0", 10));
        epService.getEPRuntime().sendEvent(new SupportBean_ST1("ST1", 0, "E0"));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{null, 10});

        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", 0, "E0"));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{10, 10});
    }

    public void testSubqueryCorrelated() {
        String fields[] = new String[] {"val0", "val1"};
        String epl = "" +
                "expression subqOne {" +
                " x => (select id from SupportBean_ST0.win:keepall() where p00 = x.intPrimitive)" +
                "} " +
                "select theString as val0, subqOne(t) as val1 from SupportBean as t";

        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        LambdaAssertionUtil.assertTypes(stmt.getEventType(), fields, new Class[]{String.class, String.class});

        epService.getEPRuntime().sendEvent(new SupportBean("E0", 0));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{"E0", null});

        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", 100));
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 99));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{"E1", null});

        epService.getEPRuntime().sendEvent(new SupportBean("E2", 100));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{"E2", "ST0"});

        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST1", 100));
        epService.getEPRuntime().sendEvent(new SupportBean("E3", 100));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{"E3", null});
    }

    public void testSubqueryUncorrelated() {
        String fields[] = new String[] {"val0", "val1"};
        String epl = "" +
                "expression subqOne {" +
                " (select id from SupportBean_ST0.std:lastevent())" +
                "} " +
                "select theString as val0, subqOne() as val1 from SupportBean as t";

        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        LambdaAssertionUtil.assertTypes(stmt.getEventType(), fields, new Class[]{String.class, String.class});

        epService.getEPRuntime().sendEvent(new SupportBean("E0", 0));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{"E0", null});

        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST0", 0));
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 99));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{"E1", "ST0"});

        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ST1", 0));
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 100));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{"E2", "ST1"});
    }

    public void testSubqueryNamedWindowUncorrelated() {
        epService.getEPAdministrator().createEPL(EventRepresentationEnum.MAP.getAnnotationText() + " create window MyWindow.win:keepall() as (val0 string, val1 int)");
        epService.getEPAdministrator().createEPL("insert into MyWindow (val0, val1) select theString, intPrimitive from SupportBean");

        String[] fieldsSelected = "c0,c1".split(",");
        String[] fieldsInside = "val0".split(",");
        String epl = "" +
                "expression subqnamedwin {" +
                "  MyWindow.where(x => x.val1 > 10).orderBy(x => x.val0)" +
                "} " +
                "select subqnamedwin() as c0, subqnamedwin().where(x => x.val1 < 100) as c1 from SupportBean_ST0 as t";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        LambdaAssertionUtil.assertTypes(stmt.getEventType(), fieldsSelected, new Class[]{Collection.class, Collection.class});

        epService.getEPRuntime().sendEvent(new SupportBean("E0", 0));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ID0", 0));
        EPAssertionUtil.assertPropsPerRow(toArrayMap((Collection) listener.assertOneGetNew().get("c0")), fieldsInside, null);
        EPAssertionUtil.assertPropsPerRow(toArrayMap((Collection) listener.assertOneGetNew().get("c1")), fieldsInside, null);
        listener.reset();

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 11));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ID1", 0));
        EPAssertionUtil.assertPropsPerRow(toArrayMap((Collection) listener.assertOneGetNew().get("c0")), fieldsInside, new Object[][]{{"E1"}});
        EPAssertionUtil.assertPropsPerRow(toArrayMap((Collection) listener.assertOneGetNew().get("c1")), fieldsInside, new Object[][]{{"E1"}});
        listener.reset();

        epService.getEPRuntime().sendEvent(new SupportBean("E2", 500));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ID2", 0));
        EPAssertionUtil.assertPropsPerRow(toArrayMap((Collection) listener.assertOneGetNew().get("c0")), fieldsInside, new Object[][]{{"E1"}, {"E2"}});
        EPAssertionUtil.assertPropsPerRow(toArrayMap((Collection) listener.assertOneGetNew().get("c1")), fieldsInside, new Object[][]{{"E1"}});
        listener.reset();
    }

    public void testSubqueryNamedWindowCorrelated() {

        String epl =    "expression subqnamedwin {" +
                        "  x => MyWindow(val0 = x.key0).where(y => val1 > 10)" +
                        "} " +
                        "select subqnamedwin(t) as c0 from SupportBean_ST0 as t";
        runAssertionSubqNWCorrelated(epl);

        // more or less prefixes
        epl =           "expression subqnamedwin {" +
                        "  x => MyWindow(val0 = x.key0).where(y => y.val1 > 10)" +
                        "} " +
                        "select subqnamedwin(t) as c0 from SupportBean_ST0 as t";
        runAssertionSubqNWCorrelated(epl);

        // with property-explicit stream name
        epl =    "expression subqnamedwin {" +
                        "  x => MyWindow(MyWindow.val0 = x.key0).where(y => y.val1 > 10)" +
                        "} " +
                        "select subqnamedwin(t) as c0 from SupportBean_ST0 as t";
        runAssertionSubqNWCorrelated(epl);

        // test ambiguous property names
        epService.getEPAdministrator().createEPL(EventRepresentationEnum.MAP.getAnnotationText() + " create window MyWindowTwo.win:keepall() as (id string, p00 int)");
        epService.getEPAdministrator().createEPL("insert into MyWindowTwo (id, p00) select theString, intPrimitive from SupportBean");
        epl =    "expression subqnamedwin {" +
                        "  x => MyWindowTwo(MyWindowTwo.id = x.id).where(y => y.p00 > 10)" +
                        "} " +
                        "select subqnamedwin(t) as c0 from SupportBean_ST0 as t";
        epService.getEPAdministrator().createEPL(epl);
    }

    private void runAssertionSubqNWCorrelated(String epl) {
        String[] fieldSelected = "c0".split(",");
        String[] fieldInside = "val0".split(",");

        epService.getEPAdministrator().createEPL(EventRepresentationEnum.MAP.getAnnotationText() + " create window MyWindow.win:keepall() as (val0 string, val1 int)");
        epService.getEPAdministrator().createEPL("insert into MyWindow (val0, val1) select theString, intPrimitive from SupportBean");
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        LambdaAssertionUtil.assertTypes(stmt.getEventType(), fieldSelected, new Class[]{Collection.class});

        epService.getEPRuntime().sendEvent(new SupportBean("E0", 0));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ID0", "x", 0));
        EPAssertionUtil.assertPropsPerRow(toArrayMap((Collection) listener.assertOneGetNew().get("c0")), fieldInside, null);
        listener.reset();

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 11));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ID1", "x", 0));
        EPAssertionUtil.assertPropsPerRow(toArrayMap((Collection) listener.assertOneGetNew().get("c0")), fieldInside, null);
        listener.reset();

        epService.getEPRuntime().sendEvent(new SupportBean("E2", 12));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("ID2", "E2", 0));
        EPAssertionUtil.assertPropsPerRow(toArrayMap((Collection) listener.assertOneGetNew().get("c0")), fieldInside, new Object[][]{{"E2"}});
        listener.reset();

        epService.getEPRuntime().sendEvent(new SupportBean("E3", 13));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("E3", "E3", 0));
        EPAssertionUtil.assertPropsPerRow(toArrayMap((Collection) listener.assertOneGetNew().get("c0")), fieldInside, new Object[][]{{"E3"}});
        listener.reset();

        epService.getEPAdministrator().destroyAllStatements();
    }

    public void testAggregationNoAccess() {
        String fields[] = new String[] {"val1", "val2", "val3", "val4"};
        String epl = "" +
                "expression sumA {x => " +
                "   sum(x.intPrimitive) " +
                "} " +
                "expression sumB {x => " +
                "   sum(x.intBoxed) " +
                "} " +
                "expression countC {" +
                "   count(*) " +
                "} " +
                "select sumA(t) as val1, sumB(t) as val2, sumA(t)/sumB(t) as val3, countC() as val4 from SupportBean as t";

        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        LambdaAssertionUtil.assertTypes(stmt.getEventType(), fields, new Class[]{Integer.class, Integer.class, Double.class, Long.class});

        epService.getEPRuntime().sendEvent(getSupportBean(5, 6));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{5, 6, 5 / 6d, 1L});

        epService.getEPRuntime().sendEvent(getSupportBean(8, 10));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{5 + 8, 6 + 10, (5 + 8) / (6d + 10d), 2L});
    }

    public void testSplitStream() {
        String epl =  "expression myLittleExpression { event => false }" +
                      "on SupportBean as myEvent " +
                      " insert into ABC select * where myLittleExpression(myEvent)" +
                      " insert into DEF select * where not myLittleExpression(myEvent)";
        epService.getEPAdministrator().createEPL(epl);
        
        epService.getEPAdministrator().createEPL("select * from DEF").addListener(listener);
        epService.getEPRuntime().sendEvent(new SupportBean());
        assertTrue(listener.isInvoked());
    }

    public void testAggregationAccess() {
        String epl = "" +
                "expression wb {s => " +
                "   window(*).where(y => y.intPrimitive > 2) " +
                "} " +
                "select wb(t) as val1 from SupportBean.win:keepall() as t";

        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        LambdaAssertionUtil.assertTypes(stmt.getEventType(), "val1".split(","), new Class[]{Collection.class, Collection.class});

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 2));
        SupportBean[] outArray = toArray((Collection)listener.assertOneGetNewAndReset().get("val1"));
        assertEquals(0, outArray.length);

        epService.getEPRuntime().sendEvent(new SupportBean("E2", 3));
        outArray = toArray((Collection)listener.assertOneGetNewAndReset().get("val1"));
        assertEquals(1, outArray.length);
        assertEquals("E2", outArray[0].getTheString());
    }

    public void testScalarReturn() {
        String epl = "" +
                "expression scalarfilter {s => " +
                "   strvals.where(y => y != 'E1') " +
                "} " +
                "select scalarfilter(t).where(x => x != 'E2') as val1 from SupportCollection as t";

        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        LambdaAssertionUtil.assertTypes(stmt.getEventType(), "val1".split(","), new Class[]{Collection.class});

        epService.getEPRuntime().sendEvent(SupportCollection.makeString("E1,E2,E3,E4"));
        LambdaAssertionUtil.assertValuesArrayScalar(listener, "val1", "E3", "E4");

        epService.getEPAdministrator().destroyAllStatements();

        // test with cast and with on-select and where-clause use
        epService.getEPAdministrator().getConfiguration().addEventType(MyEvent.class);
        epService.getEPAdministrator().getConfiguration().addEventType(SupportBean.class);

        listener.reset();
        epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as (myObject long)");
        epService.getEPAdministrator().createEPL("insert into MyWindow(myObject) select cast(intPrimitive, long) from SupportBean");
        stmt = epService.getEPAdministrator().createEPL("expression theExpression {" +
                "  myEvent => case when myEvent.myObject = 'X' " +
                "    then 0 else cast(myEvent.myObject, long) end " +
                "} " +
                "on MyEvent as myEvent select mw.* from MyWindow as mw where mw.myObject = theExpression(myEvent)");
        stmt.addListener(listener);
        String[] props = new String[] {"myObject"};

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 0));
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 1));

        epService.getEPRuntime().sendEvent(new MyEvent(2));
        assertFalse(listener.isInvoked());

        epService.getEPRuntime().sendEvent(new MyEvent("X"));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), props, new Object[] {0L});

        epService.getEPRuntime().sendEvent(new MyEvent(1));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), props, new Object[] {1L});
    }

    public void testEventTypeAndSODA() {

        String fields[] = new String[] {"fZero()", "fOne(t)", "fTwo(t, t)", "fThree(t, t)"};
        String epl = "" +
                "expression fZero {10} " +
                "expression fOne {x => x.intPrimitive} " +
                "expression fTwo {(x, y) => x.intPrimitive + y.intPrimitive} " +
                "expression fThree {(x, y) => x.intPrimitive + 100} " +
                "select fZero(), fOne(t), fTwo(t, t), fThree(t, t) from SupportBean as t";
        String eplFormatted = "" +
                "expression fZero {10}" + NEWLINE +
                "expression fOne {x => x.intPrimitive}" + NEWLINE +
                "expression fTwo {(x, y) => x.intPrimitive + y.intPrimitive}" + NEWLINE +
                "expression fThree {(x, y) => x.intPrimitive + 100}" + NEWLINE +
                "select fZero(), fOne(t), fTwo(t, t), fThree(t, t)" + NEWLINE +
                "from SupportBean as t";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);

        runAssertionTwoParameterArithmetic(stmt, fields);

        stmt.destroy();
        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(epl);
        assertEquals(epl, model.toEPL());
        assertEquals(eplFormatted, model.toEPL(new EPStatementFormatter(true)));
        stmt = epService.getEPAdministrator().create(model);
        assertEquals(epl, stmt.getText());
        stmt.addListener(listener);
        
        runAssertionTwoParameterArithmetic(stmt, fields);
    }

    private void runAssertionTwoParameterArithmetic(EPStatement stmt, String[] fields) {
        String[] props = stmt.getEventType().getPropertyNames();
        EPAssertionUtil.assertEqualsAnyOrder(props, fields);
        assertEquals(Integer.class, stmt.getEventType().getPropertyType("fZero()"));
        assertEquals(int.class, stmt.getEventType().getPropertyType("fOne(t)"));
        assertEquals(Integer.class, stmt.getEventType().getPropertyType("fTwo(t, t)"));
        assertEquals(Integer.class, stmt.getEventType().getPropertyType("fThree(t, t)"));
        EventPropertyGetter getter = stmt.getEventType().getGetter("fThree(t, t)");

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 11));
        EPAssertionUtil.assertProps(listener.assertOneGetNew(), fields, new Object[]{10, 11, 22, 111});
        assertEquals(111, getter.get(listener.assertOneGetNewAndReset()));
    }

    public void testOneParameterLambdaReturn() {

        String epl = "" +
                "expression one {x1 => x1.contained.where(y => y.p00 < 10) } " +
                "expression two {x2 => one(x2).where(y => y.p00 > 1)  } " +
                "select one(s0c) as val1, two(s0c) as val2 from SupportBean_ST0_Container as s0c";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        LambdaAssertionUtil.assertTypes(stmt.getEventType(), "val1,val2".split(","), new Class[]{Collection.class, Collection.class});

        SupportBean_ST0_Container theEvent = SupportBean_ST0_Container.make3Value("E1,K1,1", "E2,K2,2", "E20,K20,20");
        epService.getEPRuntime().sendEvent(theEvent);
        Object[] resultVal1 = ((Collection) listener.getLastNewData()[0].get("val1")).toArray();
        EPAssertionUtil.assertEqualsExactOrder(new Object[]{theEvent.getContained().get(0), theEvent.getContained().get(1)}, resultVal1
        );
        Object[] resultVal2 = ((Collection) listener.getLastNewData()[0].get("val2")).toArray();
        EPAssertionUtil.assertEqualsExactOrder(new Object[]{theEvent.getContained().get(1)}, resultVal2
        );
    }

    public void testNoParameterArithmetic() {

        String fields[] = "val1,val2".split(",");
        String epl = "" +
                "expression getEnumerationSource {1} " +
                "select getEnumerationSource() as val1, getEnumerationSource()*5 as val2 from SupportBean";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        LambdaAssertionUtil.assertTypes(stmt.getEventType(), fields, new Class[]{Integer.class, Integer.class});

        epService.getEPRuntime().sendEvent(new SupportBean());
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{1, 5});
    }

    public void testNoParameterVariable() {

        epService.getEPAdministrator().createEPL("create variable int myvar = 2");

        String fields[] = "val1,val2,val3".split(",");
        String epl = "" +
                "expression one {myvar} " +
                "expression two {myvar * 10} " +
                "select one() as val1, two() as val2, one() * two() as val3 from SupportBean";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);
        LambdaAssertionUtil.assertTypes(stmt.getEventType(), fields, new Class[]{Integer.class, Integer.class, Integer.class});

        epService.getEPRuntime().sendEvent(new SupportBean());
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{2, 20, 40});

        epService.getEPRuntime().setVariableValue("myvar", 3);
        epService.getEPRuntime().sendEvent(new SupportBean());
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{3, 30, 90});
    }

    public void testWhereClauseExpression() {

        String epl = "" +
                "expression one {x=>x.boolPrimitive} " +
                "select * from SupportBean as sb where one(sb)";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);

        epService.getEPRuntime().sendEvent(new SupportBean());
        assertFalse(listener.isInvoked());

        SupportBean theEvent = new SupportBean();
        theEvent.setBoolPrimitive(true);
        epService.getEPRuntime().sendEvent(theEvent);
        assertTrue(listener.isInvoked());
    }

    public void testInvalid() {

        String epl = "expression abc {(select * from SupportBean_ST0.std:lastevent() as st0 where p00=intPrimitive)} select abc() from SupportBean";
        tryInvalid(epl, "Error starting statement: Property named 'intPrimitive' is not valid in any stream [expression abc {(select * from SupportBean_ST0.std:lastevent() as st0 where p00=intPrimitive)} select abc() from SupportBean]");

        epl = "expression abc {x=>strvals.where(x=> x != 'E1')} select abc(str) from SupportCollection str";
        tryInvalid(epl, "Error starting statement: Error validating expression declaration 'abc': Error validating enumeration method 'where', the lambda-parameter name 'x' has already been declared in this context [expression abc {x=>strvals.where(x=> x != 'E1')} select abc(str) from SupportCollection str]");

        epl = "expression abc {avg(intPrimitive)} select abc() from SupportBean";
        tryInvalid(epl, "Error starting statement: Error validating expression declaration 'abc': Property named 'intPrimitive' is not valid in any stream [expression abc {avg(intPrimitive)} select abc() from SupportBean]");

        epl = "expression abc {(select * from SupportBean_ST0.std:lastevent() as st0 where p00=sb.intPrimitive)} select abc() from SupportBean sb";
        tryInvalid(epl, "Error starting statement: Failed to find a stream named 'sb' (did you mean 'st0'?) [expression abc {(select * from SupportBean_ST0.std:lastevent() as st0 where p00=sb.intPrimitive)} select abc() from SupportBean sb]");

        epl = "expression abc {window(*)} select abc() from SupportBean";
        tryInvalid(epl, "Error starting statement: Error validating expression declaration 'abc': The 'window' aggregation function requires that at least one stream is provided [expression abc {window(*)} select abc() from SupportBean]");

        epl = "expression abc {x => intPrimitive} select abc() from SupportBean";
        tryInvalid(epl, "Error starting statement: Parameter count mismatches for declared expression 'abc', expected 1 parameters but received 0 parameters [expression abc {x => intPrimitive} select abc() from SupportBean]");

        epl = "expression abc {intPrimitive} select abc(sb) from SupportBean sb";
        tryInvalid(epl, "Error starting statement: Parameter count mismatches for declared expression 'abc', expected 0 parameters but received 1 parameters [expression abc {intPrimitive} select abc(sb) from SupportBean sb]");

        epl = "expression abc {x=>} select abc(sb) from SupportBean sb";
        tryInvalid(epl, "Incorrect syntax near '}' at line 1 column 19 near reserved keyword 'select' [expression abc {x=>} select abc(sb) from SupportBean sb]");

        epl = "expression abc {intPrimitive} select abc() from SupportBean sb";
        tryInvalid(epl, "Error starting statement: Error validating expression declaration 'abc': Property named 'intPrimitive' is not valid in any stream [expression abc {intPrimitive} select abc() from SupportBean sb]");

        epl = "expression abc {x=>x} select abc(1) from SupportBean sb";
        tryInvalid(epl, "Error starting statement: Expression 'abc' requires a stream name as a parameter [expression abc {x=>x} select abc(1) from SupportBean sb]");

        epl = "expression abc {x=>intPrimitive} select * from SupportBean sb where abc(sb)";
        tryInvalid(epl, "Filter expression not returning a boolean value: 'abc(sb)' [expression abc {x=>intPrimitive} select * from SupportBean sb where abc(sb)]");
    }

    private SupportBean getSupportBean(int intPrimitive, Integer intBoxed) {
        SupportBean b = new SupportBean(null, intPrimitive);
        b.setIntBoxed(intBoxed);
        return b;
    }

    private void tryInvalid(String epl, String message) {
        try {
            epService.getEPAdministrator().createEPL(epl);
            fail();
        }
        catch (EPStatementException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    private SupportBean[] toArray(Collection it) {
        List<SupportBean> result = new ArrayList<SupportBean>();
        for (Object item : it) {
            result.add((SupportBean) item);
        }
        return result.toArray(new SupportBean[result.size()]);
    }

    private Map[] toArrayMap(Collection it) {
        if (it == null) {
            return null;
        }
        List<Map> result = new ArrayList<Map>();
        for (Object item : it) {
            Map map = (Map) item;
            result.add(map);
        }
        return result.toArray(new Map[result.size()]);
    }

    public static class MyEvent {
        private final Object myObject;

        public MyEvent(Object myObject) {
            this.myObject = myObject;
        }

        public Object getMyObject() {
            return myObject;
        }
    }

}
