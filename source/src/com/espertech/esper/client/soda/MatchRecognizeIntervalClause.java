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

package com.espertech.esper.client.soda;

import java.io.Serializable;

/**
 * Interval used within match recognize.
 */
public class MatchRecognizeIntervalClause implements Serializable {
    private static final long serialVersionUID = 3883389636579120071L;
    private Expression expression;

    /**
     * Ctor.
     */
    public MatchRecognizeIntervalClause() {
    }

    /**
     * Ctor.
     * @param expression interval expression
     */
    public MatchRecognizeIntervalClause(TimePeriodExpression expression) {
        this.expression = expression;
    }

    /**
     * Returns the interval expression.
     * @return expression
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Sets the interval expression.
     * @param expression to set
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
