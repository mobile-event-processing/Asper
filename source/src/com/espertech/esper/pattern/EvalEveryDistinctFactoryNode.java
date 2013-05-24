/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * This class represents an 'every-distinct' operator in the evaluation tree representing an event expression.
 */
public class EvalEveryDistinctFactoryNode extends EvalNodeFactoryBase
{
    protected List<ExprNode> expressions;
    protected transient ExprEvaluator[] distinctExpressionsArray;
    private transient MatchedEventConvertor convertor;
    private Long msecToExpire;
    protected List<ExprNode> distinctExpressions;
    private static final long serialVersionUID = 7455570958072753956L;

    /**
     * Ctor.
     * @param expressions distinct-value expressions
     */
    protected EvalEveryDistinctFactoryNode(List<ExprNode> expressions)
    {
        this.expressions = expressions;
    }

    public EvalNode makeEvalNode(PatternAgentInstanceContext agentInstanceContext) {
        if (distinctExpressionsArray == null) {
            distinctExpressionsArray = ExprNodeUtility.getEvaluators(distinctExpressions);
        }
        EvalNode child = EvalNodeUtil.makeEvalNodeSingleChild(this.getChildNodes(), agentInstanceContext);
        return new EvalEveryDistinctNode(this, child, agentInstanceContext);
    }

    public ExprEvaluator[] getDistinctExpressionsArray() {
        return distinctExpressionsArray;
    }

    public MatchedEventConvertor getConvertor() {
        return convertor;
    }

    public Long getMsecToExpire() {
        return msecToExpire;
    }

    public final String toString()
    {
        return "EvalEveryNode children=" + this.getChildNodes().size();
    }

    /**
     * Returns all expressions.
     * @return expressions
     */
    public List<ExprNode> getExpressions()
    {
        return expressions;
    }

    /**
     * Returns distinct expressions.
     * @return expressions
     */
    public List<ExprNode> getDistinctExpressions() {
        return distinctExpressions;
    }

    /**
     * Sets the convertor for matching events to events-per-stream.
     * @param convertor convertor
     */
    public void setConvertor(MatchedEventConvertor convertor)
    {
        this.convertor = convertor;
    }

    /**
     * Sets expressions for distinct-value.
     * @param distinctExpressions to set
     */
    public void setDistinctExpressions(List<ExprNode> distinctExpressions, Long msecToExpire)
    {
        this.distinctExpressions = distinctExpressions;
        this.msecToExpire = msecToExpire;
    }

    public boolean isFilterChildNonQuitting() {
        return true;
    }

    public boolean isStateful() {
        return true;
    }

    private static final Log log = LogFactory.getLog(EvalEveryNode.class);
}
