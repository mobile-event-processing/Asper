/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.core.context.factory.StatementAgentInstancePostLoad;
import com.espertech.esper.core.context.util.AgentInstanceContext;

/**
 * An instance of this class is associated with a specific named window. The processor
 * provides the views to create-window, on-delete statements and statements selecting from a named window.
 */
public class NamedWindowProcessorInstance
{
    private final Integer agentInstanceId;
    private final NamedWindowTailViewInstance tailViewInstance;
    private final NamedWindowRootViewInstance rootViewInstance;

    public NamedWindowProcessorInstance(Integer agentInstanceId, NamedWindowProcessor processor, AgentInstanceContext agentInstanceContext) {
        this.agentInstanceId = agentInstanceId;
        rootViewInstance = new NamedWindowRootViewInstance(processor.getRootView(), agentInstanceContext);
        tailViewInstance = new NamedWindowTailViewInstance(rootViewInstance, processor.getTailView(), agentInstanceContext);
        rootViewInstance.setDataWindowContents(tailViewInstance);   // for iteration used for delete without index
    }

    public NamedWindowTailViewInstance getTailViewInstance() {
        return tailViewInstance;
    }

    public NamedWindowRootViewInstance getRootViewInstance() {
        return rootViewInstance;
    }

    /**
     * Returns the number of events held.
     * @return number of events
     */
    public long getCountDataWindow()
    {
        return tailViewInstance.getNumberOfEvents();
    }

    /**
     * Deletes a named window and removes any associated resources.
     */
    public void destroy()
    {
        tailViewInstance.destroy();
        rootViewInstance.destroy();
    }

    public IndexMultiKey[] getIndexDescriptors() {
        return rootViewInstance.getIndexes();
    }

    public Integer getAgentInstanceId() {
        return agentInstanceId;
    }

    public StatementAgentInstancePostLoad getPostLoad() {
        return new StatementAgentInstancePostLoad() {
            public void executePostLoad() {
                rootViewInstance.postLoad();
            }
        };
    }
}
