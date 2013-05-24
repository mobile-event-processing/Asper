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

package com.espertech.esper.epl.virtualdw;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.hook.VirtualDataWindowLookupFieldDesc;
import com.espertech.esper.epl.join.table.EventTable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class VirtualDWEventTable implements EventTable
{
    private boolean unique;
    private List<VirtualDataWindowLookupFieldDesc> hashAccess;
    private List<VirtualDataWindowLookupFieldDesc> btreeAccess;

    public VirtualDWEventTable(boolean unique, List<VirtualDataWindowLookupFieldDesc> hashAccess, List<VirtualDataWindowLookupFieldDesc> btreeAccess) {
        this.unique = unique;
        this.hashAccess = Collections.unmodifiableList(hashAccess);
        this.btreeAccess = Collections.unmodifiableList(btreeAccess);
    }

    public void addRemove(EventBean[] newData, EventBean[] oldData) {
        add(newData);
        remove(oldData);
    }

    public void add(EventBean[] events) {
    }

    public void remove(EventBean[] events) {
    }

    public Iterator<EventBean> iterator() {
        return Collections.<EventBean>emptyList().iterator();
    }

    public boolean isEmpty() {
        return true;
    }

    public void clear() {
    }

    public String toQueryPlan() {
        return "(external event table)";
    }

    public List<VirtualDataWindowLookupFieldDesc> getHashAccess() {
        return hashAccess;
    }

    public List<VirtualDataWindowLookupFieldDesc> getBtreeAccess() {
        return btreeAccess;
    }

    public EventTable copyShallow() {
        throw new UnsupportedOperationException();
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }
}
