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

package com.espertech.esper.client.context;

/**
 * Context partition identifiers are provided by the API when interrogating context partitions for a given statement.
 */
public abstract class ContextPartitionIdentifier {
    private Integer contextPartitionId;

    /**
     * Ctor.
     */
    public ContextPartitionIdentifier() {
    }

    /**
     * Returns the context partition id.
     * @return context partition id
     */
    public Integer getContextPartitionId() {
        return contextPartitionId;
    }

    /**
     * Sets the context partition id - this set method is for engine-internal use.
     * @param contextPartitionId context partition id
     */
    public void setContextPartitionId(Integer contextPartitionId) {
        this.contextPartitionId = contextPartitionId;
    }
}
