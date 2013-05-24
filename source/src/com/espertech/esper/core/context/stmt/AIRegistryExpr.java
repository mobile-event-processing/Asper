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

public interface AIRegistryExpr {

    public AIRegistrySubselect getSubselectService(ExprSubselectNode exprSubselectNode);
    public AIRegistryPrior getPriorServices(ExprPriorNode key);
    public AIRegistryPrevious getPreviousServices(ExprPreviousNode key);

    public AIRegistrySubselect allocateSubselect(ExprSubselectNode subselectNode);
    public AIRegistryPrior allocatePrior(ExprPriorNode key);
    public AIRegistryPrevious allocatePrevious(ExprPreviousNode previousNode);

    public int getSubselectAgentInstanceCount();
    public int getPreviousAgentInstanceCount();
    public int getPriorAgentInstanceCount();

    public void deassignService(int agentInstanceId);
}
