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

import java.util.List;
import java.util.Map;

public class ContextControllerTreeAgentInstanceList {

    private long filterVersionAfterAllocation;
    private Object initPartitionKey;
    private Map<String, Object> initContextProperties;
    private List<AgentInstance> agentInstances;

    public ContextControllerTreeAgentInstanceList(long filterVersionAfterAllocation, Object initPartitionKey, Map<String, Object> initContextProperties, List<AgentInstance> agentInstances) {
        this.filterVersionAfterAllocation = filterVersionAfterAllocation;
        this.initPartitionKey = initPartitionKey;
        this.initContextProperties = initContextProperties;
        this.agentInstances = agentInstances;
    }

    public long getFilterVersionAfterAllocation() {
        return filterVersionAfterAllocation;
    }

    public Object getInitPartitionKey() {
        return initPartitionKey;
    }

    public Map<String, Object> getInitContextProperties() {
        return initContextProperties;
    }

    public List<AgentInstance> getAgentInstances() {
        return agentInstances;
    }
}
