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
import com.espertech.esper.event.EventBeanUtility;
import com.espertech.esper.util.SimpleNumberCoercer;

import java.util.Set;

public class PropertyIndexedEventTableSingleCoerceAll extends PropertyIndexedEventTableSingleCoerceAdd
{
    private final Class coercionType;

    public PropertyIndexedEventTableSingleCoerceAll(int streamNum, EventPropertyGetter propertyGetter, SimpleNumberCoercer coercer, Class coercionType) {
        super(streamNum, propertyGetter, coercer, coercionType);
        this.coercionType = coercionType;
    }

    /**
     * Returns the set of events that have the same property value as the given event.
     * @return set of events with property value, or null if none found (never returns zero-sized set)
     */
    public Set<EventBean> lookup(Object key)
    {
        key = EventBeanUtility.coerce(key, coercionType);
        return propertyIndex.get(key);
    }
}
