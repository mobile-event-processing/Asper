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

import com.espertech.esper.epl.virtualdw.VirtualDWView;
import junit.framework.TestCase;
import com.espertech.esper.epl.join.exec.base.FullTableScanLookupStrategy;
import com.espertech.esper.epl.join.exec.base.JoinExecTableLookupStrategy;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.UnindexedEventTable;

import java.util.HashMap;
import java.util.Map;

public class TestFullTableScanLookupPlan extends TestCase
{
    private UnindexedEventTable unindexedEventIndex;

    public void setUp()
    {
        unindexedEventIndex = new UnindexedEventTable(0);
    }

    public void testLookup()
    {
        FullTableScanLookupPlan spec = new FullTableScanLookupPlan(0, 1, "idx2");

        Map<String,EventTable>[] indexes = new Map[2];
        indexes[0] = new HashMap<String,EventTable>();
        indexes[1] = new HashMap<String,EventTable>();
        indexes[1].put("idx2", unindexedEventIndex);

        JoinExecTableLookupStrategy lookupStrategy = spec.makeStrategy("ABC", "001", null, indexes, null, new VirtualDWView[2]);

        FullTableScanLookupStrategy strategy = (FullTableScanLookupStrategy) lookupStrategy;
        assertEquals(unindexedEventIndex, strategy.getEventIndex());
    }
}
