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

package com.espertech.esper.regression.event;

import com.espertech.esper.client.*;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.support.bean.*;
import com.espertech.esper.support.client.SupportConfigFactory;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestPropertyResolution extends TestCase
{
    private EPServiceProvider epService;

    public void testReservedKeywordEscape()
    {
        epService = EPServiceProviderManager.getDefaultProvider(SupportConfigFactory.getConfiguration());
        epService.initialize();
        epService.getEPAdministrator().getConfiguration().addEventType("SomeKeywords", SupportBeanReservedKeyword.class);
        epService.getEPAdministrator().getConfiguration().addEventType("Order", SupportBeanReservedKeyword.class);

        EPStatement stmt = epService.getEPAdministrator().createEPL("select `seconds`, `order` from SomeKeywords");
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        Object theEvent = new SupportBeanReservedKeyword(1, 2);
        epService.getEPRuntime().sendEvent(theEvent);
        EventBean eventBean = listener.assertOneGetNewAndReset();
        assertEquals(1, eventBean.get("seconds"));
        assertEquals(2, eventBean.get("order"));

        stmt.destroy();
        stmt = epService.getEPAdministrator().createEPL("select * from `Order`");
        stmt.addListener(listener);

        epService.getEPRuntime().sendEvent(theEvent);
        eventBean = listener.assertOneGetNewAndReset();
        assertEquals(1, eventBean.get("seconds"));
        assertEquals(2, eventBean.get("order"));

        stmt.destroy();
        stmt = epService.getEPAdministrator().createEPL("select timestamp.`hour` as val from SomeKeywords");
        stmt.addListener(listener);

        SupportBeanReservedKeyword bean = new SupportBeanReservedKeyword(1, 2);
        bean.setTimestamp(new SupportBeanReservedKeyword.Inner());
        bean.getTimestamp().setHour(10);
        epService.getEPRuntime().sendEvent(bean);
        eventBean = listener.assertOneGetNewAndReset();
        assertEquals(10, eventBean.get("val"));

        // test back-tick with spaces etc
        Map<String, Object> defType = new HashMap<String, Object>();
        defType.put("candidate book", String.class);
        defType.put("XML Message Type", String.class);
        defType.put("select", int.class);
        defType.put("children's books", int[].class);
        defType.put("my <> map", Map.class);
        epService.getEPAdministrator().getConfiguration().addEventType("MyType", defType);
        epService.getEPAdministrator().createEPL("select `candidate book` as c0, `XML Message Type` as c1, `select` as c2, `children's books`[0] as c3, `my <> map`('xx') as c4 from MyType").addListener(listener);

        Map<String, Object> defValues = new HashMap<String, Object>();
        defValues.put("candidate book", "Enders Game");
        defValues.put("XML Message Type", "book");
        defValues.put("select", 100);
        defValues.put("children's books", new int[] {50, 51});
        defValues.put("my <> map", Collections.singletonMap("xx", "abc"));
        epService.getEPRuntime().sendEvent(defValues, "MyType");
        EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), "c0,c1,c2,c3,c4".split(","), new Object[]{"Enders Game", "book", 100, 50, "abc"});
        
        try {
            epService.getEPAdministrator().createEPL("select `select` from " + SupportBean.class.getName());
            fail();
        }
        catch (EPException ex) {
            assertEquals("Error starting statement: Property named 'select' is not valid in any stream [select `select` from com.espertech.esper.support.bean.SupportBean]", ex.getMessage());
        }

        try {
            epService.getEPAdministrator().createEPL("select `ab cd` from " + SupportBean.class.getName());
            fail();
        }
        catch (EPException ex) {
            ex.printStackTrace();
            assertEquals("Error starting statement: Failed to find property 'ab cd', the property name does not parse (are you sure?): Incorrect syntax near 'cd' expecting end of input but found an identifier at line 1 column 3 [ab cd] [select `ab cd` from com.espertech.esper.support.bean.SupportBean]", ex.getMessage());
        }
    }

    public void testWriteOnly()
    {
        epService = EPServiceProviderManager.getDefaultProvider(SupportConfigFactory.getConfiguration());
        epService.initialize();

        EPStatement stmt = epService.getEPAdministrator().createEPL("select * from " + SupportBeanWriteOnly.class.getName());
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        Object theEvent = new SupportBeanWriteOnly();
        epService.getEPRuntime().sendEvent(theEvent);
        EventBean eventBean = listener.assertOneGetNewAndReset();
        assertSame(theEvent, eventBean.getUnderlying());

        EventType type = stmt.getEventType();
        assertEquals(0, type.getPropertyNames().length);
    }

    public void testCaseSensitive()
    {
        epService = EPServiceProviderManager.getDefaultProvider(SupportConfigFactory.getConfiguration());
        epService.initialize();

        EPStatement stmt = epService.getEPAdministrator().createEPL("select MYPROPERTY, myproperty, myProperty from " + SupportBeanDupProperty.class.getName());
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        epService.getEPRuntime().sendEvent(new SupportBeanDupProperty("lowercamel", "uppercamel", "upper", "lower"));
        EventBean result = listener.assertOneGetNewAndReset();
        assertEquals("upper", result.get("MYPROPERTY"));
        assertEquals("lower", result.get("myproperty"));
        assertTrue(result.get("myProperty").equals("lowercamel") || result.get("myProperty").equals("uppercamel")); // JDK6 versus JDK7 JavaBean inspector

        try
        {
            epService.getEPAdministrator().createEPL("select MyProperty from " + SupportBeanDupProperty.class.getName());
            fail();
        }
        catch (EPException ex)
        {
            // expected
        }
    }

    public void testCaseInsensitive()
    {
        Configuration configuration = SupportConfigFactory.getConfiguration();
        configuration.getEngineDefaults().getEventMeta().setClassPropertyResolutionStyle(Configuration.PropertyResolutionStyle.CASE_INSENSITIVE);
        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        epService.initialize();

        EPStatement stmt = epService.getEPAdministrator().createEPL("select MYPROPERTY, myproperty, myProperty, MyProperty from " + SupportBeanDupProperty.class.getName());
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        epService.getEPRuntime().sendEvent(new SupportBeanDupProperty("lowercamel", "uppercamel", "upper", "lower"));
        EventBean result = listener.assertOneGetNewAndReset();
        assertEquals("upper", result.get("MYPROPERTY"));
        assertEquals("lower", result.get("myproperty"));
        assertTrue(result.get("myProperty").equals("lowercamel") || result.get("myProperty").equals("uppercamel")); // JDK6 versus JDK7 JavaBean inspector
        assertEquals("upper", result.get("MyProperty"));

        stmt = epService.getEPAdministrator().createEPL("select " +
                "NESTED.NESTEDVALUE as val1, " +
                "ARRAYPROPERTY[0] as val2, " +
                "MAPPED('keyOne') as val3, " +
                "INDEXED[0] as val4 " +
                " from " + SupportBeanComplexProps.class.getName());
        stmt.addListener(listener);
        epService.getEPRuntime().sendEvent(SupportBeanComplexProps.makeDefaultBean());
        EventBean theEvent = listener.assertOneGetNewAndReset();
        assertEquals("nestedValue", theEvent.get("val1"));
        assertEquals(10, theEvent.get("val2"));
        assertEquals("valueOne", theEvent.get("val3"));
        assertEquals(1, theEvent.get("val4"));
    }

    public void testAccessorStyleGlobalPublic() {
        Configuration configuration = SupportConfigFactory.getConfiguration();
        configuration.getEngineDefaults().getEventMeta().setDefaultAccessorStyle(ConfigurationEventTypeLegacy.AccessorStyle.PUBLIC);
        configuration.addEventType("SupportLegacyBean", SupportLegacyBean.class);
        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        epService.initialize();

        EPStatement stmt = epService.getEPAdministrator().createEPL("select fieldLegacyVal from SupportLegacyBean");
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        SupportLegacyBean theEvent = new SupportLegacyBean("E1");
        theEvent.fieldLegacyVal = "val1";
        epService.getEPRuntime().sendEvent(theEvent);
        assertEquals("val1", listener.assertOneGetNewAndReset().get("fieldLegacyVal"));
    }

    public void testCaseDistinctInsensitive()
    {
        Configuration configuration = SupportConfigFactory.getConfiguration();
        configuration.getEngineDefaults().getEventMeta().setClassPropertyResolutionStyle(Configuration.PropertyResolutionStyle.DISTINCT_CASE_INSENSITIVE);
        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        epService.initialize();

        EPStatement stmt = epService.getEPAdministrator().createEPL("select MYPROPERTY, myproperty, myProperty from " + SupportBeanDupProperty.class.getName());
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        epService.getEPRuntime().sendEvent(new SupportBeanDupProperty("lowercamel", "uppercamel", "upper", "lower"));
        EventBean result = listener.assertOneGetNewAndReset();
        assertEquals("upper", result.get("MYPROPERTY"));
        assertEquals("lower", result.get("myproperty"));
        assertTrue(result.get("myProperty").equals("lowercamel") || result.get("myProperty").equals("uppercamel")); // JDK6 versus JDK7 JavaBean inspector

        try
        {
            epService.getEPAdministrator().createEPL("select MyProperty from " + SupportBeanDupProperty.class.getName());
            fail();
        }
        catch (EPException ex)
        {
            assertEquals("Unexpected exception starting statement: Unable to determine which property to use for \"MyProperty\" because more than one property matched [select MyProperty from com.espertech.esper.support.bean.SupportBeanDupProperty]", ex.getMessage());
            // expected
        }
    }

    public void testCaseInsensitiveEngineDefault()
    {
        Configuration configuration = SupportConfigFactory.getConfiguration();
        configuration.getEngineDefaults().getEventMeta().setClassPropertyResolutionStyle(Configuration.PropertyResolutionStyle.CASE_INSENSITIVE);
        configuration.addEventType("Bean", SupportBean.class);

        tryCaseInsensitive(configuration, "select THESTRING, INTPRIMITIVE from Bean where THESTRING='A'", "THESTRING", "INTPRIMITIVE");
        tryCaseInsensitive(configuration, "select ThEsTrInG, INTprimitIVE from Bean where THESTRing='A'", "ThEsTrInG", "INTprimitIVE");
    }

    public void testCaseInsensitiveTypeConfig()
    {
        Configuration configuration = SupportConfigFactory.getConfiguration();
        ConfigurationEventTypeLegacy legacyDef = new ConfigurationEventTypeLegacy();
        legacyDef.setPropertyResolutionStyle(Configuration.PropertyResolutionStyle.CASE_INSENSITIVE);
        configuration.addEventType("Bean", SupportBean.class.getName(), legacyDef);

        tryCaseInsensitive(configuration, "select theSTRING, INTPRIMITIVE from Bean where THESTRING='A'", "theSTRING", "INTPRIMITIVE");
        tryCaseInsensitive(configuration, "select THEsTrInG, INTprimitIVE from Bean where theSTRing='A'", "THEsTrInG", "INTprimitIVE");
    }

    private void tryCaseInsensitive(Configuration configuration, String stmtText, String propOneName, String propTwoName)
    {
        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        epService.initialize();

        EPStatement stmt = epService.getEPAdministrator().createEPL(stmtText);
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        epService.getEPRuntime().sendEvent(new SupportBean("A", 10));
        EventBean result = listener.assertOneGetNewAndReset();
        assertEquals("A", result.get(propOneName));
        assertEquals(10, result.get(propTwoName));
    }

}
