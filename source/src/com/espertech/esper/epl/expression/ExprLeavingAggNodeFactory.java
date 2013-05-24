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

public class ExprLeavingAggNodeFactory implements AggregationMethodFactory
{
    public ExprLeavingAggNodeFactory()
    {
    }

    public Class getResultType()
    {
        return Boolean.class;
    }

    public AggregationSpec getSpec(boolean isMatchRecognize)
    {
        return null;
    }

    public AggregationMethod make(MethodResolutionService methodResolutionService, int agentInstanceId, int groupId, int aggregationId) {
        return methodResolutionService.makeLeavingAggregator(agentInstanceId, groupId, aggregationId);
    }

    public AggregationMethodFactory getPrototypeAggregator() {
        return this;
    }

    public AggregationAccessor getAccessor()
    {
        throw new UnsupportedOperationException();
    }
}
