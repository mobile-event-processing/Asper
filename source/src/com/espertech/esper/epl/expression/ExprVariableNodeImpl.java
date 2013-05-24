/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.core.DuplicatePropertyException;
import com.espertech.esper.epl.core.PropertyNotFoundException;
import com.espertech.esper.epl.variable.VariableReader;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.event.EventTypeSPI;

import java.util.Map;

/**
 * Represents a variable in an expression tree.
 */
public class ExprVariableNodeImpl extends ExprNodeBase implements ExprEvaluator, ExprVariableNode
{
    private static final long serialVersionUID = 0L;

    private final String variableName;
    private final String optSubPropName;
    private Class variableType;
    private boolean isPrimitive;
    private transient VariableReader reader;
    private transient EventPropertyGetter eventTypeGetter;

    /**
     * Ctor.
     * @param variableName is the name of the variable
     */
    public ExprVariableNodeImpl(String variableName, VariableService variableService)
    {
        if (variableName == null)
        {
            throw new IllegalArgumentException("Variables name is null");
        }

        int indexOfDot = variableName.indexOf('.');
        if (indexOfDot != -1) {
            this.optSubPropName = variableName.substring(indexOfDot + 1, variableName.length());
            this.variableName = variableName.substring(0, indexOfDot);
        }
        else {
            this.variableName = variableName;
            this.optSubPropName = null;
        }

        // Allow initialization of the reader to determine whether constant or not.
        // Validation occurs as usual.
        if (variableService != null) {
            reader = variableService.getReader(variableName);
        }
    }

    public Object getValue() {
        if (reader == null) {
            throw new EPException("Variable expression node has not been initialized");
        }
        return reader.getValue();
    }

    public boolean isConstantValue() {
        return reader != null && reader.isConstant();
    }

    public ExprEvaluator getExprEvaluator()
    {
        return this;
    }

    /**
     * Returns the name of the variable.
     * @return variable name
     */
    public String getVariableName()
    {
        return variableName;
    }

    public Map<String, Object> getEventType() {
        return null;
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException
    {
        reader = validationContext.getVariableService().getReader(variableName);
        if (reader == null)
        {
            throw new ExprValidationException("A variable by name '" + variableName + " has not been declared");
        }

        // determine if any types are property agnostic; If yes, resolve to variable
        boolean hasPropertyAgnosticType = false;
        EventType[] types = validationContext.getStreamTypeService().getEventTypes();
        for (int i = 0; i < validationContext.getStreamTypeService().getEventTypes().length; i++)
        {
            if (types[i] instanceof EventTypeSPI)
            {
                hasPropertyAgnosticType |= ((EventTypeSPI) types[i]).getMetadata().isPropertyAgnostic();
            }
        }

        if (!hasPropertyAgnosticType)
        {
            // the variable name should not overlap with a property name
            try
            {
                validationContext.getStreamTypeService().resolveByPropertyName(variableName, false);
                throw new ExprValidationException("The variable by name '" + variableName + "' is ambigous to a property of the same name");
            }
            catch (DuplicatePropertyException e)
            {
                throw new ExprValidationException("The variable by name '" + variableName + "' is ambigous to a property of the same name");
            }
            catch (PropertyNotFoundException e)
            {
                // expected
            }
        }

        variableType = reader.getType();
        isPrimitive = reader.getEventType() == null;

        if (optSubPropName != null) {
            if (reader.getEventType() == null) {
                throw new ExprValidationException("Property '" + optSubPropName + "' is not valid for variable '" + variableName + "'");
            }
            eventTypeGetter = reader.getEventType().getGetter(optSubPropName);
            if (eventTypeGetter == null) {
                throw new ExprValidationException("Property '" + optSubPropName + "' is not valid for variable '" + variableName + "'");
            }
            variableType = reader.getEventType().getPropertyType(optSubPropName);
        }
    }

    public Class getType()
    {
        if (variableType == null)
        {
            throw new IllegalStateException("Variables node has not been validated");
        }
        return variableType;
    }

    public boolean isConstantResult()
    {
        return reader.isConstant();
    }

    public String toString()
    {
        return "variableName=" + variableName;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        Object value = reader.getValue();
        if (isPrimitive) {
            return value;
        }
        if (value == null) {
            return null;
        }
        EventBean theEvent = (EventBean) value;
        if (optSubPropName == null) {
            return theEvent.getUnderlying();
        }
        return eventTypeGetter.get(theEvent);
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(variableName);
        if (optSubPropName != null) {
            buffer.append(".");
            buffer.append(optSubPropName);
        }
        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprVariableNodeImpl))
        {
            return false;
        }

        ExprVariableNodeImpl that = (ExprVariableNodeImpl) node;

        if (optSubPropName != null ? !optSubPropName.equals(that.optSubPropName) : that.optSubPropName != null) {
            return false;
        }
        return that.variableName.equals(this.variableName);
    }

    public String getVariableNameWithSubProp() {
        if (optSubPropName == null) {
            return variableName;
        }
        return variableName + "." + optSubPropName;
    }
}
