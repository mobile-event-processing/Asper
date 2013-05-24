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
 * This class represents an 'or' operator in the evaluation tree representing any event expressions.
 */
public class EvalOrFactoryNode extends EvalNodeFactoryBase
{
    private static final long serialVersionUID = -771361274781500482L;

    protected EvalOrFactoryNode() {
    }

    public EvalNode makeEvalNode(PatternAgentInstanceContext agentInstanceContext) {
        EvalNode[] children = EvalNodeUtil.makeEvalNodeChildren(this.getChildNodes(), agentInstanceContext);
        return new EvalOrNode(agentInstanceContext, this, children);
    }

    public final String toString()
    {
        return ("EvalOrNode children=" + this.getChildNodes().size());
    }

    public boolean isFilterChildNonQuitting() {
        return false;
    }

    public boolean isStateful() {
        for (EvalFactoryNode child : this.getChildNodes()) {
            if (child.isStateful()) {
                return true;
            }
        }
        return false;
    }

    private static final Log log = LogFactory.getLog(EvalOrFactoryNode.class);
}
