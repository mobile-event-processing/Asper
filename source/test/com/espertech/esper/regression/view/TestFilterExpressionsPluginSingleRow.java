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

package com.espertech.esper.regression.view;

import com.espertech.esper.client.*;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.core.service.EPStatementSPI;
import com.espertech.esper.filter.*;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.bean.SupportTradeEvent;
import com.espertech.esper.support.client.SupportConfigFactory;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;

public class TestFilterExpressionsPluginSingleRow extends TestCase
{
    private static final Log log = LogFactory.getLog(TestFilterExpressionsPluginSingleRow.class);

    private EPServiceProvider epService;
    private SupportUpdateListener listener;

    public void setUp()
    {
        listener = new SupportUpdateListener();

        Configuration config = SupportConfigFactory.getConfiguration();
        config.addEventType("SupportEvent", SupportTradeEvent.class);
        config.addEventType("SupportBean", SupportBean.class);

        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
    }

    protected void tearDown() throws Exception {
        listener = null;
    }

    public void testPluginSingleRowFunctionFilterPerf() {

        // create listeners
        int count = 100;
        SupportUpdateListener[] listeners = new SupportUpdateListener[count];
        for (int i = 0; i < count; i++) {
            listeners[i] = new SupportUpdateListener();
        }

        // func(...) = value
        runAssertionEquals(listeners);
        epService.getEPAdministrator().destroyAllStatements();

        // func(...) implied true
        runAssertionBoolean();
        epService.getEPAdministrator().destroyAllStatements();
    }

    private void runAssertionEquals(SupportUpdateListener[] listeners) {

        // test function returns lookup value and "equals"
        epService.getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("libSplit", MyLib.class.getName(), "libSplit", ConfigurationPlugInSingleRowFunction.FilterOptimizable.ENABLED);
        for (int i = 0; i < listeners.length; i++) {
            EPStatement stmt = epService.getEPAdministrator().createEPL("select * from SupportBean(libSplit(theString) = " + i + ")");
            stmt.addListener(listeners[i]);
        }

        long startTime = System.currentTimeMillis();
        MyLib.resetCountInvoked();
        int loops = 1000;
        for (int i = 0; i < loops; i++) {
            epService.getEPRuntime().sendEvent(new SupportBean("E_" + i % 100, 0));
            SupportUpdateListener listener = listeners[i % 100];
            assertTrue(listener.getAndClearIsInvoked());
        }
        long delta = System.currentTimeMillis() - startTime;
        assertEquals(loops, MyLib.getCountInvoked());

        log.info("Equals delta=" + delta);
        assertTrue("Delta is " + delta, delta < 1000);
    }

    private void runAssertionBoolean() {

        // test function returns lookup value and "equals"
        epService.getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("libE1True", MyLib.class.getName(), "libE1True", ConfigurationPlugInSingleRowFunction.FilterOptimizable.ENABLED);
        int count = 100;
        for (int i = 0; i < count; i++) {
            EPStatement stmt = epService.getEPAdministrator().createEPL("select * from SupportBean(libE1True(theString))");
            stmt.addListener(listener);
        }

        long startTime = System.currentTimeMillis();
        MyLib.resetCountInvoked();
        int loops = 10000;
        for (int i = 0; i < loops; i++) {
            String key = "E_" + i % 100;
            epService.getEPRuntime().sendEvent(new SupportBean(key, 0));
            if (key.equals("E_1")) {
                assertEquals(count, listener.getNewDataList().size());
                listener.reset();
            }
            else {
                assertFalse(listener.isInvoked());
            }
        }
        long delta = System.currentTimeMillis() - startTime;
        assertEquals(loops, MyLib.getCountInvoked());

        log.info("Boolea delta=" + delta);
        assertTrue("Delta is " + delta, delta < 1000);
    }

    public void testPluginSingleRowFunctionFilter() {

        String epl;

        epService.getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("funcOne", MyLib.class.getName(), "libSplit", ConfigurationPlugInSingleRowFunction.FilterOptimizable.DISABLED);
        epl = "select * from SupportBean(funcOne(theString) = 0)";
        assertFilterSingle(epl, FilterSpecCompiler.PROPERTY_NAME_BOOLEAN_EXPRESSION, FilterOperator.BOOLEAN_EXPRESSION);
        
        epService.getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("funcOneWDefault", MyLib.class.getName(), "libSplit");
        epl = "select * from SupportBean(funcOneWDefault(theString) = 0)";
        assertFilterSingle(epl, "funcOneWDefault(theString)", FilterOperator.EQUAL);

        epService.getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("funcTwo", MyLib.class.getName(), "libSplit", ConfigurationPlugInSingleRowFunction.FilterOptimizable.ENABLED);
        epl = "select * from SupportBean(funcTwo(theString) = 0)";
        assertFilterSingle(epl, "funcTwo(theString)", FilterOperator.EQUAL);
        
        epService.getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("libE1True", MyLib.class.getName(), "libE1True", ConfigurationPlugInSingleRowFunction.FilterOptimizable.ENABLED);
        epl = "select * from SupportBean(libE1True(theString))";
        assertFilterSingle(epl, "libE1True(theString)", FilterOperator.EQUAL);

        epl = "select * from SupportBean(funcTwo( theString ) > 10)";
        assertFilterSingle(epl, "funcTwo(theString)", FilterOperator.GREATER);
    }

    private void assertFilterSingle(String epl, String expression, FilterOperator op) {
        EPStatementSPI statementSPI = (EPStatementSPI) epService.getEPAdministrator().createEPL(epl);
        FilterValueSetParam param = getFilterSingle(statementSPI);
        assertEquals(op, param.getFilterOperator());
        assertEquals(expression, param.getLookupable().getExpression());
    }

    private FilterValueSetParam getFilterSingle(EPStatementSPI statementSPI) {
        FilterServiceSPI filterServiceSPI = (FilterServiceSPI) statementSPI.getStatementContext().getFilterService();
        FilterSet set = filterServiceSPI.take(Collections.singleton(statementSPI.getStatementId()));
        assertEquals(1, set.getFilters().size());
        FilterValueSet valueSet = set.getFilters().get(0).getFilterValueSet();
        assertEquals(1, valueSet.getParameters().size());
        return valueSet.getParameters().getFirst();
    }

    public static class MyLib {

        private static int countInvoked;

        public static int libSplit(String theString) {
            String[] key = theString.split("_");
            countInvoked++;
            return Integer.parseInt(key[1]);
        }

        public static boolean libE1True(String theString) {
            countInvoked++;
            return theString.equals("E_1");
        }

        public static int getCountInvoked() {
            return countInvoked;
        }

        public static void resetCountInvoked() {
            countInvoked = 0;
        }
    }
}
