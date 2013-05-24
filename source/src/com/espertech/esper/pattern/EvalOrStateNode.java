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

import java.util.Arrays;

/**
 * This class represents the state of a "or" operator in the evaluation state tree.
 */
public class EvalOrStateNode extends EvalStateNode implements Evaluator
{
    protected final EvalOrNode evalOrNode;
    protected final EvalStateNode[] childNodes;

    /**
     * Constructor.
     * @param parentNode is the parent evaluator to call to indicate truth value
     * @param evalOrNode is the factory node associated to the state
     */
    public EvalOrStateNode(Evaluator parentNode,
                                 EvalOrNode evalOrNode)
    {
        super(parentNode);

        this.childNodes = new EvalStateNode[evalOrNode.getChildNodes().length];
        this.evalOrNode = evalOrNode;
    }

    @Override
    public EvalNode getFactoryNode() {
        return evalOrNode;
    }

    public final void start(MatchedEventMap beginState)
    {
        // In an "or" expression we need to create states for all child expressions/listeners,
        // since all are going to be started
        int count = 0;
        for (EvalNode node : evalOrNode.getChildNodes())
        {
            EvalStateNode childState = node.newState(this, null, 0L);
            childNodes[count++] = childState;
        }

        // In an "or" expression we start all child listeners
        EvalStateNode[] childNodeCopy = new EvalStateNode[childNodes.length];
        System.arraycopy(childNodes, 0, childNodeCopy, 0, childNodes.length);
        for (EvalStateNode child : childNodeCopy)
        {
            child.start(beginState);
        }
    }

    public final void evaluateTrue(MatchedEventMap matchEvent, EvalStateNode fromNode, boolean isQuitted)
    {
        // If one of the children quits, the whole or expression turns true and all subexpressions must quit
        if (isQuitted)
        {
            for (int i = 0; i < childNodes.length; i++) {
                if (childNodes[i] == fromNode) {
                    childNodes[i] = null;
                }
            }
            quit();     // Quit the remaining listeners
        }

        this.getParentEvaluator().evaluateTrue(matchEvent, this, isQuitted);
    }

    public final void evaluateFalse(EvalStateNode fromNode)
    {
        for (int i = 0; i < childNodes.length; i++) {
            if (childNodes[i] == fromNode) {
                childNodes[i] = null;
            }
        }

        boolean allEmpty = true;
        for (int i = 0; i < childNodes.length; i++) {
            if (childNodes[i] != null) {
                allEmpty = false;
                break;
            }
        }

        if (allEmpty) {
            this.getParentEvaluator().evaluateFalse(this);
        }
    }

    public final void quit()
    {
        for (EvalStateNode child : childNodes)
        {
            if (child != null) {
                child.quit();
            }
        }
        Arrays.fill(childNodes, null);
    }

    public final Object accept(EvalStateNodeVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }

    public final Object childrenAccept(EvalStateNodeVisitor visitor, Object data)
    {
        for (EvalStateNode node : childNodes)
        {
            if (node != null) {
                node.accept(visitor, data);
            }
        }
        return data;
    }

    public boolean isNotOperator() {
        return false;
    }

    public boolean isFilterStateNode() {
        return false;
    }

    public boolean isFilterChildNonQuitting() {
        return false;
    }

    public final String toString()
    {
        return "EvalOrStateNode";
    }

    private static final Log log = LogFactory.getLog(EvalOrStateNode.class);
}
