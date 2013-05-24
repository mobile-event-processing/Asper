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
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.ViewSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * View for the on-delete statement that handles removing events from a named window.
 */
public abstract class NamedWindowOnExprBaseView extends ViewSupport implements StopCallback
{
    private static final Log log = LogFactory.getLog(NamedWindowOnExprBaseView.class);

    /**
     * The event type of the events hosted in the named window.
     */
    private final NamedWindowLookupStrategy lookupStrategy;
    private final ExprEvaluatorContext exprEvaluatorContext;

    /**
     * The root view accepting removals (old data).
     */
    protected final NamedWindowRootViewInstance rootView;

    /**
     * Ctor.
     * @param lookupStrategy for handling trigger events to determine deleted events
     * @param rootView to indicate which events to delete
     * @param exprEvaluatorContext context for expression evalauation
     */
    public NamedWindowOnExprBaseView(NamedWindowLookupStrategy lookupStrategy,
                                 NamedWindowRootViewInstance rootView,
                                 ExprEvaluatorContext exprEvaluatorContext)
    {
        this.lookupStrategy = lookupStrategy;
        this.rootView = rootView;
        this.exprEvaluatorContext = exprEvaluatorContext;
    }

    /**
     * Implemented by on-trigger views to action on the combination of trigger and matching events in the named window.
     * @param triggerEvents is the trigger events (usually 1)
     * @param matchingEvents is the matching events retrieved via lookup strategy
     */
    public abstract void handleMatching(EventBean[] triggerEvents, EventBean[] matchingEvents);

    public void stop() {
        log.debug(".stop");
        rootView.removeOnExpr(lookupStrategy);
    }

    public void update(EventBean[] newData, EventBean[] oldData)
    {
        if (newData == null)
        {
            return;
        }

        // Determine via the lookup strategy a subset of events to process
        EventBean[] eventsFound = lookupStrategy.lookup(newData, exprEvaluatorContext);

        // Let the implementation handle the delete or
        handleMatching(newData, eventsFound);
    }

    /**
     * returns expr context.
     * @return context
     */
    public ExprEvaluatorContext getExprEvaluatorContext() {
        return exprEvaluatorContext;
    }
}
