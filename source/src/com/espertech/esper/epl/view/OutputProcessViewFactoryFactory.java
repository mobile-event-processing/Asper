/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.view;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.InternalEventRouter;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.core.StreamTypeServiceImpl;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.epl.expression.ExprTimePeriod;
import com.espertech.esper.epl.expression.ExprValidationContext;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.*;

/**
 * Factory for factories for output processing views.
 */
public class OutputProcessViewFactoryFactory
{
    public static OutputProcessViewFactory make(StatementSpecCompiled statementSpec, InternalEventRouter internalEventRouter, StatementContext statementContext, EventType resultEventType, OutputProcessViewCallback optionalOutputProcessViewCallback)
            throws ExprValidationException
    {
        // determine direct-callback
        if (optionalOutputProcessViewCallback != null) {
            return new OutputProcessViewFactoryCallback(optionalOutputProcessViewCallback);
        }

        // determine routing
        boolean isRouted = false;
        boolean routeToFront = false;
        if (statementSpec.getInsertIntoDesc() != null)
        {
            isRouted = true;
            routeToFront = statementContext.getNamedWindowService().isNamedWindow(statementSpec.getInsertIntoDesc().getEventTypeName());
        }

        OutputStrategyPostProcessFactory outputStrategyPostProcessFactory = null;
        if ((statementSpec.getInsertIntoDesc() != null) || (statementSpec.getSelectStreamSelectorEnum() == SelectClauseStreamSelectorEnum.RSTREAM_ONLY))
        {
            SelectClauseStreamSelectorEnum insertIntoStreamSelector = null;
            if (statementSpec.getInsertIntoDesc() != null) {
                insertIntoStreamSelector = statementSpec.getInsertIntoDesc().getStreamSelector();
            }

            outputStrategyPostProcessFactory = new OutputStrategyPostProcessFactory(isRouted, insertIntoStreamSelector, statementSpec.getSelectStreamSelectorEnum(), internalEventRouter, statementContext.getEpStatementHandle(), routeToFront);
        }

        // Do we need to enforce an output policy?
        int streamCount = statementSpec.getStreamSpecs().size();
        OutputLimitSpec outputLimitSpec = statementSpec.getOutputLimitSpec();
        boolean isDistinct = statementSpec.getSelectClauseSpec().isDistinct();
        boolean isGrouped = statementSpec.getGroupByExpressions() != null && !statementSpec.getGroupByExpressions().isEmpty();

        if (outputLimitSpec != null) {
            ExprEvaluatorContextStatement evaluatorContextStmt = new ExprEvaluatorContextStatement(statementContext);
            ExprValidationContext validationContext = new ExprValidationContext(new StreamTypeServiceImpl(statementContext.getEngineURI(), false), statementContext.getMethodResolutionService(), null, statementContext.getTimeProvider(), statementContext.getVariableService(), evaluatorContextStmt, statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
            if (outputLimitSpec.getAfterTimePeriodExpr() != null) {
                ExprTimePeriod timePeriodExpr = (ExprTimePeriod) ExprNodeUtility.getValidatedSubtree(outputLimitSpec.getAfterTimePeriodExpr(), validationContext);
                outputLimitSpec.setAfterTimePeriodExpr(timePeriodExpr);
            }
            if (outputLimitSpec.getTimePeriodExpr() != null) {
                ExprTimePeriod timePeriodExpr = (ExprTimePeriod) ExprNodeUtility.getValidatedSubtree(outputLimitSpec.getTimePeriodExpr(), validationContext);
                outputLimitSpec.setTimePeriodExpr(timePeriodExpr);
            }
        }

        OutputProcessViewFactory outputProcessViewFactory;
        if (outputLimitSpec == null)
        {
            if (!isDistinct)
            {
                outputProcessViewFactory = new OutputProcessViewDirectFactory(statementContext, outputStrategyPostProcessFactory);
            }
            else
            {
                outputProcessViewFactory = new OutputProcessViewDirectDistinctOrAfterFactory(statementContext, outputStrategyPostProcessFactory, isDistinct, null, null, resultEventType);
            }
        }
        else if (outputLimitSpec.getRateType() == OutputLimitRateType.AFTER)
        {
            outputProcessViewFactory = new OutputProcessViewDirectDistinctOrAfterFactory(statementContext, outputStrategyPostProcessFactory, isDistinct, outputLimitSpec.getAfterTimePeriodExpr(), outputLimitSpec.getAfterNumberOfEvents(), resultEventType);
        }
        else
        {
            try {
                boolean isWithHavingClause = statementSpec.getHavingExprRootNode() != null;
                OutputConditionFactory outputConditionFactory = OutputConditionFactoryFactory.createCondition(outputLimitSpec, statementContext, isGrouped, isWithHavingClause);

                OutputProcessViewConditionFactory.ConditionType conditionType;

                if (outputLimitSpec.getDisplayLimit() == OutputLimitLimitType.SNAPSHOT)
                {
                    conditionType = OutputProcessViewConditionFactory.ConditionType.SNAPSHOT;
                }
                // For FIRST without groups we are using a special logic that integrates the first-flag, in order to still conveniently use all sorts of output conditions.
                // FIRST with group-by is handled by setting the output condition to null (OutputConditionNull) and letting the ResultSetProcessor handle first-per-group.
                // Without having-clause there is no required order of processing, thus also use regular policy.
                else if (outputLimitSpec.getDisplayLimit() == OutputLimitLimitType.FIRST && statementSpec.getGroupByExpressions().isEmpty() && isWithHavingClause) {
                    conditionType = OutputProcessViewConditionFactory.ConditionType.POLICY_FIRST;
                }
                else
                {
                    conditionType = OutputProcessViewConditionFactory.ConditionType.POLICY_NONFIRST;
                }

                boolean terminable = outputLimitSpec.getRateType() == OutputLimitRateType.TERM || outputLimitSpec.isAndAfterTerminate();
                outputProcessViewFactory = new OutputProcessViewConditionFactory(statementContext, outputStrategyPostProcessFactory, isDistinct, outputLimitSpec.getAfterTimePeriodExpr(), outputLimitSpec.getAfterNumberOfEvents(), resultEventType, outputConditionFactory, streamCount, conditionType, outputLimitSpec.getDisplayLimit(), terminable);
            }
            catch (RuntimeException ex) {
                throw new ExprValidationException("Error in the output rate limiting clause: " + ex.getMessage(), ex);
            }
        }

        return outputProcessViewFactory;
    }
}
