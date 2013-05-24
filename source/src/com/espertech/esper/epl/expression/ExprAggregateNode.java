/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.agg.service.AggregationMethodFactory;
import com.espertech.esper.epl.agg.service.AggregationResultFuture;

/**
 * Base expression node that represents an aggregation function such as 'sum' or 'count'.
 */
public interface ExprAggregateNode extends ExprEvaluator, ExprNode
{
    public AggregationMethodFactory getFactory();
    public void setAggregationResultFuture(AggregationResultFuture aggregationResultFuture, int column);
    public boolean isDistinct();
}
