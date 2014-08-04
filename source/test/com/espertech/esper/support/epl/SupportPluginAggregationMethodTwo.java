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

public class SupportPluginAggregationMethodTwo extends AggregationSupport implements Serializable
{
    @Override
    public void validate(AggregationValidationContext validationContext)
    {
        throw new IllegalArgumentException("Invalid parameter type '" + validationContext.getParameterTypes()[0].getName() + "', expecting string");
    }

    public void clear()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void enter(Object value)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void leave(Object value)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getValue()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Class getValueType()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
