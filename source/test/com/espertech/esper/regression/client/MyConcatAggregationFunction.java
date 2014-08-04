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

package com.espertech.esper.regression.client;

import com.espertech.esper.epl.agg.service.AggregationSupport;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;

import java.io.Serializable;

public class MyConcatAggregationFunction extends AggregationSupport implements Serializable
{
    private final static char DELIMITER = ' ';
    private StringBuilder builder;
    private String delimiter;

    public MyConcatAggregationFunction()
    {
        super();
        builder = new StringBuilder();
        delimiter = "";
    }

    @Override
    public void validate(AggregationValidationContext validationContext)
    {
        // No need to check the expression node type
    }

    public void clear()
    {
        builder = new StringBuilder();
    }

    public void enter(Object value)
    {
        if (value != null)
        {
            builder.append(delimiter);
            builder.append(value.toString());
            delimiter = String.valueOf(DELIMITER);
        }
    }

    public void leave(Object value)
    {
        if (value != null)
        {
            builder.delete(0, value.toString().length() + 1);
        }
    }

    public Object getValue()
    {
        return builder.toString();
    }

    public Class getValueType()
    {
        return String.class;
    }

}
