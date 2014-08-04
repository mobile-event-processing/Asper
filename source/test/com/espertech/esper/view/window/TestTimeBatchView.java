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

package com.espertech.esper.view.window;

import com.espertech.esper.client.scopetest.EPAssertionUtil;
import junit.framework.TestCase;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.support.bean.SupportMarketDataBean;
import com.espertech.esper.support.event.EventFactoryHelper;
import com.espertech.esper.support.schedule.SupportSchedulingServiceImpl;
import com.espertech.esper.support.view.SupportBeanClassView;
import com.espertech.esper.support.view.SupportStatementContextFactory;
import com.espertech.esper.support.view.SupportViewDataChecker;

import java.util.Map;

public class TestTimeBatchView extends TestCase
{
    private final static long TEST_INTERVAL_MSEC = 10000;

    private TimeBatchView myView;
    private SupportBeanClassView childView;
    private SupportSchedulingServiceImpl schedulingServiceStub;

    public void setUp()
    {
        // Set the scheduling service to use
        schedulingServiceStub = new SupportSchedulingServiceImpl();

        // Set up length window view and a test child view
        myView = new TimeBatchView(null, SupportStatementContextFactory.makeAgentInstanceViewFactoryContext(schedulingServiceStub), TEST_INTERVAL_MSEC, null, false, false, null);
        childView = new SupportBeanClassView(SupportMarketDataBean.class);
        myView.addView(childView);
    }

    public void testViewPushNoRefPoint()
    {
        long startTime = 1000000;
        schedulingServiceStub.setTime(startTime);

        assertTrue(schedulingServiceStub.getAdded().size() == 0);
        EPAssertionUtil.assertEqualsExactOrder(null, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, null);
        SupportViewDataChecker.checkNewData(childView, null);

        Map<String, EventBean> events = EventFactoryHelper.makeEventMap(
            new String[] {"a1", "b1", "b2", "c1", "d1"});

        // Send new events to the view - should have scheduled a callback for X msec after
        myView.update(new EventBean[] {events.get("a1")}, null);
        assertTrue(schedulingServiceStub.getAdded().size() == 1);
        assertTrue(schedulingServiceStub.getAdded().get(TEST_INTERVAL_MSEC) != null);
        schedulingServiceStub.getAdded().clear();
        EPAssertionUtil.assertEqualsExactOrder(new EventBean[]{events.get("a1")}, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, null);
        SupportViewDataChecker.checkNewData(childView, null);  // Data got batched, no data release till later

        schedulingServiceStub.setTime(startTime + 5000);
        myView.update(new EventBean[] {events.get("b1"), events.get("b2")}, null);
        EPAssertionUtil.assertEqualsExactOrder(new EventBean[]{events.get("a1"), events.get("b1"), events.get("b2")}, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, null);
        SupportViewDataChecker.checkNewData(childView, null);
        assertTrue(schedulingServiceStub.getAdded().size() == 0);

        // Pretend we have a callback, check data, check scheduled new callback
        schedulingServiceStub.setTime(startTime + TEST_INTERVAL_MSEC);
        myView.sendBatch();
        EPAssertionUtil.assertEqualsExactOrder(null, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, null);
        SupportViewDataChecker.checkNewData(childView, new EventBean[] {events.get("a1"), events.get("b1"), events.get("b2")});
        assertTrue(schedulingServiceStub.getAdded().size() == 1);
        assertTrue(schedulingServiceStub.getAdded().get(TEST_INTERVAL_MSEC) != null);
        schedulingServiceStub.getAdded().clear();

        // Pretend callback received again, should schedule a callback since the last interval showed data
        schedulingServiceStub.setTime(startTime + TEST_INTERVAL_MSEC * 2);
        myView.sendBatch();
        EPAssertionUtil.assertEqualsExactOrder(null, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, new EventBean[]{events.get("a1"), events.get("b1"), events.get("b2")});   // Old data is published
        SupportViewDataChecker.checkNewData(childView, null);
        assertTrue(schedulingServiceStub.getAdded().size() == 1);
        assertTrue(schedulingServiceStub.getAdded().get(TEST_INTERVAL_MSEC) != null);
        schedulingServiceStub.getAdded().clear();

        // Pretend callback received again, not schedule a callback since the this and last interval showed no data
        schedulingServiceStub.setTime(startTime + TEST_INTERVAL_MSEC * 3);
        myView.sendBatch();
        EPAssertionUtil.assertEqualsExactOrder(null, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, null);
        SupportViewDataChecker.checkNewData(childView, null);
        assertTrue(schedulingServiceStub.getAdded().size() == 0);

        // Send new event to the view - pretend we are 500 msec into the interval
        schedulingServiceStub.setTime(startTime + TEST_INTERVAL_MSEC * 3 + 500);
        myView.update(new EventBean[]{ events.get("c1")}, null);
        assertTrue(schedulingServiceStub.getAdded().size() == 1);
        assertTrue(schedulingServiceStub.getAdded().get(TEST_INTERVAL_MSEC - 500) != null);
        schedulingServiceStub.getAdded().clear();
        EPAssertionUtil.assertEqualsExactOrder(new EventBean[]{events.get("c1")}, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, null);
        SupportViewDataChecker.checkNewData(childView, null);  // Data got batched, no data release till later

        // Pretend callback received again
        schedulingServiceStub.setTime(startTime + TEST_INTERVAL_MSEC * 4);
        myView.sendBatch();
        EPAssertionUtil.assertEqualsExactOrder(null, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, null);
        SupportViewDataChecker.checkNewData(childView, new EventBean[]{events.get("c1")});
        assertTrue(schedulingServiceStub.getAdded().size() == 1);
        assertTrue(schedulingServiceStub.getAdded().get(TEST_INTERVAL_MSEC) != null);
        schedulingServiceStub.getAdded().clear();

        // Send new event to the view
        schedulingServiceStub.setTime(startTime + TEST_INTERVAL_MSEC * 4 + 500);
        myView.update(new EventBean[]{ events.get("d1") }, null);
        assertTrue(schedulingServiceStub.getAdded().size() == 0);
        EPAssertionUtil.assertEqualsExactOrder(new EventBean[]{events.get("d1")}, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, null);
        SupportViewDataChecker.checkNewData(childView, null);

        // Pretend callback again
        schedulingServiceStub.setTime(startTime + TEST_INTERVAL_MSEC * 5);
        myView.sendBatch();
        EPAssertionUtil.assertEqualsExactOrder(null, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, new EventBean[]{events.get("c1")});
        SupportViewDataChecker.checkNewData(childView, new EventBean[]{events.get("d1")});
        assertTrue(schedulingServiceStub.getAdded().size() == 1);
        assertTrue(schedulingServiceStub.getAdded().get(TEST_INTERVAL_MSEC) != null);
        schedulingServiceStub.getAdded().clear();

        // Pretend callback again
        schedulingServiceStub.setTime(startTime + TEST_INTERVAL_MSEC * 6);
        myView.sendBatch();
        EPAssertionUtil.assertEqualsExactOrder(null, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, new EventBean[]{events.get("d1")});
        SupportViewDataChecker.checkNewData(childView, null);

        // Pretend callback again
        schedulingServiceStub.setTime(startTime + TEST_INTERVAL_MSEC * 7);
        myView.sendBatch();
        EPAssertionUtil.assertEqualsExactOrder(null, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, null);
        SupportViewDataChecker.checkNewData(childView, null);
    }

    public void testViewPushWithRefPoint()
    {
        long startTime = 50000;
        schedulingServiceStub.setTime(startTime);

        myView = new TimeBatchView(null, SupportStatementContextFactory.makeAgentInstanceViewFactoryContext(schedulingServiceStub), TEST_INTERVAL_MSEC, 1505L, false, false, null);
        childView = new SupportBeanClassView(SupportMarketDataBean.class);
        myView.addView(childView);

        Map<String, EventBean> events = EventFactoryHelper.makeEventMap(
            new String[] {"A1", "A2", "A3"});

        // Send new events to the view - should have scheduled a callback for X msec after
        myView.update(new EventBean[]{ events.get("A1"), events.get("A2"), events.get("A3")}, null);
        assertTrue(schedulingServiceStub.getAdded().size() == 1);
        assertTrue(schedulingServiceStub.getAdded().get(1505L) != null);
        schedulingServiceStub.getAdded().clear();
        EPAssertionUtil.assertEqualsExactOrder(new EventBean[]{events.get("A1"), events.get("A2"), events.get("A3")}, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, null);
        SupportViewDataChecker.checkNewData(childView, null);  // Data got batched, no data release till later

        // Pretend we have a callback, check data, check scheduled new callback
        schedulingServiceStub.setTime(startTime + 1505);
        myView.sendBatch();
        EPAssertionUtil.assertEqualsExactOrder(null, myView.iterator());
        SupportViewDataChecker.checkOldData(childView, null);
        SupportViewDataChecker.checkNewData(childView, new EventBean[]{events.get("A1"), events.get("A2"), events.get("A3")});
        assertTrue(schedulingServiceStub.getAdded().size() == 1);
        assertTrue(schedulingServiceStub.getAdded().get(TEST_INTERVAL_MSEC) != null);
    }

    public void testComputeWaitMSec()
    {
        // With current=2300, ref=1000, and interval=500, expect 2500 as next interval and 200 as solution
        long result = TimeBatchView.computeWaitMSec(2300, 1000, 500);
        assertEquals(200, result);

        result = TimeBatchView.computeWaitMSec(2300, 4200, 500);
        assertEquals(400, result);

        result = TimeBatchView.computeWaitMSec(2200, 4200, 500);
        assertEquals(500, result);

        result = TimeBatchView.computeWaitMSec(2200, 2200, 500);
        assertEquals(500, result);

        result = TimeBatchView.computeWaitMSec(2201, 2200, 500);
        assertEquals(499, result);

        result = TimeBatchView.computeWaitMSec(2600, 2200, 500);
        assertEquals(100, result);

        result = TimeBatchView.computeWaitMSec(2699, 2200, 500);
        assertEquals(1, result);

        result = TimeBatchView.computeWaitMSec(2699, 2700, 500);
        assertEquals(1, result);

        result = TimeBatchView.computeWaitMSec(2699, 2700, 10000);
        assertEquals(1, result);

        result = TimeBatchView.computeWaitMSec(2700, 2700, 10000);
        assertEquals(10000, result);

        result = TimeBatchView.computeWaitMSec(2700, 6800, 10000);
        assertEquals(4100, result);

        result = TimeBatchView.computeWaitMSec(23050, 16800, 10000);
        assertEquals(3750, result);
    }
}
