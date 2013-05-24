/*
 * *************************************************************************************
 *  Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 *  http://esper.codehaus.org                                                          *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 * *************************************************************************************
 */

package com.espertech.esper.client.hook;

import com.espertech.esper.client.EventBean;

import java.util.Set;

/**
 * Represents a lookup strategy object that an EPL statement that queries a virtual data window obtains
 * to perform read operations into the virtual data window.
 * <p>
 * An instance is associated to each EPL statement querying (join, subquery, on-action etc.) the virtual data window.
 */
public interface VirtualDataWindowLookup {

    /**
     * Invoked by an EPL statement that queries a virtual data window to perform a lookup.
     * <p>
     * Keys passed are the actual query lookup values.
     * @param keys lookup values
     * @param eventsPerStream input events for the lookup
     * @return set of events
     */
    public Set<EventBean> lookup(Object[] keys, EventBean[] eventsPerStream);
}
