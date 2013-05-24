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

package com.espertech.esper.core.context.mgr;

import com.espertech.esper.epl.expression.ExprValidationException;

public class ContextControllerFactoryServiceImpl implements ContextControllerFactoryService {

    public final static ContextControllerFactoryServiceImpl DEFAULT_FACTORY = new ContextControllerFactoryServiceImpl();
    private final static ContextStateCache CACHE_NO_SAVE = new ContextStateCacheNoSave();

    public ContextControllerFactory[] getFactory(ContextControllerFactoryServiceContext serviceContext) throws ExprValidationException {
        return ContextControllerFactoryHelper.getFactory(serviceContext, CACHE_NO_SAVE);
    }

    public ContextPartitionIdManager allocatePartitionIdMgr(String contextName, String contextStmtId) {
        return new ContextPartitionIdManagerImpl();
    }
}
