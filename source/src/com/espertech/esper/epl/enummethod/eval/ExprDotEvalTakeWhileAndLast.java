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

package com.espertech.esper.epl.enummethod.eval;

import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.enummethod.dot.*;
import com.espertech.esper.epl.expression.ExprDotNodeUtility;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.arr.ObjectArrayEventType;

import java.util.List;

public class ExprDotEvalTakeWhileAndLast extends ExprDotEvalEnumMethodBase {

    public EventType[] getAddStreamTypes(String enumMethodUsedName, List<String> goesToNames, EventType inputEventType, Class collectionComponentType, List<ExprDotEvalParam> bodiesAndParameters) {
        EventType firstParamType;
        if (inputEventType == null) {
            firstParamType = ExprDotNodeUtility.makeTransientOAType(enumMethodUsedName, goesToNames.get(0), collectionComponentType);
        }
        else {
            firstParamType = inputEventType;
        }

        if (goesToNames.size() == 1) {
            return new EventType[] {firstParamType};
        }

        ObjectArrayEventType indexEventType = ExprDotNodeUtility.makeTransientOAType(enumMethodUsedName, goesToNames.get(1), int.class);
        return new EventType[]{firstParamType, indexEventType};
    }

    public EnumEval getEnumEval(EventAdapterService eventAdapterService, StreamTypeService streamTypeService, String statementId, String enumMethodUsedName, List<ExprDotEvalParam> bodiesAndParameters, EventType inputEventType, Class collectionComponentType, int numStreamsIncoming) {

        ExprDotEvalParamLambda first = (ExprDotEvalParamLambda) bodiesAndParameters.get(0);

        if (inputEventType != null) {
            super.setTypeInfo(ExprDotEvalTypeInfo.eventColl(inputEventType));
            if (first.getGoesToNames().size() == 1) {
                if (this.getEnumMethodEnum() == EnumMethodEnum.TAKEWHILELAST) {
                    return new EnumEvalTakeWhileLastEvents(first.getBodyEvaluator(), first.getStreamCountIncoming());
                }
                return new EnumEvalTakeWhileEvents(first.getBodyEvaluator(), first.getStreamCountIncoming());
            }

            if (this.getEnumMethodEnum() == EnumMethodEnum.TAKEWHILELAST) {
                return new EnumEvalTakeWhileLastIndexEvents(first.getBodyEvaluator(), first.getStreamCountIncoming(), (ObjectArrayEventType) first.getGoesToTypes()[1]);
            }
            return new EnumEvalTakeWhileIndexEvents(first.getBodyEvaluator(), first.getStreamCountIncoming(), (ObjectArrayEventType) first.getGoesToTypes()[1]);
        }

        super.setTypeInfo(ExprDotEvalTypeInfo.componentColl(collectionComponentType));
        if (first.getGoesToNames().size() == 1) {
            if (this.getEnumMethodEnum() == EnumMethodEnum.TAKEWHILELAST) {
                return new EnumEvalTakeWhileLastScalar(first.getBodyEvaluator(), first.getStreamCountIncoming(), (ObjectArrayEventType) first.getGoesToTypes()[0]);
            }
            return new EnumEvalTakeWhileScalar(first.getBodyEvaluator(), first.getStreamCountIncoming(), (ObjectArrayEventType) first.getGoesToTypes()[0]);
        }

        if (this.getEnumMethodEnum() == EnumMethodEnum.TAKEWHILELAST) {
            return new EnumEvalTakeWhileLastIndexScalar(first.getBodyEvaluator(), first.getStreamCountIncoming(), (ObjectArrayEventType) first.getGoesToTypes()[0], (ObjectArrayEventType) first.getGoesToTypes()[1]);
        }
        return new EnumEvalTakeWhileIndexScalar(first.getBodyEvaluator(), first.getStreamCountIncoming(), (ObjectArrayEventType) first.getGoesToTypes()[0], (ObjectArrayEventType) first.getGoesToTypes()[1]);
    }
}
