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
 * Context dimension descriptor for a start-and-end temporal (single instance) or initiated-terminated (overlapping) context
 */
public class ContextDescriptorInitiatedTerminated implements ContextDescriptor {

    private static final long serialVersionUID = 8185386941253467559L;
    private ContextDescriptorCondition startCondition;
    private ContextDescriptorCondition endCondition;
    private boolean overlapping;

    /**
     * Ctor.
     */
    public ContextDescriptorInitiatedTerminated() {
    }

    /**
     * Ctor.
     * @param startCondition the condition that starts/initiates a context partition
     * @param endCondition the condition that ends/terminates a context partition
     * @param overlapping true for overlapping contexts
     */
    public ContextDescriptorInitiatedTerminated(ContextDescriptorCondition startCondition, ContextDescriptorCondition endCondition, boolean overlapping) {
        this.startCondition = startCondition;
        this.endCondition = endCondition;
        this.overlapping = overlapping;
    }

    /**
     * Returns the condition that starts/initiates a context partition
     * @return start condition
     */
    public ContextDescriptorCondition getStartCondition() {
        return startCondition;
    }

    /**
     * Sets the condition that starts/initiates a context partition
     * @param startCondition start condition
     */
    public void setStartCondition(ContextDescriptorCondition startCondition) {
        this.startCondition = startCondition;
    }

    /**
     * Returns the condition that ends/terminates a context partition
     * @return end condition
     */
    public ContextDescriptorCondition getEndCondition() {
        return endCondition;
    }

    /**
     * Sets the condition that ends/terminates a context partition
     * @param endCondition end condition
     */
    public void setEndCondition(ContextDescriptorCondition endCondition) {
        this.endCondition = endCondition;
    }

    /**
     * Returns true for overlapping context, false for non-overlapping.
     * @return overlap indicator
     */
    public boolean isOverlapping() {
        return overlapping;
    }

    /**
     * Set to true for overlapping context, false for non-overlapping.
     * @param overlapping overlap indicator
     */
    public void setOverlapping(boolean overlapping) {
        this.overlapping = overlapping;
    }

    public void toEPL(StringWriter writer, EPStatementFormatter formatter) {
        write(writer, overlapping ? "initiated by " : "start ", startCondition, formatter);
        writer.append(" ");
        write(writer, overlapping ? "terminated " : "end ", endCondition, formatter);
    }

    private static void write(StringWriter writer, String label, ContextDescriptorCondition condition, EPStatementFormatter formatter) {
        writer.append(label);
        condition.toEPL(writer, formatter);
    }
}
