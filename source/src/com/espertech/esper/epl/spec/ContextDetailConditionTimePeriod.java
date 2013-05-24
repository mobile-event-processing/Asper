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

package com.espertech.esper.epl.spec;

import com.espertech.esper.epl.expression.ExprTimePeriod;
import com.espertech.esper.filter.FilterSpecCompiled;

import java.util.List;

public class ContextDetailConditionTimePeriod implements ContextDetailCondition {
    private static final long serialVersionUID = 5140498109356559324L;
    private ExprTimePeriod timePeriod;

    public ContextDetailConditionTimePeriod(ExprTimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public ExprTimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(ExprTimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public List<FilterSpecCompiled> getFilterSpecIfAny() {
        return null;
    }
}
