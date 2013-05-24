/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is always the root node in the evaluation tree representing an event expression.
 * It hold the handle to the EPStatement implementation for notifying when matches are found.
 */
public class EvalRootFactoryNode extends EvalNodeFactoryBase
{
    private static final long serialVersionUID = -4478876398666926782L;

    public EvalRootFactoryNode() {
    }

    public EvalNode makeEvalNode(PatternAgentInstanceContext agentInstanceContext) {
        return makeEvalNodeRoot(agentInstanceContext);
    }

    public EvalRootNode makeEvalNodeRoot(PatternAgentInstanceContext agentInstanceContext) {
        EvalNode child = EvalNodeUtil.makeEvalNodeSingleChild(this.getChildNodes(), agentInstanceContext);
        return new EvalRootNode(agentInstanceContext, this, child);
    }

    public final String toString()
    {
        return ("EvalRootNode children=" + this.getChildNodes().size());
    }

    public boolean isFilterChildNonQuitting() {
        return false;
    }

    public boolean isStateful() {
        return this.getChildNodes().get(0).isStateful();
    }

    private static final Log log = LogFactory.getLog(EvalRootFactoryNode.class);
}
