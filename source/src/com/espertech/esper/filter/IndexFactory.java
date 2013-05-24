/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.filter;

import com.espertech.esper.client.EventType;

/**
 * Factory for {@link FilterParamIndexBase} instances based on event property name and filter operator type.
 */
public class IndexFactory
{
    /**
     * Factory for indexes that store filter parameter constants for a given event property and filter
     * operator.
     * <p>Does not perform any check of validity of property name.
     *
     * @param filterOperator is the type of index to use
     * @return the proper index based on the filter operator type
     */
    public static FilterParamIndexBase createIndex(FilterSpecLookupable lookupable, FilterOperator filterOperator)
    {
        FilterParamIndexBase index;
        Class returnValueType = lookupable.getReturnType();

        // Handle all EQUAL comparisons
        if (filterOperator == FilterOperator.EQUAL)
        {
            index = new FilterParamIndexEquals(lookupable);
            return index;
        }

        // Handle all NOT-EQUAL comparisons
        if (filterOperator == FilterOperator.NOT_EQUAL)
        {
            index = new FilterParamIndexNotEquals(lookupable);
            return index;
        }

        if (filterOperator == FilterOperator.IS)
        {
            index = new FilterParamIndexEqualsIs(lookupable);
            return index;
        }

        if (filterOperator == FilterOperator.IS_NOT)
        {
            index = new FilterParamIndexNotEqualsIs(lookupable);
            return index;
        }

        // Handle all GREATER, LESS etc. comparisons
        if ((filterOperator == FilterOperator.GREATER) ||
            (filterOperator == FilterOperator.GREATER_OR_EQUAL) ||
            (filterOperator == FilterOperator.LESS) ||
            (filterOperator == FilterOperator.LESS_OR_EQUAL))
        {
            if (returnValueType != String.class) {
                index = new FilterParamIndexCompare(lookupable, filterOperator);
            }
            else {
                index = new FilterParamIndexCompareString(lookupable, filterOperator);
            }
            return index;
        }

        // Handle all normal and inverted RANGE comparisons
        if (filterOperator.isRangeOperator())
        {
            if (returnValueType != String.class) {
                index = new FilterParamIndexDoubleRange(lookupable, filterOperator);
            }
            else {
                index = new FilterParamIndexStringRange(lookupable, filterOperator);
            }
            return index;
        }
        if (filterOperator.isInvertedRangeOperator())
        {
            if (returnValueType != String.class) {
                return new FilterParamIndexDoubleRangeInverted(lookupable, filterOperator);
            }
            else {
                return new FilterParamIndexStringRangeInverted(lookupable, filterOperator);
            }
        }

        // Handle all IN and NOT IN comparisons
        if (filterOperator == FilterOperator.IN_LIST_OF_VALUES)
        {
            return new FilterParamIndexIn(lookupable);
        }
        if (filterOperator == FilterOperator.NOT_IN_LIST_OF_VALUES)
        {
            return new FilterParamIndexNotIn(lookupable);
        }

        // Handle all boolean expression
        if (filterOperator == FilterOperator.BOOLEAN_EXPRESSION)
        {
            return new FilterParamIndexBooleanExpr();
        }
        throw new IllegalArgumentException("Cannot create filter index instance for filter operator " + filterOperator);
    }
}

