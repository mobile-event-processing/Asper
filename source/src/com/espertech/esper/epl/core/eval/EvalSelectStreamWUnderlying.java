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

package com.espertech.esper.epl.core.eval;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.core.SelectExprProcessor;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.spec.SelectClauseStreamCompiledSpec;
import com.espertech.esper.event.DecoratingEventBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

public class EvalSelectStreamWUnderlying extends EvalSelectStreamBaseMap implements SelectExprProcessor {

    private static final Log log = LogFactory.getLog(EvalSelectStreamWUnderlying.class);

    private final List<SelectExprStreamDesc> unnamedStreams;
    private final boolean singleStreamWrapper;
    private final boolean underlyingIsFragmentEvent;
    private final int underlyingStreamNumber;
    private final EventPropertyGetter underlyingPropertyEventGetter;
    private final ExprEvaluator underlyingExprEvaluator;

    public EvalSelectStreamWUnderlying(SelectExprContext selectExprContext,
                                       EventType resultEventType,
                                       List<SelectClauseStreamCompiledSpec> namedStreams,
                                       boolean usingWildcard,
                                       List<SelectExprStreamDesc> unnamedStreams,
                                       boolean singleStreamWrapper,
                                       boolean underlyingIsFragmentEvent,
                                       int underlyingStreamNumber,
                                       EventPropertyGetter underlyingPropertyEventGetter,
                                       ExprEvaluator underlyingExprEvaluator) {
        super(selectExprContext, resultEventType, namedStreams, usingWildcard);
        this.unnamedStreams = unnamedStreams;
        this.singleStreamWrapper = singleStreamWrapper;
        this.underlyingIsFragmentEvent = underlyingIsFragmentEvent;
        this.underlyingStreamNumber = underlyingStreamNumber;
        this.underlyingPropertyEventGetter = underlyingPropertyEventGetter;
        this.underlyingExprEvaluator = underlyingExprEvaluator;
    }

    public EventBean processSpecific(Map<String, Object> props, EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext)
    {
        // In case of a wildcard and single stream that is itself a
        // wrapper bean, we also need to add the map properties
        if(singleStreamWrapper)
        {
            DecoratingEventBean wrapper = (DecoratingEventBean)eventsPerStream[0];
            if(wrapper != null)
            {
                Map<String, Object> map = wrapper.getDecoratingProperties();
                props.putAll(map);
            }
        }

        EventBean theEvent = null;
        if (underlyingIsFragmentEvent)
        {
            EventBean eventBean = eventsPerStream[underlyingStreamNumber];
            theEvent = (EventBean) eventBean.getFragment(unnamedStreams.get(0).getStreamSelected().getStreamName());
        }
        else if (underlyingPropertyEventGetter != null)
        {
            Object value = underlyingPropertyEventGetter.get(eventsPerStream[underlyingStreamNumber]);
            if (value != null)
            {
                theEvent = super.getSelectExprContext().getEventAdapterService().adapterForBean(value);
            }
        }
        else if (underlyingExprEvaluator != null) {
            Object value = underlyingExprEvaluator.evaluate(eventsPerStream, true, exprEvaluatorContext);
            if (value != null)
            {
                theEvent = super.getSelectExprContext().getEventAdapterService().adapterForBean(value);
            }
        }
        else
        {
            theEvent = eventsPerStream[underlyingStreamNumber];
        }

        // Using a wrapper bean since we cannot use the same event type else same-type filters match.
        // Wrapping it even when not adding properties is very inexpensive.
        return super.getSelectExprContext().getEventAdapterService().adapterForTypedWrapper(theEvent, props, super.getResultEventType());
    }
}