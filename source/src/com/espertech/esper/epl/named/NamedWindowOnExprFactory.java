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
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.ViewSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * View for the on-delete statement that handles removing events from a named window.
 */
public interface NamedWindowOnExprFactory
{
    public NamedWindowOnExprBaseView make(NamedWindowLookupStrategy lookupStrategy,
                                          NamedWindowRootViewInstance namedWindowRootViewInstance,
                                          AgentInstanceContext agentInstanceContext, ResultSetProcessor resultSetProcessor);
}
