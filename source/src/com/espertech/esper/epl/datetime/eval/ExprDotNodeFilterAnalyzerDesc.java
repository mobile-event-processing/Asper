/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.datetime.eval;

import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.expression.ExprIdentNode;
import com.espertech.esper.epl.expression.ExprIdentNodeImpl;
import com.espertech.esper.epl.join.plan.QueryGraph;
import com.espertech.esper.type.RelationalOpEnum;

public interface ExprDotNodeFilterAnalyzerDesc
{
    public void apply(QueryGraph queryGraph);
}

