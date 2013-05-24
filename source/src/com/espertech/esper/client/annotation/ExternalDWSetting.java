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
 * Annotation for defining a external data window settings.
 */
public @interface ExternalDWSetting
{
    /**
     * Indicator whether iterable or not.
     * @return iterable flag
     */
    boolean iterable() default true;

    /**
     * Function name to invoke when a lookup completed.
     * @return function name
     */
    String functionLookupCompleted() default "";
}
