/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.plan;

import com.espertech.esper.epl.join.exec.base.JoinExecTableLookupStrategy;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.virtualdw.VirtualDWView;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Abstract specification on how to perform a table lookup.
 */
public abstract class TableLookupPlan
{
    private int lookupStream;
    private int indexedStream;
    private String indexNum;

    public abstract JoinExecTableLookupStrategy makeStrategyInternal(EventTable eventTable, EventType[] eventTypes);
    public abstract TableLookupKeyDesc getKeyDescriptor();

    /**
     * Instantiates the lookup plan into a execution strategy for the lookup.
     * @param indexesPerStream - tables for each stream
     * @param eventTypes - types of events in stream
     * @return lookup strategy instance
     */
    public final JoinExecTableLookupStrategy makeStrategy(String statementName, String statementId, Annotation[] accessedByStmtAnnotations, Map<String,EventTable>[] indexesPerStream, EventType[] eventTypes, VirtualDWView[] viewExternals) {
        EventTable eventTable = indexesPerStream[this.getIndexedStream()].get(getIndexNum());
        if (viewExternals[indexedStream] != null) {
            return viewExternals[indexedStream].getJoinLookupStrategy(statementName, statementId, accessedByStmtAnnotations, eventTable, getKeyDescriptor(), lookupStream);
        }
        return makeStrategyInternal(eventTable, eventTypes);
    }

    /**
     * Ctor.
     * @param lookupStream - stream number of stream that supplies event to be used to look up
     * @param indexedStream - - stream number of stream that is being access via index/table
     * @param indexNum - index to use for lookup
     */
    protected TableLookupPlan(int lookupStream, int indexedStream, String indexNum)
    {
        this.lookupStream = lookupStream;
        this.indexedStream = indexedStream;
        this.indexNum = indexNum;
    }

    /**
     * Returns the lookup stream.
     * @return lookup stream
     */
    public int getLookupStream()
    {
        return lookupStream;
    }

    /**
     * Returns indexed stream.
     * @return indexed stream
     */
    public int getIndexedStream()
    {
        return indexedStream;
    }

    /**
     * Returns index number to use for looking up in.
     * @return index number
     */
    public String getIndexNum()
    {
        return indexNum;
    }

    public String toString()
    {
        return "lookupStream=" + lookupStream +
               " indexedStream=" + indexedStream +
               " indexNum=" + indexNum;
    }
}
