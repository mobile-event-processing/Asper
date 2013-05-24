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

package com.espertech.esper.epl.datetime.eval;

import com.espertech.esper.epl.enummethod.dot.ExprDotEvalTypeInfo;
import com.espertech.esper.epl.expression.ExprDotEval;

public class ExprDotEvalDTMethodDesc {

    private final ExprDotEval eval;
    private final ExprDotEvalTypeInfo returnType;
    private final ExprDotNodeFilterAnalyzerDesc intervalFilterDesc;

    public ExprDotEvalDTMethodDesc(ExprDotEval eval, ExprDotEvalTypeInfo returnType, ExprDotNodeFilterAnalyzerDesc intervalFilterDesc) {
        this.eval = eval;
        this.returnType = returnType;
        this.intervalFilterDesc = intervalFilterDesc;
    }

    public ExprDotEval getEval() {
        return eval;
    }

    public ExprDotEvalTypeInfo getReturnType() {
        return returnType;
    }

    public ExprDotNodeFilterAnalyzerDesc getIntervalFilterDesc() {
        return intervalFilterDesc;
    }
}
