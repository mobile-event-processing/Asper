/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.plan;

import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.join.exec.base.CompositeTableLookupStrategy;
import com.espertech.esper.epl.join.exec.base.JoinExecTableLookupStrategy;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.PropertyCompositeEventTable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Plan to perform an indexed table lookup.
 */
public class CompositeTableLookupPlan extends TableLookupPlan
{
    private final List<QueryGraphValueEntryHashKeyed> hashKeys;
    private final List<QueryGraphValueEntryRange> rangeKeyPairs;

    /**
     * Ctor.
     * @param lookupStream - stream that generates event to look up for
     * @param indexedStream - stream to index table lookup
     * @param indexNum - index number for the table containing the full unindexed contents
     */
    public CompositeTableLookupPlan(int lookupStream, int indexedStream, String indexNum, List<QueryGraphValueEntryHashKeyed> hashKeys, List<QueryGraphValueEntryRange> rangeKeyPairs)
    {
        super(lookupStream, indexedStream, indexNum);
        this.hashKeys = hashKeys;
        this.rangeKeyPairs = rangeKeyPairs;
    }

    public TableLookupKeyDesc getKeyDescriptor() {
        return new TableLookupKeyDesc(hashKeys, rangeKeyPairs);
    }

    public JoinExecTableLookupStrategy makeStrategyInternal(EventTable eventTable, EventType[] eventTypes)
    {
        PropertyCompositeEventTable index = (PropertyCompositeEventTable) eventTable;
        return new CompositeTableLookupStrategy(eventTypes[this.getLookupStream()], this.getLookupStream(), hashKeys, rangeKeyPairs, index);
    }

    public String toString()
    {
        return "CompositeTableLookupPlan " +
                super.toString() +
                " directKeys=" + QueryGraphValueEntryHashKeyed.toQueryPlan(hashKeys) +
                " rangeKeys=" + QueryGraphValueEntryRange.toQueryPlan(rangeKeyPairs);
    }
}
