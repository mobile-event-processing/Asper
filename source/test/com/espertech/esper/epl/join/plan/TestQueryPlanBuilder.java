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

package com.espertech.esper.epl.join.plan;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.service.StreamJoinAnalysisResult;
import com.espertech.esper.epl.join.base.HistoricalViewableDesc;
import com.espertech.esper.epl.spec.OuterJoinDesc;
import com.espertech.esper.support.bean.SupportBean_S0;
import com.espertech.esper.support.bean.SupportBean_S1;
import com.espertech.esper.support.epl.SupportExprNodeFactory;
import com.espertech.esper.support.epl.SupportOuterJoinDescFactory;
import com.espertech.esper.support.event.SupportEventAdapterService;
import com.espertech.esper.type.OuterJoinType;
import com.espertech.esper.util.DependencyGraph;
import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;

public class TestQueryPlanBuilder extends TestCase
{
    private EventType[] typesPerStream;
    private boolean[] isHistorical;
    private DependencyGraph dependencyGraph;

    public void setUp()
    {
        typesPerStream = new EventType[] {
                SupportEventAdapterService.getService().addBeanType(SupportBean_S0.class.getName(), SupportBean_S0.class, true, true, true),
                SupportEventAdapterService.getService().addBeanType(SupportBean_S1.class.getName(), SupportBean_S1.class, true, true, true)
        };
        dependencyGraph = new DependencyGraph(2, false);
        isHistorical = new boolean[2];
    }

    public void testGetPlan() throws Exception
    {
        List<OuterJoinDesc> descList = new LinkedList<OuterJoinDesc>();
        OuterJoinDesc joinDesc = SupportOuterJoinDescFactory.makeDesc("intPrimitive", "s0", "intBoxed", "s1", OuterJoinType.LEFT);
        descList.add(joinDesc);

        QueryGraph queryGraph = new QueryGraph(2);
        QueryPlan plan = QueryPlanBuilder.getPlan(typesPerStream, new LinkedList<OuterJoinDesc>(), queryGraph, null, new HistoricalViewableDesc(5), dependencyGraph, null, new StreamJoinAnalysisResult(2), true, null, null);
        assertPlan(plan);

        plan = QueryPlanBuilder.getPlan(typesPerStream, descList, queryGraph, null, new HistoricalViewableDesc(5), dependencyGraph, null, new StreamJoinAnalysisResult(2), true, null, null);
        assertPlan(plan);

        FilterExprAnalyzer.analyze(SupportExprNodeFactory.makeEqualsNode(), queryGraph, false);
        plan = QueryPlanBuilder.getPlan(typesPerStream, descList, queryGraph, null, new HistoricalViewableDesc(5), dependencyGraph, null, new StreamJoinAnalysisResult(2), true, null, null);
        assertPlan(plan);

        plan = QueryPlanBuilder.getPlan(typesPerStream, new LinkedList<OuterJoinDesc>(), queryGraph, null, new HistoricalViewableDesc(5), dependencyGraph, null, new StreamJoinAnalysisResult(2), true, null, null);
        assertPlan(plan);
    }

    private void assertPlan(QueryPlan plan)
    {
        assertEquals(2, plan.getExecNodeSpecs().length);
        assertEquals(2, plan.getExecNodeSpecs().length);
    }
}
