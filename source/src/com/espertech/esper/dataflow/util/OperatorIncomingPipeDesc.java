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

package com.espertech.esper.dataflow.util;

import java.util.List;

public class OperatorIncomingPipeDesc {
    private int incomingPortNum;
    private List<PortDesc> sources;

    public OperatorIncomingPipeDesc(int incomingPortNum, List<PortDesc> sources) {
        this.incomingPortNum = incomingPortNum;
        this.sources = sources;
    }

    public int getIncomingPortNum() {
        return incomingPortNum;
    }

    public List<PortDesc> getSources() {
        return sources;
    }
}
