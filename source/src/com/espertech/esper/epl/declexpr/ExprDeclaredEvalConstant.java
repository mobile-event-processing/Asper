/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.declexpr;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprValidationException;

import java.util.Map;

public class ExprDeclaredEvalConstant implements ExprEvaluator {
    private final Class returnType;
    private final Object value;

    public ExprDeclaredEvalConstant(Class returnType, Object value) {
        this.returnType = returnType;
        this.value = value;
    }

    public Class getType() {
        return returnType;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return value;
    }

    public Map<String, Object> getEventType() throws ExprValidationException {
        return null;
    }
}
