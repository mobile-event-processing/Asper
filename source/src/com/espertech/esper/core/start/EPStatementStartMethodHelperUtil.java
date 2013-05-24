/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.start;

import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.core.OrderByProcessor;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.core.ResultSetProcessorFactoryDesc;
import com.espertech.esper.epl.expression.ExprSubselectNode;
import com.espertech.esper.epl.spec.FilterStreamSpecCompiled;
import com.espertech.esper.epl.spec.StatementSpecCompiled;
import com.espertech.esper.epl.spec.StreamSpecCompiled;
import com.espertech.esper.event.EventTypeUtility;
import com.espertech.esper.pattern.EvalFactoryNode;
import com.espertech.esper.pattern.EvalFilterFactoryNode;
import com.espertech.esper.view.ViewFactoryChain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class EPStatementStartMethodHelperUtil
{
    private static final Log log = LogFactory.getLog(EPStatementStartMethodHelperUtil.class);

    public static Pair<ResultSetProcessor, AggregationService> startResultSetAndAggregation(ResultSetProcessorFactoryDesc resultSetProcessorPrototype, AgentInstanceContext agentInstanceContext) {
        AggregationService aggregationService = null;
        if (resultSetProcessorPrototype.getAggregationServiceFactoryDesc() != null) {
            aggregationService = resultSetProcessorPrototype.getAggregationServiceFactoryDesc().getAggregationServiceFactory().makeService(agentInstanceContext, agentInstanceContext.getStatementContext().getMethodResolutionService());
        }

        OrderByProcessor orderByProcessor = null;
        if (resultSetProcessorPrototype.getOrderByProcessorFactory() != null) {
            orderByProcessor = resultSetProcessorPrototype.getOrderByProcessorFactory().instantiate(aggregationService);
        }

        ResultSetProcessor resultSetProcessor = resultSetProcessorPrototype.getResultSetProcessorFactory().instantiate(orderByProcessor, aggregationService, agentInstanceContext);

        return new Pair<ResultSetProcessor, AggregationService>(resultSetProcessor, aggregationService);
    }

    /**
     * Returns a stream name assigned for each stream, generated if none was supplied.
     * @param streams - stream specifications
     * @return array of stream names
     */
    @SuppressWarnings({"StringContatenationInLoop"})
    protected static String[] determineStreamNames(List<StreamSpecCompiled> streams)
    {
        String[] streamNames = new String[streams.size()];
        for (int i = 0; i < streams.size(); i++)
        {
            // Assign a stream name for joins, if not supplied
            streamNames[i] = streams.get(i).getOptionalStreamName();
            if (streamNames[i] == null)
            {
                streamNames[i] = "stream_" + i;
            }
        }
        return streamNames;
    }

    protected static boolean[] getHasIStreamOnly(boolean[] isNamedWindow, ViewFactoryChain[] unmaterializedViewChain)
    {
        boolean[] result = new boolean[unmaterializedViewChain.length];
        for (int i = 0; i < unmaterializedViewChain.length; i++) {
            if (isNamedWindow[i]) {
                continue;
            }
            result[i] = unmaterializedViewChain[i].getDataWindowViewFactoryCount() == 0;
        }
        return result;
    }

    protected static boolean determineSubquerySameStream(StatementSpecCompiled statementSpec, FilterStreamSpecCompiled filterStreamSpec) {
        for (ExprSubselectNode subselect : statementSpec.getSubSelectExpressions()) {
            StreamSpecCompiled streamSpec = subselect.getStatementSpecCompiled().getStreamSpecs().get(0);
            if (!(streamSpec instanceof FilterStreamSpecCompiled)) {
                continue;
            }
            FilterStreamSpecCompiled filterStream = (FilterStreamSpecCompiled) streamSpec;
            EventType typeSubselect = filterStream.getFilterSpec().getFilterForEventType();
            EventType typeFiltered = filterStreamSpec.getFilterSpec().getFilterForEventType();
            if (EventTypeUtility.isTypeOrSubTypeOf(typeSubselect, typeFiltered) || EventTypeUtility.isTypeOrSubTypeOf(typeFiltered, typeSubselect)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean isConsumingFilters(EvalFactoryNode evalNode) {
        if (evalNode instanceof EvalFilterFactoryNode) {
            return ((EvalFilterFactoryNode) evalNode).getConsumptionLevel() != null;
        }
        boolean consumption = false;
        for (EvalFactoryNode child : evalNode.getChildNodes()) {
            consumption = consumption || isConsumingFilters(child);
        }
        return consumption;
    }
}
