/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.filter;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.variable.VariableService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExprNodeAdapterBaseVariables extends ExprNodeAdapterBase
{
    private static final Log log = LogFactory.getLog(ExprNodeAdapterBaseVariables.class);

    protected final VariableService variableService;

    public ExprNodeAdapterBaseVariables(String statementName, ExprNode exprNode, ExprEvaluatorContext evaluatorContext, VariableService variableService) {
        super(statementName, exprNode, evaluatorContext);
        this.variableService = variableService;
    }

    @Override
    public boolean evaluate(EventBean theEvent)
    {
        variableService.setLocalVersion();
        return evaluatePerStream(new EventBean[] {theEvent});
    }
}
