/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.std;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.MultiKeyUntyped;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.event.EventBeanUtility;
import com.espertech.esper.view.CloneableView;
import com.espertech.esper.view.View;
import com.espertech.esper.view.ViewSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This view retains the first event for each multi-key of distinct property values.
 * <p>
 * The view does not post a remove stream unless explicitly deleted from.
 * <p>
 * The view swallows any insert stream events that provide no new distinct set of property values.
 */
public class FirstUniqueByPropertyView extends ViewSupport implements CloneableView
{
    private final ExprNode[] uniqueCriteria;
    protected final ExprEvaluator[] uniqueCriteriaEval;
    private final int numKeys;
    private EventBean[] eventsPerStream = new EventBean[1];
    protected final Map<Object, EventBean> firstEvents = new LinkedHashMap<Object, EventBean>();
    protected final AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext;

    /**
     * Constructor.
     * @param uniqueCriteria is the expressions from which to pull the unique value
     */
    public FirstUniqueByPropertyView(ExprNode[] uniqueCriteria, AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext)
    {
        this.uniqueCriteria = uniqueCriteria;
        this.uniqueCriteriaEval = ExprNodeUtility.getEvaluators(uniqueCriteria);
        this.agentInstanceViewFactoryContext = agentInstanceViewFactoryContext;
        numKeys = uniqueCriteria.length;
    }

    public View cloneView()
    {
        return new FirstUniqueByPropertyView(uniqueCriteria, agentInstanceViewFactoryContext);
    }

    /**
     * Returns the expressions supplying the unique value to keep the most recent record for.
     * @return expressions for unique value
     */
    public final ExprNode[] getUniqueCriteria()
    {
        return uniqueCriteria;
    }

    public final EventType getEventType()
    {
        // The schema is the parent view's schema
        return parent.getEventType();
    }

    public final void update(EventBean[] newData, EventBean[] oldData)
    {
        EventBean[] newDataToPost = null;
        EventBean[] oldDataToPost = null;

        if (oldData != null)
        {
            for (EventBean oldEvent : oldData)
            {
                // Obtain unique value
                Object key = getUniqueKey(oldEvent);

                // If the old event is the current unique event, remove and post as old data
                EventBean lastValue = firstEvents.get(key);

                if (lastValue != oldEvent)
                {
                    continue;
                }

                if (oldDataToPost == null)
                {
                    oldDataToPost = new EventBean[]{oldEvent};
                }
                else
                {
                    oldDataToPost = EventBeanUtility.addToArray(oldDataToPost, oldEvent);
                }

                firstEvents.remove(key);
                internalHandleRemoved(key, lastValue);
            }
        }

        if (newData != null)
        {
            for (EventBean newEvent : newData)
            {
                // Obtain unique value
                Object key = getUniqueKey(newEvent);

                // already-seen key
                if (firstEvents.containsKey(key))
                {
                    continue;
                }

                // store
                firstEvents.put(key, newEvent);
                internalHandleAdded(key, newEvent);

                // Post the new value
                if (newDataToPost == null)
                {
                    newDataToPost = new EventBean[]{newEvent};
                }
                else
                {
                    newDataToPost = EventBeanUtility.addToArray(newDataToPost, newEvent);
                }
            }
        }

        if ((this.hasViews()) && ((newDataToPost != null) || (oldDataToPost != null)))
        {
            updateChildren(newDataToPost, oldDataToPost);
        }
    }

    public void internalHandleRemoved(Object key, EventBean lastValue) {
        // no action required
    }

    public void internalHandleAdded(Object key, EventBean newEvent) {
        // no action required
    }

    public final Iterator<EventBean> iterator()
    {
        return firstEvents.values().iterator();
    }

    public final String toString()
    {
        return this.getClass().getName() + " uniqueCriteria=" + Arrays.toString(uniqueCriteria);
    }

    protected Object getUniqueKey(EventBean theEvent)
    {
        eventsPerStream[0] = theEvent;
        if (numKeys == 1) {
            return uniqueCriteriaEval[0].evaluate(eventsPerStream, true, agentInstanceViewFactoryContext);
        }

        Object[] values = new Object[numKeys];
        for (int i = 0; i < numKeys; i++)
        {
            values[i] = uniqueCriteriaEval[i].evaluate(eventsPerStream, true, agentInstanceViewFactoryContext);
        }
        return new MultiKeyUntyped(values);
    }

    /**
     * Returns true if empty.
     * @return true if empty
     */
    public boolean isEmpty()
    {
        return firstEvents.isEmpty();
    }

    private static final Log log = LogFactory.getLog(FirstUniqueByPropertyView.class);
}
