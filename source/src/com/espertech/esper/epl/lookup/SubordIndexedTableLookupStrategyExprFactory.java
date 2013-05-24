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
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.PropertyIndexedEventTable;

import java.util.List;

/**
 * Index lookup strategy for subqueries.
 */
public class SubordIndexedTableLookupStrategyExprFactory implements SubordTableLookupStrategyFactory
{
    protected final ExprEvaluator[] evaluators;
    protected final boolean isNWOnTrigger;
    protected final int numStreamsOuter;

    public SubordIndexedTableLookupStrategyExprFactory(boolean isNWOnTrigger, int numStreamsOuter, List<SubordPropHashKey> hashKeys)
    {
        evaluators = new ExprEvaluator[hashKeys.size()];
        for (int i = 0; i < hashKeys.size(); i++) {
            evaluators[i] = hashKeys.get(i).getHashKey().getKeyExpr().getExprEvaluator();
        }
        this.isNWOnTrigger = isNWOnTrigger;
        this.numStreamsOuter = numStreamsOuter;
    }

    public SubordTableLookupStrategy makeStrategy(EventTable eventTable) {
        if (isNWOnTrigger) {
            return new SubordIndexedTableLookupStrategyExprNW(evaluators, (PropertyIndexedEventTable) eventTable);
        }
        else {
            return new SubordIndexedTableLookupStrategyExpr(numStreamsOuter, evaluators, (PropertyIndexedEventTable) eventTable);
        }
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " evaluators " + ExprNodeUtility.printEvaluators(evaluators);
    }
}
