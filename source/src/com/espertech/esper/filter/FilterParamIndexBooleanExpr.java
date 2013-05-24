/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.filter;

import com.espertech.esper.client.EventBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Index that simply maintains a list of boolean expressions.
 */
public final class FilterParamIndexBooleanExpr extends FilterParamIndexBase
{
    private final Map<ExprNodeAdapterBase, EventEvaluator> evaluatorsMap;
    private final ReadWriteLock constantsMapRWLock;

    /**
     * Constructs the index for multiple-exact matches.
     */
    public FilterParamIndexBooleanExpr()
    {
        super(FilterOperator.BOOLEAN_EXPRESSION);

        evaluatorsMap = new LinkedHashMap<ExprNodeAdapterBase, EventEvaluator>();
        constantsMapRWLock = new ReentrantReadWriteLock();
    }

    public final EventEvaluator get(Object filterConstant)
    {
        ExprNodeAdapterBase keyValues = (ExprNodeAdapterBase) filterConstant;
        return evaluatorsMap.get(keyValues);
    }

    public final void put(Object filterConstant, EventEvaluator evaluator)
    {
        ExprNodeAdapterBase keys = (ExprNodeAdapterBase) filterConstant;
        evaluatorsMap.put(keys, evaluator);
    }

    public final boolean remove(Object filterConstant)
    {
        ExprNodeAdapterBase keys = (ExprNodeAdapterBase) filterConstant;
        return evaluatorsMap.remove(keys) != null;
    }

    public final int size()
    {
        return evaluatorsMap.size();
    }

    public final ReadWriteLock getReadWriteLock()
    {
        return constantsMapRWLock;
    }

    public final void matchEvent(EventBean theEvent, Collection<FilterHandle> matches)
    {
        constantsMapRWLock.readLock().lock();
        try {
            for (Map.Entry<ExprNodeAdapterBase, EventEvaluator> evals : evaluatorsMap.entrySet())
            {
                if (evals.getKey().evaluate(theEvent))
                {
                    evals.getValue().matchEvent(theEvent, matches);
                }
            }
        }
        finally {
            constantsMapRWLock.readLock().unlock();
        }
    }

    private static final Log log = LogFactory.getLog(FilterParamIndexBooleanExpr.class);
}
