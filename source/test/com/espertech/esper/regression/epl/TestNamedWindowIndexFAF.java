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
import com.espertech.esper.support.bean.SupportSimpleBeanOne;
import com.espertech.esper.support.bean.SupportSimpleBeanTwo;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.support.epl.SupportQueryPlanIndexHook;
import com.espertech.esper.support.util.IndexAssertion;
import com.espertech.esper.support.util.IndexAssertionFAF;
import com.espertech.esper.support.util.IndexBackingTableInfo;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestNamedWindowIndexFAF extends TestCase implements IndexBackingTableInfo
{
    private static Log log = LogFactory.getLog(TestNamedWindowIndexFAF.class);

    private EPServiceProvider epService;

    public void setUp()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        config.getEngineDefaults().getLogging().setEnableQueryPlan(true);
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
    }

    public void testOnSelectIndexChoiceJoin() {
        epService.getEPAdministrator().getConfiguration().addEventType("SSB1", SupportSimpleBeanOne.class);
        epService.getEPAdministrator().getConfiguration().addEventType("SSB2", SupportSimpleBeanTwo.class);

        Object[] preloadedEventsOne = new Object[] {
                new SupportSimpleBeanOne("E1", 10, 1, 2),
                new SupportSimpleBeanOne("E2", 11, 3, 4),
                new SupportSimpleBeanTwo("E1", 20, 1, 2),
                new SupportSimpleBeanTwo("E2", 21, 3, 4),
        };
        IndexAssertionFAF fafAssertion = new IndexAssertionFAF() {
            public void run(EPOnDemandQueryResult result) {
                String[] fields = "w1.s1,w2.s2,w1.i1,w2.i2".split(",");
                EPAssertionUtil.assertPropsPerRowAnyOrder(result.getArray(), fields,
                        new Object[][]{{"E1", "E1", 10, 20}, {"E2", "E2", 11, 21}});
            }
        };

        // single prop, no index, both declared unique
        IndexAssertion[] assertionsSingleProp = new IndexAssertion[] {
                new IndexAssertion(null, "s1 = s2", true, fafAssertion),
                new IndexAssertion(null, "s1 = s2 and l1 = l2", true, fafAssertion),
                new IndexAssertion(null, "l1 = l2 and s1 = s2", true, fafAssertion),
                new IndexAssertion(null, "d1 = d2 and l1 = l2 and s1 = s2", true, fafAssertion),
                new IndexAssertion(null, "d1 = d2 and l1 = l2", false, fafAssertion),
        };
        assertIndexChoiceJoin(new String[0], preloadedEventsOne, "std:unique(s1)", "std:unique(s2)", assertionsSingleProp);

        // single prop, unique indexes, both declared keepall
        String[] uniqueIndex = new String[] {"create unique index W1I1 on W1(s1)", "create unique index W1I2 on W2(s2)"};
        assertIndexChoiceJoin(uniqueIndex, preloadedEventsOne, "win:keepall()", "win:keepall()", assertionsSingleProp);

        // single prop, mixed indexes, both declared keepall
        String[] mixedIndex = new String[] {"create index W1I1 on W1(s1, l1)", "create unique index W1I2 on W2(s2)"};
        assertIndexChoiceJoin(mixedIndex, preloadedEventsOne, "std:unique(s1)", "win:keepall()", assertionsSingleProp);

        // multi prop, no index, both declared unique
        IndexAssertion[] assertionsMultiProp = new IndexAssertion[] {
                new IndexAssertion(null, "s1 = s2", false, fafAssertion),
                new IndexAssertion(null, "s1 = s2 and l1 = l2", true, fafAssertion),
                new IndexAssertion(null, "l1 = l2 and s1 = s2", true, fafAssertion),
                new IndexAssertion(null, "d1 = d2 and l1 = l2 and s1 = s2", true, fafAssertion),
                new IndexAssertion(null, "d1 = d2 and l1 = l2", false, fafAssertion),
        };
        assertIndexChoiceJoin(new String[0], preloadedEventsOne, "std:unique(s1, l1)", "std:unique(s2, l2)", assertionsMultiProp);

        // multi prop, unique indexes, both declared keepall
        String[] uniqueIndexMulti = new String[] {"create unique index W1I1 on W1(s1, l1)", "create unique index W1I2 on W2(s2, l2)"};
        assertIndexChoiceJoin(uniqueIndexMulti, preloadedEventsOne, "win:keepall()", "win:keepall()", assertionsMultiProp);

        // multi prop, mixed indexes, both declared keepall
        String[] mixedIndexMulti = new String[] {"create index W1I1 on W1(s1)", "create unique index W1I2 on W2(s2, l2)"};
        assertIndexChoiceJoin(mixedIndexMulti, preloadedEventsOne, "std:unique(s1, l1)", "win:keepall()", assertionsMultiProp);
    }

    private void assertIndexChoiceJoin(String[] indexes, Object[] preloadedEvents, String datawindowOne, String datawindowTwo,
                                   IndexAssertion ... assertions) {
        epService.getEPAdministrator().createEPL("create window W1." + datawindowOne + " as SSB1");
        epService.getEPAdministrator().createEPL("create window W2." + datawindowTwo + " as SSB2");
        epService.getEPAdministrator().createEPL("insert into W1 select * from SSB1");
        epService.getEPAdministrator().createEPL("insert into W2 select * from SSB2");
        for (String index : indexes) {
            epService.getEPAdministrator().createEPL(index);
        }
        for (Object event : preloadedEvents) {
            epService.getEPRuntime().sendEvent(event);
        }

        int count = 0;
        for (IndexAssertion assertion : assertions) {
            log.info("======= Testing #" + count++);
            String epl = INDEX_CALLBACK_HOOK +
                    (assertion.getHint() == null ? "" : assertion.getHint()) +
                    "select * from W1 as w1, W2 as w2 " +
                    "where " + assertion.getWhereClause();
            EPOnDemandQueryResult result;
            try {
                result = epService.getEPRuntime().executeQuery(epl);
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
            SupportQueryPlanIndexHook.assertJoinAllStreamsAndReset(assertion.getUnique());
            assertion.getFafAssertion().run(result);
        }

        epService.getEPAdministrator().destroyAllStatements();
    }

    public void testOnSelectIndexChoice() {
        epService.getEPAdministrator().getConfiguration().addEventType("SSB1", SupportSimpleBeanOne.class);
        epService.getEPAdministrator().getConfiguration().addEventType("SSB2", SupportSimpleBeanTwo.class);

        Object[] preloadedEventsOne = new Object[] {new SupportSimpleBeanOne("E1", 10, 11, 12), new SupportSimpleBeanOne("E2", 20, 21, 22)};
        IndexAssertionFAF fafAssertion = new IndexAssertionFAF() {
            public void run(EPOnDemandQueryResult result) {
                String[] fields = "s1,i1".split(",");
                EPAssertionUtil.assertPropsPerRow(result.getArray(), fields, new Object[][]{{"E2", 20}});
            }
        };

        // single index one field (plus declared unique)
        String[] noindexes = new String[0];
        assertIndexChoice(noindexes, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = 'E2'", null, null, fafAssertion),
                        new IndexAssertion(null, "s1 = 'E2' and l1 = 22", null, null, fafAssertion),
                        new IndexAssertion("@Hint('index(One)')", "s1 = 'E2' and l1 = 22", null, null, fafAssertion),
                        new IndexAssertion("@Hint('index(Two,bust)')", "s1 = 'E2' and l1 = 22"), // should bust
                });

        // single index one field (plus declared unique)
        String[] indexOneField = new String[] {"create unique index One on MyWindow (s1)"};
        assertIndexChoice(indexOneField, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = 'E2'", "One", BACKING_SINGLE_UNIQUE, fafAssertion),
                        new IndexAssertion(null, "s1 = 'E2' and l1 = 22", "One", BACKING_SINGLE_UNIQUE, fafAssertion),
                        new IndexAssertion("@Hint('index(One)')", "s1 = 'E2' and l1 = 22", "One", BACKING_SINGLE_UNIQUE, fafAssertion),
                        new IndexAssertion("@Hint('index(Two,bust)')", "s1 = 'E2' and l1 = 22"), // should bust
                });

        // single index two field (plus declared unique)
        String[] indexTwoField = new String[] {"create unique index One on MyWindow (s1, l1)"};
        assertIndexChoice(indexTwoField, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = 'E2'", null, null, fafAssertion),
                        new IndexAssertion(null, "s1 = 'E2' and l1 = 22", "One", BACKING_MULTI_UNIQUE, fafAssertion),
                });

        // two index one unique (plus declared unique)
        String[] indexSetTwo = new String[] {
                "create index One on MyWindow (s1)",
                "create unique index Two on MyWindow (s1, d1)"};
        assertIndexChoice(indexSetTwo, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "s1 = 'E2'", "One", BACKING_SINGLE_DUPS, fafAssertion),
                        new IndexAssertion(null, "s1 = 'E2' and l1 = 22", "One", BACKING_SINGLE_DUPS, fafAssertion),
                        new IndexAssertion("@Hint('index(One)')", "s1 = 'E2' and l1 = 22", "One", BACKING_SINGLE_DUPS, fafAssertion),
                        new IndexAssertion("@Hint('index(Two,One)')", "s1 = 'E2' and l1 = 22", "One", BACKING_SINGLE_DUPS, fafAssertion),
                        new IndexAssertion("@Hint('index(Two,bust)')", "s1 = 'E2' and l1 = 22"),  // busted
                        new IndexAssertion("@Hint('index(explicit,bust)')", "s1 = 'E2' and l1 = 22", "One", BACKING_SINGLE_DUPS, fafAssertion),
                        new IndexAssertion(null, "s1 = 'E2' and d1 = 21 and l1 = 22", "Two", BACKING_MULTI_UNIQUE, fafAssertion),
                        new IndexAssertion("@Hint('index(explicit,bust)')", "d1 = 22 and l1 = 22"),   // busted
                });

        // range (unique)
        String[] indexSetThree = new String[] {
                "create index One on MyWindow (l1 btree)",
                "create index Two on MyWindow (d1 btree)"};
        assertIndexChoice(indexSetThree, preloadedEventsOne, "std:unique(s1)",
                new IndexAssertion[] {
                        new IndexAssertion(null, "l1 between 22 and 23", "One", BACKING_SORTED_COERCED, fafAssertion),
                        new IndexAssertion(null, "d1 between 21 and 22", "Two", BACKING_SORTED_COERCED, fafAssertion),
                        new IndexAssertion("@Hint('index(One, bust)')", "d1 between 21 and 22"), // busted
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
            String epl = INDEX_CALLBACK_HOOK +
                    (assertion.getHint() == null ? "" : assertion.getHint()) +
                    "select * from MyWindow where " + assertion.getWhereClause();
            EPOnDemandQueryResult result;
            try {
                result = epService.getEPRuntime().executeQuery(epl);
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
            SupportQueryPlanIndexHook.assertFAFAndReset(assertion.getExpectedIndexName(), assertion.getIndexBackingClass());
            assertion.getFafAssertion().run(result);
        }

        epService.getEPAdministrator().destroyAllStatements();
    }
}
