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

package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetterIndexed;
import com.espertech.esper.client.EventPropertyGetterMapped;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExprDotEvalPropertyExprIndexed extends ExprDotEvalPropertyExprBase {
    private static final Log log = LogFactory.getLog(ExprDotEvalPropertyExprIndexed.class);

    private final EventPropertyGetterIndexed indexedGetter;

    public ExprDotEvalPropertyExprIndexed(String statementName, String propertyName, int streamNum, ExprEvaluator exprEvaluator, Class propertyType, EventPropertyGetterIndexed indexedGetter) {
        super(statementName, propertyName, streamNum, exprEvaluator, propertyType);
        this.indexedGetter = indexedGetter;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        EventBean eventInQuestion = eventsPerStream[super.streamNum];
        if (eventInQuestion == null) {
            return null;
        }
        Object index = exprEvaluator.evaluate(eventsPerStream, isNewData, context);
        if (index == null || (!(index instanceof Integer))) {
            log.warn(super.getWarningText("integer", index));
            return null;
        }
        return indexedGetter.get(eventInQuestion, (Integer) index);
    }
}
