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

package com.espertech.esper.epl.declexpr;

import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.CreateExpressionDesc;
import com.espertech.esper.epl.spec.ExpressionDeclItem;
import com.espertech.esper.epl.spec.ExpressionScriptProvided;

import java.util.List;

public interface ExprDeclaredService {
    public ExpressionDeclItem getExpression(String name);
    public List<ExpressionScriptProvided> getScriptsByName(String expressionName);
    public String addExpressionOrScript(CreateExpressionDesc expression) throws ExprValidationException;
    public void destroyedExpression(CreateExpressionDesc expression);
    public void destroy();
}
