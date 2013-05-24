/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.lookup;

import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.PropertyIndexedEventTable;

import java.util.Arrays;

/**
 * Index lookup strategy for subqueries.
 */
public class SubordIndexedTableLookupStrategyPropFactory implements SubordTableLookupStrategyFactory
{
    private final String[] properties;

    /**
     * Stream numbers to get key values from.
     */
    protected final int[] keyStreamNums;

    /**
     * Getters to use to get key values.
     */
    protected final EventPropertyGetter[] propertyGetters;

    /**
     * Ctor.
     * @param eventTypes is the event types per stream
     * @param keyStreamNumbers is the stream number per property
     * @param properties is the key properties
     */
    public SubordIndexedTableLookupStrategyPropFactory(boolean isNWOnTrigger, EventType[] eventTypes, int[] keyStreamNumbers, String[] properties)
    {
        this.keyStreamNums = keyStreamNumbers;
        this.properties = properties;

        propertyGetters = new EventPropertyGetter[properties.length];
        for (int i = 0; i < keyStreamNumbers.length; i++)
        {
            int streamNumber = keyStreamNumbers[i];
            String property = properties[i];
            EventType eventType = eventTypes[streamNumber];
            propertyGetters[i] = eventType.getGetter(property);

            if (propertyGetters[i] == null)
            {
                throw new IllegalArgumentException("Property named '" + properties[i] + "' is invalid for type " + eventType);
            }
        }

        for (int i = 0; i < keyStreamNums.length; i++) {
            keyStreamNums[i] += (isNWOnTrigger ? 1 : 0); // for on-trigger the key will be provided in a {1,2,...} stream and not {0,...}
        }
    }

    public SubordTableLookupStrategy makeStrategy(EventTable eventTable) {
        return new SubordIndexedTableLookupStrategyProp(keyStreamNums, propertyGetters, (PropertyIndexedEventTable) eventTable);
    }

    /**
     * Returns properties to use from lookup event to look up in index.
     * @return properties to use from lookup event
     */
    public String[] getProperties()
    {
        return properties;
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() +
                " indexProps=" + Arrays.toString(properties) +
                " keyStreamNums=" + Arrays.toString(keyStreamNums);
    }
}
