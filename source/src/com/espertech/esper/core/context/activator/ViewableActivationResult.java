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

package com.espertech.esper.core.context.activator;

import com.espertech.esper.core.service.StatementAgentInstanceLock;
import com.espertech.esper.pattern.EvalRootState;
import com.espertech.esper.pattern.EvalRootStateNode;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.Viewable;

public class ViewableActivationResult {
    private final Viewable viewable;
    private final StopCallback stopCallback;
    private final StatementAgentInstanceLock optionalLock;
    private final EvalRootState optionalPatternRoot;

    public ViewableActivationResult(Viewable viewable, StopCallback stopCallback, StatementAgentInstanceLock optionalLock, EvalRootState optionalPatternRoot) {
        this.viewable = viewable;
        this.stopCallback = stopCallback;
        this.optionalLock = optionalLock;
        this.optionalPatternRoot = optionalPatternRoot;
    }

    public StopCallback getStopCallback() {
        return stopCallback;
    }

    public Viewable getViewable() {
        return viewable;
    }

    public StatementAgentInstanceLock getOptionalLock() {
        return optionalLock;
    }

    public EvalRootState getOptionalPatternRoot() {
        return optionalPatternRoot;
    }
}
