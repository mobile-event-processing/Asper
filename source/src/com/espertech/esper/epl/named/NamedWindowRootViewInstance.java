/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.join.exec.base.RangeIndexLookupValue;
import com.espertech.esper.epl.join.exec.base.RangeIndexLookupValueEquals;
import com.espertech.esper.epl.join.exec.base.RangeIndexLookupValueRange;
import com.espertech.esper.epl.join.exec.composite.CompositeIndexLookup;
import com.espertech.esper.epl.join.exec.composite.CompositeIndexLookupFactory;
import com.espertech.esper.epl.join.hint.IndexHint;
import com.espertech.esper.epl.join.plan.*;
import com.espertech.esper.epl.join.table.*;
import com.espertech.esper.epl.join.util.*;
import com.espertech.esper.epl.lookup.*;
import com.espertech.esper.epl.spec.CreateIndexItem;
import com.espertech.esper.epl.spec.CreateIndexType;
import com.espertech.esper.epl.virtualdw.VirtualDWView;
import com.espertech.esper.filter.*;
import com.espertech.esper.util.CollectionUtil;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.view.ViewSupport;
import com.espertech.esper.view.Viewable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The root window in a named window plays multiple roles: It holds the indexes for deleting rows, if any on-delete statement
 * requires such indexes. Such indexes are updated when events arrive, or remove from when a data window
 * or on-delete statement expires events. The view keeps track of on-delete statements their indexes used.
 */
public class NamedWindowRootViewInstance extends ViewSupport
{
    private static final Log log = LogFactory.getLog(NamedWindowRootViewInstance.class);

    private final NamedWindowRootView rootView;
    private final AgentInstanceContext agentInstanceContext;

    private final NamedWindowIndexRepository indexRepository;
    private final Map<NamedWindowLookupStrategy, EventTable> tablePerMultiLookup;
    private final Map<SubordTableLookupStrategy, EventTable> tablePerSingleLookup;
    private final ConcurrentHashMap<String, EventTable> explicitIndexes;

    private Iterable<EventBean> dataWindowContents;

    public NamedWindowRootViewInstance(NamedWindowRootView rootView, AgentInstanceContext agentInstanceContext) {
        this.rootView = rootView;
        this.agentInstanceContext = agentInstanceContext;

        this.indexRepository = new NamedWindowIndexRepository();
        this.tablePerMultiLookup = new HashMap<NamedWindowLookupStrategy, EventTable>();
        this.tablePerSingleLookup = new HashMap<SubordTableLookupStrategy, EventTable>();
        this.explicitIndexes = new ConcurrentHashMap<String, EventTable>();
    }

    public IndexMultiKey[] getIndexes() {
        return indexRepository.getIndexDescriptors();
    }

    /**
     * Sets the iterator to use to obtain current named window data window contents.
     * @param dataWindowContents iterator over events help by named window
     */
    public void setDataWindowContents(Iterable<EventBean> dataWindowContents)
    {
        this.dataWindowContents = dataWindowContents;
    }

    /**
     * Called by tail view to indicate that the data window view exired events that must be removed from index tables.
     * @param oldData removed stream of the data window
     */
    public void removeOldData(EventBean[] oldData)
    {
        if (rootView.getRevisionProcessor() != null)
        {
            rootView.getRevisionProcessor().removeOldData(oldData, indexRepository);
        }
        else
        {
            for (EventTable table : indexRepository.getTables())
            {
                table.remove(oldData);
            }
        }
    }

    /**
     * Called by tail view to indicate that the data window view has new events that must be added to index tables.
     * @param newData new event
     */
    public void addNewData(EventBean[] newData)
    {
        if (rootView.getRevisionProcessor() == null) {
            // Update indexes for fast deletion, if there are any
            for (EventTable table : indexRepository.getTables())
            {
                table.add(newData);
            }
        }
    }

    // Called by deletion strategy and also the insert-into for new events only
    public void update(EventBean[] newData, EventBean[] oldData)
    {
        if (rootView.getRevisionProcessor() != null)
        {
            rootView.getRevisionProcessor().onUpdate(newData, oldData, this, indexRepository);
        }
        else
        {
            // Update indexes for fast deletion, if there are any
            for (EventTable table : indexRepository.getTables())
            {
                if (rootView.isChildBatching()) {
                    table.add(newData);
                }
            }

            // Update child views
            updateChildren(newData, oldData);
        }
    }

    public void setParent(Viewable parent)
    {
        super.setParent(parent);
    }

    public EventType getEventType()
    {
        return rootView.getEventType();
    }

    public Iterator<EventBean> iterator()
    {
        return null;
    }

    /**
     * Destroy and clear resources.
     */
    public void destroy()
    {
        indexRepository.destroy();
        tablePerMultiLookup.clear();
        tablePerSingleLookup.clear();
    }

    /**
     * Return a snapshot using index lookup filters.
     * @param optionalFilter to index lookup
     * @return events
     */
    public Collection<EventBean> snapshot(FilterSpecCompiled optionalFilter, Annotation[] annotations) {

        // Determine virtual data window
        VirtualDWView virtualDataWindow = null;
        if (isVirtualDataWindow()) {
            virtualDataWindow = getVirtualDataWindow();
        }

        if (optionalFilter == null || optionalFilter.getParameters().isEmpty()) {
            if (virtualDataWindow != null) {
                Pair<IndexMultiKey,EventTable> pair = virtualDataWindow.getFireAndForgetDesc(Collections.<String>emptySet(), Collections.<String>emptySet());
                return virtualDataWindow.getFireAndForgetData(pair.getSecond(), new Object[0], new RangeIndexLookupValue[0], annotations);
            }
            return null;
        }

        // Determine what straight-equals keys and which ranges are available.
        // Widening/Coercion is part of filter spec compile.
        Set<String> keysAvailable = new HashSet<String>();
        Set<String> rangesAvailable = new HashSet<String>();
        for (FilterSpecParam param : optionalFilter.getParameters()) {
            if (!(param instanceof FilterSpecParamConstant || param instanceof FilterSpecParamRange)) {
                continue;
            }
            if (param.getFilterOperator() == FilterOperator.EQUAL || param.getFilterOperator() == FilterOperator.IS) {
                keysAvailable.add(param.getLookupable().getExpression());
            }
            else if (param.getFilterOperator().isRangeOperator() ||
                     param.getFilterOperator().isInvertedRangeOperator() ||
                     param.getFilterOperator().isComparisonOperator()) {
                rangesAvailable.add(param.getLookupable().getExpression());
            }
            else if (param.getFilterOperator().isRangeOperator()) {
                rangesAvailable.add(param.getLookupable().getExpression());
            }
        }

        // Find an index that matches the needs
        Pair<IndexMultiKey, EventTableAndNamePair> tablePair;
        if (virtualDataWindow != null) {
            Pair<IndexMultiKey, EventTable> tablePairNoName = virtualDataWindow.getFireAndForgetDesc(keysAvailable, rangesAvailable);
            tablePair = new Pair<IndexMultiKey, EventTableAndNamePair>(tablePairNoName.getFirst(), new EventTableAndNamePair(tablePairNoName.getSecond(), null));
        }
        else {
            IndexHint indexHint = IndexHint.getIndexHint(annotations);
            tablePair = indexRepository.findTable(keysAvailable, rangesAvailable, explicitIndexes, indexHint);
        }

        if (rootView.isQueryPlanLogging() && rootView.getQueryPlanLog().isInfoEnabled()) {
            String prefix = "Fire-and-forget from window " + rootView.getEventType().getName() + " ";
            String indexName = tablePair != null && tablePair.getSecond() != null ? tablePair.getSecond().getIndexName() : null;
            String indexText = indexName != null ? "index " + indexName + " " : "full table scan ";
            indexText += "(snapshot only, for join see separate query plan)";
            if (tablePair == null) {
                rootView.getQueryPlanLog().info(prefix + indexText);
            }
            else {
                rootView.getQueryPlanLog().info(prefix + indexText + tablePair.getSecond().getEventTable().toQueryPlan());
            }

            QueryPlanIndexHook hook = QueryPlanIndexHookUtil.getHook(annotations);
            if (hook != null) {
                hook.fireAndForget(new QueryPlanIndexDescFAF(indexName, tablePair != null ?
                        tablePair.getSecond().getEventTable().getClass().getSimpleName() : null));
            }
        }

        if (tablePair == null) {
            return null;    // indicates table scan
        }

        // Compile key index lookup values
        String[] keyIndexProps = IndexedPropDesc.getIndexProperties(tablePair.getFirst().getHashIndexedProps());
        Object[] keyValues = new Object[keyIndexProps.length];
        for (int keyIndex = 0; keyIndex < keyIndexProps.length; keyIndex++) {
            for (FilterSpecParam param : optionalFilter.getParameters()) {
                if (param.getLookupable().getExpression().equals(keyIndexProps[keyIndex])) {
                    keyValues[keyIndex] = param.getFilterValue(null, agentInstanceContext);
                    break;
                }
            }
        }

        // Analyze ranges - these may include key lookup value (EQUALS semantics)
        String[] rangeIndexProps = IndexedPropDesc.getIndexProperties(tablePair.getFirst().getRangeIndexedProps());
        RangeIndexLookupValue[] rangeValues;
        if (rangeIndexProps.length > 0) {
            rangeValues = compileRangeLookupValues(rangeIndexProps, optionalFilter.getParameters());
        }
        else {
            rangeValues = new RangeIndexLookupValue[0];
        }

        EventTable eventTable = tablePair.getSecond().getEventTable();
        if (virtualDataWindow != null) {
            return virtualDataWindow.getFireAndForgetData(eventTable, keyValues, rangeValues, annotations);
        }

        IndexMultiKey indexMultiKey = tablePair.getFirst();
        Set<EventBean> result;
        if (indexMultiKey.getHashIndexedProps().length > 0 && indexMultiKey.getRangeIndexedProps().length == 0) {
            if (indexMultiKey.getHashIndexedProps().length == 1) {
                PropertyIndexedEventTableSingle table = (PropertyIndexedEventTableSingle) eventTable;
                result = table.lookup(keyValues[0]);
            }
            else {
                PropertyIndexedEventTable table = (PropertyIndexedEventTable) eventTable;
                result = table.lookup(keyValues);
            }
        }
        else if (indexMultiKey.getHashIndexedProps().length == 0 && indexMultiKey.getRangeIndexedProps().length == 1) {
            PropertySortedEventTable table = (PropertySortedEventTable) eventTable;
            result = table.lookupConstants(rangeValues[0]);
        }
        else {
            PropertyCompositeEventTable table = (PropertyCompositeEventTable) eventTable;
            Class[] rangeCoercion = table.getOptRangeCoercedTypes();
            CompositeIndexLookup lookup = CompositeIndexLookupFactory.make(keyValues, rangeValues, rangeCoercion);
            result = new HashSet<EventBean>();
            lookup.lookup(table.getIndex(), result);
        }
        if (result != null) {
            return result;
        }
        return Collections.EMPTY_LIST;
    }

    private RangeIndexLookupValue[] compileRangeLookupValues(String[] rangeIndexProps, ArrayDeque<FilterSpecParam> parameters) {
        RangeIndexLookupValue[] result = new RangeIndexLookupValue[rangeIndexProps.length];

        for (int rangeIndex = 0; rangeIndex < rangeIndexProps.length; rangeIndex++) {
            for (FilterSpecParam param : parameters) {
                if (!(param.getLookupable().getExpression().equals(rangeIndexProps[rangeIndex]))) {
                    continue;
                }

                if (param.getFilterOperator() == FilterOperator.EQUAL || param.getFilterOperator() == FilterOperator.IS) {
                    result[rangeIndex] = new RangeIndexLookupValueEquals(param.getFilterValue(null, agentInstanceContext));
                }
                else if (param.getFilterOperator().isRangeOperator() || param.getFilterOperator().isInvertedRangeOperator()) {
                    QueryGraphRangeEnum opAdd = QueryGraphRangeEnum.mapFrom(param.getFilterOperator());
                    result[rangeIndex] = new RangeIndexLookupValueRange(param.getFilterValue(null, agentInstanceContext), opAdd, true);
                }
                else if (param.getFilterOperator().isComparisonOperator()) {

                    RangeIndexLookupValue existing = result[rangeIndex];
                    QueryGraphRangeEnum opAdd = QueryGraphRangeEnum.mapFrom(param.getFilterOperator());
                    if (existing == null) {
                        result[rangeIndex] = new RangeIndexLookupValueRange(param.getFilterValue(null, agentInstanceContext), opAdd, true);
                    }
                    else {
                        if (!(existing instanceof RangeIndexLookupValueRange)) {
                            continue;
                        }
                        RangeIndexLookupValueRange existingRange = (RangeIndexLookupValueRange) existing;
                        QueryGraphRangeEnum opExist = existingRange.getOperator();
                        QueryGraphRangeConsolidateDesc desc = QueryGraphRangeUtil.getCanConsolidate(opExist, opAdd);
                        if (desc != null) {
                            DoubleRange doubleRange = getDoubleRange(desc.isReverse(), existing.getValue(), param.getFilterValue(null, agentInstanceContext));
                            result[rangeIndex] = new RangeIndexLookupValueRange(doubleRange, desc.getType(), false);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Add an explicit index.
     * @param unique indicator whether unique
     * @param namedWindowName window name
     * @param indexName indexname
     * @param columns properties indexed
     * @throws com.espertech.esper.epl.expression.ExprValidationException if the index fails to be valid
     */
    public synchronized void addExplicitIndex(boolean unique, String namedWindowName, String indexName, List<CreateIndexItem> columns) throws ExprValidationException {

        if (explicitIndexes.containsKey(indexName)) {
            throw new ExprValidationException("Index by name '" + indexName + "' already exists");
        }

        List<IndexedPropDesc> hashProps = new ArrayList<IndexedPropDesc>();
        List<IndexedPropDesc> btreeProps = new ArrayList<IndexedPropDesc>();

        Set<String> indexed = new HashSet<String>();
        for (CreateIndexItem columnDesc : columns) {
            String columnName = columnDesc.getName();

            Class type = JavaClassHelper.getBoxedType(rootView.getEventType().getPropertyType(columnName));
            if (type == null) {
                throw new ExprValidationException("Property named '" + columnName + "' not found on named window '" + namedWindowName + "'");
            }
            if (!indexed.add(columnName)) {
                throw new ExprValidationException("Property named '" + columnName + "' has been declared more then once");
            }

            IndexedPropDesc desc = new IndexedPropDesc(columnName, type);
            if (columnDesc.getType() == CreateIndexType.HASH) {
                hashProps.add(desc);
            } else {
                btreeProps.add(desc);
            }
        }

        if (unique && !btreeProps.isEmpty()) {
            throw new ExprValidationException("Combination of unique index with btree (range) is not supported");
        }

        Pair<IndexMultiKey, EventTableAndNamePair> pair = indexRepository.addExplicitIndexOrReuse(unique, hashProps, btreeProps, dataWindowContents, rootView.getEventType(), indexName);
        explicitIndexes.put(indexName, pair.getSecond().getEventTable());
    }

    /**
     * Add an on-trigger view that, using a lookup strategy, looks up from the named window and may select or delete rows.
     * @return base view for on-trigger expression
     */
    public NamedWindowOnExprBaseView addOnExpr(NamedWindowOnExprFactory onExprFactory, AgentInstanceContext agentInstanceContext, ExprNode joinExpr, EventType filterEventType, ResultSetProcessor resultSetProcessor)
    {
        IndexHint indexHint = IndexHint.getIndexHint(agentInstanceContext.getStatementContext().getAnnotations());

        // Determine strategy for deletion and index table to use (if any)
        Pair<NamedWindowLookupStrategy,EventTableAndNamePair> strategy = getStrategyPair(agentInstanceContext.getStatementContext().getStatementName(), agentInstanceContext.getStatementContext().getStatementId(), agentInstanceContext.getStatementContext().getAnnotations(), joinExpr, filterEventType, indexHint, rootView.isEnableIndexShare(), -1);

        if (rootView.isQueryPlanLogging() && rootView.getQueryPlanLog().isInfoEnabled()) {
            String prefix = "On-Expr ";
            String indexName = strategy.getSecond() != null ? strategy.getSecond().getIndexName() : null;
            String indexText = indexName != null ? "index " + indexName + " " : "(implicit) ";
            rootView.getQueryPlanLog().info(prefix + "strategy " + strategy.getFirst().toQueryPlan());
            rootView.getQueryPlanLog().info(prefix + indexText + "table " + ((strategy.getSecond() == null) ? "N/A" : strategy.getSecond().getEventTable().toQueryPlan()));

            QueryPlanIndexHook hook = QueryPlanIndexHookUtil.getHook(agentInstanceContext.getStatementContext().getAnnotations());
            if (hook != null) {
                hook.namedWindowOnExpr(new QueryPlanIndexDescOnExpr(strategy.getSecond().getIndexName(),
                        strategy.getSecond().getEventTable().getClass().getSimpleName()));
            }
        }

        // If a new table is required, add that table to be updated
        if (strategy.getSecond() != null)
        {
            tablePerMultiLookup.put(strategy.getFirst(), strategy.getSecond().getEventTable());
        }

        return onExprFactory.make(strategy.getFirst(), this, agentInstanceContext, resultSetProcessor);
    }

    /**
     * Unregister an on-delete statement view, using the strategy as a key to remove a reference to the index table
     * used by the strategy.
     * @param strategy to use for deleting events
     */
    public void removeOnExpr(NamedWindowLookupStrategy strategy)
    {
        EventTable table = tablePerMultiLookup.remove(strategy);
        if (table != null)
        {
            indexRepository.removeTableReference(table);
        }
    }

    private Pair<IndexKeyInfo, EventTableAndNamePair> findCreateIndex(SubordPropPlan joinDesc, IndexHint optionalIndexHint, boolean isIndexShare, int subqueryNumber) {

        // hash property names and types
        String[] hashIndexPropsProvided = new String[joinDesc.getHashProps().size()];
        Class[] hashIndexCoercionType = new Class[joinDesc.getHashProps().size()];
        SubordPropHashKey[] hashJoinedProps = new SubordPropHashKey[joinDesc.getHashProps().size()];
        int count = 0;
        for (Map.Entry<String, SubordPropHashKey> entry : joinDesc.getHashProps().entrySet()) {
            hashIndexPropsProvided[count] = entry.getKey();
            hashIndexCoercionType[count] = entry.getValue().getCoercionType();
            hashJoinedProps[count++] = entry.getValue();
        }

        // hash property names and types
        String[] rangeIndexPropsProvided = new String[joinDesc.getRangeProps().size()];
        Class[] rangeIndexCoercionType = new Class[joinDesc.getRangeProps().size()];
        SubordPropRangeKey[] rangeJoinedProps = new SubordPropRangeKey[joinDesc.getRangeProps().size()];
        count = 0;
        for (Map.Entry<String, SubordPropRangeKey> entry : joinDesc.getRangeProps().entrySet()) {
            rangeIndexPropsProvided[count] = entry.getKey();
            rangeIndexCoercionType[count] = entry.getValue().getCoercionType();
            rangeJoinedProps[count++] = entry.getValue();
        }

        // Add all joined fields to an array for sorting
        List<IndexedPropDesc> hashedProps = new ArrayList<IndexedPropDesc>();
        List<IndexedPropDesc> btreeProps = new ArrayList<IndexedPropDesc>();
        for (int i = 0; i < hashIndexPropsProvided.length; i++) {
            hashedProps.add(new IndexedPropDesc(hashIndexPropsProvided[i], hashIndexCoercionType[i]));
        }
        for (int i = 0; i < rangeIndexPropsProvided.length; i++) {
            btreeProps.add(new IndexedPropDesc(rangeIndexPropsProvided[i], rangeIndexCoercionType[i]));
        }

        // Get or Create the table for this index (exact match or property names, type of index and coercion type is expected)
        Pair<IndexMultiKey, EventTableAndNamePair> tableDesc;
        if (isVirtualDataWindow()) {
            VirtualDWView viewExternal = getVirtualDataWindow();
            Pair<IndexMultiKey,EventTable> tableVW = viewExternal.getSubordinateQueryDesc(false, hashedProps, btreeProps);
            tableDesc = new Pair<IndexMultiKey, EventTableAndNamePair>(tableVW.getFirst(), new EventTableAndNamePair(tableVW.getSecond(), null));
        }
        else {
            if (joinDesc.getHashProps().isEmpty() && joinDesc.getRangeProps().isEmpty()) {
                return null;
            }
            tableDesc = indexRepository.addTableCreateOrReuse(hashedProps, btreeProps, dataWindowContents, rootView.getEventType(), optionalIndexHint, isIndexShare, subqueryNumber, rootView.getOptionalUniqueKeyProps());
        }

        // map the order of indexed columns (key) to the key information available
        IndexedPropDesc[] indexedKeyProps = tableDesc.getFirst().getHashIndexedProps();
        SubordPropHashKey[] hashesDesc = new SubordPropHashKey[indexedKeyProps.length];
        Class[] hashPropCoercionTypes = new Class[indexedKeyProps.length];
        boolean isCoerceHash = false;
        for (int i = 0; i < indexedKeyProps.length; i++) {
            String indexField = indexedKeyProps[i].getIndexPropName();
            int index = CollectionUtil.findItem(hashIndexPropsProvided, indexField);
            if (index == -1) {
                throw new IllegalStateException("Could not find index property for lookup '" + indexedKeyProps[i]);
            }
            hashesDesc[i] = hashJoinedProps[index];
            hashPropCoercionTypes[i] = indexedKeyProps[i].getCoercionType();
            ExprEvaluator evaluatorHashkey = hashesDesc[i].getHashKey().getKeyExpr().getExprEvaluator();
            if (evaluatorHashkey != null && indexedKeyProps[i].getCoercionType() != evaluatorHashkey.getType()) {   // we allow null evaluator
                isCoerceHash = true;
            }
        }

        // map the order of range columns (range) to the range information available
        indexedKeyProps = tableDesc.getFirst().getRangeIndexedProps();
        SubordPropRangeKey[] rangesDesc = new SubordPropRangeKey[indexedKeyProps.length];
        Class[] rangePropCoercionTypes = new Class[indexedKeyProps.length];
        boolean isCoerceRange = false;
        for (int i = 0; i < indexedKeyProps.length; i++) {
            String indexField = indexedKeyProps[i].getIndexPropName();
            int index = CollectionUtil.findItem(rangeIndexPropsProvided, indexField);
            if (index == -1) {
                throw new IllegalStateException("Could not find range property for lookup '" + indexedKeyProps[i]);
            }
            rangesDesc[i] = rangeJoinedProps[index];
            rangePropCoercionTypes[i] = rangeJoinedProps[index].getCoercionType();
            if (indexedKeyProps[i].getCoercionType() != rangePropCoercionTypes[i]) {
                isCoerceRange = true;
            }
        }

        IndexKeyInfo info = new IndexKeyInfo(Arrays.asList(hashesDesc),
                new CoercionDesc(isCoerceHash, hashPropCoercionTypes), Arrays.asList(rangesDesc), new CoercionDesc(isCoerceRange, rangePropCoercionTypes));
        return new Pair<IndexKeyInfo, EventTableAndNamePair>(info, tableDesc.getSecond());
    }

    private Pair<SubordTableLookupStrategy, EventTableAndNamePair> getSubqueryStrategyPair(String accessedByStatementName, String accessedByStatementId, Annotation[] accessedByStmtAnnotations, EventType[] outerStreamTypes, SubordPropPlan joinDesc, boolean isNWOnTrigger, boolean forceTableScan, IndexHint optionalIndexHint, boolean isIndexShare, int subqueryNumber) {

        Pair<IndexKeyInfo, EventTableAndNamePair> accessDesc = findCreateIndex(joinDesc, optionalIndexHint, isIndexShare, subqueryNumber);

        if (accessDesc == null) {
            return null;
        }

        IndexKeyInfo indexKeyInfo = accessDesc.getFirst();
        EventTableAndNamePair eventTableAndName = accessDesc.getSecond();
        EventTable eventTable = accessDesc.getSecond().getEventTable();

        List<SubordPropHashKey> hashKeys = indexKeyInfo.getOrderedHashProperties();
        CoercionDesc hashKeyCoercionTypes = indexKeyInfo.getOrderedKeyCoercionTypes();
        List<SubordPropRangeKey> rangeKeys = indexKeyInfo.getOrderedRangeDesc();
        CoercionDesc rangeKeyCoercionTypes = indexKeyInfo.getOrderedRangeCoercionTypes();

        SubordTableLookupStrategy lookupStrategy;
        if (isVirtualDataWindow()) {
            VirtualDWView viewExternal = getVirtualDataWindow();
            lookupStrategy = viewExternal.getSubordinateLookupStrategy(accessedByStatementName, accessedByStatementId, accessedByStmtAnnotations, outerStreamTypes,
                    hashKeys, hashKeyCoercionTypes, rangeKeys, rangeKeyCoercionTypes, isNWOnTrigger, eventTable, joinDesc, forceTableScan);
        }
        else {
            if (forceTableScan) {
                lookupStrategy = null;
            }
            else {
                SubordTableLookupStrategyFactory lookupStrategyFactory = SubordinateTableLookupStrategyUtil.getLookupStrategy(outerStreamTypes,
                        hashKeys, hashKeyCoercionTypes, rangeKeys, rangeKeyCoercionTypes, isNWOnTrigger);
                lookupStrategy = lookupStrategyFactory.makeStrategy(eventTable);
            }
        }

        return new Pair<SubordTableLookupStrategy,EventTableAndNamePair>(lookupStrategy, eventTableAndName);
    }

    private Pair<NamedWindowLookupStrategy,EventTableAndNamePair> getStrategyPair(String accessedByStatementName, String accessedByStatementId, Annotation[] accessedByStmtAnnotations, ExprNode joinExpr, EventType filterEventType, IndexHint optionalIndexHint, boolean isIndexShare, int subqueryNumber)
    {
        EventType[] allStreamsZeroIndexed = new EventType[] {rootView.getEventType(), filterEventType};
        EventType[] outerStreams = new EventType[] {filterEventType};
        SubordPropPlan joinedPropPlan = QueryPlanIndexBuilder.getJoinProps(joinExpr, 1, allStreamsZeroIndexed);

        // No join expression means delete all
        if (joinExpr == null && (!(isVirtualDataWindow())))
        {
            return new Pair<NamedWindowLookupStrategy,EventTableAndNamePair>(new NamedWindowLookupStrategyAllRows(dataWindowContents), null);
        }

        // Here the stream offset is 1 as the named window lookup provides the arriving event in stream 1
        Pair<SubordTableLookupStrategy,EventTableAndNamePair> lookupPair = getSubqueryStrategyPair(accessedByStatementName, accessedByStatementId, accessedByStmtAnnotations, outerStreams, joinedPropPlan, true, false, optionalIndexHint, isIndexShare, subqueryNumber);

        if (lookupPair == null) {
            return new Pair<NamedWindowLookupStrategy,EventTableAndNamePair>(new NamedWindowLookupStrategyTableScan(joinExpr.getExprEvaluator(), dataWindowContents), null);
        }

        if (joinExpr == null) {   // it can be null when using virtual data window
            return new Pair<NamedWindowLookupStrategy,EventTableAndNamePair>(
                    new NamedWindowLookupStrategyIndexedUnfiltered(lookupPair.getFirst()),
                    lookupPair.getSecond());
        }
        else {
            return new Pair<NamedWindowLookupStrategy,EventTableAndNamePair>(
                    new NamedWindowLookupStrategyIndexed(joinExpr.getExprEvaluator(), lookupPair.getFirst()),
                    lookupPair.getSecond());
        }
    }

    /**
     * Drop an explicit index.
     * @param indexName to drop
     */
    public void removeExplicitIndex(String indexName)
    {
        EventTable table = explicitIndexes.remove(indexName);
        if (table != null) {
            indexRepository.removeTableReference(table);
        }
    }

    public SubordTableLookupStrategy getAddSubqueryLookupStrategy(String accessedByStatementName, String accessedByStatementId, Annotation[] accessedByStmtAnnotations, EventType[] eventTypesPerStream, SubordPropPlan joinDesc, boolean fullTableScan, int subqueryNum, IndexHint optionalIndexHint) {

        // NOTE: key stream nums are relative to the outer streams, i.e. 0=first outer, 1=second outer (index stream is implied and not counted).
        // Here the stream offset for key is zero as in a subquery only the outer events are provided in events-per-stream.
        Pair<SubordTableLookupStrategy,EventTableAndNamePair> strategyTablePair = getSubqueryStrategyPair(accessedByStatementName, accessedByStatementId, accessedByStmtAnnotations, eventTypesPerStream, joinDesc, false, fullTableScan, optionalIndexHint, rootView.isEnableIndexShare(), subqueryNum);
        if (strategyTablePair == null || strategyTablePair.getFirst() == null) {
            if (rootView.isQueryPlanLogging() && rootView.getQueryPlanLog().isInfoEnabled()) {
                rootView.getQueryPlanLog().info("shared, full table scan");
            }
            return new SubordFullTableScanLookupStrategyLocking(dataWindowContents, agentInstanceContext.getEpStatementAgentInstanceHandle().getStatementAgentInstanceLock());
        }

        SubordIndexedTableLookupStrategyLocking locking = new SubordIndexedTableLookupStrategyLocking(strategyTablePair.getFirst(), agentInstanceContext.getEpStatementAgentInstanceHandle().getStatementAgentInstanceLock());
        tablePerSingleLookup.put(locking, strategyTablePair.getSecond().getEventTable());

        if (rootView.isQueryPlanLogging() && rootView.getQueryPlanLog().isInfoEnabled()) {
            String prefix = "Subquery " + subqueryNum + " ";
            String indexName = strategyTablePair.getSecond().getIndexName();
            String indexText = indexName != null ? "index " + indexName + " " : "(implicit) ";
            rootView.getQueryPlanLog().info(prefix + "shared index");
            rootView.getQueryPlanLog().info(prefix + "strategy " + strategyTablePair.getFirst().toQueryPlan());
            rootView.getQueryPlanLog().info(prefix + indexText + "table " + strategyTablePair.getSecond().getEventTable().toQueryPlan());

            QueryPlanIndexHook hook = QueryPlanIndexHookUtil.getHook(accessedByStmtAnnotations);
            if (hook != null) {
                hook.subquery(new QueryPlanIndexDescSubquery(strategyTablePair.getSecond().getIndexName(),
                        strategyTablePair.getSecond().getEventTable().getClass().getSimpleName(), subqueryNum));
            }
        }
        return locking;
    }

    public void removeSubqueryLookupStrategy(SubordTableLookupStrategy namedWindowSubqueryLookup) {
        EventTable table = tablePerSingleLookup.remove(namedWindowSubqueryLookup);
        if (table != null) {
            indexRepository.removeTableReference(table);
        }
    }

    private DoubleRange getDoubleRange(boolean reverse, Object start, Object end) {
        if (start == null || end == null) {
            return null;
        }
        double startDbl = ((Number) start).doubleValue();
        double endDbl = ((Number) end).doubleValue();
        if (reverse) {
            return new DoubleRange(startDbl, endDbl);
        }
        else {
            return new DoubleRange(endDbl, startDbl);
        }
    }

    public boolean isVirtualDataWindow() {
        return this.getViews().get(0) instanceof VirtualDWView;
    }

    public VirtualDWView getVirtualDataWindow() {
        if (!isVirtualDataWindow()) {
            return null;
        }
        return (VirtualDWView) this.getViews().get(0);
    }

    public void postLoad() {
        EventBean[] events = new EventBean[1];
        for (EventBean event : dataWindowContents) {
            events[0] = event;
            for (EventTable table : indexRepository.getTables()) {
                table.add(events);
            }
        }
    }
}
