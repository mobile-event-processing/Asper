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

public class EnumEvalBase {

    protected ExprEvaluator innerExpression;
    protected int streamNumLambda;

    public EnumEvalBase(ExprEvaluator innerExpression, int streamCountIncoming) {
        this(streamCountIncoming);
        this.innerExpression = innerExpression;
    }

    public EnumEvalBase(int streamCountIncoming) {
        this.streamNumLambda = streamCountIncoming;
    }

    public ExprEvaluator getInnerExpression() {
        return innerExpression;
    }

    public int getStreamNumSize() {
        return streamNumLambda + 1;
    }
}
