/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.client.EventBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents the state of a match-until node in the evaluation state tree.
 */
public class EvalMatchUntilStateNode extends EvalStateNode implements Evaluator
{
    protected final EvalMatchUntilNode evalMatchUntilNode;
    protected MatchedEventMap beginState;
    protected final ArrayList<EventBean>[] matchedEventArrays;

    protected EvalStateNode stateMatcher;
    protected EvalStateNode stateUntil;
    protected int numMatches;
    protected Integer lowerbounds;
    protected Integer upperbounds;

    /**
     * Constructor.
     * @param parentNode is the parent evaluator to call to indicate truth value
     * @param evalMatchUntilNode is the factory node associated to the state
     */
    public EvalMatchUntilStateNode(Evaluator parentNode,
                                         EvalMatchUntilNode evalMatchUntilNode)
    {
        super(parentNode);

        this.matchedEventArrays = (ArrayList<EventBean>[]) new ArrayList[evalMatchUntilNode.getFactoryNode().getTagsArrayed().length];
        this.evalMatchUntilNode = evalMatchUntilNode;
    }

    @Override
    public EvalNode getFactoryNode() {
        return evalMatchUntilNode;
    }

    public final void start(MatchedEventMap beginState)
    {
        this.beginState = beginState;

        EvalNode childMatcher = evalMatchUntilNode.getChildNodeSub();
        stateMatcher = childMatcher.newState(this, null, 0L);

        if (evalMatchUntilNode.getChildNodeUntil() != null)
        {
            EvalNode childUntil = evalMatchUntilNode.getChildNodeUntil();
            stateUntil = childUntil.newState(this, null, 0L);
        }

        // start until first, it controls the expression
        // if the same event fires both match and until, the match should not count
        if (stateUntil != null)
        {
            stateUntil.start(beginState);
        }

        initBounds();

        if (stateMatcher != null) {
            stateMatcher.start(beginState);
        }
    }

    public final void evaluateTrue(MatchedEventMap matchEvent, EvalStateNode fromNode, boolean isQuitted)
    {
        boolean isMatcher = false;
        if (fromNode == stateMatcher)
        {
            // Add the additional tagged events to the list for later posting
            isMatcher = true;
            numMatches++;
            int[] tags = evalMatchUntilNode.getFactoryNode().getTagsArrayed();
            for (int i = 0; i < tags.length; i++)
            {
                Object theEvent = matchEvent.getMatchingEventAsObject(tags[i]);
                if (theEvent != null)
                {
                    if (matchedEventArrays[i] == null)
                    {
                        matchedEventArrays[i] = new ArrayList<EventBean>();
                    }
                    if (theEvent instanceof EventBean) {
                        matchedEventArrays[i].add((EventBean) theEvent);
                    }
                    else {
                        EventBean[] arrayEvents = (EventBean[]) theEvent;
                        matchedEventArrays[i].addAll(Arrays.asList(arrayEvents));
                    }

                }
            }
        }

        if (isQuitted)
        {
            if (isMatcher)
            {
                stateMatcher = null;
            }
            else
            {
                stateUntil = null;
            }
        }

        // handle matcher evaluating true
        if (isMatcher)
        {
            if ((isTightlyBound()) && (numMatches == lowerbounds))
            {
                quit();
                MatchedEventMap consolidated = consolidate(matchEvent, matchedEventArrays, evalMatchUntilNode.getFactoryNode().getTagsArrayed());
                this.getParentEvaluator().evaluateTrue(consolidated, this, true);
            }
            else
            {
                // restart or keep started if not bounded, or not upper bounds, or upper bounds not reached
                boolean restart = (!isBounded()) ||
                                  (upperbounds == null) ||
                                  (upperbounds > numMatches);
                if (stateMatcher == null)
                {
                    if (restart)
                    {
                        EvalNode childMatcher = evalMatchUntilNode.getChildNodeSub();
                        stateMatcher = childMatcher.newState(this, null, 0L);
                        stateMatcher.start(beginState);
                    }
                }
                else
                {
                    if (!restart)
                    {
                        stateMatcher.quit();
                        stateMatcher = null;
                    }
                }
            }
        }
        else
        // handle until-node
        {
            quit();

            // consolidate multiple matched events into a single event
            MatchedEventMap consolidated = consolidate(matchEvent, matchedEventArrays, evalMatchUntilNode.getFactoryNode().getTagsArrayed());

            if ((lowerbounds != null) && (numMatches < lowerbounds))
            {
                this.getParentEvaluator().evaluateFalse(this);
            }
            else
            {
                this.getParentEvaluator().evaluateTrue(consolidated, this, true);
            }
        }
    }

    private static MatchedEventMap consolidate(MatchedEventMap beginState, ArrayList<EventBean>[] matchedEventList, int[] tagsArrayed)
    {
        if (tagsArrayed == null)
        {
            return beginState;
        }

        for (int i = 0; i < tagsArrayed.length; i++)
        {
            if (matchedEventList[i] == null)
            {
                continue;
            }
            EventBean[] eventsForTag = matchedEventList[i].toArray(new EventBean[matchedEventList[i].size()]);
            beginState.add(tagsArrayed[i], eventsForTag);
        }

        return beginState;
    }

    public final void evaluateFalse(EvalStateNode fromNode)
    {
        boolean isMatcher = false;
        if (fromNode == stateMatcher)
        {
            isMatcher = true;
        }

        if (isMatcher)
        {
            stateMatcher.quit();
            stateMatcher = null;
        }
        else
        {
            stateUntil.quit();
            stateUntil = null;
            this.getParentEvaluator().evaluateFalse(this);
        }
    }

    public final void quit()
    {
        if (stateMatcher != null)
        {
            stateMatcher.quit();
            stateMatcher = null;
        }
        if (stateUntil != null)
        {
            stateUntil.quit();
            stateUntil = null;
        }
    }

    public final Object accept(EvalStateNodeVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }

    public final Object childrenAccept(EvalStateNodeVisitor visitor, Object data)
    {
        if (stateMatcher != null) {
            stateMatcher.accept(visitor, data);
        }
        if (stateUntil != null) {
            stateUntil.accept(visitor, data);
        }
        return data;
    }

    public final String toString()
    {
        return "EvalMatchUntilStateNode";
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

    private boolean isTightlyBound() {
        return lowerbounds != null && upperbounds != null && upperbounds.equals(lowerbounds);
    }

    private boolean isBounded() {
        return lowerbounds != null || upperbounds != null;
    }

    protected void initBounds()
    {
        EventBean[] eventsPerStream = evalMatchUntilNode.getFactoryNode().getConvertor().convert(beginState);
        if (evalMatchUntilNode.getFactoryNode().getLowerBounds() != null) {
            lowerbounds = (Integer) evalMatchUntilNode.getFactoryNode().getLowerBounds().getExprEvaluator().evaluate(eventsPerStream, true, evalMatchUntilNode.getContext().getAgentInstanceContext());
        }
        if (evalMatchUntilNode.getFactoryNode().getUpperBounds() != null) {
            upperbounds = (Integer) evalMatchUntilNode.getFactoryNode().getUpperBounds().getExprEvaluator().evaluate(eventsPerStream, true, evalMatchUntilNode.getContext().getAgentInstanceContext());
        }
        if (upperbounds != null && lowerbounds != null) {
            if (upperbounds < lowerbounds) {
                Integer lbounds =  lowerbounds;
                lowerbounds = upperbounds;
                upperbounds = lbounds;
            }
        }
    }

    private static final Log log = LogFactory.getLog(EvalMatchUntilStateNode.class);

}
