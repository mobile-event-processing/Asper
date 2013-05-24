/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.epl.expression.ExprNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class represents a match-until observer in the evaluation tree representing any event expressions.
 */
public class EvalMatchUntilFactoryNode extends EvalNodeFactoryBase
{
    private static final long serialVersionUID = 5697835058233579562L;
    private ExprNode lowerBounds;
    private ExprNode upperBounds;
    private transient MatchedEventConvertor convertor;
    private int[] tagsArrayed;

    /**
     * Ctor.
     */
    protected EvalMatchUntilFactoryNode(ExprNode lowerBounds, ExprNode upperBounds)
    {
        this.lowerBounds = lowerBounds;
        this.upperBounds = upperBounds;
    }

    public EvalNode makeEvalNode(PatternAgentInstanceContext agentInstanceContext) {
        EvalNode[] children = EvalNodeUtil.makeEvalNodeChildren(this.getChildNodes(), agentInstanceContext);
        return new EvalMatchUntilNode(agentInstanceContext, this, children[0], children.length == 1 ? null : children[1]);
    }

    /**
     * Returns an array of tags for events, which is all tags used within the repeat-operator.
     * @return array of tags
     */
    public int[] getTagsArrayed()
    {
        return tagsArrayed;
    }

    /**
     * Sets the convertor for matching events to events-per-stream.
     * @param convertor convertor
     */
    public void setConvertor(MatchedEventConvertor convertor) {
        this.convertor = convertor;
    }

    public ExprNode getLowerBounds() {
        return lowerBounds;
    }

    public ExprNode getUpperBounds() {
        return upperBounds;
    }

    public void setLowerBounds(ExprNode lowerBounds) {
        this.lowerBounds = lowerBounds;
    }

    public void setUpperBounds(ExprNode upperBounds) {
        this.upperBounds = upperBounds;
    }

    /**
     * Sets the tags used within the repeat operator.
     * @param tagsArrayedSet tags used within the repeat operator
     */
    public void setTagsArrayedSet(int[] tagsArrayedSet)
    {
        tagsArrayed = tagsArrayedSet;
    }

    public MatchedEventConvertor getConvertor() {
        return convertor;
    }

    public final String toString()
    {
        return ("EvalMatchUntilNode children=" + this.getChildNodes().size());
    }

    public boolean isFilterChildNonQuitting() {
        return true;
    }

    public boolean isStateful() {
        return true;
    }

    private static final Log log = LogFactory.getLog(EvalMatchUntilFactoryNode.class);
}
