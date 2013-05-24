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

package com.espertech.esper.epl.enummethod.dot;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetterIndexed;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprValidationException;

import java.util.Map;

public class PropertyExprEvaluatorNonLambdaIndexed implements ExprEvaluator {

    private final int streamId;
    private final EventPropertyGetterIndexed indexedGetter;
    private final ExprEvaluator paramEval;
    private final Class returnType;

    public PropertyExprEvaluatorNonLambdaIndexed(int streamId, EventPropertyGetterIndexed indexedGetter, ExprEvaluator paramEval, Class returnType) {
        this.streamId = streamId;
        this.indexedGetter = indexedGetter;
        this.paramEval = paramEval;
        this.returnType = returnType;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Integer key = (Integer) paramEval.evaluate(eventsPerStream, isNewData, context);
        EventBean eventInQuestion = eventsPerStream[streamId];
        if (eventInQuestion == null) {
            return null;
        }
        return indexedGetter.get(eventInQuestion, key);
    }

    public Class getType() {
        return returnType;
    }

    public Map<String, Object> getEventType() throws ExprValidationException {
        return null;
    }
}
