/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.plan;

import com.espertech.esper.epl.expression.ExprIdentNode;
import com.espertech.esper.epl.expression.ExprNode;

import java.io.StringWriter;
import java.util.*;

/**
 * Property lists stored as a value for each stream-to-stream relationship, for use by {@link QueryGraph}.
 */
public class QueryGraphValue
{
    private Map<String, QueryGraphValueEntry> entries;

    /**
     * Ctor.
     */
    public QueryGraphValue()
    {
        entries = new LinkedHashMap<String, QueryGraphValueEntry>();
    }

    public Map<String, QueryGraphValueEntry> getEntries() {
        return entries;
    }

    /**
     * Add key and index property.
     * @param keyProperty - key property
     * @param indexProperty - index property
     * @return true if added and either property did not exist, false if either already existed
     */
    public boolean addStrictCompare(String keyProperty, ExprIdentNode keyPropNode, String indexProperty, ExprIdentNode indexPropNode)
    {
        QueryGraphValueEntry value = entries.get(indexProperty);
        if (value instanceof QueryGraphValueEntryHashKeyedExpr) {
            // if this index property exists and is compared to a constant, ignore the index prop
            QueryGraphValueEntryHashKeyedExpr expr = (QueryGraphValueEntryHashKeyedExpr) value;
            if (expr.isConstant()) {
                return false;
            }
        }
        if (value instanceof QueryGraphValueEntryHashKeyedProp) {
            return false;   // second comparison, ignore
        }

        entries.put(indexProperty, new QueryGraphValueEntryHashKeyedProp(keyPropNode, keyProperty));
        return true;
    }

    public void addRange(QueryGraphRangeEnum rangeType, ExprNode propertyStart, ExprNode propertyEnd, String propertyValue) {
        if (!rangeType.isRange()) {
            throw new IllegalArgumentException("Expected range type, received " + rangeType);
        }

        // duplicate can be removed right away
        if (entries.containsKey(propertyValue)) {
            return;
        }

        entries.put(propertyValue, new QueryGraphValueEntryRangeIn(rangeType, propertyStart, propertyEnd, true));
    }

    public void addRelOp(ExprNode propertyKey, QueryGraphRangeEnum op, String propertyValue, boolean isBetweenOrIn) {

        // Note: Read as follows:
        // System.out.println("If I have an index on '" + propertyValue + "' I'm evaluating " + propertyKey + " and finding all values of " + propertyValue + " " + op + " then " + propertyKey);

        // Check if there is an opportunity to convert this to a range or remove an earlier specification
        QueryGraphValueEntry existing = entries.get(propertyValue);
        if (existing == null) {
            entries.put(propertyValue, new QueryGraphValueEntryRangeRelOp(op, propertyKey, isBetweenOrIn));
            return;
        }

        if (!(existing instanceof QueryGraphValueEntryRangeRelOp)) {
            return; // another comparison exists already, don't add range
        }

        QueryGraphValueEntryRangeRelOp relOp = (QueryGraphValueEntryRangeRelOp) existing;
        QueryGraphRangeConsolidateDesc opsDesc = QueryGraphRangeUtil.getCanConsolidate(op, relOp.getType());
        if (opsDesc != null) {
            ExprNode start = !opsDesc.isReverse() ? relOp.getExpression() : propertyKey;
            ExprNode end = !opsDesc.isReverse() ?  propertyKey : relOp.getExpression();
            entries.remove(propertyValue);
            addRange(opsDesc.getType(), start, end, propertyValue);
        }
    }

    public void addUnkeyedExpr(String indexedProp, ExprNode exprNodeNoIdent) {
        entries.put(indexedProp, new QueryGraphValueEntryHashKeyedExpr(exprNodeNoIdent, false));
    }

    public void addKeyedExpr(String indexedProp, ExprNode exprNodeNoIdent) {
        entries.put(indexedProp, new QueryGraphValueEntryHashKeyedExpr(exprNodeNoIdent, true));
    }

    public QueryGraphValuePairHashKeyIndex getHashKeyProps() {
        List<QueryGraphValueEntryHashKeyed> keys = new ArrayList<QueryGraphValueEntryHashKeyed>();
        Deque<String> indexed = new ArrayDeque<String>();
        for (Map.Entry<String, QueryGraphValueEntry> entry : entries.entrySet()) {
            if (entry.getValue() instanceof QueryGraphValueEntryHashKeyed) {
                QueryGraphValueEntryHashKeyed keyprop = (QueryGraphValueEntryHashKeyed) entry.getValue();
                keys.add(keyprop);
                indexed.add(entry.getKey());
            }
        }

        String[] strictKeys = new String[indexed.size()];
        int count = 0;
        for (Map.Entry<String, QueryGraphValueEntry> entry : entries.entrySet()) {
            if (entry.getValue() instanceof QueryGraphValueEntryHashKeyed) {
                if (entry.getValue() instanceof QueryGraphValueEntryHashKeyedProp) {
                    QueryGraphValueEntryHashKeyedProp keyprop = (QueryGraphValueEntryHashKeyedProp) entry.getValue();
                    strictKeys[count] = keyprop.getKeyProperty();
                }
                count++;
            }
        }

        return new QueryGraphValuePairHashKeyIndex(indexed.toArray(new String[indexed.size()]), keys, strictKeys);
    }

    public QueryGraphValuePairRangeIndex getRangeProps() {
        Deque<String> indexed = new ArrayDeque<String>();
        List<QueryGraphValueEntryRange> keys = new ArrayList<QueryGraphValueEntryRange>();
        for (Map.Entry<String, QueryGraphValueEntry> entry : entries.entrySet()) {
            if (entry.getValue() instanceof QueryGraphValueEntryRange) {
                QueryGraphValueEntryRange keyprop = (QueryGraphValueEntryRange) entry.getValue();
                keys.add(keyprop);
                indexed.add(entry.getKey());
            }
        }
        return new QueryGraphValuePairRangeIndex(indexed.toArray(new String[indexed.size()]), keys);
    }

    public String toString()
    {
        StringWriter writer = new StringWriter();
        writer.append("QueryGraphValue ");
        String delimiter = "";
        for (Map.Entry<String, QueryGraphValueEntry> entry : entries.entrySet()) {
            writer.append(delimiter);
            writer.append(entry.getKey() + ": " + entry.getValue());
            delimiter = ", ";
        }
        return writer.toString();
    }
}

