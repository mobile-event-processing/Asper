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
import com.espertech.esper.epl.core.EngineSettingsService;
import com.espertech.esper.epl.metric.MetricReportingServiceSPI;
import com.espertech.esper.epl.named.NamedWindowService;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.vaevent.ValueAddEventService;
import com.espertech.esper.view.ViewService;

import java.net.URI;

public final class StatementContextEngineServices
{
    private final String engineURI;
    private final EventAdapterService eventAdapterService;
    private final NamedWindowService namedWindowService;
    private final VariableService variableService;
    private final EngineSettingsService engineSettingsService;
    private final ValueAddEventService valueAddEventService;
    private final ConfigurationInformation configSnapshot;
    private final MetricReportingServiceSPI metricReportingService;
    private final ViewService viewService;
    private final ExceptionHandlingService exceptionHandlingService;
    private final ExpressionResultCacheService expressionResultCacheService;

    public StatementContextEngineServices(String engineURI, EventAdapterService eventAdapterService, NamedWindowService namedWindowService, VariableService variableService, EngineSettingsService engineSettingsService, ValueAddEventService valueAddEventService, ConfigurationInformation configSnapshot, MetricReportingServiceSPI metricReportingService, ViewService viewService, ExceptionHandlingService exceptionHandlingService, ExpressionResultCacheService expressionResultCacheService) {
        this.engineURI = engineURI;
        this.eventAdapterService = eventAdapterService;
        this.namedWindowService = namedWindowService;
        this.variableService = variableService;
        this.engineSettingsService = engineSettingsService;
        this.valueAddEventService = valueAddEventService;
        this.configSnapshot = configSnapshot;
        this.metricReportingService = metricReportingService;
        this.viewService = viewService;
        this.exceptionHandlingService = exceptionHandlingService;
        this.expressionResultCacheService = expressionResultCacheService;
    }

    public String getEngineURI() {
        return engineURI;
    }

    public EventAdapterService getEventAdapterService() {
        return eventAdapterService;
    }

    public NamedWindowService getNamedWindowService() {
        return namedWindowService;
    }

    public VariableService getVariableService() {
        return variableService;
    }

    public URI[] getPlugInTypeResolutionURIs() {
        return engineSettingsService.getPlugInEventTypeResolutionURIs();
    }

    public ValueAddEventService getValueAddEventService() {
        return valueAddEventService;
    }

    public ConfigurationInformation getConfigSnapshot() {
        return configSnapshot;
    }

    public MetricReportingServiceSPI getMetricReportingService() {
        return metricReportingService;
    }

    public ViewService getViewService() {
        return viewService;
    }

    public ExceptionHandlingService getExceptionHandlingService() {
        return exceptionHandlingService;
    }

    public ExpressionResultCacheService getExpressionResultCacheService() {
        return expressionResultCacheService;
    }
}
