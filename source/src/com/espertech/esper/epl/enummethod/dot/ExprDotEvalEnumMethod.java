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

import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.expression.ExprDotEval;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprValidationContext;
import com.espertech.esper.epl.expression.ExprValidationException;

import java.util.List;

public interface ExprDotEvalEnumMethod extends ExprDotEval {

    public void init(EnumMethodEnum lambda,
                     String lambdaUsedName,
                     ExprDotEvalTypeInfo currentInputType,
                     List<ExprNode> parameters,
                     ExprValidationContext validationContext) throws ExprValidationException;
}
