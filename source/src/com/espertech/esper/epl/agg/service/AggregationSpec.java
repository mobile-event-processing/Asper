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

package com.espertech.esper.epl.agg.service;

/**
 * Wrapper for an aggregation spec consisting of a stream number.
 */
public class AggregationSpec
{
    private int streamNum;

    /**
     * Ctor.
     * @param streamNum stream number
     */
    public AggregationSpec(int streamNum)
    {
        this.streamNum = streamNum;
    }

    /**
     * Returns stream number.
     * @return stream number
     */
    public int getStreamNum()
    {
        return streamNum;
    }

    /**
     * Sets the stream number
     * @param streamNum to set
     */
    public void setStreamNum(int streamNum)
    {
        this.streamNum = streamNum;
    }
}
