/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.service.StatementResultService;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.metric.MetricReportingService;
import com.espertech.esper.epl.metric.StatementMetricHandle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * View for the on-delete statement that handles removing events from a named window.
 */
public class NamedWindowOnMergeViewFactory extends NamedWindowOnExprBaseViewFactory
{
    private static final Log log = LogFactory.getLog(NamedWindowOnMergeViewFactory.class);
    private final NamedWindowOnMergeHelper namedWindowOnMergeHelper;
    private final StatementResultService statementResultService;
    private final StatementMetricHandle createNamedWindowMetricHandle;
    private final MetricReportingService metricReportingService;

    public NamedWindowOnMergeViewFactory(EventType namedWindowEventType, NamedWindowOnMergeHelper namedWindowOnMergeHelper, StatementResultService statementResultService, StatementMetricHandle createNamedWindowMetricHandle, MetricReportingService metricReportingService) {
        super(namedWindowEventType);
        this.namedWindowOnMergeHelper = namedWindowOnMergeHelper;
        this.statementResultService = statementResultService;
        this.createNamedWindowMetricHandle = createNamedWindowMetricHandle;
        this.metricReportingService = metricReportingService;
    }

    public NamedWindowOnExprBaseView make(NamedWindowLookupStrategy lookupStrategy, NamedWindowRootViewInstance namedWindowRootViewInstance, AgentInstanceContext agentInstanceContext, ResultSetProcessor resultSetProcessor) {
        return new NamedWindowOnMergeView(lookupStrategy, namedWindowRootViewInstance, agentInstanceContext, this);
    }

    public NamedWindowOnMergeHelper getNamedWindowOnMergeHelper() {
        return namedWindowOnMergeHelper;
    }

    public StatementResultService getStatementResultService() {
        return statementResultService;
    }

    public StatementMetricHandle getCreateNamedWindowMetricHandle() {
        return createNamedWindowMetricHandle;
    }

    public MetricReportingService getMetricReportingService() {
        return metricReportingService;
    }
}