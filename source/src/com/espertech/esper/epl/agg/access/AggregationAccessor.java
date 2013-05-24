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

import java.util.Collection;

/**
 * Accessor for first/last/window access aggregation functions.
 */
public interface AggregationAccessor
{
    /**
     * Returns the value for a first/last/window access aggregation function.
     * @param access access
     * @return value
     */
    public Object getValue(AggregationAccess access);

    public Collection<EventBean> getCollectionReadOnly(AggregationAccess access);

    public EventBean getEventBean(AggregationAccess currentAcces);
}
