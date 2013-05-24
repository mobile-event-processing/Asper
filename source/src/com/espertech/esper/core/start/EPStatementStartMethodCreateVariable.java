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
import com.espertech.esper.core.context.mgr.ContextPropertyRegistryImpl;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.service.EPServicesContext;
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.core.*;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.epl.expression.ExprValidationContext;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.CreateVariableDesc;
import com.espertech.esper.epl.spec.SelectClauseElementWildcard;
import com.espertech.esper.epl.spec.SelectClauseStreamSelectorEnum;
import com.espertech.esper.epl.spec.StatementSpecCompiled;
import com.espertech.esper.epl.variable.CreateVariableView;
import com.espertech.esper.epl.variable.VariableDeclarationException;
import com.espertech.esper.epl.variable.VariableExistsException;
import com.espertech.esper.epl.view.OutputProcessViewBase;
import com.espertech.esper.epl.view.OutputProcessViewFactory;
import com.espertech.esper.epl.view.OutputProcessViewFactoryFactory;
import com.espertech.esper.view.StatementStopCallback;
import com.espertech.esper.view.ViewProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;

/**
 * Starts and provides the stop method for EPL statements.
 */
public class EPStatementStartMethodCreateVariable extends EPStatementStartMethodBase
{
    private static final Log log = LogFactory.getLog(EPStatementStartMethodCreateVariable.class);

    public EPStatementStartMethodCreateVariable(StatementSpecCompiled statementSpec) {
        super(statementSpec);
    }

    public EPStatementStartResult startInternal(final EPServicesContext services, final StatementContext statementContext, boolean isNewStatement, boolean isRecoveringStatement, boolean isRecoveringResilient) throws ExprValidationException, ViewProcessingException {
        final CreateVariableDesc createDesc = statementSpec.getCreateVariableDesc();

        // Get assignment value
        Object value = null;
        if (createDesc.getAssignment() != null)
        {
            // Evaluate assignment expression
            StreamTypeService typeService = new StreamTypeServiceImpl(new EventType[0], new String[0], new boolean[0], services.getEngineURI(), false);
            ExprEvaluatorContextStatement evaluatorContextStmt = new ExprEvaluatorContextStatement(statementContext);
            ExprValidationContext validationContext = new ExprValidationContext(typeService, statementContext.getMethodResolutionService(), null, statementContext.getSchedulingService(), statementContext.getVariableService(), evaluatorContextStmt, statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
            ExprNode validated = ExprNodeUtility.getValidatedSubtree(createDesc.getAssignment(), validationContext);
            value = validated.getExprEvaluator().evaluate(null, true, evaluatorContextStmt);
        }

        // Create variable
        try
        {
            services.getVariableService().createNewVariable(createDesc.getVariableName(), createDesc.getVariableType(), value, createDesc.isConstant(), createDesc.isArray(), statementContext.getExtensionServicesContext(), services.getEngineImportService());
        }
        catch (VariableExistsException ex)
        {
            // for new statement we don't allow creating the same variable
            if (isNewStatement)
            {
                throw new ExprValidationException("Cannot create variable: " + ex.getMessage(), ex);
            }
        }
        catch (VariableDeclarationException ex)
        {
            throw new ExprValidationException("Cannot create variable: " + ex.getMessage(), ex);
        }

        final CreateVariableView createView = new CreateVariableView(statementContext.getStatementId(), services.getEventAdapterService(), services.getVariableService(), createDesc.getVariableName(), statementContext.getStatementResultService());
        final int variableNum = services.getVariableService().getReader(createDesc.getVariableName()).getVariableNumber();
        services.getVariableService().registerCallback(variableNum, createView);
        statementContext.getStatementStopService().addSubscriber(new StatementStopCallback() {
            public void statementStopped()
            {
                services.getVariableService().unregisterCallback(variableNum, createView);
            }
        });

        // Create result set processor, use wildcard selection
        statementSpec.getSelectClauseSpec().getSelectExprList().clear();
        statementSpec.getSelectClauseSpec().add(new SelectClauseElementWildcard());
        statementSpec.setSelectStreamDirEnum(SelectClauseStreamSelectorEnum.RSTREAM_ISTREAM_BOTH);
        StreamTypeService typeService = new StreamTypeServiceImpl(new EventType[] {createView.getEventType()}, new String[] {"create_variable"}, new boolean[] {true}, services.getEngineURI(), false);
        AgentInstanceContext agentInstanceContext = getDefaultAgentInstanceContext(statementContext);
        ResultSetProcessorFactoryDesc resultSetProcessorPrototype = ResultSetProcessorFactoryFactory.getProcessorPrototype(
                statementSpec, statementContext, typeService, null, new boolean[0], true, ContextPropertyRegistryImpl.EMPTY_REGISTRY, null);
        ResultSetProcessor resultSetProcessor = EPStatementStartMethodHelperAssignExpr.getAssignResultSetProcessor(agentInstanceContext, resultSetProcessorPrototype);

        // Attach output view
        OutputProcessViewFactory outputViewFactory = OutputProcessViewFactoryFactory.make(statementSpec, services.getInternalEventRouter(), agentInstanceContext.getStatementContext(), resultSetProcessor.getResultEventType(), null);
        OutputProcessViewBase outputView = outputViewFactory.makeView(resultSetProcessor, agentInstanceContext);
        createView.addView(outputView);

        services.getStatementVariableRefService().addReferences(statementContext.getStatementName(), Collections.singleton(createDesc.getVariableName()));
        EPStatementDestroyMethod destroyMethod = new EPStatementDestroyMethod() {
            public void destroy() {
                try {
                    services.getStatementVariableRefService().removeReferencesStatement(statementContext.getStatementName());
                }
                catch (RuntimeException ex) {
                    log.error("Error removing variable '" + createDesc.getVariableName() + "': " + ex.getMessage());
                }
            }
        };

        EPStatementStopMethod stopMethod = new EPStatementStopMethod(){
            public void stop()
            {
            }
        };

        return new EPStatementStartResult(outputView, stopMethod, destroyMethod);
    }
}
