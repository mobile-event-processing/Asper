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

package com.espertech.esper.support.epl;

import com.espertech.esper.epl.agg.service.AggregationSupport;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;

import java.io.Serializable;

public class SupportPluginAggregationMethodOne extends AggregationSupport implements Serializable
{
    private int count;

    public void clear()
    {
        count = 0;    
    }

    @Override
    public void validate(AggregationValidationContext validationContext)
    {
    }

    public void enter(Object value)
    {
        count--;
    }

    public void leave(Object value)
    {
        count++;
    }

    public Object getValue()
    {
        return count;
    }

    public Class getValueType()
    {
        return int.class;
    }
}
