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

package com.espertech.esper.dataflow.core;

import com.asper.sources.net.sf.cglib.reflect.FastMethod;
import com.espertech.esper.client.dataflow.EPDataFlowSignal;
import com.espertech.esper.dataflow.interfaces.EPDataFlowEmitter;

public interface SubmitHandler extends EPDataFlowEmitter {
    public void submitInternal(Object object);
    public void handleSignal(EPDataFlowSignal signal);
    public FastMethod getFastMethod();
}
