/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.table;

import com.espertech.esper.client.EventType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertySortedEventTableCoercedFactory extends PropertySortedEventTableFactory
{
    private Class coercionType;

   /**
     * Ctor.
     * @param streamNum - the stream number that is indexed
     * @param eventType - types of events indexed
     * @param propertyName - property names to use for indexing
     * @param coercionType - property types
     */
    public PropertySortedEventTableCoercedFactory(int streamNum, EventType eventType, String propertyName, Class coercionType)
    {
        super(streamNum, eventType, propertyName);
        this.coercionType = coercionType;
    }

    @Override
    public EventTable makeEventTable() {
        return new PropertySortedEventTableCoerced(streamNum, propertyGetter, coercionType);
    }

    public String toString()
    {
        return "PropertySortedEventTableCoerced" +
                " streamNum=" + streamNum +
                " propertyName=" + propertyName +
                " coercionType=" + coercionType;
    }

    private static Log log = LogFactory.getLog(PropertySortedEventTableCoercedFactory.class);
}
