/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.base;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.join.exec.sorted.SortedAccessStrategy;
import com.espertech.esper.epl.join.exec.sorted.SortedAccessStrategyFactory;
import com.espertech.esper.epl.join.plan.QueryGraphValueEntryRange;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.PropertySortedEventTable;

import java.util.Iterator;
import java.util.Set;

/**
 * Index lookup strategy into a poll-based cache result.
 */
public class HistoricalIndexLookupStrategySorted implements HistoricalIndexLookupStrategy
{
    private final SortedAccessStrategy strategy;

    /**
     * Ctor.
     */
    public HistoricalIndexLookupStrategySorted(int lookupStream, QueryGraphValueEntryRange property)
    {
        strategy = SortedAccessStrategyFactory.make(false, lookupStream, -1, property, null);
    }

    public Iterator<EventBean> lookup(EventBean lookupEvent, EventTable indexTable, ExprEvaluatorContext context)
    {
        // The table may not be indexed as the cache may not actively cache, in which case indexing doesn't makes sense
        if (indexTable instanceof PropertySortedEventTable)
        {
            PropertySortedEventTable index = (PropertySortedEventTable) indexTable;
            Set<EventBean> events = strategy.lookup(lookupEvent, index, context);
            if (events != null)
            {
                return events.iterator();
            }
            return null;
        }

        return indexTable.iterator();
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " strategy: " + strategy.toQueryPlan();
    }
}
