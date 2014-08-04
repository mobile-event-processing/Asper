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
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.bean.SupportBean_A;
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

public class TestNamedWindowIndexFAFPerf extends TestCase implements IndexBackingTableInfo
{
    private static Log log = LogFactory.getLog(TestNamedWindowIndexFAFPerf.class);

    private EPServiceProvider epService;

    public void setUp()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
    }

    public void testFAFKeyBTreePerformance()
    {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);

        // create window one
        epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as SupportBean");
        epService.getEPAdministrator().createEPL("insert into MyWindow select * from SupportBean");
        EPStatement idx = epService.getEPAdministrator().createEPL("create index idx1 on MyWindow(intPrimitive btree)");

        // insert X rows
        int maxRows = 10000;   //for performance testing change to int maxRows = 100000;
        for (int i = 0; i < maxRows; i++) {
            epService.getEPRuntime().sendEvent(new SupportBean("A", i));
        }
        epService.getEPRuntime().sendEvent(new SupportBean("B", 100));

        // fire single-key queries
        String eplIdx1One = "select intPrimitive as sumi from MyWindow where intPrimitive = 5501";
        runFAFAssertion(eplIdx1One, 5501);

        String eplIdx1Two = "select sum(intPrimitive) as sumi from MyWindow where intPrimitive > 9997";
        runFAFAssertion(eplIdx1Two, 9998 + 9999);

        // drop index, create multikey btree
        idx.destroy();
        idx = epService.getEPAdministrator().createEPL("create index idx2 on MyWindow(intPrimitive btree, theString btree)");

        String eplIdx2One = "select intPrimitive as sumi from MyWindow where intPrimitive = 5501 and theString = 'A'";
        runFAFAssertion(eplIdx2One, 5501);

        String eplIdx2Two = "select sum(intPrimitive) as sumi from MyWindow where intPrimitive in [5000:5004) and theString = 'A'";
        runFAFAssertion(eplIdx2Two, 5000+5001+5003+5002);

        String eplIdx2Three = "select sum(intPrimitive) as sumi from MyWindow where intPrimitive=5001 and theString between 'A' and 'B'";
        runFAFAssertion(eplIdx2Three, 5001);
    }

    private void runFAFAssertion(String epl, Integer expected) {
        long start = System.currentTimeMillis();
        int loops = 1000;

        EPOnDemandPreparedQuery query = epService.getEPRuntime().prepareQuery(epl);
        for (int i = 0; i < loops; i++) {
            runFAFQuery(query, expected);
        }
        long end = System.currentTimeMillis();
        long delta = end - start;
        assertTrue("delta=" + delta, delta < 1000);
    }

    public void testFAFKeyAndRangePerformance()
    {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);

        // create window one
        epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as SupportBean");
        epService.getEPAdministrator().createEPL("insert into MyWindow select * from SupportBean");
        epService.getEPAdministrator().createEPL("create index idx1 on MyWindow(theString hash, intPrimitive btree)");

        // insert X rows
        int maxRows = 10000;   //for performance testing change to int maxRows = 100000;
        for (int i=0; i < maxRows; i++) {
            epService.getEPRuntime().sendEvent(new SupportBean("A", i));
        }

        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive not in [3:9997]", 1+2+9998+9999);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive not in [3:9997)", 1+2+9997+9998+9999);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive not in (3:9997]", 1+2+3+9998+9999);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive not in (3:9997)", 1+2+3+9997+9998+9999);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'B' and intPrimitive not in (3:9997)", null);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive between 200 and 202", 603);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive between 202 and 199", 199+200+201+202);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive >= 200 and intPrimitive <= 202", 603);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive >= 202 and intPrimitive <= 200", null);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive > 9997", 9998 + 9999);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive >= 9997", 9997 + 9998 + 9999);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive < 5", 4+3+2+1);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive <= 5", 5+4+3+2+1);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive in [200:202]", 603);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive in [200:202)", 401);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive in (200:202]", 403);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where theString = 'A' and intPrimitive in (200:202)", 201);

        // test no value returned
        EPOnDemandPreparedQuery query = epService.getEPRuntime().prepareQuery("select * from MyWindow where theString = 'A' and intPrimitive < 0");
        EPOnDemandQueryResult result = query.execute();
        assertEquals(0, result.getArray().length);
    }

    public void testFAFRangePerformance()
    {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);

        // create window one
        epService.getEPAdministrator().createEPL("create window MyWindow.win:keepall() as SupportBean");
        epService.getEPAdministrator().createEPL("insert into MyWindow select * from SupportBean");
        epService.getEPAdministrator().createEPL("create index idx1 on MyWindow(intPrimitive btree)");

        // insert X rows
        int maxRows = 10000;   //for performance testing change to int maxRows = 100000;
        for (int i=0; i < maxRows; i++) {
            epService.getEPRuntime().sendEvent(new SupportBean("K", i));
        }

        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive between 200 and 202", 603);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive between 202 and 199", 199+200+201+202);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive >= 200 and intPrimitive <= 202", 603);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive >= 202 and intPrimitive <= 200", null);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive > 9997", 9998 + 9999);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive >= 9997", 9997 + 9998 + 9999);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive < 5", 4+3+2+1);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive <= 5", 5+4+3+2+1);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive in [200:202]", 603);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive in [200:202)", 401);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive in (200:202]", 403);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive in (200:202)", 201);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive not in [3:9997]", 1+2+9998+9999);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive not in [3:9997)", 1+2+9997+9998+9999);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive not in (3:9997]", 1+2+3+9998+9999);
        runFAFAssertion("select sum(intPrimitive) as sumi from MyWindow where intPrimitive not in (3:9997)", 1+2+3+9997+9998+9999);

        // test no value returned
        EPOnDemandPreparedQuery query = epService.getEPRuntime().prepareQuery("select * from MyWindow where intPrimitive < 0");
        EPOnDemandQueryResult result = query.execute();
        assertEquals(0, result.getArray().length);
    }

    public void testFAFKeyPerformance()
    {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean_A", SupportBean_A.class);

        // create window one
        String stmtTextCreateOne = "create window MyWindowOne.win:keepall() as (f1 string, f2 int)";
        epService.getEPAdministrator().createEPL(stmtTextCreateOne);
        epService.getEPAdministrator().createEPL("insert into MyWindowOne(f1, f2) select theString, intPrimitive from SupportBean");
        epService.getEPAdministrator().createEPL("create index MyWindowOneIndex on MyWindowOne(f1)");

        // insert X rows
        int maxRows = 100;   //for performance testing change to int maxRows = 100000;
        for (int i=0; i < maxRows; i++) {
            epService.getEPRuntime().sendEvent(new SupportBean("K" + i, i));
        }

        // fire N queries each returning 1 row
        long start = System.currentTimeMillis();
        String queryText = "select * from MyWindowOne where f1='K10'";
        EPOnDemandPreparedQuery query = epService.getEPRuntime().prepareQuery(queryText);
        int loops = 10000;  

        for (int i = 0; i < loops; i++) {
            EPOnDemandQueryResult result = query.execute();
            assertEquals(1, result.getArray().length);
            assertEquals("K10", result.getArray()[0].get("f1"));
        }
        long end = System.currentTimeMillis();
        long delta = end - start;
        assertTrue("delta=" + delta, delta < 500);
        
        // test no value returned
        queryText = "select * from MyWindowOne where f1='KX'";
        query = epService.getEPRuntime().prepareQuery(queryText);
        EPOnDemandQueryResult result = query.execute();
        assertEquals(0, result.getArray().length);

        // test query null
        queryText = "select * from MyWindowOne where f1=null";
        query = epService.getEPRuntime().prepareQuery(queryText);
        result = query.execute();
        assertEquals(0, result.getArray().length);
        
        // insert null and test null
        epService.getEPRuntime().sendEvent(new SupportBean(null, -2));
        result = query.execute();
        assertEquals(0, result.getArray().length);

        // test two values
        epService.getEPRuntime().sendEvent(new SupportBean(null, -1));
        query = epService.getEPRuntime().prepareQuery("select * from MyWindowOne where f1 is null order by f2 asc");
        result = query.execute();
        assertEquals(2, result.getArray().length);
        assertEquals(-2, result.getArray()[0].get("f2"));
        assertEquals(-1, result.getArray()[1].get("f2"));
    }

    private void runFAFQuery(EPOnDemandPreparedQuery query, Integer expectedValue) {
        EPOnDemandQueryResult result = query.execute();
        assertEquals(1, result.getArray().length);
        assertEquals(expectedValue, result.getArray()[0].get("sumi"));
    }
}
