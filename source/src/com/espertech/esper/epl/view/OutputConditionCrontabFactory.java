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
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.schedule.ScheduleSpec;

import java.util.List;

/**
 * Output condition handling crontab-at schedule output.
 */
public final class OutputConditionCrontabFactory implements OutputConditionFactory
{
    private final ScheduleSpec scheduleSpec;

    public OutputConditionCrontabFactory(List<ExprNode> scheduleSpecExpressionList,
                                         StatementContext statementContext)
            throws ExprValidationException
    {
        scheduleSpec = ExprNodeUtility.toCrontabSchedule(scheduleSpecExpressionList, statementContext);
    }

    public OutputCondition make(AgentInstanceContext agentInstanceContext, OutputCallback outputCallback) {
        return new OutputConditionCrontab(outputCallback, agentInstanceContext, this);
    }

    public ScheduleSpec getScheduleSpec() {
        return scheduleSpec;
    }
}
