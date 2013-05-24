/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.plan;

import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.join.exec.base.IndexedTableLookupStrategy;
import com.espertech.esper.epl.join.exec.base.IndexedTableLookupStrategyExpr;
import com.espertech.esper.epl.join.exec.base.JoinExecTableLookupStrategy;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.PropertyIndexedEventTable;

import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Plan to perform an indexed table lookup.
 */
public class IndexedTableLookupPlanMulti extends TableLookupPlan
{
    private List<QueryGraphValueEntryHashKeyed> keyProperties;

    /**
     * Ctor.
     * @param lookupStream - stream that generates event to look up for
     * @param indexedStream - stream to index table lookup
     * @param indexNum - index number for the table containing the full unindexed contents
     * @param keyProperties - properties to use in lookup event to access index
     */
    public IndexedTableLookupPlanMulti(int lookupStream, int indexedStream, String indexNum, List<QueryGraphValueEntryHashKeyed> keyProperties)
    {
        super(lookupStream, indexedStream, indexNum);
        this.keyProperties = keyProperties;
    }

    public TableLookupKeyDesc getKeyDescriptor() {
        return new TableLookupKeyDesc(keyProperties, Collections.<QueryGraphValueEntryRange>emptyList());
    }

    public JoinExecTableLookupStrategy makeStrategyInternal(EventTable eventTable, EventType[] eventTypes)
    {
        PropertyIndexedEventTable index = (PropertyIndexedEventTable) eventTable;
        String[] keyProps = new String[keyProperties.size()];
        ExprEvaluator[] evaluators = new ExprEvaluator[keyProperties.size()];
        boolean isStrictlyProps = true;
        for (int i = 0; i < keyProps.length; i++) {
            isStrictlyProps = isStrictlyProps && keyProperties.get(i) instanceof QueryGraphValueEntryHashKeyedProp;
            evaluators[i] = keyProperties.get(i).getKeyExpr().getExprEvaluator();

            if (keyProperties.get(i) instanceof QueryGraphValueEntryHashKeyedProp) {
                keyProps[i] = ((QueryGraphValueEntryHashKeyedProp) keyProperties.get(i)).getKeyProperty();
            }
            else {
                isStrictlyProps = false;
            }
        }
        if (isStrictlyProps) {
            return new IndexedTableLookupStrategy(eventTypes[this.getLookupStream()], keyProps, index);
        }
        else {
            return new IndexedTableLookupStrategyExpr(evaluators, getLookupStream(), index);
        }            
    }

    public String toString()
    {
        return this.getClass().getSimpleName() + " " +
                super.toString() +
               " keyProperties=" + QueryGraphValueEntryHashKeyed.toQueryPlan(keyProperties);
    }
}
