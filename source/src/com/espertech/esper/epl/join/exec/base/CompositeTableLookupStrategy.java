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
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.join.exec.composite.CompositeIndexQuery;
import com.espertech.esper.epl.join.exec.composite.CompositeIndexQueryFactory;
import com.espertech.esper.epl.join.plan.QueryGraphValueEntryHashKeyed;
import com.espertech.esper.epl.join.plan.QueryGraphValueEntryRange;
import com.espertech.esper.epl.join.rep.Cursor;
import com.espertech.esper.epl.join.table.PropertyCompositeEventTable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Lookup on an nested map structure that represents an index for use with at least one range and possibly multiple ranges
 * and optionally keyed by one or more unique keys.
 * <p>
 * Use the sorted strategy instead if supporting a single range only and no other unique keys are part of the index.
 */
public class CompositeTableLookupStrategy implements JoinExecTableLookupStrategy
{
    private final EventType eventType;
    private final PropertyCompositeEventTable index;
    private final CompositeIndexQuery chain;
    private final List<QueryGraphValueEntryRange> rangeKeyPairs;

    /**
     * Ctor.
     * @param eventType - event type to expect for lookup
     * @param index - index to look up in
     */
    public CompositeTableLookupStrategy(EventType eventType, int lookupStream, List<QueryGraphValueEntryHashKeyed> hashKeys, List<QueryGraphValueEntryRange> rangeKeyPairs, PropertyCompositeEventTable index)
    {
        this.eventType = eventType;
        this.index = index;
        this.rangeKeyPairs = rangeKeyPairs;
        chain = CompositeIndexQueryFactory.makeJoinSingleLookupStream(false, lookupStream, hashKeys, index.getOptKeyCoercedTypes(), rangeKeyPairs, index.getOptRangeCoercedTypes());
    }

    /**
     * Returns event type of the lookup event.
     * @return event type of the lookup event
     */
    public EventType getEventType()
    {
        return eventType;
    }

    /**
     * Returns index to look up in.
     * @return index to use
     */
    public PropertyCompositeEventTable getIndex()
    {
        return index;
    }

    public Set<EventBean> lookup(EventBean theEvent, Cursor cursor, ExprEvaluatorContext context)
    {
        Set<EventBean> result = chain.get(theEvent, index.getIndex(), context);
        if (result != null && result.isEmpty()) {
            return null;
        }
        return result;
    }

    public String toString()
    {
        return "CompositeTableLookupStrategy indexProps=" + Arrays.toString(rangeKeyPairs.toArray()) +
                " index=(" + index + ')';
    }
}
