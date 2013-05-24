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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

public abstract class IntervalComputerExprBase implements IntervalComputer {
    private final ExprEvaluator start;
    private final ExprEvaluator finish;

    public IntervalComputerExprBase(IntervalStartEndParameterPair pair) {
        this.start = pair.getStart().getEvaluator();
        this.finish = pair.getEnd().getEvaluator();
    }

    public abstract boolean compute(long leftStartTs, long leftEndTs, long rightStartTs, long rightEndTs, long start, long end);

    public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
        Object startValue = start.evaluate(eventsPerStream, newData, context);
        if (startValue == null) {
            return null;
        }

        Object endValue = finish.evaluate(eventsPerStream, newData, context);
        if (endValue == null) {
            return null;
        }

        long start = toLong(startValue);
        long end = toLong(endValue);

        if (start > end) {
            return compute(leftStart, leftEnd, rightStart, rightEnd, end, start);
        }
        else {
            return compute(leftStart, leftEnd, rightStart, rightEnd, start, end);
        }
    }

    public static long toLong(Object value) {
        double d = ((Number) value).doubleValue();
        return (long) (d * 1000);
    }
}
