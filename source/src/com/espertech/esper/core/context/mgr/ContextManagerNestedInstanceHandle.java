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

public class ContextManagerNestedInstanceHandle implements ContextControllerInstanceHandle {
    private final ContextController controller;
    private final int contextPartitionOrPathId;
    private final boolean branch;

    public ContextManagerNestedInstanceHandle(ContextController controller, int contextPartitionOrPathId, boolean branch) {
        this.controller = controller;
        this.contextPartitionOrPathId = contextPartitionOrPathId;
        this.branch = branch;
    }

    public ContextController getController() {
        return controller;
    }

    public Integer getContextPartitionOrPathId() {
        return contextPartitionOrPathId;
    }

    public boolean isBranch() {
        return branch;
    }
}
