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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SupportBeanStaticOuter
{
    private SupportBeanStaticInner inside;

    public SupportBeanStaticOuter() {
        this.inside = new SupportBeanStaticInner();
    }

    public SupportBeanStaticInner getInside() {
        return inside;
    }

    public void setInside(SupportBeanStaticInner inside) {
        this.inside = inside;
    }
}
