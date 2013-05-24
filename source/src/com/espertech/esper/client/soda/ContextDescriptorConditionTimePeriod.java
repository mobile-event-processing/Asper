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

package com.espertech.esper.client.soda;

import java.io.StringWriter;

/**
 * Context condition that start/initiated or ends/terminates context partitions based on a time period.
 */
public class ContextDescriptorConditionTimePeriod implements ContextDescriptorCondition {

    private static final long serialVersionUID = 212201302878097145L;
    private Expression timePeriod;

    /**
     * Ctor.
     */
    public ContextDescriptorConditionTimePeriod() {
    }

    /**
     * Ctor.
     * @param timePeriod time period expression
     */
    public ContextDescriptorConditionTimePeriod(Expression timePeriod) {
        this.timePeriod = timePeriod;
    }

    /**
     * Returns the time period expression
     * @return time period expression
     */
    public Expression getTimePeriod() {
        return timePeriod;
    }

    /**
     * Sets the time period expression
     * @param timePeriod time period expression
     */
    public void setTimePeriod(Expression timePeriod) {
        this.timePeriod = timePeriod;
    }

    public void toEPL(StringWriter writer, EPStatementFormatter formatter) {
        writer.append("after ");
        timePeriod.toEPL(writer, ExpressionPrecedenceEnum.MINIMUM);
    }
}
