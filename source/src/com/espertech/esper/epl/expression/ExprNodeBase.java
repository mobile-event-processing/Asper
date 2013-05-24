/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;

/**
 * Superclass for filter nodes in a filter expression tree. Allow
 * validation against stream event types and evaluation of events against filter tree.
 */
public abstract class ExprNodeBase implements ExprNode {
    private static final Log log = LogFactory.getLog(ExprNode.class);
    private static final long serialVersionUID = 0L;

    private final ArrayList<ExprNode> childNodes;

    /**
     * Constructor creates a list of child nodes.
     */
    public ExprNodeBase()
    {
        childNodes = new ArrayList<ExprNode>();
    }

    public void accept(ExprNodeVisitor visitor)
    {
        if (visitor.isVisit(this))
        {
            visitor.visit(this);

            for (ExprNode childNode : childNodes)
            {
                childNode.accept(visitor);
            }
        }
    }

    public void accept(ExprNodeVisitorWithParent visitor)
    {
        if (visitor.isVisit(this))
        {
            visitor.visit(this, null);

            for (ExprNode childNode : childNodes)
            {
                childNode.acceptChildnodes(visitor, this);
            }
        }
    }

    public void acceptChildnodes(ExprNodeVisitorWithParent visitor, ExprNode parent)
    {
        if (visitor.isVisit(this))
        {
            visitor.visit(this, parent);

            for (ExprNode childNode : childNodes)
            {
                childNode.acceptChildnodes(visitor, this);
            }
        }
    }

    public final void addChildNode(ExprNode childNode)
    {
        childNodes.add(childNode);
    }

    public final ArrayList<ExprNode> getChildNodes()
    {
        return childNodes;
    }

    public void replaceUnlistedChildNode(ExprNode nodeToReplace, ExprNode newNode) {
        // Override to replace child expression nodes that are chained or otherwise not listed as child nodes
    }
}
