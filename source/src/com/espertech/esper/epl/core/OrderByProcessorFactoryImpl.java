/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.OrderByItem;
import com.espertech.esper.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Comparator;
import java.util.List;

/**
 * An order-by processor that sorts events according to the expressions
 * in the order_by clause.
 */
public class OrderByProcessorFactoryImpl implements OrderByProcessorFactory {

	private static final Log log = LogFactory.getLog(OrderByProcessorFactoryImpl.class);

	private final OrderByElement[] orderBy;
	private final ExprEvaluator[] groupByNodes;
	private final boolean needsGroupByKeys;
	private final Comparator<Object> comparator;

	/**
	 * Ctor.
	 *
	 * @param orderByList -
	 *            the nodes that generate the keys to sort events on
	 * @param groupByNodes -
	 *            generate the keys for determining aggregation groups
	 * @param needsGroupByKeys -
	 *            indicates whether this processor needs to have individual
	 *            group by keys to evaluate the sort condition successfully
     * @param isSortUsingCollator for string value sorting using compare or Collator
     * @throws com.espertech.esper.epl.expression.ExprValidationException when order-by items don't divulge a type
	 */
	public OrderByProcessorFactoryImpl(final List<OrderByItem> orderByList,
                                       List<ExprNode> groupByNodes,
                                       boolean needsGroupByKeys,
                                       boolean isSortUsingCollator)
            throws ExprValidationException
    {
		this.orderBy = toElementArray(orderByList);
		this.groupByNodes = ExprNodeUtility.getEvaluators(groupByNodes);
		this.needsGroupByKeys = needsGroupByKeys;

        comparator = getComparator(orderBy, isSortUsingCollator);
    }

    public OrderByProcessor instantiate(AggregationService aggregationService) {
        return new OrderByProcessorImpl(this, aggregationService);
    }

    public OrderByElement[] getOrderBy() {
        return orderBy;
    }

    public ExprEvaluator[] getGroupByNodes() {
        return groupByNodes;
    }

    public boolean isNeedsGroupByKeys() {
        return needsGroupByKeys;
    }

    public Comparator<Object> getComparator() {
        return comparator;
    }

    /**
     * Returns a comparator for order items that may sort string values using Collator.
     * @param orderBy order-by items
     * @param isSortUsingCollator true for Collator string sorting
     * @return comparator
     * @throws com.espertech.esper.epl.expression.ExprValidationException if the return type of order items cannot be determined
     */
    protected static Comparator<Object> getComparator(OrderByElement[] orderBy, boolean isSortUsingCollator) throws ExprValidationException
    {
        Comparator<Object> comparator;

        if (isSortUsingCollator)
        {
            // determine String types
            boolean hasStringTypes = false;
            boolean stringTypes[] = new boolean[orderBy.length];
            int count = 0;
            for (OrderByElement item : orderBy)
            {
                if (item.getExpr().getType() == String.class)
                {
                    hasStringTypes = true;
                    stringTypes[count] = true;
                }
                count++;
            }

            if (!hasStringTypes)
            {
                if (orderBy.length > 1) {
                    comparator = new MultiKeyCastingComparator(new MultiKeyComparator(getIsDescendingValues(orderBy)));
                }
                else {
                    comparator = new ObjectComparator(getIsDescendingValues(orderBy)[0]);
                }
            }
            else
            {
                if (orderBy.length > 1) {
                    comparator = new MultiKeyCastingComparator(new MultiKeyCollatingComparator(getIsDescendingValues(orderBy), stringTypes));
                }
                else {
                    comparator = new ObjectCollatingComparator(getIsDescendingValues(orderBy)[0]);
                }
            }
        }
        else
        {
            if (orderBy.length > 1) {
                comparator = new MultiKeyCastingComparator(new MultiKeyComparator(getIsDescendingValues(orderBy)));
            }
            else {
                comparator = new ObjectComparator(getIsDescendingValues(orderBy)[0]);
            }
        }

        return comparator;
    }

	private static boolean[] getIsDescendingValues(OrderByElement[] orderBy)
	{
		boolean[] isDescendingValues  = new boolean[orderBy.length];
		int count = 0;
		for(OrderByElement pair : orderBy)
		{
			isDescendingValues[count++] = pair.isDescending();
		}
		return isDescendingValues;
	}

    private OrderByElement[] toElementArray(List<OrderByItem> orderByList) {
        OrderByElement[] elements = new OrderByElement[orderByList.size()];
        int count = 0;
        for (OrderByItem item : orderByList) {
            elements[count++] = new OrderByElement(item.getExprNode().getExprEvaluator(), item.isDescending());
        }
        return elements;
    }
}
