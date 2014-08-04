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

package com.espertech.esper.multithread;

import com.espertech.esper.epl.agg.service.AggregationSupport;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;

import java.util.ArrayList;
import java.util.List;

public class MyIntListAggregation extends AggregationSupport {

    private List<Integer> values = new ArrayList<Integer>();

    @Override
    public void validate(AggregationValidationContext validationContext) {
    }

    @Override
    public void enter(Object value) {
        values.add((Integer)value);
    }

    @Override
    public void leave(Object value) {
    }

    @Override
    public Object getValue() {
        return new ArrayList<Integer>(values);
    }

    @Override
    public Class getValueType() {
        return List.class;
    }

    @Override
    public void clear() {
        values.clear();
    }
}
