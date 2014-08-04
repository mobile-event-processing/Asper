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

package com.espertech.esper.regression.client;

import com.espertech.esper.client.*;
import com.espertech.esper.client.hook.ExceptionHandlerContext;
import com.espertech.esper.client.hook.ExceptionHandlerFactoryContext;
import com.espertech.esper.epl.agg.service.AggregationSupport;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.client.*;
import junit.framework.TestCase;

import java.util.List;

public class TestExceptionHandler extends TestCase
{
    private EPServiceProvider epService;

    public void testHandler()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        // add same factory twice
        config.getEngineDefaults().getExceptionHandling().getHandlerFactories().clear();
        config.getEngineDefaults().getExceptionHandling().addClass(SupportExceptionHandlerFactory.class);
        config.getEngineDefaults().getExceptionHandling().addClass(SupportExceptionHandlerFactory.class);
        config.addEventType("SupportBean", SupportBean.class);
        config.addPlugInAggregationFunction("myinvalidagg", InvalidAggTest.class.getName());

        epService = EPServiceProviderManager.getDefaultProvider(config);
        SupportExceptionHandlerFactory.getFactoryContexts().clear();
        SupportExceptionHandlerFactory.getHandlers().clear();
        epService.initialize();

        String epl = "@Name('ABCName') select myinvalidagg() from SupportBean";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);

        List<ExceptionHandlerFactoryContext> contexts = SupportExceptionHandlerFactory.getFactoryContexts();
        assertEquals(2, contexts.size());
        assertEquals(epService.getURI(), contexts.get(0).getEngineURI());
        assertEquals(epService.getURI(), contexts.get(1).getEngineURI());

        SupportExceptionHandlerFactory.SupportExceptionHandler handlerOne = SupportExceptionHandlerFactory.getHandlers().get(0);
        SupportExceptionHandlerFactory.SupportExceptionHandler handlerTwo = SupportExceptionHandlerFactory.getHandlers().get(1);
        epService.getEPRuntime().sendEvent(new SupportBean());

        assertEquals(1, handlerOne.getContexts().size());
        assertEquals(1, handlerTwo.getContexts().size());
        ExceptionHandlerContext ehc = handlerOne.getContexts().get(0);
        assertEquals(epService.getURI(), ehc.getEngineURI());
        assertEquals(epl, ehc.getEpl());
        assertEquals("ABCName", ehc.getStatementName());
        assertEquals("Sample exception", ehc.getThrowable().getMessage());
    }

    /**
     * Ensure the support configuration has an exception handler that rethrows exceptions.
     */
    public void testSupportConfigHandlerRethrow()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        config.addEventType("SupportBean", SupportBean.class);
        config.addPlugInAggregationFunction("myinvalidagg", InvalidAggTest.class.getName());

        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();

        String epl = "@Name('ABCName') select myinvalidagg() from SupportBean";
        epService.getEPAdministrator().createEPL(epl);

        try {
            epService.getEPRuntime().sendEvent(new SupportBean());
            fail();
        }
        catch (EPException ex) {
            // expected
        }
    }

    public static class InvalidAggTest extends AggregationSupport {

        @Override
        public void validate(AggregationValidationContext validationContext)
        {
        }

        @Override
        public void enter(Object value) {
            throw new RuntimeException("Sample exception");
        }

        @Override
        public void leave(Object value) {
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public Class getValueType() {
            return null;
        }

        @Override
        public void clear() {
        }
    }

}
