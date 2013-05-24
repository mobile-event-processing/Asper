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

public class QueryPlanIndexDescSubquery extends QueryPlanIndexDescBase {
    private final int subqueryNum;

    public QueryPlanIndexDescSubquery(String tableName, String indexBackingClass, int subqueryNum) {
        super(tableName, indexBackingClass);
        this.subqueryNum = subqueryNum;
    }

    public int getSubqueryNum() {
        return subqueryNum;
    }
}
