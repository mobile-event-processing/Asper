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

package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.agg.service.AggregationService;

import java.util.Collection;

public class SubselectAggregationPreprocessor {

    private final AggregationService aggregationService;
    private final ExprEvaluator filterExpr;

    public SubselectAggregationPreprocessor(AggregationService aggregationService, ExprEvaluator filterExpr) {
        this.aggregationService = aggregationService;
        this.filterExpr = filterExpr;
    }

    public void evaluate(EventBean[] eventsPerStream, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext) {

        aggregationService.clearResults(exprEvaluatorContext);

        if (matchingEvents == null) {
            return;
        }

        EventBean[] events = new EventBean[eventsPerStream.length + 1];
        System.arraycopy(eventsPerStream, 0, events, 1, eventsPerStream.length);

        for (EventBean subselectEvent : matchingEvents)
        {
            // Prepare filter expression event list
            events[0] = subselectEvent;

            Boolean pass = (Boolean) filterExpr.evaluate(events, true, exprEvaluatorContext);
            if ((pass != null) && (pass))
            {
                aggregationService.applyEnter(events, null, exprEvaluatorContext);
            }
        }                
    }
}
