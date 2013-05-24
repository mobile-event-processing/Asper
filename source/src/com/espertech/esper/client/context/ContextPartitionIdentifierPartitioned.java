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
 * Context partition identifier for segmented contexts.
 */
public class ContextPartitionIdentifierPartitioned extends ContextPartitionIdentifier {
    private Object[] keys;

    /**
     * Returns the partition keys.
     * @return keys
     */
    public Object[] getKeys() {
        return keys;
    }

    /**
     * Sets the partition keys.
     * @param keys to set
     */
    public void setKeys(Object[] keys) {
        this.keys = keys;
    }
}
