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

package com.espertech.esper.regression.context;

import com.espertech.esper.client.context.ContextPartitionSelectorById;

import java.util.Set;

public class SupportSelectorById implements ContextPartitionSelectorById {
    private final Set<Integer> ids;

    public SupportSelectorById(Set<Integer> ids) {
        this.ids = ids;
    }

    public Set<Integer> getContextPartitionIds() {
        return ids;
    }
}
