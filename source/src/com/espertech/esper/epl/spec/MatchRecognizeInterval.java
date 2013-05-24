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

package com.espertech.esper.epl.spec;

import com.espertech.esper.epl.expression.*;
import com.espertech.esper.util.MetaDefItem;

import java.io.Serializable;

/**
 * Interval specification within match_recognize.
 */
public class MatchRecognizeInterval implements MetaDefItem, Serializable
{
    private ExprTimePeriod timePeriodExpr;
    private static final long serialVersionUID = 9015877742992218244L;

    /**
     * Ctor.
     * @param timePeriodExpr time period
     */
    public MatchRecognizeInterval(ExprTimePeriod timePeriodExpr)
    {
        this.timePeriodExpr = timePeriodExpr;
    }

    /**
     * Returns the time period.
     * @return time period
     */
    public ExprTimePeriod getTimePeriodExpr()
    {
        return timePeriodExpr;
    }

    /**
     * Returns the number of milliseconds.
     * @return msec
     */
    public long getMSec()
    {
        double seconds = (Double) timePeriodExpr.evaluate(null, true, null);
        return (long) (seconds * 1000);
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException {
        timePeriodExpr = (ExprTimePeriod) ExprNodeUtility.getValidatedSubtree(timePeriodExpr, validationContext);
    }
}