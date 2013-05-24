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
import com.espertech.esper.util.SimpleNumberBigDecimalCoercer;
import com.espertech.esper.util.SimpleNumberBigIntegerCoercer;
import com.espertech.esper.util.SimpleNumberCoercerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents the between-clause function in an expression tree.
 */
public interface ExprBetweenNode extends ExprNode, ExprEvaluator
{
    /**
     * Returns true if the low endpoint is included, false if not
     * @return indicator if endppoint is included
     */
    public boolean isLowEndpointIncluded();

    /**
     * Returns true if the high endpoint is included, false if not
     * @return indicator if endppoint is included
     */
    public boolean isHighEndpointIncluded();

    /**
     * Returns true for inverted range, or false for regular (openn/close/half-open/half-closed) ranges.
     * @return true for not betwene, false for between
     */
    public boolean isNotBetween();
}
