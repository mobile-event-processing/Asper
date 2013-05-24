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

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the state collected by an "every" operator. The state includes handles to any sub-listeners
 * started by the operator.
 */
public class EvalEveryStateNode extends EvalStateNode implements Evaluator
{
    protected final EvalEveryNode evalEveryNode;
    protected final List<EvalStateNode> spawnedNodes;
    protected MatchedEventMap beginState;

    /**
     * Constructor.
     * @param parentNode is the parent evaluator to call to indicate truth value
     * @param evalEveryNode is the factory node associated to the state
     */
    public EvalEveryStateNode(Evaluator parentNode,
                                 EvalEveryNode evalEveryNode)
    {
        super(parentNode);

        this.evalEveryNode = evalEveryNode;
        this.spawnedNodes = new ArrayList<EvalStateNode>();
    }

    @Override
    public EvalNode getFactoryNode() {
        return evalEveryNode;
    }

    public final void start(MatchedEventMap beginState)
    {
        this.beginState = beginState.shallowCopy();
        EvalStateNode childState = evalEveryNode.getChildNode().newState(this, null, 0L);
        spawnedNodes.add(childState);

        // During the start of the child we need to use the temporary evaluator to catch any event created during a start.
        // Events created during the start would likely come from the "not" operator.
        // Quit the new child again if
        EvalEveryStateSpawnEvaluator spawnEvaluator = new EvalEveryStateSpawnEvaluator(evalEveryNode.getContext().getPatternContext().getStatementName());
        childState.setParentEvaluator(spawnEvaluator);
        childState.start(beginState);

        // If the spawned expression turned true already, just quit it
        if (spawnEvaluator.isEvaluatedTrue())
        {
            childState.quit();
        }
        else
        {
            childState.setParentEvaluator(this);
        }
    }

    public final void evaluateFalse(EvalStateNode fromNode)
    {
        fromNode.quit();
        spawnedNodes.remove(fromNode);

        // Spawn all nodes below this EVERY node
        // During the start of a child we need to use the temporary evaluator to catch any event created during a start
        // Such events can be raised when the "not" operator is used.
        EvalEveryStateSpawnEvaluator spawnEvaluator = new EvalEveryStateSpawnEvaluator(evalEveryNode.getContext().getPatternContext().getStatementName());
        EvalStateNode spawned = evalEveryNode.getChildNode().newState(spawnEvaluator, null, 0L);
        spawned.start(beginState);

        // If the whole spawned expression already turned true, quit it again
        if (spawnEvaluator.isEvaluatedTrue())
        {
            spawned.quit();
        }
        else
        {
            spawnedNodes.add(spawned);
            spawned.setParentEvaluator(this);
        }
    }

    public final void evaluateTrue(MatchedEventMap matchEvent, EvalStateNode fromNode, boolean isQuitted)
    {
        if (isQuitted)
        {
            spawnedNodes.remove(fromNode);
        }

        // See explanation in EvalFilterStateNode for the type check
        if (fromNode.isFilterStateNode())
        {
            // We do not need to newState new listeners here, since the filter state node below this node did not quit
        }
        else
        {
            // Spawn all nodes below this EVERY node
            // During the start of a child we need to use the temporary evaluator to catch any event created during a start
            // Such events can be raised when the "not" operator is used.
            EvalEveryStateSpawnEvaluator spawnEvaluator = new EvalEveryStateSpawnEvaluator(evalEveryNode.getContext().getPatternContext().getStatementName());
            EvalStateNode spawned = evalEveryNode.getChildNode().newState(spawnEvaluator, null, 0L);
            spawned.start(beginState);

            // If the whole spawned expression already turned true, quit it again
            if (spawnEvaluator.isEvaluatedTrue())
            {
                spawned.quit();
            }
            else
            {
                spawnedNodes.add(spawned);
                spawned.setParentEvaluator(this);
            }
        }

        // All nodes indicate to their parents that their child node did not quit, therefore a false for isQuitted
        this.getParentEvaluator().evaluateTrue(matchEvent, this, false);
    }

    public final void quit()
    {
        // Stop all child nodes
        for (EvalStateNode child : spawnedNodes)
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
        for (EvalStateNode spawnedNode : spawnedNodes)
        {
            spawnedNode.accept(visitor, data);
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
        return true;
    }

    public final String toString()
    {
        return "EvalEveryStateNode spawnedChildren=" + spawnedNodes.size();
    }

    private static final Log log = LogFactory.getLog(EvalEveryStateNode.class);
}
