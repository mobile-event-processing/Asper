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

import java.util.TreeMap;

public class ContextStateCacheNoSave implements ContextStateCache {

    public ContextStatePathValueBinding getBinding(Object bindingInfo) {
        return null; // no binding required
    }

    public void addContextPath(String contextName, int level, int parentPath, int subPath, Integer optionalContextPartitionId, Object additionalInfo, ContextStatePathValueBinding binding) {
        // no action required
    }

    public void removeContextParentPath(String contextName, int level, int parentPath) {
        // no action required
    }

    public void removeContextPath(String contextName, int level, int parentPath, int subPath) {
        // no action required
    }

    public TreeMap<ContextStatePathKey, ContextStatePathValue> getContextPaths(String contextName) {
        return null; // no state required
    }

    public void removeContext(String contextName) {
        // no action required
    }
}
