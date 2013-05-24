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
import com.espertech.esper.core.service.EPServicesContext;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.core.StreamTypeServiceImpl;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.pattern.EvalFactoryNode;
import com.espertech.esper.schedule.ScheduleSpec;
import com.espertech.esper.view.ViewProcessingException;
import com.espertech.esper.view.ZeroDepthStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Starts and provides the stop method for EPL statements.
 */
public class EPStatementStartMethodCreateContext extends EPStatementStartMethodBase
{
    private static final Log log = LogFactory.getLog(EPStatementStartMethodCreateContext.class);

    public EPStatementStartMethodCreateContext(StatementSpecCompiled statementSpec) {
        super(statementSpec);
    }

    public EPStatementStartResult startInternal(final EPServicesContext services, StatementContext statementContext, boolean isNewStatement, boolean isRecoveringStatement, boolean isRecoveringResilient) throws ExprValidationException, ViewProcessingException {
        final CreateContextDesc context = statementSpec.getContextDesc();
        final AgentInstanceContext agentInstanceContext = getDefaultAgentInstanceContext(statementContext);

        // compile filter specs, if any
        Set<String> eventTypesReferenced = new HashSet<String>();
        validateContextDetail(services, statementContext, eventTypesReferenced, context.getContextDetail());
        services.getStatementEventTypeRefService().addReferences(statementContext.getStatementName(), eventTypesReferenced);

        // add context - does not activate that context
        services.getContextManagementService().addContextSpec(services, agentInstanceContext, context, isRecoveringResilient);

        // define output event type
        String typeName = "EventType_Context_" + context.getContextName();
        EventType resultType = services.getEventAdapterService().createAnonymousMapType(typeName, Collections.<String, Object>emptyMap());

        EPStatementStopMethod stopMethod = new EPStatementStopMethod() {
            public void stop() {
                // no action
            }
        };

        EPStatementDestroyMethod destroyMethod = new EPStatementDestroyMethod() {
            public void destroy() {
                services.getContextManagementService().destroyedContext(context.getContextName());
            }
        };
        return new EPStatementStartResult(new ZeroDepthStream(resultType), stopMethod, destroyMethod);
    }

    private void validateContextDetail(EPServicesContext servicesContext, StatementContext statementContext, Set<String> eventTypesReferenced, ContextDetail contextDetail) throws ExprValidationException {
        if (contextDetail instanceof ContextDetailPartitioned) {
            ContextDetailPartitioned segmented = (ContextDetailPartitioned) contextDetail;
            for (ContextDetailPartitionItem partition : segmented.getItems()) {
                FilterStreamSpecRaw raw = new FilterStreamSpecRaw(partition.getFilterSpecRaw(), Collections.<ViewSpec>emptyList(), null, new StreamSpecOptions());
                FilterStreamSpecCompiled result = (FilterStreamSpecCompiled) raw.compile(statementContext, eventTypesReferenced, false, Collections.<Integer>emptyList());
                partition.setFilterSpecCompiled(result.getFilterSpec());
            }
        }
        else if (contextDetail instanceof ContextDetailCategory) {

            // compile filter
            ContextDetailCategory category = (ContextDetailCategory) contextDetail;
            FilterStreamSpecRaw raw = new FilterStreamSpecRaw(category.getFilterSpecRaw(), Collections.<ViewSpec>emptyList(), null, new StreamSpecOptions());
            FilterStreamSpecCompiled result = (FilterStreamSpecCompiled) raw.compile(statementContext, eventTypesReferenced, false, Collections.<Integer>emptyList());
            category.setFilterSpecCompiled(result.getFilterSpec());
            servicesContext.getStatementEventTypeRefService().addReferences(statementContext.getStatementName(), eventTypesReferenced);

            // compile expressions
            for (ContextDetailCategoryItem item : category.getItems()) {
                FilterSpecRaw filterSpecRaw = new FilterSpecRaw(category.getFilterSpecRaw().getEventTypeName(), Collections.singletonList(item.getExpression()), null);
                FilterStreamSpecRaw rawExpr = new FilterStreamSpecRaw(filterSpecRaw, Collections.<ViewSpec>emptyList(), null, new StreamSpecOptions());
                FilterStreamSpecCompiled compiled = (FilterStreamSpecCompiled) rawExpr.compile(statementContext, eventTypesReferenced, false, Collections.<Integer>emptyList());
                item.setCompiledFilter(compiled.getFilterSpec());
            }
        }
        else if (contextDetail instanceof ContextDetailHash) {
            ContextDetailHash hashed = (ContextDetailHash) contextDetail;
            for (ContextDetailHashItem hashItem : hashed.getItems()) {
                FilterStreamSpecRaw raw = new FilterStreamSpecRaw(hashItem.getFilterSpecRaw(), Collections.<ViewSpec>emptyList(), null, new StreamSpecOptions());
                FilterStreamSpecCompiled result = (FilterStreamSpecCompiled) raw.compile(statementContext, eventTypesReferenced, false, Collections.<Integer>emptyList());
                hashItem.setFilterSpecCompiled(result.getFilterSpec());

                // validate parameters
                StreamTypeServiceImpl streamTypes = new StreamTypeServiceImpl(result.getFilterSpec().getFilterForEventType(), null, true, statementContext.getEngineURI());
                ExprValidationContext validationContext = new ExprValidationContext(streamTypes, statementContext.getMethodResolutionService(), null, statementContext.getSchedulingService(), statementContext.getVariableService(), getDefaultAgentInstanceContext(statementContext), statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
                ExprNodeUtility.validate(Collections.singletonList(hashItem.getFunction()), validationContext);
            }
        }
        else if (contextDetail instanceof ContextDetailInitiatedTerminated) {
            ContextDetailInitiatedTerminated fixed = (ContextDetailInitiatedTerminated) contextDetail;
            ContextDetailMatchPair startCondition = validateRewriteContextCondition(servicesContext, statementContext, fixed.getStart(), eventTypesReferenced, new MatchEventSpec(), new LinkedHashSet<String>());
            ContextDetailMatchPair endCondition = validateRewriteContextCondition(servicesContext, statementContext, fixed.getEnd(), eventTypesReferenced, startCondition.getMatches(), startCondition.getAllTags());
            fixed.setStart(startCondition.getCondition());
            fixed.setEnd(endCondition.getCondition());
        }
        else if (contextDetail instanceof ContextDetailInitiatedTerminated) {
            ContextDetailInitiatedTerminated overlap = (ContextDetailInitiatedTerminated) contextDetail;
            ContextDetailMatchPair startCondition = validateRewriteContextCondition(servicesContext, statementContext, overlap.getStart(), eventTypesReferenced, new MatchEventSpec(), new LinkedHashSet<String>());
            ContextDetailMatchPair endCondition = validateRewriteContextCondition(servicesContext, statementContext, overlap.getEnd(), eventTypesReferenced, startCondition.getMatches(), startCondition.getAllTags());
            overlap.setStart(startCondition.getCondition());
            overlap.setEnd(endCondition.getCondition());
        }
        else if (contextDetail instanceof ContextDetailNested) {
            ContextDetailNested nested = (ContextDetailNested) contextDetail;
            for (CreateContextDesc nestedContext : nested.getContexts()) {
                validateContextDetail(servicesContext, statementContext, eventTypesReferenced, nestedContext.getContextDetail());
            }
        }
        else {
            throw new IllegalStateException("Unrecognized context detail " + contextDetail);
        }
    }

    private ContextDetailMatchPair validateRewriteContextCondition(EPServicesContext servicesContext, StatementContext statementContext, ContextDetailCondition endpoint, Set<String> eventTypesReferenced, MatchEventSpec priorMatches, Set<String> priorAllTags) throws ExprValidationException {
        if (endpoint instanceof ContextDetailConditionCrontab) {
            ContextDetailConditionCrontab crontab = (ContextDetailConditionCrontab) endpoint;
            ScheduleSpec schedule = ExprNodeUtility.toCrontabSchedule(crontab.getCrontab(), statementContext);
            crontab.setSchedule(schedule);
            return new ContextDetailMatchPair(crontab, new MatchEventSpec(), new LinkedHashSet<String>());
        }

        if (endpoint instanceof ContextDetailConditionTimePeriod) {
            ContextDetailConditionTimePeriod timePeriod = (ContextDetailConditionTimePeriod) endpoint;
            ExprValidationContext validationContext = new ExprValidationContext(new StreamTypeServiceImpl(servicesContext.getEngineURI(), false), statementContext.getMethodResolutionService(), null, statementContext.getSchedulingService(), statementContext.getVariableService(), getDefaultAgentInstanceContext(statementContext), statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
            ExprNodeUtility.getValidatedSubtree(timePeriod.getTimePeriod(), validationContext);
            return new ContextDetailMatchPair(timePeriod, new MatchEventSpec(), new LinkedHashSet<String>());
        }

        if (endpoint instanceof ContextDetailConditionPattern) {
            ContextDetailConditionPattern pattern = (ContextDetailConditionPattern) endpoint;
            Pair<MatchEventSpec, Set<String>> matches = validatePatternContextConditionPattern(statementContext, pattern, eventTypesReferenced, priorMatches, priorAllTags);
            return new ContextDetailMatchPair(pattern, matches.getFirst(), matches.getSecond());
        }

        if (endpoint instanceof ContextDetailConditionFilter) {
            ContextDetailConditionFilter filter = (ContextDetailConditionFilter) endpoint;

            // compile as filter if there are no prior match to consider
            if (priorMatches == null || (priorMatches.getArrayEventTypes().isEmpty() && priorMatches.getTaggedEventTypes().isEmpty())) {
                FilterStreamSpecRaw rawExpr = new FilterStreamSpecRaw(filter.getFilterSpecRaw(), Collections.<ViewSpec>emptyList(), null, new StreamSpecOptions());
                FilterStreamSpecCompiled compiled = (FilterStreamSpecCompiled) rawExpr.compile(statementContext, eventTypesReferenced, false, Collections.<Integer>emptyList());
                filter.setFilterSpecCompiled(compiled.getFilterSpec());
                MatchEventSpec matchEventSpec = new MatchEventSpec();
                EventType filterForType = compiled.getFilterSpec().getFilterForEventType();
                LinkedHashSet<String> allTags = new LinkedHashSet<String>();
                if (filter.getOptionalFilterAsName() != null) {
                    matchEventSpec.getTaggedEventTypes().put(filter.getOptionalFilterAsName(), new Pair<EventType, String>(filterForType, rawExpr.getRawFilterSpec().getEventTypeName()));
                    allTags.add(filter.getOptionalFilterAsName());
                }
                return new ContextDetailMatchPair(filter, matchEventSpec, allTags);
            }

            // compile as pattern if there are prior matches to consider, since this is a type of followed-by relationship
            EvalFactoryNode factoryNode = servicesContext.getPatternNodeFactory().makeFilterNode(filter.getFilterSpecRaw(), filter.getOptionalFilterAsName(), 0);
            ContextDetailConditionPattern pattern = new ContextDetailConditionPattern(factoryNode, true);
            Pair<MatchEventSpec, Set<String>> matches = validatePatternContextConditionPattern(statementContext, pattern, eventTypesReferenced, priorMatches, priorAllTags);
            return new ContextDetailMatchPair(pattern, matches.getFirst(), matches.getSecond());
        }
        else {
            throw new IllegalStateException("Unrecognized endpoint type " + endpoint);
        }
    }

    private Pair<MatchEventSpec, Set<String>> validatePatternContextConditionPattern(StatementContext statementContext, ContextDetailConditionPattern pattern, Set<String> eventTypesReferenced, MatchEventSpec priorMatches, Set<String> priorAllTags)
        throws ExprValidationException {
        PatternStreamSpecRaw raw = new PatternStreamSpecRaw(pattern.getPatternRaw(), Collections.<EvalFactoryNode, String>emptyMap(), Collections.<ViewSpec>emptyList(), null, new StreamSpecOptions());
        PatternStreamSpecCompiled compiled = raw.compile(statementContext, eventTypesReferenced, false, Collections.<Integer>emptyList(), priorMatches, priorAllTags);
        pattern.setPatternCompiled(compiled);
        return new Pair<MatchEventSpec, Set<String>>(new MatchEventSpec(compiled.getTaggedEventTypes(), compiled.getArrayEventTypes()), compiled.getAllTags());
    }

    private static class ContextDetailMatchPair {
        private final ContextDetailCondition condition;
        private final MatchEventSpec matches;
        private final Set<String> allTags;

        private ContextDetailMatchPair(ContextDetailCondition condition, MatchEventSpec matches, Set<String> allTags) {
            this.condition = condition;
            this.matches = matches;
            this.allTags = allTags;
        }

        public ContextDetailCondition getCondition() {
            return condition;
        }

        public MatchEventSpec getMatches() {
            return matches;
        }

        public Set<String> getAllTags() {
            return allTags;
        }
    }
}