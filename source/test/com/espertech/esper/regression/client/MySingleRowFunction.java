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

package com.espertech.esper.regression.client;

public class MySingleRowFunction
{
    public static int computePower3(int i)
    {
        return i * i * i;
    }

    public static String surroundx(String target)
    {
        return "X" + target + "X";
    }

    public static InnerSingleRow getChainTop() {
        return new InnerSingleRow();
    }

    public static void throwexception() {
        throw new RuntimeException("This is a 'throwexception' generated exception");
    }

    public static class InnerSingleRow {
        public int chainValue(int i, int j) {
            return i*j;
        }
    }
}
