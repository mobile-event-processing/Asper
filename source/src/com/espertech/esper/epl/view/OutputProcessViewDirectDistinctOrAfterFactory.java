/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.view;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.expression.ExprTimePeriod;
import com.espertech.esper.event.EventBeanReader;
import com.espertech.esper.event.EventBeanReaderDefaultImpl;
import com.espertech.esper.event.EventTypeSPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Output process view that does not enforce any output policies and may simply
 * hand over events to child views, but works with distinct and after-output policies
 */
public class OutputProcessViewDirectDistinctOrAfterFactory extends OutputProcessViewDirectFactory
{
	private static final Log log = LogFactory.getLog(OutputProcessViewDirectDistinctOrAfterFactory.class);

    private final boolean isDistinct;
    protected final ExprTimePeriod afterTimePeriod;
    protected final Integer afterConditionNumberOfEvents;

    private EventBeanReader eventBeanReader;

    public OutputProcessViewDirectDistinctOrAfterFactory(StatementContext statementContext, OutputStrategyPostProcessFactory postProcessFactory, boolean distinct, ExprTimePeriod afterTimePeriod, Integer afterConditionNumberOfEvents, EventType resultEventType) {
        super(statementContext, postProcessFactory);
        isDistinct = distinct;
        this.afterTimePeriod = afterTimePeriod;
        this.afterConditionNumberOfEvents = afterConditionNumberOfEvents;

        if (isDistinct)
        {
            if (resultEventType instanceof EventTypeSPI)
            {
                EventTypeSPI eventTypeSPI = (EventTypeSPI) resultEventType;
                eventBeanReader = eventTypeSPI.getReader();
            }
            if (eventBeanReader == null)
            {
                eventBeanReader = new EventBeanReaderDefaultImpl(resultEventType);
            }
        }
    }

    @Override
    public OutputProcessViewBase makeView(ResultSetProcessor resultSetProcessor, AgentInstanceContext agentInstanceContext) {

        boolean isAfterConditionSatisfied = true;
        Long afterConditionTime = null;
        if (afterConditionNumberOfEvents != null)
        {
            isAfterConditionSatisfied = false;
        }
        else if (afterTimePeriod != null)
        {
            isAfterConditionSatisfied = false;
            Object result = afterTimePeriod.evaluate(null, true, agentInstanceContext);
            if (result == null)
            {
                log.warn("The expression in the 'after' clause time period has returned a null value, ignoring after-clause");
                isAfterConditionSatisfied = true;
            }
            else
            {
                double sec = ((Number) result).doubleValue();
                long msec = (long) (sec * 1000.0);
                afterConditionTime = agentInstanceContext.getStatementContext().getTimeProvider().getTime() + msec;
            }
        }

        if (super.postProcessFactory == null) {
            return new OutputProcessViewDirectDistinctOrAfter(resultSetProcessor, afterConditionTime, afterConditionNumberOfEvents, isAfterConditionSatisfied, this);
        }
        OutputStrategyPostProcess postProcess = postProcessFactory.make(agentInstanceContext);
        return new OutputProcessViewDirectDistinctOrAfterPostProcess(resultSetProcessor, afterConditionTime, afterConditionNumberOfEvents, isAfterConditionSatisfied, this, postProcess);
    }

    public boolean isDistinct() {
        return isDistinct;
    }

    public EventBeanReader getEventBeanReader() {
        return eventBeanReader;
    }
}