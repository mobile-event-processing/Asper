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

package com.espertech.esper.util;

import com.espertech.esper.support.bean.SupportEnum;
import junit.framework.TestCase;

public class TestSerializableObjectCopier extends TestCase
{
    public void testCopyEnum() throws Exception
    {
        SupportEnum enumOne = SupportEnum.ENUM_VALUE_2;
        Object result = SerializableObjectCopier.copy(enumOne);
        assertEquals(result, enumOne);
        assertTrue(result == enumOne);
    }
}
