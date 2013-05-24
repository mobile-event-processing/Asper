/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.view;

import com.espertech.esper.util.ExecutionPathDebugLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Output limit condition that is satisfied when either
 * the total number of new events arrived or the total number
 * of old events arrived is greater than a preset value.
 */
public final class OutputConditionCount extends OutputConditionBase implements OutputCondition
{
    private static final boolean DO_OUTPUT = true;
	private static final boolean FORCE_UPDATE = false;

    private final OutputConditionCountFactory parent;

    private long eventRate;
    private int newEventsCount;
    private int oldEventsCount;

    public OutputConditionCount(OutputCallback outputCallback, OutputConditionCountFactory parent) {
        super(outputCallback);
        this.parent = parent;
        this.eventRate = parent.getEventRate();
    }

    /**
     * Returns the number of new events.
     * @return number of new events
     */
    public int getNewEventsCount() {
		return newEventsCount;
	}

    /**
     * Returns the number of old events.
     * @return number of old events
     */
	public int getOldEventsCount() {
		return oldEventsCount;
	}

    public final void updateOutputCondition(int newDataCount, int oldDataCount)
    {
        if (parent.getVariableReader() != null)
        {
            Object value = parent.getVariableReader().getValue();
            if (value != null)
            {
                eventRate = ((Number) value).longValue();
            }
        }

        this.newEventsCount += newDataCount;
        this.oldEventsCount += oldDataCount;

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".updateBatchCondition, " +
                    "  newEventsCount==" + newEventsCount +
                    "  oldEventsCount==" + oldEventsCount);
        }

        if (isSatisfied())
        {
        	if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
            {
                log.debug(".updateOutputCondition() condition satisfied");
            }
            this.newEventsCount = 0;
            this.oldEventsCount = 0;
            outputCallback.continueOutputProcessing(DO_OUTPUT, FORCE_UPDATE);
        }
    }

    public final String toString()
    {
        return this.getClass().getName() +
                " eventRate=" + eventRate;
    }

    private boolean isSatisfied()
    {
    	return (newEventsCount >= eventRate) || (oldEventsCount >= eventRate);
    }

    public void terminated() {
        outputCallback.continueOutputProcessing(true, true);
    }

    private static final Log log = LogFactory.getLog(OutputConditionCount.class);
}
