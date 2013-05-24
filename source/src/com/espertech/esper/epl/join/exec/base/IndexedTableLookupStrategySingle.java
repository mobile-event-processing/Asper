/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.exec.base;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.join.rep.Cursor;
import com.espertech.esper.epl.join.table.PropertyIndexedEventTableSingle;
import com.espertech.esper.event.EventBeanUtility;

import java.util.Set;

public class IndexedTableLookupStrategySingle implements JoinExecTableLookupStrategy
{
    private final EventType eventType;
    private final String property;
    private final PropertyIndexedEventTableSingle index;
    private final EventPropertyGetter propertyGetter;

    /**
     * Ctor.
     * @param eventType - event type to expect for lookup
     * @param index - index to look up in
     */
    public IndexedTableLookupStrategySingle(EventType eventType, String property, PropertyIndexedEventTableSingle index)
    {
        this.eventType = eventType;
        this.property = property;
        if (index == null) {
            throw new IllegalArgumentException("Unexpected null index received");
        }
        this.index = index;
        propertyGetter = EventBeanUtility.getAssertPropertyGetter(eventType, property);
    }

    /**
     * Returns event type of the lookup event.
     * @return event type of the lookup event
     */
    public EventType getEventType()
    {
        return eventType;
    }

    /**
     * Returns index to look up in.
     * @return index to use
     */
    public PropertyIndexedEventTableSingle getIndex()
    {
        return index;
    }

    public Set<EventBean> lookup(EventBean theEvent, Cursor cursor, ExprEvaluatorContext exprEvaluatorContext)
    {
        Object key = getKey(theEvent);
        return index.lookup(key);
    }

    private Object getKey(EventBean theEvent)
    {
        return propertyGetter.get(theEvent);
    }

    public String toString()
    {
        return "IndexedTableLookupStrategy indexProp=" + property +
                " index=(" + index + ')';
    }
}
