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

package com.espertech.esper.epl.join.plan;

import com.espertech.esper.epl.expression.ExprIdentNode;
import com.espertech.esper.epl.expression.ExprIdentNodeImpl;
import com.espertech.esper.epl.expression.ExprNode;
import junit.framework.TestCase;

import java.util.Map;

public class TestQueryGraphValue extends TestCase {

    public void testRangeRelOp() {

        tryAdd("b", QueryGraphRangeEnum.GREATER_OR_EQUAL, "a",      // read a >= b
               "c", QueryGraphRangeEnum.LESS_OR_EQUAL, "a",         // read a <= c
                new Object[][] {{null, "b", "c", QueryGraphRangeEnum.RANGE_CLOSED, "a"}});

        tryAdd("b", QueryGraphRangeEnum.GREATER, "a",      // read a > b
               "c", QueryGraphRangeEnum.LESS, "a",         // read a < c
                new Object[][] {{null, "b", "c", QueryGraphRangeEnum.RANGE_OPEN, "a"}});

        tryAdd("b", QueryGraphRangeEnum.GREATER_OR_EQUAL, "a",      // read a >= b
               "c", QueryGraphRangeEnum.LESS, "a",                  // read a < c
                new Object[][] {{null, "b", "c", QueryGraphRangeEnum.RANGE_HALF_OPEN, "a"}});

        tryAdd("b", QueryGraphRangeEnum.GREATER, "a",                       // read a > b
               "c", QueryGraphRangeEnum.LESS_OR_EQUAL, "a",                  // read a <= c
                new Object[][] {{null, "b", "c", QueryGraphRangeEnum.RANGE_HALF_CLOSED, "a"}});

        // sanity
        tryAdd("b", QueryGraphRangeEnum.LESS_OR_EQUAL, "a",                     // read a <= b
               "c", QueryGraphRangeEnum.GREATER_OR_EQUAL, "a",                  // read a >= c
                new Object[][] {{null, "c", "b", QueryGraphRangeEnum.RANGE_CLOSED, "a"}});
    }

    private void tryAdd(String propertyKeyOne, QueryGraphRangeEnum opOne, String valueOne,
                        String propertyKeyTwo, QueryGraphRangeEnum opTwo, String valueTwo,
                        Object[][] expected) {

        QueryGraphValue value = new QueryGraphValue();
        value.addRelOp(new ExprIdentNodeImpl(propertyKeyOne), opOne, valueOne, true);
        value.addRelOp(new ExprIdentNodeImpl(propertyKeyTwo), opTwo, valueTwo, true);
        assertRanges(expected, value);

        value = new QueryGraphValue();
        value.addRelOp(new ExprIdentNodeImpl(propertyKeyTwo), opTwo, valueTwo, true);
        value.addRelOp(new ExprIdentNodeImpl(propertyKeyOne), opOne, valueOne, true);
        assertRanges(expected, value);
    }

    public void testNoDup() {

        QueryGraphValue value = new QueryGraphValue();
        value.addRelOp(new ExprIdentNodeImpl("b"), QueryGraphRangeEnum.LESS_OR_EQUAL, "a", false);
        value.addRelOp(new ExprIdentNodeImpl("b"), QueryGraphRangeEnum.LESS_OR_EQUAL, "a", false);
        assertRanges(new Object[][] {{"b", null, null, QueryGraphRangeEnum.LESS_OR_EQUAL, "a"}}, value);

        value = new QueryGraphValue();
        value.addRange(QueryGraphRangeEnum.RANGE_CLOSED, new ExprIdentNodeImpl("b"), new ExprIdentNodeImpl("c"), "a");
        value.addRange(QueryGraphRangeEnum.RANGE_CLOSED, new ExprIdentNodeImpl("b"), new ExprIdentNodeImpl("c"), "a");
        assertRanges(new Object[][] {{null, "b", "c", QueryGraphRangeEnum.RANGE_CLOSED, "a"}}, value);
    }

    private void assertRanges(Object[][] ranges, QueryGraphValue value) {
        assertEquals(ranges.length, value.getEntries().size());

        int count = -1;
        for (Map.Entry<String, QueryGraphValueEntry> entry : value.getEntries().entrySet()) {
            count++;
            QueryGraphValueEntryRange r = (QueryGraphValueEntryRange) entry.getValue();

            assertEquals(ranges[count][3], r.getType());
            assertEquals(ranges[count][4], entry.getKey());

            if (r instanceof QueryGraphValueEntryRangeRelOp) {
                QueryGraphValueEntryRangeRelOp relOp = (QueryGraphValueEntryRangeRelOp) r;
                assertEquals(ranges[count][0], getProp(relOp.getExpression()));
            }
            else {
                QueryGraphValueEntryRangeIn rangeIn = (QueryGraphValueEntryRangeIn) r;
                assertEquals(ranges[count][1], getProp(rangeIn.getExprStart()));
                assertEquals(ranges[count][2], getProp(rangeIn.getExprEnd()));
            }
        }
    }

    private String getProp(ExprNode node) {
        return ((ExprIdentNode) node).getUnresolvedPropertyName();
    }
}
