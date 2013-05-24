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
import com.espertech.esper.epl.join.plan.QueryGraphRangeEnum;
import com.espertech.esper.epl.join.plan.QueryGraphValueEntryRange;
import com.espertech.esper.epl.join.plan.QueryGraphValueEntryRangeIn;
import com.espertech.esper.epl.join.plan.QueryGraphValueEntryRangeRelOp;
import com.espertech.esper.epl.lookup.SubordPropRangeKey;

import java.util.*;

public class CompositeIndexQueryRange implements CompositeIndexQuery {

    private final CompositeAccessStrategy strategy;
    private CompositeIndexQuery next;

    public CompositeIndexQueryRange(boolean isNWOnTrigger, int lookupStream, int numStreams, SubordPropRangeKey subqRangeKey, Class coercionType) {

        QueryGraphValueEntryRange rangeProp = subqRangeKey.getRangeInfo();

        if (rangeProp.getType().isRange()) {
            QueryGraphValueEntryRangeIn rangeIn = (QueryGraphValueEntryRangeIn) rangeProp;
            ExprEvaluator start = rangeIn.getExprStart().getExprEvaluator();
            boolean includeStart = rangeProp.getType().isIncludeStart();

            ExprEvaluator end = rangeIn.getExprEnd().getExprEvaluator();
            boolean includeEnd = rangeProp.getType().isIncludeEnd();

            if (!rangeProp.getType().isRangeInverted()) {
                strategy = new CompositeAccessStrategyRangeNormal(isNWOnTrigger, lookupStream, numStreams, start, includeStart, end, includeEnd, coercionType, ((QueryGraphValueEntryRangeIn) rangeProp).isAllowRangeReversal());
            }
            else {
                strategy = new CompositeAccessStrategyRangeInverted(isNWOnTrigger, lookupStream, numStreams, start, includeStart, end, includeEnd, coercionType);
            }
        }
        else {
            QueryGraphValueEntryRangeRelOp relOp = (QueryGraphValueEntryRangeRelOp) rangeProp;
            ExprEvaluator key = relOp.getExpression().getExprEvaluator();
            if (rangeProp.getType() == QueryGraphRangeEnum.GREATER_OR_EQUAL) {
                strategy = new CompositeAccessStrategyGE(isNWOnTrigger, lookupStream, numStreams, key, coercionType);
            }
            else if (rangeProp.getType() == QueryGraphRangeEnum.GREATER) {
                strategy = new CompositeAccessStrategyGT(isNWOnTrigger, lookupStream, numStreams, key, coercionType);
            }
            else if (rangeProp.getType() == QueryGraphRangeEnum.LESS_OR_EQUAL) {
                strategy = new CompositeAccessStrategyLE(isNWOnTrigger, lookupStream, numStreams, key, coercionType);
            }
            else if (rangeProp.getType() == QueryGraphRangeEnum.LESS) {
                strategy = new CompositeAccessStrategyLT(isNWOnTrigger, lookupStream, numStreams, key, coercionType);
            }
            else {
                throw new IllegalArgumentException("Comparison operator " + rangeProp.getType() + " not supported");
            }
        }
    }

    public void add(EventBean theEvent, Map parent, Set<EventBean> result) {
        strategy.lookup(theEvent, parent, result, next, null);
    }

    public void add(EventBean[] eventsPerStream, Map parent, Collection<EventBean> result) {
        strategy.lookup(eventsPerStream, parent, result, next, null);
    }

    public Set<EventBean> get(EventBean theEvent, Map parent, ExprEvaluatorContext context) {
        return strategy.lookup(theEvent, parent, null, next, context);
    }

    public Collection<EventBean> get(EventBean[] eventsPerStream, Map parent, ExprEvaluatorContext context) {
        return strategy.lookup(eventsPerStream, parent, null, next, context);
    }

    protected static Set<EventBean> handle(EventBean theEvent, SortedMap sortedMapOne, SortedMap sortedMapTwo, Set<EventBean> result, CompositeIndexQuery next) {
        if (next == null) {
            if (result == null) {
                result = new HashSet<EventBean>();
            }
            addResults(sortedMapOne, sortedMapTwo, result);
            return result;
        }
        else {
            if (result == null) {
                result = new HashSet<EventBean>();
            }
            Map<Object, Map> map = (Map<Object, Map>) sortedMapOne;
            for (Map.Entry<Object, Map> entry : map.entrySet()) {
                next.add(theEvent, entry.getValue(), result);
            }
            if (sortedMapTwo != null) {
                map = (Map<Object, Map>) sortedMapTwo;
                for (Map.Entry<Object, Map> entry : map.entrySet()) {
                    next.add(theEvent, entry.getValue(), result);
                }
            }
            return result;
        }
    }

    protected static Collection<EventBean> handle(EventBean[] eventsPerStream, SortedMap sortedMapOne, SortedMap sortedMapTwo, Collection<EventBean> result, CompositeIndexQuery next) {
        if (next == null) {
            if (result == null) {
                result = new HashSet<EventBean>();
            }
            addResults(sortedMapOne, sortedMapTwo, result);
            return result;
        }
        else {
            if (result == null) {
                result = new HashSet<EventBean>();
            }
            Map<Object, Map> map = (Map<Object, Map>) sortedMapOne;
            for (Map.Entry<Object, Map> entry : map.entrySet()) {
                next.add(eventsPerStream, entry.getValue(), result);
            }
            if (sortedMapTwo != null) {
                map = (Map<Object, Map>) sortedMapTwo;
                for (Map.Entry<Object, Map> entry : map.entrySet()) {
                    next.add(eventsPerStream, entry.getValue(), result);
                }
            }
            return result;
        }
    }

    private static void addResults(SortedMap sortedMapOne, SortedMap sortedMapTwo, Collection<EventBean> result) {
        Map<Object, Set<EventBean>> map = (Map<Object, Set<EventBean>>) sortedMapOne;
        for (Map.Entry<Object, Set<EventBean>> entry : map.entrySet()) {
            result.addAll(entry.getValue());
        }

        if (sortedMapTwo != null) {
            map = (Map<Object, Set<EventBean>>) sortedMapTwo;
            for (Map.Entry<Object, Set<EventBean>> entry : map.entrySet()) {
                result.addAll(entry.getValue());
            }
        }
    }

    public void setNext(CompositeIndexQuery next) {
        this.next = next;
    }
}
