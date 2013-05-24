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

package com.espertech.esper.epl.datetime.interval;

import com.espertech.esper.epl.expression.ExprConstantNode;
import com.espertech.esper.epl.expression.ExprConstantNodeImpl;
import com.espertech.esper.epl.expression.ExprEvaluator;

public class ExprOptionalConstant {
    private final ExprEvaluator evaluator;
    private final Long optionalConstant;

    public ExprOptionalConstant(ExprEvaluator evaluator, Long optionalConstant) {
        this.evaluator = evaluator;
        this.optionalConstant = optionalConstant;
    }

    public ExprOptionalConstant(ExprEvaluator evaluator) {
        this.evaluator = evaluator;
        this.optionalConstant = null;
    }

    public Long getOptionalConstant() {
        return optionalConstant;
    }

    public ExprEvaluator getEvaluator() {
        return evaluator;
    }

    public static ExprOptionalConstant make(long maxValue) {
        ExprConstantNode constantNode = new ExprConstantNodeImpl(maxValue);
        return new ExprOptionalConstant(constantNode.getExprEvaluator(), maxValue);
    }
}
