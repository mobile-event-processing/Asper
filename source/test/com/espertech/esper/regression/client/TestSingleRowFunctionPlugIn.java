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
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.client.SupportConfigFactory;
import junit.framework.TestCase;

public class TestSingleRowFunctionPlugIn extends TestCase
{
    private EPServiceProvider epService;
    private SupportUpdateListener listener;

    public void setUp()
    {
        listener = new SupportUpdateListener();

        Configuration configuration = SupportConfigFactory.getConfiguration();
        configuration.addPlugInSingleRowFunction("power3", MySingleRowFunction.class.getName(), "computePower3");
        configuration.addPlugInSingleRowFunction("chainTop", MySingleRowFunction.class.getName(), "getChainTop");
        configuration.addPlugInSingleRowFunction("surroundx", MySingleRowFunction.class.getName(), "surroundx");
        configuration.addPlugInSingleRowFunction("throwExceptionLogMe", MySingleRowFunction.class.getName(), "throwexception", ConfigurationPlugInSingleRowFunction.ValueCache.DISABLED, ConfigurationPlugInSingleRowFunction.FilterOptimizable.ENABLED, false);
        configuration.addPlugInSingleRowFunction("throwExceptionRethrow", MySingleRowFunction.class.getName(), "throwexception", ConfigurationPlugInSingleRowFunction.ValueCache.DISABLED, ConfigurationPlugInSingleRowFunction.FilterOptimizable.ENABLED, true);
        configuration.addPlugInSingleRowFunction("power3Rethrow", MySingleRowFunction.class.getName(), "computePower3", ConfigurationPlugInSingleRowFunction.ValueCache.DISABLED, ConfigurationPlugInSingleRowFunction.FilterOptimizable.ENABLED, true);
        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        epService.initialize();
    }

    public void tearDown() {
        listener = null;
    }

    public void testPropertyOrSingleRowMethod() throws Exception
    {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);
        String text = "select surroundx('test') as val from SupportBean";
        EPStatement stmt = epService.getEPAdministrator().createEPL(text);
        stmt.addListener(listener);

        String fields[] = new String[] {"val"};
        epService.getEPRuntime().sendEvent(new SupportBean("a", 3));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{"XtestX"});
    }

    public void testChainMethod() throws Exception
    {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);
        String text = "select chainTop().chainValue(12, intPrimitive) as val from SupportBean";
        EPStatement stmt = epService.getEPAdministrator().createEPL(text);
        stmt.addListener(listener);

        runAssertionChainMethod();

        stmt.destroy();
        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(text);
        assertEquals(text, model.toEPL());
        stmt = epService.getEPAdministrator().create(model);
        assertEquals(text, stmt.getText());
        stmt.addListener(listener);

        runAssertionChainMethod();
    }

    public void testSingleMethod() throws Exception
    {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);

        String text = "select power3(intPrimitive) as val from SupportBean";
        EPStatement stmt = epService.getEPAdministrator().createEPL(text);
        stmt.addListener(listener);

        runAssertionSingleMethod();

        stmt.destroy();
        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(text);
        assertEquals(text, model.toEPL());
        stmt = epService.getEPAdministrator().create(model);
        assertEquals(text, stmt.getText());
        stmt.addListener(listener);

        runAssertionSingleMethod();

        stmt.destroy();
        text = "select power3(2) as val from SupportBean";
        stmt = epService.getEPAdministrator().createEPL(text);
        stmt.addListener(listener);

        runAssertionSingleMethod();

        // test exception behavior
        // logged-only
        epService.getEPAdministrator().createEPL("select throwExceptionLogMe() from SupportBean").addListener(listener);
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        epService.getEPAdministrator().destroyAllStatements();

        // rethrow
        epService.getEPAdministrator().createEPL("@Name('S0') select throwExceptionRethrow() from SupportBean").addListener(listener);
        try {
            epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
            fail();
        }
        catch (EPException ex) {
            assertEquals("java.lang.RuntimeException: Unexpected exception in statement 'S0': Invocation exception when invoking method 'throwexception' of class 'com.espertech.esper.regression.client.MySingleRowFunction' passing parameters [] for statement 'S0': RuntimeException : This is a 'throwexception' generated exception", ex.getMessage());
            epService.getEPAdministrator().destroyAllStatements();
        }

        // NPE when boxed is null
        epService.getEPAdministrator().createEPL("@Name('S1') select power3Rethrow(intBoxed) from SupportBean").addListener(listener);
        try {
            epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
            fail();
        }
        catch (EPException ex) {
            assertEquals("java.lang.RuntimeException: Unexpected exception in statement 'S1': NullPointerException invoking method 'computePower3' of class 'com.espertech.esper.regression.client.MySingleRowFunction' in parameter 0 passing parameters [null] for statement 'S1': The method expects a primitive int value but received a null value", ex.getMessage());
        }
    }

    private void runAssertionChainMethod()
    {
        String fields[] = new String[] {"val"};
        epService.getEPRuntime().sendEvent(new SupportBean("a", 3));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{36});

        listener.reset();
    }

    private void runAssertionSingleMethod()
    {
        String fields[] = new String[] {"val"};
        epService.getEPRuntime().sendEvent(new SupportBean("a", 2));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[]{8});

        listener.reset();
    }

    public void testFailedValidation()
    {
        Configuration configuration = SupportConfigFactory.getConfiguration();
        configuration.addPlugInSingleRowFunction("singlerow", MySingleRowFunctionTwo.class.getName(), "testSingleRow");
        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        epService.initialize();

        try
        {
            String text = "select singlerow('a', 'b') from " + SupportBean.class.getName();
            epService.getEPAdministrator().createEPL(text);
        }
        catch (EPStatementException ex)
        {
            assertEquals("Error starting statement: Could not find static method named 'testSingleRow' in class 'com.espertech.esper.regression.client.MySingleRowFunctionTwo' with matching parameter number and expected parameter type(s) 'String, String' (nearest match found was 'testSingleRow' taking type(s) 'String, int') [select singlerow('a', 'b') from com.espertech.esper.support.bean.SupportBean]", ex.getMessage());
        }
    }

    public void testInvalidConfigure()
    {
        tryInvalidConfigure("a b", "MyClass", "some");
        tryInvalidConfigure("abc", "My Class", "other s");

        // configured twice
        try
        {
            epService.getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("concatstring", MySingleRowFunction.class.getName(), "xyz");
            epService.getEPAdministrator().getConfiguration().addPlugInAggregationFunction("concatstring", MyConcatAggregationFunction.class.getName());
            fail();
        }
        catch (ConfigurationException ex)
        {
            // expected
        }

        // configured twice
        try
        {
            epService.getEPAdministrator().getConfiguration().addPlugInAggregationFunction("teststring", MyConcatAggregationFunction.class.getName());
            epService.getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("teststring", MySingleRowFunction.class.getName(), "xyz");
            fail();
        }
        catch (ConfigurationException ex)
        {
            // expected
        }
    }

    private void tryInvalidConfigure(String funcName, String className, String methodName)
    {
        try
        {
            epService.getEPAdministrator().getConfiguration().addPlugInSingleRowFunction(funcName, className, methodName);
            fail();
        }
        catch (ConfigurationException ex)
        {
            // expected
        }
    }
}
