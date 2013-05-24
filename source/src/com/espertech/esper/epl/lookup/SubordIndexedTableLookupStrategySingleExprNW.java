/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.lookup;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.join.table.PropertyIndexedEventTableSingle;

import java.util.Collection;

/**
 * Index lookup strategy for subqueries.
 */
public class SubordIndexedTableLookupStrategySingleExprNW implements SubordTableLookupStrategy
{
    /**
     * Stream numbers to get key values from.
     */
    protected final ExprEvaluator evaluator;

    /**
     * Index to look up in.
     */
    protected final PropertyIndexedEventTableSingle index;

    public SubordIndexedTableLookupStrategySingleExprNW(ExprEvaluator evaluator, PropertyIndexedEventTableSingle index) {
        this.evaluator = evaluator;
        this.index = index;
    }

    /**
     * Returns index to look up in.
     * @return index to use
     */
    public PropertyIndexedEventTableSingle getIndex()
    {
        return index;
    }

    public Collection<EventBean> lookup(EventBean[] eventsPerStream, ExprEvaluatorContext context)
    {
        Object key = getKey(eventsPerStream, context);
        return index.lookup(key);
    }

    public Collection<EventBean> lookup(Object[] keys) {
        return index.lookup(keys[0]);
    }

    /**
     * Get the index lookup keys.
     * @param eventsPerStream is the events for each stream
     * @return key object
     */
    protected Object getKey(EventBean[] eventsPerStream, ExprEvaluatorContext context)
    {
        return evaluator.evaluate(eventsPerStream, true, context);
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " evaluator " + evaluator.getClass().getSimpleName();
    }
}
