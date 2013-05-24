/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.service;

import com.espertech.esper.client.EPStatementException;
import com.espertech.esper.client.annotation.Audit;
import com.espertech.esper.client.annotation.AuditEnum;
import com.espertech.esper.client.annotation.Drop;
import com.espertech.esper.client.annotation.Priority;
import com.espertech.esper.core.context.mgr.ContextControllerFactoryServiceImpl;
import com.espertech.esper.core.context.stmt.StatementAIResourceRegistry;
import com.espertech.esper.core.context.util.ContextDescriptor;
import com.espertech.esper.epl.agg.service.AggregationServiceFactoryServiceImpl;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.MethodResolutionServiceImpl;
import com.espertech.esper.epl.metric.StatementMetricHandle;
import com.espertech.esper.epl.script.AgentInstanceScriptContext;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.filter.FilterServiceSPI;
import com.espertech.esper.pattern.*;
import com.espertech.esper.pattern.pool.PatternSubexpressionPoolStmtHandler;
import com.espertech.esper.pattern.pool.PatternSubexpressionPoolStmtSvc;
import com.espertech.esper.schedule.ScheduleBucket;
import com.espertech.esper.schedule.SchedulingServiceSPI;
import com.espertech.esper.view.StatementStopServiceImpl;
import com.espertech.esper.view.ViewEnumHelper;
import com.espertech.esper.view.ViewResolutionService;
import com.espertech.esper.view.ViewResolutionServiceImpl;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Default implementation for making a statement-specific context class.
 */
public class StatementContextFactoryDefault implements StatementContextFactory
{
    private final PluggableObjectRegistryImpl viewRegistry;
    private final PluggableObjectCollection patternObjectClasses;
    private final Class systemVirtualDWViewFactory;

    private StatementContextEngineServices stmtEngineServices;

    /**
     * Ctor.
     * @param viewPlugIns is the view plug-in object descriptions
     * @param plugInPatternObj is the pattern plug-in object descriptions
     */
    public StatementContextFactoryDefault(PluggableObjectCollection viewPlugIns, PluggableObjectCollection plugInPatternObj, Class systemVirtualDWViewFactory)
    {
        viewRegistry = new PluggableObjectRegistryImpl(new PluggableObjectCollection[] {ViewEnumHelper.getBuiltinViews(), viewPlugIns});

        this.systemVirtualDWViewFactory = systemVirtualDWViewFactory;

        patternObjectClasses = new PluggableObjectCollection();
        patternObjectClasses.addObjects(plugInPatternObj);
        patternObjectClasses.addObjects(PatternObjectHelper.getBuiltinPatternObjects());
    }

    public void setStmtEngineServices(EPServicesContext services) {
        stmtEngineServices = getStmtCtxEngineServices(services);
    }

    public static StatementContextEngineServices getStmtCtxEngineServices(EPServicesContext services) {
        return new StatementContextEngineServices(
                services.getEngineURI(),
                services.getEventAdapterService(),
                services.getNamedWindowService(),
                services.getVariableService(),
                services.getEngineSettingsService(),
                services.getValueAddEventService(),
                services.getConfigSnapshot(),
                services.getMetricsReportingService(),
                services.getViewService(),
                services.getExceptionHandlingService(),
                services.getExpressionResultCacheSharable()
                );
    }

    public StatementContext makeContext(String statementId,
                                    String statementName,
                                    String expression,
                                    EPServicesContext engineServices,
                                    Map<String, Object> optAdditionalContext,
                                    boolean isFireAndForget,
                                    Annotation[] annotations,
                                    EPIsolationUnitServices isolationUnitServices,
                                    boolean stateless,
                                    StatementSpecRaw statementSpecRaw)
    {
        // Allocate the statement's schedule bucket which stays constant over it's lifetime.
        // The bucket allows callbacks for the same time to be ordered (within and across statements) and thus deterministic.
        ScheduleBucket scheduleBucket = engineServices.getSchedulingMgmtService().allocateBucket();

        // Create a lock for the statement
        StatementAgentInstanceLock defaultStatementAgentInstanceLock;

        // For on-delete statements, use the create-named-window statement lock
        CreateWindowDesc optCreateWindowDesc = statementSpecRaw.getCreateWindowDesc();
        OnTriggerDesc optOnTriggerDesc = statementSpecRaw.getOnTriggerDesc();
        if ((optOnTriggerDesc != null) && (optOnTriggerDesc instanceof OnTriggerWindowDesc))
        {
            String windowName = ((OnTriggerWindowDesc) optOnTriggerDesc).getWindowName();
            defaultStatementAgentInstanceLock = engineServices.getNamedWindowService().getNamedWindowLock(windowName);
            if (defaultStatementAgentInstanceLock == null)
            {
                throw new EPStatementException("Named window '" + windowName + "' has not been declared", expression);
            }
        }
        // For creating a named window, save the lock for use with on-delete/on-merge/on-update etc. statements
        else if (optCreateWindowDesc != null)
        {
            defaultStatementAgentInstanceLock = engineServices.getNamedWindowService().getNamedWindowLock(optCreateWindowDesc.getWindowName());
            if (defaultStatementAgentInstanceLock == null)
            {
                defaultStatementAgentInstanceLock = engineServices.getStatementLockFactory().getStatementLock(statementName, expression, annotations, false);
                engineServices.getNamedWindowService().addNamedWindowLock(optCreateWindowDesc.getWindowName(), defaultStatementAgentInstanceLock, statementName);
            }
        }
        else
        {
            defaultStatementAgentInstanceLock = engineServices.getStatementLockFactory().getStatementLock(statementName, expression, annotations, stateless);
        }

        StatementMetricHandle stmtMetric = null;
        if (!isFireAndForget)
        {
            stmtMetric = engineServices.getMetricsReportingService().getStatementHandle(statementId, statementName);
        }

        AnnotationAnalysisResult annotationData = AnnotationAnalysisResult.analyzeAnnotations(annotations);
        boolean hasVariables = statementSpecRaw.isHasVariables() || (statementSpecRaw.getCreateContextDesc() != null);
        EPStatementHandle epStatementHandle = new EPStatementHandle(statementId, statementName, expression, expression, hasVariables, stmtMetric, annotationData.getPriority(), annotationData.isPremptive());

        MethodResolutionService methodResolutionService = new MethodResolutionServiceImpl(engineServices.getEngineImportService(), engineServices.getSchedulingService());

        PatternContextFactory patternContextFactory = new PatternContextFactoryDefault();

        String optionalCreateNamedWindowName = statementSpecRaw.getCreateWindowDesc() != null ? statementSpecRaw.getCreateWindowDesc().getWindowName() : null;
        ViewResolutionService viewResolutionService = new ViewResolutionServiceImpl(viewRegistry, optionalCreateNamedWindowName, systemVirtualDWViewFactory);
        PatternObjectResolutionService patternResolutionService = new PatternObjectResolutionServiceImpl(patternObjectClasses);

        SchedulingServiceSPI schedulingService = engineServices.getSchedulingService();
        FilterServiceSPI filterService = engineServices.getFilterService();
        if (isolationUnitServices != null)
        {
            filterService = isolationUnitServices.getFilterService();
            schedulingService = isolationUnitServices.getSchedulingService();
        }

        Audit scheduleAudit = AuditEnum.SCHEDULE.getAudit(annotations);
        if (scheduleAudit != null) {
            schedulingService = new SchedulingServiceAudit(engineServices.getEngineURI(), statementName, schedulingService);
        }

        StatementAIResourceRegistry statementAgentInstanceRegistry = null;
        ContextDescriptor contextDescriptor = null;
        String optionalContextName = statementSpecRaw.getOptionalContextName();
        if (optionalContextName != null) {
            contextDescriptor = engineServices.getContextManagementService().getContextDescriptor(optionalContextName);

            // allocate a per-instance registry of aggregations and prev/prior/subselect
            if (contextDescriptor != null) {
                statementAgentInstanceRegistry = contextDescriptor.getAiResourceRegistryFactory().make();
            }
        }

        boolean countSubexpressions = engineServices.getConfigSnapshot().getEngineDefaults().getPatterns().getMaxSubexpressions() != null;
        PatternSubexpressionPoolStmtSvc patternSubexpressionPoolStmtSvc = null;
        if (countSubexpressions) {
            PatternSubexpressionPoolStmtHandler stmtCounter = new PatternSubexpressionPoolStmtHandler();
            patternSubexpressionPoolStmtSvc = new PatternSubexpressionPoolStmtSvc(engineServices.getPatternSubexpressionPoolSvc(), stmtCounter);
            engineServices.getPatternSubexpressionPoolSvc().addPatternContext(statementName, stmtCounter);
        }

        AgentInstanceScriptContext defaultAgentInstanceScriptContext = null;
        if (statementSpecRaw.getScriptExpressions() != null && !statementSpecRaw.getScriptExpressions().isEmpty()) {
            defaultAgentInstanceScriptContext = new AgentInstanceScriptContext();
        }

        // Create statement context
        return new StatementContext(stmtEngineServices,
                statementId,
                null, 
                statementName,
                expression,
                schedulingService,
                scheduleBucket,
                epStatementHandle,
                viewResolutionService,
                patternResolutionService,
                null,   // no statement extension context
                new StatementStopServiceImpl(),
                methodResolutionService,
                patternContextFactory,
                filterService,
                new StatementResultServiceImpl(statementName, engineServices.getStatementLifecycleSvc(), engineServices.getMetricsReportingService(), engineServices.getThreadingService()),
                engineServices.getInternalEventEngineRouteDest(),
                annotations,
                statementAgentInstanceRegistry,
                defaultStatementAgentInstanceLock,
                contextDescriptor,
                patternSubexpressionPoolStmtSvc,
                stateless,
                ContextControllerFactoryServiceImpl.DEFAULT_FACTORY,
                defaultAgentInstanceScriptContext,
                AggregationServiceFactoryServiceImpl.DEFAULT_FACTORY);
    }

    /**
     * Analysis result of analysing annotations for a statement.
     */
    public static class AnnotationAnalysisResult
    {
        private int priority;
        private boolean isPremptive;

        /**
         * Ctor.
         * @param priority priority
         * @param premptive preemptive indicator
         */
        private AnnotationAnalysisResult(int priority, boolean premptive)
        {
            this.priority = priority;
            isPremptive = premptive;
        }

        /**
         * Returns execution priority.
         * @return priority.
         */
        public int getPriority()
        {
            return priority;
        }

        /**
         * Returns preemptive indicator (drop or normal).
         * @return true for drop
         */
        public boolean isPremptive()
        {
            return isPremptive;
        }

        /**
         * Analyze the annotations and return priority and drop settings.
         * @param annotations to analyze
         * @return analysis result
         */
        public static AnnotationAnalysisResult analyzeAnnotations(Annotation[] annotations)
        {
            boolean preemptive = false;
            int priority = 0;
            boolean hasPrioritySetting = false;
            for (Annotation annotation : annotations)
            {
                if (annotation instanceof Priority)
                {
                    priority = ((Priority) annotation).value();
                    hasPrioritySetting = true;
                }
                if (annotation instanceof Drop)
                {
                    preemptive = true;
                }
            }
            if (!hasPrioritySetting && preemptive)
            {
                priority = 1;
            }
            return new AnnotationAnalysisResult(priority, preemptive);
        }
    }
}
