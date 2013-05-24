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

package com.espertech.esper.schedule;

import java.util.HashSet;
import java.util.Set;

/**
 * Service for holding expiration dates to adjust.
 */
public class ScheduleAdjustmentService
{
    private Set<ScheduleAdjustmentCallback> callbacks = new HashSet<ScheduleAdjustmentCallback>();

    /**
     * Add a callback
     * @param callback to add
     */
    public void addCallback(ScheduleAdjustmentCallback callback)
    {
        callbacks.add(callback);
    }

    /**
     * Make callbacks to adjust expiration dates.
     * @param delta to adjust for
     */
    public void adjust(long delta)
    {
        for (ScheduleAdjustmentCallback callback : callbacks)
        {
            callback.adjust(delta);
        }
    }

    public void removeCallback(ScheduleAdjustmentCallback callback) {
        callbacks.remove(callback);
    }
}
