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

public class PropertyIndexedEventTableSingleCoerceAdd extends PropertyIndexedEventTableSingle
{
    private final SimpleNumberCoercer coercer;
    private final Class coercionType;

    public PropertyIndexedEventTableSingleCoerceAdd(int streamNum, EventPropertyGetter propertyGetter, SimpleNumberCoercer coercer, Class coercionType) {
        super(streamNum, propertyGetter);
        this.coercer = coercer;
        this.coercionType = coercionType;
    }

    protected Object getKey(EventBean theEvent)
    {
        Object keyValue = super.getKey(theEvent);
        if ((keyValue != null) && (!keyValue.getClass().equals(coercionType)))
        {
            if (keyValue instanceof Number)
            {
                keyValue = coercer.coerceBoxed((Number) keyValue);
            }
            else {
                keyValue = EventBeanUtility.coerce(keyValue, coercionType);
            }
        }
        return keyValue;
    }
}
