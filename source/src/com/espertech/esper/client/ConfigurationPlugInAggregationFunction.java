/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client;

import java.io.Serializable;

/**
 * Configuration information for plugging in a custom aggregation function.
 */
public class ConfigurationPlugInAggregationFunction implements Serializable
{
    private String name;
    private String functionClassName;
    private String factoryClassName;

    private static final long serialVersionUID = 4096734947283212246L;

    /**
     * Ctor.
     */
    public ConfigurationPlugInAggregationFunction()
    {
    }

    /**
     * Ctor.
     * @param name of the aggregation function
     * @param functionClassName (deprecated) name of the class providing the aggregation function
     * @param factoryClassName the name of the aggregation function factory class
     */
    public ConfigurationPlugInAggregationFunction(String name, String functionClassName, String factoryClassName) {
        this.name = name;
        this.functionClassName = functionClassName;
        this.factoryClassName = factoryClassName;
    }

    /**
     * Returns the aggregation function name.
     * @return aggregation function name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the aggregation function name.
     * @param name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the aggregation function name.
     * <p>
     *     Use the factory class name instead.
     * </p>
     * @return name
     * @deprecated
     */
    public String getFunctionClassName()
    {
        return functionClassName;
    }

    /**
     * Sets the aggregation function's implementation class name.
     * <p>
     *     Use the factory class name instead.
     * </p>
     * @param functionClassName is the implementation class name
     * @deprecated
     */
    public void setFunctionClassName(String functionClassName)
    {
        this.functionClassName = functionClassName;
    }

    /**
     * Returns the class name of the aggregation function factory class.
     * @return class name
     */
    public String getFactoryClassName() {
        return factoryClassName;
    }

    /**
     * Sets the class name of the aggregation function factory class.
     * @param factoryClassName class name to set
     */
    public void setFactoryClassName(String factoryClassName) {
        this.factoryClassName = factoryClassName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationPlugInAggregationFunction that = (ConfigurationPlugInAggregationFunction) o;

        if (factoryClassName != null ? !factoryClassName.equals(that.factoryClassName) : that.factoryClassName != null)
            return false;
        if (functionClassName != null ? !functionClassName.equals(that.functionClassName) : that.functionClassName != null)
            return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (functionClassName != null ? functionClassName.hashCode() : 0);
        result = 31 * result + (factoryClassName != null ? factoryClassName.hashCode() : 0);
        return result;
    }
}
