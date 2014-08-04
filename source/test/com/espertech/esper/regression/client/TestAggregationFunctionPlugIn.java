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
import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.client.soda.*;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.bean.SupportBean_A;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.support.epl.SupportPluginAggregationMethodOne;
import com.espertech.esper.support.epl.SupportPluginAggregationMethodThree;
import com.espertech.esper.support.epl.SupportPluginAggregationMethodTwo;
import com.espertech.esper.util.SerializableObjectCopier;
import junit.framework.TestCase;

public class TestAggregationFunctionPlugIn extends TestCase
{
    private EPServiceProvider epService;

    public void setUp()
    {
        Configuration configuration = SupportConfigFactory.getConfiguration();
        configuration.addPlugInAggregationFunction("concatstring", MyConcatAggregationFunction.class.getName());
        configuration.addPlugInAggregationFunctionFactory("concatstringTwo", MyConcatTwoAggFunctionFactory.class.getName());
        configuration.getEngineDefaults().getThreading().setEngineFairlock(true);
        epService = EPServiceProviderManager.getProvider("TestAggregationFunctionPlugIn", configuration);
        epService.initialize();
    }

    public void tearDown()
    {
        epService.initialize();
    }

    public void testGrouped() throws Exception
    {
        String textOne = "select irstream CONCATSTRING(theString) as val from " + SupportBean.class.getName() + ".win:length(10) group by intPrimitive";
        tryGrouped(textOne, null);

        String textTwo = "select irstream concatstring(theString) as val from " + SupportBean.class.getName() + ".win:length(10) group by intPrimitive";
        tryGrouped(textTwo, null);

        String textThree = "select irstream concatstring(theString) as val from " + SupportBean.class.getName() + ".win:length(10) group by intPrimitive";
        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(textThree);
        SerializableObjectCopier.copy(model);
        assertEquals(textThree, model.toEPL());
        tryGrouped(null, model);

        String textFour = "select irstream concatstring(theString) as val from " + SupportBean.class.getName() + ".win:length(10) group by intPrimitive";
        EPStatementObjectModel modelTwo = new EPStatementObjectModel();
        modelTwo.setSelectClause(SelectClause.create().streamSelector(StreamSelector.RSTREAM_ISTREAM_BOTH)
                .add(Expressions.plugInAggregation("concatstring", Expressions.property("theString")), "val"));
        modelTwo.setFromClause(FromClause.create(FilterStream.create(SupportBean.class.getName()).addView("win", "length", Expressions.constant(10))));
        modelTwo.setGroupByClause(GroupByClause.create("intPrimitive"));
        assertEquals(textFour, modelTwo.toEPL());
        SerializableObjectCopier.copy(modelTwo);
        tryGrouped(null, modelTwo);

        String textFive = "select irstream concatstringTwo(theString) as val from " + SupportBean.class.getName() + ".win:length(10) group by intPrimitive";
        tryGrouped(textFive, null);
    }

    private void tryGrouped(String text, EPStatementObjectModel model)
    {
        EPStatement statement;
        if (model != null)
        {
            statement = epService.getEPAdministrator().create(model);
        }
        else
        {
            statement = epService.getEPAdministrator().createEPL(text);
        }
        SupportUpdateListener listener = new SupportUpdateListener();
        statement.addListener(listener);

        epService.getEPRuntime().sendEvent(new SupportBean("a", 1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a"}, new Object[] {""});

        epService.getEPRuntime().sendEvent(new SupportBean("b", 2));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"b"}, new Object[] {""});

        epService.getEPRuntime().sendEvent(new SupportBean("c", 1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a c"}, new Object[] {"a"});

        epService.getEPRuntime().sendEvent(new SupportBean("d", 2));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"b d"}, new Object[] {"b"});

        epService.getEPRuntime().sendEvent(new SupportBean("e", 1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a c e"}, new Object[] {"a c"});

        epService.getEPRuntime().sendEvent(new SupportBean("f", 2));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"b d f"}, new Object[] {"b d"});

        listener.reset();
    }

    public void testWindow()
    {
        String text = "select irstream concatstring(theString) as val from " + SupportBean.class.getName() + ".win:length(2)";
        EPStatement statement = epService.getEPAdministrator().createEPL(text);
        SupportUpdateListener listener = new SupportUpdateListener();
        statement.addListener(listener);

        epService.getEPRuntime().sendEvent(new SupportBean("a", -1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a"}, new Object[] {""});

        epService.getEPRuntime().sendEvent(new SupportBean("b", -1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a b"}, new Object[] {"a"});

        epService.getEPRuntime().sendEvent(new SupportBean("c", -1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"b c"}, new Object[] {"a b"});

        epService.getEPRuntime().sendEvent(new SupportBean("d", -1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"c d"}, new Object[] {"b c"});
        epService.getEPAdministrator().destroyAllStatements();
    }

    public void testDistinctAndStarParam()
    {
        epService.getEPAdministrator().getConfiguration().addEventType(SupportBean.class);

        // test *-parameter
        String textTwo = "select concatstring(*) as val from SupportBean";
        EPStatement statementTwo = epService.getEPAdministrator().createEPL(textTwo);
        SupportUpdateListener listenerTwo = new SupportUpdateListener();
        statementTwo.addListener(listenerTwo);

        epService.getEPRuntime().sendEvent(new SupportBean("d", -1));
        EPAssertionUtil.assertProps(listenerTwo.assertOneGetNewAndReset(), "val".split(","), new Object[] {"SupportBean(d, -1)"});

        epService.getEPRuntime().sendEvent(new SupportBean("e", 2));
        EPAssertionUtil.assertProps(listenerTwo.assertOneGetNewAndReset(), "val".split(","), new Object[] {"SupportBean(d, -1) SupportBean(e, 2)"});

        try {
            epService.getEPAdministrator().createEPL("select concatstring(*) as val from SupportBean.std:lastevent(), SupportBean unidirectional");
        }
        catch (EPStatementException ex) {
            assertEquals("Error starting statement: Invalid use of wildcard (*) for stream selection in a join or an empty from-clause, please use the stream-alias syntax to select a specific stream instead [select concatstring(*) as val from SupportBean.std:lastevent(), SupportBean unidirectional]", ex.getMessage());
        }

        // test distinct
        String text = "select irstream concatstring(distinct theString) as val from SupportBean";
        EPStatement statement = epService.getEPAdministrator().createEPL(text);
        SupportUpdateListener listener = new SupportUpdateListener();
        statement.addListener(listener);

        epService.getEPRuntime().sendEvent(new SupportBean("a", -1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a"}, new Object[] {""});

        epService.getEPRuntime().sendEvent(new SupportBean("b", -1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a b"}, new Object[] {"a"});

        epService.getEPRuntime().sendEvent(new SupportBean("b", -1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a b"}, new Object[] {"a b"});

        epService.getEPRuntime().sendEvent(new SupportBean("c", -1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a b c"}, new Object[] {"a b"});

        epService.getEPRuntime().sendEvent(new SupportBean("a", -1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a b c"}, new Object[] {"a b c"});
    }

    public void testArrayParamsAndDotMethod()
    {
        epService.getEPAdministrator().getConfiguration().addPlugInAggregationFunction("countback", SupportPluginAggregationMethodOne.class.getName());

        String text = "select irstream countback({1,2,intPrimitive}) as val from " + SupportBean.class.getName();
        EPStatement statement = epService.getEPAdministrator().createEPL(text);
        SupportUpdateListener listener = new SupportUpdateListener();
        statement.addListener(listener);

        epService.getEPRuntime().sendEvent(new SupportBean());
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {-1}, new Object[] {0});

        // test dot-method
        epService.getEPAdministrator().getConfiguration().addEventType(SupportBean_A.class);
        epService.getEPAdministrator().getConfiguration().addPlugInAggregationFunctionFactory("myagg", MyAggFuncFactory.class.getName());
        String[] fields = "val0,val1".split(",");
        epService.getEPAdministrator().createEPL("select (myagg(id)).getTheString() as val0, (myagg(id)).getIntPrimitive() as val1 from SupportBean_A").addListener(listener);
        
        epService.getEPRuntime().sendEvent(new SupportBean_A("A1"));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[] {"XX", 1});
        assertEquals(1, MyAggFuncFactory.getInstanceCount());

        epService.getEPRuntime().sendEvent(new SupportBean_A("A2"));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), fields, new Object[] {"XX", 2});
    }

    public void testMultipleParams()
    {
        epService.getEPAdministrator().getConfiguration().addPlugInAggregationFunction("countboundary", SupportPluginAggregationMethodThree.class.getName());

        String text = "select irstream countboundary(1, 10, intPrimitive) as val from " + SupportBean.class.getName();
        EPStatement statement = epService.getEPAdministrator().createEPL(text);
        SupportUpdateListener listener = new SupportUpdateListener();
        statement.addListener(listener);

        runAssertion(listener);
        
        statement.destroy();
        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(text);
        assertEquals(text, model.toEPL());
        statement = epService.getEPAdministrator().create(model);
        statement.addListener(listener);
        
        runAssertion(listener);
    }

    private void runAssertion(SupportUpdateListener listener) {

        AggregationValidationContext validContext = SupportPluginAggregationMethodThree.getContexts().get(0);
        EPAssertionUtil.assertEqualsExactOrder(new Class[]{Integer.class, Integer.class, int.class}, validContext.getParameterTypes());
        EPAssertionUtil.assertEqualsExactOrder(new Object[]{1, 10, null}, validContext.getConstantValues());
        EPAssertionUtil.assertEqualsExactOrder(new boolean[]{true, true, false}, validContext.getIsConstantValue());

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 5));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {1}, new Object[] {0});

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 0));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {1}, new Object[] {1});

        epService.getEPRuntime().sendEvent(new SupportBean("E1", 11));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {1}, new Object[] {1});
        
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {2}, new Object[] {1});
    }

    public void testNoSubnodesRuntimeAdd()
    {
        epService.getEPAdministrator().getConfiguration().addPlugInAggregationFunction("countback", SupportPluginAggregationMethodOne.class.getName());

        String text = "select irstream countback() as val from " + SupportBean.class.getName();
        EPStatement statement = epService.getEPAdministrator().createEPL(text);
        SupportUpdateListener listener = new SupportUpdateListener();
        statement.addListener(listener);

        epService.getEPRuntime().sendEvent(new SupportBean());
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {-1}, new Object[] {0});

        epService.getEPRuntime().sendEvent(new SupportBean());
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {-2}, new Object[] {-1});
    }

    public void testMappedPropertyLookAlike()
    {
        String text = "select irstream concatstring('a') as val from " + SupportBean.class.getName();
        EPStatement statement = epService.getEPAdministrator().createEPL(text);
        SupportUpdateListener listener = new SupportUpdateListener();
        statement.addListener(listener);
        assertEquals(String.class, statement.getEventType().getPropertyType("val"));

        epService.getEPRuntime().sendEvent(new SupportBean());
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a"}, new Object[] {""});

        epService.getEPRuntime().sendEvent(new SupportBean());
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a a"}, new Object[] {"a"});

        epService.getEPRuntime().sendEvent(new SupportBean());
        EPAssertionUtil.assertPropsPerRow(listener.assertInvokedAndReset(), "val", new Object[] {"a a a"}, new Object[] {"a a"});
    }

    public void testFailedValidation()
    {
        Configuration configuration = SupportConfigFactory.getConfiguration();
        configuration.addPlugInAggregationFunction("concat", SupportPluginAggregationMethodTwo.class.getName());
        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        epService.initialize();

        try
        {
            String text = "select * from " + SupportBean.class.getName() + " group by concat(1)";
            epService.getEPAdministrator().createEPL(text);
        }
        catch (EPStatementException ex)
        {
            assertEquals("Error starting statement: Plug-in aggregation function 'concat' failed validation: Invalid parameter type 'java.lang.Integer', expecting string [select * from com.espertech.esper.support.bean.SupportBean group by concat(1)]", ex.getMessage());
        }

        try
        {
            String text = "select * from " + SupportBean.class.getName() + " group by concat(1, 1)";
            epService.getEPAdministrator().createEPL(text);
        }
        catch (EPStatementException ex)
        {
            assertEquals("Error starting statement: Plug-in aggregation function 'concat' failed validation: Invalid parameter type 'java.lang.Integer', expecting string [select * from com.espertech.esper.support.bean.SupportBean group by concat(1, 1)]", ex.getMessage());
        }
    }

    public void testInvalidUse()
    {
        Configuration configuration = SupportConfigFactory.getConfiguration();
        configuration.addPlugInAggregationFunction("xxx", String.class.getName());
        configuration.addPlugInAggregationFunction("yyy", "com.NoSuchClass");
        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        epService.initialize();

        try
        {
            String text = "select * from " + SupportBean.class.getName() + " group by xxx(1)";
            epService.getEPAdministrator().createEPL(text);
        }
        catch (EPStatementException ex)
        {
            assertEquals("Error in expression: Error resolving aggregation: Aggregation class by name 'java.lang.String' does not subclass AggregationSupport [select * from com.espertech.esper.support.bean.SupportBean group by xxx(1)]", ex.getMessage());
        }

        try
        {
            String text = "select * from " + SupportBean.class.getName() + " group by yyy(1)";
            epService.getEPAdministrator().createEPL(text);
        }
        catch (EPStatementException ex)
        {
            assertEquals("Error in expression: Error resolving aggregation: Could not load aggregation class by name 'com.NoSuchClass' [select * from com.espertech.esper.support.bean.SupportBean group by yyy(1)]", ex.getMessage());
        }
    }

    public void testInvalidConfigure()
    {
        tryInvalidConfigure("a b", "MyClass");
        tryInvalidConfigure("abc", "My Class");

        // configure twice
        try
        {
            epService.getEPAdministrator().getConfiguration().addPlugInAggregationFunction("concatstring", MyConcatAggregationFunction.class.getName());
            fail();
        }
        catch (ConfigurationException ex)
        {
            // expected
        }
    }

    private void tryInvalidConfigure(String funcName, String className)
    {
        try
        {
            epService.getEPAdministrator().getConfiguration().addPlugInAggregationFunction(funcName, className);
            fail();
        }
        catch (ConfigurationException ex)
        {
            // expected
        }
    }

    public void testInvalid()
    {
        tryInvalid("select xxx(theString) from " + SupportBean.class.getName(),
                "Error starting statement: Unknown single-row function, aggregation function or mapped or indexed property named 'xxx' could not be resolved [select xxx(theString) from com.espertech.esper.support.bean.SupportBean]");
    }

    private void tryInvalid(String stmtText, String expectedMsg)
    {
        try
        {
            epService.getEPAdministrator().createEPL(stmtText);
            fail();
        }
        catch (EPStatementException ex)
        {
            assertEquals(expectedMsg, ex.getMessage());
        }
    }

    public static class MyAggFuncFactory implements AggregationFunctionFactory {
        private static int instanceCount;

        public static int getInstanceCount() {
            return instanceCount;
        }

        public void setFunctionName(String functionName) {
        }

        public void validate(AggregationValidationContext validationContext) {
        }

        public AggregationMethod newAggregator() {
            instanceCount++;
            return new MyAggFuncMethod();
        }

        public Class getValueType() {
            return SupportBean.class;
        }
    }

    public static class MyAggFuncMethod implements AggregationMethod {

        private int count;

        public void enter(Object value) {
            count++;
        }

        public void leave(Object value) {
            count--;
        }

        public Object getValue() {
            return new SupportBean("XX", count);
        }

        public Class getValueType() {
            return SupportBean.class;
        }

        public void clear() {
            count = 0;
        }
    }
}
