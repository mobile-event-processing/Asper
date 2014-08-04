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

import junit.framework.TestCase;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatementSyntaxException;
import com.espertech.esper.support.bean.SupportBeanReservedKeyword;
import com.espertech.esper.support.client.SupportConfigFactory;

public class TestInvalidSyntaxMsg extends TestCase
{
    private EPServiceProvider epService;

    public void setUp()
    {
        epService = EPServiceProviderManager.getDefaultProvider(SupportConfigFactory.getConfiguration());
        epService.initialize();
    }

    public void testInvalidSyntax()
    {
        tryCompile("insert into 7event select * from " + SupportBeanReservedKeyword.class.getName(),
                   "Incorrect syntax near ' ' ('into' is a reserved keyword) at line 1 column 11 unexpected character 'v', check for an invalid identifier or missing additional keywords [insert into 7event select * from com.espertech.esper.support.bean.SupportBeanReservedKeyword]");

        tryCompile("select foo, create from " + SupportBeanReservedKeyword.class.getName(),
                   "Incorrect syntax near 'create' (a reserved keyword) at line 1 column 12, please check the select clause [select foo, create from com.espertech.esper.support.bean.SupportBeanReservedKeyword]");

        tryCompile("select * from pattern [",
                   "Unexpected end of input near '[' at line 1 column 22, please check the pattern expression within the from clause [select * from pattern []");

        tryCompile("select * from A, into",
                   "Incorrect syntax near 'into' (a reserved keyword) at line 1 column 17, please check the from clause [select * from A, into]");

        tryCompile("select * from pattern[A -> B - C]",
                   "Incorrect syntax near 'B' at line 1 column 27, please check the pattern expression within the from clause [select * from pattern[A -> B - C]]");

        tryCompile("insert into A (a",
                   "Incorrect syntax near 'a' expecting a closing parenthesis ')' but found end of input at line 1 column 15, please check the insert-into clause [insert into A (a]");

        tryCompile("select case when 1>2 from A",
                   "Incorrect syntax near 'from' (a reserved keyword) expecting 'then' but found 'from' at line 1 column 21, please check the case expression within the select clause [select case when 1>2 from A]");

        tryCompile("select * from A full outer join B on A.field < B.field",
                   "Incorrect syntax near '<' expecting an equals '=' but found a lesser then '<' at line 1 column 45, please check the outer join within the from clause [select * from A full outer join B on A.field < B.field]");

        tryCompile("select a.b('aa\") from A",
                   "Incorrect syntax near '(' expecting a closing parenthesis ')' but found end of input at line 1 column 10, please check the select clause [select a.b('aa\") from A]");

        tryCompile("select * from A, sql:mydb [\"",
                   "Unexpected end of input near '[' at line 1 column 26, please check the relational data join within the from clause [select * from A, sql:mydb [\"]");

        tryCompile("select * google",
                   "Incorrect syntax near 'google' expecting 'from' but found an identifier at line 1 column 9 [select * google]");

        tryCompile("insert into into",
                   "Incorrect syntax near 'into' (a reserved keyword) at line 1 column 12, please check the insert-into clause [insert into into]");

        tryCompile("select prior(A, x) from A",
                   "Incorrect syntax near 'A' at line 1 column 13, please check the select clause [select prior(A, x) from A]");
    }

    private void tryCompile(String expression, String expectedMsg)
    {
        try
        {
            epService.getEPAdministrator().createEPL(expression);
            fail();
        }
        catch (EPStatementSyntaxException ex)
        {
            assertEquals(expectedMsg, ex.getMessage());
            // expected
        }
    }

}
