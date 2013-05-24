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

import java.util.Iterator;

/**
 * Table of events allowing add and remove. Lookup in table is coordinated
 * through the underlying implementation.
 */
public interface EventTable extends Iterable<EventBean>
{
    /**
     * Add and remove events from table.
     * <p>
     *     It is up to the index to decide whether to add first and then remove,
     *     or whether to remove and then add.
     * </p>
     * <p>
     *     It is important to note that a given event can be in both the
     *     removed and the added events. This means that unique indexes probably need to remove first
     *     and then add. Most other non-unique indexes will add first and then remove
     *     since the an event can be both in the add and the remove stream.
     * </p>
     * @param newData to add
     * @param oldData to remove
     */
    void addRemove(EventBean[] newData, EventBean[] oldData);

    /**
     * Add events to table.
     * @param events to add
     */
    public void add(EventBean[] events);

    /**
     * Remove events from table.
     * @param events to remove
     */
    public void remove(EventBean[] events);

    /**
     * Returns an iterator over events in the table.
     * @return table iterator
     */
    public Iterator<EventBean> iterator();

    /**
     * Returns true if the index is empty, or false if not
     * @return true for empty index
     */
    public boolean isEmpty();

    /**
     * Clear out index.
     */
    public void clear();

    public String toQueryPlan();
}
