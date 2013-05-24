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
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.ExprTimePeriod;

/**
 * Output condition that is satisfied at the end
 * of every time interval of a given length.
 */
public final class OutputConditionTimeFactory implements OutputConditionFactory
{
    private final ExprTimePeriod timePeriod;
    private final long msecIntervalSize;

    /**
     * Constructor.
     * @param timePeriod is the number of minutes or seconds to batch events for, may include variables
     */
    public OutputConditionTimeFactory(ExprTimePeriod timePeriod,
                                      StatementContext statementContext)
    {
        this.timePeriod = timePeriod;

        Double numSeconds = (Double) timePeriod.evaluate(null, true, new ExprEvaluatorContextStatement(statementContext));
        if (numSeconds == null)
        {
            throw new IllegalArgumentException("Output condition by time returned a null value for the interval size");
        }
        if ((numSeconds < 0.001) && (!timePeriod.hasVariable()))
        {
            throw new IllegalArgumentException("Output condition by time requires a interval size of at least 1 msec or a variable");
        }
        this.msecIntervalSize = Math.round(1000 * numSeconds);
    }

    public OutputCondition make(AgentInstanceContext agentInstanceContext, OutputCallback outputCallback) {
        return new OutputConditionTime(outputCallback, agentInstanceContext, this);
    }

    public ExprTimePeriod getTimePeriod() {
        return timePeriod;
    }

    public long getMsecIntervalSize() {
        return msecIntervalSize;
    }
}
