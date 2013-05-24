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
import com.espertech.esper.epl.join.table.PropertyIndexedEventTableSingle;

/**
 * Index lookup strategy that coerces the key values before performing a lookup.
 */
public class SubordIndexedTableLookupStrategySingleCoercingFactory extends SubordIndexedTableLookupStrategySingleExprFactory
{
    private Class coercionType;

    /**
     * Ctor.
     */
    public SubordIndexedTableLookupStrategySingleCoercingFactory(boolean isNWOnTrigger, int streamCountOuter, SubordPropHashKey hashKey, Class coercionType) {
        super(isNWOnTrigger, streamCountOuter, hashKey);
        this.coercionType = coercionType;
    }

    @Override
    public SubordTableLookupStrategy makeStrategy(EventTable eventTable) {
        if (isNWOnTrigger) {
            return new SubordIndexedTableLookupStrategySingleCoercingNW(evaluator, (PropertyIndexedEventTableSingle) eventTable, coercionType);
        }
        else {
            return new SubordIndexedTableLookupStrategySingleCoercing(streamCountOuter, evaluator, (PropertyIndexedEventTableSingle) eventTable, coercionType);
        }
    }
}
