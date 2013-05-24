/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.table;

import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.collection.MultiKeyUntyped;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class PropertyIndexedEventTableUnique extends PropertyIndexedEventTable
{
    private final String indexName;
    protected final Map<MultiKeyUntyped, EventBean> propertyIndex;

    public PropertyIndexedEventTableUnique(int streamNum, EventPropertyGetter[] propertyGetters, String indexName) {
        super(streamNum, propertyGetters);
        this.indexName = indexName;
        propertyIndex = new HashMap<MultiKeyUntyped, EventBean>();
    }

    /**
     * Remove then add events.
     * @param newData to add
     * @param oldData to remove
     */
    @Override
    public void addRemove(EventBean[] newData, EventBean[] oldData) {
        remove(oldData);
        add(newData);
    }

    /**
     * Add an array of events. Same event instance is not added twice. Event properties should be immutable.
     * Allow null passed instead of an empty array.
     * @param events to add
     * @throws IllegalArgumentException if the event was already existed in the index
     */
    @Override
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
    @Override
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
     * @param keys to compare against
     * @return set of events with property value, or null if none found (never returns zero-sized set)
     */
    @Override
    public Set<EventBean> lookup(Object[] keys)
    {
        MultiKeyUntyped key = new MultiKeyUntyped(keys);
        EventBean event = propertyIndex.get(key);
        if (event != null) {
            return Collections.singleton(event);
        }
        return null;
    }

    private void add(EventBean theEvent)
    {
        MultiKeyUntyped key = getMultiKey(theEvent);

        EventBean existing = propertyIndex.put(key, theEvent);
        if (existing != null && !existing.equals(theEvent)) {
            throw handleUniqueIndexViolation(indexName, key);
        }
    }

    protected static EPException handleUniqueIndexViolation(String indexName, Object key) {
        String indexNameDisplay = indexName == null ? "" : " '" + indexName + "'";
        throw new EPException("Unique index violation, index" + indexNameDisplay + " is a unique index and key '" + key + "' already exists");
    }

    private void remove(EventBean theEvent)
    {
        MultiKeyUntyped key = getMultiKey(theEvent);
        EventBean event = propertyIndex.get(key);
        if (event == null || !event.equals(theEvent)) {
            return;
        }
        propertyIndex.remove(key);
    }

    public boolean isEmpty()
    {
        return propertyIndex.isEmpty();
    }

    @Override
    public Iterator<EventBean> iterator()
    {
        return propertyIndex.values().iterator();
    }

    public void clear()
    {
        propertyIndex.clear();
    }

    public String toQueryPlan()
    {
        return this.getClass().getSimpleName() +
                " streamNum=" + streamNum +
                " propertyGetters=" + Arrays.toString(propertyGetters);
    }

    private static Log log = LogFactory.getLog(PropertyIndexedEventTableUnique.class);
}
