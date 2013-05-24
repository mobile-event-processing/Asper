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

package com.espertech.esper.epl.enummethod.eval;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.event.arr.ObjectArrayEventBean;
import com.espertech.esper.event.arr.ObjectArrayEventType;

import java.util.Collection;

public class EnumEvalAnyOfScalar extends EnumEvalBaseScalar implements EnumEval {

    public EnumEvalAnyOfScalar(ExprEvaluator innerExpression, int streamCountIncoming, ObjectArrayEventType type) {
        super(innerExpression, streamCountIncoming, type);
    }

    public Object evaluateEnumMethod(EventBean[] eventsLambda, Collection target, boolean isNewData, ExprEvaluatorContext context) {
        if (target.isEmpty()) {
            return false;
        }

        ObjectArrayEventBean evalEvent = new ObjectArrayEventBean(new Object[1], type);
        for (Object next : target) {

            evalEvent.getProperties()[0] = next;
            eventsLambda[streamNumLambda] = evalEvent;

            Object pass = innerExpression.evaluate(eventsLambda, isNewData, context);
            if (pass != null && ((Boolean) pass)) {
                return true;
            }
        }

        return false;
    }
}
