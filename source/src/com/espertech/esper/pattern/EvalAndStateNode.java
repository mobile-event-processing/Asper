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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class represents the state of an "and" operator in the evaluation state tree.
 */
public class EvalAndStateNode extends EvalStateNode implements Evaluator
{
    protected final EvalAndNode evalAndNode;
    protected final EvalStateNode[] activeChildNodes;
    protected Object[] eventsPerChild;

    /**
     * Constructor.
     * @param parentNode is the parent evaluator to call to indicate truth value
     * @param evalAndNode is the factory node associated to the state
     */
    public EvalAndStateNode(Evaluator parentNode,
                                  EvalAndNode evalAndNode)
    {
        super(parentNode);

        this.evalAndNode = evalAndNode;
        this.activeChildNodes = new EvalStateNode[evalAndNode.getChildNodes().length];
        this.eventsPerChild = new Object[evalAndNode.getChildNodes().length];
    }

    @Override
    public EvalNode getFactoryNode() {
        return evalAndNode;
    }

    public final void start(MatchedEventMap beginState)
    {
        // In an "and" expression we need to create a state for all child listeners
        int count = 0;
        for (EvalNode node : evalAndNode.getChildNodes())
        {
            EvalStateNode childState = node.newState(this, null, 0L);
            activeChildNodes[count++] = childState;
        }

        // Start all child nodes
        for (EvalStateNode child : activeChildNodes)
        {
            if (child != null) {
                child.start(beginState);
            }
        }
    }

    public boolean isFilterStateNode() {
        return false;
    }

    public boolean isNotOperator() {
        return false;
    }

    public boolean isFilterChildNonQuitting() {
        return false;
    }

    public final void evaluateTrue(MatchedEventMap matchEvent, EvalStateNode fromNode, boolean isQuitted)
    {
        Integer indexFrom = null;
        for (int i = 0 ; i < activeChildNodes.length; i++) {
            if (activeChildNodes[i] == fromNode) {
                indexFrom = i;
            }
        }

        // If one of the children quits, remove the child
        if (isQuitted && indexFrom != null) {
            activeChildNodes[indexFrom] = null;
        }

        if (eventsPerChild == null || indexFrom == null) {
            return;
        }

        // Add the event received to the list of events per child
        addMatchEvent(eventsPerChild, indexFrom, matchEvent);

        // If all nodes have events received, the AND expression turns true
        boolean allHaveEvents = true;
        for (int i = 0; i < eventsPerChild.length; i++) {
            if (eventsPerChild[i] == null) {
                allHaveEvents = false;
                break;
            }
        }
        if (!allHaveEvents)
        {
            return;
        }

        // For each combination in eventsPerChild for all other state nodes generate an event to the parent
        List<MatchedEventMap> result = generateMatchEvents(matchEvent, eventsPerChild, indexFrom);

        boolean hasActive = false;
        for (int i = 0 ; i < activeChildNodes.length; i++) {
            if (activeChildNodes[i] != null) {
                hasActive = true;
                break;
            }
        }

        // Check if this is quitting
        boolean quitted = true;
        if (hasActive)
        {
            for (EvalStateNode stateNode : activeChildNodes)
            {
                if (stateNode != null && !(stateNode.isNotOperator()))
                {
                    quitted = false;
                }
            }
        }

        // So we are quitting if all non-not child nodes have quit, since the not-node wait for evaluate false
        if (quitted)
        {
            quit();
        }

        // Send results to parent
        for (MatchedEventMap theEvent : result)
        {
            this.getParentEvaluator().evaluateTrue(theEvent, this, quitted);
        }
    }

    public final void evaluateFalse(EvalStateNode fromNode)
    {
        Integer indexFrom = null;
        for (int i = 0 ; i < activeChildNodes.length; i++) {
            if (activeChildNodes[i] == fromNode) {
                activeChildNodes[i] = null;
                indexFrom = i;
            }
        }

        if (indexFrom != null) {
            eventsPerChild[indexFrom] = null;
        }

        // The and node cannot turn true anymore, might as well quit all child nodes
        this.getParentEvaluator().evaluateFalse(this);
        quit();
    }

    /**
     * Generate a list of matching event combinations constisting of the events per child that are passed in.
     * @param matchEvent can be populated with prior events that must be passed on
     * @param eventsPerChild is the list of events for each child node to the "And" node.
     * @return list of events populated with all possible combinations
     */
    public static List<MatchedEventMap> generateMatchEvents(MatchedEventMap matchEvent,
                                                              Object[] eventsPerChild,
                                                              int indexFrom)
    {
        // Place event list for each child state node into an array, excluding the node where the event came from
        ArrayList<List<MatchedEventMap>> listArray = new ArrayList<List<MatchedEventMap>>();
        int index = 0;
        for (int i = 0; i < eventsPerChild.length; i++)
        {
            Object eventsChild = eventsPerChild[i];
            if (indexFrom != i && eventsChild != null)
            {
                if (eventsChild instanceof MatchedEventMap) {
                    listArray.add(index++, Collections.singletonList((MatchedEventMap)eventsChild));
                }
                else {
                    listArray.add(index++, (List<MatchedEventMap>) eventsChild);
                }
            }
        }

        // Recusively generate MatchedEventMap instances for all accumulated events
        List<MatchedEventMap> results = new ArrayList<MatchedEventMap>();
        generateMatchEvents(listArray, 0, results, matchEvent);

        return results;
    }

    /**
     * For each combination of MatchedEventMap instance in all collections, add an entry to the list.
     * Recursive method.
     * @param eventList is an array of lists containing MatchedEventMap instances to combine
     * @param index is the current index into the array
     * @param result is the resulting list of MatchedEventMap
     * @param matchEvent is the start MatchedEventMap to generate from
     */
    protected static void generateMatchEvents(ArrayList<List<MatchedEventMap>> eventList,
                                                   int index,
                                                   List<MatchedEventMap> result,
                                                   MatchedEventMap matchEvent)
    {
        List<MatchedEventMap> events = eventList.get(index);

        for (MatchedEventMap theEvent : events)
        {
            MatchedEventMap current = matchEvent.shallowCopy();
            current.merge(theEvent);

            // If this is the very last list in the array of lists, add accumulated MatchedEventMap events to result
            if ((index + 1) == eventList.size())
            {
                result.add(current);
            }
            else
            {
                // make a copy of the event collection and hand to next list of events
                generateMatchEvents(eventList, index + 1, result, current);
            }
        }
    }

    public final void quit()
    {
        for (EvalStateNode child : activeChildNodes)
        {
            if (child != null) {
                child.quit();
            }
        }
        Arrays.fill(activeChildNodes, null);
        eventsPerChild = null;
    }

    public final Object accept(EvalStateNodeVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }

    public final Object childrenAccept(EvalStateNodeVisitor visitor, Object data)
    {
        for (EvalStateNode node : activeChildNodes)
        {
            if (node != null) {
                node.accept(visitor, data);
            }
        }
        return data;
    }

    public final String toString()
    {
        return "EvalAndStateNode";
    }

    public static void addMatchEvent(Object[] eventsPerChild, int indexFrom, MatchedEventMap matchEvent) {
        Object matchEventHolder = eventsPerChild[indexFrom];
        if (matchEventHolder == null) {
            eventsPerChild[indexFrom] = matchEvent;
        }
        else if (matchEventHolder instanceof MatchedEventMap) {
            List<MatchedEventMap> list = new ArrayList<MatchedEventMap>(4);
            list.add((MatchedEventMap) matchEventHolder);
            list.add(matchEvent);
            eventsPerChild[indexFrom] = list;
        }
        else {
            List<MatchedEventMap> list = (List<MatchedEventMap>) matchEventHolder;
            list.add(matchEvent);
        }
    }

    private static final Log log = LogFactory.getLog(EvalAndStateNode.class);
}
