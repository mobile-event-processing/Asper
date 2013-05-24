/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.lookup.SubordTableLookupStrategy;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Uses an index to determine event to be deleted or selected from a named window.
 */
public class NamedWindowLookupStrategyIndexed implements NamedWindowLookupStrategy
{
    private final ExprEvaluator joinExpr;
    private final EventBean[] eventsPerStream;
    private final SubordTableLookupStrategy tableLookupStrategy;

    /**
     * Ctor.
     * @param joinExpr the validated where clause of the on-delete
     * @param tableLookupStrategy the strategy for looking up in an index the matching events using correlation
     */
    public NamedWindowLookupStrategyIndexed(ExprEvaluator joinExpr, SubordTableLookupStrategy tableLookupStrategy)
    {
        this.joinExpr = joinExpr;
        this.eventsPerStream = new EventBean[2];
        this.tableLookupStrategy = tableLookupStrategy;
    }

    public EventBean[] lookup(EventBean[] newData, ExprEvaluatorContext exprEvaluatorContext)
    {
        Set<EventBean> removeEvents = null;

        // For every new event (usually 1)
        for (EventBean newEvent : newData)
        {
            eventsPerStream[1] = newEvent;

            // use index to find match
            Collection<EventBean> matches = tableLookupStrategy.lookup(eventsPerStream, exprEvaluatorContext);
            if ((matches == null) || (matches.isEmpty()))
            {
                continue;
            }

            // evaluate expression
            Iterator<EventBean> eventsIt = matches.iterator();
            for (;eventsIt.hasNext();)
            {
                eventsPerStream[0] = eventsIt.next();   // next named window event

                for (EventBean aNewData : newData)
                {
                    eventsPerStream[1] = aNewData;    // Stream 1 events are the originating events (on-delete events)

                    Boolean result = (Boolean) joinExpr.evaluate(eventsPerStream, true, exprEvaluatorContext);
                    if (result != null)
                    {
                        if (result)
                        {
                            if (removeEvents == null)
                            {
                                removeEvents = new LinkedHashSet<EventBean>();
                            }
                            removeEvents.add(eventsPerStream[0]);
                        }
                    }
                }
            }
        }

        if (removeEvents == null)
        {
            return null;
        }

        return removeEvents.toArray(new EventBean[removeEvents.size()]);
    }

    public String toString() {
        return toQueryPlan();
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " " + " strategy " + tableLookupStrategy.toQueryPlan();
    }
}
