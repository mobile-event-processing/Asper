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
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.core.service.EPStatementSPI;
import com.espertech.esper.core.service.StatementType;
import com.espertech.esper.support.bean.*;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.support.epl.SupportQueryPlanIndexHook;
import com.espertech.esper.support.util.IndexAssertion;
import com.espertech.esper.support.util.IndexAssertionEventSend;
import com.espertech.esper.support.util.IndexBackingTableInfo;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class TestNamedWindowSelect extends TestCase implements IndexBackingTableInfo
{
    private static Log log = LogFactory.getLog(TestNamedWindowSelect.class);

    private EPServiceProvider epService;
    private SupportUpdateListener listenerSelect;
    private SupportUpdateListener listenerSelectTwo;
    private SupportUpdateListener listenerConsumer;

    public void setUp()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        config.getEngineDefaults().getLogging().setEnableQueryPlan(true);
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
        listenerSelect = new SupportUpdateListener();
        listenerSelectTwo = new SupportUpdateListener();
        listenerConsumer = new SupportUpdateListener();
    }

    protected void tearDown() throws Exception {
        listenerSelect = null;
        listenerSelectTwo = null;
        listenerConsumer = null;
    }

    public void testWindowAgg() {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);
        epService.getEPAdministrator().getConfiguration().addEventType("S0", SupportBean_S0.class);
        epService.getEPAdministrator().getConfiguration().addEventType("S1", SupportBean_S1.class);

        epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as SupportBean");
        epService.getEPAdministrator().createEPL("insert into MyWindow select * from SupportBean");
        epService.getEPAdministrator().createEPL("on S1 as s1 delete from MyWindow where s1.p10 = theString");

        EPStatement stmt = epService.getEPAdministrator().createEPL("on S0 as s0 " +
                "select window(win.*) as c0," +
                "window(win.*).where(v => v.intPrimitive < 2) as c1, " +
                "window(win.*).toMap(k=>k.theString,v=>v.intPrimitive) as c2 " +
                "from MyWindow as win");
        stmt.addListener(listenerSelect);

        SupportBean[] beans = new SupportBean[3];
        for (int i = 0; i < beans.length; i++) {
            beans[i] = new SupportBean("E" + i, i);
        }

        epService.getEPRuntime().sendEvent(beans[0]);
        epService.getEPRuntime().sendEvent(beans[1]);
        epService.getEPRuntime().sendEvent(new SupportBean_S0(10));
        assertReceived(beans, new int[]{0, 1}, new int[]{0, 1}, "E0,E1".split(","), new Object[] {0,1});

        // add bean
        epService.getEPRuntime().sendEvent(beans[2]);
        epService.getEPRuntime().sendEvent(new SupportBean_S0(10));
        assertReceived(beans, new int[]{0, 1, 2}, new int[]{0, 1}, "E0,E1,E2".split(","), new Object[] {0,1, 2});

        // delete bean
        epService.getEPRuntime().sendEvent(new SupportBean_S1(11, "E1"));
        epService.getEPRuntime().sendEvent(new SupportBean_S0(12));
        assertReceived(beans, new int[]{0, 2}, new int[]{0}, "E0,E2".split(","), new Object[] {0,2});

        // delete another bean
        epService.getEPRuntime().sendEvent(new SupportBean_S1(13, "E0"));
        epService.getEPRuntime().sendEvent(new SupportBean_S0(14));
        assertReceived(beans, new int[]{2}, new int[0], "E2".split(","), new Object[] {2});

        // delete last bean
        epService.getEPRuntime().sendEvent(new SupportBean_S1(15, "E2"));
        epService.getEPRuntime().sendEvent(new SupportBean_S0(16));
        assertReceived(beans, null, null, null, null);
    }

    private void assertReceived(SupportBean[] beans, int[] indexesAll, int[] indexesWhere, String[] mapKeys, Object[] mapValues) {
        EventBean received = listenerSelect.assertOneGetNewAndReset();
        EPAssertionUtil.assertEqualsExactOrder(SupportBean.getBeansPerIndex(beans, indexesAll), (Object[]) received.get("c0"));
        EPAssertionUtil.assertEqualsExactOrder(SupportBean.getBeansPerIndex(beans, indexesWhere), (Collection) received.get("c1"));
        EPAssertionUtil.assertPropsMap((Map) received.get("c2"), mapKeys, mapValues);
    }

    public void testOnSelectIndexChoice() {
        epService.getEPAdministrator().getConfiguration().addEventType("SSB1", SupportSimpleBeanOne.class);
        epService.getEPAdministrator().getConfiguration().addEventType("SSB2", SupportSimpleBeanTwo.class);

        Object[] preloadedEventsOne = new Object[] {new SupportSimpleBeanOne("E1", 10, 11, 12), new SupportSimpleBeanOne("E2", 20, 21, 22)};
        IndexAssertionEventSend eventSendAssertion = new IndexAssertionEventSend() {
            public void run() {
                String[] fields = "ssb2.s2,ssb1.s1,ssb1.i1".split(",");
                epService.getEPRuntime().sendEvent(new SupportSimpleBeanTwo("E2", 50, 21, 22));
                EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{"E2", "E2", 20});
                epService.getEPRuntime().sendEvent(new SupportSimpleBeanTwo("E1", 60, 11, 12));
                EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[] {"E1", "E1", 10});
            }
        };

        // single index one field (std:unique(s1))
        assertIndexChoice(new String[0], preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = s2", null, BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", null, BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion("@Hint('index(One)')", "s1 = ssb2.s2 and l1 = ssb2.l2", null, BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion("@Hint('index(Two,bust)')", "s1 = ssb2.s2 and l1 = ssb2.l2"),// busted
                });

        // single index one field (std:unique(s1))
        String[] indexOneField = new String[] {"create unique index One on MyWindow (s1)"};
        assertIndexChoice(indexOneField, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = s2", "One", BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion("@Hint('index(One)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion("@Hint('index(Two,bust)')", "s1 = ssb2.s2 and l1 = ssb2.l2"),// busted
                });

        // single index two field  (std:unique(s1))
        String[] indexTwoField = new String[] {"create unique index One on MyWindow (s1, l1)"};
        assertIndexChoice(indexTwoField, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = ssb2.s2", null, BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_MULTI_UNIQUE, eventSendAssertion),
                });
        assertIndexChoice(indexTwoField, preloadedEventsOne, "win:keepall()",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = ssb2.s2", null, BACKING_SINGLE_DUPS_COERCEADD, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_MULTI_UNIQUE, eventSendAssertion),
                });

        // two index one unique  (std:unique(s1))
        String[] indexSetTwo = new String[] {
                "create index One on MyWindow (s1)",
                "create unique index Two on MyWindow (s1, d1)"};
        assertIndexChoice(indexSetTwo, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = ssb2.s2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", null, BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion("@Hint('index(One)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion("@Hint('index(Two,One)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion("@Hint('index(Two,bust)')", "s1 = ssb2.s2 and l1 = ssb2.l2"), // busted
                        new IndexAssertion("@Hint('index(explicit,bust)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and d1 = ssb2.d2 and l1 = ssb2.l2", "Two", BACKING_MULTI_UNIQUE, eventSendAssertion),
                        new IndexAssertion("@Hint('index(explicit,bust)')", "d1 = ssb2.d2 and l1 = ssb2.l2"), // busted
                });

        // two index one unique  (win:keepall)
        assertIndexChoice(indexSetTwo, preloadedEventsOne, "win:keepall()",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = ssb2.s2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", null, BACKING_MULTI_DUPS_COERCEADD, eventSendAssertion),
                        new IndexAssertion("@Hint('index(One)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion("@Hint('index(Two,One)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion("@Hint('index(Two,bust)')", "s1 = ssb2.s2 and l1 = ssb2.l2"), // busted
                        new IndexAssertion("@Hint('index(explicit,bust)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and d1 = ssb2.d2 and l1 = ssb2.l2", "Two", BACKING_MULTI_UNIQUE, eventSendAssertion),
                        new IndexAssertion("@Hint('index(explicit,bust)')", "d1 = ssb2.d2 and l1 = ssb2.l2"), // busted
                });

        // range  (std:unique(s1))
        IndexAssertionEventSend noAssertion = new IndexAssertionEventSend() {
            public void run() {
            }
        };
        String[] indexSetThree = new String[] {
                "create index One on MyWindow (i1 btree)",
                "create index Two on MyWindow (d1 btree)"};
        assertIndexChoice(indexSetThree, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "i1 between 1 and 10", "One", BACKING_SORTED_COERCED, noAssertion),
                        new IndexAssertion(null, "d1 between 1 and 10", "Two", BACKING_SORTED_COERCED, noAssertion),
                        new IndexAssertion("@Hint('index(One, bust)')", "d1 between 1 and 10"),// busted
                });
    }

    private void assertIndexChoice(String[] indexes, Object[] preloadedEvents, String datawindow,
                                   IndexAssertion ... assertions) {
        epService.getEPAdministrator().createEPL("create window MyWindow." + datawindow + " as SSB1");
        epService.getEPAdministrator().createEPL("insert into MyWindow select * from SSB1");
        for (String index : indexes) {
            epService.getEPAdministrator().createEPL(index);
        }
        for (Object event : preloadedEvents) {
            epService.getEPRuntime().sendEvent(event);
        }

        int count = 0;
        for (IndexAssertion assertion : assertions) {
            log.info("======= Testing #" + count++);
            String consumeEpl = INDEX_CALLBACK_HOOK +
                    (assertion.getHint() == null ? "" : assertion.getHint()) +
                    "on SSB2 as ssb2 " +
                    "select * " +
                    "from MyWindow as ssb1 where " + assertion.getWhereClause();

            EPStatement consumeStmt = null;
            try {
                consumeStmt = epService.getEPAdministrator().createEPL(consumeEpl);
            }
            catch (EPStatementException ex) {
                if (assertion.getEventSendAssertion() == null) {
                    // no assertion, expected
                    assertTrue(ex.getMessage().contains("index hint busted"));
                    continue;
                }
                throw new RuntimeException("Unexpected statement exception: " + ex.getMessage(), ex);
            }

            // assert index and access
            SupportQueryPlanIndexHook.assertOnExprAndReset(assertion.getExpectedIndexName(), assertion.getIndexBackingClass());
            consumeStmt.addListener(listenerSelect);
            assertion.getEventSendAssertion().run();
            consumeStmt.destroy();
        }

        epService.getEPAdministrator().destroyAllStatements();
    }

    public void testSelectAggregationHavingStreamWildcard()
    {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean_A", SupportBean_A.class);

        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as (a string, b int)";
        epService.getEPAdministrator().createEPL(stmtTextCreate);

        String stmtTextInsertOne = "insert into MyWindow select theString as a, intPrimitive as b from SupportBean";
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        String stmtTextSelect = "on SupportBean_A select mwc.* as mwcwin from MyWindow mwc where id = a group by a having sum(b) = 20";
        EPStatementSPI select = (EPStatementSPI) epService.getEPAdministrator().createEPL(stmtTextSelect);
        assertFalse(select.getStatementContext().isStatelessSelect());
        select.addListener(listenerSelect);

        // send 3 event
        sendSupportBean("E1", 16);
        sendSupportBean("E2", 2);
        sendSupportBean("E1", 4);

        // fire trigger
        sendSupportBean_A("E1");
        EventBean[] events = listenerSelect.getLastNewData();
        assertEquals(2, events.length);
        assertEquals("E1", events[0].get("mwcwin.a"));
        assertEquals("E1", events[1].get("mwcwin.a"));
    }

    public void testPatternTimedSelect()
    {
        // test for JIRA ESPER-332
        sendTimer(0, epService);

        String stmtTextCreate = "create window MyWindow.win:keepall() as select * from " + SupportBean.class.getName();
        epService.getEPAdministrator().createEPL(stmtTextCreate);

        String stmtCount = "on pattern[every timer:interval(10 sec)] select count(eve), eve from MyWindow as eve";
        epService.getEPAdministrator().createEPL(stmtCount);

        String stmtTextOnSelect = "on pattern [ every timer:interval(10 sec)] select theString from MyWindow having count(theString) > 0";
        EPStatement stmt = epService.getEPAdministrator().createEPL(stmtTextOnSelect);
        stmt.addListener(listenerConsumer);

        String stmtTextInsertOne = "insert into MyWindow select * from " + SupportBean.class.getName();
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        sendTimer(11000, epService);
        assertFalse(listenerConsumer.isInvoked());

        sendTimer(21000, epService);
        assertFalse(listenerConsumer.isInvoked());

        sendSupportBean("E1", 1);
        sendTimer(31000, epService);
        assertEquals("E1", listenerConsumer.assertOneGetNewAndReset().get("theString"));
    }

    public void testInsertIntoWildcard()
    {
        String[] fields = new String[] {"theString", "intPrimitive"};

        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as select * from " + SupportBean.class.getName();
        EPStatement stmtCreate = epService.getEPAdministrator().createEPL(stmtTextCreate);

        // create insert into
        String stmtTextInsertOne = "insert into MyWindow select * from " + SupportBean.class.getName() + "(theString like 'E%')";
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        // create on-select stmt
        String stmtTextSelect = "on " + SupportBean_A.class.getName() + " insert into MyStream select mywin.* from MyWindow as mywin order by theString asc";
        EPStatement stmtSelect = epService.getEPAdministrator().createEPL(stmtTextSelect);
        stmtSelect.addListener(listenerSelect);
        assertEquals(StatementType.ON_INSERT, ((EPStatementSPI) stmtSelect).getStatementMetadata().getStatementType());

        // create consuming statement
        String stmtTextConsumer = "select * from default.MyStream";
        EPStatement stmtConsumer = epService.getEPAdministrator().createEPL(stmtTextConsumer);
        stmtConsumer.addListener(listenerConsumer);

        // create second inserting statement
        String stmtTextInsertTwo = "insert into MyStream select * from " + SupportBean.class.getName() + "(theString like 'I%')";
        epService.getEPAdministrator().createEPL(stmtTextInsertTwo);

        // send event
        sendSupportBean("E1", 1);
        assertFalse(listenerSelect.isInvoked());
        assertFalse(listenerConsumer.isInvoked());
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}});

        // fire trigger
        sendSupportBean_A("A1");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{"E1", 1});
        EPAssertionUtil.assertProps(listenerConsumer.assertOneGetNewAndReset(), fields, new Object[]{"E1", 1});

        // insert via 2nd insert into
        sendSupportBean("I2", 2);
        assertFalse(listenerSelect.isInvoked());
        EPAssertionUtil.assertProps(listenerConsumer.assertOneGetNewAndReset(), fields, new Object[]{"I2", 2});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}});

        // send event
        sendSupportBean("E3", 3);
        assertFalse(listenerSelect.isInvoked());
        assertFalse(listenerConsumer.isInvoked());
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}, {"E3", 3}});

        // fire trigger
        sendSupportBean_A("A2");
        assertEquals(1, listenerSelect.getNewDataList().size());
        EPAssertionUtil.assertPropsPerRow(listenerSelect.getLastNewData(), fields, new Object[][]{{"E1", 1}, {"E3", 3}});
        listenerSelect.reset();
        assertEquals(2, listenerConsumer.getNewDataList().size());
        EPAssertionUtil.assertPropsPerRow(listenerConsumer.getNewDataListFlattened(), fields, new Object[][]{{"E1", 1}, {"E3", 3}});
        listenerConsumer.reset();

        // check type
        EventType consumerType = stmtConsumer.getEventType();
        assertEquals(String.class, consumerType.getPropertyType("theString"));
        assertTrue(consumerType.getPropertyNames().length > 10);
        assertEquals(SupportBean.class, consumerType.getUnderlyingType());

        // check type
        EventType onSelectType = stmtSelect.getEventType();
        assertEquals(String.class, onSelectType.getPropertyType("theString"));
        assertTrue(onSelectType.getPropertyNames().length > 10);
        assertEquals(SupportBean.class, onSelectType.getUnderlyingType());

        // delete all from named window
        String stmtTextDelete = "on " + SupportBean_B.class.getName() + " delete from MyWindow";
        epService.getEPAdministrator().createEPL(stmtTextDelete);
        sendSupportBean_B("B1");

        // fire trigger - nothing to insert
        sendSupportBean_A("A3");

        stmtConsumer.destroy();
        stmtSelect.destroy();
        stmtCreate.destroy();
    }

    public void testInvalid()
    {
        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as select * from " + SupportBean.class.getName();
        epService.getEPAdministrator().createEPL(stmtTextCreate);

        tryInvalid("on " + SupportBean_A.class.getName() + " select * from MyWindow where sum(intPrimitive) > 100",
                   "Error validating expression: An aggregate function may not appear in a WHERE clause (use the HAVING clause) [on com.espertech.esper.support.bean.SupportBean_A select * from MyWindow where sum(intPrimitive) > 100]");

        tryInvalid("on " + SupportBean_A.class.getName() + " insert into MyStream select * from DUMMY",
                   "Named window 'DUMMY' has not been declared [on com.espertech.esper.support.bean.SupportBean_A insert into MyStream select * from DUMMY]");

        tryInvalid("on " + SupportBean_A.class.getName() + " select prev(1, theString) from MyWindow",
                   "Error starting statement: Previous function cannot be used in this context [on com.espertech.esper.support.bean.SupportBean_A select prev(1, theString) from MyWindow]");
    }

    private void tryInvalid(String text, String message)
    {
        try
        {
            epService.getEPAdministrator().createEPL(text);
            fail();
        }
        catch (EPStatementException ex)
        {
            assertEquals(message, ex.getMessage());
        }
    }

    public void testSelectCondition()
    {
        String[] fieldsCreate = new String[] {"a", "b"};
        String[] fieldsOnSelect = new String[] {"a", "b", "id"};

        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        EPStatement stmtCreate = epService.getEPAdministrator().createEPL(stmtTextCreate);

        // create select stmt
        String stmtTextSelect = "on " + SupportBean_A.class.getName() + " select mywin.*, id from MyWindow as mywin where MyWindow.b < 3 order by a asc";
        EPStatement stmtSelect = epService.getEPAdministrator().createEPL(stmtTextSelect);
        stmtSelect.addListener(listenerSelect);
        assertEquals(StatementType.ON_SELECT, ((EPStatementSPI) stmtSelect).getStatementMetadata().getStatementType());

        // create insert into
        String stmtTextInsertOne = "insert into MyWindow select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        // send 3 event
        sendSupportBean("E1", 1);
        sendSupportBean("E2", 2);
        sendSupportBean("E3", 3);
        assertFalse(listenerSelect.isInvoked());

        // fire trigger
        sendSupportBean_A("A1");
        assertEquals(2, listenerSelect.getLastNewData().length);
        EPAssertionUtil.assertProps(listenerSelect.getLastNewData()[0], fieldsCreate, new Object[]{"E1", 1});
        EPAssertionUtil.assertProps(listenerSelect.getLastNewData()[1], fieldsCreate, new Object[]{"E2", 2});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fieldsCreate, new Object[][]{{"E1", 1}, {"E2", 2}, {"E3", 3}});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fieldsOnSelect, new Object[][]{{"E1", 1, "A1"}, {"E2", 2, "A1"}});

        sendSupportBean("E4", 0);
        sendSupportBean_A("A2");
        assertEquals(3, listenerSelect.getLastNewData().length);
        EPAssertionUtil.assertProps(listenerSelect.getLastNewData()[0], fieldsOnSelect, new Object[]{"E1", 1, "A2"});
        EPAssertionUtil.assertProps(listenerSelect.getLastNewData()[1], fieldsOnSelect, new Object[]{"E2", 2, "A2"});
        EPAssertionUtil.assertProps(listenerSelect.getLastNewData()[2], fieldsOnSelect, new Object[]{"E4", 0, "A2"});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fieldsCreate, new Object[][]{{"E1", 1}, {"E2", 2}, {"E3", 3}, {"E4", 0}});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fieldsCreate, new Object[][]{{"E1", 1}, {"E2", 2}, {"E4", 0}});

        stmtSelect.destroy();
        stmtCreate.destroy();
    }

    public void testSelectJoinColumnsLimit()
    {
        String[] fields = new String[] {"triggerid", "wina", "b"};

        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        EPStatement stmtCreate = epService.getEPAdministrator().createEPL(stmtTextCreate);

        // create select stmt
        String stmtTextSelect = "on " + SupportBean_A.class.getName() + " as trigger select trigger.id as triggerid, win.a as wina, b from MyWindow as win order by wina";
        EPStatement stmtSelect = epService.getEPAdministrator().createEPL(stmtTextSelect);
        stmtSelect.addListener(listenerSelect);

        // create insert into
        String stmtTextInsertOne = "insert into MyWindow select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        // send 3 event
        sendSupportBean("E1", 1);
        sendSupportBean("E2", 2);
        assertFalse(listenerSelect.isInvoked());

        // fire trigger
        sendSupportBean_A("A1");
        assertEquals(2, listenerSelect.getLastNewData().length);
        EPAssertionUtil.assertProps(listenerSelect.getLastNewData()[0], fields, new Object[]{"A1", "E1", 1});
        EPAssertionUtil.assertProps(listenerSelect.getLastNewData()[1], fields, new Object[]{"A1", "E2", 2});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{"A1", "E1", 1}, {"A1", "E2", 2}});

        // try limit clause
        stmtSelect.destroy();
        stmtTextSelect = "on " + SupportBean_A.class.getName() + " as trigger select trigger.id as triggerid, win.a as wina, b from MyWindow as win order by wina limit 1";
        stmtSelect = epService.getEPAdministrator().createEPL(stmtTextSelect);
        stmtSelect.addListener(listenerSelect);

        sendSupportBean_A("A1");
        assertEquals(1, listenerSelect.getLastNewData().length);
        EPAssertionUtil.assertProps(listenerSelect.getLastNewData()[0], fields, new Object[]{"A1", "E1", 1});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{"A1", "E1", 1}});

        stmtCreate.destroy();
    }

    public void testSelectAggregation()
    {
        String[] fields = new String[] {"sumb"};

        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        EPStatement stmtCreate = epService.getEPAdministrator().createEPL(stmtTextCreate);

        // create select stmt
        String stmtTextSelect = "on " + SupportBean_A.class.getName() + " select sum(b) as sumb from MyWindow";
        EPStatement stmtSelect = epService.getEPAdministrator().createEPL(stmtTextSelect);
        stmtSelect.addListener(listenerSelect);

        // create insert into
        String stmtTextInsertOne = "insert into MyWindow select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        // send 3 event
        sendSupportBean("E1", 1);
        sendSupportBean("E2", 2);
        sendSupportBean("E3", 3);
        assertFalse(listenerSelect.isInvoked());

        // fire trigger
        sendSupportBean_A("A1");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{6});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{6}});

        // create delete stmt
        String stmtTextDelete = "on " + SupportBean_B.class.getName() + " delete from MyWindow where id = a";
        epService.getEPAdministrator().createEPL(stmtTextDelete);

        // Delete E2
        sendSupportBean_B("E2");

        // fire trigger
        sendSupportBean_A("A2");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{4});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{4}});

        sendSupportBean("E4", 10);
        sendSupportBean_A("A3");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{14});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{14}});

        EventType resultType = stmtSelect.getEventType();
        assertEquals(1, resultType.getPropertyNames().length);
        assertEquals(Integer.class, resultType.getPropertyType("sumb"));

        stmtSelect.destroy();
        stmtCreate.destroy();
    }

    public void testSelectAggregationCorrelated()
    {
        String[] fields = new String[] {"sumb"};

        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        EPStatement stmtCreate = epService.getEPAdministrator().createEPL(stmtTextCreate);

        // create select stmt
        String stmtTextSelect = "on " + SupportBean_A.class.getName() + " select sum(b) as sumb from MyWindow where a = id";
        EPStatement stmtSelect = epService.getEPAdministrator().createEPL(stmtTextSelect);
        stmtSelect.addListener(listenerSelect);

        // create insert into
        String stmtTextInsertOne = "insert into MyWindow select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        // send 3 event
        sendSupportBean("E1", 1);
        sendSupportBean("E2", 2);
        sendSupportBean("E3", 3);
        assertFalse(listenerSelect.isInvoked());

        // fire trigger
        sendSupportBean_A("A1");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{null});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{null}});

        // fire trigger
        sendSupportBean_A("E2");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{2});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{2}});

        sendSupportBean("E2", 10);
        sendSupportBean_A("E2");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{12});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{12}});

        EventType resultType = stmtSelect.getEventType();
        assertEquals(1, resultType.getPropertyNames().length);
        assertEquals(Integer.class, resultType.getPropertyType("sumb"));

        stmtSelect.destroy();
        stmtCreate.destroy();
    }

    public void testSelectAggregationGrouping()
    {
        String[] fields = new String[] {"a", "sumb"};

        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        EPStatement stmtCreate = epService.getEPAdministrator().createEPL(stmtTextCreate);

        // create select stmt
        String stmtTextSelect = "on " + SupportBean_A.class.getName() + " select a, sum(b) as sumb from MyWindow group by a order by a desc";
        EPStatement stmtSelect = epService.getEPAdministrator().createEPL(stmtTextSelect);
        stmtSelect.addListener(listenerSelect);

        // create select stmt
        String stmtTextSelectTwo = "on " + SupportBean_A.class.getName() + " select a, sum(b) as sumb from MyWindow group by a having sum(b) > 5 order by a desc";
        EPStatement stmtSelectTwo = epService.getEPAdministrator().createEPL(stmtTextSelectTwo);
        stmtSelectTwo.addListener(listenerSelectTwo);

        // create insert into
        String stmtTextInsertOne = "insert into MyWindow select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        // fire trigger
        sendSupportBean_A("A1");
        assertFalse(listenerSelect.isInvoked());
        assertFalse(listenerSelectTwo.isInvoked());

        // send 3 events
        sendSupportBean("E1", 1);
        sendSupportBean("E2", 2);
        sendSupportBean("E1", 5);
        assertFalse(listenerSelect.isInvoked());
        assertFalse(listenerSelectTwo.isInvoked());

        // fire trigger
        sendSupportBean_A("A1");
        EPAssertionUtil.assertPropsPerRow(listenerSelect.getLastNewData(), fields, new Object[][]{{"E2", 2}, {"E1", 6}});
        assertNull(listenerSelect.getLastOldData());
        listenerSelect.reset();
        EPAssertionUtil.assertPropsPerRow(listenerSelectTwo.getLastNewData(), fields, new Object[][]{{"E1", 6}});
        assertNull(listenerSelect.getLastOldData());
        listenerSelect.reset();

        // send 3 events
        sendSupportBean("E4", -1);
        sendSupportBean("E2", 10);
        sendSupportBean("E1", 100);
        assertFalse(listenerSelect.isInvoked());

        sendSupportBean_A("A2");
        EPAssertionUtil.assertPropsPerRow(listenerSelect.getLastNewData(), fields, new Object[][]{{"E4", -1}, {"E2", 12}, {"E1", 106}});

        // create delete stmt, delete E2
        String stmtTextDelete = "on " + SupportBean_B.class.getName() + " delete from MyWindow where id = a";
        epService.getEPAdministrator().createEPL(stmtTextDelete);
        sendSupportBean_B("E2");

        sendSupportBean_A("A3");
        EPAssertionUtil.assertPropsPerRow(listenerSelect.getLastNewData(), fields, new Object[][]{{"E4", -1}, {"E1", 106}});
        assertNull(listenerSelect.getLastOldData());
        listenerSelect.reset();
        EPAssertionUtil.assertPropsPerRow(listenerSelectTwo.getLastNewData(), fields, new Object[][]{{"E1", 106}});
        assertNull(listenerSelectTwo.getLastOldData());
        listenerSelectTwo.reset();

        EventType resultType = stmtSelect.getEventType();
        assertEquals(2, resultType.getPropertyNames().length);
        assertEquals(String.class, resultType.getPropertyType("a"));
        assertEquals(Integer.class, resultType.getPropertyType("sumb"));

        stmtSelect.destroy();
        stmtCreate.destroy();
        stmtSelectTwo.destroy();
    }

    public void testSelectCorrelationDelete()
    {
        String[] fields = new String[] {"a", "b"};

        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        EPStatement stmtCreate = epService.getEPAdministrator().createEPL(stmtTextCreate);

        // create select stmt
        String stmtTextSelect = "on " + SupportBean_A.class.getName() + " select mywin.* from MyWindow as mywin where id = a";
        EPStatement stmtSelect = epService.getEPAdministrator().createEPL(stmtTextSelect);
        stmtSelect.addListener(listenerSelect);

        // create insert into
        String stmtTextInsertOne = "insert into MyWindow select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        // create delete stmt
        String stmtTextDelete = "on " + SupportBean_B.class.getName() + " delete from MyWindow where a = id";
        EPStatement stmtDelete = epService.getEPAdministrator().createEPL(stmtTextDelete);

        // send 3 event
        sendSupportBean("E1", 1);
        sendSupportBean("E2", 2);
        sendSupportBean("E3", 3);
        assertFalse(listenerSelect.isInvoked());

        // fire trigger
        sendSupportBean_A("X1");
        assertFalse(listenerSelect.isInvoked());
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}, {"E2", 2}, {"E3", 3}});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, null);

        sendSupportBean_A("E2");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{"E2", 2});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{"E2", 2}});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}, {"E2", 2}, {"E3", 3}});

        sendSupportBean_A("E1");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{"E1", 1});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{"E1", 1}});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}, {"E2", 2}, {"E3", 3}});

        // delete event
        sendSupportBean_B("E1");
        assertFalse(listenerSelect.isInvoked());

        sendSupportBean_A("E1");
        assertFalse(listenerSelect.isInvoked());
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E2", 2}, {"E3", 3}});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, null);

        sendSupportBean_A("E2");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{"E2", 2});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{"E2", 2}});

        stmtSelect.destroy();
        stmtDelete.destroy();
        stmtCreate.destroy();
    }

    public void testPatternCorrelation()
    {
        String[] fields = new String[] {"a", "b"};

        // create window
        String stmtTextCreate = "create window MyWindow.win:keepall() as select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        EPStatement stmtCreate = epService.getEPAdministrator().createEPL(stmtTextCreate);

        // create select stmt
        String stmtTextSelect = "on pattern [every ea=" + SupportBean_A.class.getName() +
                                " or every eb=" + SupportBean_B.class.getName() + "] select mywin.* from MyWindow as mywin where a = coalesce(ea.id, eb.id)";
        EPStatement stmtSelect = epService.getEPAdministrator().createEPL(stmtTextSelect);
        stmtSelect.addListener(listenerSelect);

        // create insert into
        String stmtTextInsertOne = "insert into MyWindow select theString as a, intPrimitive as b from " + SupportBean.class.getName();
        epService.getEPAdministrator().createEPL(stmtTextInsertOne);

        // send 3 event
        sendSupportBean("E1", 1);
        sendSupportBean("E2", 2);
        sendSupportBean("E3", 3);
        assertFalse(listenerSelect.isInvoked());

        // fire trigger
        sendSupportBean_A("X1");
        assertFalse(listenerSelect.isInvoked());
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}, {"E2", 2}, {"E3", 3}});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, null);

        sendSupportBean_B("E2");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{"E2", 2});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{"E2", 2}});

        sendSupportBean_A("E1");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{"E1", 1});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{"E1", 1}});

        sendSupportBean_B("E3");
        EPAssertionUtil.assertProps(listenerSelect.assertOneGetNewAndReset(), fields, new Object[]{"E3", 3});
        EPAssertionUtil.assertPropsPerRow(stmtSelect.iterator(), fields, new Object[][]{{"E3", 3}});
        EPAssertionUtil.assertPropsPerRow(stmtCreate.iterator(), fields, new Object[][]{{"E1", 1}, {"E2", 2}, {"E3", 3}});

        stmtCreate.destroy();
        stmtSelect.destroy();
    }

    private SupportBean_A sendSupportBean_A(String id)
    {
        SupportBean_A bean = new SupportBean_A(id);
        epService.getEPRuntime().sendEvent(bean);
        return bean;
    }

    private SupportBean_B sendSupportBean_B(String id)
    {
        SupportBean_B bean = new SupportBean_B(id);
        epService.getEPRuntime().sendEvent(bean);
        return bean;
    }

    private SupportBean sendSupportBean(String theString, int intPrimitive)
    {
        SupportBean bean = new SupportBean();
        bean.setTheString(theString);
        bean.setIntPrimitive(intPrimitive);
        epService.getEPRuntime().sendEvent(bean);
        return bean;
    }

    private void sendTimer(long timeInMSec, EPServiceProvider epService)
    {
        CurrentTimeEvent theEvent = new CurrentTimeEvent(timeInMSec);
        EPRuntime runtime = epService.getEPRuntime();
        runtime.sendEvent(theEvent);
    }
}
