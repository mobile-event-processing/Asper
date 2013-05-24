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

package com.espertech.esper.epl.core;

import com.espertech.esper.epl.expression.ExprEvaluator;

public class OrderByElement {
    private ExprEvaluator expr;
    private boolean isDescending;

    public OrderByElement(ExprEvaluator expr, boolean descending) {
        this.expr = expr;
        isDescending = descending;
    }

    public ExprEvaluator getExpr() {
        return expr;
    }

    public boolean isDescending() {
        return isDescending;
    }
}
