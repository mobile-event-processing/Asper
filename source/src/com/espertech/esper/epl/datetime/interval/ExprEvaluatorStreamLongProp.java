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

package com.espertech.esper.epl.datetime.interval;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprValidationException;

import java.util.Map;

public class ExprEvaluatorStreamLongProp implements ExprEvaluator {

    private final int streamId;
    private final EventPropertyGetter getter;

    public ExprEvaluatorStreamLongProp(int streamId, EventPropertyGetter getter) {
        this.streamId = streamId;
        this.getter = getter;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        EventBean theEvent = eventsPerStream[streamId];
        if (theEvent == null) {
            return null;
        }
        return getter.get(theEvent);
    }

    public Class getType() {
        return Long.class;
    }

    public Map<String, Object> getEventType() throws ExprValidationException {
        return null;
    }
}
