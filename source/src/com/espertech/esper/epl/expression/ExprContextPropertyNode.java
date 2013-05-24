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
import com.espertech.esper.client.EventType;

import java.util.Arrays;
import java.util.Map;

/**
 * Represents an stream property identifier in a filter expressiun tree.
 */
public class ExprContextPropertyNode extends ExprNodeBase implements ExprEvaluator
{
    private static final long serialVersionUID = 2816977190089087618L;
    private final String propertyName;
    private Class returnType;
    private transient EventPropertyGetter getter;

    public ExprContextPropertyNode(String propertyName) {
        this.propertyName = propertyName;
    }

    public ExprEvaluator getExprEvaluator()
    {
        return this;
    }

    public Map<String, Object> getEventType() {
        return null;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException
    {
        if (validationContext.getContextDescriptor() == null) {
            throw new ExprValidationException("Context property '" + propertyName + "' cannot be used in the expression as provided");
        }
        EventType eventType = validationContext.getContextDescriptor().getContextPropertyRegistry().getContextEventType();
        if (eventType == null) {
            throw new ExprValidationException("Context property '" + propertyName + "' cannot be used in the expression as provided");
        }
        getter = eventType.getGetter(propertyName);
        if (getter == null) {
            throw new ExprValidationException("Context property '" + propertyName + "' is not a known property, known properties are " + Arrays.toString(eventType.getPropertyNames()));
        }
        returnType = eventType.getPropertyType(propertyName);
    }

    public boolean isConstantResult() {
        return false;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (context.getContextProperties() == null) {
            return null;
        }
        return getter.get(context.getContextProperties());
    }

    public Class getType() {
        return returnType;
    }

    public String toExpressionString() {
        return propertyName;
    }

    public EventPropertyGetter getGetter() {
        return getter;
    }

    public boolean equalsNode(ExprNode node) {
        if (this == node) return true;
        if (node == null || getClass() != node.getClass()) return false;

        ExprContextPropertyNode that = (ExprContextPropertyNode) node;
        return propertyName.equals(that.propertyName);
    }
}
