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

package com.espertech.esper.epl.datetime.calop;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

import java.util.Calendar;

public class CalendarOpWithMax implements CalendarOp {

    private final CalendarFieldEnum fieldName;

    public CalendarOpWithMax(CalendarFieldEnum fieldName) {
        this.fieldName = fieldName;
    }

    public void evaluate(Calendar cal, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        cal.set(fieldName.getCalendarField(), cal.getActualMaximum(fieldName.getCalendarField()));
    }
}
