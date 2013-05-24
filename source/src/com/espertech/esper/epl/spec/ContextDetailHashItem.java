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

package com.espertech.esper.epl.spec;

import com.espertech.esper.epl.expression.ExprChainedSpec;
import com.espertech.esper.filter.FilterSpecCompiled;
import com.espertech.esper.filter.FilterSpecLookupable;
import com.espertech.esper.filter.FilterValueSetParam;

import java.io.Serializable;
import java.util.ArrayDeque;

public class ContextDetailHashItem implements Serializable {

    private static final long serialVersionUID = -1311534513012512587L;
    private final ExprChainedSpec function;
    private final FilterSpecRaw filterSpecRaw;

    private transient ArrayDeque<FilterValueSetParam> parametersCompiled;
    private transient FilterSpecCompiled filterSpecCompiled;
    private FilterSpecLookupable lookupable;

    public ContextDetailHashItem(ExprChainedSpec function, FilterSpecRaw filterSpecRaw) {
        this.function = function;
        this.filterSpecRaw = filterSpecRaw;
    }

    public ExprChainedSpec getFunction() {
        return function;
    }

    public FilterSpecRaw getFilterSpecRaw() {
        return filterSpecRaw;
    }

    public FilterSpecCompiled getFilterSpecCompiled() {
        return filterSpecCompiled;
    }

    public void setFilterSpecCompiled(FilterSpecCompiled filterSpecCompiled) {
        this.filterSpecCompiled = filterSpecCompiled;
        this.parametersCompiled = filterSpecCompiled.getValueSet(null, null, null).getParameters();
    }

    public ArrayDeque<FilterValueSetParam> getParametersCompiled() {
        return parametersCompiled;
    }

    public FilterSpecLookupable getLookupable() {
        return lookupable;
    }

    public void setLookupable(FilterSpecLookupable lookupable) {
        this.lookupable = lookupable;
    }
}
