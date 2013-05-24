/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.pollindex;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.PropertySortedEventTableCoercedFactory;
import com.espertech.esper.epl.join.table.PropertySortedEventTableFactory;
import com.espertech.esper.epl.join.table.UnindexedEventTableList;

import java.util.List;

/**
 * Strategy for building an index out of poll-results knowing the properties to base the index on.
 */
public class PollResultIndexingStrategySorted implements PollResultIndexingStrategy
{
    private final int streamNum;
    private final EventType eventType;
    private final String propertyName;
    private final Class coercionType;

    /**
     * Ctor.
     * @param streamNum is the stream number of the indexed stream
     * @param eventType is the event type of the indexed stream
     */
    public PollResultIndexingStrategySorted(int streamNum, EventType eventType, String propertyName, Class coercionType)
    {
        this.streamNum = streamNum;
        this.eventType = eventType;
        this.propertyName = propertyName;
        this.coercionType = coercionType;
    }

    public EventTable index(List<EventBean> pollResult, boolean isActiveCache)
    {
        if (!isActiveCache)
        {
            return new UnindexedEventTableList(pollResult);
        }
        PropertySortedEventTableFactory tableFactory;
        if (coercionType == null) {
            tableFactory = new PropertySortedEventTableFactory(streamNum, eventType, propertyName);
        }
        else {
            tableFactory = new PropertySortedEventTableCoercedFactory(streamNum, eventType, propertyName, coercionType);
        }
        EventTable table = tableFactory.makeEventTable();
        table.add(pollResult.toArray(new EventBean[pollResult.size()]));        
        return table;
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " property " + propertyName + " coercion " + coercionType;
    }
}
