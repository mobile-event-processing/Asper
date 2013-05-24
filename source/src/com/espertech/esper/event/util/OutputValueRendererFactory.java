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

package com.espertech.esper.event.util;

/**
 * For rendering an output value returned by a property.
 */
public class OutputValueRendererFactory
{
    private static OutputValueRenderer jsonStringOutput = new OutputValueRendererJSONString();
    private static OutputValueRenderer xmlStringOutput = new OutputValueRendererXMLString();
    private static OutputValueRenderer baseOutput = new OutputValueRendererBase();

    /**
     * Returns a renderer for an output value.
     * @param type to render
     * @param options options
     * @return renderer
     */
    protected static OutputValueRenderer getOutputValueRenderer(Class type, RendererMetaOptions options)
    {
        if (type.isArray())
        {
            type = type.getComponentType();
        }
        if ((type == String.class) || (type == Character.class) || (type == char.class) || type.isEnum())
        {
            if (options.isXmlOutput())
            {
                return xmlStringOutput;
            }
            else
            {
                return jsonStringOutput;
            }
        }
        else
        {
            return baseOutput;
        }
    }
}
