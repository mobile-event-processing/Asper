/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.table;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Index that organizes events by the event property values into hash buckets. Based on a HashMap
 * with {@link com.espertech.esper.collection.MultiKeyUntyped} keys that store the property values.
 */
public class PropertyIndexedEventTableSingle implements EventTable
{
    protected final int streamNum;
    protected final EventPropertyGetter propertyGetter;
    protected final Map<Object, Set<EventBean>> propertyIndex;

    public PropertyIndexedEventTableSingle(int streamNum, EventPropertyGetter propertyGetter)
    {
        this.streamNum = streamNum;
        this.propertyGetter = propertyGetter;
        propertyIndex = new HashMap<Object, Set<EventBean>>();
    }

    /**
     * Determine multikey for index access.
     * @param theEvent to get properties from for key
     * @return multi key
     */
    protected Object getKey(EventBean theEvent)
    {
        return propertyGetter.get(theEvent);
    }

    public void addRemove(EventBean[] newData, EventBean[] oldData) {
        add(newData);
        remove(oldData);
    }

    /**
     * Add an array of events. Same event instance is not added twice. Event properties should be immutable.
     * Allow null passed instead of an empty array.
     * @param events to add
     * @throws IllegalArgumentException if the event was already existed in the index
     */
    public void add(EventBean[] events)
    {
        if (events == null)
        {
            return;
        }
        for (EventBean theEvent : events)
        {
            add(theEvent);
        }
    }

    /**
     * Remove events.
     * @param events to be removed, can be null instead of an empty array.
     * @throws IllegalArgumentException when the event could not be removed as its not in the index
     */
    public void remove(EventBean[] events)
    {
        if (events == null)
        {
            return;
        }
        for (EventBean theEvent : events)
        {
            remove(theEvent);
        }
    }

    /**
     * Returns the set of events that have the same property value as the given event.
     * @param key to compare against
     * @return set of events with property value, or null if none found (never returns zero-sized set)
     */
    public Set<EventBean> lookup(Object key)
    {
        return propertyIndex.get(key);
    }

    private void add(EventBean theEvent)
    {
        Object key = getKey(theEvent);

        Set<EventBean> events = propertyIndex.get(key);
        if (events == null)
        {
            events = new LinkedHashSet<EventBean>();
            propertyIndex.put(key, events);
        }

        events.add(theEvent);
    }

    private void remove(EventBean theEvent)
    {
        Object key = getKey(theEvent);

        Set<EventBean> events = propertyIndex.get(key);
        if (events == null)
        {
            return;
        }

        if (!events.remove(theEvent))
        {
            // Not an error, its possible that an old-data event is artificial (such as for statistics) and
            // thus did not correspond to a new-data event raised earlier.
            return;
        }

        if (events.isEmpty())
        {
            propertyIndex.remove(key);
        }
    }

    public boolean isEmpty()
    {
        return propertyIndex.isEmpty();
    }

    public Iterator<EventBean> iterator()
    {
        return new PropertyIndexedEventTableIterator<Object>(propertyIndex);
    }

    public void clear()
    {
        propertyIndex.clear();
    }

    public String toString()
    {
        return toQueryPlan();
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() +
                " streamNum=" + streamNum +
                " propertyGetter=" + propertyGetter;
    }

    private static Log log = LogFactory.getLog(PropertyIndexedEventTableSingle.class);
}
