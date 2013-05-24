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
 * Context partition identifier for nested contexts.
 */
public class ContextPartitionIdentifierNested extends ContextPartitionIdentifier {
    private ContextPartitionIdentifier[] identifiers;

    /**
     * Returns nested partition identifiers.
     * @return identifiers
     */
    public ContextPartitionIdentifier[] getIdentifiers() {
        return identifiers;
    }

    /**
     * Sets nested partition identifiers.
     * @param identifiers identifiers
     */
    public void setIdentifiers(ContextPartitionIdentifier[] identifiers) {
        this.identifiers = identifiers;
    }
}
