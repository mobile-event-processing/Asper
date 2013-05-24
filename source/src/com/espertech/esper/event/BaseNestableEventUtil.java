/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.PropertyAccessException;
import com.espertech.esper.event.arr.ObjectArrayEventPropertyGetter;
import com.espertech.esper.event.bean.BeanEventPropertyGetter;
import com.espertech.esper.event.bean.BeanEventType;
import com.espertech.esper.event.map.MapEventPropertyGetter;
import com.espertech.esper.event.map.MapEventType;
import com.espertech.esper.event.property.IndexedProperty;
import com.espertech.esper.event.property.MappedProperty;
import com.espertech.esper.event.property.Property;
import com.espertech.esper.event.property.PropertyParser;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BaseNestableEventUtil
{
    public static Map<String, Object> checkedCastUnderlyingMap(EventBean theEvent) throws PropertyAccessException {
        return (Map<String, Object>) theEvent.getUnderlying();
    }
    
    public static Object[] checkedCastUnderlyingObjectArray(EventBean theEvent) throws PropertyAccessException {
        return (Object[]) theEvent.getUnderlying();
    }

    public static Object handleNestedValueArrayWithMap(Object value, int index, MapEventPropertyGetter getter) {
        if (!value.getClass().isArray())
        {
            return null;
        }
        if (Array.getLength(value) <= index)
        {
            return null;
        }
        Object valueMap = Array.get(value, index);
        if (!(valueMap instanceof Map))
        {
            if (value instanceof EventBean) {
                return getter.get((EventBean) value);
            }
            return null;
        }
        return getter.getMap((Map<String, Object>) valueMap);
    }

    public static Object handleNestedValueArrayWithMapFragment(Object value, int index, MapEventPropertyGetter getter, EventAdapterService eventAdapterService, EventType fragmentType) {
        if (!value.getClass().isArray())
        {
            return null;
        }
        if (Array.getLength(value) <= index)
        {
            return null;
        }
        Object valueMap = Array.get(value, index);
        if (!(valueMap instanceof Map))
        {
            if (value instanceof EventBean) {
                return getter.getFragment((EventBean) value);
            }
            return null;
        }

        // If the map does not contain the key, this is allowed and represented as null
        EventBean eventBean = eventAdapterService.adapterForTypedMap((Map<String, Object>) valueMap, fragmentType);
        return getter.getFragment(eventBean);
    }

    public static Object handleNestedValueArrayWithObjectArray(Object value, int index, ObjectArrayEventPropertyGetter getter) {
        if (!value.getClass().isArray())
        {
            return null;
        }
        if (Array.getLength(value) <= index)
        {
            return null;
        }
        Object valueArray = Array.get(value, index);
        if (!(valueArray instanceof Object[]))
        {
            if (value instanceof EventBean) {
                return getter.get((EventBean) value);
            }
            return null;
        }
        return getter.getObjectArray((Object[]) valueArray);
    }

    public static Object handleNestedValueArrayWithObjectArrayFragment(Object value, int index, ObjectArrayEventPropertyGetter getter, EventType fragmentType, EventAdapterService eventAdapterService) {
        if (!value.getClass().isArray())
        {
            return null;
        }
        if (Array.getLength(value) <= index)
        {
            return null;
        }
        Object valueArray = Array.get(value, index);
        if (!(valueArray instanceof Object[]))
        {
            if (value instanceof EventBean) {
                return getter.getFragment((EventBean) value);
            }
            return null;
        }

        // If the map does not contain the key, this is allowed and represented as null
        EventBean eventBean = eventAdapterService.adapterForTypedObjectArray((Object[]) valueArray, fragmentType);
        return getter.getFragment(eventBean);
    }

    public static Object handleCreateFragmentMap(Object value, EventType fragmentEventType, EventAdapterService eventAdapterService) {
        if (!(value instanceof Map))
        {
            if (value instanceof EventBean) {
                return value;
            }
            return null;
        }
        Map subEvent = (Map) value;
        return eventAdapterService.adapterForTypedMap(subEvent, fragmentEventType);
    }

    public static Object handleCreateFragmentObjectArray(Object value, EventType fragmentEventType, EventAdapterService eventAdapterService) {
        if (!(value instanceof Object[]))
        {
            if (value instanceof EventBean) {
                return value;
            }
            return null;
        }
        Object[] subEvent = (Object[]) value;
        return eventAdapterService.adapterForTypedObjectArray(subEvent, fragmentEventType);
    }

    public static Object getMappedPropertyValue(Object value, String key) {
        if (value == null)
        {
            return null;
        }
        if (!(value instanceof Map))
        {
            return null;
        }
        Map innerMap = (Map) value;
        return innerMap.get(key);
    }

    public static boolean getMappedPropertyExists(Object value, String key) {
        if (value == null)
        {
            return false;
        }
        if (!(value instanceof Map))
        {
            return false;
        }
        Map innerMap = (Map) value;
        return innerMap.containsKey(key);
    }

    public static MapIndexedPropPair getIndexedAndMappedProps(String[] properties) {
        Set<String> mapPropertiesToCopy = new HashSet<String>();
        Set<String> arrayPropertiesToCopy = new HashSet<String>();
        for (int i = 0; i < properties.length; i++) {
            Property prop = PropertyParser.parse(properties[i], false);
            if (prop instanceof MappedProperty) {
                MappedProperty mappedProperty = (MappedProperty) prop;
                mapPropertiesToCopy.add(mappedProperty.getPropertyNameAtomic());
            }
            if (prop instanceof IndexedProperty) {
                IndexedProperty indexedProperty = (IndexedProperty) prop;
                arrayPropertiesToCopy.add(indexedProperty.getPropertyNameAtomic());
            }
        }
        return new MapIndexedPropPair(mapPropertiesToCopy, arrayPropertiesToCopy);
    }

    public static Object getIndexedValue(Object value, int index) {
        if (value == null)
        {
            return null;
        }
        if (!value.getClass().isArray())
        {
            return null;
        }
        if (index >= Array.getLength(value))
        {
            return null;
        }
        return Array.get(value, index);
    }

    public static boolean isExistsIndexedValue(Object value, int index) {
        if (value == null)
        {
            return false;
        }
        if (!value.getClass().isArray())
        {
            return false;
        }
        if (index >= Array.getLength(value))
        {
            return false;
        }
        return true;
    }

    public static EventBean getFragmentNonPojo(EventAdapterService eventAdapterService, Object fragmentUnderlying, EventType fragmentEventType) {
        if (fragmentUnderlying == null) {
            return null;
        }
        if (fragmentEventType instanceof MapEventType) {
            return eventAdapterService.adapterForTypedMap((Map<String, Object>) fragmentUnderlying, fragmentEventType);
        }
        return eventAdapterService.adapterForTypedObjectArray((Object[]) fragmentUnderlying, fragmentEventType);
    }

    public static Object getFragmentArray(EventAdapterService eventAdapterService, Object value, EventType fragmentEventType) {
        if (value instanceof Object[]) {
            Object[] subEvents = (Object[]) value;

            int countNull = 0;
            for (Object subEvent : subEvents)
            {
                if (subEvent != null)
                {
                    countNull++;
                }
            }

            EventBean[] outEvents = new EventBean[countNull];
            int count = 0;
            for (Object item : subEvents)
            {
                if (item != null)
                {
                    outEvents[count++] = BaseNestableEventUtil.getFragmentNonPojo(eventAdapterService, item, fragmentEventType);
                }
            }

            return outEvents;
        }

        if (!(value instanceof Map[]))
        {
            return null;
        }
        Map[] mapTypedSubEvents = (Map[]) value;

        int countNull = 0;
        for (Map map : mapTypedSubEvents)
        {
            if (map != null)
            {
                countNull++;
            }
        }

        EventBean[] mapEvents = new EventBean[countNull];
        int count = 0;
        for (Map map : mapTypedSubEvents)
        {
            if (map != null)
            {
                mapEvents[count++] = eventAdapterService.adapterForTypedMap(map, fragmentEventType);
            }
        }

        return mapEvents;
    }

    public static Object getBeanArrayValue(BeanEventPropertyGetter nestedGetter, Object value, int index) {

        if (value == null)
        {
            return null;
        }
        if (!value.getClass().isArray())
        {
            return null;
        }
        if (Array.getLength(value) <= index)
        {
            return null;
        }
        Object arrayItem = Array.get(value, index);
        if (arrayItem == null)
        {
            return null;
        }

        return nestedGetter.getBeanProp(arrayItem);
    }

    public static Object getFragmentPojo(Object result, BeanEventType eventType, EventAdapterService eventAdapterService) {
        if (result == null)
        {
            return null;
        }
        if (result.getClass().isArray())
        {
            int len = Array.getLength(result);
            EventBean[] events = new EventBean[len];
            for (int i = 0; i < events.length; i++) {
                events[i] = eventAdapterService.adapterForTypedBean(Array.get(result, i), eventType);
            }
            return events;
        }
        return eventAdapterService.adapterForTypedBean(result, eventType);
    }

    public static Object getArrayPropertyValue(EventBean[] wrapper, int index, EventPropertyGetter nestedGetter) {
        if (wrapper == null)
        {
            return null;
        }
        if (wrapper.length <= index)
        {
            return null;
        }
        EventBean innerArrayEvent = wrapper[index];
        return nestedGetter.get(innerArrayEvent);
    }

    public static Object getArrayPropertyFragment(EventBean[] wrapper, int index, EventPropertyGetter nestedGetter) {
        if (wrapper == null)
        {
            return null;
        }
        if (wrapper.length <= index)
        {
            return null;
        }
        EventBean innerArrayEvent = wrapper[index];
        return nestedGetter.getFragment(innerArrayEvent);
    }

    public static Object getArrayPropertyUnderlying(EventBean[] wrapper, int index) {
        if (wrapper == null)
        {
            return null;
        }
        if (wrapper.length <= index)
        {
            return null;
        }

        return wrapper[index].getUnderlying();
    }

    public static Object getArrayPropertyBean(EventBean[] wrapper, int index) {
        if (wrapper == null)
        {
            return null;
        }
        if (wrapper.length <= index)
        {
            return null;
        }

        return wrapper[index];
    }

    public static Object getArrayPropertyAsUnderlyingsArray(Class underlyingType, EventBean[] wrapper) {
        if (wrapper !=  null)
        {
            Object array = Array.newInstance(underlyingType, wrapper.length);
            for (int i = 0; i < wrapper.length; i++)
            {
                Array.set(array, i, wrapper[i].getUnderlying());
            }
            return array;
        }

        return null;
    }

    public static class MapIndexedPropPair {
        private final Set<String> mapProperties;
        private final Set<String> arrayProperties;

        public MapIndexedPropPair(Set<String> mapProperties, Set<String> arrayProperties) {
            this.mapProperties = mapProperties;
            this.arrayProperties = arrayProperties;
        }

        public Set<String> getMapProperties() {
            return mapProperties;
        }

        public Set<String> getArrayProperties() {
            return arrayProperties;
        }
    }
}
