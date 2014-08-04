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

package com.espertech.esper.support.epl;

import com.espertech.esper.epl.join.plan.QueryPlan;
import com.espertech.esper.epl.join.plan.QueryPlanIndex;
import com.espertech.esper.epl.join.plan.QueryPlanIndexItem;
import com.espertech.esper.epl.join.util.QueryPlanIndexDescFAF;
import com.espertech.esper.epl.join.util.QueryPlanIndexDescOnExpr;
import com.espertech.esper.epl.join.util.QueryPlanIndexDescSubquery;
import com.espertech.esper.epl.join.util.QueryPlanIndexHook;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

public class SupportQueryPlanIndexHook implements QueryPlanIndexHook {

    private static final List<QueryPlanIndexDescSubquery> subqueries = new ArrayList<QueryPlanIndexDescSubquery>();
    private static final List<QueryPlanIndexDescOnExpr> onexprs = new ArrayList<QueryPlanIndexDescOnExpr>();
    private static final List<QueryPlanIndexDescFAF> fafSnapshots = new ArrayList<QueryPlanIndexDescFAF>();
    private static final List<QueryPlan> joins = new ArrayList<QueryPlan>();

    public static String resetGetClassName() {
        reset();
        return SupportQueryPlanIndexHook.class.getName();
    }

    public static void reset() {
        subqueries.clear();
        onexprs.clear();
        fafSnapshots.clear();
        joins.clear();
    }

    public void subquery(QueryPlanIndexDescSubquery subquery) {
        subqueries.add(subquery);
    }

    public void namedWindowOnExpr(QueryPlanIndexDescOnExpr onexprdesc) {
        onexprs.add(onexprdesc);
    }

    public void fireAndForget(QueryPlanIndexDescFAF fafdesc) {
        fafSnapshots.add(fafdesc);
    }

    public void join(QueryPlan join) {
        joins.add(join);
    }

    public static List<QueryPlanIndexDescSubquery> getAndResetSubqueries() {
        List<QueryPlanIndexDescSubquery> copy = new ArrayList<QueryPlanIndexDescSubquery>(subqueries);
        reset();
        return copy;
    }

    public static void assertSubquery(QueryPlanIndexDescSubquery subquery, int subqueryNum, String tableName, String indexBackingClass) {
        Assert.assertEquals(tableName, subquery.getTableName());
        Assert.assertEquals(subqueryNum, subquery.getSubqueryNum());
        Assert.assertEquals(indexBackingClass, subquery.getIndexBackingClass());
    }

    public static void assertSubqueryAndReset(int subqueryNum, String tableName, String indexBackingClass) {
        Assert.assertTrue(subqueries.size() == 1);
        QueryPlanIndexDescSubquery subquery = subqueries.get(0);
        assertSubquery(subquery, subqueryNum, tableName, indexBackingClass);
        reset();
    }

    public static void assertOnExprAndReset(String tableName, String indexBackingClass) {
        Assert.assertTrue(onexprs.size() == 1);
        QueryPlanIndexDescOnExpr onexp = onexprs.get(0);
        Assert.assertEquals(tableName, onexp.getTableName());
        Assert.assertEquals(indexBackingClass, onexp.getIndexBackingClass());
        reset();
    }

    public static void assertFAFAndReset(String tableName, String indexBackingClass) {
        Assert.assertTrue(fafSnapshots.size() == 1);
        QueryPlanIndexDescFAF fafdesc = fafSnapshots.get(0);
        Assert.assertEquals(tableName, fafdesc.getTableName());
        Assert.assertEquals(indexBackingClass, fafdesc.getIndexBackingClass());
        reset();
    }

    public static void assertJoinOneStreamAndReset(boolean unique) {
        Assert.assertTrue(joins.size() == 1);
        QueryPlan join = joins.get(0);
        QueryPlanIndex first = join.getIndexSpecs()[1];
        String firstName = first.getItems().keySet().iterator().next();
        QueryPlanIndexItem index = first.getItems().get(firstName);
        Assert.assertEquals(unique, index.isUnique());
        reset();
    }

    public static void assertJoinAllStreamsAndReset(boolean unique) {
        Assert.assertTrue(joins.size() == 1);
        QueryPlan join = joins.get(0);
        for (QueryPlanIndex index : join.getIndexSpecs()) {
            String firstName = index.getItems().keySet().iterator().next();
            QueryPlanIndexItem indexDesc = index.getItems().get(firstName);
            Assert.assertEquals(unique, indexDesc.isUnique());
        }
        reset();
    }
}
