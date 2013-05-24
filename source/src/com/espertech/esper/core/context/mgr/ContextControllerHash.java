/*
 * *************************************************************************************
 *  Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 *  http://esper.codehaus.org                                                          *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 * *************************************************************************************
 */

package com.espertech.esper.core.context.mgr;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.context.ContextPartitionIdentifierHash;
import com.espertech.esper.client.context.ContextPartitionSelector;
import com.espertech.esper.client.context.ContextPartitionSelectorFiltered;
import com.espertech.esper.client.context.ContextPartitionSelectorHash;
import com.espertech.esper.core.context.util.ContextControllerSelectorUtil;
import com.espertech.esper.core.context.util.StatementAgentInstanceUtil;
import com.espertech.esper.epl.spec.ContextDetailHashItem;
import com.espertech.esper.type.NumberSetParameter;

import java.util.*;

public class ContextControllerHash implements ContextController, ContextControllerHashedInstanceCallback {

    protected final int pathId;
    protected final ContextControllerLifecycleCallback activationCallback;
    protected final ContextControllerHashFactory factory;

    protected final List<ContextControllerHashedFilterCallback> filterCallbacks = new ArrayList<ContextControllerHashedFilterCallback>();
    protected final Map<Integer, ContextControllerInstanceHandle> partitionKeys = new LinkedHashMap<Integer, ContextControllerInstanceHandle>();

    protected ContextInternalFilterAddendum activationFilterAddendum;
    protected int currentSubpathId;
    protected List<NumberSetParameter> optionalPartitionRanges;

    public ContextControllerHash(int pathId, ContextControllerLifecycleCallback activationCallback, ContextControllerHashFactory factory) {
        this.pathId = pathId;
        this.activationCallback = activationCallback;
        this.factory = factory;
    }

    public Collection<Integer> getSelectedContextPartitionPathIds(ContextPartitionSelector contextPartitionSelector) {
        if (contextPartitionSelector instanceof ContextPartitionSelectorHash) {
            ContextPartitionSelectorHash hash = (ContextPartitionSelectorHash) contextPartitionSelector;
            if (hash.getHashes() == null || hash.getHashes().isEmpty()) {
                return Collections.emptyList();
            }
            if (hash.getHashes().size() == 1) {
                return Collections.singleton(hash.getHashes().iterator().next());
            }
            return new ArrayList<Integer>(hash.getHashes());
        }
        if (contextPartitionSelector instanceof ContextPartitionSelectorFiltered) {
            ContextPartitionSelectorFiltered filter = (ContextPartitionSelectorFiltered) contextPartitionSelector;
            ContextPartitionIdentifierHash identifierHash = new ContextPartitionIdentifierHash();
            List<Integer> accepted = new ArrayList<Integer>();
            for (Map.Entry<Integer, ContextControllerInstanceHandle> entry : partitionKeys.entrySet()) {
                identifierHash.setHash(entry.getKey());
                identifierHash.setContextPartitionId(entry.getValue().getContextPartitionOrPathId());
                if (filter.filter(identifierHash)) {
                    accepted.add(entry.getValue().getContextPartitionOrPathId());
                }
            }
            return accepted;
        }
        throw ContextControllerSelectorUtil.getInvalidSelector(new Class[]{ContextPartitionSelectorHash.class}, contextPartitionSelector);
    }

    public void activate(EventBean optionalTriggeringEvent, Map<String, Object> optionalTriggeringPattern, ContextControllerState controllerState, ContextInternalFilterAddendum activationFilterAddendum) {
        ContextControllerFactoryContext factoryContext = factory.getFactoryContext();
        this.activationFilterAddendum = activationFilterAddendum;

        if (factoryContext.getNestingLevel() == 1) {
            controllerState = ContextControllerStateUtil.getRecoveryStates(factory.getStateCache(), factoryContext.getOutermostContextName());
        }
        if (controllerState == null) {

            // handle preallocate
            if (factory.getHashedSpec().isPreallocate()) {
                for (int i = 0; i < factory.getHashedSpec().getGranularity(); i++) {
                    Map<String, Object> properties = ContextPropertyEventType.getHashBean(factoryContext.getContextName(), i);
                    currentSubpathId++;

                    // merge filter addendum, if any
                    ContextInternalFilterAddendum filterAddendumToUse = activationFilterAddendum;
                    if (factory.hasFiltersSpecsNestedContexts()) {
                        filterAddendumToUse = activationFilterAddendum != null ? activationFilterAddendum.deepCopy() : new ContextInternalFilterAddendum();
                        factory.populateContextInternalFilterAddendums(filterAddendumToUse, i);
                    }

                    ContextControllerInstanceHandle handle = activationCallback.contextPartitionInstantiate(null, currentSubpathId, this, optionalTriggeringEvent, null, i, properties, controllerState, filterAddendumToUse, factory.getFactoryContext().isRecoveringResilient());
                    partitionKeys.put(i, handle);

                    factory.getStateCache().addContextPath(factory.getFactoryContext().getOutermostContextName(), factory.getFactoryContext().getNestingLevel(), pathId, currentSubpathId, handle.getContextPartitionOrPathId(), i, factory.getBinding());
                }
                return;
            }

            // start filters if not preallocated
            activateFilters(optionalTriggeringEvent);

            return;
        }

        // get state
        TreeMap<ContextStatePathKey, ContextStatePathValue> states = controllerState.getStates();
        NavigableMap<ContextStatePathKey, ContextStatePathValue> childContexts = ContextControllerStateUtil.getChildContexts(factoryContext, pathId, states);

        int maxSubpathId = Integer.MIN_VALUE;
        for (Map.Entry<ContextStatePathKey, ContextStatePathValue> entry : childContexts.entrySet()) {

            Integer hashAlgoGeneratedId = (Integer) factory.getBinding().byteArrayToObject(entry.getValue().getBlob(), null);
            Map<String, Object> properties = ContextPropertyEventType.getHashBean(factoryContext.getContextName(), hashAlgoGeneratedId);

            // merge filter addendum, if any
            ContextInternalFilterAddendum filterAddendumToUse = activationFilterAddendum;
            if (factory.hasFiltersSpecsNestedContexts()) {
                filterAddendumToUse = activationFilterAddendum != null ? activationFilterAddendum.deepCopy() : new ContextInternalFilterAddendum();
                factory.populateContextInternalFilterAddendums(filterAddendumToUse, hashAlgoGeneratedId);
            }

            ContextControllerInstanceHandle handle = activationCallback.contextPartitionInstantiate(entry.getValue().getOptionalContextPartitionId(), entry.getKey().getSubPath(), this, optionalTriggeringEvent, optionalTriggeringPattern, hashAlgoGeneratedId, properties, controllerState, filterAddendumToUse, factoryContext.isRecoveringResilient());
            partitionKeys.put(hashAlgoGeneratedId, handle);

            int subPathId = entry.getKey().getSubPath();
            if (entry.getKey().getSubPath() > maxSubpathId) {
                maxSubpathId = subPathId;
            }
        }
        currentSubpathId = maxSubpathId != Integer.MIN_VALUE ? maxSubpathId : 0;

        // activate filters
        if (!factory.getHashedSpec().isPreallocate()) {
            activateFilters(null);
        }
    }

    protected void activateFilters(EventBean optionalTriggeringEvent) {
        ContextControllerFactoryContext factoryContext = factory.getFactoryContext();
        for (ContextDetailHashItem item : factory.getHashedSpec().getItems()) {
            ContextControllerHashedFilterCallback callback = new ContextControllerHashedFilterCallback(factoryContext.getServicesContext(), factoryContext.getAgentInstanceContextCreate(), item, this, activationFilterAddendum);
            filterCallbacks.add(callback);

            if (optionalTriggeringEvent != null) {
                boolean match = StatementAgentInstanceUtil.evaluateFilterForStatement(factoryContext.getServicesContext(), optionalTriggeringEvent, factoryContext.getAgentInstanceContextCreate(), callback.getFilterHandle());

                if (match) {
                    callback.matchFound(optionalTriggeringEvent, null);
                }
            }
        }
    }

    public void setContextPartitionRange(List<NumberSetParameter> partitionRanges) {
        optionalPartitionRanges = partitionRanges;
    }

    public synchronized void create(int id, EventBean theEvent) {
        ContextControllerFactoryContext factoryContext = factory.getFactoryContext();
        if (partitionKeys.containsKey(id)) {
            return;
        }

        // check if the partition range falls within the responsibility as assign, if any
        if (optionalPartitionRanges != null) {
            boolean pass = false;
            for (NumberSetParameter param : optionalPartitionRanges) {
                if (param.containsPoint(id)) {
                    pass = true;
                    break;
                }
            }
            if (!pass) {
                return;
            }
        }
        
        Map<String, Object> properties = ContextPropertyEventType.getHashBean(factoryContext.getContextName(), id);
        currentSubpathId++;

        // merge filter addendum, if any
        ContextInternalFilterAddendum filterAddendumToUse = activationFilterAddendum;
        if (factory.hasFiltersSpecsNestedContexts()) {
            filterAddendumToUse = activationFilterAddendum != null ? activationFilterAddendum.deepCopy() : new ContextInternalFilterAddendum();
            factory.populateContextInternalFilterAddendums(filterAddendumToUse, id);
        }

        ContextControllerInstanceHandle handle = activationCallback.contextPartitionInstantiate(null, currentSubpathId, this, theEvent, null, id, properties, null, filterAddendumToUse, factory.getFactoryContext().isRecoveringResilient());
        partitionKeys.put(id, handle);
        factory.getStateCache().addContextPath(factoryContext.getOutermostContextName(), factoryContext.getNestingLevel(), pathId, currentSubpathId, handle.getContextPartitionOrPathId(), id, factory.getBinding());
    }

    public ContextControllerFactory getFactory() {
        return factory;
    }

    public int getPathId() {
        return pathId;
    }

    public void deactivate() {
        ContextControllerFactoryContext factoryContext = factory.getFactoryContext();
        for (ContextControllerHashedFilterCallback callback : filterCallbacks) {
            callback.destroy(factoryContext.getServicesContext().getFilterService());
        }
        partitionKeys.clear();
        filterCallbacks.clear();
        factory.getStateCache().removeContextParentPath(factoryContext.getOutermostContextName(), factoryContext.getNestingLevel(), pathId);
    }
}
