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

package com.espertech.esper.filter;

import com.espertech.esper.client.EventType;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.bean.SupportBeanSimple;
import com.espertech.esper.support.filter.SupportFilterHandle;
import com.espertech.esper.support.filter.SupportFilterSpecBuilder;
import com.espertech.esper.support.event.SupportEventTypeFactory;

import junit.framework.TestCase;

public class TestEventTypeIndexBuilder extends TestCase
{
    private EventTypeIndex eventTypeIndex;
    private EventTypeIndexBuilder indexBuilder;

    private EventType typeOne;
    private EventType typeTwo;

    private FilterValueSet valueSetOne;
    private FilterValueSet valueSetTwo;

    private FilterHandle callbackOne;
    private FilterHandle callbackTwo;

    public void setUp()
    {
        eventTypeIndex = new EventTypeIndex();
        indexBuilder = new EventTypeIndexBuilder(eventTypeIndex);

        typeOne = SupportEventTypeFactory.createBeanType(SupportBean.class);
        typeTwo = SupportEventTypeFactory.createBeanType(SupportBeanSimple.class);

        valueSetOne = SupportFilterSpecBuilder.build(typeOne, new Object[0]).getValueSet(null, null, null);
        valueSetTwo = SupportFilterSpecBuilder.build(typeTwo, new Object[0]).getValueSet(null, null, null);

        callbackOne = new SupportFilterHandle();
        callbackTwo = new SupportFilterHandle();
    }

    public void testAddRemove()
    {
        assertNull(eventTypeIndex.get(typeOne));
        assertNull(eventTypeIndex.get(typeTwo));

        indexBuilder.add(valueSetOne, callbackOne);
        indexBuilder.add(valueSetTwo, callbackTwo);

        assertTrue(eventTypeIndex.get(typeOne) != null);
        assertTrue(eventTypeIndex.get(typeTwo) != null);

        try
        {
            indexBuilder.add(valueSetOne, callbackOne);
            assertTrue(false);
        }
        catch (IllegalStateException ex)
        {
            // Expected exception
        }

        indexBuilder.remove(callbackOne);
        indexBuilder.add(valueSetOne, callbackOne);
        indexBuilder.remove(callbackOne);
    }
}
