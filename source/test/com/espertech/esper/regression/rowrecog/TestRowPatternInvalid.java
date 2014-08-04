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

package com.espertech.esper.regression.rowrecog;

import com.espertech.esper.client.*;
import com.espertech.esper.support.client.SupportConfigFactory;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestRowPatternInvalid extends TestCase {

    private static final Log log = LogFactory.getLog(TestRowPatternInvalid.class);

    private EPServiceProvider epService;

    public void setUp()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        config.addEventType("MyEvent", SupportRecogBean.class);
        epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();
    }

    public void testInvalid()
    {
        Configuration config = SupportConfigFactory.getConfiguration();
        config.addEventType("MyEvent", SupportRecogBean.class);
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
        epService.initialize();

        // invalid after syntax
        String text = "select * from MyEvent.win:keepall() " +
                "match_recognize (" +
                "  measures A.theString as a" +
                "  AFTER MATCH SKIP TO OTHER ROW " +
                "  pattern (A B*) " +
                "  define " +
                "    A as A.theString like 'A%'" +
                ")";
        tryInvalid(text, "Error in expression: Match-recognize AFTER clause must be either AFTER MATCH SKIP TO LAST ROW or AFTER MATCH SKIP TO NEXT ROW or AFTER MATCH SKIP TO CURRENT ROW [select * from MyEvent.win:keepall() match_recognize (  measures A.theString as a  AFTER MATCH SKIP TO OTHER ROW   pattern (A B*)   define     A as A.theString like 'A%')]");

        // property cannot resolve
        text = "select * from MyEvent.win:keepall() " +
                "match_recognize (" +
                "  measures A.theString as a, D.theString as x" +
                "  pattern (A B*) " +
                "  define " +
                "    A as A.theString like 'A%'" +
                ")";
        tryInvalid(text, "Error starting statement: Failed to resolve property 'D.theString' to a stream or nested property in a stream, ensure that grouped variables (variables B) are accessed via index (i.e. variable[0].property) or appear within an aggregation, ensure that singleton variables (variables A) are not accessed via index [select * from MyEvent.win:keepall() match_recognize (  measures A.theString as a, D.theString as x  pattern (A B*)   define     A as A.theString like 'A%')]");

        // property not named
        text = "select * from MyEvent.win:keepall() " +
                "match_recognize (" +
                "  measures A.theString, A.theString as xxx" +
                "  pattern (A B*) " +
                "  define " +
                "    A as A.theString like 'A%'" +
                ")";
        tryInvalid(text, "Error starting statement: The measures clause requires that each expression utilizes the AS keyword to assign a column name [select * from MyEvent.win:keepall() match_recognize (  measures A.theString, A.theString as xxx  pattern (A B*)   define     A as A.theString like 'A%')]");

        // grouped property not indexed
        text = "select * from MyEvent.win:keepall() " +
                "match_recognize (" +
                "  measures B.theString as b1" +
                "  pattern (A B*) " +
                "  define " +
                "    A as A.theString like 'A%'" +
                ")";
        tryInvalid(text, "Error starting statement: Failed to resolve property 'B.theString' to a stream or nested property in a stream, ensure that grouped variables (variables B) are accessed via index (i.e. variable[0].property) or appear within an aggregation, ensure that singleton variables (variables A) are not accessed via index [select * from MyEvent.win:keepall() match_recognize (  measures B.theString as b1  pattern (A B*)   define     A as A.theString like 'A%')]");

        // define twice
        text = "select * from MyEvent.win:keepall() " +
                "match_recognize (" +
                "  measures A.theString as a" +
                "  pattern (A B*) " +
                "  define " +
                "    A as A.theString like 'A%'," +
                "    A as A.theString like 'A%'" +
                ")";
        tryInvalid(text, "Error starting statement: Variable 'A' has already been defined [select * from MyEvent.win:keepall() match_recognize (  measures A.theString as a  pattern (A B*)   define     A as A.theString like 'A%',    A as A.theString like 'A%')]");

        // define for not used variable
        text = "select * from MyEvent.win:keepall() " +
                "match_recognize (" +
                "  measures A.theString as a" +
                "  pattern (A B*) " +
                "  define " +
                "    X as X.theString like 'A%'" +
                ")";
        tryInvalid(text, "Error starting statement: Variable 'X' does not occur in pattern [select * from MyEvent.win:keepall() match_recognize (  measures A.theString as a  pattern (A B*)   define     X as X.theString like 'A%')]");

        // define mentions another variable
        text = "select * from MyEvent.win:keepall() " +
                "match_recognize (" +
                "  measures A.theString as a" +
                "  pattern (A B*) " +
                "  define " +
                "    A as B.theString like 'A%'" +
                ")";
        tryInvalid(text, "Error starting statement: Failed to find a stream named 'B' (did you mean 'A'?) [select * from MyEvent.win:keepall() match_recognize (  measures A.theString as a  pattern (A B*)   define     A as B.theString like 'A%')]");

        // aggregation over multiple groups
        text = "select * from MyEvent.win:keepall() " +
                "match_recognize (" +
                "  measures sum(A.value+B.value) as mytotal" +
                "  pattern (A* B*) " +
                "  define " +
                "    A as A.theString like 'A%'" +
                ")";
        tryInvalid(text, "Error starting statement: Aggregation functions in the measure-clause must only refer to properties of exactly one group variable returning multiple events [select * from MyEvent.win:keepall() match_recognize (  measures sum(A.value+B.value) as mytotal  pattern (A* B*)   define     A as A.theString like 'A%')]");

        // aggregation over no groups
        text = "select * from MyEvent.win:keepall() " +
                "match_recognize (" +
                "  measures sum(A.value) as mytotal" +
                "  pattern (A B*) " +
                "  define " +
                "    A as A.theString like 'A%'" +
                ")";
        tryInvalid(text, "Error starting statement: Aggregation functions in the measure-clause must refer to one or more properties of exactly one group variable returning multiple events [select * from MyEvent.win:keepall() match_recognize (  measures sum(A.value) as mytotal  pattern (A B*)   define     A as A.theString like 'A%')]");

        // aggregation in define
        text = "select * from MyEvent.win:keepall() " +
                "match_recognize (" +
                "  measures A.theString as astring" +
                "  pattern (A B) " +
                "  define " +
                "    A as sum(A.value + B.value) > 3000" +
                ")";
        tryInvalid(text, "Error starting statement: An aggregate function may not appear in a DEFINE clause [select * from MyEvent.win:keepall() match_recognize (  measures A.theString as astring  pattern (A B)   define     A as sum(A.value + B.value) > 3000)]");

        // join disallowed
        text = "select * from MyEvent.win:keepall(), MyEvent.win:keepall() " +
                "match_recognize (" +
                "  measures A.value as aval" +
                "  pattern (A B*) " +
                "  define " +
                "    A as A.theString like 'A%'" +
                ")";
        tryInvalid(text, "Error starting statement: Joins are not allowed when using match recognize [select * from MyEvent.win:keepall(), MyEvent.win:keepall() match_recognize (  measures A.value as aval  pattern (A B*)   define     A as A.theString like 'A%')]");
    }

    private void tryInvalid(String epl, String error)
    {
        try
        {
            epService.getEPAdministrator().createEPL(epl);
            fail();
        }
        catch (EPStatementException ex)
        {
            assertEquals(error, ex.getMessage());
        }
    }
}