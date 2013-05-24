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

package com.espertech.esper.core.context.mgr;

public class ContextStatePathValue {
    private final Integer optionalContextPartitionId;
    private final byte[] blob;

    public ContextStatePathValue(Integer optionalContextPartitionId, byte[] blob) {
        this.optionalContextPartitionId = optionalContextPartitionId;
        this.blob = blob;
    }

    public Integer getOptionalContextPartitionId() {
        return optionalContextPartitionId;
    }

    public byte[] getBlob() {
        return blob;
    }

    public String toString() {
        return "ContextStatePathValue{" +
                "optionalContextPartitionId=" + optionalContextPartitionId +
                ", blob=" + blob +
                '}';
    }
}

