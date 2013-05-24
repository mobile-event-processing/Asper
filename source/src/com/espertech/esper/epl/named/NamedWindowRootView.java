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
import com.espertech.esper.collection.Pair;
import com.espertech.esper.core.service.EPStatementHandle;
import com.espertech.esper.core.service.InternalEventRouter;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.core.service.StatementResultService;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.lookup.SubordPropPlan;
import com.espertech.esper.epl.lookup.SubordTableLookupStrategy;
import com.espertech.esper.epl.metric.MetricReportingService;
import com.espertech.esper.epl.spec.CreateIndexItem;
import com.espertech.esper.epl.spec.OnTriggerDesc;
import com.espertech.esper.event.vaevent.ValueAddEventProcessor;
import com.espertech.esper.util.AuditPath;
import com.espertech.esper.view.StatementStopService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * The root window in a named window plays multiple roles: It holds the indexes for deleting rows, if any on-delete statement
 * requires such indexes. Such indexes are updated when events arrive, or remove from when a data window
 * or on-delete statement expires events. The view keeps track of on-delete statements their indexes used.
 */
public class NamedWindowRootView
{
    private static final Log queryPlanLog = LogFactory.getLog(AuditPath.QUERYPLAN_LOG);
    private static final Log log = LogFactory.getLog(NamedWindowRootView.class);

    private final ValueAddEventProcessor revisionProcessor;
    private final boolean queryPlanLogging;
    private final EventType eventType;
    private final boolean isChildBatching;
    private final boolean isEnableIndexShare;
    private final Set<String> optionalUniqueKeyProps;

    public NamedWindowRootView(ValueAddEventProcessor revisionProcessor, boolean queryPlanLogging, MetricReportingService metricReportingService, EventType eventType, boolean childBatching, boolean isEnableIndexShare, Set<String> optionalUniqueKeyProps) {
        this.revisionProcessor = revisionProcessor;
        this.queryPlanLogging = queryPlanLogging;
        this.eventType = eventType;
        this.isChildBatching = childBatching;
        this.isEnableIndexShare = isEnableIndexShare;
        this.optionalUniqueKeyProps = optionalUniqueKeyProps;
    }

    public Set<String> getOptionalUniqueKeyProps() {
        return optionalUniqueKeyProps;
    }

    public ValueAddEventProcessor getRevisionProcessor() {
        return revisionProcessor;
    }

    public boolean isChildBatching() {
        return isChildBatching;
    }

    public static Log getQueryPlanLog() {
        return queryPlanLog;
    }

    public boolean isQueryPlanLogging() {
        return queryPlanLogging;
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean isEnableIndexShare() {
        return isEnableIndexShare;
    }
}
