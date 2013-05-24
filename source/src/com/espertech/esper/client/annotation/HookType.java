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

package com.espertech.esper.client.annotation;

/**
 * Enumeration for the different types of statement-processing hooks (callbacks) that can be provided for a statement.
 */
public enum HookType
{
    /**
     * For use when installing a callback for converting SQL input parameters or column output values.
     */
    SQLCOL,

    /**
     * For use when installing a callback for converting SQL row results to a POJO object.
     */
    SQLROW,

    /**
     * For internal use, query planning reporting.
     */
    INTERNAL_QUERY_PLAN
}
