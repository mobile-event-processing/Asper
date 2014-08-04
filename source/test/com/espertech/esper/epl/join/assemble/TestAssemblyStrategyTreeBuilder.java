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

package com.espertech.esper.epl.join.assemble;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.HashMap;

public class TestAssemblyStrategyTreeBuilder extends TestCase
{
    public void testInvalidBuild()
    {
        // root stream out of bounds
        tryInvalidBuild(3, convert(new int[][] {{1, 2}, {}, {}}), new boolean[] {true, true, true});
        tryInvalidBuild(-1, convert(new int[][] {{1, 2}, {}, {}}), new boolean[] {true, true, true});

        // not matching outer-inner
        tryInvalidBuild(0, convert(new int[][] {{1, 2}, {}, {}}), new boolean[] {true, true});

        // stream relationships not filled
        tryInvalidBuild(0, convert(new int[][] {{1, 2}}), new boolean[] {true, true, true});

        // stream relationships duplicates
        tryInvalidBuild(0, convert(new int[][] {{1, 2}, {1}, {}}), new boolean[] {true, true});
        tryInvalidBuild(0, convert(new int[][] {{1, 2}, {}, {2}}), new boolean[] {true, true, true});

        // stream relationships out of range
        tryInvalidBuild(0, convert(new int[][] {{1, 3}, {}, {}}), new boolean[] {true, true});

        // stream relationships missing stream
        tryInvalidBuild(0, convert(new int[][] {{1}, {}, {}}), new boolean[] {true, true});
    }

    public void testValidBuildSimpleReqOpt()
    {
        BaseAssemblyNode node = AssemblyStrategyTreeBuilder.build(2, convert(new int[][] {{}, {0}, {1}}), new boolean[] {false, true, true});

        RootRequiredAssemblyNode child1 = (RootRequiredAssemblyNode) node;
        assertEquals(2, child1.getStreamNum());
        assertEquals(1, child1.getChildNodes().size());
        assertEquals(null, child1.getParentAssembler());

        BranchOptionalAssemblyNode child1_1 = (BranchOptionalAssemblyNode) child1.getChildNodes().get(0);
        assertEquals(1, child1_1.getStreamNum());
        assertEquals(1, child1_1.getChildNodes().size());
        assertEquals(child1, child1_1.getParentAssembler());

        LeafAssemblyNode leaf1_2 = (LeafAssemblyNode) child1_1.getChildNodes().get(0);
        assertEquals(0, leaf1_2.getStreamNum());
        assertEquals(0, leaf1_2.getChildNodes().size());
        assertEquals(child1_1, leaf1_2.getParentAssembler());
    }

    public void testValidBuildSimpleOptReq()
    {
        BaseAssemblyNode node = AssemblyStrategyTreeBuilder.build(2, convert(new int[][] {{}, {0}, {1}}), new boolean[] {true, false, true});

        RootOptionalAssemblyNode child1 = (RootOptionalAssemblyNode) node;
        assertEquals(2, child1.getStreamNum());
        assertEquals(1, child1.getChildNodes().size());
        assertEquals(null, child1.getParentAssembler());

        BranchRequiredAssemblyNode child1_1 = (BranchRequiredAssemblyNode) child1.getChildNodes().get(0);
        assertEquals(1, child1_1.getStreamNum());
        assertEquals(1, child1_1.getChildNodes().size());
        assertEquals(child1, child1_1.getParentAssembler());

        LeafAssemblyNode leaf1_2 = (LeafAssemblyNode) child1_1.getChildNodes().get(0);
        assertEquals(0, leaf1_2.getStreamNum());
        assertEquals(0, leaf1_2.getChildNodes().size());
        assertEquals(child1_1, leaf1_2.getParentAssembler());
    }

    public void testValidBuildCartesian()
    {
        BaseAssemblyNode node = AssemblyStrategyTreeBuilder.build(1, convert(new int[][] {{}, {0,2}, {}}), new boolean[] {false, true, false});

        RootCartProdAssemblyNode top = (RootCartProdAssemblyNode) node;
        assertEquals(2, top.getChildNodes().size());

        LeafAssemblyNode leaf1 = (LeafAssemblyNode) top.getChildNodes().get(0);
        assertEquals(0, leaf1.getStreamNum());
        assertEquals(0, leaf1.getChildNodes().size());
        assertEquals(top, leaf1.getParentAssembler());

        LeafAssemblyNode leaf2 = (LeafAssemblyNode) top.getChildNodes().get(0);
        assertEquals(0, leaf2.getStreamNum());
        assertEquals(0, leaf2.getChildNodes().size());
        assertEquals(top, leaf2.getParentAssembler());
    }

    private void tryInvalidBuild(int rootStream, Map<Integer, int[]> joinedPerStream, boolean[] isInnerPerStream)
    {
        try
        {
            AssemblyStrategyTreeBuilder.build(rootStream, joinedPerStream, isInnerPerStream);
            fail();
        }
        catch (IllegalArgumentException ex)
        {
            log.debug(".tryInvalidBuild expected exception=" + ex);
            // expected
        }
    }

    private Map<Integer, int[]> convert(int[][] array)
    {
        Map<Integer, int[]> result = new HashMap<Integer, int[]>();
        for (int i = 0; i < array.length; i++)
        {
            result.put(i, array[i]);
        }
        return result;
    }

    private static final Log log = LogFactory.getLog(TestAssemblyStrategyTreeBuilder.class);
}
