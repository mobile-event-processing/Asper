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
import com.espertech.esper.collection.OneEventCollection;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.metric.MetricReportingPath;
import com.espertech.esper.event.EventBeanUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;

/**
 * View for the on-delete statement that handles removing events from a named window.
 */
public class NamedWindowOnMergeView extends NamedWindowOnExprBaseView
{
    private static final Log log = LogFactory.getLog(NamedWindowOnMergeView.class);

    private final NamedWindowOnMergeViewFactory parent;
    private EventBean[] lastResult;

    public NamedWindowOnMergeView(NamedWindowLookupStrategy lookupStrategy, NamedWindowRootViewInstance rootView, ExprEvaluatorContext exprEvaluatorContext, NamedWindowOnMergeViewFactory parent) {
        super(lookupStrategy, rootView, exprEvaluatorContext);
        this.parent = parent;
    }

    public void handleMatching(EventBean[] triggerEvents, EventBean[] matchingEvents)
    {
        OneEventCollection newData = new OneEventCollection();
        OneEventCollection oldData = null;
        EventBean[] eventsPerStream = new EventBean[3]; // first:named window, second: trigger, third:before-update (optional)

        if ((matchingEvents == null) || (matchingEvents.length == 0)){
            for (EventBean triggerEvent : triggerEvents) {
                eventsPerStream[1] = triggerEvent;
                for (NamedWindowOnMergeMatch action : parent.getNamedWindowOnMergeHelper().getUnmatched()) {
                    if (!action.isApplies(eventsPerStream, super.getExprEvaluatorContext())) {
                        continue;
                    }
                    action.apply(null, eventsPerStream, newData, oldData, super.getExprEvaluatorContext());
                    break;  // apply no other actions
                }
            }
        }
        else {

            // handle update/
            oldData = new OneEventCollection();

            for (EventBean triggerEvent : triggerEvents) {
                eventsPerStream[1] = triggerEvent;
                for (EventBean matchingEvent : matchingEvents) {
                    eventsPerStream[0] = matchingEvent;
                    for (NamedWindowOnMergeMatch action : parent.getNamedWindowOnMergeHelper().getMatched()) {
                        if (!action.isApplies(eventsPerStream, super.getExprEvaluatorContext())) {
                            continue;
                        }
                        action.apply(matchingEvent, eventsPerStream, newData, oldData, super.getExprEvaluatorContext());
                        break;  // apply no other actions
                    }
                }
            }
        }

        if (!newData.isEmpty() || (oldData != null && !oldData.isEmpty()))
        {
            if ((MetricReportingPath.isMetricsEnabled) && (parent.getCreateNamedWindowMetricHandle().isEnabled()) && !newData.isEmpty())
            {
                parent.getMetricReportingService().accountTime(parent.getCreateNamedWindowMetricHandle(), 0, 0, newData.toArray().length);
            }

            // Events to delete are indicated via old data
            // The on-merge listeners receive the events deleted, but only if there is interest
            if (parent.getStatementResultService().isMakeNatural()) {
                EventBean[] eventsPerStreamNaturalNew = newData.isEmpty() ? null : newData.toArray();
                EventBean[] eventsPerStreamNaturalOld = (oldData == null || oldData.isEmpty()) ? null : oldData.toArray();
                this.rootView.update(EventBeanUtility.denaturalize(eventsPerStreamNaturalNew), EventBeanUtility.denaturalize(eventsPerStreamNaturalOld));
                updateChildren(eventsPerStreamNaturalNew, eventsPerStreamNaturalOld);
            }
            else {
                EventBean[] eventsPerStreamNew = newData.isEmpty() ? null : newData.toArray();
                EventBean[] eventsPerStreamOld = (oldData == null || oldData.isEmpty()) ? null : oldData.toArray();
                this.rootView.update(eventsPerStreamNew, eventsPerStreamOld);
                if (parent.getStatementResultService().isMakeSynthetic()) {
                    updateChildren(eventsPerStreamNew, eventsPerStreamOld);
                }
            }
        }

        // Keep the last delete records
        lastResult = matchingEvents;
    }

    public EventType getEventType()
    {
        return rootView.getEventType();
    }

    public Iterator<EventBean> iterator()
    {
        return new ArrayEventIterator(lastResult);
    }
}