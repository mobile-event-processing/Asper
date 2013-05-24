/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.start;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.factory.StatementAgentInstanceFactoryUpdate;
import com.espertech.esper.core.context.factory.StatementAgentInstanceFactoryUpdateResult;
import com.espertech.esper.core.context.mgr.ContextManagedStatementOnTriggerDesc;
import com.espertech.esper.core.context.stmt.AIRegistryExpr;
import com.espertech.esper.core.context.stmt.AIRegistrySubselect;
import com.espertech.esper.core.context.subselect.SubSelectActivationCollection;
import com.espertech.esper.core.context.subselect.SubSelectStrategyCollection;
import com.espertech.esper.core.context.subselect.SubSelectStrategyHolder;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.context.util.ContextMergeView;
import com.espertech.esper.core.service.*;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.StreamTypeServiceImpl;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.ViewProcessingException;
import com.espertech.esper.view.Viewable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Starts and provides the stop method for EPL statements.
 */
public class EPStatementStartMethodUpdate extends EPStatementStartMethodBase
{
    private static final Log log = LogFactory.getLog(EPStatementStartMethodUpdate.class);

    public EPStatementStartMethodUpdate(StatementSpecCompiled statementSpec) {
        super(statementSpec);
    }

    public EPStatementStartResult startInternal(final EPServicesContext services, final StatementContext statementContext, boolean isNewStatement, boolean isRecoveringStatement, boolean isRecoveringResilient) throws ExprValidationException, ViewProcessingException {
        // define stop
        final List<StopCallback> stopCallbacks = new LinkedList<StopCallback>();
        final EPStatementStopMethod stopMethod = new EPStatementStopMethodImpl(statementContext, stopCallbacks);

        // determine context
        final String contextName = statementSpec.getOptionalContextName();
        if (contextName != null) {
            throw new ExprValidationException("Update IStream is not supported in conjunction with a context");
        }

        // First we create streams for subselects, if there are any
        SubSelectActivationCollection subSelectStreamDesc = EPStatementStartMethodHelperSubselect.createSubSelectActivation(services, statementSpec, statementContext);

        final StreamSpecCompiled streamSpec = statementSpec.getStreamSpecs().get(0);
        final UpdateDesc updateSpec = statementSpec.getUpdateSpec();
        String triggereventTypeName;

        if (streamSpec instanceof FilterStreamSpecCompiled)
        {
            FilterStreamSpecCompiled filterStreamSpec = (FilterStreamSpecCompiled) streamSpec;
            triggereventTypeName = filterStreamSpec.getFilterSpec().getFilterForEventTypeName();
        }
        else if (streamSpec instanceof NamedWindowConsumerStreamSpec)
        {
            NamedWindowConsumerStreamSpec namedSpec = (NamedWindowConsumerStreamSpec) streamSpec;
            triggereventTypeName = namedSpec.getWindowName();
        }
        else
        {
            throw new ExprValidationException("Unknown stream specification streamEventType: " + streamSpec);
        }

        // determine a stream name
        String streamName = triggereventTypeName;
        if (updateSpec.getOptionalStreamName() != null)
        {
            streamName = updateSpec.getOptionalStreamName();
        }

        final EventType streamEventType = services.getEventAdapterService().getExistsTypeByName(triggereventTypeName);
        StreamTypeService typeService = new StreamTypeServiceImpl(new EventType[] {streamEventType}, new String[] {streamName}, new boolean[] {true}, services.getEngineURI(), false);

        // determine subscriber result types
        ExprEvaluatorContextStatement evaluatorContextStmt = new ExprEvaluatorContextStatement(statementContext);
        statementContext.getStatementResultService().setSelectClause(new Class[] {streamEventType.getUnderlyingType()}, new String[] {"*"}, false, null, evaluatorContextStmt);

        // Materialize sub-select views
        SubSelectStrategyCollection subSelectStrategyCollection = EPStatementStartMethodHelperSubselect.planSubSelect(services, statementContext, isQueryPlanLogging(services), subSelectStreamDesc, new String[]{streamName}, new EventType[]{streamEventType}, new String[]{triggereventTypeName}, stopCallbacks, statementSpec.getAnnotations(), statementSpec.getDeclaredExpressions(), null);

        ExprValidationContext validationContext = new ExprValidationContext(typeService, statementContext.getMethodResolutionService(), null, statementContext.getSchedulingService(), statementContext.getVariableService(), evaluatorContextStmt, statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
        for (OnTriggerSetAssignment assignment : updateSpec.getAssignments())
        {
            ExprNode validated = ExprNodeUtility.getValidatedSubtree(assignment.getExpression(), validationContext);
            assignment.setExpression(validated);
            EPStatementStartMethodHelperValidate.validateNoAggregations(validated, "Aggregation functions may not be used within an update-clause");
        }
        if (updateSpec.getOptionalWhereClause() != null)
        {
            ExprNode validated = ExprNodeUtility.getValidatedSubtree(updateSpec.getOptionalWhereClause(), validationContext);
            updateSpec.setOptionalWhereClause(validated);
            EPStatementStartMethodHelperValidate.validateNoAggregations(validated, "Aggregation functions may not be used within an update-clause");
        }

        // preprocessing view
        InternalRoutePreprocessView onExprView = new InternalRoutePreprocessView(streamEventType, statementContext.getStatementResultService());

        // validation
        InternalEventRouterDesc routerDesc = services.getInternalEventRouter().getValidatePreprocessing(onExprView.getEventType(), updateSpec, statementContext.getAnnotations());

        // create context factory
        StatementAgentInstanceFactoryUpdate contextFactory = new StatementAgentInstanceFactoryUpdate(statementContext, services, streamEventType, updateSpec, onExprView, routerDesc, subSelectStrategyCollection);

        // perform start of hook-up to start
        Viewable finalViewable;
        EPStatementStopMethod stopStatementMethod;
        EPStatementDestroyMethod destroyStatementMethod;
        Map<ExprSubselectNode, SubSelectStrategyHolder> subselectStrategyInstances;

        // With context - delegate instantiation to context
        if (statementSpec.getOptionalContextName() != null) {

            // use statement-wide agent-instance-specific subselects
            AIRegistryExpr aiRegistryExpr = statementContext.getStatementAgentInstanceRegistry().getAgentInstanceExprService();
            subselectStrategyInstances = new HashMap<ExprSubselectNode, SubSelectStrategyHolder>();
            for (ExprSubselectNode node : subSelectStrategyCollection.getSubqueries().keySet()) {
                AIRegistrySubselect specificService = aiRegistryExpr.allocateSubselect(node);
                node.setStrategy(specificService);
                subselectStrategyInstances.put(node, new SubSelectStrategyHolder(null, null, null, null, null, null));
            }

            ContextMergeView mergeView = new ContextMergeView(onExprView.getEventType());
            finalViewable = mergeView;

            ContextManagedStatementOnTriggerDesc statement = new ContextManagedStatementOnTriggerDesc(statementSpec, statementContext, mergeView, contextFactory);
            services.getContextManagementService().addStatement(statementSpec.getOptionalContextName(), statement, isRecoveringResilient);
            stopStatementMethod = new EPStatementStopMethod(){
                public void stop()
                {
                    services.getContextManagementService().stoppedStatement(contextName, statementContext.getStatementName(), statementContext.getStatementId());
                    stopMethod.stop();
                }
            };

            destroyStatementMethod = new EPStatementDestroyMethod(){
                public void destroy() {
                    services.getContextManagementService().destroyedStatement(statementSpec.getOptionalContextName(), statementContext.getStatementName(), statementContext.getStatementId());
                }
            };
        }
        // Without context - start here
        else {
            AgentInstanceContext agentInstanceContext = getDefaultAgentInstanceContext(statementContext);
            final StatementAgentInstanceFactoryUpdateResult resultOfStart = contextFactory.newContext(agentInstanceContext, isRecoveringResilient);
            finalViewable = resultOfStart.getFinalView();
            stopStatementMethod = new EPStatementStopMethod() {
                public void stop() {
                    resultOfStart.getStopCallback().stop();
                    stopMethod.stop();
                }
            };
            destroyStatementMethod = null;
            subselectStrategyInstances = resultOfStart.getSubselectStrategies();
        }

        // assign subquery nodes
        EPStatementStartMethodHelperAssignExpr.assignSubqueryStrategies(subSelectStrategyCollection, subselectStrategyInstances);

        return new EPStatementStartResult(finalViewable, stopStatementMethod, destroyStatementMethod);
    }
}
