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

package com.espertech.esper.support.bean;

public final class SupportBeanFinal
{
    private final int intPrimitive;

    public SupportBeanFinal(int intPrimitive)
    {
        this.intPrimitive = intPrimitive;
    }

    public final int getIntPrimitive()
    {
        return intPrimitive;
    }
}
