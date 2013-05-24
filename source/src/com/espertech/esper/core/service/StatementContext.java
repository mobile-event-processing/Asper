/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.service;

import com.espertech.esper.client.ConfigurationInformation;
import com.espertech.esper.core.context.mgr.ContextControllerFactoryService;
import com.espertech.esper.core.context.stmt.StatementAIResourceRegistry;
import com.espertech.esper.core.context.util.ContextDescriptor;
import com.espertech.esper.epl.agg.service.AggregationServiceFactoryService;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.metric.MetricReportingServiceSPI;
import com.espertech.esper.epl.named.NamedWindowService;
import com.espertech.esper.epl.script.AgentInstanceScriptContext;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.EventTypeIdGenerator;
import com.espertech.esper.event.vaevent.ValueAddEventService;
import com.espertech.esper.filter.FilterService;
import com.espertech.esper.pattern.PatternContextFactory;
import com.espertech.esper.pattern.PatternObjectResolutionService;
import com.espertech.esper.pattern.pool.PatternSubexpressionPoolStmtSvc;
import com.espertech.esper.schedule.ScheduleAdjustmentService;
import com.espertech.esper.schedule.ScheduleBucket;
import com.espertech.esper.schedule.SchedulingService;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.view.StatementStopService;
import com.espertech.esper.view.ViewResolutionService;
import com.espertech.esper.view.ViewService;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.HashSet;

/**
 * Contains handles to the implementation of the the scheduling service for use in view evaluation.
 */
public final class StatementContext
{
    private final StatementContextEngineServices stmtEngineServices;
    private final String statementId;
    private final byte[] statementIdBytes;
    private final String statementName;
    private final String expression;
    private SchedulingService schedulingService;
    private final ScheduleBucket scheduleBucket;
    private final EPStatementHandle epStatementHandle;
    private final ViewResolutionService viewResolutionService;
    private final PatternObjectResolutionService patternResolutionService;
    private final StatementExtensionSvcContext statementExtensionSvcContext;
    private final StatementStopService statementStopService;
    private final MethodResolutionService methodResolutionService;
    private final PatternContextFactory patternContextFactory;
    private FilterService filterService;
    private InternalEventRouteDest internalEventEngineRouteDest;
    private final StatementResultService statementResultService;
    private final HashSet<String> dynamicReferenceEventTypes;
    private final ScheduleAdjustmentService scheduleAdjustmentService;
    private final Annotation[] annotations;
    private final StatementAIResourceRegistry statementAgentInstanceRegistry;
    private final ContextDescriptor contextDescriptor;
    private final PatternSubexpressionPoolStmtSvc patternSubexpressionPoolSvc;
    private final boolean statelessSelect;
    private final ContextControllerFactoryService contextControllerFactoryService;
    private final AggregationServiceFactoryService aggregationServiceFactoryService;

    // settable for view-sharing
    private StatementAgentInstanceLock defaultAgentInstanceLock;

    private AgentInstanceScriptContext defaultAgentInstanceScriptContext;

    /**
     * Constructor.
     * @param stmtEngineServices is the engine services for the statement
     * @param statementId is the statement is assigned for the statement for which this context exists
     * @param statementName is the statement name
     * @param expression is the EPL or pattern expression used
     * @param schedulingService implementation for schedule registration
     * @param scheduleBucket is for ordering scheduled callbacks within the view statements
     * @param epStatementHandle is the statements-own handle for use in registering callbacks with services
     * @param viewResultionService is a service for resolving view namespace and name to a view factory
     * @param statementExtensionSvcContext provide extension points for custom statement resources
     * @param statementStopService for registering a callback invoked when a statement is stopped
     * @param methodResolutionService is a service for resolving static methods and aggregation functions
     * @param patternContextFactory is the pattern-level services and context information factory
     * @param filterService is the filtering service
     * @param patternResolutionService is the service that resolves pattern objects for the statement
     * @param statementResultService handles awareness of listeners/subscriptions for a statement customizing output produced
     * @param internalEventEngineRouteDest routing destination
     */
    public StatementContext(StatementContextEngineServices stmtEngineServices,
                              String statementId,
                              byte[] statementIdBytes,
                              String statementName,
                              String expression,
                              SchedulingService schedulingService,
                              ScheduleBucket scheduleBucket,
                              EPStatementHandle epStatementHandle,
                              ViewResolutionService viewResultionService,
                              PatternObjectResolutionService patternResolutionService,
                              StatementExtensionSvcContext statementExtensionSvcContext,
                              StatementStopService statementStopService,
                              MethodResolutionService methodResolutionService,
                              PatternContextFactory patternContextFactory,
                              FilterService filterService,
                              StatementResultService statementResultService,
                              InternalEventRouteDest internalEventEngineRouteDest,
                              Annotation[] annotations,
                              StatementAIResourceRegistry statementAgentInstanceRegistry,
                              StatementAgentInstanceLock defaultAgentInstanceLock,
                              ContextDescriptor contextDescriptor,
                              PatternSubexpressionPoolStmtSvc patternSubexpressionPoolSvc,
                              boolean statelessSelect,
                              ContextControllerFactoryService contextControllerFactoryService,
                              AgentInstanceScriptContext defaultAgentInstanceScriptContext,
                              AggregationServiceFactoryService aggregationServiceFactoryService)
    {
        this.stmtEngineServices = stmtEngineServices;
        this.statementId = statementId;
        this.statementIdBytes = statementIdBytes;
        this.statementName = statementName;
        this.expression = expression;
        this.schedulingService = schedulingService;
        this.scheduleBucket = scheduleBucket;
        this.epStatementHandle = epStatementHandle;
        this.viewResolutionService = viewResultionService;
        this.patternResolutionService = patternResolutionService;
        this.statementExtensionSvcContext = statementExtensionSvcContext;
        this.statementStopService = statementStopService;
        this.methodResolutionService = methodResolutionService;
        this.patternContextFactory = patternContextFactory;
        this.filterService = filterService;
        this.statementResultService = statementResultService;
        this.dynamicReferenceEventTypes = new HashSet<String>();
        this.internalEventEngineRouteDest = internalEventEngineRouteDest;
        this.scheduleAdjustmentService = new ScheduleAdjustmentService();
        this.annotations = annotations;
        this.statementAgentInstanceRegistry = statementAgentInstanceRegistry;
        this.defaultAgentInstanceLock = defaultAgentInstanceLock;
        this.contextDescriptor = contextDescriptor;
        this.patternSubexpressionPoolSvc = patternSubexpressionPoolSvc;
        this.statelessSelect = statelessSelect;
        this.contextControllerFactoryService = contextControllerFactoryService;
        this.defaultAgentInstanceScriptContext = defaultAgentInstanceScriptContext;
        this.aggregationServiceFactoryService = aggregationServiceFactoryService;
    }

    /**
     * Returns the statement id.
     * @return statement id
     */
    public String getStatementId()
    {
        return statementId;
    }

    /**
     * Returns the statement name
     * @return statement name
     */
    public String getStatementName()
    {
        return statementName;
    }

    /**
     * Returns service to use for schedule evaluation.
     * @return schedule evaluation service implemetation
     */
    public final SchedulingService getSchedulingService()
    {
        return schedulingService;
    }

    /**
     * Returns service for generating events and handling event types.
     * @return event adapter service
     */
    public EventAdapterService getEventAdapterService()
    {
        return stmtEngineServices.getEventAdapterService();
    }

    /**
     * Returns the schedule bucket for ordering schedule callbacks within this pattern.
     * @return schedule bucket
     */
    public ScheduleBucket getScheduleBucket()
    {
        return scheduleBucket;
    }

    /**
     * Returns the statement's resource locks.
     * @return statement resource lock/handle
     */
    public EPStatementHandle getEpStatementHandle()
    {
        return epStatementHandle;
    }

    /**
     * Returns view resolution svc.
     * @return view resolution
     */
    public ViewResolutionService getViewResolutionService()
    {
        return viewResolutionService;
    }

    /**
     * Returns extension context for statements.
     * @return context
     */
    public StatementExtensionSvcContext getExtensionServicesContext()
    {
        return statementExtensionSvcContext;
    }

    /**
     * Returns statement stop subscription taker.
     * @return stop service
     */
    public StatementStopService getStatementStopService()
    {
        return statementStopService;
    }

    /**
     * Returns service to look up static and aggregation methods or functions.
     * @return method resolution
     */
    public MethodResolutionService getMethodResolutionService()
    {
        return methodResolutionService;
    }

    /**
     * Returns the pattern context factory for the statement.
     * @return pattern context factory
     */
    public PatternContextFactory getPatternContextFactory()
    {
        return patternContextFactory;
    }

    /**
     * Returns the statement expression text
     * @return expression text
     */
    public String getExpression()
    {
        return expression;
    }

    /**
     * Returns the engine URI.
     * @return engine URI
     */
    public String getEngineURI()
    {
        return stmtEngineServices.getEngineURI();
    }

    /**
     * Returns the filter service.
     * @return filter service
     */
    public FilterService getFilterService()
    {
        return filterService;
    }

    /**
     * Returns the statement's resolution service for pattern objects.
     * @return service for resolving pattern objects
     */
    public PatternObjectResolutionService getPatternResolutionService()
    {
        return patternResolutionService;
    }

    /**
     * Returns the named window management service.
     * @return service for managing named windows
     */
    public NamedWindowService getNamedWindowService()
    {
        return stmtEngineServices.getNamedWindowService();
    }

    /**
     * Returns variable service.
     * @return variable service
     */
    public VariableService getVariableService()
    {
        return stmtEngineServices.getVariableService();
    }

    /**
     * Returns the service that handles awareness of listeners/subscriptions for a statement customizing output produced
     * @return statement result svc
     */
    public StatementResultService getStatementResultService()
    {
        return statementResultService;
    }

    /**
     * Returns the URIs for resolving the event name against plug-inn event representations, if any
     * @return URIs
     */
    public URI[] getPlugInTypeResolutionURIs()
    {
        return stmtEngineServices.getPlugInTypeResolutionURIs();
    }

    /**
     * Returns the update event service.
     * @return revision service
     */
    public ValueAddEventService getValueAddEventService()
    {
        return stmtEngineServices.getValueAddEventService();
    }

    /**
     * Add an event type name created during statement start and not available through static analysis.
     * @param eventTypeName to add
     */
    public void addDynamicReferenceEventType(String eventTypeName)
    {
        dynamicReferenceEventTypes.add(eventTypeName);
    }

    /**
     * Returns event type names created during statement start and not available through static analysis.
     * @return event type names
     */
    public HashSet<String> getDynamicReferenceEventTypes()
    {
        return dynamicReferenceEventTypes;
    }

    /**
     * Returns the configuration.
     * @return configuration
     */
    public ConfigurationInformation getConfigSnapshot()
    {
        return stmtEngineServices.getConfigSnapshot();
    }

    /**
     * Sets the scheduling service
     * @param schedulingService service
     */
    public void setSchedulingService(SchedulingService schedulingService)
    {
        this.schedulingService = schedulingService;
    }

    /**
     * Sets the filter service
     * @param filterService filter service
     */
    public void setFilterService(FilterService filterService)
    {
        this.filterService = filterService;
    }

    /**
     * Returns the internal event router.
     * @return router
     */
    public InternalEventRouteDest getInternalEventEngineRouteDest()
    {
        return internalEventEngineRouteDest;
    }

    /**
     * Sets the internal event router.
     * @param internalEventEngineRouteDest router
     */
    public void setInternalEventEngineRouteDest(InternalEventRouteDest internalEventEngineRouteDest)
    {
        this.internalEventEngineRouteDest = internalEventEngineRouteDest;
    }

    /**
     * Return the service for adjusting schedules.
     * @return service for adjusting schedules
     */
    public ScheduleAdjustmentService getScheduleAdjustmentService()
    {
        return scheduleAdjustmentService;
    }

    /**
     * Returns metrics svc.
     * @return metrics
     */
    public MetricReportingServiceSPI getMetricReportingService() {
        return stmtEngineServices.getMetricReportingService();
    }

    /**
     * Returns the time provider.
     * @return time provider
     */
    public TimeProvider getTimeProvider()
    {
        return schedulingService;
    }

    /**
     * Returns view svc.
     * @return svc
     */
    public ViewService getViewService() {
        return stmtEngineServices.getViewService();
    }

    public ExceptionHandlingService getExceptionHandlingService() {
        return stmtEngineServices.getExceptionHandlingService();
    }

    public Annotation[] getAnnotations()
    {
        return annotations;
    }

    public ExpressionResultCacheService getExpressionResultCacheServiceSharable() {
        return stmtEngineServices.getExpressionResultCacheService();
    }

    public String toString()
    {
        return  " stmtId=" + statementId +
                " stmtName=" + statementName;
    }

    public int getAgentInstanceId() {
        throw new RuntimeException("Statement agent instance information is not available when providing a context");
    }

    public StatementAIResourceRegistry getStatementAgentInstanceRegistry() {
        return statementAgentInstanceRegistry;
    }

    public StatementAgentInstanceLock getDefaultAgentInstanceLock() {
        return defaultAgentInstanceLock;
    }

    public ContextDescriptor getContextDescriptor() {
        return contextDescriptor;
    }
    
    public byte[] getStatementIdBytes() {
        return statementIdBytes;
    }

    public void setDefaultAgentInstanceLock(StatementAgentInstanceLock defaultAgentInstanceLock) {
        this.defaultAgentInstanceLock = defaultAgentInstanceLock;
    }

    public PatternSubexpressionPoolStmtSvc getPatternSubexpressionPoolSvc() {
        return patternSubexpressionPoolSvc;
    }

    public boolean isStatelessSelect() {
        return statelessSelect;
    }

    public ContextControllerFactoryService getContextControllerFactoryService() {
        return contextControllerFactoryService;
    }

    public AgentInstanceScriptContext getDefaultAgentInstanceScriptContext() {
        return defaultAgentInstanceScriptContext;
    }

    public AggregationServiceFactoryService getAggregationServiceFactoryService() {
        return aggregationServiceFactoryService;
    }
}
