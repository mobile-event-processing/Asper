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

package com.espertech.esper.filter;

import junit.framework.TestCase;

public class TestFilterServiceProvider extends TestCase
{
    public void testGetService()
    {
        FilterService serviceOne = FilterServiceProvider.newService();
        FilterService serviceTwo = FilterServiceProvider.newService();

        assertTrue(serviceOne != null);
        assertTrue(serviceOne != serviceTwo);
    }
}
