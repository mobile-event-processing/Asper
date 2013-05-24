/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.lookup;

import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.PropertyIndexedEventTableSingle;

/**
 * Index lookup strategy for subqueries.
 */
public class SubordIndexedTableLookupStrategySingleExprFactory implements SubordTableLookupStrategyFactory
{
    protected final ExprEvaluator evaluator;
    protected boolean isNWOnTrigger;
    protected int streamCountOuter;

    public SubordIndexedTableLookupStrategySingleExprFactory(boolean isNWOnTrigger, int streamCountOuter, SubordPropHashKey hashKey)
    {
        this.streamCountOuter = streamCountOuter;
        this.evaluator = hashKey.getHashKey().getKeyExpr().getExprEvaluator();
        this.isNWOnTrigger = isNWOnTrigger;
    }

    public SubordTableLookupStrategy makeStrategy(EventTable eventTable) {
        if (isNWOnTrigger) {
            return new SubordIndexedTableLookupStrategySingleExprNW(evaluator, (PropertyIndexedEventTableSingle) eventTable);
        }
        else {
            return new SubordIndexedTableLookupStrategySingleExpr(streamCountOuter, evaluator, (PropertyIndexedEventTableSingle) eventTable);
        }
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " evaluator " + evaluator.getClass().getSimpleName();
    }
}
