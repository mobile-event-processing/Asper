/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Statement-lock implementation that doesn't lock.
 */
public class StatementNoLockImpl implements StatementAgentInstanceLock
{
    private static final Log log = LogFactory.getLog(StatementNoLockImpl.class);

    private final String name;

    /**
     * Ctor.
     * @param name of lock
     */
    public StatementNoLockImpl(String name)
    {
        this.name = name;
    }

    /**
     * Lock write lock.
     */
    public void acquireWriteLock(StatementLockFactory statementLockFactory)
    {
    }

    /**
     * Unlock write lock.
     */
    public void releaseWriteLock(StatementLockFactory statementLockFactory)
    {
    }

    /**
     * Lock read lock.
     */
    public void acquireReadLock()
    {
    }

    /**
     * Unlock read lock.
     */
    public void releaseReadLock()
    {
    }

    public String toString()
    {
        return this.getClass().getSimpleName() + " name=" + name;
    }
}
