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
import com.espertech.esper.client.EPStatementException;
import com.espertech.esper.support.bean.*;
import com.espertech.esper.support.client.SupportConfigFactory;
import junit.framework.TestCase;

public class TestEnumInvalid extends TestCase {

    private EPServiceProvider epService;

    public void setUp() {

        Configuration config = SupportConfigFactory.getConfiguration();
        config.addEventType("SupportBean", SupportBean.class);
        config.addEventType("SupportBean_ST0", SupportBean_ST0.class);
        config.addEventType("SupportBean_ST0_Container", SupportBean_ST0_Container.class);
        config.addEventType("SupportBeanComplexProps", SupportBeanComplexProps.class);
        config.addEventType("SupportCollection", SupportCollection.class);
        config.addImport(SupportBean_ST0_Container.class);
        config.addPlugInSingleRowFunction("makeTest", SupportBean_ST0_Container.class.getName(), "makeTest");
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
    }

    public void testInvalid() {
        String epl;

        // primitive array property
        epl = "select arrayProperty.where(x=>x.boolPrimitive) from SupportBeanComplexProps";
        tryInvalid(epl, "Error starting statement: Error validating enumeration method 'where' parameter 0: Failed to resolve property 'x.boolPrimitive' to a stream or nested property in a stream [select arrayProperty.where(x=>x.boolPrimitive) from SupportBeanComplexProps]");

        // property not there
        epl = "select contained.where(x=>x.dummy = 1) from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Error validating enumeration method 'where' parameter 0: Failed to resolve property 'x.dummy' to a stream or nested property in a stream [select contained.where(x=>x.dummy = 1) from SupportBean_ST0_Container]");

        // test not an enumeration method
        epl = "select contained.notAMethod(x=>x.boolPrimitive) from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Could not find event property, enumeration method or instance method named 'notAMethod' in collection of events of type 'SupportBean_ST0' [select contained.notAMethod(x=>x.boolPrimitive) from SupportBean_ST0_Container]");

        // invalid lambda expression for non-lambda func
        epl = "select makeTest(x=>1) from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Unexpected lambda-expression encountered as parameter to UDF or static method 'makeTest' [select makeTest(x=>1) from SupportBean_ST0_Container]");

        // invalid lambda expression for non-lambda func
        epl = "select SupportBean_ST0_Container.makeTest(x=>1) from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Unexpected lambda-expression encountered as parameter to UDF or static method 'makeTest' [select SupportBean_ST0_Container.makeTest(x=>1) from SupportBean_ST0_Container]");

        // invalid incompatible params
        epl = "select contained.take('a') from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Failed to resolve enumeration method, date-time method or mapped property 'contained.take('a')': Error validating enumeration method 'take', expected a number-type result for expression parameter 0 but received java.lang.String [select contained.take('a') from SupportBean_ST0_Container]");

        // invalid incompatible params
        epl = "select contained.take(x => x.p00) from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Parameters mismatch for enumeration method 'take', the method requires an (non-lambda) expression providing count, but receives a lambda expression [select contained.take(x => x.p00) from SupportBean_ST0_Container]");

        // invalid too many lambda parameter
        epl = "select contained.where((x,y,z) => true) from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Parameters mismatch for enumeration method 'where', the method requires a lambda expression providing predicate, but receives a 3-parameter lambda expression [select contained.where((x,y,z) => true) from SupportBean_ST0_Container]");

        // invalid no parameter
        epl = "select contained.where() from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Parameters mismatch for enumeration method 'where', the method has multiple footprints accepting a lambda expression providing predicate, or a 2-parameter lambda expression providing (predicate, index), but receives no parameters [select contained.where() from SupportBean_ST0_Container]");

        // invalid wrong parameter
        epl = "select contained.where(x=>true,y=>true) from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Parameters mismatch for enumeration method 'where', the method has multiple footprints accepting a lambda expression providing predicate, or a 2-parameter lambda expression providing (predicate, index), but receives a lambda expression and a lambda expression [select contained.where(x=>true,y=>true) from SupportBean_ST0_Container]");

        // invalid wrong parameter
        epl = "select contained.where(1) from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Parameters mismatch for enumeration method 'where', the method requires a lambda expression providing predicate, but receives an (non-lambda) expression [select contained.where(1) from SupportBean_ST0_Container]");

        // invalid too many parameter
        epl = "select contained.where(1,2) from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Parameters mismatch for enumeration method 'where', the method has multiple footprints accepting a lambda expression providing predicate, or a 2-parameter lambda expression providing (predicate, index), but receives an (non-lambda) expression and an (non-lambda) expression [select contained.where(1,2) from SupportBean_ST0_Container]");

        // subselect multiple columns
        epl = "select (select theString, intPrimitive from SupportBean.std:lastevent()).where(x=>x.boolPrimitive) from SupportBean_ST0";
        tryInvalid(epl, "Error starting statement: Invalid input for built-in enumeration method 'where', expecting collection of event-type or scalar values as input, received an incompatible type [select (select theString, intPrimitive from SupportBean.std:lastevent()).where(x=>x.boolPrimitive) from SupportBean_ST0]");

        // subselect individual column
        epl = "select (select theString from SupportBean.std:lastevent()).where(x=>x.boolPrimitive) from SupportBean_ST0";
        tryInvalid(epl, "Error starting statement: Error validating enumeration method 'where' parameter 0: Failed to resolve property 'x.boolPrimitive' to a stream or nested property in a stream [select (select theString from SupportBean.std:lastevent()).where(x=>x.boolPrimitive) from SupportBean_ST0]");

        // aggregation
        epl = "select avg(intPrimitive).where(x=>x.boolPrimitive) from SupportBean_ST0";
        tryInvalid(epl, "Incorrect syntax near '.' at line 1 column 24, please check the select clause near reserved keyword 'where' [select avg(intPrimitive).where(x=>x.boolPrimitive) from SupportBean_ST0]");

        // invalid incompatible params
        epl = "select contained.allOf(x => 1) from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Error validating enumeration method 'allOf', expected a boolean-type result for expression parameter 0 but received java.lang.Integer [select contained.allOf(x => 1) from SupportBean_ST0_Container]");

        // invalid incompatible params
        epl = "select contained.allOf(x => 1) from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Error validating enumeration method 'allOf', expected a boolean-type result for expression parameter 0 but received java.lang.Integer [select contained.allOf(x => 1) from SupportBean_ST0_Container]");

        // invalid incompatible params
        epl = "select contained.aggregate(0, (result, item) => result || ',') from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Error validating enumeration method 'aggregate' parameter 1: Implicit conversion from datatype 'Integer' to string is not allowed [select contained.aggregate(0, (result, item) => result || ',') from SupportBean_ST0_Container]");

        // invalid incompatible params
        epl = "select contained.average(x => x.id) from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Error validating enumeration method 'average', expected a number-type result for expression parameter 0 but received java.lang.String [select contained.average(x => x.id) from SupportBean_ST0_Container]");

        // not a property
        epl = "select contained.firstof().dummy from SupportBean_ST0_Container";
        tryInvalid(epl, "Error starting statement: Could not find enumeration method, date-time method or instance method named 'dummy' in class 'com.espertech.esper.support.bean.SupportBean_ST0' taking no parameters [select contained.firstof().dummy from SupportBean_ST0_Container]");
    }

    private void tryInvalid(String epl, String message) {
        try
        {
            epService.getEPAdministrator().createEPL(epl);
            fail();
        }
        catch (EPStatementException ex) {
            assertEquals(message, ex.getMessage());
        }
    }
}
