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
 * This class represents an 'not' operator in the evaluation tree representing any event expressions.
 */
public class EvalNotFactoryNode extends EvalNodeFactoryBase
{
    private static final long serialVersionUID = 2768112579538392761L;

    protected EvalNotFactoryNode() {
    }

    public EvalNode makeEvalNode(PatternAgentInstanceContext agentInstanceContext) {
        EvalNode child = EvalNodeUtil.makeEvalNodeSingleChild(this.getChildNodes(), agentInstanceContext);
        return new EvalNotNode(agentInstanceContext, this, child);
    }

    public final String toString()
    {
        return "EvalNotNode children=" + this.getChildNodes().size();
    }

    public boolean isFilterChildNonQuitting() {
        return false;
    }

    public boolean isStateful() {
        return true;
    }

    private static final Log log = LogFactory.getLog(EvalNotFactoryNode.class);
}
