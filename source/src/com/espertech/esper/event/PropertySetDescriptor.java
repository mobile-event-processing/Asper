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

package com.espertech.esper.event;

import com.espertech.esper.client.EventPropertyDescriptor;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.FragmentEventType;
import com.espertech.esper.event.map.MapEventPropertyGetter;

import java.util.List;
import java.util.Map;

/**
 * Descriptor of a property set.
 */
public class PropertySetDescriptor
{
    private final List<String> propertyNameList;
    private final List<EventPropertyDescriptor> propertyDescriptors;
    private final Map<String, Class> simplePropertyTypes;
    private final Map<String, EventPropertyGetter> propertyGetters;
    private final Map<String, FragmentEventType> simpleFragmentTypes;
    private final Map<String, Object> nestableTypes;

    /**
     * Ctor.
     * @param propertyNameList property name list
     * @param simplePropertyTypes property types
     * @param propertyDescriptors property descriptors
     * @param propertyGetters property getters
     * @param simpleFragmentTypes fragment types per property
     */
    public PropertySetDescriptor(List<String> propertyNameList, List<EventPropertyDescriptor> propertyDescriptors, Map<String, Class> simplePropertyTypes, Map<String, EventPropertyGetter> propertyGetters, Map<String, FragmentEventType> simpleFragmentTypes, Map<String, Object> nestableTypes)
    {
        this.propertyNameList = propertyNameList;
        this.propertyDescriptors = propertyDescriptors;
        this.simplePropertyTypes = simplePropertyTypes;
        this.propertyGetters = propertyGetters;
        this.simpleFragmentTypes = simpleFragmentTypes;
        this.nestableTypes = nestableTypes;
    }

    /**
     * Returns map of property name and class.
     * @return property name and class
     */
    public Map<String, Class> getSimplePropertyTypes()
    {
        return simplePropertyTypes;
    }

    /**
     * Returns map of property name and getter.
     * @return property name and getter
     */
    public Map<String, EventPropertyGetter> getPropertyGetters()
    {
        return propertyGetters;
    }

    /**
     * Returns property name list.
     * @return property name list
     */
    public List<String> getPropertyNameList()
    {
        return propertyNameList;
    }

    /**
     * Returns the property descriptors.
     * @return property descriptors
     */
    public List<EventPropertyDescriptor> getPropertyDescriptors()
    {
        return propertyDescriptors;
    }

    /**
     * Returns the property fragment types.
     * @return fragment types.
     */
    public Map<String, FragmentEventType> getSimpleFragmentTypes()
    {
        return simpleFragmentTypes;
    }

    public Map<String, Object> getNestableTypes() {
        return nestableTypes;
    }

    public String[] getPropertyNameArray() {
        return propertyNameList.toArray(new String[propertyNameList.size()]);
    }
}
