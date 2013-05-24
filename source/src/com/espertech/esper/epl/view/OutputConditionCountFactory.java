/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.view;

import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.variable.VariableReader;

public final class OutputConditionCountFactory implements OutputConditionFactory
{
    private final long eventRate;
    private final VariableReader variableReader;

    /**
     * Constructor.
     * @param eventRate is the number of old or new events that
     * must arrive in order for the condition to be satisfied
     * @param variableReader is for reading the variable value, if a variable was supplied, else null
     */
    public OutputConditionCountFactory(int eventRate, VariableReader variableReader)
    {
        if ((eventRate < 1) && (variableReader == null))
        {
            throw new IllegalArgumentException("Limiting output by event count requires an event count of at least 1 or a variable name");
        }
        this.eventRate = eventRate;
        this.variableReader = variableReader;
    }

    public OutputCondition make(AgentInstanceContext agentInstanceContext, OutputCallback outputCallback) {
        return new OutputConditionCount(outputCallback, this);
    }

    public long getEventRate() {
        return eventRate;
    }

    public VariableReader getVariableReader() {
        return variableReader;
    }
}
