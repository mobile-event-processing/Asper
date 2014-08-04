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

import junit.framework.TestCase;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.filter.SupportFilterSpecBuilder;
import com.espertech.esper.support.filter.SupportFilterHandle;
import com.espertech.esper.support.event.SupportEventBeanFactory;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.EventBean;

import java.util.LinkedList;
import java.util.List;

public class TestIndexTreeBuilder extends TestCase
{
    List<FilterHandle> matches;
    IndexTreeBuilder builder;
    EventBean eventBean;
    EventType eventType;
    FilterHandle testFilterCallback[];

    public void setUp()
    {
        SupportBean testBean = new SupportBean();
        testBean.setIntPrimitive(50);
        testBean.setDoublePrimitive(0.5);
        testBean.setTheString("jack");
        testBean.setLongPrimitive(10);
        testBean.setShortPrimitive((short) 20);

        builder = new IndexTreeBuilder();
        eventBean = SupportEventBeanFactory.createObject(testBean);
        eventType = eventBean.getEventType();

        matches = new LinkedList<FilterHandle>();

        // Allocate a couple of callbacks
        testFilterCallback = new SupportFilterHandle[20];
        for (int i = 0; i < testFilterCallback.length; i++)
        {
            testFilterCallback[i] = new SupportFilterHandle();
        }
    }

    public void testBuildWithMatch()
    {
        FilterHandleSetNode topNode = new FilterHandleSetNode();

        // Add some parameter-less expression
        FilterValueSet filterSpec = makeFilterValues();
        builder.add(filterSpec, testFilterCallback[0], topNode);
        assertTrue(topNode.contains(testFilterCallback[0]));

        // Attempt a match
        topNode.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 1);
        matches.clear();

        // Add a filter that won't match, with a single parameter matching against an int
        filterSpec = makeFilterValues("intPrimitive", FilterOperator.EQUAL, 100);
        builder.add(filterSpec, testFilterCallback[1], topNode);
        assertTrue(topNode.getIndizes().size() == 1);
        assertTrue(topNode.getIndizes().get(0).size() == 1);

        // Match again
        topNode.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 1);
        matches.clear();

        // Add a filter that will match
        filterSpec = makeFilterValues("intPrimitive", FilterOperator.EQUAL, 50);
        builder.add(filterSpec, testFilterCallback[2], topNode);
        assertTrue(topNode.getIndizes().size() == 1);
        assertTrue(topNode.getIndizes().get(0).size() == 2);

        // match
        topNode.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 2);
        matches.clear();

        // Add some filter against a double
        filterSpec = makeFilterValues("doublePrimitive", FilterOperator.LESS, 1.1);
        builder.add(filterSpec, testFilterCallback[3], topNode);
        assertTrue(topNode.getIndizes().size() == 2);
        assertTrue(topNode.getIndizes().get(0).size() == 2);
        assertTrue(topNode.getIndizes().get(1).size() == 1);

        topNode.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 3);
        matches.clear();

        filterSpec = makeFilterValues("doublePrimitive", FilterOperator.LESS_OR_EQUAL, 0.5);
        builder.add(filterSpec, testFilterCallback[4], topNode);
        assertTrue(topNode.getIndizes().size() == 3);
        assertTrue(topNode.getIndizes().get(0).size() == 2);
        assertTrue(topNode.getIndizes().get(1).size() == 1);
        assertTrue(topNode.getIndizes().get(2).size() == 1);

        topNode.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 4);
        matches.clear();

        // Add an filterSpec against double and string
        filterSpec = makeFilterValues("doublePrimitive", FilterOperator.LESS, 1.1,
                                    "theString", FilterOperator.EQUAL, "jack");
        builder.add(filterSpec, testFilterCallback[5], topNode);
        assertTrue(topNode.getIndizes().size() == 3);
        assertTrue(topNode.getIndizes().get(0).size() == 2);
        assertTrue(topNode.getIndizes().get(1).size() == 1);
        assertTrue(topNode.getIndizes().get(2).size() == 1);
        FilterHandleSetNode nextLevelSetNode = (FilterHandleSetNode) topNode.getIndizes().get(1).get(Double.valueOf(1.1));
        assertTrue(nextLevelSetNode != null);
        assertTrue(nextLevelSetNode.getIndizes().size() == 1);

        topNode.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 5);
        matches.clear();

        filterSpec = makeFilterValues("doublePrimitive", FilterOperator.LESS, 1.1,
                                    "theString", FilterOperator.EQUAL, "beta");
        builder.add(filterSpec, testFilterCallback[6], topNode);

        topNode.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 5);
        matches.clear();

        filterSpec = makeFilterValues("doublePrimitive", FilterOperator.LESS, 1.1,
                                    "theString", FilterOperator.EQUAL, "jack");
        builder.add(filterSpec, testFilterCallback[7], topNode);
        assertTrue(nextLevelSetNode.getIndizes().size() == 1);
        FilterHandleSetNode nodeTwo = (FilterHandleSetNode) nextLevelSetNode.getIndizes().get(0).get("jack");
        assertTrue(nodeTwo.getFilterCallbackCount() == 2);

        topNode.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 6);
        matches.clear();

        // Try depth first
        filterSpec = makeFilterValues("theString", FilterOperator.EQUAL, "jack",
                                    "longPrimitive", FilterOperator.EQUAL, 10L,
                                    "shortPrimitive", FilterOperator.EQUAL, (short) 20);
        builder.add(filterSpec, testFilterCallback[8], topNode);

        topNode.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 7);
        matches.clear();

        // Add an filterSpec in the middle
        filterSpec = makeFilterValues("longPrimitive", FilterOperator.EQUAL, 10L,
                                    "theString", FilterOperator.EQUAL, "jack");
        builder.add(filterSpec, testFilterCallback[9], topNode);

        filterSpec = makeFilterValues("longPrimitive", FilterOperator.EQUAL, 10L,
                                    "theString", FilterOperator.EQUAL, "jim");
        builder.add(filterSpec, testFilterCallback[10], topNode);

        filterSpec = makeFilterValues("longPrimitive", FilterOperator.EQUAL, 10L,
                                    "theString", FilterOperator.EQUAL, "joe");
        builder.add(filterSpec, testFilterCallback[11], topNode);

        topNode.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 8);
        matches.clear();
    }

    public void testBuildMatchRemove()
    {
        FilterHandleSetNode top = new FilterHandleSetNode();

        // Add a parameter-less filter
        FilterValueSet filterSpecNoParams = makeFilterValues();
        IndexTreePath pathAddedTo = builder.add(filterSpecNoParams, testFilterCallback[0], top);

        // Try a match
        top.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 1);
        matches.clear();

        // Remove filter
        builder.remove(eventType, testFilterCallback[0], pathAddedTo, top);

        // Match should not be found
        top.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 0);
        matches.clear();

        // Add a depth-first filterSpec
        FilterValueSet filterSpecOne = makeFilterValues(
                "theString", FilterOperator.EQUAL, "jack",
                "longPrimitive", FilterOperator.EQUAL, 10L,
                "shortPrimitive", FilterOperator.EQUAL, (short) 20);
        IndexTreePath pathAddedToOne = builder.add(filterSpecOne, testFilterCallback[1], top);

        FilterValueSet filterSpecTwo = makeFilterValues(
                "theString", FilterOperator.EQUAL, "jack",
                "longPrimitive", FilterOperator.EQUAL, 10L,
                "shortPrimitive", FilterOperator.EQUAL, (short) 20);
        IndexTreePath pathAddedToTwo = builder.add(filterSpecTwo, testFilterCallback[2], top);

        FilterValueSet filterSpecThree = makeFilterValues(
                "theString", FilterOperator.EQUAL, "jack",
                "longPrimitive", FilterOperator.EQUAL, 10L);
        IndexTreePath pathAddedToThree = builder.add(filterSpecThree, testFilterCallback[3], top);

        FilterValueSet filterSpecFour = makeFilterValues(
                "theString", FilterOperator.EQUAL, "jack");
        IndexTreePath pathAddedToFour = builder.add(filterSpecFour, testFilterCallback[4], top);

        FilterValueSet filterSpecFive = makeFilterValues(
                "longPrimitive", FilterOperator.EQUAL, 10L);
        IndexTreePath pathAddedToFive = builder.add(filterSpecFive, testFilterCallback[5], top);

        top.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 5);
        matches.clear();

        // Remove some of the nodes
        builder.remove(eventType, testFilterCallback[2], pathAddedToTwo, top);

        top.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 4);
        matches.clear();

        // Remove some of the nodes
        builder.remove(eventType, testFilterCallback[4], pathAddedToFour, top);

        top.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 3);
        matches.clear();

        // Remove some of the nodes
        builder.remove(eventType, testFilterCallback[5], pathAddedToFive, top);

        top.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 2);
        matches.clear();

        // Remove some of the nodes
        builder.remove(eventType, testFilterCallback[1], pathAddedToOne, top);

        top.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 1);
        matches.clear();

        // Remove some of the nodes
        builder.remove(eventType, testFilterCallback[3], pathAddedToThree, top);

        top.matchEvent(eventBean, matches);
        assertTrue(matches.size() == 0);
        matches.clear();
    }

    private FilterValueSet makeFilterValues(Object ... filterSpecArgs)
    {
        FilterSpecCompiled spec = SupportFilterSpecBuilder.build(eventType, filterSpecArgs);
        FilterValueSet filterValues = spec.getValueSet(null, null, null);
        return filterValues;
    }
}
