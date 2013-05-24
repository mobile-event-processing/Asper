/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.collection.MultiKeyUntyped;
import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * An order-by processor that sorts events according to the expressions
 * in the order_by clause.
 */
public class OrderByProcessorImpl implements OrderByProcessor {

	private static final Log log = LogFactory.getLog(OrderByProcessorImpl.class);

	private final OrderByProcessorFactoryImpl factory;
	private final AggregationService aggregationService;

    public OrderByProcessorImpl(OrderByProcessorFactoryImpl factory, AggregationService aggregationService) {
        this.factory = factory;
        this.aggregationService = aggregationService;
    }

    public Object getSortKey(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        OrderByElement[] elements = factory.getOrderBy();
        if (elements.length == 1) {
            return elements[0].getExpr().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        }

        Object[] values = new Object[elements.length];
        int count = 0;
        for (OrderByElement sortPair : factory.getOrderBy())
        {
            values[count++] = sortPair.getExpr().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        }

        return new MultiKeyUntyped(values);
    }

    public Object[] getSortKeyPerRow(EventBean[] generatingEvents, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        if (generatingEvents == null)
        {
            return null;
        }

        Object[] sortProperties = new Object[generatingEvents.length];

        int count = 0;
        EventBean[] evalEventsPerStream = new EventBean[1];

        if (factory.getOrderBy().length == 1) {
            ExprEvaluator singleEval = factory.getOrderBy()[0].getExpr();
            for (EventBean theEvent : generatingEvents)
            {
                evalEventsPerStream[0] = theEvent;
                sortProperties[count] = singleEval.evaluate(evalEventsPerStream, isNewData, exprEvaluatorContext);
                count++;
            }
        }
        else {
            for (EventBean theEvent : generatingEvents)
            {
                Object[] values = new Object[factory.getOrderBy().length];
                int countTwo = 0;
                evalEventsPerStream[0] = theEvent;
                for (OrderByElement sortPair : factory.getOrderBy())
                {
                    values[countTwo++] = sortPair.getExpr().evaluate(evalEventsPerStream, isNewData, exprEvaluatorContext);
                }

                sortProperties[count] = new MultiKeyUntyped(values);
                count++;
            }
        }

        return sortProperties;
    }

	public EventBean[] sort(EventBean[] outgoingEvents, EventBean[][] generatingEvents, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
	{
		if (outgoingEvents == null || outgoingEvents.length < 2)
		{
			return outgoingEvents;
		}

		// Get the group by keys if needed
		Object[] groupByKeys = null;
		if (factory.isNeedsGroupByKeys())
		{
			groupByKeys = generateGroupKeys(generatingEvents, isNewData, exprEvaluatorContext);
		}

		return sort(outgoingEvents, generatingEvents, groupByKeys, isNewData, exprEvaluatorContext);
	}

	public EventBean[] sort(EventBean[] outgoingEvents, EventBean[][] generatingEvents, Object[] groupByKeys, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
	{
        if (outgoingEvents == null || outgoingEvents.length < 2)
		{
			return outgoingEvents;
		}

		// Create the multikeys of sort values
		List<Object> sortValuesMultiKeys = createSortProperties(generatingEvents, groupByKeys, isNewData, exprEvaluatorContext);

		// Map the sort values to the corresponding outgoing events
		Map<Object, List<EventBean>> sortToOutgoing = new HashMap<Object, List<EventBean>>();
		int countOne = 0;
		for (Object sortValues : sortValuesMultiKeys)
		{
			List<EventBean> list = sortToOutgoing.get(sortValues);
			if (list == null)
			{
				list = new ArrayList<EventBean>();
			}
			list.add(outgoingEvents[countOne++]);
			sortToOutgoing.put(sortValues, list);
		}

		// Sort the sort values
		Collections.sort(sortValuesMultiKeys, factory.getComparator());

		// Sort the outgoing events in the same order
		Set<Object> sortSet = new LinkedHashSet<Object>(sortValuesMultiKeys);
		EventBean[] result = new EventBean[outgoingEvents.length];
		int countTwo = 0;
		for (Object sortValues : sortSet)
		{
			Collection<EventBean> output = sortToOutgoing.get(sortValues);
			for(EventBean theEvent : output)
			{
				result[countTwo++] = theEvent;
			}
		}

		return result;
	}

	private List<Object> createSortProperties(EventBean[][] generatingEvents, Object[] groupByKeys, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
	{
		Object[] sortProperties = new Object[generatingEvents.length];

        OrderByElement[] elements = factory.getOrderBy();
        if (elements.length == 1) {
            int count = 0;
            for (EventBean[] eventsPerStream : generatingEvents)
            {
                // Make a new multikey that contains the sort-by values.
                if (factory.isNeedsGroupByKeys())
                {
                    aggregationService.setCurrentAccess(groupByKeys[count], exprEvaluatorContext.getAgentInstanceId());
                }

                sortProperties[count] = elements[0].getExpr().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
                count++;
            }
        }
        else {
            int count = 0;
            for (EventBean[] eventsPerStream : generatingEvents)
            {
                // Make a new multikey that contains the sort-by values.
                if (factory.isNeedsGroupByKeys())
                {
                    aggregationService.setCurrentAccess(groupByKeys[count], exprEvaluatorContext.getAgentInstanceId());
                }

                Object[] values = new Object[factory.getOrderBy().length];
                int countTwo = 0;
                for (OrderByElement sortPair : factory.getOrderBy())
                {
                    values[countTwo++] = sortPair.getExpr().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
                }

                sortProperties[count] = new MultiKeyUntyped(values);
                count++;
            }
        }
        return Arrays.asList(sortProperties);
	}

    public EventBean[] sort(EventBean[] outgoingEvents, Object[] orderKeys, ExprEvaluatorContext exprEvaluatorContext)
    {
        TreeMap<Object, Object> sort = new TreeMap<Object, Object>(factory.getComparator());

        if (outgoingEvents == null || outgoingEvents.length < 2)
        {
            return outgoingEvents;
        }

        for (int i = 0; i < outgoingEvents.length; i++)
        {
            Object entry = sort.get(orderKeys[i]);
            if (entry == null)
            {
                sort.put(orderKeys[i], outgoingEvents[i]);
            }
            else if (entry instanceof EventBean)
            {
                List<EventBean> list = new ArrayList<EventBean>();
                list.add((EventBean)entry);
                list.add(outgoingEvents[i]);
                sort.put(orderKeys[i], list);
            }
            else
            {
                List<EventBean> list = (List<EventBean>) entry;
                list.add(outgoingEvents[i]);
            }
        }

        EventBean[] result = new EventBean[outgoingEvents.length];
        int count = 0;
        for (Object entry : sort.values())
        {
            if (entry instanceof List)
            {
                List<EventBean> output = (List<EventBean>) entry;
                for(EventBean theEvent : output)
                {
                    result[count++] = theEvent;
                }
            }
            else
            {
                result[count++] = (EventBean) entry;
            }
        }
        return result;
    }

    private Object[] generateGroupKeys(EventBean[][] generatingEvents, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
	{
		Object keys[] = new Object[generatingEvents.length];

		int count = 0;
		for (EventBean[] eventsPerStream : generatingEvents)
		{
			keys[count++] = generateGroupKey(eventsPerStream, isNewData, exprEvaluatorContext);
		}

		return keys;
	}

    private Object generateGroupKey(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
    {
        ExprEvaluator[] evals = factory.getGroupByNodes();
        if (evals.length == 1) {
            return evals[0].evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        }

        Object[] keys = new Object[evals.length];
        int count = 0;
        for (ExprEvaluator exprNode : evals)
        {
            keys[count] = exprNode.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
            count++;
        }

        return new MultiKeyUntyped(keys);
    }
}
