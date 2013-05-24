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

import java.util.HashMap;

/**
 * This class represents the state of a followed-by operator in the evaluation state tree.
 */
public class EvalFollowedByStateNode extends EvalStateNode implements Evaluator
{
    protected final EvalFollowedByNode evalFollowedByNode;
    protected final HashMap<EvalStateNode, Integer> nodes;

    /**
     * Constructor.
     * @param parentNode is the parent evaluator to call to indicate truth value
     * @param evalFollowedByNode is the factory node associated to the state
     */
    public EvalFollowedByStateNode(Evaluator parentNode,
                                         EvalFollowedByNode evalFollowedByNode)
    {
        super(parentNode);

        this.evalFollowedByNode = evalFollowedByNode;
        this.nodes = new HashMap<EvalStateNode, Integer>();
    }

    @Override
    public EvalNode getFactoryNode() {
        return evalFollowedByNode;
    }

    public final void start(MatchedEventMap beginState)
    {
        EvalNode child = evalFollowedByNode.getChildNodes()[0];
        EvalStateNode childState = child.newState(this, null, 0L);
        nodes.put(childState, 0);
        childState.start(beginState);
    }

    public final void evaluateTrue(MatchedEventMap matchEvent, EvalStateNode fromNode, boolean isQuitted)
    {
        Integer index = nodes.get(fromNode);

        if (isQuitted)
        {
            nodes.remove(fromNode);
        }

        // the node may already have quit as a result of an outer state quitting this state,
        // however the callback may still be received; It is fine to ignore this callback. 
        if (index == null)
        {
            return;
        }

        // If the match came from the very last filter, need to escalate
        int numChildNodes = evalFollowedByNode.getChildNodes().length;
        if (index == (numChildNodes - 1))
        {
            boolean isFollowedByQuitted = false;
            if (nodes.isEmpty())
            {
                isFollowedByQuitted = true;
            }

            this.getParentEvaluator().evaluateTrue(matchEvent, this, isFollowedByQuitted);
        }
        // Else start a new sub-expression for the next-in-line filter
        else
        {
            EvalNode child = evalFollowedByNode.getChildNodes()[index + 1];
            EvalStateNode childState = child.newState(this, null, 0L);
            nodes.put(childState, index + 1);
            childState.start(matchEvent);
        }
    }

    public final void evaluateFalse(EvalStateNode fromNode)
    {
        fromNode.quit();
        nodes.remove(fromNode);

        if (nodes.isEmpty())
        {
            this.getParentEvaluator().evaluateFalse(this);
            quit();
        }
    }

    public final void quit()
    {
        for (EvalStateNode child : nodes.keySet())
        {
            child.quit();
        }
    }

    public final Object accept(EvalStateNodeVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }

    public final Object childrenAccept(EvalStateNodeVisitor visitor, Object data)
    {
        for (EvalStateNode node : nodes.keySet())
        {
            node.accept(visitor, data);
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
        return "EvalFollowedByStateNode nodes=" + nodes.size();
    }

    private static final Log log = LogFactory.getLog(EvalFollowedByStateNode.class);
}
