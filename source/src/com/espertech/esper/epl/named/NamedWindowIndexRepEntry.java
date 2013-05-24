/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.epl.join.table.EventTable;

public class NamedWindowIndexRepEntry
{
    private final EventTable table;
    private final String optionalIndexName;
    private int refCount;

    public NamedWindowIndexRepEntry(EventTable table, String optionalIndexName, int refCount) {
        this.table = table;
        this.optionalIndexName = optionalIndexName;
        this.refCount = refCount;
    }

    public EventTable getTable() {
        return table;
    }

    public String getOptionalIndexName() {
        return optionalIndexName;
    }

    public int getRefCount() {
        return refCount;
    }

    public void setRefCount(int refCount) {
        this.refCount = refCount;
    }
}
