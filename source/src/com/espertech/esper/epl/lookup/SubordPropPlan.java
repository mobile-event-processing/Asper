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

package com.espertech.esper.epl.lookup;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SubordPropPlan implements Serializable {
    private static final long serialVersionUID = -8857080184548049650L;

    private final Map<String, SubordPropHashKey> hashProps;
    private final Map<String, SubordPropRangeKey> rangeProps;

    public SubordPropPlan() {
        hashProps = Collections.<String, SubordPropHashKey>emptyMap();
        rangeProps = Collections.<String, SubordPropRangeKey>emptyMap();
    }

    public SubordPropPlan(LinkedHashMap<String, SubordPropHashKey> hashProps, LinkedHashMap<String, SubordPropRangeKey> rangeProps) {
        this.hashProps = hashProps;
        this.rangeProps = rangeProps;
    }

    public Map<String, SubordPropRangeKey> getRangeProps() {
        return rangeProps;
    }

    public Map<String, SubordPropHashKey> getHashProps() {
        return hashProps;
    }
}
