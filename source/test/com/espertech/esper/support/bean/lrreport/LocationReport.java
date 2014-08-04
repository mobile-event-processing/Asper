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

package com.espertech.esper.support.bean.lrreport;

import java.util.List;

public class LocationReport {

    private List<Item> items;

    public LocationReport(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }
}
