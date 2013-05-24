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
 * This class represents an 'and' operator in the evaluation tree representing an event expressions.
 */
public class EvalAndFactoryNode extends EvalNodeFactoryBase
{
    private static final long serialVersionUID = -7065465204506721233L;

    protected EvalAndFactoryNode() {
    }

    public EvalNode makeEvalNode(PatternAgentInstanceContext agentInstanceContext) {
        EvalNode[] children = EvalNodeUtil.makeEvalNodeChildren(this.getChildNodes(), agentInstanceContext);
        return new EvalAndNode(agentInstanceContext, this, children);
    }

    public final String toString()
    {
        return ("EvalAndFactoryNode children=" + this.getChildNodes().size());
    }

    public boolean isFilterChildNonQuitting() {
        return false;
    }

    public boolean isStateful() {
        return true;
    }

    private static final Log log = LogFactory.getLog(EvalAndFactoryNode.class);
}
