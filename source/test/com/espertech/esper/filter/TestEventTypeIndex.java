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

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.support.bean.ISupportA;
import com.espertech.esper.support.bean.ISupportABCImpl;
import com.espertech.esper.support.bean.ISupportAImplSuperGImplPlus;
import com.espertech.esper.support.bean.ISupportBaseAB;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.filter.SupportFilterHandle;
import com.espertech.esper.support.event.SupportEventBeanFactory;
import com.espertech.esper.support.event.SupportEventTypeFactory;

public class TestEventTypeIndex extends TestCase
{
    private EventTypeIndex testIndex;

    private EventBean testEventBean;
    private EventType testEventType;

    private FilterHandleSetNode handleSetNode;
    private FilterHandle filterCallback;

    public void setUp()
    {
        SupportBean testBean = new SupportBean();
        testEventBean = SupportEventBeanFactory.createObject(testBean);
        testEventType = testEventBean.getEventType();

        handleSetNode = new FilterHandleSetNode();
        filterCallback = new SupportFilterHandle();
        handleSetNode.add(filterCallback);

        testIndex = new EventTypeIndex();
        testIndex.add(testEventType, handleSetNode);
    }

    public void testMatch()
    {
        List<FilterHandle> matchesList = new LinkedList<FilterHandle>();

        // Invoke match
        testIndex.matchEvent(testEventBean, matchesList);

        assertEquals(1, matchesList.size());
        assertEquals(filterCallback, matchesList.get(0));
    }

    public void testInvalidSecondAdd()
    {
        try
        {
            testIndex.add(testEventType, handleSetNode);
            assertTrue(false);
        }
        catch (IllegalStateException ex)
        {
            // Expected
        }
    }

    public void testGet()
    {
        assertEquals(handleSetNode, testIndex.get(testEventType));
    }

    public void testSuperclassMatch()
    {
        testEventBean = SupportEventBeanFactory.createObject(new ISupportAImplSuperGImplPlus());
        testEventType = SupportEventTypeFactory.createBeanType(ISupportA.class);

        testIndex = new EventTypeIndex();
        testIndex.add(testEventType, handleSetNode);

        List<FilterHandle> matchesList = new LinkedList<FilterHandle>();
        testIndex.matchEvent(testEventBean, matchesList);

        assertEquals(1, matchesList.size());
        assertEquals(filterCallback, matchesList.get(0));
    }

    public void testInterfaceMatch()
    {
        testEventBean = SupportEventBeanFactory.createObject(new ISupportABCImpl("a", "b", "ab", "c"));
        testEventType = SupportEventTypeFactory.createBeanType(ISupportBaseAB.class);

        testIndex = new EventTypeIndex();
        testIndex.add(testEventType, handleSetNode);

        List<FilterHandle> matchesList = new LinkedList<FilterHandle>();
        testIndex.matchEvent(testEventBean, matchesList);

        assertEquals(1, matchesList.size());
        assertEquals(filterCallback, matchesList.get(0));
    }
}