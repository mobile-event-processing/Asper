/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.service;

import com.espertech.esper.core.context.factory.StatementAgentInstanceFactoryResult;
import com.espertech.esper.core.context.mgr.ContextStatePathKey;
import com.espertech.esper.pattern.EvalRootState;

/**
 * Statement-level extension services.
 */
public interface StatementExtensionSvcContext
{
    public void startContextPartition(StatementAgentInstanceFactoryResult startResult, int agentInstanceId);
    public void endContextPartition(int agentInstanceId);

    public void startContextPattern(EvalRootState patternStopCallback, boolean startEndpoint, ContextStatePathKey path);
    public void stopContextPattern(EvalRootState patternStopCallback, boolean startEndpoint, ContextStatePathKey path);
}
