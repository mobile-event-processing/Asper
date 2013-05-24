/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.filter;

import com.espertech.esper.client.EventPropertyGetter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Index for filter parameter constants to match using the equals (=) operator.
 * The implementation is based on a regular HashMap.
 */
public abstract class FilterParamIndexEqualsBase extends FilterParamIndexLookupableBase
{
    protected final Map<Object, EventEvaluator> constantsMap;
    protected final ReadWriteLock constantsMapRWLock;

    protected FilterParamIndexEqualsBase(FilterSpecLookupable lookupable, FilterOperator filterOperator) {
        super(filterOperator, lookupable);

        constantsMap = new HashMap<Object, EventEvaluator>();
        constantsMapRWLock = new ReentrantReadWriteLock();
    }

    public final EventEvaluator get(Object filterConstant)
    {
        return constantsMap.get(filterConstant);
    }

    public final void put(Object filterConstant, EventEvaluator evaluator)
    {
        constantsMap.put(filterConstant, evaluator);
    }

    public final boolean remove(Object filterConstant)
    {
        if (constantsMap.remove(filterConstant) == null)
        {
            return false;
        }
        return true;
    }

    public final int size()
    {
        return constantsMap.size();
    }

    public final ReadWriteLock getReadWriteLock()
    {
        return constantsMapRWLock;
    }
}
