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

package com.espertech.esper.support.util;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import junit.framework.Assert;

public class SupportModelHelper {
    public static void compileCreate(EPServiceProvider epService, String epl) {
        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(epl);
        Assert.assertEquals(epl, model.toEPL());
        EPStatement stmt = epService.getEPAdministrator().create(model);
        Assert.assertEquals(epl, stmt.getText());
    }
}
