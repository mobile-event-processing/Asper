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

public class SupportBeanRendererTwo
{
    private String stringVal;
    private SupportEnum enumValue;

    public SupportBeanRendererTwo()
    {
    }

    public SupportEnum getEnumValue()
    {
        return enumValue;
    }

    public void setEnumValue(SupportEnum enumValue)
    {
        this.enumValue = enumValue;
    }

    public String getStringVal()
    {
        return stringVal;
    }

    public void setStringVal(String stringVal)
    {
        this.stringVal = stringVal;
    }
}
