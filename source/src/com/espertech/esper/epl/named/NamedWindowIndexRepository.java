/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.epl.join.hint.*;
import com.espertech.esper.epl.join.plan.QueryPlanIndexItem;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.EventTableAndNamePair;
import com.espertech.esper.epl.join.table.EventTableUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * A repository of index tables for use with a single named window and all it's deleting statements that
 * may use the indexes to correlate triggering events with indexed events of the named window.
 * <p>
 * Maintains index tables and keeps a reference count for user. Allows reuse of indexes for multiple
 * deleting statements.
 */
public class NamedWindowIndexRepository
{
    private static final Log log = LogFactory.getLog(NamedWindowIndexRepository.class);

    private List<EventTable> tables;
    private Map<IndexMultiKey, NamedWindowIndexRepEntry> tableIndexesRefCount;

    /**
     * Ctor.
     */
    public NamedWindowIndexRepository()
    {
        tables = new ArrayList<EventTable>();
        tableIndexesRefCount = new HashMap<IndexMultiKey, NamedWindowIndexRepEntry>();
    }

    public Pair<IndexMultiKey, EventTableAndNamePair> addExplicitIndexOrReuse(
                               boolean unique,
                               List<IndexedPropDesc> hashProps,
                               List<IndexedPropDesc> btreeProps,
                               Iterable<EventBean> prefilledEvents,
                               EventType indexedType,
                               String indexName)
    {
        if (hashProps.isEmpty() && btreeProps.isEmpty()) {
            throw new IllegalArgumentException("Invalid zero element list for hash and btree columns");
        }

        // Get an existing table, if any, matching the exact requirement
        IndexMultiKey indexPropKeyMatch = findExactMatchNameAndType(tableIndexesRefCount.keySet(), unique, hashProps, btreeProps);
        if (indexPropKeyMatch != null) {
            NamedWindowIndexRepEntry refTablePair = tableIndexesRefCount.get(indexPropKeyMatch);
            refTablePair.setRefCount(refTablePair.getRefCount() + 1);
            return new Pair<IndexMultiKey, EventTableAndNamePair>(indexPropKeyMatch, new EventTableAndNamePair(refTablePair.getTable(), refTablePair.getOptionalIndexName()));
        }

        return addIndex(unique, hashProps, btreeProps, prefilledEvents, indexedType, indexName, false);
    }

    public Pair<IndexMultiKey, EventTableAndNamePair> addTableCreateOrReuse(
            List<IndexedPropDesc> hashProps,
            List<IndexedPropDesc> btreeProps,
            Iterable<EventBean> prefilledEvents,
            EventType indexedType,
            IndexHint optionalIndexHint,
            boolean isIndexShare,
            int subqueryNumber,
            Set<String> optionalUniqueKeyProps)
    {
        if (hashProps.isEmpty() && btreeProps.isEmpty()) {
            throw new IllegalArgumentException("Invalid zero element list for hash and btree columns");
        }

        // if there are hints, follow these
        if (optionalIndexHint != null) {
            Map<IndexMultiKey, NamedWindowIndexRepEntry> indexCandidates = findCandidates(hashProps, btreeProps);
            List<IndexHintInstruction> instructions = optionalIndexHint.getInstructionsSubquery(subqueryNumber);
            IndexMultiKey found = handleIndexHint(indexCandidates, instructions);
            if (found != null) {
                return reference(found);
            }
        }

        // Get an existing table, if any, matching the exact requirement, prefer unique
        IndexMultiKey indexPropKeyMatch = findExactMatchNameAndType(tableIndexesRefCount.keySet(), true, hashProps, btreeProps);
        if (indexPropKeyMatch == null) {
            indexPropKeyMatch = findExactMatchNameAndType(tableIndexesRefCount.keySet(), false, hashProps, btreeProps);
        }
        if (indexPropKeyMatch != null) {
            return reference(indexPropKeyMatch);
        }

        // not found as a full match
        // try match on any of the unique indexes
        Map<IndexMultiKey, NamedWindowIndexRepEntry> indexCandidates = findCandidates(hashProps, btreeProps);
        if (!indexCandidates.isEmpty()) {
            for (Map.Entry<IndexMultiKey, NamedWindowIndexRepEntry> indexKey : indexCandidates.entrySet()) {
                if (indexKey.getKey().isUnique()) {
                    return reference(indexKey.getKey());
                }
            }
        }

        // not found, see if the named window is declared unique
        boolean unique = false;
        boolean coerce = !isIndexShare;
        if (optionalUniqueKeyProps != null && !optionalUniqueKeyProps.isEmpty()) {
            List<IndexedPropDesc> newHashProps = new ArrayList<IndexedPropDesc>();
            for (String uniqueKey : optionalUniqueKeyProps) {
                boolean found = false;
                for (IndexedPropDesc hashProp : hashProps) {
                    if (hashProp.getIndexPropName().equals(uniqueKey)) {
                        newHashProps.add(hashProp);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    newHashProps = null;
                    break;
                }
            }
            if (newHashProps != null) {
                hashProps = newHashProps;
                btreeProps = Collections.emptyList();
                unique = true;
                coerce = false;
            }
        }

        // not found at all, create
        return addIndex(unique, hashProps, btreeProps, prefilledEvents, indexedType, null, coerce);
    }

    private IndexMultiKey handleIndexHint(Map<IndexMultiKey, NamedWindowIndexRepEntry> indexCandidates, List<IndexHintInstruction> instructions) {
        for (IndexHintInstruction instruction : instructions) {
            if (instruction instanceof IndexHintInstructionIndexName) {
                String indexName = ((IndexHintInstructionIndexName) instruction).getIndexName();
                IndexMultiKey found = findExplicitIndexByName(indexCandidates, indexName);
                if (found != null) {
                    return found;
                }
            }
            if (instruction instanceof IndexHintInstructionExplicit) {
                IndexMultiKey found = findExplicitIndexAnyName(indexCandidates);
                if (found != null) {
                    return found;
                }
            }
            if (instruction instanceof IndexHintInstructionBust) {
                throw new EPException("Failed to plan index access, index hint busted out");
            }
        }
        return null;
    }

    private Pair<IndexMultiKey, EventTableAndNamePair> reference(IndexMultiKey found) {
        NamedWindowIndexRepEntry refTablePair = tableIndexesRefCount.get(found);
        refTablePair.setRefCount(refTablePair.getRefCount() + 1);
        return new Pair<IndexMultiKey, EventTableAndNamePair>(found, new EventTableAndNamePair(refTablePair.getTable(), refTablePair.getOptionalIndexName()));
    }

    private IndexMultiKey findExplicitIndexByName(Map<IndexMultiKey, NamedWindowIndexRepEntry> indexCandidates, String name) {
        for (Map.Entry<IndexMultiKey, NamedWindowIndexRepEntry> entry : indexCandidates.entrySet()) {
            if (entry.getValue().getOptionalIndexName() != null && entry.getValue().getOptionalIndexName().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private IndexMultiKey findExplicitIndexAnyName(Map<IndexMultiKey, NamedWindowIndexRepEntry> indexCandidates) {
        for (Map.Entry<IndexMultiKey, NamedWindowIndexRepEntry> entry : indexCandidates.entrySet()) {
            if (entry.getValue().getOptionalIndexName() != null) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Map<IndexMultiKey, NamedWindowIndexRepEntry> findCandidates(List<IndexedPropDesc> hashProps, List<IndexedPropDesc> btreeProps) {
        Map<IndexMultiKey, NamedWindowIndexRepEntry> indexCandidates = new HashMap<IndexMultiKey, NamedWindowIndexRepEntry>();
        for (Map.Entry<IndexMultiKey, NamedWindowIndexRepEntry> entry : tableIndexesRefCount.entrySet()) {
            boolean matches = indexMatchesProvided(entry.getKey(), hashProps, btreeProps);
            if (matches) {
                indexCandidates.put(entry.getKey(), entry.getValue());
            }
        }
        return indexCandidates;
    }

    private Pair<IndexMultiKey, EventTableAndNamePair> addIndex(boolean unique, List<IndexedPropDesc> hashProps, List<IndexedPropDesc> btreeProps, Iterable<EventBean> prefilledEvents, EventType indexedType, String indexName, boolean mustCoerce) {

        // not resolved as full match and not resolved as unique index match, allocate
        IndexMultiKey indexPropKey = new IndexMultiKey(unique, hashProps, btreeProps);

        IndexedPropDesc[] indexedPropDescs = hashProps.toArray(new IndexedPropDesc[hashProps.size()]);
        String[] indexProps = IndexedPropDesc.getIndexProperties(indexedPropDescs);
        Class[] indexCoercionTypes = IndexedPropDesc.getCoercionTypes(indexedPropDescs);
        if (!mustCoerce) {
            indexCoercionTypes = null;
        }

        IndexedPropDesc[] rangePropDescs = btreeProps.toArray(new IndexedPropDesc[btreeProps.size()]);
        String[] rangeProps = IndexedPropDesc.getIndexProperties(rangePropDescs);
        Class[] rangeCoercionTypes = IndexedPropDesc.getCoercionTypes(rangePropDescs);

        QueryPlanIndexItem indexItem = new QueryPlanIndexItem(indexProps, indexCoercionTypes, rangeProps, rangeCoercionTypes, false);
        EventTable table = EventTableUtil.buildIndex(0, indexItem, indexedType, true, unique, indexName);

        // fill table since its new
        EventBean[] events = new EventBean[1];
        for (EventBean prefilledEvent : prefilledEvents)
        {
            events[0] = prefilledEvent;
            table.add(events);
        }

        // add table
        tables.add(table);

        // add index, reference counted
        tableIndexesRefCount.put(indexPropKey, new NamedWindowIndexRepEntry(table, indexName, 1));

        return new Pair<IndexMultiKey, EventTableAndNamePair>(indexPropKey, new EventTableAndNamePair(table, indexName));
    }

    private boolean indexMatchesProvided(IndexMultiKey indexDesc, List<IndexedPropDesc> hashPropsProvided, List<IndexedPropDesc> rangePropsProvided) {
        IndexedPropDesc[] hashPropIndexedList = indexDesc.getHashIndexedProps();
        for (IndexedPropDesc hashPropIndexed : hashPropIndexedList) {
            boolean foundHashProp = indexHashIsProvided(hashPropIndexed, hashPropsProvided);
            if (!foundHashProp) {
                return false;
            }
        }

        IndexedPropDesc[] rangePropIndexedList = indexDesc.getRangeIndexedProps();
        for (IndexedPropDesc rangePropIndexed : rangePropIndexedList) {
            boolean foundRangeProp = indexHashIsProvided(rangePropIndexed, rangePropsProvided);
            if (!foundRangeProp) {
                return false;
            }
        }

        return true;
    }

    private boolean indexHashIsProvided(IndexedPropDesc hashPropIndexed, List<IndexedPropDesc> hashPropsProvided) {
        for (IndexedPropDesc hashPropProvided : hashPropsProvided) {
            if (hashPropProvided.getIndexPropName().equals(hashPropIndexed.getIndexPropName())) {
                return true;
            }
        }
        return false;
    }

    private IndexMultiKey findExactMatchNameAndType(Set<IndexMultiKey> indexMultiKeys, boolean unique, List<IndexedPropDesc> hashProps, List<IndexedPropDesc> btreeProps) {
        for (IndexMultiKey existing : indexMultiKeys) {
            if (isExactMatch(existing, unique, hashProps, btreeProps)) {
                return existing;
            }
        }
        return null;
    }

    private boolean isExactMatch(IndexMultiKey existing, boolean unique, List<IndexedPropDesc> hashProps, List<IndexedPropDesc> btreeProps) {
        if (existing.isUnique() != unique) {
            return false;
        }
        boolean keyPropCompare = IndexedPropDesc.compare(Arrays.asList(existing.getHashIndexedProps()), hashProps);
        return keyPropCompare && IndexedPropDesc.compare(Arrays.asList(existing.getRangeIndexedProps()), btreeProps);
    }

    public void addTableReference(EventTable table) {
        for (Map.Entry<IndexMultiKey, NamedWindowIndexRepEntry> entry : tableIndexesRefCount.entrySet())
        {
            if (entry.getValue().getTable() == table)
            {
                int current = entry.getValue().getRefCount() + 1;
                entry.getValue().setRefCount(current);
            }
        }
    }

    /**
     * Remove a reference to an index table, decreasing its reference count.
     * If the table is no longer used, discard it and no longer update events into the index.
     * @param table to remove a reference to
     */
    public void removeTableReference(EventTable table)
    {
        for (Map.Entry<IndexMultiKey, NamedWindowIndexRepEntry> entry : tableIndexesRefCount.entrySet())
        {
            if (entry.getValue().getTable() == table)
            {
                int current = entry.getValue().getRefCount();
                if (current > 1)
                {
                    current--;
                    entry.getValue().setRefCount(current);
                    break;
                }

                tables.remove(table);
                tableIndexesRefCount.remove(entry.getKey());
                break;
            }
        }
    }

    /**
     * Returns a list of current index tables in the repository.
     * @return index tables
     */
    public List<EventTable> getTables()
    {
        return tables;
    }

    /**
     * Destroy indexes.
     */
    public void destroy()
    {
        tables.clear();
        tableIndexesRefCount.clear();
    }

    public Pair<IndexMultiKey, EventTableAndNamePair> findTable(Set<String> keyPropertyNames, Set<String> rangePropertyNames, Map<String, EventTable> explicitIndexNames, IndexHint optionalIndexHint) {

        if (keyPropertyNames.isEmpty() && rangePropertyNames.isEmpty()) {
            return null;
        }

        // determine candidates
        List<IndexedPropDesc> hashProps = new ArrayList<IndexedPropDesc>();
        for (String keyPropertyName : keyPropertyNames) {
            hashProps.add(new IndexedPropDesc(keyPropertyName, null));
        }
        List<IndexedPropDesc> rangeProps = new ArrayList<IndexedPropDesc>();
        for (String rangePropertyName : rangePropertyNames) {
            rangeProps.add(new IndexedPropDesc(rangePropertyName, null));
        }
        Map<IndexMultiKey, NamedWindowIndexRepEntry> indexCandidates = findCandidates(hashProps, rangeProps);

        // handle hint
        if (optionalIndexHint != null) {
            List<IndexHintInstruction> instructions = optionalIndexHint.getInstructionsFireAndForget();
            IndexMultiKey found = handleIndexHint(indexCandidates, instructions);
            if (found != null) {
                return getPair(found);
            }
        }

        // no candidates
        if (indexCandidates == null || indexCandidates.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("No index found.");
            }
            return null;
        }

        // take the table that has a unique index
        for (Map.Entry<IndexMultiKey, NamedWindowIndexRepEntry> entry : indexCandidates.entrySet()) {
            if (entry.getKey().isUnique()) {
                return getPair(entry.getKey());
            }
        }

        // take the best available table
        IndexMultiKey indexMultiKey;
        List<IndexMultiKey> indexes = new ArrayList<IndexMultiKey>(indexCandidates.keySet());
        if (indexes.size() > 1) {
            Comparator<IndexMultiKey> comparator = new Comparator<IndexMultiKey>() {
                public int compare(IndexMultiKey o1, IndexMultiKey o2)
                {
                    String[] indexedProps1 = IndexedPropDesc.getIndexProperties(o1.getHashIndexedProps());
                    String[] indexedProps2 = IndexedPropDesc.getIndexProperties(o2.getHashIndexedProps());
                    if (indexedProps1.length > indexedProps2.length) {
                        return -1;  // sort desc by count columns
                    }
                    if (indexedProps1.length == indexedProps2.length) {
                        return 0;
                    }
                    return 1;
                }
            };
            Collections.sort(indexes,comparator);
        }
        indexMultiKey = indexes.get(0);
        return getPair(indexMultiKey);
    }

    private Pair<IndexMultiKey, EventTableAndNamePair> getPair(IndexMultiKey indexMultiKey) {
        NamedWindowIndexRepEntry indexFound = tableIndexesRefCount.get(indexMultiKey);
        EventTable tableFound = indexFound.getTable();
        return new Pair<IndexMultiKey, EventTableAndNamePair>(indexMultiKey, new EventTableAndNamePair(tableFound, indexFound.getOptionalIndexName()));
    }

    public IndexMultiKey[] getIndexDescriptors() {
        Set<IndexMultiKey> keySet = tableIndexesRefCount.keySet();
        return keySet.toArray(new IndexMultiKey[keySet.size()]);
    }
}
