/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.start;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.context.ContextPartitionSelector;
import com.espertech.esper.client.context.ContextPartitionSelectorAll;
import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.core.context.mgr.ContextManager;
import com.espertech.esper.core.context.mgr.ContextPropertyRegistryImpl;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.service.EPPreparedQueryResult;
import com.espertech.esper.core.service.EPServicesContext;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.core.service.StreamJoinAnalysisResult;
import com.espertech.esper.epl.core.*;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.join.base.*;
import com.espertech.esper.epl.named.NamedWindowProcessor;
import com.espertech.esper.epl.named.NamedWindowProcessorInstance;
import com.espertech.esper.epl.spec.NamedWindowConsumerStreamSpec;
import com.espertech.esper.epl.spec.StatementSpecCompiled;
import com.espertech.esper.epl.spec.StreamSpecCompiled;
import com.espertech.esper.event.EventBeanReader;
import com.espertech.esper.event.EventBeanReaderDefaultImpl;
import com.espertech.esper.event.EventBeanUtility;
import com.espertech.esper.event.EventTypeSPI;
import com.espertech.esper.filter.FilterSpecCompiled;
import com.espertech.esper.filter.FilterSpecCompiler;
import com.espertech.esper.util.AuditPath;
import com.espertech.esper.view.Viewable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Starts and provides the stop method for EPL statements.
 */
public class EPPreparedExecuteMethod
{
    private static final Log queryPlanLog = LogFactory.getLog(AuditPath.QUERYPLAN_LOG);
    private static final Log log = LogFactory.getLog(EPPreparedExecuteMethod.class);

    private final StatementSpecCompiled statementSpec;
    private final ResultSetProcessor resultSetProcessor;
    private final NamedWindowProcessor[] processors;
    private final AgentInstanceContext agentInstanceContext;
    private final EPServicesContext services;
    private EventBeanReader eventBeanReader;
    private JoinSetComposerPrototype joinSetComposerPrototype;
    private final FilterSpecCompiled[] filters;

    /**
     * Ctor.
     * @param statementSpec is a container for the definition of all statement constructs that
     * may have been used in the statement, i.e. if defines the select clauses, insert into, outer joins etc.
     * @param services is the service instances for dependency injection
     * @param statementContext is statement-level information and statement services
     * @throws ExprValidationException if the preparation failed
     */
    public EPPreparedExecuteMethod(StatementSpecCompiled statementSpec,
                                EPServicesContext services,
                                StatementContext statementContext)
            throws ExprValidationException
    {
        boolean queryPlanLogging = services.getConfigSnapshot().getEngineDefaults().getLogging().isEnableQueryPlan();
        if (queryPlanLogging) {
            queryPlanLog.info("Query plans for Fire-and-forget query '" + statementContext.getExpression() + "'");
        }

        this.statementSpec = statementSpec;
        this.services = services;

        validateExecuteQuery();

        int numStreams = statementSpec.getStreamSpecs().size();
        EventType[] typesPerStream = new EventType[numStreams];
        String[] namesPerStream = new String[numStreams];
        processors = new NamedWindowProcessor[numStreams];
        agentInstanceContext = new AgentInstanceContext(statementContext, null, -1, null, null, statementContext.getDefaultAgentInstanceScriptContext());

        // resolve types and named window processors
        for (int i = 0; i < numStreams; i++)
        {
            final StreamSpecCompiled streamSpec = statementSpec.getStreamSpecs().get(i);
            NamedWindowConsumerStreamSpec namedSpec = (NamedWindowConsumerStreamSpec) streamSpec;

            String streamName = namedSpec.getWindowName();
            if (namedSpec.getOptionalStreamName() != null)
            {
                streamName = namedSpec.getOptionalStreamName();
            }
            namesPerStream[i] = streamName;

            processors[i] = services.getNamedWindowService().getProcessor(namedSpec.getWindowName());
            if (processors[i] == null) {
                throw new ExprValidationException("A named window by name '" + namedSpec.getWindowName() + "' does not exist");
            }
            typesPerStream[i] = processors[i].getTailView().getEventType();
        }

        // compile filter to optimize access to named window
        filters = new FilterSpecCompiled[numStreams];
        if (statementSpec.getFilterRootNode() != null) {
            LinkedHashMap<String, Pair<EventType, String>> tagged = new LinkedHashMap<String, Pair<EventType, String>>();
            for (int i = 0; i < numStreams; i++) {
                try {
                    StreamTypeServiceImpl types = new StreamTypeServiceImpl(typesPerStream, namesPerStream, new boolean[numStreams], services.getEngineURI(), false);
                    filters[i] = FilterSpecCompiler.makeFilterSpec(typesPerStream[i], namesPerStream[i],
                            Collections.singletonList(statementSpec.getFilterRootNode()), null,
                            tagged, tagged, types,
                            null, statementContext, Collections.singleton(i));
                }
                catch (Exception ex) {
                    log.warn("Unexpected exception analyzing filter paths: " + ex.getMessage(), ex);
                }
            }
        }

        // obtain result set processor
        boolean[] isIStreamOnly = new boolean[namesPerStream.length];
        Arrays.fill(isIStreamOnly, true);
        StreamTypeService typeService = new StreamTypeServiceImpl(typesPerStream, namesPerStream, isIStreamOnly, services.getEngineURI(), true);
        EPStatementStartMethodHelperValidate.validateNodes(statementSpec, statementContext, typeService, null);

        ResultSetProcessorFactoryDesc resultSetProcessorPrototype = ResultSetProcessorFactoryFactory.getProcessorPrototype(statementSpec, statementContext, typeService, null, new boolean[0], true, ContextPropertyRegistryImpl.EMPTY_REGISTRY, null);
        resultSetProcessor = EPStatementStartMethodHelperAssignExpr.getAssignResultSetProcessor(agentInstanceContext, resultSetProcessorPrototype);

        if (statementSpec.getSelectClauseSpec().isDistinct())
        {
            if (resultSetProcessor.getResultEventType() instanceof EventTypeSPI)
            {
                eventBeanReader = ((EventTypeSPI) resultSetProcessor.getResultEventType()).getReader();
            }
            if (eventBeanReader == null)
            {
                eventBeanReader = new EventBeanReaderDefaultImpl(resultSetProcessor.getResultEventType());
            }
        }

        // plan joins or simple queries
        if (numStreams > 1)
        {
            StreamJoinAnalysisResult streamJoinAnalysisResult = new StreamJoinAnalysisResult(numStreams);
            Arrays.fill(streamJoinAnalysisResult.getNamedWindow(), true);
            for (int i = 0; i < numStreams; i++) {
                NamedWindowProcessorInstance processorInstance = processors[i].getProcessorInstance(agentInstanceContext);
                if (processors[i].isVirtualDataWindow()) {
                    streamJoinAnalysisResult.getViewExternal()[i] = processorInstance.getRootViewInstance().getVirtualDataWindow();
                }
                String[][] uniqueIndexes = processors[i].getUniqueIndexes(processorInstance);
                streamJoinAnalysisResult.getUniqueKeys()[i] = uniqueIndexes;
            }

            boolean hasAggregations = !resultSetProcessorPrototype.getAggregationServiceFactoryDesc().getExpressions().isEmpty();
            joinSetComposerPrototype = JoinSetComposerPrototypeFactory.makeComposerPrototype(null, null,
                    statementSpec.getOuterJoinDescList(), statementSpec.getFilterRootNode(), typesPerStream, namesPerStream,
                    streamJoinAnalysisResult, queryPlanLogging, statementContext.getAnnotations(), new HistoricalViewableDesc(numStreams), agentInstanceContext, false, hasAggregations);
        }

        // check context partition use
        if (statementSpec.getOptionalContextName() != null) {
            if (numStreams > 1) {
                throw new ExprValidationException("Joins in runtime queries for context partitions are not supported");
            }
        }
    }

    /**
     * Returns the event type of the prepared statement.
     * @return event type
     */
    public EventType getEventType()
    {
        return resultSetProcessor.getResultEventType();
    }

    /**
     * Executes the prepared query.
     * @return query results
     */
    public EPPreparedQueryResult execute(ContextPartitionSelector[] contextPartitionSelectors)
    {
        int numStreams = processors.length;

        if (contextPartitionSelectors != null && contextPartitionSelectors.length != numStreams) {
            throw new IllegalArgumentException("Number of context partition selectors does not match the number of named windows in the from-clause");
        }

        // handle non-context case
        if (statementSpec.getOptionalContextName() == null) {

            Collection<EventBean>[] snapshots = new Collection[numStreams];
            for (int i = 0; i < numStreams; i++) {

                ContextPartitionSelector selector = contextPartitionSelectors == null ? null : contextPartitionSelectors[i];
                snapshots[i] = getStreamFilterSnapshot(i, selector);
            }

            resultSetProcessor.clear();
            return process(snapshots);
        }

        List<ContextPartitionResult> contextPartitionResults = new ArrayList<ContextPartitionResult>();

        // context partition runtime query
        Collection<Integer> agentInstanceIds;
        if (contextPartitionSelectors == null || contextPartitionSelectors[0] instanceof ContextPartitionSelectorAll) {
            agentInstanceIds = processors[0].getProcessorInstancesAll();
        }
        else {
            ContextManager contextManager = services.getContextManagementService().getContextManager(statementSpec.getOptionalContextName());
            agentInstanceIds = contextManager.getAgentInstanceIds(contextPartitionSelectors[0]);
        }

        // collect events and agent instances
        for (int agentInstanceId : agentInstanceIds) {
            NamedWindowProcessorInstance processorInstance = processors[0].getProcessorInstance(agentInstanceId);
            if (processorInstance != null) {
                Collection<EventBean> coll = processorInstance.getTailViewInstance().snapshot(filters[0], statementSpec.getAnnotations());
                contextPartitionResults.add(new ContextPartitionResult(coll, processorInstance.getTailViewInstance().getAgentInstanceContext()));
            }
        }

        // process context partitions
        ArrayDeque<EventBean[]> events = new ArrayDeque<EventBean[]>();
        for (ContextPartitionResult contextPartitionResult : contextPartitionResults) {
            Collection<EventBean> snapshot = contextPartitionResult.getEvents();
            if (statementSpec.getFilterRootNode() != null) {
                snapshot = getFiltered(snapshot, Collections.singletonList(statementSpec.getFilterRootNode()));
            }
            EventBean[] rows = snapshot.toArray(new EventBean[snapshot.size()]);
            resultSetProcessor.setAgentInstanceContext(contextPartitionResult.getContext());
            UniformPair<EventBean[]> results = resultSetProcessor.processViewResult(rows, null, true);
            if (results != null && results.getFirst() != null && results.getFirst().length > 0) {
                events.add(results.getFirst());
            }
        }
        return new EPPreparedQueryResult(resultSetProcessor.getResultEventType(), EventBeanUtility.flatten(events));
    }

    private Collection<EventBean> getStreamFilterSnapshot(int streamNum, ContextPartitionSelector contextPartitionSelector) {
        final StreamSpecCompiled streamSpec = statementSpec.getStreamSpecs().get(streamNum);
        NamedWindowConsumerStreamSpec namedSpec = (NamedWindowConsumerStreamSpec) streamSpec;
        NamedWindowProcessor namedWindowProcessor = processors[streamNum];

        // handle the case of a single or matching agent instance
        NamedWindowProcessorInstance processorInstance = namedWindowProcessor.getProcessorInstance(agentInstanceContext);
        if (processorInstance != null) {
            return getStreamSnapshotInstance(streamNum, namedSpec, processorInstance);
        }

        // context partition runtime query
        Collection<Integer> contextPartitions;
        if (contextPartitionSelector == null || contextPartitionSelector instanceof ContextPartitionSelectorAll) {
            contextPartitions = namedWindowProcessor.getProcessorInstancesAll();
        }
        else {
            ContextManager contextManager = services.getContextManagementService().getContextManager(namedWindowProcessor.getContextName());
            contextPartitions = contextManager.getAgentInstanceIds(contextPartitionSelector);
        }

        // collect events
        ArrayDeque<EventBean> events = new ArrayDeque<EventBean>();
        for (int agentInstanceId : contextPartitions) {
            processorInstance = namedWindowProcessor.getProcessorInstance(agentInstanceId);
            if (processorInstance != null) {
                Collection<EventBean> coll = processorInstance.getTailViewInstance().snapshot(filters[streamNum], statementSpec.getAnnotations());
                events.addAll(coll);
            }
        }
        return events;
    }

    private Collection<EventBean> getStreamSnapshotInstance(int streamNum, NamedWindowConsumerStreamSpec namedSpec, NamedWindowProcessorInstance processorInstance) {
        Collection<EventBean> coll = processorInstance.getTailViewInstance().snapshot(filters[streamNum], statementSpec.getAnnotations());
        if (namedSpec.getFilterExpressions().size() != 0) {
            coll = getFiltered(coll, namedSpec.getFilterExpressions());
        }
        return coll;
    }

    private EPPreparedQueryResult process(Collection<EventBean>[] snapshots) {

        int numStreams = processors.length;

        UniformPair<EventBean[]> results;
        if (numStreams == 1)
        {
            if (statementSpec.getFilterRootNode() != null)
            {
                snapshots[0] = getFiltered(snapshots[0], Arrays.asList(statementSpec.getFilterRootNode()));
            }
            EventBean[] rows = snapshots[0].toArray(new EventBean[snapshots[0].size()]);
            results = resultSetProcessor.processViewResult(rows, null, true);
        }
        else
        {
            Viewable[] viewablePerStream = new Viewable[numStreams];
            for (int i = 0; i < numStreams; i++)
            {
                NamedWindowProcessorInstance instance = processors[i].getProcessorInstance(agentInstanceContext);
                if (instance == null) {
                    throw new UnsupportedOperationException("Joins against named windows that are under context are not supported");
                }
                viewablePerStream[i] = instance.getTailViewInstance();
            }

            JoinSetComposerDesc joinSetComposerDesc = joinSetComposerPrototype.create(viewablePerStream, true);
            JoinSetComposer joinComposer = joinSetComposerDesc.getJoinSetComposer();
            JoinSetFilter joinFilter;
            if (joinSetComposerDesc.getPostJoinFilterEvaluator() != null) {
                joinFilter = new JoinSetFilter(joinSetComposerDesc.getPostJoinFilterEvaluator());
            }
            else {
                joinFilter = null;
            }

            EventBean[][] oldDataPerStream = new EventBean[numStreams][];
            EventBean[][] newDataPerStream = new EventBean[numStreams][];
            for (int i = 0; i < numStreams; i++)
            {
                newDataPerStream[i] = snapshots[i].toArray(new EventBean[snapshots[i].size()]);
            }
            UniformPair<Set<MultiKey<EventBean>>> result = joinComposer.join(newDataPerStream, oldDataPerStream, agentInstanceContext);
            if (joinFilter != null) {
                joinFilter.process(result.getFirst(), null, agentInstanceContext);
            }
            results = resultSetProcessor.processJoinResult(result.getFirst(), null, true);
        }

        if (statementSpec.getSelectClauseSpec().isDistinct())
        {
            results.setFirst(EventBeanUtility.getDistinctByProp(results.getFirst(), eventBeanReader));
        }

        return new EPPreparedQueryResult(resultSetProcessor.getResultEventType(), results.getFirst());
    }

    private void validateExecuteQuery() throws ExprValidationException
    {
        if (statementSpec.getSubSelectExpressions().size() > 0)
        {
            throw new ExprValidationException("Subqueries are not a supported feature of on-demand queries");
        }
        for (int i = 0; i < statementSpec.getStreamSpecs().size(); i++)
        {
            if (!(statementSpec.getStreamSpecs().get(i) instanceof NamedWindowConsumerStreamSpec))
            {
                throw new ExprValidationException("On-demand queries require named windows and do not allow event streams or patterns");
            }
            if (statementSpec.getStreamSpecs().get(i).getViewSpecs().size() != 0)
            {
                throw new ExprValidationException("Views are not a supported feature of on-demand queries");
            }
        }
        if (statementSpec.getOutputLimitSpec() != null)
        {
            throw new ExprValidationException("Output rate limiting is not a supported feature of on-demand queries");
        }
        if (statementSpec.getInsertIntoDesc() != null)
        {
            throw new ExprValidationException("Insert-into is not a supported feature of on-demand queries");
        }
    }

    private List<EventBean> getFiltered(Collection<EventBean> snapshot, List<ExprNode> filterExpressions)
    {
        EventBean[] eventsPerStream = new EventBean[1];
        List<EventBean> filteredSnapshot = new ArrayList<EventBean>();
        ExprEvaluator[] evaluators = ExprNodeUtility.getEvaluators(filterExpressions);
        for (EventBean row : snapshot)
        {
            boolean pass = true;
            eventsPerStream[0] = row;
            for (ExprEvaluator filter : evaluators)
            {
                Boolean result = (Boolean) filter.evaluate(eventsPerStream, true, agentInstanceContext);
                if (result == null || !result) {
                    pass = false;
                    break;
                }
            }

            if (pass)
            {
                filteredSnapshot.add(row);
            }
        }

        return filteredSnapshot;
    }

    public FilterSpecCompiled[] getFilters() {
        return filters;
    }

    public NamedWindowProcessor[] getProcessors() {
        return processors;
    }

    private static class ContextPartitionResult
    {
        private final Collection<EventBean> events;
        private final AgentInstanceContext context;

        private ContextPartitionResult(Collection<EventBean> events, AgentInstanceContext context) {
            this.events = events;
            this.context = context;
        }

        public Collection<EventBean> getEvents() {
            return events;
        }

        public AgentInstanceContext getContext() {
            return context;
        }
    }
}
