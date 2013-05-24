/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.pattern.guard.Guard;
import com.espertech.esper.pattern.guard.Quitable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class represents the state of a "within" operator in the evaluation state tree.
 * The within operator applies to a subexpression and is thus expected to only
 * have one child node.
 */
public class EvalGuardStateNode extends EvalStateNode implements Evaluator, Quitable
{
    protected EvalGuardNode evalGuardNode;
    protected EvalStateNode activeChildNode;
    protected Guard guard;

    /**
     * Constructor.
     * @param parentNode is the parent evaluator to call to indicate truth value
     * @param evalGuardNode is the factory node associated to the state
     */
    public EvalGuardStateNode(Evaluator parentNode,
                               EvalGuardNode evalGuardNode)
    {
        super(parentNode);
        this.evalGuardNode = evalGuardNode;
    }

    @Override
    public EvalNode getFactoryNode() {
        return evalGuardNode;
    }

    public PatternAgentInstanceContext getContext() {
        return evalGuardNode.getContext();
    }

    public void start(MatchedEventMap beginState)
    {
        guard = evalGuardNode.getFactoryNode().getGuardFactory().makeGuard(evalGuardNode.getContext(), beginState, this, null, null);
        activeChildNode = evalGuardNode.getChildNode().newState(this, null, 0L);

        // Start the single child state
        activeChildNode.start(beginState);

        // Start the guard
        guard.startGuard();
    }

    public final void evaluateTrue(MatchedEventMap matchEvent, EvalStateNode fromNode, boolean isQuitted)
    {
        boolean haveQuitted = activeChildNode == null;

        // If one of the children quits, remove the child
        if (isQuitted)
        {
            activeChildNode = null;

            // Stop guard, since associated subexpression is gone
            guard.stopGuard();
        }

        if (!(haveQuitted))
        {
            boolean guardPass = guard.inspect(matchEvent);
            if (guardPass)
            {
                this.getParentEvaluator().evaluateTrue(matchEvent, this, isQuitted);
            }
        }
    }

    public final void evaluateFalse(EvalStateNode fromNode)
    {
    }

    public final void quit()
    {
        if (activeChildNode != null)
        {
            activeChildNode.quit();
            guard.stopGuard();
        }

        activeChildNode = null;
    }

    public final Object accept(EvalStateNodeVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }

    public final Object childrenAccept(EvalStateNodeVisitor visitor, Object data)
    {
        if (activeChildNode != null)
        {
            activeChildNode.accept(visitor, data);
        }
        return data;
    }

    public final String toString()
    {
        return "EvaluationWitinStateNode activeChildNode=" + activeChildNode +
                 " guard=" + guard;
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

    public void guardQuit()
    {
        // It is possible that the child node has already been quit such as when the parent wait time was shorter.
        // 1. parent node's guard indicates quit to all children
        // 2. this node's guards also indicates quit, however that already occured
        if (activeChildNode != null)
        {
            activeChildNode.quit();
        }
        activeChildNode = null;

        // Indicate to parent state that this is permanently false.
        this.getParentEvaluator().evaluateFalse(this);
    }

    private static final Log log = LogFactory.getLog(EvalGuardStateNode.class);
}
