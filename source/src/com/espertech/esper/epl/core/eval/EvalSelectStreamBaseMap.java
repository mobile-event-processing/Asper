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
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.spec.SelectClauseStreamCompiledSpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EvalSelectStreamBaseMap extends EvalSelectStreamBase implements SelectExprProcessor {

    private static final Log log = LogFactory.getLog(EvalSelectStreamBaseMap.class);

    protected EvalSelectStreamBaseMap(SelectExprContext selectExprContext, EventType resultEventType, List<SelectClauseStreamCompiledSpec> namedStreams, boolean usingWildcard) {
        super(selectExprContext, resultEventType, namedStreams, usingWildcard);
    }

    public abstract EventBean processSpecific(Map<String, Object> props, EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext);

    public EventBean process(EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext)
    {
        // Evaluate all expressions and build a map of name-value pairs
        Map<String, Object> props = new HashMap<String, Object>();
        int count = 0;
        for (ExprEvaluator expressionNode : selectExprContext.getExpressionNodes())
        {
            Object evalResult = expressionNode.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
            props.put(selectExprContext.getColumnNames()[count], evalResult);
            count++;
        }
        for (SelectClauseStreamCompiledSpec element : namedStreams)
        {
            EventBean theEvent = eventsPerStream[element.getStreamNumber()];
            props.put(selectExprContext.getColumnNames()[count], theEvent);
            count++;
        }
        if (isUsingWildcard && eventsPerStream.length > 1)
        {
            for (EventBean anEventsPerStream : eventsPerStream)
            {
                props.put(selectExprContext.getColumnNames()[count], anEventsPerStream);
                count++;
            }
        }

        return processSpecific(props, eventsPerStream, exprEvaluatorContext);
    }
}