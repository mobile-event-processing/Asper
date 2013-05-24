/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.start;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.service.EPServicesContext;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.StatementSpecCompiled;
import com.espertech.esper.view.ViewProcessingException;
import com.espertech.esper.view.ZeroDepthStream;

import java.util.Collections;

/**
 * Starts and provides the stop method for EPL statements.
 */
public class EPStatementStartMethodCreateExpression extends EPStatementStartMethodBase
{
    public EPStatementStartMethodCreateExpression(StatementSpecCompiled statementSpec) {
        super(statementSpec);
    }

    public EPStatementStartResult startInternal(final EPServicesContext services, StatementContext statementContext, boolean isNewStatement, boolean isRecoveringStatement, boolean isRecoveringResilient) throws ExprValidationException, ViewProcessingException {
        String expressionName = services.getExprDeclaredService().addExpressionOrScript(statementSpec.getCreateExpressionDesc());

        // define output event type
        String typeName = "EventType_Expression_" + expressionName;
        EventType resultType = services.getEventAdapterService().createAnonymousMapType(typeName, Collections.<String, Object>emptyMap());

        EPStatementStopMethod stopMethod = new EPStatementStopMethod() {
            public void stop() {
                // no action
            }
        };

        EPStatementDestroyMethod destroyMethod = new EPStatementDestroyMethod() {
            public void destroy() {
                services.getExprDeclaredService().destroyedExpression(statementSpec.getCreateExpressionDesc());
            }
        };
        return new EPStatementStartResult(new ZeroDepthStream(resultType), stopMethod, destroyMethod);
    }
}
