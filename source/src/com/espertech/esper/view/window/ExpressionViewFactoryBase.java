/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.window;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.agg.service.AggregationServiceFactoryDesc;
import com.espertech.esper.epl.agg.service.AggregationServiceFactoryFactory;
import com.espertech.esper.epl.agg.service.AggregationServiceFactoryServiceImpl;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.StreamTypeServiceImpl;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.event.map.MapEventBean;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.view.*;

import java.util.*;

/**
 * Base factory for expression-based window and batch view.
 */
public abstract class ExpressionViewFactoryBase implements DataWindowViewFactory, DataWindowViewWithPrevious
{
    private EventType eventType;
    protected ExprNode expiryExpression;
    protected MapEventBean builtinMapBean;
    protected Set<String> variableNames;
    protected AggregationServiceFactoryDesc aggregationServiceFactoryDesc;

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException
    {
        if (expressionParameters.size() != 1) {
            String errorMessage = "Expression window view requires a single expression as a parameter";
            throw new ViewParameterException(errorMessage);
        }
        expiryExpression = expressionParameters.get(0);
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException
    {
        this.eventType = parentEventType;

        // define built-in fields
        Map<String, Object> builtinTypeDef = new LinkedHashMap<String, Object>();
        builtinTypeDef.put(ExpressionViewUtil.CURRENT_COUNT, Integer.class);
        builtinTypeDef.put(ExpressionViewUtil.OLDEST_TIMESTAMP, Long.class);
        builtinTypeDef.put(ExpressionViewUtil.NEWEST_TIMESTAMP, Long.class);
        builtinTypeDef.put(ExpressionViewUtil.EXPIRED_COUNT, Integer.class);
        builtinTypeDef.put(ExpressionViewUtil.VIEW_REFERENCE, Object.class);
        EventType builtinMapType = statementContext.getEventAdapterService().createAnonymousMapType(statementContext.getStatementId() + "_exprview", builtinTypeDef);
        builtinMapBean = new MapEventBean(new HashMap<String, Object>(), builtinMapType);
        StreamTypeService streamTypeService = new StreamTypeServiceImpl(new EventType[] {eventType, builtinMapType}, new String[2], new boolean[2], statementContext.getEngineURI(), false);

        // validate expression
        expiryExpression = ViewFactorySupport.validateExpr(statementContext, expiryExpression, streamTypeService, 0);

        ExprNodeSummaryVisitor summaryVisitor = new ExprNodeSummaryVisitor();
        expiryExpression.accept(summaryVisitor);
        if (summaryVisitor.isHasSubselect() || summaryVisitor.isHasStreamSelect() || summaryVisitor.isHasPreviousPrior()) {
            throw new ViewParameterException("Invalid expiry expression: Sub-select, previous or prior functions are not supported in this context");
        }

        Class returnType = expiryExpression.getExprEvaluator().getType();
        if (JavaClassHelper.getBoxedType(returnType) != Boolean.class) {
            throw new ViewParameterException("Invalid return value for expiry expression, expected a boolean return value but received " + JavaClassHelper.getParameterAsString(returnType));
        }

        // determine variables used, if any
        ExprNodeVariableVisitor visitor = new ExprNodeVariableVisitor();
        expiryExpression.accept(visitor);
        variableNames = visitor.getVariableNames();

        // determine aggregation nodes, if any
        List<ExprAggregateNode> aggregateNodes = new ArrayList<ExprAggregateNode>();
        ExprAggregateNodeUtil.getAggregatesBottomUp(expiryExpression, aggregateNodes);
        if (!aggregateNodes.isEmpty()) {
            try {
                aggregationServiceFactoryDesc = AggregationServiceFactoryFactory.getService(Collections.<ExprAggregateNode>emptyList(), aggregateNodes, Collections.<ExprAggregateNode>emptyList(), false, new ExprEvaluatorContextStatement(statementContext), statementContext.getAnnotations(), statementContext.getVariableService(), false, null, null, AggregationServiceFactoryServiceImpl.DEFAULT_FACTORY, streamTypeService.getEventTypes());
            }
            catch (ExprValidationException ex) {
                throw new ViewParameterException(ex.getMessage(), ex);
            }
        }
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public boolean canReuse(View view)
    {
        return false;
    }
}
