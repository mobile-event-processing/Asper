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

package com.espertech.esper.multithread;

import com.espertech.esper.client.EPServiceProvider;

import java.util.Iterator;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SendEventCallable implements Callable
{
    private final int threadNum;
    private final EPServiceProvider engine;
    private final Iterator<Object> events;

    public SendEventCallable(int threadNum, EPServiceProvider engine, Iterator<Object> events)
    {
        this.threadNum = threadNum;
        this.engine = engine;
        this.events = events;
    }

    public Object call() throws Exception
    {
        log.debug(".call Thread " + Thread.currentThread().getId() + " starting");
        try
        {
            while (events.hasNext())
            {
                engine.getEPRuntime().sendEvent(events.next());
            }
        }
        catch (RuntimeException ex)
        {
            log.fatal("Error in thread " + threadNum, ex);
            return false;
        }
        log.debug(".call Thread " + Thread.currentThread().getId() + " done");
        return true;
    }

    private static final Log log = LogFactory.getLog(SendEventCallable.class);            
}
