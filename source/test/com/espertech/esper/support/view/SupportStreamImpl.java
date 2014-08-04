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

package com.espertech.esper.support.view;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.view.EventStream;
import com.espertech.esper.view.View;
import com.espertech.esper.support.event.SupportEventTypeFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Unit test class for view testing that implements the EventStream interface to which views can be attached as child views.
 * The schema class is passed in during construction. The stream behaves much like a lenght window in that it
 * keeps a reference to the last X inserted events in the past. The update method reflects new events added
 * and events pushed out of the window. This is useful for view testing of views that use the oldData values
 * supplied in the update method.
 */
public class SupportStreamImpl implements EventStream
{
    private final EventType eventType;
    private final int depth;

    List<EventBean> events;
    List<View> childViews;

    public SupportStreamImpl(Class clazz, int depth)
    {
        this.eventType = SupportEventTypeFactory.createBeanType(clazz);
        this.depth = depth;

        this.events = new LinkedList<EventBean>();
        this.childViews = new LinkedList<View>();
    }

    /**
     * Insert a single event to the stream
     * @param theEvent
     */
    public void insert(EventBean theEvent)
    {
        events.add(theEvent);

        EventBean[] oldEvents = null;
        if (events.size() > depth)
        {
            oldEvents = new EventBean[] {events.remove(0)};
        }

        for (View child : childViews)
        {
            child.update(new EventBean[] {theEvent}, oldEvents);
        }
    }

    /**
     * Insert a bunch of events to the stream
     * @param eventArray
     */
    public void insert(EventBean[] eventArray)
    {
        for (EventBean theEvent : eventArray)
        {
            events.add(theEvent);
        }

        EventBean[] oldEvents = null;
        int expiredCount = events.size() - depth;
        if (expiredCount > 0)
        {
            oldEvents = new EventBean[expiredCount];
            for (int i = 0; i < expiredCount; i++)
            {
                oldEvents[i] = events.remove(0);
            }
        }

        for (View child : childViews)
        {
            child.update(eventArray, oldEvents);
        }
    }

    public Object get(long index)
    {
        if ((index > Integer.MAX_VALUE) || (index < Integer.MIN_VALUE))
        {
            throw new IllegalArgumentException("Index not within int range supported by this implementation");
        }
        return events.get((int)index);
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public Iterator<EventBean> iterator()
    {
        log.info(".iterator Not yet implemented");
        return null;
    }

    public View addView(View view)
    {
        childViews.add(view);
        view.setParent(this);
        return view;
    }

    public List<View> getViews()
    {
        return childViews;
    }

    public boolean removeView(View view)
    {
        boolean isRemoved = childViews.remove(view);
        view.setParent(null);
        return isRemoved;
    }

    public void removeAllViews()
    {
        childViews.clear();
    }

    public boolean hasViews()
    {
        return (childViews.size() > 0);
    }

    private static final Log log = LogFactory.getLog(SupportStreamImpl.class);
}
