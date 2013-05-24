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
 * Context partition identifier for hash context.
 */
public class ContextPartitionIdentifierHash extends ContextPartitionIdentifier {
    private int hash;

    /**
     * Returns the hash code.
     * @return hash code
     */
    public int getHash() {
        return hash;
    }

    /**
     * Sets the hash code.
     * @param hash hash code
     */
    public void setHash(int hash) {
        this.hash = hash;
    }
}
