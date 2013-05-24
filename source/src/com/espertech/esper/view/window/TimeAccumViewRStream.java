/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.window;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.core.service.EPStatementHandleCallback;
import com.espertech.esper.core.service.ExtensionServicesContext;
import com.espertech.esper.schedule.ScheduleHandleCallback;
import com.espertech.esper.schedule.ScheduleSlot;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * A data window view that holds events in a stream and only removes events from a stream (rstream) if
 * no more events arrive for a given time interval, also handling the remove stream
 * by keeping set-like semantics. See {@link TimeAccumView} for the same behavior without
 * remove stream handling.
 */
public class TimeAccumViewRStream extends ViewSupport implements CloneableView, DataWindowView, StoppableView, StopCallback
{
    // View parameters
    private final TimeAccumViewFactory factory;
    protected final AgentInstanceViewFactoryChainContext agentInstanceContext;
    protected final long msecIntervalSize;
    protected final ScheduleSlot scheduleSlot;

    // Current running parameters
    protected LinkedHashMap<EventBean, Long> currentBatch = new LinkedHashMap<EventBean, Long>();
    protected EventBean lastEvent;
    protected long callbackScheduledTime;
    protected EPStatementHandleCallback handle;

    /**
     * Constructor.
     * @param msecIntervalSize is the number of milliseconds to batch events for
     * @param timeBatchViewFactory fr copying this view in a group-by
     */
    public TimeAccumViewRStream(TimeAccumViewFactory timeBatchViewFactory,
                         AgentInstanceViewFactoryChainContext agentInstanceContext,
                         long msecIntervalSize)
    {
        this.agentInstanceContext = agentInstanceContext;
        this.factory = timeBatchViewFactory;
        this.msecIntervalSize = msecIntervalSize;

        this.scheduleSlot = agentInstanceContext.getStatementContext().getScheduleBucket().allocateSlot();

        ScheduleHandleCallback callback = new ScheduleHandleCallback() {
            public void scheduledTrigger(ExtensionServicesContext extensionServicesContext)
            {
                TimeAccumViewRStream.this.sendRemoveStream();
            }
        };
        handle = new EPStatementHandleCallback(agentInstanceContext.getEpStatementAgentInstanceHandle(), callback);
        agentInstanceContext.getTerminationCallbacks().add(this);
    }

    public View cloneView()
    {
        return factory.makeView(agentInstanceContext);
    }

    /**
     * Returns the interval size in milliseconds.
     * @return batch size
     */
    public final long getMsecIntervalSize()
    {
        return msecIntervalSize;
    }

    public final EventType getEventType()
    {
        return parent.getEventType();
    }

    public void update(EventBean[] newData, EventBean[] oldData)
    {
        if ((newData != null) && (newData.length > 0))
        {
            // If we have an empty window about to be filled for the first time, add a callback
            boolean removeSchedule = false;
            boolean addSchedule = false;
            long timestamp = agentInstanceContext.getStatementContext().getSchedulingService().getTime();

            // if the window is already filled, then we may need to reschedule
            if (!currentBatch.isEmpty())
            {
                // check if we need to reschedule
                long callbackTime = timestamp + msecIntervalSize;
                if (callbackTime != callbackScheduledTime)
                {
                    removeSchedule = true;
                    addSchedule = true;
                }
            }
            else
            {
                addSchedule = true;
            }

            if (removeSchedule)
            {
                agentInstanceContext.getStatementContext().getSchedulingService().remove(handle, scheduleSlot);
                callbackScheduledTime = -1;
            }
            if (addSchedule)
            {
                agentInstanceContext.getStatementContext().getSchedulingService().add(msecIntervalSize, handle, scheduleSlot);
                callbackScheduledTime = msecIntervalSize + timestamp;
            }

            // add data points to the window
            for (int i = 0; i < newData.length; i++)
            {
                currentBatch.put(newData[i], timestamp);
                internalHandleAdded(newData[i], timestamp);
                lastEvent = newData[i];
            }
        }

        if ((oldData != null) && (oldData.length > 0))
        {
            boolean removedLastEvent = false;
            for (EventBean anOldData : oldData)
            {
                currentBatch.remove(anOldData);
                internalHandleRemoved(anOldData);
                if (anOldData == lastEvent)
                {
                    removedLastEvent = true;
                }
            }

            // we may need to reschedule as the newest event may have been deleted
            if (currentBatch.size() == 0)
            {
                agentInstanceContext.getStatementContext().getSchedulingService().remove(handle, scheduleSlot);
                callbackScheduledTime = -1;
                lastEvent = null;
            }
            else
            {
                // reschedule if the last event was removed
                if (removedLastEvent)
                {
                    Set<EventBean> keyset = currentBatch.keySet();
                    EventBean[] events = keyset.toArray(new EventBean[keyset.size()]);
                    lastEvent = events[events.length - 1];
                    long lastTimestamp = currentBatch.get(lastEvent);

                    // reschedule, newest event deleted
                    long timestamp = agentInstanceContext.getStatementContext().getSchedulingService().getTime();
                    long callbackTime = lastTimestamp + msecIntervalSize;
                    long deltaFromNow = callbackTime - timestamp;
                    if (callbackTime != callbackScheduledTime)
                    {
                        agentInstanceContext.getStatementContext().getSchedulingService().remove(handle, scheduleSlot);
                        agentInstanceContext.getStatementContext().getSchedulingService().add(deltaFromNow, handle, scheduleSlot);
                        callbackScheduledTime = callbackTime;
                    }
                }
            }
        }

        // update child views
        if (this.hasViews())
        {
            updateChildren(newData, oldData);
        }
    }

    /**
     * This method sends the remove stream for all accumulated events.
     */
    protected void sendRemoveStream()
    {
        callbackScheduledTime = -1;

        // If there are child views and the batch was filled, fireStatementStopped update method
        if (this.hasViews())
        {
            // Convert to object arrays
            EventBean[] oldData = null;
            if (!currentBatch.isEmpty())
            {
                oldData = currentBatch.keySet().toArray(new EventBean[currentBatch.size()]);
            }

            if (oldData != null)
            {
                updateChildren(null, oldData);
            }
        }

        currentBatch.clear();
    }

    /**
     * Returns true if the window is empty, or false if not empty.
     * @return true if empty
     */
    public boolean isEmpty()
    {
        return currentBatch.isEmpty();
    }

    public final Iterator<EventBean> iterator()
    {
        return currentBatch.keySet().iterator();
    }

    public final String toString()
    {
        return this.getClass().getName() + " msecIntervalSize=" + msecIntervalSize;
    }

    public void stopView() {
        stopSchedule();
        agentInstanceContext.getTerminationCallbacks().remove(this);
    }

    public void stop() {
        stopSchedule();
    }

    public void stopSchedule() {
        if (handle != null) {
            agentInstanceContext.getStatementContext().getSchedulingService().remove(handle, scheduleSlot);
        }
    }

    public void internalHandleRemoved(EventBean anOldData) {
        // no action required
    }

    public void internalHandleAdded(EventBean eventBean, long timestamp) {
        // no action required
    }

    private static final Log log = LogFactory.getLog(TimeAccumViewRStream.class);
}
