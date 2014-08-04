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

package com.espertech.esper.epl.agg;

import com.espertech.esper.epl.agg.service.*;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.MethodResolutionServiceImpl;
import com.espertech.esper.epl.expression.ExprAggregateNode;
import com.espertech.esper.support.epl.SupportExprNodeFactory;
import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;

public class TestAggregationServiceFactory extends TestCase
{
    private List<ExprAggregateNode> selectAggregateNodes;
    private List<ExprAggregateNode> havingAggregateNodes;
    private List<ExprAggregateNode> orderByAggregateNodes;
    private MethodResolutionService methodResolutionService;

    public void setUp()
    {
        selectAggregateNodes = new LinkedList<ExprAggregateNode>();
        havingAggregateNodes = new LinkedList<ExprAggregateNode>();
        orderByAggregateNodes = new LinkedList<ExprAggregateNode>();
        methodResolutionService = new MethodResolutionServiceImpl(null, null);
    }

    public void testGetService() throws Exception
    {
        // Test with aggregates but no group by
        selectAggregateNodes.add(SupportExprNodeFactory.makeSumAggregateNode());
        AggregationServiceFactoryDesc service = AggregationServiceFactoryFactory.getService(selectAggregateNodes, havingAggregateNodes, orderByAggregateNodes, false, null, null, null, false, null, null, AggregationServiceFactoryServiceImpl.DEFAULT_FACTORY, null);
        assertTrue(service.getAggregationServiceFactory() instanceof AggSvcGroupAllNoAccessFactory);

        // Test with aggregates and group by
        service = AggregationServiceFactoryFactory.getService(selectAggregateNodes, havingAggregateNodes, orderByAggregateNodes, true, null, null, null, false, null, null, AggregationServiceFactoryServiceImpl.DEFAULT_FACTORY, null);
        assertTrue(service.getAggregationServiceFactory() instanceof AggSvcGroupByRefcountedNoAccessFactory);
    }

    public void testGetNullService() throws Exception
    {
        // Test no aggregates and no group-by
    	AggregationServiceFactoryDesc service = AggregationServiceFactoryFactory.getService(selectAggregateNodes, havingAggregateNodes, orderByAggregateNodes, false, null, null, null, false, null, null, AggregationServiceFactoryServiceImpl.DEFAULT_FACTORY, null);
    	assertTrue(service.getAggregationServiceFactory() instanceof AggregationServiceNullFactory);
    }
}
