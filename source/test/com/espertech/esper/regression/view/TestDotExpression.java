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
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.support.bean.*;
import com.espertech.esper.support.client.SupportConfigFactory;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.Map;

public class TestDotExpression extends TestCase
{
	private EPServiceProvider epService;
	private SupportUpdateListener listener;

	protected void setUp()
	{
	    epService = EPServiceProviderManager.getDefaultProvider(SupportConfigFactory.getConfiguration());
	    epService.initialize();
        listener = new SupportUpdateListener();
	}

    protected void tearDown() throws Exception {
        listener = null;
    }

    public void testMapIndexPropertyRooted() {
        epService.getEPAdministrator().getConfiguration().addEventType(MyTypeErasure.class);
        EPStatement stmt = epService.getEPAdministrator().createEPL("select " +
                "innerTypes('key1') as c0,\n" +
                "innerTypes(key) as c1,\n" +
                "innerTypes('key1').ids[1] as c2,\n" +
                "innerTypes(key).getIds(subkey) as c3,\n" +
                "innerTypesArray[1].ids[1] as c4,\n" +
                "innerTypesArray(subkey).getIds(subkey) as c5\n" +
                "from MyTypeErasure");
        stmt.addListener(listener);
        assertEquals(InnerType.class, stmt.getEventType().getPropertyType("c0"));
        assertEquals(InnerType.class, stmt.getEventType().getPropertyType("c1"));
        assertEquals(int.class, stmt.getEventType().getPropertyType("c2"));
        assertEquals(int.class, stmt.getEventType().getPropertyType("c3"));
        
        MyTypeErasure event = new MyTypeErasure("key1", 2, Collections.singletonMap("key1", new InnerType(new int[] {20, 30, 40})), new InnerType[] {new InnerType(new int[] {2, 3}), new InnerType(new int[] {4, 5}), new InnerType(new int[] {6, 7, 8})});
        epService.getEPRuntime().sendEvent(event);
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), "c0,c1,c2,c3,c4,c5".split(","), new Object[] {event.getInnerTypes().get("key1"), event.getInnerTypes().get("key1"), 30, 40, 5, 8});
    }

    public void testInvalid() {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBean", SupportBean.class);
        epService.getEPAdministrator().getConfiguration().addEventType("SupportChainTop", SupportChainTop.class);

        tryInvalid("select (abc).noSuchMethod() from SupportBean abc",
                "Error starting statement: Could not find enumeration method, date-time method or instance method named 'noSuchMethod' in class 'com.espertech.esper.support.bean.SupportBean' taking no parameters [select (abc).noSuchMethod() from SupportBean abc]");
        tryInvalid("select (abc).getChildOne(\"abc\", 10).noSuchMethod() from SupportChainTop abc",
                "Error starting statement: Could not find enumeration method, date-time method or instance method named 'noSuchMethod' in class 'com.espertech.esper.support.bean.SupportChainChildOne' taking no parameters [select (abc).getChildOne(\"abc\", 10).noSuchMethod() from SupportChainTop abc]");
    }

    public void testNestedPropertyInstanceExpr() {
        epService.getEPAdministrator().getConfiguration().addEventType("LevelZero", LevelZero.class);
        epService.getEPAdministrator().createEPL("select " +
                "levelOne.getCustomLevelOne(10) as val0, " +
                "levelOne.levelTwo.getCustomLevelTwo(20) as val1, " +
                "levelOne.levelTwo.levelThree.getCustomLevelThree(30) as val2 " +
                "from LevelZero").addListener(listener);
        
        epService.getEPRuntime().sendEvent(new LevelZero(new LevelOne(new LevelTwo(new LevelThree()))));
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), "val0,val1,val2".split(","), new Object[]{"level1:10", "level2:20", "level3:30"});
    }

    public void testChainedUnparameterized() {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBeanComplexProps", SupportBeanComplexProps.class);

        String epl = "select " +
                "(nested).getNestedValue(), " +
                "(nested).getNestedNested().getNestedNestedValue() " +
                "from SupportBeanComplexProps";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);

        SupportBeanComplexProps bean = SupportBeanComplexProps.makeDefaultBean();
        Object[][] rows = new Object[][] {
                {"(nested).getNestedValue()", String.class}
                };
        for (int i = 0; i < rows.length; i++) {
            EventPropertyDescriptor prop = stmt.getEventType().getPropertyDescriptors()[i];
            assertEquals(rows[i][0], prop.getPropertyName());
            assertEquals(rows[i][1], prop.getPropertyType());
        }

        epService.getEPRuntime().sendEvent(bean);
        EPAssertionUtil.assertProps(listener.assertOneGetNew(), "(nested).getNestedValue()".split(","), new Object[]{bean.getNested().getNestedValue()});
    }

    public void testChainedParameterized() {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportChainTop", SupportChainTop.class);

        String subexpr="(top).getChildOne(\"abc\", 10).getChildTwo(\"append\")";
        String epl = "select " +
                subexpr +
                " from SupportChainTop as top";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);

        runAssertionChainedParam(stmt, subexpr);

        listener.reset();
        stmt.destroy();
        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(epl);
        assertEquals(epl, model.toEPL());
        stmt = epService.getEPAdministrator().create(model);
        stmt.addListener(listener);

        runAssertionChainedParam(stmt, subexpr);
    }

    public void testArrayPropertySizeAndGet() {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBeanComplexProps", SupportBeanComplexProps.class);

        String epl = "select " +
                "(arrayProperty).size() as size, " +
                "(arrayProperty).get(0) as get0, " +
                "(arrayProperty).get(1) as get1, " +
                "(arrayProperty).get(2) as get2, " +
                "(arrayProperty).get(3) as get3 " +
                "from SupportBeanComplexProps";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);

        SupportBeanComplexProps bean = SupportBeanComplexProps.makeDefaultBean();
        Object[][] rows = new Object[][] {
                {"size", Integer.class},
                {"get0", int.class},
                {"get1", int.class},
                {"get2", int.class},
                {"get3", int.class}
                };
        for (int i = 0; i < rows.length; i++) {
            EventPropertyDescriptor prop = stmt.getEventType().getPropertyDescriptors()[i];
            assertEquals("failed for " + rows[i][0], rows[i][0], prop.getPropertyName());
            assertEquals("failed for " + rows[i][0], rows[i][1], prop.getPropertyType());
        }

        epService.getEPRuntime().sendEvent(bean);
        EPAssertionUtil.assertProps(listener.assertOneGetNew(), "size,get0,get1,get2,get3".split(","),
                new Object[]{bean.getArrayProperty().length, bean.getArrayProperty()[0], bean.getArrayProperty()[1], bean.getArrayProperty()[2], null});
    }
    
    public void testArrayPropertySizeAndGetChained() {
        epService.getEPAdministrator().getConfiguration().addEventType("SupportBeanCombinedProps", SupportBeanCombinedProps.class);

        String epl = "select " +
                "(abc).getArray().size() as size, " +
                "(abc).getArray().get(0).getNestLevOneVal() as get0 " +
                "from SupportBeanCombinedProps as abc";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        stmt.addListener(listener);

        SupportBeanCombinedProps bean = SupportBeanCombinedProps.makeDefaultBean();
        Object[][] rows = new Object[][] {
                {"size", Integer.class},
                {"get0", String.class},
                };
        for (int i = 0; i < rows.length; i++) {
            EventPropertyDescriptor prop = stmt.getEventType().getPropertyDescriptors()[i];
            assertEquals(rows[i][0], prop.getPropertyName());
            assertEquals(rows[i][1], prop.getPropertyType());
        }

        epService.getEPRuntime().sendEvent(bean);
        EPAssertionUtil.assertProps(listener.assertOneGetNew(), "size,get0".split(","),
                new Object[]{bean.getArray().length, bean.getArray()[0].getNestLevOneVal()});
    }

    private void runAssertionChainedParam(EPStatement stmt, String subexpr) {

        Object[][] rows = new Object[][] {
                {subexpr, SupportChainChildTwo.class}
                };
        for (int i = 0; i < rows.length; i++) {
            EventPropertyDescriptor prop = stmt.getEventType().getPropertyDescriptors()[i];
            assertEquals(rows[i][0], prop.getPropertyName());
            assertEquals(rows[i][1], prop.getPropertyType());
        }

        epService.getEPRuntime().sendEvent(new SupportChainTop());
        Object result = listener.assertOneGetNewAndReset().get(subexpr);
        assertEquals("abcappend", ((SupportChainChildTwo)result).getText());
    }

    private void tryInvalid(String epl, String message)
    {
        try {
            epService.getEPAdministrator().createEPL(epl);
            fail();
        }
        catch (EPStatementException ex) {
            assertEquals(message, ex.getMessage());
        }
    }

    public static class LevelZero {
        private LevelOne levelOne;

        private LevelZero(LevelOne levelOne) {
            this.levelOne = levelOne;
        }

        public LevelOne getLevelOne() {
            return levelOne;
        }
    }

    public static class LevelOne {
        private LevelTwo levelTwo;

        public LevelOne(LevelTwo levelTwo) {
            this.levelTwo = levelTwo;
        }

        public LevelTwo getLevelTwo() {
            return levelTwo;
        }

        public String getCustomLevelOne(int val) {
            return "level1:" + val;
        }
    }

    public static class LevelTwo {
        private LevelThree levelThree;

        public LevelTwo(LevelThree levelThree) {
            this.levelThree = levelThree;
        }

        public LevelThree getLevelThree() {
            return levelThree;
        }

        public String getCustomLevelTwo(int val) {
            return "level2:" + val;
        }
    }

    public static class LevelThree {
        public String getCustomLevelThree(int val) {
            return "level3:" + val;
        }
    }

    public static class MyTypeErasure {

        private String key;
        private int subkey;
        private Map<String, InnerType> innerTypes;
        private InnerType[] innerTypesArray;

        public MyTypeErasure(String key, int subkey, Map<String, InnerType> innerTypes, InnerType[] innerTypesArray) {
            this.key = key;
            this.subkey = subkey;
            this.innerTypes = innerTypes;
            this.innerTypesArray = innerTypesArray;
        }

        public Map<String, InnerType> getInnerTypes() {
            return innerTypes;
        }

        public String getKey() {
            return key;
        }

        public int getSubkey() {
            return subkey;
        }

        public InnerType[] getInnerTypesArray() {
            return innerTypesArray;
        }
    }

    public static class InnerType {

        private final int[] ids;

        public InnerType(int[] ids) {
            this.ids = ids;
        }

        public int[] getIds() {
            return ids;
        }

        public int getIds(int subkey) {
            return ids[subkey];
        }
    }
}
