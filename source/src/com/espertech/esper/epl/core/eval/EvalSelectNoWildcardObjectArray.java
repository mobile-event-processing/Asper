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
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.event.EventAdapterService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EvalSelectNoWildcardObjectArray implements SelectExprProcessor {

    private static final Log log = LogFactory.getLog(EvalSelectNoWildcardObjectArray.class);

    private final SelectExprContext selectExprContext;
    private final EventType resultEventType;

    public EvalSelectNoWildcardObjectArray(SelectExprContext selectExprContext, EventType resultEventType) {
        this.selectExprContext = selectExprContext;
        this.resultEventType = resultEventType;
    }

    public EventBean process(EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext)
    {
        ExprEvaluator[] expressionNodes = selectExprContext.getExpressionNodes();
        EventAdapterService eventAdapterService = selectExprContext.getEventAdapterService();

        // Evaluate all expressions and build a map of name-value pairs
        Object[] props = new Object[expressionNodes.length];
        for (int i = 0; i < expressionNodes.length; i++)
        {
            Object evalResult = expressionNodes[i].evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
            props[i] = evalResult;
        }

        return eventAdapterService.adapterForTypedObjectArray(props, resultEventType);
    }

    public EventType getResultEventType()
    {
        return resultEventType;
    }
}