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
import com.espertech.esper.client.EventPropertyGetter;

import java.util.Map;

public class ExprIdentNodeEvaluatorContext implements ExprIdentNodeEvaluator {

    private final int streamNum;
    private final Class resultType;
    private final EventPropertyGetter getter;

    public ExprIdentNodeEvaluatorContext(int streamNum, Class resultType, EventPropertyGetter getter) {
        this.streamNum = streamNum;
        this.resultType = resultType;
        this.getter = getter;
    }

    public boolean evaluatePropertyExists(EventBean[] eventsPerStream, boolean isNewData) {
        return true;
    }

    public int getStreamNum() {
        return streamNum;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (context.getContextProperties() != null) {
            return getter.get(context.getContextProperties());
        }
        return null;
    }

    public Class getType() {
        return resultType;
    }

    public Map<String, Object> getEventType() throws ExprValidationException {
        return null;
    }

    public EventPropertyGetter getGetter() {
        return getter;
    }

    public boolean isContextEvaluated() {
        return true;
    }
}
