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

package com.espertech.esper.core.context.schedule;

import com.espertech.esper.core.context.util.EPStatementAgentInstanceHandle;

public interface SchedulableAgentInstanceDirectory {
    public void add(EPStatementAgentInstanceHandle handle);
    public void remove(String statementId, int agentInstanceId);
    public void removeStatement(String statementId);
    public EPStatementAgentInstanceHandle lookup(String statementId, int agentInstanceId);
}
