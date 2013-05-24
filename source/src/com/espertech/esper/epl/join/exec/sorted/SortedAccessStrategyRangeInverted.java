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
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.join.table.PropertySortedEventTable;

import java.util.Collection;
import java.util.Set;

public class SortedAccessStrategyRangeInverted extends SortedAccessStrategyRangeBase implements SortedAccessStrategy {

    public SortedAccessStrategyRangeInverted(boolean isNWOnTrigger, int lookupStream, int numStreams, ExprEvaluator start, boolean includeStart, ExprEvaluator end, boolean includeEnd) {
        super(isNWOnTrigger, lookupStream, numStreams, start, includeStart, end, includeEnd);
    }

    public Set<EventBean> lookup(EventBean theEvent, PropertySortedEventTable index, ExprEvaluatorContext context) {
        return index.lookupRangeInverted(super.evaluateLookupStart(theEvent, context), includeStart, super.evaluateLookupEnd(theEvent, context), includeEnd);
    }

    public Collection<EventBean> lookup(EventBean[] eventsPerStream, PropertySortedEventTable index, ExprEvaluatorContext context) {
        return index.lookupRangeInvertedColl(super.evaluatePerStreamStart(eventsPerStream, context), includeStart, super.evaluatePerStreamEnd(eventsPerStream, context), includeEnd);
    }
}

