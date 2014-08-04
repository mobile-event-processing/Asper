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

package com.espertech.esper.view;

import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.bean.SupportBean_A;
import com.espertech.esper.support.view.SupportStatementContextFactory;
import com.espertech.esper.support.view.SupportStreamImpl;
import com.espertech.esper.support.view.SupportViewSpecFactory;
import junit.framework.TestCase;

public class TestViewServiceImpl extends TestCase
{
    private ViewServiceImpl viewService;

    private Viewable viewOne;
    private Viewable viewTwo;
    private Viewable viewThree;
    private Viewable viewFour;
    private Viewable viewFive;

    private EventStream streamOne;
    private EventStream streamTwo;

    public void setUp() throws Exception
    {
        streamOne = new SupportStreamImpl(SupportBean.class, 1);
        streamTwo = new SupportStreamImpl(SupportBean_A.class, 1);

        viewService = new ViewServiceImpl();

        AgentInstanceViewFactoryChainContext context = SupportStatementContextFactory.makeAgentInstanceViewFactoryContext();

        viewOne = viewService.createViews(streamOne, SupportViewSpecFactory.makeFactoryListOne(streamOne.getEventType()), context, false).getFinalViewable();
        viewTwo = viewService.createViews(streamOne, SupportViewSpecFactory.makeFactoryListTwo(streamOne.getEventType()), context, false).getFinalViewable();
        viewThree = viewService.createViews(streamOne, SupportViewSpecFactory.makeFactoryListThree(streamOne.getEventType()), context, false).getFinalViewable();
        viewFour = viewService.createViews(streamOne, SupportViewSpecFactory.makeFactoryListFour(streamOne.getEventType()), context, false).getFinalViewable();
        viewFive = viewService.createViews(streamTwo, SupportViewSpecFactory.makeFactoryListFive(streamTwo.getEventType()), context, false).getFinalViewable();
    }

    public void testCheckChainReuse()
    {
        // Child views of first and second level must be the same
        assertEquals(2, streamOne.getViews().size());
        View child1_1 = streamOne.getViews().get(0);
        View child2_1 = streamOne.getViews().get(0);
        assertTrue(child1_1 == child2_1);

        assertEquals(2, child1_1.getViews().size());
        View child1_1_1 = child1_1.getViews().get(0);
        View child2_1_1 = child2_1.getViews().get(0);
        assertTrue(child1_1_1 == child2_1_1);

        assertEquals(2, child1_1_1.getViews().size());
        assertEquals(2, child2_1_1.getViews().size());
        assertTrue(child2_1_1.getViews().get(0) != child2_1_1.getViews().get(1));

        // Create one more view chain
        View child3_1 = streamOne.getViews().get(0);
        assertTrue(child3_1 == child1_1);
        assertEquals(2, child3_1.getViews().size());
        View child3_1_1 = child3_1.getViews().get(1);
        assertTrue(child3_1_1 != child2_1_1);
    }

    public void testRemove()
    {
        assertEquals(2, streamOne.getViews().size());
        assertEquals(1, streamTwo.getViews().size());

        viewService.remove(streamOne, viewOne);
        viewService.remove(streamOne, viewTwo);
        viewService.remove(streamOne, viewThree);
        viewService.remove(streamOne, viewFour);

        viewService.remove(streamTwo, viewFive);

        assertEquals(0, streamOne.getViews().size());
        assertEquals(0, streamTwo.getViews().size());
    }

    public void testRemoveInvalid()
    {
        try
        {
            viewService.remove(streamOne, viewOne);
            viewService.remove(streamOne, viewOne);
            TestCase.fail();
        }
        catch (IllegalArgumentException ex)
        {
            // Expected
        }
    }
}