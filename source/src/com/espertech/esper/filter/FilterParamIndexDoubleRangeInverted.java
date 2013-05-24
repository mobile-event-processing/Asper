/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.filter;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Map;

/**
 * Index for filter parameter constants for the not range operators (range open/closed/half).
 * The implementation is based on the SortedMap implementation of TreeMap and stores only expression
 * parameter values of type DoubleRange.
 */
public final class FilterParamIndexDoubleRangeInverted extends FilterParamIndexDoubleRangeBase
{
    public FilterParamIndexDoubleRangeInverted(FilterSpecLookupable lookupable, FilterOperator filterOperator) {
        super(lookupable, filterOperator);
        if (!(filterOperator.isInvertedRangeOperator()))
        {
            throw new IllegalArgumentException("Invalid filter operator " + filterOperator);
        }
    }

    public final void matchEvent(EventBean theEvent, Collection<FilterHandle> matches)
    {
        Object objAttributeValue = lookupable.getGetter().get(theEvent);

        if (objAttributeValue == null)
        {
            return;
        }

        double attributeValue = ((Number) objAttributeValue).doubleValue();

        if (this.getFilterOperator() == FilterOperator.NOT_RANGE_CLOSED) {   // include all endpoints
            for (Map.Entry<DoubleRange, EventEvaluator> entry : ranges.entrySet()) {
                if ((attributeValue < entry.getKey().getMin()) ||
                    (attributeValue > entry.getKey().getMax()))
                {
                    entry.getValue().matchEvent(theEvent, matches);
                }
            }
        }
        else if (this.getFilterOperator() == FilterOperator.NOT_RANGE_OPEN) { // include neither endpoint
            for (Map.Entry<DoubleRange, EventEvaluator> entry : ranges.entrySet()) {
                if ((attributeValue <= entry.getKey().getMin()) ||
                    (attributeValue >= entry.getKey().getMax()))
                {
                    entry.getValue().matchEvent(theEvent, matches);
                }
            }
        }
        else if (this.getFilterOperator() == FilterOperator.NOT_RANGE_HALF_CLOSED) { // include high endpoint not low endpoint
            for (Map.Entry<DoubleRange, EventEvaluator> entry : ranges.entrySet()) {
                if ((attributeValue <= entry.getKey().getMin()) ||
                    (attributeValue > entry.getKey().getMax()))
                {
                    entry.getValue().matchEvent(theEvent, matches);
                }
            }
        }
        else if (this.getFilterOperator() == FilterOperator.NOT_RANGE_HALF_OPEN) { // include low endpoint not high endpoint
            for (Map.Entry<DoubleRange, EventEvaluator> entry : ranges.entrySet()) {
                if ((attributeValue < entry.getKey().getMin()) ||
                    (attributeValue >= entry.getKey().getMax()))
                {
                    entry.getValue().matchEvent(theEvent, matches);
                }
            }
        }
        else
        {
            throw new IllegalStateException("Invalid filter operator " + this.getFilterOperator());
        }
    }

    private static final Log log = LogFactory.getLog(FilterParamIndexDoubleRangeInverted.class);
}
