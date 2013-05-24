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
import com.espertech.esper.epl.core.EngineImportService;
import com.espertech.esper.epl.core.SelectExprProcessor;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.SelectClauseExprCompiledSpec;
import com.espertech.esper.event.EventTypeUtility;

public class EvalSelectStreamWUnderlyingRecastBean implements SelectExprProcessor {
    private final SelectExprContext selectExprContext;
    private final EventType targetType;
    private final ExprEvaluator evaluator;

    public EvalSelectStreamWUnderlyingRecastBean(SelectExprContext selectExprContext, SelectClauseExprCompiledSpec expressionSelectedAsStream, EventType underlyingEventType, EventType targetType, int numAdditionalExpressions)
        throws ExprValidationException
    {
        this.selectExprContext = selectExprContext;
        this.targetType = targetType;
        this.evaluator = expressionSelectedAsStream.getSelectExpression().getExprEvaluator();

        if (targetType.getUnderlyingType() != underlyingEventType.getUnderlyingType()) {
            if (!EventTypeUtility.isTypeOrSubTypeOf(underlyingEventType, targetType)) {
                throw new ExprValidationException("Expression-returned event type '" + underlyingEventType.getName() +
                        "' with underlying type '" + underlyingEventType.getUnderlyingType().getName() +
                        "' cannot be converted target event type '" + targetType.getName() +
                        "' with underlying type '" + targetType.getUnderlyingType().getName() + "'");
            }
        }

        if (numAdditionalExpressions != 0) {
            throw new ExprValidationException("Cannot transpose additional properties in the select-clause to target event type '" +
                    targetType.getName() +
                    "' with underlying type '" + targetType.getUnderlyingType().getName() + "', the " + EngineImportService.EXT_SINGLEROW_FUNCTION_TRANSPOSE + " function must occur alone in the select clause");
        }
    }

    public EventType getResultEventType() {
        return targetType;
    }

    public EventBean process(EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        Object result = evaluator.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        return selectExprContext.getEventAdapterService().adapterForTypedBean(result, targetType);
    }
}
