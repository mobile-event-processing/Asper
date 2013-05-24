/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

public class ExprDotNodeFilterAnalyzerInputProp implements ExprDotNodeFilterAnalyzerInput
{
    private final int streamNum;
    private final String propertyName;

    public ExprDotNodeFilterAnalyzerInputProp(int streamNum, String propertyName) {
        this.streamNum = streamNum;
        this.propertyName = propertyName;
    }

    public int getStreamNum() {
        return streamNum;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
