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

package com.espertech.esper.event.bean;

import com.asper.sources.net.sf.cglib.reflect.FastMethod;
import com.espertech.esper.client.EventBean;

public class BeanEventPropertyWriterIndexedProp extends BeanEventPropertyWriter {

    private final int index;

    public BeanEventPropertyWriterIndexedProp(Class clazz, FastMethod writerMethod, int index) {
        super(clazz, writerMethod);
        this.index = index;
    }

    public void write(Object value, EventBean target) {
        super.invoke(new Object[] {index, value}, target.getUnderlying());
    }
}
