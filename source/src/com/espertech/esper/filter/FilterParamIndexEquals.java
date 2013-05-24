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
import com.espertech.esper.client.EventPropertyGetter;

import java.util.Collection;

/**
 * Index for filter parameter constants to match using the equals (=) operator.
 * The implementation is based on a regular HashMap.
 */
public final class FilterParamIndexEquals extends FilterParamIndexEqualsBase
{
    public FilterParamIndexEquals(FilterSpecLookupable lookupable) {
        super(lookupable, FilterOperator.EQUAL);
    }

    public final void matchEvent(EventBean theEvent, Collection<FilterHandle> matches)
    {
        Object attributeValue = lookupable.getGetter().get(theEvent);
        if (attributeValue == null) {   //  null cannot match, not even null: requires use of "is"
            return;
        }

        // Look up in hashtable
        EventEvaluator evaluator = null;
        constantsMapRWLock.readLock().lock();
        try
        {
            evaluator = constantsMap.get(attributeValue);
        }
        finally
        {
            constantsMapRWLock.readLock().unlock();
        }

        // No listener found for the value, return
        if (evaluator == null)
        {
            return;
        }

        evaluator.matchEvent(theEvent, matches);
    }
}
