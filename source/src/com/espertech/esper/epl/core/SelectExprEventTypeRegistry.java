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

package com.espertech.esper.epl.core;

import com.espertech.esper.client.EventType;
import com.espertech.esper.event.EventTypeSPI;

import java.util.HashSet;

/**
 * Registry for event types creates as part of the select expression analysis.
 */
public class SelectExprEventTypeRegistry
{
    private HashSet<String> registry;

    /**
     * Ctor.
     * @param registry the holder of the registry
     */
    public SelectExprEventTypeRegistry(HashSet<String> registry)
    {
        this.registry = registry;
    }

    /**
     * Adds an event type.
     * @param eventType to add
     */
    public void add(EventType eventType)
    {
        if (!(eventType instanceof EventTypeSPI))
        {
            return;
        }
        registry.add(((EventTypeSPI) eventType).getMetadata().getPrimaryName());
    }
}
