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
import com.espertech.esper.collection.Pair;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.enummethod.dot.EnumMethodEnum;
import com.espertech.esper.epl.enummethod.dot.ExprDotEvalEnumMethodBase;
import com.espertech.esper.epl.enummethod.dot.ExprDotEvalParam;
import com.espertech.esper.epl.enummethod.dot.ExprDotEvalTypeInfo;
import com.espertech.esper.epl.expression.ExprDotNode;
import com.espertech.esper.epl.expression.ExprEvaluatorEnumeration;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.EventTypeUtility;

import java.util.List;

public class ExprDotEvalSetExceptUnionIntersect extends ExprDotEvalEnumMethodBase {

    public EventType[] getAddStreamTypes(String enumMethodUsedName, List<String> goesToNames, EventType inputEventType, Class collectionComponentType, List<ExprDotEvalParam> bodiesAndParameters) {
        return new EventType[] {};
    }

    public EnumEval getEnumEval(EventAdapterService eventAdapterService, StreamTypeService streamTypeService, String statementId, String enumMethodUsedName, List<ExprDotEvalParam> bodiesAndParameters, EventType inputEventType, Class collectionComponentType, int numStreamsIncoming) throws ExprValidationException {
        ExprDotEvalParam first = bodiesAndParameters.get(0);

        Pair<ExprEvaluatorEnumeration, ExprDotEvalTypeInfo> enumSrc = ExprDotNode.getEnumerationSource(first.getBody(), streamTypeService, eventAdapterService, statementId, true);
        if (inputEventType != null) {
            super.setTypeInfo(ExprDotEvalTypeInfo.eventColl(inputEventType));
        }
        else {
            super.setTypeInfo(ExprDotEvalTypeInfo.componentColl(collectionComponentType));
        }

        if (enumSrc.getFirst() == null) {
            String message = "Enumeration method '" + enumMethodUsedName + "' requires an expression yielding an event-collection as input paramater";
            throw new ExprValidationException(message);
        }

        EventType setType = enumSrc.getFirst().getEventTypeCollection(eventAdapterService);
        if (setType != inputEventType) {
            boolean isSubtype = EventTypeUtility.isTypeOrSubTypeOf(setType, inputEventType);
            if (!isSubtype) {
                String message = "Enumeration method '" + enumMethodUsedName + "' expects event type '" + inputEventType.getName() + "' but receives event type '" + enumSrc.getFirst().getEventTypeCollection(eventAdapterService).getName() + "'";
                throw new ExprValidationException(message);
            }
        }

        if (this.getEnumMethodEnum() == EnumMethodEnum.UNION) {
            return new EnumEvalUnion(numStreamsIncoming, enumSrc.getFirst(), inputEventType == null);
        }
        else if (this.getEnumMethodEnum() == EnumMethodEnum.INTERSECT) {
            return new EnumEvalIntersect(numStreamsIncoming, enumSrc.getFirst(), inputEventType == null);
        }
        else if (this.getEnumMethodEnum() == EnumMethodEnum.EXCEPT) {
            return new EnumEvalExcept(numStreamsIncoming, enumSrc.getFirst(), inputEventType == null);
        }
        else {
            throw new IllegalArgumentException("Invalid enumeration method for this factory: " + this.getEnumMethodEnum());
        }
    }
}
