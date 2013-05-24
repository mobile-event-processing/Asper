/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.lookup;

import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.PropertyIndexedEventTable;

import java.util.List;

/**
 * Index lookup strategy that coerces the key values before performing a lookup.
 */
public class SubordIndexedTableLookupStrategyCoercingFactory extends SubordIndexedTableLookupStrategyExprFactory
{
    private Class[] coercionTypes;

    public SubordIndexedTableLookupStrategyCoercingFactory(boolean isNWOnTrigger, int numStreamsOuter, List<SubordPropHashKey> hashKeys, Class[] coercionTypes) {
        super(isNWOnTrigger, numStreamsOuter, hashKeys);
        this.coercionTypes = coercionTypes;
    }

    @Override
    public SubordTableLookupStrategy makeStrategy(EventTable eventTable) {
        if (isNWOnTrigger) {
            return new SubordIndexedTableLookupStrategyCoercingNW(evaluators, (PropertyIndexedEventTable) eventTable, coercionTypes);
        }
        else {
            return new SubordIndexedTableLookupStrategyCoercing(numStreamsOuter, evaluators, (PropertyIndexedEventTable) eventTable, coercionTypes);
        }
    }

}
