/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.lookup;

import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.UnindexedEventTable;

/**
 * Factory for lookup on an unindexed table returning the full table as matching events.
 */
public class SubordFullTableScanLookupStrategyFactory implements SubordTableLookupStrategyFactory
{
    public SubordFullTableScanLookupStrategy makeStrategy(EventTable eventTable) {
        return new SubordFullTableScanLookupStrategy((UnindexedEventTable)eventTable);
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName();
    }
}
