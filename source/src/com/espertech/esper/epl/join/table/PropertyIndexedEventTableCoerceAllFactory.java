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
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.MultiKeyUntyped;
import com.espertech.esper.util.JavaClassHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;

/**
 * Index that organizes events by the event property values into hash buckets. Based on a HashMap
 * with {@link com.espertech.esper.collection.MultiKeyUntyped} keys that store the property values.
 * <p>
 * Performs coercion of the index keys before storing the keys, and coercion of the lookup keys before lookup.
 * <p>
 * Takes a list of property names as parameter. Doesn't care which event type the events have as long as the properties
 * exist. If the same event is added twice, the class throws an exception on add.
 */
public class PropertyIndexedEventTableCoerceAllFactory extends PropertyIndexedEventTableCoerceAddFactory
{
    private static Log log = LogFactory.getLog(PropertyIndexedEventTableCoerceAllFactory.class);
    private final Class[] coercionTypes;

    /**
     * Ctor.
     * @param streamNum is the stream number of the indexed stream
     * @param eventType is the event type of the indexed stream
     * @param propertyNames are the property names to get property values
     * @param coercionType are the classes to coerce indexed values to
     */
    public PropertyIndexedEventTableCoerceAllFactory(int streamNum, EventType eventType, String[] propertyNames, Class[] coercionType)
    {
        super(streamNum, eventType, propertyNames, coercionType);
        this.coercionTypes = coercionType;
    }

    public EventTable makeEventTable() {
        return new PropertyIndexedEventTableCoerceAll(streamNum, propertyGetters, coercers, coercionType);
    }
}
