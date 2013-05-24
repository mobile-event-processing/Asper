/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern.observer;

import com.espertech.esper.core.service.EPStatementHandleCallback;
import com.espertech.esper.core.service.ExtensionServicesContext;
import com.espertech.esper.pattern.MatchedEventMap;
import com.espertech.esper.schedule.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Observer implementation for indicating that a certain time arrived, similar to "crontab".
 */
public class TimerAtObserver implements EventObserver, ScheduleHandleCallback
{
    private final ScheduleSpec scheduleSpec;
    private final ScheduleSlot scheduleSlot;
    private final MatchedEventMap beginState;
    private final ObserverEventEvaluator observerEventEvaluator;

    private boolean isTimerActive = false;
    private EPStatementHandleCallback scheduleHandle;

    /**
     * Ctor.
     * @param scheduleSpec - specification containing the crontab schedule
     * @param beginState - start state
     * @param observerEventEvaluator - receiver for events
     */
    public TimerAtObserver(ScheduleSpec scheduleSpec, MatchedEventMap beginState, ObserverEventEvaluator observerEventEvaluator)
    {
        this.scheduleSpec = scheduleSpec;
        this.beginState = beginState;
        this.observerEventEvaluator = observerEventEvaluator;
        this.scheduleSlot = observerEventEvaluator.getContext().getPatternContext().getScheduleBucket().allocateSlot();
    }

    public final void scheduledTrigger(ExtensionServicesContext extensionServicesContext)
    {
        observerEventEvaluator.observerEvaluateTrue(beginState);
        isTimerActive = false;
    }

    public void startObserve()
    {
        if (isTimerActive)
        {
            throw new IllegalStateException("Timer already active");
        }

        scheduleHandle = new EPStatementHandleCallback(observerEventEvaluator.getContext().getAgentInstanceContext().getEpStatementAgentInstanceHandle(), this);
        SchedulingService schedulingService = observerEventEvaluator.getContext().getPatternContext().getSchedulingService();
        long nextScheduledTime = ScheduleComputeHelper.computeDeltaNextOccurance(scheduleSpec, schedulingService.getTime());
        schedulingService.add(nextScheduledTime, scheduleHandle, scheduleSlot);
        isTimerActive = true;
    }

    public void stopObserve()
    {
        if (isTimerActive)
        {
            observerEventEvaluator.getContext().getPatternContext().getSchedulingService().remove(scheduleHandle, scheduleSlot);
            isTimerActive = false;
            scheduleHandle = null;
        }
    }

    private static final Log log = LogFactory.getLog(TimerAtObserver.class);
}
