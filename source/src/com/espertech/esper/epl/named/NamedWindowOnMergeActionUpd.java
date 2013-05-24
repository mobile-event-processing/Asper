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

package com.espertech.esper.epl.named;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.collection.OneEventCollection;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

public class NamedWindowOnMergeActionUpd extends NamedWindowOnMergeAction {
    private final NamedWindowUpdateHelper updateHelper;

    public NamedWindowOnMergeActionUpd(ExprEvaluator optionalFilter, NamedWindowUpdateHelper updateHelper) {
        super(optionalFilter);
        this.updateHelper = updateHelper;
    }

    public void apply(EventBean matchingEvent, EventBean[] eventsPerStream, OneEventCollection newData, OneEventCollection oldData, ExprEvaluatorContext exprEvaluatorContext) {
        EventBean copy = updateHelper.update(matchingEvent, eventsPerStream, exprEvaluatorContext);
        newData.add(copy);
        oldData.add(matchingEvent);
    }
}
