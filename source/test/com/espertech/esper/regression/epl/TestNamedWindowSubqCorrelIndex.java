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
import com.espertech.esper.epl.join.util.QueryPlanIndexDescSubquery;
import com.espertech.esper.support.bean.*;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.support.epl.SupportQueryPlanIndexHook;
import com.espertech.esper.support.util.IndexAssertion;
import com.espertech.esper.support.util.IndexAssertionEventSend;
import com.espertech.esper.support.util.IndexBackingTableInfo;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class TestNamedWindowSubqCorrelIndex extends TestCase implements IndexBackingTableInfo
{
    private static Log log = LogFactory.getLog(TestNamedWindowSubqCorrelIndex.class);

    private EPServiceProvider epService;
    private EPRuntime epRuntime;
    private SupportUpdateListener listenerStmtOne;

    public void setUp()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        config.getEngineDefaults().getLogging().setEnableQueryPlan(true);
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
        epRuntime = epService.getEPRuntime();
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);
        epService.getEPAdministrator().getConfiguration().addEventType("ABean", SupportBean_S0.class);
        epService.getEPAdministrator().getConfiguration().addEventType("SB2", SupportBeanTwo.class);
        listenerStmtOne = new SupportUpdateListener();
    }

    protected void tearDown() throws Exception {
        listenerStmtOne = null;
        epRuntime = null;
    }

    public void testNoShare() {
        runAssertion(false, false, false, false);
    }

    public void testNoShareSetnoindex() {
        runAssertion(false, false, false, true);
    }

    public void testNoShareCreate() {
        runAssertion(false, false, true, false);
    }

    public void testShare() {
        runAssertion(true, false, false, false);
    }

    public void testShareCreate() {
        runAssertion(true, false, true, false);
    }

    public void testShareCreateSetnoindex() {
        runAssertion(true, false, true, true);
    }

    public void testDisableShare() {
        runAssertion(true, true, false, false);
    }

    public void testDisableShareCreate() {
        runAssertion(true, true, true, false);
    }

    public void testMultipleIndexHints() {
        epService.getEPAdministrator().getConfiguration().addEventType("SSB1", SupportSimpleBeanOne.class);
        epService.getEPAdministrator().getConfiguration().addEventType("SSB2", SupportSimpleBeanTwo.class);

        epService.getEPAdministrator().createEPL("@Hint('enable_window_subquery_indexshare') create window MyWindow.win:keepall() as select * from SSB1");
        epService.getEPAdministrator().createEPL("create unique index I1 on MyWindow (s1)");
        epService.getEPAdministrator().createEPL("create unique index I2 on MyWindow (i1)");

        epService.getEPAdministrator().createEPL(INDEX_CALLBACK_HOOK +
                "@Hint('index(subquery(1), I1, bust)')\n" +
                "@Hint('index(subquery(0), I2, bust)')\n" +
                "select " +
                "(select * from MyWindow where s1 = ssb2.s2 and i1 = ssb2.i2) as sub1," +
                "(select * from MyWindow where i1 = ssb2.i2 and s1 = ssb2.s2) as sub2 " +
                "from SSB2 ssb2");
        List<QueryPlanIndexDescSubquery> subqueries = SupportQueryPlanIndexHook.getAndResetSubqueries();
        SupportQueryPlanIndexHook.assertSubquery(subqueries.get(0), 0, "I2", BACKING_SINGLE_UNIQUE);
        SupportQueryPlanIndexHook.assertSubquery(subqueries.get(1), 1, "I1", BACKING_SINGLE_UNIQUE);
    }

    public void testIndexShareIndexChoice() {
        epService.getEPAdministrator().getConfiguration().addEventType("SSB1", SupportSimpleBeanOne.class);
        epService.getEPAdministrator().getConfiguration().addEventType("SSB2", SupportSimpleBeanTwo.class);

        Object[] preloadedEventsOne = new Object[] {new SupportSimpleBeanOne("E1", 10, 11, 12), new SupportSimpleBeanOne("E2", 20, 21, 22)};
        IndexAssertionEventSend eventSendAssertion = new IndexAssertionEventSend() {
            public void run() {
                String[] fields = "s2,ssb1.s1,ssb1.i1".split(",");
                epRuntime.sendEvent(new SupportSimpleBeanTwo("E2", 50, 21, 22));
                EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fields, new Object[]{"E2", "E2", 20});
                epRuntime.sendEvent(new SupportSimpleBeanTwo("E1", 60, 11, 12));
                EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fields, new Object[] {"E1", "E1", 10});
            }
        };

        // no index one field (essentially duplicated since declared std:unique)
        String[] noindexes = new String[] {};
        assertIndexChoice(true, noindexes, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = ssb2.s2", null, BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", null, BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion("@Hint('index(One)')", "s1 = ssb2.s2 and l1 = ssb2.l2", null, BACKING_SINGLE_UNIQUE, eventSendAssertion),
                });

        // single index one field (essentially duplicated since declared std:unique)
        String[] indexOneField = new String[] {"create unique index One on MyWindow (s1)"};
        assertIndexChoice(true, indexOneField, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = ssb2.s2", "One", BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion("@Hint('index(One)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_UNIQUE, eventSendAssertion),
                });

        // single index two field
        String[] indexTwoField = new String[] {"create unique index One on MyWindow (s1, l1)"};
        assertIndexChoice(true, indexTwoField, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = ssb2.s2", null, BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_MULTI_UNIQUE, eventSendAssertion),
                });

        // two index one unique with std:unique(s1)
        String[] indexSetTwo = new String[] {
                "create index One on MyWindow (s1)",
                "create unique index Two on MyWindow (s1, d1)"};
        assertIndexChoice(true, indexSetTwo, preloadedEventsOne, "std:unique(s1)",
            new IndexAssertion[] {
                new IndexAssertion(null, "d1 = ssb2.d2", null, BACKING_SINGLE_DUPS, eventSendAssertion),
                new IndexAssertion(null, "s1 = ssb2.s2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", null, BACKING_SINGLE_UNIQUE, eventSendAssertion),
                new IndexAssertion("@Hint('index(One)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                new IndexAssertion("@Hint('index(Two,One)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                new IndexAssertion("@Hint('index(Two,bust)')", "s1 = ssb2.s2 and l1 = ssb2.l2"), // busted
                new IndexAssertion("@Hint('index(explicit,bust)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                new IndexAssertion(null, "s1 = ssb2.s2 and d1 = ssb2.d2 and l1 = ssb2.l2", "Two", BACKING_MULTI_UNIQUE, eventSendAssertion),
                new IndexAssertion("@Hint('index(explicit,bust)')", "d1 = ssb2.d2 and l1 = ssb2.l2") // busted
            });

        // two index one unique with keep-all
        assertIndexChoice(true, indexSetTwo, preloadedEventsOne, "win:keepall()",
                new IndexAssertion[] {
                        new IndexAssertion(null, "d1 = ssb2.d2", null, BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", null, BACKING_MULTI_DUPS, eventSendAssertion),
                        new IndexAssertion("@Hint('index(One)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion("@Hint('index(Two,One)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion("@Hint('index(Two,bust)')", "s1 = ssb2.s2 and l1 = ssb2.l2"), // busted
                        new IndexAssertion("@Hint('index(explicit,bust)')", "s1 = ssb2.s2 and l1 = ssb2.l2", "One", BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and d1 = ssb2.d2 and l1 = ssb2.l2", "Two", BACKING_MULTI_UNIQUE, eventSendAssertion),
                        new IndexAssertion("@Hint('index(explicit,bust)')", "d1 = ssb2.d2 and l1 = ssb2.l2") // busted
                });

        // range
        IndexAssertionEventSend noAssertion = new IndexAssertionEventSend() {
            public void run() {
            }
        };
        String[] indexSetThree = new String[] {
                "create index One on MyWindow (i1 btree)",
                "create index Two on MyWindow (d1 btree)"};
        assertIndexChoice(true, indexSetThree, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "i1 between 1 and 10", "One", BACKING_SORTED_COERCED, noAssertion),
                        new IndexAssertion(null, "d1 between 1 and 10", "Two", BACKING_SORTED_COERCED, noAssertion),
                        new IndexAssertion("@Hint('index(One, bust)')", "d1 between 1 and 10"), // busted
                });
    }

    public void testNoIndexShareIndexChoice() {
        epService.getEPAdministrator().getConfiguration().addEventType("SSB1", SupportSimpleBeanOne.class);
        epService.getEPAdministrator().getConfiguration().addEventType("SSB2", SupportSimpleBeanTwo.class);

        Object[] preloadedEventsOne = new Object[] {new SupportSimpleBeanOne("E1", 10, 11, 12), new SupportSimpleBeanOne("E2", 20, 21, 22)};
        IndexAssertionEventSend eventSendAssertion = new IndexAssertionEventSend() {
            public void run() {
                String[] fields = "s2,ssb1.s1,ssb1.i1".split(",");
                epRuntime.sendEvent(new SupportSimpleBeanTwo("E2", 50, 21, 22));
                EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fields, new Object[]{"E2", "E2", 20});
                epRuntime.sendEvent(new SupportSimpleBeanTwo("E1", 60, 11, 12));
                EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fields, new Object[] {"E1", "E1", 10});
            }
        };
        IndexAssertionEventSend noAssertion = new IndexAssertionEventSend() {
            public void run() {
            }
        };

        // unique-s1
        assertIndexChoice(false, new String[0], preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = ssb2.s2", null, BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", null, BACKING_SINGLE_UNIQUE, eventSendAssertion),
                        new IndexAssertion(null, "i1 between 1 and 10", null, BACKING_SORTED, noAssertion),
                        new IndexAssertion(null, "l1 = ssb2.l2", null, BACKING_SINGLE_DUPS, eventSendAssertion),
                });

        // unique-s1+i1
        assertIndexChoice(false, new String[0], preloadedEventsOne, "std:unique(s1, d1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = ssb2.s2", null, BACKING_SINGLE_DUPS, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2", null, BACKING_MULTI_DUPS, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and d1 = ssb2.d2", null, BACKING_MULTI_UNIQUE, eventSendAssertion),
                        new IndexAssertion(null, "s1 = ssb2.s2 and l1 = ssb2.l2 and d1 = ssb2.d2", null, BACKING_MULTI_UNIQUE, eventSendAssertion),
                        new IndexAssertion(null, "d1 = ssb2.d2 and s1 = ssb2.s2 and l1 = ssb2.l2", null, BACKING_MULTI_UNIQUE, eventSendAssertion),
                        new IndexAssertion(null, "l1 = ssb2.l2 and s1 = ssb2.s2 and d1 = ssb2.d2", null, BACKING_MULTI_UNIQUE, eventSendAssertion),
                });
    }

    private void assertIndexChoice(boolean indexShare, String[] indexes, Object[] preloadedEvents, String datawindow,
                                   IndexAssertion ... assertions) {
        String epl = "create window MyWindow." + datawindow + " as select * from SSB1";
        if (indexShare) {
            epl = "@Hint('enable_window_subquery_indexshare') " + epl;
        }
        epService.getEPAdministrator().createEPL(epl);
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
                    (assertion.getHint() == null ? "" : assertion.getHint()) + "select *, " +
                    "(select * from MyWindow where " + assertion.getWhereClause() + ") as ssb1 from SSB2 as ssb2";

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
            SupportQueryPlanIndexHook.assertSubqueryAndReset(0, assertion.getExpectedIndexName(), assertion.getIndexBackingClass());
            consumeStmt.addListener(listenerStmtOne);
            assertion.getEventSendAssertion().run();
            consumeStmt.destroy();
        }

        epService.getEPAdministrator().destroyAllStatements();
    }

    private void runAssertion(boolean enableIndexShareCreate, boolean disableIndexShareConsumer, boolean createExplicitIndex, boolean setNoindex) {
        String createEpl = "create window MyWindow.std:unique(theString) as select * from SupportBean";
        if (enableIndexShareCreate) {
            createEpl = "@Hint('enable_window_subquery_indexshare') " + createEpl;
        }
        epService.getEPAdministrator().createEPL(createEpl);
        epService.getEPAdministrator().createEPL("insert into MyWindow select * from SupportBean");

        EPStatement stmtIndex = null;
        if (createExplicitIndex) {
            stmtIndex = epService.getEPAdministrator().createEPL("create index MyIndex on MyWindow (theString)");
        }

        String consumeEpl = "select status.*, (select * from MyWindow where theString = ABean.p00) as details from ABean as status";
        if (disableIndexShareConsumer) {
            consumeEpl = "@Hint('disable_window_subquery_indexshare') " + consumeEpl;
        }
        if (setNoindex) {
            consumeEpl = "@Hint('set_noindex') " + consumeEpl;
        }
        EPStatement consumeStmt = epService.getEPAdministrator().createEPL(consumeEpl);
        consumeStmt.addListener(listenerStmtOne);

        String[] fields = "id,details.theString,details.intPrimitive".split(",");

        epRuntime.sendEvent(new SupportBean("E1", 10));
        epRuntime.sendEvent(new SupportBean("E2", 20));
        epRuntime.sendEvent(new SupportBean("E3", 30));

        epRuntime.sendEvent(new SupportBean_S0(1, "E1"));
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fields, new Object[]{1, "E1", 10});

        epRuntime.sendEvent(new SupportBean_S0(2, "E2"));
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fields, new Object[]{2, "E2", 20});

        // test late start
        consumeStmt.destroy();
        consumeStmt = epService.getEPAdministrator().createEPL(consumeEpl);
        consumeStmt.addListener(listenerStmtOne);

        epRuntime.sendEvent(new SupportBean_S0(1, "E1"));
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fields, new Object[]{1, "E1", 10});

        epRuntime.sendEvent(new SupportBean_S0(2, "E2"));
        EPAssertionUtil.assertProps(listenerStmtOne.assertOneGetNewAndReset(), fields, new Object[]{2, "E2", 20});

        if (stmtIndex != null) {
            stmtIndex.destroy();
        }
        consumeStmt.destroy();
    }
}
