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

package com.espertech.esper.rowregex;

import com.espertech.esper.epl.parse.EPLTreeWalker;
import com.espertech.esper.epl.parse.TestEPLTreeWalker;
import com.espertech.esper.epl.spec.StatementSpecRaw;
import junit.framework.TestCase;

import java.util.LinkedHashSet;
import java.util.Arrays;

public class TestEventRowRegexHelper extends TestCase
{
    public void testVariableAnalysis() throws Exception
    {
        String[][] patternTests = new String[][] {
                {"A", "[A]", "[]"},
                {"A B", "[A, B]", "[]"},
                {"A B*", "[A]", "[B]"},
                {"A B*", "[A]", "[B]"},
                {"A B B", "[A]", "[B]"},
                {"A B A", "[B]", "[A]"},
                {"A B+ C", "[A, C]", "[B]"},
                {"A B?", "[A, B]", "[]"},
                {"(A B)* C", "[C]", "[A, B]"},
                {"D (A B)+ (G H)? C", "[D, G, H, C]", "[A, B]"},
                {"A B | A C", "[A, B, C]", "[]"},
                {"(A B*) | (A+ C)", "[C]", "[B, A]"},
                {"(A | B) | (C | A)", "[A, B, C]", "[]"},
        };

        for (int i = 0; i < patternTests.length; i++)
        {
            String pattern = patternTests[i][0];
            String expression = "select * from MyEvent.win:keepall() match_recognize (" +
                    "  partition by string measures A.string as a_string pattern ( " + pattern + ") define A as (A.value = 1) )";

            EPLTreeWalker walker = TestEPLTreeWalker.parseAndWalkEPL(expression);
            StatementSpecRaw raw = walker.getStatementSpec();

            RowRegexExprNode parent = raw.getMatchRecognizeSpec().getPattern();
            LinkedHashSet<String> singles = new LinkedHashSet<String>();
            LinkedHashSet<String> multiples = new LinkedHashSet<String>();
            
            EventRowRegexHelper.recursiveInspectVariables(parent, false, singles, multiples);

            String outText = "Failed in :" + pattern +
                    " result is : single " + Arrays.toString(singles.toArray()) +
                    " multiple " + Arrays.toString(multiples.toArray());
            
            assertEquals(outText, patternTests[i][1], Arrays.toString(singles.toArray()));
            assertEquals(outText, patternTests[i][2], Arrays.toString(multiples.toArray()));
        }
    }
}
