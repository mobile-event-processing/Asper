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

package com.espertech.esper.epl.agg.access;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluator;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents the aggregation accessor that provides the result for the "last" aggregation function without index.
 */
public class AggregationAccessorLast implements AggregationAccessor
{
    private final int streamNum;
    private final ExprEvaluator childNode;
    private final EventBean[] eventsPerStream;

    /**
     * Ctor.
     * @param streamNum stream id
     * @param childNode expression
     */
    public AggregationAccessorLast(int streamNum, ExprEvaluator childNode)
    {
        this.streamNum = streamNum;
        this.childNode = childNode;
        this.eventsPerStream = new EventBean[streamNum + 1];
    }

    public Object getValue(AggregationAccess access) {
        EventBean bean = access.getLastValue();
        if (bean == null) {
            return null;
        }
        eventsPerStream[streamNum] = bean;
        return childNode.evaluate(eventsPerStream, true, null);
    }

    public Collection<EventBean> getCollectionReadOnly(AggregationAccess access) {
        EventBean bean = access.getLastValue();
        if (bean == null) {
            return null;
        }
        return Collections.singletonList(bean);
    }

    public EventBean getEventBean(AggregationAccess currentAcces) {
        return currentAcces.getLastValue();
    }
}