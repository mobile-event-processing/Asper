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

import com.espertech.esper.epl.expression.ExprPreviousNode;
import com.espertech.esper.epl.expression.ExprPriorNode;
import com.espertech.esper.epl.expression.ExprSubselectNode;

import java.util.HashMap;
import java.util.Map;

public abstract class AIRegistryExprBase implements AIRegistryExpr {

    private final Map<ExprSubselectNode, AIRegistrySubselect> subselects;
    private final Map<ExprPriorNode, AIRegistryPrior> priors;
    private final Map<ExprPreviousNode, AIRegistryPrevious> previous;

    public AIRegistryExprBase() {
        subselects = new HashMap<ExprSubselectNode, AIRegistrySubselect>();
        priors = new HashMap<ExprPriorNode, AIRegistryPrior>();
        previous = new HashMap<ExprPreviousNode, AIRegistryPrevious>();
    }

    public abstract AIRegistrySubselect allocateAIRegistrySubselect();
    public abstract AIRegistryPrevious allocateAIRegistryPrevious();
    public abstract AIRegistryPrior allocateAIRegistryPrior();

    public AIRegistrySubselect getSubselectService(ExprSubselectNode exprSubselectNode) {
        return subselects.get(exprSubselectNode);
    }

    public AIRegistryPrior getPriorServices(ExprPriorNode key) {
        return priors.get(key);
    }

    public AIRegistryPrevious getPreviousServices(ExprPreviousNode key) {
        return previous.get(key);
    }

    public AIRegistrySubselect allocateSubselect(ExprSubselectNode subselectNode) {
        AIRegistrySubselect subselect = allocateAIRegistrySubselect();
        subselects.put(subselectNode, subselect);
        return subselect;
    }

    public AIRegistryPrior allocatePrior(ExprPriorNode key) {
        AIRegistryPrior service = allocateAIRegistryPrior();
        priors.put(key, service);
        return service;
    }

    public AIRegistryPrevious allocatePrevious(ExprPreviousNode previousNode) {
        AIRegistryPrevious service = allocateAIRegistryPrevious();
        previous.put(previousNode, service);
        return service;
    }

    public int getSubselectAgentInstanceCount() {
        int total = 0;
        for (Map.Entry<ExprSubselectNode, AIRegistrySubselect> entry : subselects.entrySet()) {
            total += entry.getValue().getAgentInstanceCount();
        }
        return total;
    }

    public int getPreviousAgentInstanceCount() {
        int total = 0;
        for (Map.Entry<ExprPreviousNode, AIRegistryPrevious> entry : previous.entrySet()) {
            total += entry.getValue().getAgentInstanceCount();
        }
        return total;
    }

    public int getPriorAgentInstanceCount() {
        int total = 0;
        for (Map.Entry<ExprPriorNode, AIRegistryPrior> entry : priors.entrySet()) {
            total += entry.getValue().getAgentInstanceCount();
        }
        return total;
    }

    public void deassignService(int agentInstanceId) {
        for (Map.Entry<ExprSubselectNode, AIRegistrySubselect> entry : subselects.entrySet()) {
            entry.getValue().deassignService(agentInstanceId);
        }
        for (Map.Entry<ExprPriorNode, AIRegistryPrior> entry : priors.entrySet()) {
            entry.getValue().deassignService(agentInstanceId);
        }
        for (Map.Entry<ExprPreviousNode, AIRegistryPrevious> entry : previous.entrySet()) {
            entry.getValue().deassignService(agentInstanceId);
        }
    }
}
