/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.spec;

import com.espertech.esper.epl.expression.ExprNode;

import java.io.Serializable;
import java.util.List;

public class ExpressionDeclItem implements Serializable
{
    private static final long serialVersionUID = 1823345580817519502L;

    private final String name;
    private ExprNode inner;
    private List<String> parametersNames;

    public ExpressionDeclItem(String name, List<String> parametersNames, ExprNode inner) {
        this.name = name;
        this.parametersNames = parametersNames;
        this.inner = inner;
    }

    public String getName() {
        return name;
    }

    public ExprNode getInner() {
        return inner;
    }

    public List<String> getParametersNames() {
        return parametersNames;
    }
}


