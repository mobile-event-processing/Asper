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

import com.espertech.esper.client.annotation.AuditEnum;
import com.espertech.esper.core.context.util.ContextDescriptor;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.StreamTypeServiceImpl;
import com.espertech.esper.epl.core.ViewResourceDelegateUnverified;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.schedule.TimeProvider;

import java.lang.annotation.Annotation;

public class ExprValidationContext {
    private final StreamTypeService streamTypeService;
    private final MethodResolutionService methodResolutionService;
    private final ViewResourceDelegateUnverified viewResourceDelegate;
    private final TimeProvider timeProvider;
    private final VariableService variableService;
    private final ExprEvaluatorContext exprEvaluatorContext;
    private final EventAdapterService eventAdapterService;
    private final String statementName;
    private final String statementId;
    private final Annotation[] annotations;
    private final ContextDescriptor contextDescriptor;

    private final boolean isExpressionNestedAudit;
    private final boolean isExpressionAudit;

    public ExprValidationContext(StreamTypeService streamTypeService,
                                 MethodResolutionService methodResolutionService,
                                 ViewResourceDelegateUnverified viewResourceDelegate,
                                 TimeProvider timeProvider,
                                 VariableService variableService,
                                 ExprEvaluatorContext exprEvaluatorContext,
                                 EventAdapterService eventAdapterService,
                                 String statementName,
                                 String statementId,
                                 Annotation[] annotations,
                                 ContextDescriptor contextDescriptor) {
        this.streamTypeService = streamTypeService;
        this.methodResolutionService = methodResolutionService;
        this.viewResourceDelegate = viewResourceDelegate;
        this.timeProvider = timeProvider;
        this.variableService = variableService;
        this.exprEvaluatorContext = exprEvaluatorContext;
        this.eventAdapterService = eventAdapterService;
        this.statementName = statementName;
        this.statementId = statementId;
        this.annotations = annotations;
        this.contextDescriptor = contextDescriptor;

        isExpressionAudit = AuditEnum.EXPRESSION.getAudit(annotations) != null;
        isExpressionNestedAudit = AuditEnum.EXPRESSION_NESTED.getAudit(annotations) != null;
    }

    public ExprValidationContext(StreamTypeServiceImpl types, ExprValidationContext ctx) {
        this(types, ctx.getMethodResolutionService(), ctx.getViewResourceDelegate(), ctx.getTimeProvider(), ctx.getVariableService(), ctx.getExprEvaluatorContext(), ctx.getEventAdapterService(), ctx.getStatementName(), ctx.getStatementId(), ctx.getAnnotations(), ctx.getContextDescriptor());
    }

    public StreamTypeService getStreamTypeService() {
        return streamTypeService;
    }

    public MethodResolutionService getMethodResolutionService() {
        return methodResolutionService;
    }

    public ViewResourceDelegateUnverified getViewResourceDelegate() {
        return viewResourceDelegate;
    }

    public TimeProvider getTimeProvider() {
        return timeProvider;
    }

    public VariableService getVariableService() {
        return variableService;
    }

    public ExprEvaluatorContext getExprEvaluatorContext() {
        return exprEvaluatorContext;
    }

    public EventAdapterService getEventAdapterService() {
        return eventAdapterService;
    }

    public String getStatementName() {
        return statementName;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public boolean isExpressionNestedAudit() {
        return isExpressionNestedAudit;
    }

    public boolean isExpressionAudit() {
        return isExpressionAudit;
    }

    public String getStatementId() {
        return statementId;
    }

    public ContextDescriptor getContextDescriptor() {
        return contextDescriptor;
    }
}
