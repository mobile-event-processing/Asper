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
 * Context partition identifier for category context.
 */
public class ContextPartitionIdentifierCategory extends ContextPartitionIdentifier {
    private String label;

    /**
     * Returns the category label.
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the category label.
     * @param label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
