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

package com.espertech.esper.core.context.mgr;

import com.espertech.esper.client.EventBean;

import java.util.Map;

public class ContextControllerInitTermState {

    private final long startTime;
    private final Map<String, Object> patternData;
    private final EventBean filterEvent;

    public ContextControllerInitTermState(long startTime, Map<String, Object> patternData, EventBean filterEvent) {
        this.startTime = startTime;
        this.patternData = patternData;
        this.filterEvent = filterEvent;
    }

    public long getStartTime() {
        return startTime;
    }

    public Map<String, Object> getPatternData() {
        return patternData;
    }

    public EventBean getFilterEvent() {
        return filterEvent;
    }
}
