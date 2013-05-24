/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.epl.variable.VariableReader;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.client.EventBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An order-by processor that sorts events according to the expressions
 * in the order_by clause.
 */
public class OrderByProcessorRowLimit implements OrderByProcessor {

	private static final Log log = LogFactory.getLog(OrderByProcessorImpl.class);

    private final VariableReader numRowsVariableReader;
    private final VariableReader offsetVariableReader;
    private int currentRowLimit;
    private int currentOffset;

    public OrderByProcessorRowLimit(VariableReader numRowsVariableReader, VariableReader offsetVariableReader, int currentRowLimit, int currentOffset) {
        this.numRowsVariableReader = numRowsVariableReader;
        this.offsetVariableReader = offsetVariableReader;
        this.currentRowLimit = currentRowLimit;
        this.currentOffset = currentOffset;
    }

    public EventBean[] sort(EventBean[] outgoingEvents, EventBean[][] generatingEvents, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        return applyLimit(outgoingEvents);
    }

    public EventBean[] sort(EventBean[] outgoingEvents, EventBean[][] generatingEvents, Object[] groupByKeys, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        return applyLimit(outgoingEvents);
    }

    public Object getSortKey(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        return null;
    }

    public Object[] getSortKeyPerRow(EventBean[] generatingEvents, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        return null;
    }

    public EventBean[] sort(EventBean[] outgoingEvents, Object[] orderKeys, ExprEvaluatorContext exprEvaluatorContext)
    {
        return applyLimit(outgoingEvents);
    }

    /**
     * Applys the limiting function to outgoing events.
     * @param outgoingEvents unlimited
     * @return limited
     */
    protected EventBean[] applyLimit(EventBean[] outgoingEvents)
    {
        if (outgoingEvents == null) {
            return null;
        }
        if (numRowsVariableReader != null)
        {
            Number varValue = (Number) numRowsVariableReader.getValue();
            if (varValue != null)
            {
                currentRowLimit = varValue.intValue();
            }
            else
            {
                currentRowLimit = Integer.MAX_VALUE;
            }
            if (currentRowLimit < 0)
            {
                currentRowLimit = Integer.MAX_VALUE;
            }
        }

        if (offsetVariableReader != null)
        {
            Number varValue = (Number) offsetVariableReader.getValue();
            if (varValue != null)
            {
                currentOffset = varValue.intValue();
            }
            else
            {
                currentOffset = 0;
            }
            if (currentOffset < 0)
            {
                currentOffset = 0;
            }
        }

        // no offset
        if (currentOffset == 0)
        {
            if ((outgoingEvents.length <= currentRowLimit))
            {
                return outgoingEvents;
            }

            if (currentRowLimit == 0)
            {
                return null;
            }

            EventBean[] limited = new EventBean[currentRowLimit];
            System.arraycopy(outgoingEvents, 0, limited, 0, currentRowLimit);
            return limited;
        }
        // with offset
        else
        {
            int maxInterested = currentRowLimit + currentOffset;
            if (currentRowLimit == Integer.MAX_VALUE)
            {
                maxInterested = Integer.MAX_VALUE;
            }

            // more rows then requested
            if (outgoingEvents.length > maxInterested)
            {
                EventBean[] limited = new EventBean[currentRowLimit];
                System.arraycopy(outgoingEvents, currentOffset, limited, 0, currentRowLimit);
                return limited;
            }

            // less or equal rows to offset
            if (outgoingEvents.length <= currentOffset)
            {
                return null;
            }

            int size = outgoingEvents.length - currentOffset;
            EventBean[] limited = new EventBean[size];
            System.arraycopy(outgoingEvents, currentOffset, limited, 0, size);
            return limited;
        }
    }
}
