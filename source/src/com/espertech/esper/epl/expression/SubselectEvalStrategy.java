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

import java.util.Collection;

/**
 * Strategy for evaluation of a subselect.
 */
public interface SubselectEvalStrategy
{
    /**
     * Evaluate.
     * @param eventsPerStream events per stream
     * @param isNewData true for new data
     * @param matchingEvents prefiltered events
     * @param exprEvaluatorContext expression evaluation context
     * @return eval result
     */
    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext);
}
