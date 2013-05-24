/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.context.subselect;

import com.espertech.esper.epl.agg.service.AggregationServiceFactoryDesc;
import com.espertech.esper.epl.expression.ExprPreviousNode;
import com.espertech.esper.epl.expression.ExprPriorNode;

import java.util.List;

/**
 * Entry holding lookup resource references for use by {@link SubSelectActivationCollection}.
 */
public class SubSelectStrategyFactoryDesc
{
    private final SubSelectActivationHolder subSelectActivationHolder;
    private final SubSelectStrategyFactory factory;
    private final AggregationServiceFactoryDesc aggregationServiceFactoryDesc;
    private final List<ExprPriorNode> priorNodesList;
    private final List<ExprPreviousNode> prevNodesList;

    public SubSelectStrategyFactoryDesc(SubSelectActivationHolder subSelectActivationHolder, SubSelectStrategyFactory factory, AggregationServiceFactoryDesc aggregationServiceFactoryDesc, List<ExprPriorNode> priorNodesList, List<ExprPreviousNode> prevNodesList) {
        this.subSelectActivationHolder = subSelectActivationHolder;
        this.factory = factory;
        this.aggregationServiceFactoryDesc = aggregationServiceFactoryDesc;
        this.priorNodesList = priorNodesList;
        this.prevNodesList = prevNodesList;
    }

    public SubSelectActivationHolder getSubSelectActivationHolder() {
        return subSelectActivationHolder;
    }

    public SubSelectStrategyFactory getFactory() {
        return factory;
    }

    public AggregationServiceFactoryDesc getAggregationServiceFactoryDesc() {
        return aggregationServiceFactoryDesc;
    }

    public List<ExprPriorNode> getPriorNodesList() {
        return priorNodesList;
    }

    public List<ExprPreviousNode> getPrevNodesList() {
        return prevNodesList;
    }
}
