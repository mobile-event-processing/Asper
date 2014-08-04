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
import java.util.ArrayList;
import java.util.List;

public class SupportPluginAggregationMethodThree extends AggregationSupport implements Serializable
{
    private static List<AggregationValidationContext> contexts = new ArrayList<AggregationValidationContext>();
    private int count;

    public static List<AggregationValidationContext> getContexts()
    {
        return contexts;
    }

    public void clear()
    {
        count = 0;
    }

    @Override
    public void validate(AggregationValidationContext validationContext)
    {
        contexts.add(validationContext);
    }

    public void enter(Object value)
    {
        Object[] parameters = (Object[]) value;
        int lower = (Integer) parameters[0];
        int upper = (Integer) parameters[1];
        int val = (Integer) parameters[2];
        if ((val >= lower) && (val <= upper))
        {
            count++;
        }
    }

    public void leave(Object value)
    {
        Object[] parameters = (Object[]) value;
        int lower = (Integer) parameters[0];
        int upper = (Integer) parameters[1];
        int val = (Integer) parameters[2];
        if ((val >= lower) && (val <= upper))
        {
            count--;
        }
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
