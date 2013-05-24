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
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.epl.join.table.PropertyIndexedEventTable;

import java.util.Collection;
import java.util.List;

/**
 * Index lookup strategy for subqueries.
 */
public class SubordIndexedTableLookupStrategyExpr implements SubordTableLookupStrategy
{
    /**
     * Index to look up in.
     */
    protected final PropertyIndexedEventTable index;

    protected final ExprEvaluator[] evaluators;
    private EventBean[] events;

    /**
     * Ctor.
     * @param index is the table carrying the data to lookup into
     */
    public SubordIndexedTableLookupStrategyExpr(int numStreamsOuter, ExprEvaluator[] evaluators, PropertyIndexedEventTable index)
    {
        this.evaluators = evaluators;
        events = new EventBean[numStreamsOuter + 1];
        this.index = index;
    }

    /**
     * Returns index to look up in.
     * @return index to use
     */
    public PropertyIndexedEventTable getIndex()
    {
        return index;
    }

    public Collection<EventBean> lookup(EventBean[] eventsPerStream, ExprEvaluatorContext context)
    {
        Object[] keys = getKeys(eventsPerStream, context);
        return index.lookup(keys);
    }

    public Collection<EventBean> lookup(Object[] keys) {
        return index.lookup(keys);
    }

    /**
     * Get the index lookup keys.
     * @param eventsPerStream is the events for each stream
     * @return key object
     */
    protected Object[] getKeys(EventBean[] eventsPerStream, ExprEvaluatorContext context)
    {
        System.arraycopy(eventsPerStream, 0, events, 1, eventsPerStream.length);
        Object[] keyValues = new Object[evaluators.length];
        for (int i = 0; i < evaluators.length; i++)
        {
            keyValues[i] = evaluators[i].evaluate(events, true, context);
        }
        return keyValues;
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " evaluators " + ExprNodeUtility.printEvaluators(evaluators);
    }
}
