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

public interface ContextControllerFactoryService {
    public ContextControllerFactory[] getFactory(ContextControllerFactoryServiceContext context) throws ExprValidationException;

    public ContextPartitionIdManager allocatePartitionIdMgr(String contextName, String contextStmtId);
}
