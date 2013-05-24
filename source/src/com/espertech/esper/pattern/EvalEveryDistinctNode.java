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
 * This class represents an 'every-distinct' operator in the evaluation tree representing an event expression.
 */
public class EvalEveryDistinctNode extends EvalNodeBase {
    protected final EvalEveryDistinctFactoryNode factoryNode;
    private final EvalNode childNode;

    public EvalEveryDistinctNode(EvalEveryDistinctFactoryNode factoryNode, EvalNode childNode, PatternAgentInstanceContext agentInstanceContext) {
        super(agentInstanceContext);
        this.factoryNode = factoryNode;
        this.childNode = childNode;
    }

    public EvalEveryDistinctFactoryNode getFactoryNode() {
        return factoryNode;
    }

    public EvalNode getChildNode() {
        return childNode;
    }

    public EvalStateNode newState(Evaluator parentNode,
                                  EvalStateNodeNumber stateNodeNumber, long stateNodeId)
    {
        if (factoryNode.getMsecToExpire() == null) {
            return new EvalEveryDistinctStateNode(parentNode, this);
        }
        else {
            return new EvalEveryDistinctStateExpireKeyNode(parentNode, this);
        }
    }

    private static final Log log = LogFactory.getLog(EvalEveryNode.class);
}
