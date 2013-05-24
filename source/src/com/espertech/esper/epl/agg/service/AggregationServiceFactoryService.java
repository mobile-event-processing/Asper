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

package com.espertech.esper.epl.agg.service;

import com.espertech.esper.client.annotation.Hint;
import com.espertech.esper.epl.agg.access.AggregationAccessorSlotPair;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.variable.VariableService;

public interface AggregationServiceFactoryService {
    public AggregationServiceFactory getNullAggregationService();
    public AggregationServiceFactory getNoGroupNoAccess(ExprEvaluator[] evaluatorsArr, AggregationMethodFactory[] aggregatorsArr);
    public AggregationServiceFactory getNoGroupAccessOnly(AggregationAccessorSlotPair[] pairs, int[] accessedStreams, boolean join);
    public AggregationServiceFactory getNoGroupAccessMixed(ExprEvaluator[] evaluatorsArr, AggregationMethodFactory[] aggregatorsArr, AggregationAccessorSlotPair[] pairs, int[] accessedStreams, boolean join);
    public AggregationServiceFactory getGroupedNoReclaimNoAccess(ExprEvaluator[] evaluatorsArr, AggregationMethodFactory[] aggregatorsArr);
    public AggregationServiceFactory getGroupNoReclaimAccessOnly(AggregationAccessorSlotPair[] pairs, int[] accessedStreams, boolean join);
    public AggregationServiceFactory getGroupNoReclaimMixed(ExprEvaluator[] evaluatorsArr, AggregationMethodFactory[] aggregatorsArr, AggregationAccessorSlotPair[] pairs, int[] accessedStreams, boolean join);
    public AggregationServiceFactory getGroupReclaimAged(ExprEvaluator[] evaluatorsArr, AggregationMethodFactory[] aggregatorsArr, Hint reclaimGroupAged, Hint reclaimGroupFrequency, VariableService variableService, AggregationAccessorSlotPair[] pairs, int[] accessedStreams, boolean join) throws ExprValidationException;
    public AggregationServiceFactory getGroupReclaimNoAccess(ExprEvaluator[] evaluatorsArr, AggregationMethodFactory[] aggregatorsArr, AggregationAccessorSlotPair[] pairs, int[] accessedStreams, boolean join);
    public AggregationServiceFactory getGroupReclaimMixable(ExprEvaluator[] evaluatorsArr, AggregationMethodFactory[] aggregatorsArr, AggregationAccessorSlotPair[] pairs, int[] accessedStreams, boolean join);
}
