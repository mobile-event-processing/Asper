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

package com.espertech.esper.support.event;

import com.espertech.esper.client.EventType;

import java.util.Map;

public class SupportEventTypeFactory
{
    public static EventType createBeanType(Class clazz, String name)
    {
        return SupportEventAdapterService.getService().addBeanType(name, clazz, false, false, false);
    }

    public static EventType createBeanType(Class clazz)
    {
        return SupportEventAdapterService.getService().addBeanType(clazz.getName(), clazz, false, false, false);
    }

    public static EventType createMapType(Map<String,Object> map)
    {
        return SupportEventAdapterService.getService().createAnonymousMapType("test", map);
    }
}
