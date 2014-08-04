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

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.support.bean.SupportBean_ST0;
import com.espertech.esper.support.bean.SupportBean_ST0_Container;
import com.espertech.esper.support.bean.lrreport.LocationReportFactory;
import com.espertech.esper.support.bean.lrreport.LocationReport;
import com.espertech.esper.support.bean.sales.PersonSales;
import com.espertech.esper.support.bean.sales.Sale;
import com.espertech.esper.support.client.SupportConfigFactory;
import junit.framework.TestCase;

import java.util.Collection;

public class TestEnumNested extends TestCase {

    private EPServiceProvider epService;
    private SupportUpdateListener listener;

    public void setUp() {

        Configuration config = SupportConfigFactory.getConfiguration();
        config.addImport(LocationReportFactory.class);
        config.addEventType("Bean", SupportBean_ST0_Container.class);
        config.addEventType("PersonSales", PersonSales.class);
        config.addEventType("LocationReport", LocationReport.class);
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
        listener = new SupportUpdateListener();
    }

    protected void tearDown() throws Exception {
        listener = null;
    }

    public void testEquivalentToMinByUncorrelated() {

        String eplFragment = "select contained.where(x => (x.p00 = contained.min(y => y.p00))) as val from Bean";
        EPStatement stmtFragment = epService.getEPAdministrator().createEPL(eplFragment);
        stmtFragment.addListener(listener);

        SupportBean_ST0_Container bean = SupportBean_ST0_Container.make2Value("E1,2", "E2,1", "E3,2");
        epService.getEPRuntime().sendEvent(bean);
        Collection<SupportBean_ST0> result = (Collection<SupportBean_ST0>) listener.assertOneGetNewAndReset().get("val");
        EPAssertionUtil.assertEqualsExactOrder(new Object[]{bean.getContained().get(1)}, result.toArray());
    }

    public void testMinByWhere() {

        String eplFragment = "select sales.where(x => x.buyer = persons.minBy(y => age)) as val from PersonSales";
        EPStatement stmtFragment = epService.getEPAdministrator().createEPL(eplFragment);
        stmtFragment.addListener(listener);

        PersonSales bean = PersonSales.make();
        epService.getEPRuntime().sendEvent(bean);

        Collection<Sale> sales = (Collection<Sale>) listener.assertOneGetNewAndReset().get("val");
        EPAssertionUtil.assertEqualsExactOrder(new Object[]{bean.getSales().get(0)}, sales.toArray());
    }

    public void testCorrelated() {

        String eplFragment = "select contained.where(x => x = (contained.firstOf(y => y.p00 = x.p00 ))) as val from Bean";
        EPStatement stmtFragment = epService.getEPAdministrator().createEPL(eplFragment);
        stmtFragment.addListener(listener);

        SupportBean_ST0_Container bean = SupportBean_ST0_Container.make2Value("E1,2", "E2,1", "E3,3");
        epService.getEPRuntime().sendEvent(bean);
        Collection<SupportBean_ST0> result = (Collection<SupportBean_ST0>) listener.assertOneGetNewAndReset().get("val");
        assertEquals(3, result.size());  // this would be 1 if the cache is invalid

    }
}
