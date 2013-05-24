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

package com.espertech.esper.epl.join.exec.composite;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.event.EventBeanUtility;

import java.util.*;

public class CompositeAccessStrategyRangeInverted extends CompositeAccessStrategyRangeBase implements CompositeAccessStrategy {

    public CompositeAccessStrategyRangeInverted(boolean isNWOnTrigger, int lookupStream, int numStreams, ExprEvaluator start, boolean includeStart, ExprEvaluator end, boolean includeEnd, Class coercionType) {
        super(isNWOnTrigger, lookupStream, numStreams, start, includeStart, end, includeEnd, coercionType);
    }

    public Set<EventBean> lookup(EventBean theEvent, Map parent, Set<EventBean> result, CompositeIndexQuery next, ExprEvaluatorContext context) {
        Object comparableStart = super.evaluateLookupStart(theEvent, context);
        if (comparableStart == null) {
            return null;
        }
        Object comparableEnd = super.evaluateLookupEnd(theEvent, context);
        if (comparableEnd == null) {
            return null;
        }
        comparableStart = EventBeanUtility.coerce(comparableStart, coercionType);
        comparableEnd = EventBeanUtility.coerce(comparableEnd, coercionType);

        TreeMap index = (TreeMap) parent;
        SortedMap<Object,Set<EventBean>> submapOne = index.headMap(comparableStart, !includeStart);
        SortedMap<Object,Set<EventBean>> submapTwo = index.tailMap(comparableEnd, !includeEnd);
        return CompositeIndexQueryRange.handle(theEvent, submapOne, submapTwo, result, next);
    }

    public Collection<EventBean> lookup(EventBean[] eventPerStream, Map parent, Collection<EventBean> result, CompositeIndexQuery next, ExprEvaluatorContext context) {
        Object comparableStart = super.evaluatePerStreamStart(eventPerStream, context);
        if (comparableStart == null) {
            return null;
        }
        Object comparableEnd = super.evaluatePerStreamEnd(eventPerStream, context);
        if (comparableEnd == null) {
            return null;
        }
        comparableStart = EventBeanUtility.coerce(comparableStart, coercionType);
        comparableEnd = EventBeanUtility.coerce(comparableEnd, coercionType);

        TreeMap index = (TreeMap) parent;
        SortedMap<Object,Set<EventBean>> submapOne = index.headMap(comparableStart, !includeStart);
        SortedMap<Object,Set<EventBean>> submapTwo = index.tailMap(comparableEnd, !includeEnd);
        return CompositeIndexQueryRange.handle(eventPerStream, submapOne, submapTwo, result, next);
    }
}
