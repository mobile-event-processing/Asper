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
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.property.PropertyEvaluator;
import com.espertech.esper.pattern.MatchedEventMap;

import java.util.*;

/**
 * Contains the filter criteria to sift through events. The filter criteria are the event class to look for and
 * a set of parameters (attribute names, operators and constant/range values).
 */
public final class FilterSpecCompiled
{
    private final static ArrayDeque<FilterSpecParam> EMPTY_LIST = new ArrayDeque<FilterSpecParam>();
    private final static FilterSpecParamComparator COMPARATOR_PARAMETERS = new FilterSpecParamComparator();

    private final EventType filterForEventType;
    private final String filterForEventTypeName;
    private final ArrayDeque<FilterSpecParam> parameters;
    private final PropertyEvaluator optionalPropertyEvaluator;

    /**
     * Constructor - validates parameter list against event type, throws exception if invalid
     * property names or mismatcing filter operators are found.
     * @param eventType is the event type
     * @param filterParameters is a list of filter parameters
     * @param eventTypeName is the name of the event type
     * @param optionalPropertyEvaluator optional if evaluating properties returned by filtered events
     * @throws IllegalArgumentException if validation invalid
     */
    public FilterSpecCompiled(EventType eventType, String eventTypeName, List<FilterSpecParam> filterParameters,
                              PropertyEvaluator optionalPropertyEvaluator)
    {
        this.filterForEventType = eventType;
        this.filterForEventTypeName = eventTypeName;
        this.parameters = sortRemoveDups(filterParameters);
        this.optionalPropertyEvaluator = optionalPropertyEvaluator;
    }

    /**
     * Returns type of event to filter for.
     * @return event type
     */
    public final EventType getFilterForEventType()
    {
        return filterForEventType;
    }

    /**
     * Returns list of filter parameters.
     * @return list of filter params
     */
    public final ArrayDeque<FilterSpecParam> getParameters()
    {
        return parameters;
    }

    /**
     * Returns the event type name.
     * @return event type name
     */
    public String getFilterForEventTypeName()
    {
        return filterForEventTypeName;
    }

    /**
     * Return the evaluator for property value if any is attached, or none if none attached.
     * @return property evaluator
     */
    public PropertyEvaluator getOptionalPropertyEvaluator()
    {
        return optionalPropertyEvaluator;
    }

    /**
     * Returns the result event type of the filter specification.
     * @return event type
     */
    public EventType getResultEventType()
    {
        if (optionalPropertyEvaluator != null)
        {
            return optionalPropertyEvaluator.getFragmentEventType();
        }
        else
        {
            return filterForEventType;
        }
    }

    /**
     * Returns the values for the filter, using the supplied result events to ask filter parameters
     * for the value to filter for.
     * @param matchedEvents contains the result events to use for determining filter values
     * @return filter values
     */
    public FilterValueSet getValueSet(MatchedEventMap matchedEvents, ExprEvaluatorContext evaluatorContext, List<FilterValueSetParam> addendum)
    {
        ArrayDeque<FilterValueSetParam> valueList;
        if (addendum != null) {
            valueList = new ArrayDeque<FilterValueSetParam>(parameters.size() + addendum.size());
            valueList.addAll(addendum);
        }
        else {
            valueList = new ArrayDeque<FilterValueSetParam>(parameters.size());
        }
        populateValueSet(valueList, matchedEvents, evaluatorContext);
        return new FilterValueSetImpl(filterForEventType, valueList);
    }

    private void populateValueSet(ArrayDeque<FilterValueSetParam> valueList, MatchedEventMap matchedEvents, ExprEvaluatorContext exprEvaluatorContext) {
        // Ask each filter specification parameter for the actual value to filter for
        for (FilterSpecParam specParam : parameters)
        {
            Object filterForValue = specParam.getFilterValue(matchedEvents, exprEvaluatorContext);

            FilterValueSetParam valueParam = new FilterValueSetParamImpl(specParam.getLookupable(), specParam.getFilterOperator(), filterForValue);
            valueList.add(valueParam);
        }
    }

    @SuppressWarnings({"StringConcatenationInsideStringBufferAppend"})
    public final String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("FilterSpecCompiled type=" + this.filterForEventType);
        buffer.append(" parameters=" + Arrays.toString(parameters.toArray()));
        return buffer.toString();
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof FilterSpecCompiled))
        {
            return false;
        }

        FilterSpecCompiled other = (FilterSpecCompiled) obj;
        if (!equalsTypeAndFilter(other))
        {
            return false;
        }

        if ((this.optionalPropertyEvaluator == null) && (other.optionalPropertyEvaluator == null))
        {
            return true;
        }       
        if ((this.optionalPropertyEvaluator != null) && (other.optionalPropertyEvaluator == null))
        {
            return false;
        }
        if ((this.optionalPropertyEvaluator == null) && (other.optionalPropertyEvaluator != null))
        {
            return false;
        }

        return this.optionalPropertyEvaluator.compareTo(other.optionalPropertyEvaluator);
    }

    /**
     * Compares only the type and filter portion and not the property evaluation portion.
     * @param other filter to compare
     * @return true if same
     */
    public boolean equalsTypeAndFilter(FilterSpecCompiled other)
    {
        if (this.filterForEventType != other.filterForEventType)
        {
            return false;
        }
        if (this.parameters.size() != other.parameters.size())
        {
            return false;
        }

        Iterator<FilterSpecParam> iterOne = parameters.iterator();
        Iterator<FilterSpecParam> iterOther = other.parameters.iterator();
        while (iterOne.hasNext())
        {
            if (!iterOne.next().equals(iterOther.next()))
            {
                return false;
            }
        }

        return true;
    }

    public int hashCode()
    {
        int hashCode = filterForEventType.hashCode();
        for (FilterSpecParam param : parameters)
        {
            hashCode ^= 31 * param.hashCode();
        }
        return hashCode;
    }

    protected static ArrayDeque<FilterSpecParam> sortRemoveDups(List<FilterSpecParam> parameters) {

        if (parameters.isEmpty()) {
            return EMPTY_LIST;
        }

        ArrayDeque<FilterSpecParam> result = new ArrayDeque<FilterSpecParam>();
        if (parameters.size() == 1) {
            result.addAll(parameters);
            return result;
        }

        TreeMap<FilterOperator, List<FilterSpecParam>> map = new TreeMap<FilterOperator, List<FilterSpecParam>>(COMPARATOR_PARAMETERS);
        for (FilterSpecParam parameter : parameters) {

            List<FilterSpecParam> list = map.get(parameter.getFilterOperator());
            if (list == null) {
                list = new ArrayList<FilterSpecParam>();
                map.put(parameter.getFilterOperator(), list);
            }

            boolean hasDuplicate = false;
            for (FilterSpecParam existing : list) {
                if (existing.getLookupable().equals(parameter.getLookupable())) {
                    hasDuplicate = true;
                    break;
                }
            }
            if (hasDuplicate) {
                continue;
            }

            list.add(parameter);
        }

        for (Map.Entry<FilterOperator, List<FilterSpecParam>> entry : map.entrySet()) {
            result.addAll(entry.getValue());
        }
        return result;
    }
}
