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

import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.filter.FilterSpecCompiled;
import com.espertech.esper.schedule.ScheduleSpec;

import java.util.List;

public class ContextDetailConditionCrontab implements ContextDetailCondition {
    private static final long serialVersionUID = -1671433952748059211L;
    private final List<ExprNode> crontab;
    private ScheduleSpec schedule;

    public ContextDetailConditionCrontab(List<ExprNode> crontab) {
        this.crontab = crontab;
    }

    public List<ExprNode> getCrontab() {
        return crontab;
    }

    public ScheduleSpec getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleSpec schedule) {
        this.schedule = schedule;
    }

    public List<FilterSpecCompiled> getFilterSpecIfAny() {
        return null;
    }
}
