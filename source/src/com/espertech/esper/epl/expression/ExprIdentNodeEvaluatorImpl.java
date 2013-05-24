/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;

import java.util.Map;

public class ExprIdentNodeEvaluatorImpl implements ExprIdentNodeEvaluator
{
    private final int streamNum;
    private final EventPropertyGetter propertyGetter;
    private final Class propertyType;

    public ExprIdentNodeEvaluatorImpl(int streamNum, EventPropertyGetter propertyGetter, Class propertyType) {
        this.streamNum = streamNum;
        this.propertyGetter = propertyGetter;
        this.propertyType = propertyType;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        EventBean theEvent = eventsPerStream[streamNum];
        if (theEvent == null)
        {
            return null;
        }
        return propertyGetter.get(theEvent);
    }

    public Class getType() {
        return propertyType;
    }

    public Map<String, Object> getEventType() throws ExprValidationException {
        return null;
    }

    public EventPropertyGetter getGetter() {
        return propertyGetter;
    }

    /**
     * Returns true if the property exists, or false if not.
     * @param eventsPerStream each stream's events
     * @param isNewData if the stream represents insert or remove stream
     * @return true if the property exists, false if not
     */
    public boolean evaluatePropertyExists(EventBean[] eventsPerStream, boolean isNewData)
    {
        EventBean theEvent = eventsPerStream[streamNum];
        if (theEvent == null)
        {
            return false;
        }
        return propertyGetter.isExistsProperty(theEvent);
    }

    public int getStreamNum() {
        return streamNum;
    }

    public boolean isContextEvaluated() {
        return false;
    }
}
