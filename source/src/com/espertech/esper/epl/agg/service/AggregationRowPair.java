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

package com.espertech.esper.epl.agg.service;

import com.espertech.esper.epl.agg.access.AggregationAccess;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;

/**
 * Pair of aggregation methods and accesses (first/last/window) data window representations.
 */
public class AggregationRowPair
{
    private final AggregationMethod[] methods;
    private final AggregationAccess[] accesses;

    /**
     * Ctor.
     * @param methods aggregation methods/state
     * @param accesses access is data window representations
     */
    public AggregationRowPair(AggregationMethod[] methods, AggregationAccess[] accesses)
    {
        this.methods = methods;
        this.accesses = accesses;
    }

    /**
     * Returns aggregation methods.
     * @return aggregation methods
     */
    public AggregationMethod[] getMethods()
    {
        return methods;
    }

    /**
     * Returns accesses to data window state.
     * @return accesses
     */
    public AggregationAccess[] getAccesses()
    {
        return accesses;
    }
}
