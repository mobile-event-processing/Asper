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

public class ISupportDImpl implements ISupportD
{
    private String valueD;
    private String valueBaseD;
    private String valueBaseDBase;

    public ISupportDImpl(String valueD, String valueBaseD, String valueBaseDBase)
    {
        this.valueD = valueD;
        this.valueBaseD = valueBaseD;
        this.valueBaseDBase = valueBaseDBase;
    }

    public String getD()
    {
        return valueD;
    }

    public String getBaseD()
    {
        return valueBaseD;
    }

    public String getBaseDBase()
    {
        return valueBaseDBase;
    }
}
