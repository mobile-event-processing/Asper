/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.util.SimpleNumberCoercer;
import com.espertech.esper.util.SimpleNumberCoercerFactory;
import com.espertech.esper.util.CoercionException;

import java.util.Map;

/**
 * Represents an equals (=, !=, <>, is, is not) comparator in a filter expressiun tree.
 */
public interface ExprEqualsNode extends ExprNode
{
    /**
     * Returns true if this is a NOT EQUALS node, false if this is a EQUALS node.
     * @return true for !=, false for =
     */
    public boolean isNotEquals();

    /**
     * Returns true if this is a "IS" or "IS NOT" node, false if this is a EQUALS or NOT EQUALS node.
     * @return true for !=, false for =
     */
    public boolean isIs();
}
