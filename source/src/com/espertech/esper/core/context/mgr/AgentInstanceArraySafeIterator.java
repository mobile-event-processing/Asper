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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.SafeIterator;
import com.espertech.esper.core.service.StatementAgentInstanceLock;

public class AgentInstanceArraySafeIterator extends AgentInstanceArrayIterator implements SafeIterator<EventBean> {

    public AgentInstanceArraySafeIterator(AgentInstance[] instances) {
        super(instances);
        for (int i = 0; i < instances.length; i++) {
            StatementAgentInstanceLock instanceLock = instances[i].getAgentInstanceContext().getEpStatementAgentInstanceHandle().getStatementAgentInstanceLock();
            instanceLock.acquireWriteLock(null);
        }
    }

    public void close() {
        for (int i = 0; i < instances.length; i++) {
            StatementAgentInstanceLock instanceLock = instances[i].getAgentInstanceContext().getEpStatementAgentInstanceHandle().getStatementAgentInstanceLock();
            instanceLock.releaseWriteLock(null);
        }
    }
}
