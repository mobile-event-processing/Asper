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

package com.espertech.esper.view;

import com.espertech.esper.client.EventType;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.collection.ArrayEventIterator;

import java.util.List;
import java.util.Iterator;

public class ViewableDefaultImpl implements Viewable
{
    private final EventType eventType;

    public ViewableDefaultImpl(EventType eventType)
    {
        this.eventType = eventType;
    }

    public View addView(View view)
    {
        return null;
    }

    public List<View> getViews()
    {
        return null;
    }

    public boolean removeView(View view)
    {
        return false;
    }

    public void removeAllViews()
    {
    }

    public boolean hasViews()
    {
        return false;
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public Iterator<EventBean> iterator()
    {
        return new ArrayEventIterator(null);
    }
}
