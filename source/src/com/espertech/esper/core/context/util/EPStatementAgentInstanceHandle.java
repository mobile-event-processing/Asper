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

package com.espertech.esper.core.context.util;

import com.espertech.esper.core.service.EPStatementDispatch;
import com.espertech.esper.core.service.EPStatementHandle;
import com.espertech.esper.core.service.StatementAgentInstanceFilterVersion;
import com.espertech.esper.core.service.StatementAgentInstanceLock;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.filter.FilterFaultHandler;

public class EPStatementAgentInstanceHandle {
    private final EPStatementHandle statementHandle;
    private final String statementId;
    private transient StatementAgentInstanceLock statementAgentInstanceLock = null;
    private final int agentInstanceId;
    private final int priority;
    private final boolean isPreemptive;
    private final boolean isHasVariables;
    private final boolean isCanSelfJoin;
    private final StatementAgentInstanceFilterVersion statementFilterVersion;
    private transient EPStatementDispatch optionalDispatchable;
    private boolean destroyed;

    private final int hashCode;
    private FilterFaultHandler filterFaultHandler;

    public EPStatementAgentInstanceHandle(EPStatementHandle statementHandle, StatementAgentInstanceLock statementAgentInstanceLock, int agentInstanceId, StatementAgentInstanceFilterVersion statementFilterVersion) {
        this.statementHandle = statementHandle;
        this.statementId = statementHandle.getStatementId();
        this.statementAgentInstanceLock = statementAgentInstanceLock;
        this.agentInstanceId = agentInstanceId;
        this.priority = statementHandle.getPriority();
        isPreemptive = statementHandle.isPreemptive();
        isHasVariables = statementHandle.isHasVariables();
        isCanSelfJoin = statementHandle.isCanSelfJoin();
        hashCode = 31 * statementHandle.hashCode() + agentInstanceId;
        this.statementFilterVersion = statementFilterVersion;
    }

    public EPStatementHandle getStatementHandle() {
        return statementHandle;
    }

    public String getStatementId() {
        return statementId;
    }

    public StatementAgentInstanceLock getStatementAgentInstanceLock() {
        return statementAgentInstanceLock;
    }

    public int getAgentInstanceId() {
        return agentInstanceId;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isPreemptive() {
        return isPreemptive;
    }

    public boolean isHasVariables() {
        return isHasVariables;
    }

    public boolean isCanSelfJoin() {
        return isCanSelfJoin;
    }

    public void setStatementAgentInstanceLock(StatementAgentInstanceLock statementAgentInstanceLock) {
        this.statementAgentInstanceLock = statementAgentInstanceLock;
    }

    public StatementAgentInstanceFilterVersion getStatementFilterVersion() {
        return statementFilterVersion;
    }

    /**
     * Tests filter version.
     * @param filterVersion to test
     * @return indicator whether version is up-to-date
     */
    public boolean isCurrentFilter(long filterVersion) {
        return statementFilterVersion.isCurrentFilter(filterVersion);
    }

    public boolean equals(Object otherObj)
    {
        if (this == otherObj) {
            return true;
        }

        if (!(otherObj instanceof EPStatementAgentInstanceHandle)) {
            return false;
        }

        EPStatementAgentInstanceHandle other = (EPStatementAgentInstanceHandle) otherObj;
        return other.statementId.equals(this.statementId) && other.agentInstanceId == this.agentInstanceId;
    }

    public int hashCode()
    {
        return hashCode;
    }

    /**
     * Provides a callback for use when statement processing for filters and schedules is done,
     * for use by join statements that require an explicit indicator that all
     * joined streams results have been processed.
     * @param optionalDispatchable is the instance for calling onto after statement callback processing
     */
    public void setOptionalDispatchable(EPStatementDispatch optionalDispatchable)
    {
        this.optionalDispatchable = optionalDispatchable;
    }

    public EPStatementDispatch getOptionalDispatchable() {
        return optionalDispatchable;
    }

    /**
     * Invoked by {@link com.espertech.esper.client.EPRuntime} to indicate that a statements's
     * filer and schedule processing is done, and now it's time to process join results.
     * @param exprEvaluatorContext context for expression evaluation
     */
    public void internalDispatch(ExprEvaluatorContext exprEvaluatorContext)
    {
        if (optionalDispatchable != null)
        {
            optionalDispatchable.execute(exprEvaluatorContext);
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public String toString() {
        return "EPStatementAgentInstanceHandle{" +
                "name=" + statementHandle.getStatementName() +
                '}';
    }

    public FilterFaultHandler getFilterFaultHandler() {
        return filterFaultHandler;
    }

    public void setFilterFaultHandler(FilterFaultHandler filterFaultHandler) {
        this.filterFaultHandler = filterFaultHandler;
    }
}
