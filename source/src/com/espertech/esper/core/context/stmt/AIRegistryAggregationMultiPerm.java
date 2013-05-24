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
import com.espertech.esper.collection.ArrayWrap;
import com.espertech.esper.epl.agg.service.AggregationRowRemovedCallback;
import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

import java.util.Collection;

public class AIRegistryAggregationMultiPerm implements AIRegistryAggregation {
    private final ArrayWrap<AggregationService> services;
    private int count;

    public AIRegistryAggregationMultiPerm() {
        this.services = new ArrayWrap<AggregationService>(AggregationService.class, 2);
    }

    public void assignService(int serviceId, AggregationService aggregationService) {
        AIRegistryUtil.checkExpand(serviceId, services);
        services.getArray()[serviceId] = aggregationService;
        count++;
    }

    public void deassignService(int serviceId) {
        services.getArray()[serviceId] = null;
        count--;
    }

    public int getInstanceCount() {
        return count;
    }

    public void applyEnter(EventBean[] eventsPerStream, Object optionalGroupKeyPerRow, ExprEvaluatorContext exprEvaluatorContext) {
        services.getArray()[exprEvaluatorContext.getAgentInstanceId()].applyEnter(eventsPerStream, optionalGroupKeyPerRow, exprEvaluatorContext);
    }

    public void applyLeave(EventBean[] eventsPerStream, Object optionalGroupKeyPerRow, ExprEvaluatorContext exprEvaluatorContext) {
        services.getArray()[exprEvaluatorContext.getAgentInstanceId()].applyLeave(eventsPerStream, optionalGroupKeyPerRow, exprEvaluatorContext);
    }

    public void setCurrentAccess(Object groupKey, int agentInstanceId) {
        services.getArray()[agentInstanceId].setCurrentAccess(groupKey, agentInstanceId);
    }

    public void clearResults(ExprEvaluatorContext exprEvaluatorContext) {
        services.getArray()[exprEvaluatorContext.getAgentInstanceId()].clearResults(exprEvaluatorContext);
    }

    public Object getValue(int column, int agentInstanceId) {
        return services.getArray()[agentInstanceId].getValue(column, agentInstanceId);
    }

    public Collection<EventBean> getCollection(int column, ExprEvaluatorContext context) {
        return services.getArray()[context.getAgentInstanceId()].getCollection(column, context);
    }

    public EventBean getEventBean(int column, ExprEvaluatorContext context) {
        return services.getArray()[context.getAgentInstanceId()].getEventBean(column, context);
    }

    public void setRemovedCallback(AggregationRowRemovedCallback callback) {
        // not applicable
    }

}
