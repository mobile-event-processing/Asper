/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.lookup;

import com.espertech.esper.epl.join.exec.sorted.SortedAccessStrategy;
import com.espertech.esper.epl.join.exec.sorted.SortedAccessStrategyFactory;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.PropertySortedEventTable;

/**
 * Index lookup strategy for subqueries.
 */
public class SubordSortedTableLookupStrategyFactory implements SubordTableLookupStrategyFactory
{
    private final SubordPropRangeKey rangeKey;

    protected final SortedAccessStrategy strategy;

    public SubordSortedTableLookupStrategyFactory(boolean isNWOnTrigger, int numStreams, SubordPropRangeKey rangeKey)
    {
        this.rangeKey = rangeKey;
        this.strategy = SortedAccessStrategyFactory.make(isNWOnTrigger, -1, numStreams, rangeKey);
    }

    public SubordTableLookupStrategy makeStrategy(EventTable eventTable) {
        return new SubordSortedTableLookupStrategy(strategy, (PropertySortedEventTable) eventTable);
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " range " + rangeKey.toQueryPlan();
    }
}
