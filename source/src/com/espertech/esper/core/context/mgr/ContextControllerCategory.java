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
import com.espertech.esper.client.context.ContextPartitionIdentifierCategory;
import com.espertech.esper.client.context.ContextPartitionSelector;
import com.espertech.esper.client.context.ContextPartitionSelectorCategory;
import com.espertech.esper.client.context.ContextPartitionSelectorFiltered;
import com.espertech.esper.core.context.util.ContextControllerSelectorUtil;
import com.espertech.esper.epl.spec.ContextDetailCategoryItem;
import com.espertech.esper.type.NumberSetParameter;

import java.util.*;

public class ContextControllerCategory implements ContextController {

    private final int pathId;
    private final ContextControllerLifecycleCallback activationCallback;
    private final ContextControllerCategoryFactory factory;

    private final Map<Integer, ContextControllerInstanceHandle> handleCategories = new LinkedHashMap<Integer, ContextControllerInstanceHandle>();

    private int currentSubpathId;

    public ContextControllerCategory(int pathId, ContextControllerLifecycleCallback activationCallback, ContextControllerCategoryFactory factory) {
        this.pathId = pathId;
        this.activationCallback = activationCallback;
        this.factory = factory;
    }

    public Collection<Integer> getSelectedContextPartitionPathIds(ContextPartitionSelector contextPartitionSelector) {
        if (contextPartitionSelector instanceof ContextPartitionSelectorFiltered) {
            ContextPartitionSelectorFiltered filter = (ContextPartitionSelectorFiltered) contextPartitionSelector;
            ContextPartitionIdentifierCategory identifier = new ContextPartitionIdentifierCategory();
            List<Integer> accepted = new ArrayList<Integer>();
            for (Map.Entry<Integer, ContextControllerInstanceHandle> entry : handleCategories.entrySet()) {
                identifier.setContextPartitionId(entry.getValue().getContextPartitionOrPathId());
                identifier.setLabel(factory.getCategorySpec().getItems().get(entry.getKey()).getName());
                if (filter.filter(identifier)) {
                    accepted.add(entry.getValue().getContextPartitionOrPathId());
                }
            }
            return accepted;
        }
        if (contextPartitionSelector instanceof ContextPartitionSelectorCategory) {
            ContextPartitionSelectorCategory category = (ContextPartitionSelectorCategory) contextPartitionSelector;
            if (category.getLabels() == null || category.getLabels().isEmpty()) {
                return Collections.emptyList();
            }
            List<Integer> items = new ArrayList<Integer>();
            int count = 0;
            for (ContextDetailCategoryItem item : factory.getCategorySpec().getItems()) {
                if (category.getLabels().contains(item.getName())) {
                    ContextControllerInstanceHandle handle = handleCategories.get(count);
                    if (handle != null) {
                        items.add(handle.getContextPartitionOrPathId());
                    }
                }
                count++;
            }
            return items;
        }
        throw ContextControllerSelectorUtil.getInvalidSelector(new Class[] {ContextPartitionSelectorCategory.class}, contextPartitionSelector);
    }

    public void activate(EventBean optionalTriggeringEvent, Map<String, Object> optionalTriggeringPattern, ContextControllerState controllerState, ContextInternalFilterAddendum activationFilterAddendum) {
        if (factory.getFactoryContext().getNestingLevel() == 1) {
            controllerState = ContextControllerStateUtil.getRecoveryStates(factory.getStateCache(), factory.getFactoryContext().getOutermostContextName());
        }

        if (controllerState == null) {
            int count = 0;
            for (ContextDetailCategoryItem category : factory.getCategorySpec().getItems()) {
                Map<String, Object> context = ContextPropertyEventType.getCategorizedBean(factory.getFactoryContext().getContextName(), 0, category.getName());
                currentSubpathId++;

                // merge filter addendum, if any
                ContextInternalFilterAddendum filterAddendumToUse = activationFilterAddendum;
                if (factory.hasFiltersSpecsNestedContexts()) {
                    filterAddendumToUse = activationFilterAddendum != null ? activationFilterAddendum.deepCopy() : new ContextInternalFilterAddendum();
                    factory.populateContextInternalFilterAddendums(filterAddendumToUse, category.getName());
                }

                ContextControllerInstanceHandle handle = activationCallback.contextPartitionInstantiate(null, currentSubpathId, this, null, null, category.getName(), context, controllerState, filterAddendumToUse, factory.getFactoryContext().isRecoveringResilient());
                handleCategories.put(count, handle);
                count++;

                factory.getStateCache().addContextPath(factory.getFactoryContext().getOutermostContextName(), factory.getFactoryContext().getNestingLevel(), pathId, currentSubpathId, handle.getContextPartitionOrPathId(), null, ContextControllerCategoryFactory.EMPTY_BINDING);
            }
            return;
        }

        TreeMap<ContextStatePathKey, ContextStatePathValue> states = controllerState.getStates();
        NavigableMap<ContextStatePathKey, ContextStatePathValue> childContexts = ContextControllerStateUtil.getChildContexts(factory.getFactoryContext(), pathId, states);

        int maxSubpathId = Integer.MIN_VALUE;
        int count = 0;
        for (Map.Entry<ContextStatePathKey, ContextStatePathValue> entry : childContexts.entrySet()) {

            ContextDetailCategoryItem category = factory.getCategorySpec().getItems().get(count);
            Map<String, Object> context = ContextPropertyEventType.getCategorizedBean(factory.getFactoryContext().getContextName(), 0, category.getName());

            // merge filter addendum, if any
            ContextInternalFilterAddendum filterAddendumToUse = activationFilterAddendum;
            if (factory.hasFiltersSpecsNestedContexts()) {
                filterAddendumToUse = activationFilterAddendum != null ? activationFilterAddendum.deepCopy() : new ContextInternalFilterAddendum();
                factory.populateContextInternalFilterAddendums(filterAddendumToUse, category.getName());
            }

            int contextPartitionId = entry.getValue().getOptionalContextPartitionId();
            ContextControllerInstanceHandle handle = activationCallback.contextPartitionInstantiate(contextPartitionId, entry.getKey().getSubPath(), this, null, null, category.getName(), context, controllerState, filterAddendumToUse, factory.getFactoryContext().isRecoveringResilient());
            handleCategories.put(count, handle);
            count++;

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

    public void deactivate() {
        handleCategories.clear();
    }

    public void setContextPartitionRange(List<NumberSetParameter> partitionRanges) {
        throw new UnsupportedOperationException();
    }
}
