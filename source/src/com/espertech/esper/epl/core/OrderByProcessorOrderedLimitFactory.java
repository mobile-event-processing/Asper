/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.epl.agg.service.AggregationService;

/**
 * Sorter and row limiter in one: sorts using a sorter and row limits
 */
public class OrderByProcessorOrderedLimitFactory implements OrderByProcessorFactory
{
    private final OrderByProcessorFactoryImpl orderByProcessorFactory;
    private final OrderByProcessorRowLimitFactory orderByProcessorRowLimitFactory;

    public OrderByProcessorOrderedLimitFactory(OrderByProcessorFactoryImpl orderByProcessorFactory, OrderByProcessorRowLimitFactory orderByProcessorRowLimitFactory) {
        this.orderByProcessorFactory = orderByProcessorFactory;
        this.orderByProcessorRowLimitFactory = orderByProcessorRowLimitFactory;
    }

    public OrderByProcessor instantiate(AggregationService aggregationService) {
        OrderByProcessorImpl orderByProcessor = (OrderByProcessorImpl) orderByProcessorFactory.instantiate(aggregationService);
        OrderByProcessorRowLimit orderByProcessorLimit = (OrderByProcessorRowLimit) orderByProcessorRowLimitFactory.instantiate(aggregationService);
        return new OrderByProcessorOrderedLimit(orderByProcessor, orderByProcessorLimit);
    }
}
