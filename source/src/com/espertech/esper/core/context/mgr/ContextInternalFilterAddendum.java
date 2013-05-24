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

package com.espertech.esper.core.context.mgr;

import com.espertech.esper.filter.FilterSpecCompiled;
import com.espertech.esper.filter.FilterValueSetParam;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ContextInternalFilterAddendum {
    private final IdentityHashMap<FilterSpecCompiled, List<FilterValueSetParam>> filterAddendum;

    public ContextInternalFilterAddendum() {
        filterAddendum = new IdentityHashMap<FilterSpecCompiled, List<FilterValueSetParam>>();
    }

    public List<FilterValueSetParam> getFilterAddendum(FilterSpecCompiled filterSpecCompiled) {
        return filterAddendum.get(filterSpecCompiled);
    }

    public IdentityHashMap<FilterSpecCompiled, List<FilterValueSetParam>> getFilterAddendum() {
        return filterAddendum;
    }

    public ContextInternalFilterAddendum deepCopy() {
        ContextInternalFilterAddendum copy = new ContextInternalFilterAddendum();
        for (Map.Entry<FilterSpecCompiled, List<FilterValueSetParam>> entry : filterAddendum.entrySet()) {
            List<FilterValueSetParam> copyList = new ArrayList<FilterValueSetParam>(entry.getValue());
            copy.filterAddendum.put(entry.getKey(), copyList);
        }
        return copy;
    }
}
