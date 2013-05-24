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

package com.espertech.esper.core.context.stmt;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.agg.service.AggregationRowRemovedCallback;
import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AIRegistryAggregationMap implements AIRegistryAggregation {
    private final Map<Integer, AggregationService> services;

    public AIRegistryAggregationMap() {
        this.services = new HashMap<Integer, AggregationService>();
    }

    public void assignService(int serviceId, AggregationService aggregationService) {
        services.put(serviceId, aggregationService);
    }

    public void deassignService(int serviceId) {
        services.remove(serviceId);
    }

    public int getInstanceCount() {
        return services.size();
    }

    public void applyEnter(EventBean[] eventsPerStream, Object optionalGroupKeyPerRow, ExprEvaluatorContext exprEvaluatorContext) {
        services.get(exprEvaluatorContext.getAgentInstanceId()).applyEnter(eventsPerStream, optionalGroupKeyPerRow, exprEvaluatorContext);
    }

    public void applyLeave(EventBean[] eventsPerStream, Object optionalGroupKeyPerRow, ExprEvaluatorContext exprEvaluatorContext) {
        services.get(exprEvaluatorContext.getAgentInstanceId()).applyLeave(eventsPerStream, optionalGroupKeyPerRow, exprEvaluatorContext);
    }

    public void setCurrentAccess(Object groupKey, int agentInstanceId) {
        services.get(agentInstanceId).setCurrentAccess(groupKey, agentInstanceId);
    }

    public void clearResults(ExprEvaluatorContext exprEvaluatorContext) {
        services.get(exprEvaluatorContext.getAgentInstanceId()).clearResults(exprEvaluatorContext);
    }

    public Object getValue(int column, int agentInstanceId) {
        return services.get(agentInstanceId).getValue(column, agentInstanceId);
    }

    public Collection<EventBean> getCollection(int column, ExprEvaluatorContext context) {
        return services.get(context.getAgentInstanceId()).getCollection(column, context);
    }

    public EventBean getEventBean(int column, ExprEvaluatorContext context) {
        return services.get(context.getAgentInstanceId()).getEventBean(column, context);
    }

    public void setRemovedCallback(AggregationRowRemovedCallback callback) {
        // not applicable
    }

}
