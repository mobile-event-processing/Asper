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

import com.espertech.esper.util.MetaDefItem;

import java.io.Serializable;

/**
 * Describes a column name and type.
 */
public class ColumnDesc implements MetaDefItem, Serializable
{
    private static final long serialVersionUID = -3508097717971934622L;
    
    private final String name;
    private final String type;
    private final boolean array;

    /**
     * Ctor.
     * @param name column name
     * @param type type
     * @param array true for array
     */
    public ColumnDesc(String name, String type, boolean array)
    {
        this.name = name;
        this.type = type;
        this.array = array;
    }

    /**
     * Returns column name.
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Return column type
     * @return type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Return true for array
     * @return array indicator
     */
    public boolean isArray()
    {
        return array;
    }
}
