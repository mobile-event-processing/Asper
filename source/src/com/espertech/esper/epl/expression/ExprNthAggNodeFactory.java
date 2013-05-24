/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.agg.access.AggregationAccessor;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationMethodFactory;
import com.espertech.esper.epl.agg.service.AggregationSpec;
import com.espertech.esper.epl.core.MethodResolutionService;

public class ExprNthAggNodeFactory implements AggregationMethodFactory
{
    private final Class childType;
    private int size;
    private final boolean isDistinct;

    public ExprNthAggNodeFactory(Class childType, int size, boolean isDistinct)
    {
        this.childType = childType;
        this.size = size;
        this.isDistinct = isDistinct;
    }

    public Class getResultType()
    {
        return childType;
    }

    public AggregationSpec getSpec(boolean isMatchRecognize)
    {
        return null;
    }

    public AggregationAccessor getAccessor()
    {
        throw new UnsupportedOperationException();
    }

    public AggregationMethod make(MethodResolutionService methodResolutionService, int agentInstanceId, int groupId, int aggregationId) {
        AggregationMethod method = methodResolutionService.makeNthAggregator(agentInstanceId, groupId, aggregationId, childType, size + 1);
        if (!isDistinct) {
            return method;
        }
        return methodResolutionService.makeDistinctAggregator(agentInstanceId, groupId, aggregationId, method, childType, false);
    }

    public AggregationMethodFactory getPrototypeAggregator() {
        return this;
    }
}