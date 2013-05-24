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

package com.espertech.esper.core.context.factory;

import com.espertech.esper.core.context.subselect.SubSelectStrategyHolder;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.Viewable;

import java.util.Collections;

public class StatementAgentInstanceFactoryCreateWindowResult extends StatementAgentInstanceFactoryResult {

    private final Viewable eventStreamParentViewable;
    private final StatementAgentInstancePostLoad postLoad;
    private final Viewable topView;

    public StatementAgentInstanceFactoryCreateWindowResult(Viewable finalView, StopCallback stopCallback, AgentInstanceContext agentInstanceContext, Viewable eventStreamParentViewable, StatementAgentInstancePostLoad postLoad, Viewable topView) {
        super(finalView, stopCallback, agentInstanceContext,
                    null, Collections.<ExprSubselectNode, SubSelectStrategyHolder>emptyMap(),
                Collections.<ExprPriorNode, ExprPriorEvalStrategy>emptyMap(), Collections.<ExprPreviousNode, ExprPreviousEvalStrategy>emptyMap(),
                Collections.<StatementAgentInstancePreload>emptyList()
        );
        this.eventStreamParentViewable = eventStreamParentViewable;
        this.postLoad = postLoad;
        this.topView = topView;
    }

    public Viewable getEventStreamParentViewable() {
        return eventStreamParentViewable;
    }

    public StatementAgentInstancePostLoad getPostLoad() {
        return postLoad;
    }

    public Viewable getTopView() {
        return topView;
    }
}
