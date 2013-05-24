/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.start;

import com.espertech.esper.core.context.util.StatementAgentInstanceUtil;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.util.StopCallback;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Method to call to stop an EPStatement.
 */
public class EPStatementStopMethodImpl implements EPStatementStopMethod
{
    private static final Log log = LogFactory.getLog(EPStatementStopMethodImpl.class);

    private final StatementContext statementContext;
    private final List<StopCallback> stopCallbacks;

    public EPStatementStopMethodImpl(StatementContext statementContext, List<StopCallback> stopCallbacks) {
        this.statementContext = statementContext;
        this.stopCallbacks = stopCallbacks;
    }

    public void stop() {
        for (StopCallback stopCallback : stopCallbacks){
            StatementAgentInstanceUtil.stopSafe(stopCallback, statementContext);
        }
    }
}
