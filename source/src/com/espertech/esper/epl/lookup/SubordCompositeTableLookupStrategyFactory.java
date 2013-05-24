/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.lookup;

import com.espertech.esper.epl.join.exec.composite.CompositeIndexQuery;
import com.espertech.esper.epl.join.exec.composite.CompositeIndexQueryFactory;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.PropertyCompositeEventTable;

import java.util.Collection;

/**
 * Index lookup strategy for subqueries.
 */
public class SubordCompositeTableLookupStrategyFactory implements SubordTableLookupStrategyFactory
{
    private final CompositeIndexQuery innerIndexQuery;
    private final Collection<SubordPropRangeKey> rangeDescs;

    public SubordCompositeTableLookupStrategyFactory(boolean isNWOnTrigger, int numStreams, Collection<SubordPropHashKey> keyExpr, Class[] coercionKeyTypes, Collection<SubordPropRangeKey> rangeProps, Class[] coercionRangeTypes) {
        this.rangeDescs = rangeProps;
        this.innerIndexQuery = CompositeIndexQueryFactory.makeSubordinate(isNWOnTrigger, numStreams, keyExpr, coercionKeyTypes, rangeProps, coercionRangeTypes);
    }

    public SubordTableLookupStrategy makeStrategy(EventTable eventTable) {
        return new SubordCompositeTableLookupStrategy(innerIndexQuery, (PropertyCompositeEventTable) eventTable);
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " ranges=" + SubordPropRangeKey.toQueryPlan(rangeDescs);
    }

}
