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

package com.espertech.esper.epl.enummethod.eval;

import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.event.arr.ObjectArrayEventBean;
import com.espertech.esper.event.arr.ObjectArrayEventType;

public abstract class EnumEvalBaseScalar extends EnumEvalBase implements EnumEval {

    protected final ObjectArrayEventType type;

    public EnumEvalBaseScalar(ExprEvaluator innerExpression, int streamCountIncoming, ObjectArrayEventType type) {
        super(innerExpression, streamCountIncoming);
        this.type = type;
    }
}
