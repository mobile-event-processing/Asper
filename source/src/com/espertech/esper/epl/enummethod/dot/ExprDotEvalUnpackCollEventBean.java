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
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.EventUnderlyingCollection;
import com.espertech.esper.epl.expression.ExprDotEval;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

import java.util.Collection;

public class ExprDotEvalUnpackCollEventBean implements ExprDotEval {

    private ExprDotEvalTypeInfo typeInfo;

    public ExprDotEvalUnpackCollEventBean(EventType type) {
        typeInfo = ExprDotEvalTypeInfo.componentColl(type.getUnderlyingType());
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (target == null) {
            return null;
        }
        Collection<EventBean> it = (Collection<EventBean>) target;
        return new EventUnderlyingCollection(it);
    }

    public ExprDotEvalTypeInfo getTypeInfo() {
        return typeInfo;
    }
}
