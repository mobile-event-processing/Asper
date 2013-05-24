/*
 * *************************************************************************************
 *  Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 *  http://esper.codehaus.org                                                          *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 * *************************************************************************************
 */

package com.espertech.esper.epl.core.eval;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.core.SelectExprProcessor;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.spec.SelectClauseStreamCompiledSpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

public class EvalSelectStreamNoUnderlyingMap extends EvalSelectStreamBaseMap implements SelectExprProcessor {

    private static final Log log = LogFactory.getLog(EvalSelectStreamNoUnderlyingMap.class);

    public EvalSelectStreamNoUnderlyingMap(SelectExprContext selectExprContext, EventType resultEventType, List<SelectClauseStreamCompiledSpec> namedStreams, boolean usingWildcard) {
        super(selectExprContext, resultEventType, namedStreams, usingWildcard);
    }

    public EventBean processSpecific(Map<String, Object> props, EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext)
    {
        return super.getSelectExprContext().getEventAdapterService().adapterForTypedMap(props, super.getResultEventType());
    }
}