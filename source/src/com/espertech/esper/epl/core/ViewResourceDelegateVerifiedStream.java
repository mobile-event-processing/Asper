/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.epl.expression.ExprPreviousNode;
import com.espertech.esper.epl.expression.ExprPriorNode;

import java.util.*;

/**
 * Coordinates between view factories and requested resource (by expressions) the
 * availability of view resources to expressions.
 */
public class ViewResourceDelegateVerifiedStream
{
    private final List<ExprPreviousNode> previousRequests;
    private final SortedMap<Integer, List<ExprPriorNode>> priorRequests;

    public ViewResourceDelegateVerifiedStream(List<ExprPreviousNode> previousRequests, SortedMap<Integer, List<ExprPriorNode>> priorRequests) {
        this.previousRequests = previousRequests;
        this.priorRequests = priorRequests;
    }

    public List<ExprPreviousNode> getPreviousRequests() {
        return previousRequests;
    }

    public SortedMap<Integer, List<ExprPriorNode>> getPriorRequests() {
        return priorRequests;
    }

    public List<ExprPriorNode> getPriorRequestsAsList() {
        if (priorRequests.isEmpty()) {
            return Collections.emptyList();
        }
        List<ExprPriorNode> nodes = new ArrayList<ExprPriorNode>();
        for (List<ExprPriorNode> priorNodes : priorRequests.values()) {
            nodes.addAll(priorNodes);
        }
        return nodes;
    }
}
