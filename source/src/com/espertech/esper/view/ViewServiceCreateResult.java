/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view;

public class ViewServiceCreateResult
{
    private final Viewable finalViewable;
    private final Viewable topViewable;

    public ViewServiceCreateResult(Viewable finalViewable, Viewable topViewable) {
        this.finalViewable = finalViewable;
        this.topViewable = topViewable;
    }

    public Viewable getFinalViewable() {
        return finalViewable;
    }

    public Viewable getTopViewable() {
        return topViewable;
    }
}
