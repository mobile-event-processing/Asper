/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.agg.service;

import com.espertech.esper.client.annotation.Hint;
import com.espertech.esper.client.annotation.HintEnum;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.agg.access.AggregationAccessorSlotPair;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.variable.VariableReader;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.type.DoubleValue;
import com.espertech.esper.util.JavaClassHelper;

/**
 * Implementation for handling aggregation with grouping by group-keys.
 */
public class AggSvcGroupByReclaimAgedFactory extends AggregationServiceFactoryBase
{
    private static final long DEFAULT_MAX_AGE_MSEC = 60000L;

    protected final AggregationAccessorSlotPair[] accessors;
    protected final int[] streams;
    protected final boolean isJoin;

    protected final AggSvcGroupByReclaimAgedEvalFunc evaluationFunctionMaxAge;
    protected final AggSvcGroupByReclaimAgedEvalFunc evaluationFunctionFrequency;
    private volatile long currentReclaimFrequency = DEFAULT_MAX_AGE_MSEC;

    /**
     * Ctor.
     * @param evaluators - evaluate the sub-expression within the aggregate function (ie. sum(4*myNum))
     * @param prototypes - collect the aggregation state that evaluators evaluate to, act as prototypes for new aggregations
     * aggregation states for each group
     * @param reclaimGroupAged hint to reclaim
     * @param reclaimGroupFrequency hint to reclaim
     * @param variableService variables
     * @param accessors accessor definitions
     * @param streams streams in join
     * @param isJoin true for join, false for single-stream
     * @throws com.espertech.esper.epl.expression.ExprValidationException when validation fails
     */
    public AggSvcGroupByReclaimAgedFactory(ExprEvaluator evaluators[],
                                           AggregationMethodFactory prototypes[],
                                           Hint reclaimGroupAged,
                                           Hint reclaimGroupFrequency,
                                           final VariableService variableService,
                                           AggregationAccessorSlotPair[] accessors,
                                           int[] streams,
                                           boolean isJoin)
            throws ExprValidationException
    {
        super(evaluators, prototypes);
        this.accessors = accessors;
        this.streams = streams;
        this.isJoin = isJoin;

        String hintValueMaxAge = HintEnum.RECLAIM_GROUP_AGED.getHintAssignedValue(reclaimGroupAged);
        if (hintValueMaxAge == null)
        {
            throw new ExprValidationException("Required hint value for hint '" + HintEnum.RECLAIM_GROUP_AGED + "' has not been provided");
        }
        evaluationFunctionMaxAge = getEvaluationFunction(variableService, hintValueMaxAge);

        String hintValueFrequency = HintEnum.RECLAIM_GROUP_FREQ.getHintAssignedValue(reclaimGroupAged);
        if ((reclaimGroupFrequency == null) || (hintValueFrequency == null))
        {
            evaluationFunctionFrequency = evaluationFunctionMaxAge;
            currentReclaimFrequency = getReclaimFrequency(currentReclaimFrequency);
        }
        else
        {
            evaluationFunctionFrequency = getEvaluationFunction(variableService, hintValueFrequency);
        }
    }

    public AggregationService makeService(AgentInstanceContext agentInstanceContext, MethodResolutionService methodResolutionService) {
        return new AggSvcGroupByReclaimAgedImpl(evaluators, aggregators, accessors, streams, isJoin, evaluationFunctionMaxAge, evaluationFunctionFrequency, methodResolutionService);
    }

    private AggSvcGroupByReclaimAgedEvalFunc getEvaluationFunction(final VariableService variableService, String hintValue)
            throws ExprValidationException
    {
        final VariableReader variableReader = variableService.getReader(hintValue);
        if (variableReader != null)
        {
            if (!JavaClassHelper.isNumeric(variableReader.getType()))
            {
                throw new ExprValidationException("Variable type of variable '" + variableReader.getVariableName() + "' is not numeric");
            }

            return new AggSvcGroupByReclaimAgedEvalFuncVariable(variableReader);
        }
        else
        {
            Double valueDouble;
            try
            {
                valueDouble = DoubleValue.parseString(hintValue);
            }
            catch (RuntimeException ex)
            {
                throw new ExprValidationException("Failed to parse hint parameter value '" + hintValue + "' as a double-typed seconds value or variable name");
            }
            if (valueDouble <= 0)
            {
                throw new ExprValidationException("Hint parameter value '" + hintValue + "' is an invalid value, expecting a double-typed seconds value or variable name");
            }
            return new AggSvcGroupByReclaimAgedEvalFuncConstant(valueDouble);
        }
    }

    private long getReclaimFrequency(long currentReclaimFrequency)
    {
        Double frequency = evaluationFunctionFrequency.getLongValue();
        if ((frequency == null) || (frequency <= 0))
        {
            return currentReclaimFrequency;
        }
        return Math.round(frequency * 1000d);
    }
}
