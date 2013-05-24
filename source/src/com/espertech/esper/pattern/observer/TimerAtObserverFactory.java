/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern.observer;

import com.espertech.esper.client.EPException;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.pattern.*;
import com.espertech.esper.schedule.ScheduleParameterException;
import com.espertech.esper.schedule.ScheduleSpec;
import com.espertech.esper.schedule.ScheduleSpecUtil;
import com.espertech.esper.util.MetaDefItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.List;

/**
 * Factory for 'crontab' observers that indicate truth when a time point was reached.
 */
public class TimerAtObserverFactory implements ObserverFactory, MetaDefItem, Serializable
{
    private static final long serialVersionUID = -4463261229142331396L;

    /**
     * Parameters.
     */
    protected List<ExprNode> parameters;

    /**
     * Convertor.
     */
    protected transient MatchedEventConvertor convertor;

    /**
     * The schedule specification for the timer-at.
     */
    protected ScheduleSpec spec = null;

    public void setObserverParameters(List<ExprNode> parameters, MatchedEventConvertor convertor) throws ObserverParameterException
    {
        if (log.isDebugEnabled())
        {
            log.debug(".setObserverParameters " + parameters);
        }

        if ((parameters.size() < 5) || (parameters.size() > 6))
        {
            throw new ObserverParameterException("Invalid number of parameters for timer:at");
        }

        this.parameters = parameters;
        this.convertor = convertor;

        // if all parameters are constants, lets try to evaluate and build a schedule for early validation
        boolean allConstantResult = true;
        for (ExprNode param : parameters)
        {
            if (!param.isConstantResult())
            {
                allConstantResult = false;
            }
        }

        if (allConstantResult)
        {
            try
            {
                List<Object> observerParameters = PatternExpressionUtil.evaluate("Timer-at observer", new MatchedEventMapImpl(convertor.getMatchedEventMapMeta()), parameters, convertor, null);
                spec = ScheduleSpecUtil.computeValues(observerParameters.toArray());
            }
            catch (ScheduleParameterException e)
            {
                throw new ObserverParameterException("Error computing crontab schedule specification: " + e.getMessage(), e);
            }
        }
    }

    protected ScheduleSpec computeSpec(MatchedEventMap beginState, PatternAgentInstanceContext context) {
        if (spec != null) {
            return spec;
        }
        List<Object> observerParameters = PatternExpressionUtil.evaluate("Timer-at observer", beginState, parameters, convertor, context.getAgentInstanceContext());
        try {
            return ScheduleSpecUtil.computeValues(observerParameters.toArray());
        }
        catch (ScheduleParameterException e) {
            throw new EPException("Error computing crontab schedule specification: " + e.getMessage(), e);
        }
    }

    public EventObserver makeObserver(PatternAgentInstanceContext context, MatchedEventMap beginState, ObserverEventEvaluator observerEventEvaluator,
                                      EvalStateNodeNumber stateNodeId, Object observerState) {
        return new TimerAtObserver(computeSpec(beginState, context), beginState, observerEventEvaluator);
    }

    private static final Log log = LogFactory.getLog(TimerAtObserverFactory.class);
}
