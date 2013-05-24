/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import java.util.List;

/**
 * Superclass of all nodes in an evaluation tree representing an event pattern expression.
 * Follows the Composite pattern. Child nodes do not carry references to parent nodes, the tree
 * is unidirectional.
 */
public interface EvalFactoryNode
{
    /**
     * Adds a child node.
     * @param childNode is the child evaluation tree node to add
     */
    public void addChildNode(EvalFactoryNode childNode);

    /**
     * Returns list of child nodes
     * @return list of child nodes
     */
    public List<EvalFactoryNode> getChildNodes();

    public void addChildNodes(List<EvalFactoryNode> childNodes);

    public EvalNode makeEvalNode(PatternAgentInstanceContext agentInstanceContext);

    public boolean isFilterChildNonQuitting();

    public short getFactoryNodeId();
    public void setFactoryNodeId(short factoryNodeId);

    public boolean isStateful();
}
