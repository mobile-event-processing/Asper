/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.exec.base;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.join.exec.sorted.SortedAccessStrategy;
import com.espertech.esper.epl.join.exec.sorted.SortedAccessStrategyFactory;
import com.espertech.esper.epl.join.plan.QueryGraphValueEntryRange;
import com.espertech.esper.epl.join.rep.Cursor;
import com.espertech.esper.epl.join.table.PropertySortedEventTable;

import java.util.Set;

/**
 * Lookup on an index that is a sorted index on a single property queried as a range.
 * <p>
 * Use the composite strategy if supporting multiple ranges or if range is in combination with unique key.
 */
public class SortedTableLookupStrategy implements JoinExecTableLookupStrategy
{
    private final QueryGraphValueEntryRange rangeKeyPair;
    private final PropertySortedEventTable index;
    private final SortedAccessStrategy strategy;

    /**
     * Ctor.
     * @param index - index to look up in
     */
    public SortedTableLookupStrategy(int lookupStream, int numStreams, QueryGraphValueEntryRange rangeKeyPair, Class coercionType, PropertySortedEventTable index)
    {
        this.rangeKeyPair = rangeKeyPair;
        this.index = index;
        this.strategy = SortedAccessStrategyFactory.make(false, lookupStream, numStreams, rangeKeyPair, coercionType);
    }

    /**
     * Returns index to look up in.
     * @return index to use
     */
    public PropertySortedEventTable getIndex()
    {
        return index;
    }

    public Set<EventBean> lookup(EventBean theEvent, Cursor cursor, ExprEvaluatorContext exprEvaluatorContext)
    {
        return strategy.lookup(theEvent, index, exprEvaluatorContext);
    }

    public String toString()
    {
        return "SortedTableLookupStrategy indexProps=" + rangeKeyPair +
                " index=(" + index + ')';
    }
}
