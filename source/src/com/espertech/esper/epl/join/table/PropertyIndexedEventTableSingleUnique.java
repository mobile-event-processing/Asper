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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Unique index.
 */
public class PropertyIndexedEventTableSingleUnique extends PropertyIndexedEventTableSingle
{
    private final String indexName;
    private final Map<Object, EventBean> propertyIndex;

    public PropertyIndexedEventTableSingleUnique(int streamNum, EventPropertyGetter propertyGetter, String indexName)
    {
        super(streamNum, propertyGetter);
        this.indexName = indexName;
        propertyIndex = new HashMap<Object, EventBean>();
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

    @Override
    public Set<EventBean> lookup(Object key)
    {
        EventBean event = propertyIndex.get(key);
        if (event != null) {
            return Collections.singleton(event);
        }
        return null;
    }

    private void add(EventBean theEvent)
    {
        Object key = getKey(theEvent);

        EventBean existing = propertyIndex.put(key, theEvent);
        if (existing != null && !existing.equals(theEvent)) {
            throw PropertyIndexedEventTableUnique.handleUniqueIndexViolation(indexName, key);
        }
    }

    private void remove(EventBean theEvent)
    {
        Object key = getKey(theEvent);
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

    public String toString()
    {
        return toQueryPlan();
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() +
                " streamNum=" + streamNum +
                " propertyGetter=" + propertyGetter;
    }

    private static Log log = LogFactory.getLog(PropertyIndexedEventTableSingleUnique.class);
}
