/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.lookup;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.join.table.PropertyIndexedEventTable;

import java.util.Arrays;
import java.util.Collection;

/**
 * Index lookup strategy for subqueries.
 */
public class SubordIndexedTableLookupStrategyProp implements SubordTableLookupStrategy
{
    /**
     * Stream numbers to get key values from.
     */
    protected final int[] keyStreamNums;

    /**
     * Getters to use to get key values.
     */
    protected final EventPropertyGetter[] propertyGetters;

    /**
     * Index to look up in.
     */
    protected final PropertyIndexedEventTable index;


    public SubordIndexedTableLookupStrategyProp(int[] keyStreamNums, EventPropertyGetter[] propertyGetters, PropertyIndexedEventTable index) {
        this.keyStreamNums = keyStreamNums;
        this.propertyGetters = propertyGetters;
        this.index = index;
    }

    /**
     * Returns index to look up in.
     * @return index to use
     */
    public PropertyIndexedEventTable getIndex()
    {
        return index;
    }

    public Collection<EventBean> lookup(EventBean[] eventsPerStream, ExprEvaluatorContext context)
    {
        Object[] keys = getKeys(eventsPerStream);
        return index.lookup(keys);
    }

    public Collection<EventBean> lookup(Object[] keys) {
        return index.lookup(keys);
    }

    /**
     * Get the index lookup keys.
     * @param eventsPerStream is the events for each stream
     * @return key object
     */
    protected Object[] getKeys(EventBean[] eventsPerStream)
    {
        Object[] keyValues = new Object[propertyGetters.length];
        for (int i = 0; i < propertyGetters.length; i++)
        {
            int streamNum = keyStreamNums[i];
            EventBean theEvent = eventsPerStream[streamNum];
            keyValues[i] = propertyGetters[i].get(theEvent);
        }
        return keyValues;
    }

    public String toString()
    {
        return toQueryPlan();
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() +
                " keyStreamNums=" + Arrays.toString(keyStreamNums);
    }
}
