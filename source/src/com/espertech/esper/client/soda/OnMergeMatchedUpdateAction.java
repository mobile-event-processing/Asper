/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.soda;

import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

/**
 * For use with on-merge clauses, updates rows in a named window if matching rows are found.
 */
public class OnMergeMatchedUpdateAction implements OnMergeMatchedAction
{
    private static final long serialVersionUID = 0L;

    private List<AssignmentPair> assignments = Collections.emptyList();
    private Expression whereClause;

    /**
     * Ctor.
     */
    public OnMergeMatchedUpdateAction() {
    }

    /**
     * Ctor.
     * @param assignments assignments of values to columns
     * @param whereClause optional condition or null
     */
    public OnMergeMatchedUpdateAction(List<AssignmentPair> assignments, Expression whereClause) {
        this.assignments = assignments;
        this.whereClause = whereClause;
    }

    /**
     * Returns the action condition, or null if undefined.
     * @return condition
     */
    public Expression getWhereClause() {
        return whereClause;
    }

    /**
     * Sets the action condition, or null if undefined.
     * @param whereClause to set, or null to remove the condition
     */
    public void setWhereClause(Expression whereClause) {
        this.whereClause = whereClause;
    }

    /**
     * Returns the assignments to execute against any rows found in a named window
     * @return assignments
     */
    public List<AssignmentPair> getAssignments() {
        return assignments;
    }

    /**
     * Sets the assignments to execute against any rows found in a named window
     * @param assignments to set
     */
    public void setAssignments(List<AssignmentPair> assignments) {
        this.assignments = assignments;
    }

    @Override
    public void toEPL(StringWriter writer) {
        writer.write("then update set ");
        String delimiter = "";
        for (AssignmentPair pair : assignments)
        {
            writer.write(delimiter);
            writer.write(pair.getName());
            writer.write(" = ");
            pair.getValue().toEPL(writer, ExpressionPrecedenceEnum.MINIMUM);
            delimiter = ", ";
        }
        if (whereClause != null) {
            writer.write(" where ");
            whereClause.toEPL(writer, ExpressionPrecedenceEnum.MINIMUM);
        }
    }
}