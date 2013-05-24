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
import com.espertech.esper.core.service.StatementAgentInstanceLock;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

import java.util.ArrayDeque;
import java.util.Collection;

/**
 * Index lookup strategy for subqueries.
 */
public class SubordFullTableScanLookupStrategyLocking implements SubordTableLookupStrategy
{
    private final Iterable<EventBean> contents;
    private final StatementAgentInstanceLock statementLock;

    public SubordFullTableScanLookupStrategyLocking(Iterable<EventBean> contents, StatementAgentInstanceLock statementLock) {
        this.contents = contents;
        this.statementLock = statementLock;
    }

    @Override
    public Collection<EventBean> lookup(EventBean[] events, ExprEvaluatorContext context) {
        return lookupInternal();
    }

    public Collection<EventBean> lookup(Object[] keys) {
        return lookupInternal();
    }

    private Collection<EventBean> lookupInternal() {
        statementLock.acquireReadLock();
        try {
            ArrayDeque<EventBean> result = new ArrayDeque<EventBean>();
            for (EventBean eventBean : contents) {
                result.add(eventBean);
            }
            return result;
        }
        finally {
            statementLock.releaseReadLock();
        }
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName();
    }
}
