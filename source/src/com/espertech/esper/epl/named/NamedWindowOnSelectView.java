/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.ArrayEventIterator;
import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.event.EventBeanUtility;
import com.espertech.esper.util.AuditPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * View for the on-select statement that handles selecting events from a named window.
 */
public class NamedWindowOnSelectView extends NamedWindowOnExprBaseView
{
    private static final Log log = LogFactory.getLog(NamedWindowOnSelectView.class);

    private final NamedWindowOnSelectViewFactory parent;
    private final ResultSetProcessor resultSetProcessor;
    private EventBean[] lastResult;
    private Set<MultiKey<EventBean>> oldEvents = new HashSet<MultiKey<EventBean>>();
    private final boolean audit;
    private final boolean isDelete;

    public NamedWindowOnSelectView(NamedWindowLookupStrategy lookupStrategy, NamedWindowRootViewInstance rootView, ExprEvaluatorContext exprEvaluatorContext, NamedWindowOnSelectViewFactory parent, ResultSetProcessor resultSetProcessor, boolean audit, boolean isDelete) {
        super(lookupStrategy, rootView, exprEvaluatorContext);
        this.parent = parent;
        this.resultSetProcessor = resultSetProcessor;
        this.audit = audit;
        this.isDelete = isDelete;
    }

    public void handleMatching(EventBean[] triggerEvents, EventBean[] matchingEvents)
    {
        EventBean[] newData;

        // clear state from prior results
        resultSetProcessor.clear();

        // build join result
        // use linked hash set to retain order of join results for last/first/window to work most intuitively
        Set<MultiKey<EventBean>> newEvents = new LinkedHashSet<MultiKey<EventBean>>();
        for (int i = 0; i < triggerEvents.length; i++)
        {
            EventBean triggerEvent = triggerEvents[0];
            if (matchingEvents != null)
            {
                for (int j = 0; j < matchingEvents.length; j++)
                {
                    EventBean[] eventsPerStream = new EventBean[2];
                    eventsPerStream[0] = matchingEvents[j];
                    eventsPerStream[1] = triggerEvent;
                    newEvents.add(new MultiKey<EventBean>(eventsPerStream));
                }
            }
        }

        // process matches
        UniformPair<EventBean[]> pair = resultSetProcessor.processJoinResult(newEvents, oldEvents, false);
        newData = (pair != null ? pair.getFirst() : null);

        if (parent.isDistinct())
        {
            newData = EventBeanUtility.getDistinctByProp(newData, parent.getEventBeanReader());
        }

        if (parent.getInternalEventRouter() != null)
        {
            if (newData != null)
            {
                for (int i = 0; i < newData.length; i++)
                {
                    if (audit) {
                        AuditPath.auditInsertInto(getExprEvaluatorContext().getEngineURI(), getExprEvaluatorContext().getStatementName(), newData[i]);
                    }
                    parent.getInternalEventRouter().route(newData[i], parent.getStatementHandle(), parent.getInternalEventRouteDest(), getExprEvaluatorContext(), parent.isAddToFront());
                }
            }
        }

        // The on-select listeners receive the events selected
        if ((newData != null) && (newData.length > 0))
        {
            // And post only if we have listeners/subscribers that need the data
            if (parent.getStatementResultService().isMakeNatural() || parent.getStatementResultService().isMakeSynthetic())
            {
                updateChildren(newData, null);
            }
        }
        lastResult = newData;

        // clear state from prior results
        resultSetProcessor.clear();

        // Events to delete are indicated via old data
        if (isDelete) {
            this.rootView.update(null, matchingEvents);
        }
    }

    public EventType getEventType()
    {
        if (resultSetProcessor != null)
        {
            return resultSetProcessor.getResultEventType();
        }
        else
        {
            return rootView.getEventType();
        }
    }

    public Iterator<EventBean> iterator()
    {
        return new ArrayEventIterator(lastResult);
    }
}
