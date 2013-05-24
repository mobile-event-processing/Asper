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

package com.espertech.esper.epl.join.util;

import com.espertech.esper.epl.join.plan.QueryPlan;

public interface QueryPlanIndexHook {
    public void subquery(QueryPlanIndexDescSubquery subquery);
    public void namedWindowOnExpr(QueryPlanIndexDescOnExpr onexpr);
    public void fireAndForget(QueryPlanIndexDescFAF queryPlanIndexDescFAF);
    public void join(QueryPlan join);
}
