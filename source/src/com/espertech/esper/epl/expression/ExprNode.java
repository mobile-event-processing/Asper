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

package com.espertech.esper.epl.expression;

import com.espertech.esper.util.MetaDefItem;

import java.io.Serializable;
import java.util.ArrayList;

public interface ExprNode extends ExprValidator, MetaDefItem, Serializable {
    
    public ExprEvaluator getExprEvaluator();

    /**
     * Returns the expression node rendered as a string.
     * @return string rendering of expression
     */
    public String toExpressionString();

    /**
     * Returns true if the expression node's evaluation value doesn't depend on any events data,
     * as must be determined at validation time, which is bottom-up and therefore
     * reliably allows each node to determine constant value.
     * @return true for constant evaluation value, false for non-constant evaluation value
     */
    public boolean isConstantResult();

    /**
     * Return true if a expression node semantically equals the current node, or false if not.
     * <p>Concrete implementations should compare the type and any additional information
     * that impact the evaluation of a node.
     * @param node to compare to
     * @return true if semantically equal, or false if not equals
     */
    public boolean equalsNode(ExprNode node);

    /**
     * Accept the visitor. The visitor will first visit the parent then visit all child nodes, then their child nodes.
     * <p>The visitor can decide to skip child nodes by returning false in isVisit.
     * @param visitor to visit each node and each child node.
     */
    public void accept(ExprNodeVisitor visitor);

    /**
     * Accept the visitor. The visitor will first visit the parent then visit all child nodes, then their child nodes.
     * <p>The visitor can decide to skip child nodes by returning false in isVisit.
     *
     * @param visitor to visit each node and each child node.
     */
    public void accept(ExprNodeVisitorWithParent visitor);

    /**
     * Accept a visitor that receives both parent and child node.
     * @param visitor to apply
     * @param parent node
     */
    public void acceptChildnodes(ExprNodeVisitorWithParent visitor, ExprNode parent);

    /**
     * Adds a child node.
     * @param childNode is the child evaluation tree node to add
     */
    public void addChildNode(ExprNode childNode);

    /**
     * Returns list of child nodes.
     * @return list of child nodes
     */
    public ArrayList<ExprNode> getChildNodes();

    public void replaceUnlistedChildNode(ExprNode nodeToReplace, ExprNode newNode);
}
