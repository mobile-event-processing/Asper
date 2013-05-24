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

package com.espertech.esper.epl.join.plan;

public class CoercionDesc {

    private boolean coerce;
    private Class[] coercionTypes;

    public CoercionDesc(boolean coerce, Class[] coercionTypes) {
        this.coerce = coerce;
        this.coercionTypes = coercionTypes;
    }

    public boolean isCoerce() {
        return coerce;
    }

    public Class[] getCoercionTypes() {
        return coercionTypes;
    }
}
