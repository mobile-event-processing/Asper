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
import com.espertech.esper.client.context.ContextPartitionIdentifierPartitioned;
import com.espertech.esper.client.context.ContextPartitionSelector;
import com.espertech.esper.client.context.ContextPartitionSelectorFiltered;
import com.espertech.esper.client.context.ContextPartitionSelectorSegmented;
import com.espertech.esper.collection.MultiKeyUntyped;
import com.espertech.esper.core.context.util.ContextControllerSelectorUtil;
import com.espertech.esper.core.context.util.StatementAgentInstanceUtil;
import com.espertech.esper.epl.spec.ContextDetailPartitionItem;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.type.NumberSetParameter;

import java.util.*;

public class ContextControllerPartitioned implements ContextController, ContextControllerPartitionedInstanceCreateCallback {

    protected final int pathId;
    protected final ContextControllerLifecycleCallback activationCallback;
    protected final ContextControllerPartitionedFactory factory;

    protected final List<ContextControllerPartitionedFilterCallback> filterCallbacks = new ArrayList<ContextControllerPartitionedFilterCallback>();
    protected final HashMap<Object, ContextControllerInstanceHandle> partitionKeys = new HashMap<Object, ContextControllerInstanceHandle>();

    private ContextInternalFilterAddendum activationFilterAddendum;
    protected int currentSubpathId;

    public ContextControllerPartitioned(int pathId, ContextControllerLifecycleCallback activationCallback, ContextControllerPartitionedFactory factory) {
        this.pathId = pathId;
        this.activationCallback = activationCallback;
        this.factory = factory;
    }

    public Collection<Integer> getSelectedContextPartitionPathIds(ContextPartitionSelector contextPartitionSelector) {
        boolean isMultiKey = factory.getContextDetailPartitionItems().get(0).getPropertyNames().size() > 1;
        if (contextPartitionSelector instanceof ContextPartitionSelectorFiltered) {
            List<Integer> ids = new ArrayList<Integer>();
            ContextPartitionSelectorFiltered filtered = (ContextPartitionSelectorFiltered) contextPartitionSelector;

            ContextPartitionIdentifierPartitioned identifier = new ContextPartitionIdentifierPartitioned();
            for (Map.Entry<Object, ContextControllerInstanceHandle> entry : partitionKeys.entrySet()) {
                identifier.setContextPartitionId(entry.getValue().getContextPartitionOrPathId());
                if (isMultiKey) {
                    identifier.setKeys(((MultiKeyUntyped)entry.getKey()).getKeys());
                }
                else {
                    identifier.setKeys(new Object[] {entry.getKey()});
                }

                if (filtered.filter(identifier)) {
                    ids.add(entry.getValue().getContextPartitionOrPathId());
                }
            }
            return ids;
        }
        else if (contextPartitionSelector instanceof ContextPartitionSelectorSegmented) {
            ContextPartitionSelectorSegmented partitioned = (ContextPartitionSelectorSegmented) contextPartitionSelector;
            if (partitioned.getPartitionKeys() == null || partitioned.getPartitionKeys().isEmpty()) {
                return Collections.emptyList();
            }
            Set<Integer> ids = new HashSet<Integer>();
            for (Object[] keyObjects : partitioned.getPartitionKeys()) {

                Object key;
                if (isMultiKey) {
                    key = new MultiKeyUntyped(keyObjects);
                }
                else {
                    key = keyObjects[0];
                }
                ContextControllerInstanceHandle instanceHandle = partitionKeys.get(key);
                if (instanceHandle != null && instanceHandle.getContextPartitionOrPathId() != null) {
                    ids.add(instanceHandle.getContextPartitionOrPathId());
                }
            }
            return ids;
        }
        throw ContextControllerSelectorUtil.getInvalidSelector(new Class[]{ContextPartitionSelectorSegmented.class}, contextPartitionSelector);
    }

    public void activate(EventBean optionalTriggeringEvent, Map<String, Object> optionalTriggeringPattern, ContextControllerState controllerState, ContextInternalFilterAddendum filterAddendum) {
        ContextControllerFactoryContext factoryContext = factory.getFactoryContext();
        this.activationFilterAddendum = filterAddendum;

        for (ContextDetailPartitionItem item : factory.getSegmentedSpec().getItems()) {
            ContextControllerPartitionedFilterCallback callback = new ContextControllerPartitionedFilterCallback(factoryContext.getServicesContext(), factoryContext.getAgentInstanceContextCreate(), item, this, filterAddendum);
            filterCallbacks.add(callback);

            if (optionalTriggeringEvent != null) {
                boolean match = StatementAgentInstanceUtil.evaluateFilterForStatement(factoryContext.getServicesContext(), optionalTriggeringEvent, factoryContext.getAgentInstanceContextCreate(), callback.getFilterHandle());

                if (match) {
                    callback.matchFound(optionalTriggeringEvent, null);
                }
            }
        }

        if (factoryContext.getNestingLevel() == 1) {
            controllerState = ContextControllerStateUtil.getRecoveryStates(factory.getStateCache(), factoryContext.getOutermostContextName());
        }
        if (controllerState == null) {
            return;
        }
        TreeMap<ContextStatePathKey, ContextStatePathValue> states = controllerState.getStates();

        // restart if there are states
        int maxSubpathId = Integer.MIN_VALUE;
        ContextStatePathKey start = new ContextStatePathKey(factoryContext.getOutermostContextName(), factoryContext.getNestingLevel(), pathId, Integer.MIN_VALUE);
        ContextStatePathKey end = new ContextStatePathKey(factoryContext.getOutermostContextName(), factoryContext.getNestingLevel(), pathId, Integer.MAX_VALUE);
        NavigableMap<ContextStatePathKey, ContextStatePathValue> childContexts = states.subMap(start, true, end, true);
        EventAdapterService eventAdapterService = factory.getFactoryContext().getServicesContext().getEventAdapterService();

        for (Map.Entry<ContextStatePathKey, ContextStatePathValue> entry : childContexts.entrySet()) {
            String key = (String) factory.getBinding().byteArrayToObject(entry.getValue().getBlob(), eventAdapterService);
            Map<String, Object> props = ContextPropertyEventType.getPartitionBean(factoryContext.getContextName(), 0, key, factory.getSegmentedSpec().getItems().get(0).getPropertyNames());

            // merge filter addendum, if any
            ContextInternalFilterAddendum myFilterAddendum = activationFilterAddendum;
            if (factory.hasFiltersSpecsNestedContexts()) {
                filterAddendum = activationFilterAddendum != null ? activationFilterAddendum.deepCopy() : new ContextInternalFilterAddendum();
                factory.populateContextInternalFilterAddendums(filterAddendum, key);
            }

            ContextControllerInstanceHandle handle = activationCallback.contextPartitionInstantiate(entry.getValue().getOptionalContextPartitionId(), entry.getKey().getSubPath(), this, optionalTriggeringEvent, optionalTriggeringPattern, key, props, controllerState, myFilterAddendum, factoryContext.isRecoveringResilient());
            partitionKeys.put(key, handle);

            int subPathId = entry.getKey().getSubPath();
            if (entry.getKey().getSubPath() > maxSubpathId) {
                maxSubpathId = subPathId;
            }
        }
        currentSubpathId = maxSubpathId != Integer.MIN_VALUE ? maxSubpathId : 0;
    }

    public ContextControllerFactory getFactory() {
        return factory;
    }

    public int getPathId() {
        return pathId;
    }

    public void setContextPartitionRange(List<NumberSetParameter> partitionRanges) {
        throw new UnsupportedOperationException();
    }

    public synchronized void deactivate() {
        ContextControllerFactoryContext factoryContext = factory.getFactoryContext();
        for (ContextControllerPartitionedFilterCallback callback : filterCallbacks) {
            callback.destroy(factoryContext.getServicesContext().getFilterService());
        }
        partitionKeys.clear();
        filterCallbacks.clear();
        factory.getStateCache().removeContextParentPath(factoryContext.getOutermostContextName(), factoryContext.getNestingLevel(), pathId);
    }

    public synchronized void create(Object key, EventBean theEvent) {
        boolean exists = partitionKeys.containsKey(key);
        if (exists) {
            return;
        }

        currentSubpathId++;

        // determine properties available for querying
        ContextControllerFactoryContext factoryContext = factory.getFactoryContext();
        Map<String, Object> props = ContextPropertyEventType.getPartitionBean(factoryContext.getContextName(), 0, key, factory.getSegmentedSpec().getItems().get(0).getPropertyNames());

        // merge filter addendum, if any
        ContextInternalFilterAddendum filterAddendum = activationFilterAddendum;
        if (factory.hasFiltersSpecsNestedContexts()) {
            filterAddendum = activationFilterAddendum != null ? activationFilterAddendum.deepCopy() : new ContextInternalFilterAddendum();
            factory.populateContextInternalFilterAddendums(filterAddendum, key);
        }

        ContextControllerInstanceHandle handle = activationCallback.contextPartitionInstantiate(null, currentSubpathId, this, theEvent, null, key, props, null, filterAddendum, false);

        partitionKeys.put(key, handle);

        factory.getStateCache().addContextPath(factoryContext.getOutermostContextName(), factoryContext.getNestingLevel(), pathId, currentSubpathId, handle.getContextPartitionOrPathId(), key, factory.getBinding());
    }
}
