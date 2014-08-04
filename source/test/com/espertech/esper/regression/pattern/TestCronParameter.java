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

package com.espertech.esper.regression.pattern;

import com.espertech.esper.client.scopetest.SupportUpdateListener;
import junit.framework.TestCase;
import com.espertech.esper.client.*;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.client.time.TimerEvent;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.event.EventBeanUtility;
import com.espertech.esper.regression.support.EventCollection;
import com.espertech.esper.regression.support.EventDescriptor;
import com.espertech.esper.regression.support.EventExpressionCase;
import com.espertech.esper.support.bean.SupportBeanConstants;
import com.espertech.esper.support.bean.SupportBean_A;
import com.espertech.esper.support.client.SupportConfigFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class TestCronParameter extends TestCase implements SupportBeanConstants
{
    private EPStatement patternStmt;
    private String expressionText;
    private static long baseTime;
    private EventCollection testData;
    private EventExpressionCase testCase;
    private SupportUpdateListener listener;
    private Calendar calendar;
    private final Log log = LogFactory.getLog(getClass());

    public void setUp()
    {
        listener = new SupportUpdateListener();
        testCase = null;
        calendar = Calendar.getInstance();
    }

    protected void tearDown() throws Exception {
        listener = null;
    }

    public void testOperator() throws Exception
    {
        // Observer for last Sunday of month, 0 = Sunday
        int lastDayOfWeek = getLastDayOfWeekInMonth(0, 2007);
        calendar.set(2007, getCurrentMonth(2007), lastDayOfWeek, 8, 00, 00);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, *, *, 0 last, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();

        // Observer for last day of current month
        calendar.set(2007, getCurrentMonth(2007), getLastDayOfMonth(2007), 8, 00, 00);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, last, *, *, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();

        // Observer for last day of Auguts 2007
        // For Java: January=0, February=1, March=2, April=3, May=4, June=5,
        //            July=6, August=7, September=8, November=9, October=10, December=11
        // For Esper: January=1, February=2, March=3, April=4, May=5, June=6,
        //            July=7, August=8, September=9, November=10, October=11, December=12
        calendar.set(2007, Calendar.AUGUST, 31, 8, 00, 00);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, last, 8, *, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();

        // Observer for last day of February 2007
        calendar.set(2007, Calendar.FEBRUARY, 28, 8, 00, 00);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, last, 2, *, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();

        // Observer for last day of week (Saturday)
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, *, *, last, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();

        // Observer for last Friday of June
        calendar.set(2007, Calendar.JUNE, 29, 8, 00, 00);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, *, 6, 5 last, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();

        // Observer for last weekday of the current month
        calendar.set(2007, getCurrentMonth(2007), getLastWeekDayOfMonth(null, 2007), 8, 00, 00);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, lastweekday, *, *, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();

        // Observer for last weekday of September 2007, it's Friday September 28th
        calendar.set(2007, Calendar.SEPTEMBER, 28, 10, 00, 00);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, lastweekday, 9, *, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();

        // Observer for last weekday of February, it's Wednesday February 28th
        calendar.set(2007, Calendar.FEBRUARY, 28, 8, 00, 00);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, lastweekday, 2, *, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();

        // Observer for nearest weekday for current month on the 10th
        calendar.set(2007, getCurrentMonth(2007), getLastWeekDayOfMonth(10, 2007), 8, 00, 00);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, 10 weekday, *, *, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();

        // Observer for nearest weekday of September 1st (Saturday), it's Monday September 3rd (no "jump" over month)
        calendar.set(2007, Calendar.SEPTEMBER, 3, 8, 00, 00);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, 1 weekday, 9, *, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();

        // Observer for nearest weekday of September 30th (Sunday), it's Friday September 28th (no "jump" over month)
        calendar.set(2007, Calendar.SEPTEMBER, 28, 8, 00, 00);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, 30 weekday, 9, *, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();

        // Observer for last Friday of current month,
        // 0=Sunday, 1=Monday, 2=Tuesday, 3=Wednesday, 4= Thursday, 5=Friday, 6=Saturday
        calendar.set(2007, getCurrentMonth(2007), getLastDayOfWeekInMonth(5, 2007), 8, 00, 00);
        printCurrentTime(calendar);
        baseTime = calendar.getTimeInMillis();
        testData = getEventSet(baseTime, 1000 * 60 * 10);
        expressionText = "timer:at(*, *, *, *, 5 last, *)";
        testCase = new EventExpressionCase(expressionText);
        testCase.add("A1");
        runTestEvent();
    }

    private void runTestEvent()
    {
        int totalEventsReceived = 0;

        Configuration config = SupportConfigFactory.getConfiguration();
        EPServiceProvider serviceProvider = EPServiceProviderManager.getDefaultProvider(config);
        serviceProvider.initialize();

        EPRuntime runtime = serviceProvider.getEPRuntime();
        // Send the start time to the runtime
        TimerEvent startTime = new CurrentTimeEvent(baseTime);
        runtime.sendEvent(startTime);
        log.debug(".runTest Start time is " + startTime);
        try
        {
            patternStmt = serviceProvider.getEPAdministrator().createPattern(expressionText);
        }
        catch (Exception ex)
        {
            log.fatal(".runTest Failed to create statement for pattern expression=" + expressionText, ex);
            TestCase.fail();
        }
        patternStmt.addListener(listener);

        // Send actual test events
        for (Map.Entry<String, Object> entry : testData.entrySet())
        {
            String eventId = entry.getKey();

            // Manipulate the time when this event was send
            if (testData.getTime(eventId) != null)
            {
                TimerEvent currentTimeEvent = new CurrentTimeEvent(testData.getTime(eventId));
                runtime.sendEvent(currentTimeEvent);
                log.debug(".runTest Sending event " + entry.getKey()
                        + " = " + entry.getValue() +
                        "  timed " + currentTimeEvent);
            }

            // Send event itself
            runtime.sendEvent(entry.getValue());

            // Check expected results for this event
            checkResults(eventId);

            // Count and clear the list of events that each listener has received
            totalEventsReceived += countListenerEvents();
        }

        // Count number of expected matches
        int totalExpected = 0;
        for (LinkedList<EventDescriptor> events : testCase.getExpectedResults().values())
        {
            totalExpected += events.size();
        }

        if (totalExpected != totalEventsReceived)
        {
            log.debug(".test Count expected does not match count received, expected=" + totalExpected +
                    " received=" + totalEventsReceived);
            TestCase.assertTrue(false);
        }

        // Kill expression
        patternStmt.removeAllListeners();

        // Send test events again to also test that all were indeed killed
        for (Map.Entry<String, Object> entry : testData.entrySet())
        {
            runtime.sendEvent(entry.getValue());
        }

        if (listener.getNewDataList().size() > 0)
        {
            log.debug(".test A match was received after stopping all expressions");
            TestCase.assertTrue(false);
        }

        // test the object model
        String epl = "select * from pattern [" + expressionText + "]";
        EPStatementObjectModel model = serviceProvider.getEPAdministrator().compileEPL(epl);
        assertEquals(epl, model.toEPL());
        EPStatement stmt = serviceProvider.getEPAdministrator().create(model);
        assertEquals(epl, stmt.getText());
    }

    private void checkResults(String eventId)
    {
        log.debug(".checkResults Checking results for event " + eventId);

        String expressionText = patternStmt.getText();

        LinkedHashMap<String, LinkedList<EventDescriptor>> allExpectedResults = testCase.getExpectedResults();
        EventBean[] receivedResults = listener.getLastNewData();

        // If nothing at all was expected for this event, make sure nothing was received
        if (!(allExpectedResults.containsKey(eventId)))
        {
            if ((receivedResults != null) && (receivedResults.length > 0))
            {
                log.debug(".checkResults Incorrect result for expression : " + expressionText);
                log.debug(".checkResults Expected no results for event " + eventId + ", but received " + receivedResults.length + " events");
                log.debug(".checkResults Received, have " + receivedResults.length + " entries");
                printList(receivedResults);
                TestCase.assertFalse(true);
            }
        }

        LinkedList<EventDescriptor> expectedResults = allExpectedResults.get(eventId);

        // Compare the result lists, not caring about the order of the elements
        if (!(compareLists(receivedResults, expectedResults)))
        {
            log.debug(".checkResults Incorrect result for expression : " + expressionText);
            log.debug(".checkResults Expected size=" + expectedResults.size() + " received size=" + (receivedResults == null ? 0 : receivedResults.length));

            log.debug(".checkResults Expected, have " + expectedResults.size() + " entries");
            printList(expectedResults);
            log.debug(".checkResults Received, have " + (receivedResults == null ? 0 : receivedResults.length) + " entries");
            printList(receivedResults);

            TestCase.assertFalse(true);
        }
    }

    private boolean compareLists(EventBean[] receivedResults,
                                 LinkedList<EventDescriptor> expectedResults)
    {
        int receivedSize = (receivedResults == null) ? 0 : receivedResults.length;
        if (expectedResults.size() != receivedSize)
        {
            return false;
        }

        // To make sure all received events have been expected
        LinkedList<EventDescriptor> expectedResultsClone = new LinkedList<EventDescriptor>(expectedResults);

        // Go through the list of expected results and remove from received result list if found
        for (EventDescriptor desc : expectedResults)
        {
            EventDescriptor foundMatch = null;

            for (EventBean received : receivedResults)
            {
                if (compareEvents(desc, received))
                {
                    foundMatch = desc;
                    break;
                }
            }

            // No match between expected and received
            if (foundMatch == null)
            {
                return false;
            }

            expectedResultsClone.remove(foundMatch);
        }

        // Any left over received results also invalidate the test
        if (expectedResultsClone.size() > 0)
        {
            return false;
        }
        return true;
    }

    private static boolean compareEvents(EventDescriptor eventDesc, EventBean eventBean)
    {
        for (Map.Entry<String, Object> entry : eventDesc.getEventProperties().entrySet())
        {
            if (!(eventBean.get(entry.getKey()) == (entry.getValue())))
            {
                return false;
            }
        }
        return true;
    }

    private void printList(LinkedList<EventDescriptor> events)
    {
        int index = 0;
        for (EventDescriptor desc : events)
        {
            StringBuffer buffer = new StringBuffer();
            int count = 0;

            for (Map.Entry<String, Object> entry : desc.getEventProperties().entrySet())
            {
                buffer.append(" (" + (count++) + ") ");
                buffer.append("tag=" + entry.getKey());

                String id = findValue(entry.getValue());
                buffer.append("  eventId=" + id);
            }

            log.debug(".printList (" + index + ") : " + buffer.toString());
            index++;
        }
    }

    private void printList(EventBean[] events)
    {
        if (events == null)
        {
            log.debug(".printList : null-value events array");
            return;
        }

        log.debug(".printList : " + events.length + " elements...");
        for (int i = 0; i < events.length; i++)
        {
            log.debug("  " + EventBeanUtility.printEvent(events[i]));
        }
    }

    private String findValue(Object value)
    {
        for (Map.Entry<String, Object> entry : testData.entrySet())
        {
            if (value == entry.getValue())
            {
                return entry.getKey();
            }
        }
        return null;
    }

    private int countListenerEvents()
    {
        int count = 0;
        for (EventBean[] events : listener.getNewDataList())
        {
            count += events.length;
        }
        listener.reset();
        return count;
    }

    private EventCollection getEventSet(long baseTime, long numMSecBetweenEvents)
    {
        LinkedHashMap<String, Object> testData = new LinkedHashMap<String, Object>();
        testData.put("A1", new SupportBean_A("A1"));
        LinkedHashMap<String, Long> times = makeExternalClockTimes(testData, baseTime, numMSecBetweenEvents);
        return new EventCollection(testData, times);
    }

    private LinkedHashMap<String, Long> makeExternalClockTimes(LinkedHashMap<String, Object> testData,
                                                               long baseTime,
                                                               long numMSecBetweenEvents)
    {
        LinkedHashMap<String, Long> testDataTimers = new LinkedHashMap<String, Long>();

        testDataTimers.put(EventCollection.ON_START_EVENT_ID, baseTime);

        for (String id : testData.keySet())
        {
            baseTime += numMSecBetweenEvents;
            testDataTimers.put(id, baseTime);
        }

        return testDataTimers;
    }

    private int getCurrentMonth(int year)
    {
        setTime(year);
        return calendar.get(Calendar.MONTH);
    }

    private int getLastDayOfMonth(int year)
    {
        setTime(year);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    private int getLastDayOfWeekInMonth(int day, int year)
    {
        if (day < 0 || day > 7)
        {
            throw new IllegalArgumentException("Last xx day of the month has to be a day of week (0-7)");
        }
        int dayOfWeek = getDayOfWeek(day, year);
        setTime(year);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        int dayDiff = calendar.get(Calendar.DAY_OF_WEEK) - dayOfWeek;
        if (dayDiff > 0)
        {
            calendar.add(Calendar.DAY_OF_WEEK, -dayDiff);
        }
        else if (dayDiff < 0)
        {
            calendar.add(Calendar.DAY_OF_WEEK, -7 - dayDiff);
        }
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    private int getLastWeekDayOfMonth(Integer day, int year)
    {
        int computeDay = (day == null) ? getLastDayOfMonth(year) : day;
        setTime(year);
        if (!checkDayValidInMonth(computeDay, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
        {
            throw new IllegalArgumentException("Invalid day for " + calendar.get(Calendar.MONTH));
        }
        calendar.set(Calendar.DAY_OF_MONTH, computeDay);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if ((dayOfWeek >= Calendar.MONDAY) && (dayOfWeek <= Calendar.FRIDAY))
        {
            return computeDay;
        }
        if (dayOfWeek == Calendar.SATURDAY)
        {
            if (computeDay == 1)
            {
                calendar.add(Calendar.DAY_OF_MONTH, +2);
            }
            else
            {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            }
        }
        if (dayOfWeek == Calendar.SUNDAY)
        {
            if ((computeDay == 28) || (computeDay == 29) || (computeDay == 30) || (computeDay == 31))
            {
                calendar.add(Calendar.DAY_OF_MONTH, -2);
            }
            else
            {
                calendar.add(Calendar.DAY_OF_MONTH, +2);
            }
        }
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    private int getDayOfWeek(int day, int year)
    {
        setTime(year);
        calendar.set(Calendar.DAY_OF_WEEK, day + 1);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    private void setTime(int year)
    {
        Date date = new Date();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
    }

    private boolean checkDayValidInMonth(int day, int month, int year)
    {
        try
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setLenient(false);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.getTime();
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
        return true;
    }

    private void printCurrentTime(Calendar cal)
    {
        Date date = cal.getTime();
        //System.out.println(new SimpleDateFormat("EEEE").format(date) + " " + new SimpleDateFormat("MMM").format(date) +
        //        " " + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.YEAR));
    }
}



