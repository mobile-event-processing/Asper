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

package com.espertech.esper.epl.join.exec.sorted;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.join.table.PropertySortedEventTable;

import java.util.Collection;
import java.util.Set;

public class SortedAccessStrategyLT extends SortedAccessStrategyRelOpBase implements SortedAccessStrategy {

    public SortedAccessStrategyLT(boolean isNWOnTrigger, int lookupStream, int numStreams, ExprEvaluator keyEval) {
        super(isNWOnTrigger, lookupStream, numStreams, keyEval);
    }

    public Set<EventBean> lookup(EventBean theEvent, PropertySortedEventTable index, ExprEvaluatorContext context) {
        return index.lookupLess(super.evaluateLookup(theEvent, context));
    }

    public Collection<EventBean> lookup(EventBean[] eventsPerStream, PropertySortedEventTable index, ExprEvaluatorContext context) {
        return index.lookupLessThenColl(super.evaluatePerStream(eventsPerStream, context));
    }
}