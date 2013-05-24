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
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.core.SelectExprProcessor;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.event.MappedEventBean;

public class EvalSelectStreamWUnderlyingRecastMap implements SelectExprProcessor {

    private final SelectExprContext selectExprContext;
    private final int underlyingStreamNumber;
    private final EventType resultType;

    public EvalSelectStreamWUnderlyingRecastMap(SelectExprContext selectExprContext, int underlyingStreamNumber, EventType resultType) {
        this.selectExprContext = selectExprContext;
        this.underlyingStreamNumber = underlyingStreamNumber;
        this.resultType = resultType;
    }

    public EventType getResultEventType() {
        return resultType;
    }

    public EventBean process(EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        MappedEventBean theEvent = (MappedEventBean) eventsPerStream[underlyingStreamNumber];
        return selectExprContext.getEventAdapterService().adapterForTypedMap(theEvent.getProperties(), resultType);
    }
}
