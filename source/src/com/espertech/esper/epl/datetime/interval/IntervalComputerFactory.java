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

package com.espertech.esper.epl.datetime.interval;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.datetime.eval.DatetimeMethodEnum;
import com.espertech.esper.epl.expression.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class IntervalComputerFactory {

    public static IntervalComputer make(DatetimeMethodEnum method, List<ExprNode> expressions) throws ExprValidationException {
        ExprOptionalConstant[] parameters = getParameters(expressions);

        if (method == DatetimeMethodEnum.BEFORE) {
            if (parameters.length == 0) {
                return new IntervalComputerBeforeNoParam();
            }
            IntervalStartEndParameterPair pair = IntervalStartEndParameterPair.fromParamsWithLongMaxEnd(parameters);
            if (pair.isConstant()) {
                return new IntervalComputerConstantBefore(pair);
            }
            return new IntervalComputerBeforeWithDeltaExpr(pair);
        }
        else if (method == DatetimeMethodEnum.AFTER) {
            if (parameters.length == 0) {
                return new IntervalComputerAfterNoParam();
            }
            IntervalStartEndParameterPair pair = IntervalStartEndParameterPair.fromParamsWithLongMaxEnd(parameters);
            if (pair.isConstant()) {
                return new IntervalComputerConstantAfter(pair);
            }
            return new IntervalComputerAfterWithDeltaExpr(pair);
        }
        else if (method == DatetimeMethodEnum.COINCIDES) {
            if (parameters.length == 0) {
                return new IntervalComputerCoincidesNoParam();
            }
            IntervalStartEndParameterPair pair = IntervalStartEndParameterPair.fromParamsWithSameEnd(parameters);
            if (pair.isConstant()) {
                return new IntervalComputerConstantCoincides(pair);
            }
            return new IntervalComputerCoincidesWithDeltaExpr(pair);
        }
        else if (method == DatetimeMethodEnum.DURING || method == DatetimeMethodEnum.INCLUDES) {
            if (parameters.length == 0) {
                if (method == DatetimeMethodEnum.DURING) {
                    return new IntervalComputerDuringNoParam();
                }
                return new IntervalComputerIncludesNoParam();
            }
            if (parameters.length == 1) {
                return new IntervalComputerDuringAndIncludesThreshold(method == DatetimeMethodEnum.DURING, parameters[0].getEvaluator());
            }
            if (parameters.length == 2) {
                return new IntervalComputerDuringAndIncludesMinMax(method == DatetimeMethodEnum.DURING, parameters[0].getEvaluator(), parameters[1].getEvaluator());
            }
            return new IntervalComputerDuringMinMaxStartEnd(method == DatetimeMethodEnum.DURING, parameters);
        }
        else if (method == DatetimeMethodEnum.FINISHES) {
            if (parameters.length == 0) {
                return new IntervalComputerFinishesNoParam();
            }
            validateConstantThreshold("finishes", parameters[0]);
            return new IntervalComputerFinishesThreshold(parameters[0].getEvaluator());
        }
        else if (method == DatetimeMethodEnum.FINISHEDBY) {
            if (parameters.length == 0) {
                return new IntervalComputerFinishedByNoParam();
            }
            validateConstantThreshold("finishedby", parameters[0]);
            return new IntervalComputerFinishedByThreshold(parameters[0].getEvaluator());
        }
        else if (method == DatetimeMethodEnum.MEETS) {
            if (parameters.length == 0) {
                return new IntervalComputerMeetsNoParam();
            }
            validateConstantThreshold("meets", parameters[0]);
            return new IntervalComputerMeetsThreshold(parameters[0].getEvaluator());
        }
        else if (method == DatetimeMethodEnum.METBY) {
            if (parameters.length == 0) {
                return new IntervalComputerMetByNoParam();
            }
            validateConstantThreshold("metBy", parameters[0]);
            return new IntervalComputerMetByThreshold(parameters[0].getEvaluator());
        }
        else if (method == DatetimeMethodEnum.OVERLAPS || method == DatetimeMethodEnum.OVERLAPPEDBY) {
            if (parameters.length == 0) {
                if (method == DatetimeMethodEnum.OVERLAPS) {
                    return new IntervalComputerOverlapsNoParam();
                }
                return new IntervalComputerOverlappedByNoParam();
            }
            if (parameters.length == 1) {
                return new IntervalComputerOverlapsAndByThreshold(method == DatetimeMethodEnum.OVERLAPS, parameters[0].getEvaluator());
            }
            return new IntervalComputerOverlapsAndByMinMax(method == DatetimeMethodEnum.OVERLAPS, parameters[0].getEvaluator(), parameters[1].getEvaluator());
        }
        else if (method == DatetimeMethodEnum.STARTS) {
            if (parameters.length == 0) {
                return new IntervalComputerStartsNoParam();
            }
            validateConstantThreshold("starts", parameters[0]);
            return new IntervalComputerStartsThreshold(parameters[0].getEvaluator());
        }
        else if (method == DatetimeMethodEnum.STARTEDBY) {
            if (parameters.length == 0) {
                return new IntervalComputerStartedByNoParam();
            }
            validateConstantThreshold("startedBy", parameters[0]);
            return new IntervalComputerStartedByThreshold(parameters[0].getEvaluator());
        }
        throw new IllegalArgumentException("Unknown datetime method '" + method + "'");
    }

    private static void validateConstantThreshold(String method, ExprOptionalConstant param) throws ExprValidationException {
        if (param.getOptionalConstant() != null && (param.getOptionalConstant()).longValue() < 0) {
            throw new ExprValidationException("The " + method + " date-time method does not allow negative threshold value");
        }
    }

    private static ExprOptionalConstant[] getParameters(List<ExprNode> expressions) {
        ExprOptionalConstant[] parameters = new ExprOptionalConstant[expressions.size() - 1];
        for (int i = 1; i < expressions.size(); i++) {
            parameters[i - 1] = getExprOrConstant(expressions.get(i));
        }
        return parameters;
    }

    private static ExprOptionalConstant getExprOrConstant(ExprNode exprNode) {
        if (exprNode instanceof ExprTimePeriod) {
            Long constant = null;
            if (exprNode.isConstantResult()) {
                double sec = (Double) exprNode.getExprEvaluator().evaluate(null, true, null);
                constant = (long)(sec * 1000L);
            }
            return new ExprOptionalConstant(exprNode.getExprEvaluator(), constant);
        }
        else if (ExprNodeUtility.isConstantValueExpr(exprNode)) {
            ExprConstantNode constantNode = (ExprConstantNode) exprNode;
            return new ExprOptionalConstant(constantNode.getExprEvaluator(), ((Number)constantNode.getValue()).longValue());
        }
        else {
            return new ExprOptionalConstant(exprNode.getExprEvaluator(), null);
        }
    }

    /**
     * After.
     */
    public static class IntervalComputerConstantAfter extends IntervalComputerConstantBase implements IntervalComputer {

        public IntervalComputerConstantAfter(IntervalStartEndParameterPair pair) {
            super(pair, true);
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return computeInternal(leftStart, leftEnd, rightStart, rightEnd, start, end);
        }

        public static Boolean computeInternal(long leftStart, long leftEnd, long rightStart, long rightEnd, long start, long end) {
            long delta = leftStart - rightEnd;
            return start <= delta && delta <= end;
        }
    }

    public static class IntervalComputerAfterWithDeltaExpr extends IntervalComputerExprBase {

        public IntervalComputerAfterWithDeltaExpr(IntervalStartEndParameterPair pair) {
            super(pair);
        }

        public boolean compute(long leftStartTs, long leftEnd, long rightStartTs, long rightEnd, long start, long end) {
            return IntervalComputerConstantAfter.computeInternal(leftStartTs, leftEnd, rightStartTs, rightEnd, start, end);
        }
    }

    public static class IntervalComputerAfterNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return leftStart > rightEnd;
        }
    }

    /**
     * Before.
     */
    public static class IntervalComputerConstantBefore extends IntervalComputerConstantBase implements IntervalComputer {

        public IntervalComputerConstantBefore(IntervalStartEndParameterPair pair) {
            super(pair, true);
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return computeInternal(leftStart, leftEnd, rightStart, start, end);
        }

        public static Boolean computeInternal(long left, long leftEnd, long right, long start, long end) {
            long delta = right - leftEnd;
            return start <= delta && delta <= end;
        }
    }

    public static class IntervalComputerBeforeWithDeltaExpr extends IntervalComputerExprBase {

        public IntervalComputerBeforeWithDeltaExpr(IntervalStartEndParameterPair pair) {
            super(pair);
        }

        public boolean compute(long leftStartTs, long leftEnd, long rightStartTs, long rightEnd, long start, long end) {
            return IntervalComputerConstantBefore.computeInternal(leftStartTs, leftEnd, rightStartTs, start, end);
        }
    }

    public static class IntervalComputerBeforeNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return leftEnd < rightStart;
        }
    }

    /**
     * Coincides.
     */
    public static class IntervalComputerConstantCoincides implements IntervalComputer {

        protected final long start;
        protected final long end;

        public IntervalComputerConstantCoincides(IntervalStartEndParameterPair pair) throws ExprValidationException {
            start = pair.getStart().getOptionalConstant();
            end = pair.getEnd().getOptionalConstant();
            if (start < 0 || end < 0) {
                throw new ExprValidationException("The coincides date-time method does not allow negative start and end values");
            }
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return computeInternal(leftStart, leftEnd, rightStart, rightEnd, start, end);
        }

        public static Boolean computeInternal(long left, long leftEnd, long right, long rightEnd, long startThreshold, long endThreshold) {
            return Math.abs(left - right) <= startThreshold &&
                   Math.abs(leftEnd - rightEnd) <= endThreshold;
        }
    }

    public static class IntervalComputerCoincidesWithDeltaExpr implements IntervalComputer {

        private static final Log log = LogFactory.getLog(IntervalComputerCoincidesWithDeltaExpr.class);

        private final ExprEvaluator start;
        private final ExprEvaluator finish;

        public IntervalComputerCoincidesWithDeltaExpr(IntervalStartEndParameterPair pair) {
            this.start = pair.getStart().getEvaluator();
            this.finish = pair.getEnd().getEvaluator();
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            Object startValue = start.evaluate(eventsPerStream, newData, context);
            if (startValue == null) {
                return null;
            }

            Object endValue = finish.evaluate(eventsPerStream, newData, context);
            if (endValue == null) {
                return null;
            }

            long start = IntervalComputerExprBase.toLong(startValue);
            long end = IntervalComputerExprBase.toLong(endValue);
            if (start < 0 || end < 0) {
                log.warn("The coincides date-time method does not allow negative start and end values");
                return null;
            }

            return IntervalComputerConstantCoincides.computeInternal(leftStart, leftEnd, rightStart, rightEnd, start, end);
        }
    }

    public static class IntervalComputerCoincidesNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return leftStart == rightStart && leftEnd == rightEnd;
        }
    }

    /**
     * During And Includes.
     */
    public static class IntervalComputerDuringNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return rightStart < leftStart && leftEnd < rightEnd;
        }
    }

    public static class IntervalComputerIncludesNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return leftStart < rightStart && rightEnd < leftEnd;
        }
    }

    public static class IntervalComputerDuringAndIncludesThreshold implements IntervalComputer {

        private final boolean during;
        private final ExprEvaluator threshold;

        public IntervalComputerDuringAndIncludesThreshold(boolean during, ExprEvaluator threshold) {
            this.during = during;
            this.threshold = threshold;
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {

            Object thresholdValue = threshold.evaluate(eventsPerStream, newData, context);
            if (thresholdValue == null) {
                return null;
            }

            long threshold = IntervalComputerExprBase.toLong(thresholdValue);

            if (during) {
                long deltaStart = leftStart - rightStart;
                if (deltaStart <= 0 || deltaStart > threshold) {
                    return false;
                }

                long deltaEnd = rightEnd - leftEnd;
                return !(deltaEnd <= 0 || deltaEnd > threshold);
            }
            else {
                long deltaStart = rightStart - leftStart;
                if (deltaStart <= 0 || deltaStart > threshold) {
                    return false;
                }

                long deltaEnd = leftEnd - rightEnd;
                return !(deltaEnd <= 0 || deltaEnd > threshold);
            }
        }
    }

    public static class IntervalComputerDuringAndIncludesMinMax implements IntervalComputer {

        private final boolean during;
        private final ExprEvaluator minEval;
        private final ExprEvaluator maxEval;

        public IntervalComputerDuringAndIncludesMinMax(boolean during, ExprEvaluator minEval, ExprEvaluator maxEval) {
            this.during = during;
            this.minEval = minEval;
            this.maxEval = maxEval;
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {

            Object minObject = minEval.evaluate(eventsPerStream, newData, context);
            if (minObject == null) {
                return null;
            }
            long min = IntervalComputerExprBase.toLong(minObject);

            Object maxObject = maxEval.evaluate(eventsPerStream, newData, context);
            if (maxObject == null) {
                return null;
            }
            long max = IntervalComputerExprBase.toLong(maxObject);

            if (during) {
                return computeInternalDuring(leftStart, leftEnd, rightStart, rightEnd, min, max, min, max);
            }
            else {
                return computeInternalIncludes(leftStart, leftEnd, rightStart, rightEnd, min, max, min, max);
            }
        }

        public static boolean computeInternalDuring(long left, long leftEnd, long right, long rightEnd,
                                        long startMin, long startMax, long endMin, long endMax) {
            if (startMin <= 0) {
                startMin = 1;
            }
            long deltaStart = left - right;
            if (deltaStart < startMin || deltaStart > startMax) {
                return false;
            }

            long deltaEnd = rightEnd - leftEnd;
            return !(deltaEnd < endMin || deltaEnd > endMax);
        }

        public static boolean computeInternalIncludes(long left, long leftEnd, long right, long rightEnd,
                                        long startMin, long startMax, long endMin, long endMax) {
            if (startMin <= 0) {
                startMin = 1;
            }
            long deltaStart = right - left;
            if (deltaStart < startMin || deltaStart > startMax) {
                return false;
            }

            long deltaEnd = leftEnd - rightEnd;
            return !(deltaEnd < endMin || deltaEnd > endMax);
        }
    }

    public static class IntervalComputerDuringMinMaxStartEnd implements IntervalComputer {

        private final boolean during;
        private final ExprEvaluator minStartEval;
        private final ExprEvaluator maxStartEval;
        private final ExprEvaluator minEndEval;
        private final ExprEvaluator maxEndEval;

        public IntervalComputerDuringMinMaxStartEnd(boolean during, ExprOptionalConstant[] parameters) {
            this.during = during;
            minStartEval = parameters[0].getEvaluator();
            maxStartEval = parameters[1].getEvaluator();
            minEndEval = parameters[2].getEvaluator();
            maxEndEval = parameters[3].getEvaluator();
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {

            Object minStartObject = minStartEval.evaluate(eventsPerStream, newData, context);
            if (minStartObject == null) {
                return null;
            }

            Object maxStartObject = maxStartEval.evaluate(eventsPerStream, newData, context);
            if (maxStartObject == null) {
                return null;
            }

            Object minEndObject = minEndEval.evaluate(eventsPerStream, newData, context);
            if (minEndObject == null) {
                return null;
            }

            Object maxEndObject = maxEndEval.evaluate(eventsPerStream, newData, context);
            if (maxEndObject == null) {
                return null;
            }

            long minStart = IntervalComputerExprBase.toLong(minStartObject);
            long maxStart = IntervalComputerExprBase.toLong(maxStartObject);
            long minEnd = IntervalComputerExprBase.toLong(minEndObject);
            long maxEnd = IntervalComputerExprBase.toLong(maxEndObject);

            if (during) {
                return IntervalComputerDuringAndIncludesMinMax.computeInternalDuring(leftStart, leftEnd, rightStart, rightEnd, minStart, maxStart, minEnd, maxEnd);
            }
            else {
                return IntervalComputerDuringAndIncludesMinMax.computeInternalIncludes(leftStart, leftEnd, rightStart, rightEnd, minStart, maxStart, minEnd, maxEnd);
            }
        }
    }

    /**
     * Finishes.
     */
    public static class IntervalComputerFinishesNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return rightStart < leftStart && (leftEnd == rightEnd);
        }
    }

    public static class IntervalComputerFinishesThreshold implements IntervalComputer {
        private static final Log log = LogFactory.getLog(IntervalComputerFinishesThreshold.class);

        private final ExprEvaluator threshold;

        public IntervalComputerFinishesThreshold(ExprEvaluator threshold) {
            this.threshold = threshold;
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {

            Object thresholdValue = threshold.evaluate(eventsPerStream, newData, context);
            if (thresholdValue == null) {
                return null;
            }

            long threshold = IntervalComputerExprBase.toLong(thresholdValue);
            if (threshold < 0) {
                log.warn("The 'finishes' date-time method does not allow negative threshold");
                return null;
            }

            if (rightStart >= leftStart) {
                return false;
            }
            long delta = Math.abs(leftEnd - rightEnd);
            return delta <= threshold;
        }
    }

    /**
     * Finishes-By.
     */
    public static class IntervalComputerFinishedByNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return leftStart < rightStart && (leftEnd == rightEnd);
        }
    }

    public static class IntervalComputerFinishedByThreshold implements IntervalComputer {

        private static final Log log = LogFactory.getLog(IntervalComputerFinishedByThreshold.class);
        private final ExprEvaluator threshold;

        public IntervalComputerFinishedByThreshold(ExprEvaluator threshold) {
            this.threshold = threshold;
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {

            Object thresholdValue = threshold.evaluate(eventsPerStream, newData, context);
            if (thresholdValue == null) {
                return null;
            }

            long threshold = IntervalComputerExprBase.toLong(thresholdValue);
            if (threshold < 0) {
                log.warn("The 'finishes' date-time method does not allow negative threshold");
                return null;
            }

            if (leftStart >= rightStart) {
                return false;
            }
            long delta = Math.abs(leftEnd - rightEnd);
            return delta <= threshold;
        }
    }

    /**
     * Meets.
     */
    public static class IntervalComputerMeetsNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return leftEnd == rightStart;
        }
    }

    public static class IntervalComputerMeetsThreshold implements IntervalComputer {

        private static final Log log = LogFactory.getLog(IntervalComputerMeetsThreshold.class);
        private final ExprEvaluator threshold;

        public IntervalComputerMeetsThreshold(ExprEvaluator threshold) {
            this.threshold = threshold;
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {

            Object thresholdValue = threshold.evaluate(eventsPerStream, newData, context);
            if (thresholdValue == null) {
                return null;
            }

            long threshold = IntervalComputerExprBase.toLong(thresholdValue);
            if (threshold < 0) {
                log.warn("The 'finishes' date-time method does not allow negative threshold");
                return null;
            }

            long delta = Math.abs(rightStart - leftEnd);
            return delta <= threshold;
        }
    }

    /**
     * Met-By.
     */
    public static class IntervalComputerMetByNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return rightEnd == leftStart;
        }
    }

    public static class IntervalComputerMetByThreshold implements IntervalComputer {

        private static final Log log = LogFactory.getLog(IntervalComputerMetByThreshold.class);
        private final ExprEvaluator threshold;

        public IntervalComputerMetByThreshold(ExprEvaluator threshold) {
            this.threshold = threshold;
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {

            Object thresholdValue = threshold.evaluate(eventsPerStream, newData, context);
            if (thresholdValue == null) {
                return null;
            }

            long threshold = IntervalComputerExprBase.toLong(thresholdValue);
            if (threshold < 0) {
                log.warn("The 'finishes' date-time method does not allow negative threshold");
                return null;
            }

            long delta = Math.abs(leftStart - rightEnd);
            return delta <= threshold;
        }
    }

    /**
     * Overlaps.
     */
    public static class IntervalComputerOverlapsNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return (leftStart < rightStart) &&
                   (rightStart < leftEnd) &&
                   (leftEnd < rightEnd);
        }
    }

    public static class IntervalComputerOverlapsAndByThreshold implements IntervalComputer {

        private final boolean overlaps;
        private final ExprEvaluator threshold;

        public IntervalComputerOverlapsAndByThreshold(boolean overlaps, ExprEvaluator threshold) {
            this.overlaps = overlaps;
            this.threshold = threshold;
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {

            Object thresholdValue = threshold.evaluate(eventsPerStream, newData, context);
            if (thresholdValue == null) {
                return null;
            }

            long threshold = IntervalComputerExprBase.toLong(thresholdValue);

            if (overlaps) {
                return computeInternalOverlaps(leftStart, leftEnd, rightStart, rightEnd, 0, threshold);
            }
            else {
                return computeInternalOverlaps(rightStart, rightEnd, leftStart, leftEnd, 0, threshold);
            }
        }

        public static boolean computeInternalOverlaps(long left, long leftEnd, long right, long rightEnd, long min, long max) {
            boolean match = ((left < right) &&
                   (right < leftEnd) &&
                   (leftEnd < rightEnd));
            if (!match) {
                return false;
            }
            long delta = leftEnd - right;
            return min <= delta && delta <= max;
        }
    }

    public static class IntervalComputerOverlapsAndByMinMax implements IntervalComputer {

        private final boolean overlaps;
        private final ExprEvaluator minEval;
        private final ExprEvaluator maxEval;

        public IntervalComputerOverlapsAndByMinMax(boolean overlaps, ExprEvaluator minEval, ExprEvaluator maxEval) {
            this.overlaps = overlaps;
            this.minEval = minEval;
            this.maxEval = maxEval;
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {

            Object minValue = minEval.evaluate(eventsPerStream, newData, context);
            if (minValue == null) {
                return null;
            }
            Object maxValue = maxEval.evaluate(eventsPerStream, newData, context);
            if (maxValue == null) {
                return null;
            }

            long minThreshold = IntervalComputerExprBase.toLong(minValue);
            long maxThreshold = IntervalComputerExprBase.toLong(maxValue);

            if (overlaps) {
                return IntervalComputerOverlapsAndByThreshold.computeInternalOverlaps(leftStart, leftEnd, rightStart, rightEnd, minThreshold, maxThreshold);
            }
            else {
                return IntervalComputerOverlapsAndByThreshold.computeInternalOverlaps(rightStart, rightEnd, leftStart, leftEnd, minThreshold, maxThreshold);
            }
        }
    }

    /**
     * OverlappedBy.
     */
    public static class IntervalComputerOverlappedByNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return (rightStart < leftStart) &&
                   (leftStart < rightEnd) &&
                   (rightEnd < leftEnd);
        }
    }

    /**
     * Starts.
     */
    public static class IntervalComputerStartsNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return (leftStart == rightStart) && (leftEnd < rightEnd);
        }
    }

    public static class IntervalComputerStartsThreshold implements IntervalComputer {

        private static final Log log = LogFactory.getLog(IntervalComputerStartsThreshold.class);

        private final ExprEvaluator threshold;

        public IntervalComputerStartsThreshold(ExprEvaluator threshold) {
            this.threshold = threshold;
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {

            Object thresholdValue = threshold.evaluate(eventsPerStream, newData, context);
            if (thresholdValue == null) {
                return null;
            }

            long threshold = IntervalComputerExprBase.toLong(thresholdValue);
            if (threshold < 0) {
                log.warn("The 'finishes' date-time method does not allow negative threshold");
                return null;
            }

            long delta = Math.abs(leftStart - rightStart);
            return delta <= threshold && (leftEnd < rightEnd);
        }
    }

    /**
     * Started-by.
     */
    public static class IntervalComputerStartedByNoParam implements IntervalComputer {

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
            return (leftStart == rightStart) && (leftEnd > rightEnd);
        }
    }

    public static class IntervalComputerStartedByThreshold implements IntervalComputer {

        private static final Log log = LogFactory.getLog(IntervalComputerStartedByThreshold.class);

        private final ExprEvaluator threshold;

        public IntervalComputerStartedByThreshold(ExprEvaluator threshold) {
            this.threshold = threshold;
        }

        public Boolean compute(long leftStart, long leftEnd, long rightStart, long rightEnd, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {

            Object thresholdValue = threshold.evaluate(eventsPerStream, newData, context);
            if (thresholdValue == null) {
                return null;
            }

            long threshold = IntervalComputerExprBase.toLong(thresholdValue);
            if (threshold < 0) {
                log.warn("The 'finishes' date-time method does not allow negative threshold");
                return null;
            }

            long delta = Math.abs(leftStart - rightStart);
            return delta <= threshold && (leftEnd > rightEnd);
        }
    }
}
