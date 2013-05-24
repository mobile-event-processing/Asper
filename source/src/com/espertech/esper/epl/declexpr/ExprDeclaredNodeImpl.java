/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.declexpr;

import com.espertech.esper.client.EventType;
import com.espertech.esper.client.annotation.Audit;
import com.espertech.esper.client.annotation.AuditEnum;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.StreamTypeServiceImpl;
import com.espertech.esper.epl.enummethod.dot.ExprDeclaredOrLambdaNode;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.spec.ExpressionDeclItem;
import com.espertech.esper.util.SerializableObjectCopier;

import java.util.ArrayList;
import java.util.List;

/**
 * Expression instance as declared elsewhere.
 */
public class ExprDeclaredNodeImpl extends ExprNodeBase implements ExprDeclaredNode, ExprDeclaredOrLambdaNode
{
    private static final long serialVersionUID = 9140100131374697808L;

    private final ExpressionDeclItem prototype;
    private List<ExprNode> chainParameters;
    private transient ExprEvaluator exprEvaluator;
    private ExprNode expressionBodyCopy;

    public ExprDeclaredNodeImpl(ExpressionDeclItem prototype, List<ExprNode> chainParameters) {
        this.prototype = prototype;
        this.chainParameters = chainParameters;

        // copy expression - we do it at this time and not later
        try {
             expressionBodyCopy = (ExprNode) SerializableObjectCopier.copy(prototype.getInner());
        } catch (Exception e) {
            throw new RuntimeException("Internal error providing expression tree: " + e.getMessage(), e);
        }
    }

    public boolean validated() {
        return exprEvaluator != null;
    }

    /**
     * Received indication of the streams and types available before sub-selects are started and expression validation occurs.
     */
    public void setSubselectOuterStreamNames(String[] outerStreamNames,
                                             EventType[] outerEventTypesSelect,
                                             String[] outerEventTypeNames,
                                             String engineURI,
                                             ExprSubselectNode subselect,
                                             String subexpressionStreamName,
                                             EventType subselectStreamType,
                                             String subselecteventTypeName)
        throws ExprValidationException
    {
        checkParameterCount();

        // determine stream ids for each parameter
        int[] streamParameters = new int[chainParameters.size()];
        for (int param = 0; param < chainParameters.size(); param++) {
            if (!(chainParameters.get(param) instanceof ExprIdentNode)) {
                throw new ExprValidationException("Sub-selects in an expression declaration require passing only stream names as parameters");
            }
            String parameterName = ((ExprIdentNode) chainParameters.get(param)).getUnresolvedPropertyName();
            int streamId = -1;
            for (int i =0; i < outerStreamNames.length; i++) {
                if (parameterName.equals(outerStreamNames[i])) {
                    streamId  = i;
                    break;
                }
            }
            if (streamId == -1) {
                throw new ExprValidationException("Invalid parameter to expression declaration, parameter " + param + " is not the name of a stream in the query");
            }
            streamParameters[param] = streamId;
        }

        // compile a new StreamTypeService for use in validating that particular subselect
        EventType[] eventTypes = new EventType[chainParameters.size() + 1];
        String[] streamNames = new String[chainParameters.size() + 1];
        eventTypes[0] = subselectStreamType;
        streamNames[0] = subexpressionStreamName;
        for (int i = 0; i < streamParameters.length; i++) {
            eventTypes[i+1] = outerEventTypesSelect[streamParameters[i]];
            streamNames[i+1] = prototype.getParametersNames().get(i);
        }

        StreamTypeServiceImpl availableTypes = new StreamTypeServiceImpl(eventTypes, streamNames, new boolean[eventTypes.length], engineURI, false);
        availableTypes.setRequireStreamNames(true);

        // apply
        subselect.setFilterSubqueryStreamTypes(availableTypes);
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException
    {
        if (exprEvaluator != null) {
            return; // already evaluated
        }

        if (!this.getChildNodes().isEmpty()) {
            throw new IllegalStateException("Execution node has its own child nodes");
        }

        // validate chain
        List<ExprNode> validated = new ArrayList<ExprNode>();
        for (ExprNode expr : chainParameters) {
            validated.add(ExprNodeUtility.getValidatedSubtree(expr, validationContext));
        }
        chainParameters = validated;

        // validate parameter count
        checkParameterCount();

        // create context for expression body
        EventType[] eventTypes = new EventType[prototype.getParametersNames().size()];
        String[] streamNames = new String[prototype.getParametersNames().size()];
        boolean[] isIStreamOnly = new boolean[prototype.getParametersNames().size()];
        int[] streamsIdsPerStream = new int[prototype.getParametersNames().size()];
        boolean allStreamIdsMatch = true;

        for (int i = 0; i < prototype.getParametersNames().size(); i++) {
            ExprNode parameter = chainParameters.get(i);
            if (!(parameter instanceof ExprStreamUnderlyingNode)) {
                throw new ExprValidationException("Expression '" + prototype.getName() + "' requires a stream name as a parameter");
            }
            ExprStreamUnderlyingNode und = (ExprStreamUnderlyingNode) parameter;
            eventTypes[i] = validationContext.getStreamTypeService().getEventTypes()[und.getStreamId()];
            isIStreamOnly[i] = validationContext.getStreamTypeService().getIStreamOnly()[und.getStreamId()];
            streamNames[i] = prototype.getParametersNames().get(i);
            streamsIdsPerStream[i] = und.getStreamId();

            if (und.getStreamId() != i) {
                allStreamIdsMatch = false;
            }
        }

        StreamTypeService streamTypeService = validationContext.getStreamTypeService();
        StreamTypeServiceImpl copyTypes = new StreamTypeServiceImpl(eventTypes, streamNames, isIStreamOnly, streamTypeService.getEngineURIQualifier(), streamTypeService.isOnDemandStreams());
        copyTypes.setRequireStreamNames(true);

        // validate expression body in this context
        try {
            ExprValidationContext expressionBodyContext = new ExprValidationContext(copyTypes, validationContext);
            expressionBodyCopy = ExprNodeUtility.getValidatedSubtree(expressionBodyCopy, expressionBodyContext);
        }
        catch (ExprValidationException ex) {
            String message = "Error validating expression declaration '" + prototype.getName() + "': " + ex.getMessage();
            throw new ExprValidationException(message, ex);
        }

        // add child node
        this.addChildNode(expressionBodyCopy);

        // analyze child node
        ExprNodeSummaryVisitor summaryVisitor = new ExprNodeSummaryVisitor();
        expressionBodyCopy.accept(summaryVisitor);
        boolean isCache;
        if (summaryVisitor.isHasAggregation() || summaryVisitor.isHasPreviousPrior()) {
            isCache = false;
        }
        else {
            isCache = true;
        }

        // determine a suitable evaluation
        if (expressionBodyCopy.isConstantResult()) {
            // pre-evaluated
            exprEvaluator = new ExprDeclaredEvalConstant(expressionBodyCopy.getExprEvaluator().getType(), expressionBodyCopy.getExprEvaluator().evaluate(null, true, null));
        }
        else if (prototype.getParametersNames().isEmpty() ||
                (allStreamIdsMatch && prototype.getParametersNames().size() == streamTypeService.getEventTypes().length)) {
            exprEvaluator = new ExprDeclaredEvalNoRewrite(expressionBodyCopy.getExprEvaluator(), prototype, isCache);
        }
        else {
            exprEvaluator = new ExprDeclaredEvalRewrite(expressionBodyCopy.getExprEvaluator(), prototype, isCache, streamsIdsPerStream);
        }

        Audit audit = AuditEnum.EXPRDEF.getAudit(validationContext.getAnnotations());
        if (audit != null) {
            exprEvaluator = (ExprEvaluator) ExprEvaluatorProxy.newInstance(validationContext.getStreamTypeService().getEngineURIQualifier(), validationContext.getStatementName(), prototype.getName(), exprEvaluator);
        }
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprDeclaredNodeImpl))
        {
            return false;
        }

        ExprDeclaredNodeImpl otherExprCaseNode = (ExprDeclaredNodeImpl) node;
        return expressionBodyCopy.equalsNode(otherExprCaseNode.getExpressionBodyCopy());
    }

    public void accept(ExprNodeVisitor visitor) {
        super.accept(visitor);
        if (this.getChildNodes().isEmpty()) {
            expressionBodyCopy.accept(visitor);
        }
    }

    public void accept(ExprNodeVisitorWithParent visitor) {
        super.accept(visitor);
        if (this.getChildNodes().isEmpty()) {
            expressionBodyCopy.accept(visitor);
        }
    }

    public void acceptChildnodes(ExprNodeVisitorWithParent visitor, ExprNode parent) {
        super.acceptChildnodes(visitor, parent);
        if (this.getChildNodes().isEmpty()) {
            expressionBodyCopy.accept(visitor);
        }
    }

    public ExprNode getExpressionBodyCopy() {
        return expressionBodyCopy;
    }

    public ExpressionDeclItem getPrototype() {
        return prototype;
    }

    public List<ExprNode> getChainParameters() {
        return chainParameters;
    }

    public ExprEvaluator getExprEvaluator() {
        return exprEvaluator;
    }

    private void checkParameterCount() throws ExprValidationException {
        if (chainParameters.size() != prototype.getParametersNames().size()) {
            throw new ExprValidationException("Parameter count mismatches for declared expression '" + prototype.getName() + "', expected " +
                prototype.getParametersNames().size() + " parameters but received " + chainParameters.size() + " parameters");
        }
    }

    public String toExpressionString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append(prototype.getName() + "(");
        String delimiter = "";
        for (ExprNode parameter : chainParameters) {
            buf.append(delimiter);
            buf.append(parameter.toExpressionString());
            delimiter = ", ";
        }
        buf.append(")");
        return buf.toString();
    }
}