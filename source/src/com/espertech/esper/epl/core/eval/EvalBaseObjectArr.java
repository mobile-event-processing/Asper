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

public abstract class EvalBaseObjectArr extends EvalBase implements SelectExprProcessor {

    protected EvalBaseObjectArr(SelectExprContext selectExprContext, EventType resultEventType) {
        super(selectExprContext, resultEventType);
    }

    public abstract EventBean processSpecific(Object[] props, EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext);

    public EventBean process(EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext)
    {
        ExprEvaluator[] expressionNodes = selectExprContext.getExpressionNodes();

        Object[] result = new Object[expressionNodes.length];
        for (int i = 0; i < expressionNodes.length; i++)
        {
            result[i] = expressionNodes[i].evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        }

        return processSpecific(result, eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
    }
}