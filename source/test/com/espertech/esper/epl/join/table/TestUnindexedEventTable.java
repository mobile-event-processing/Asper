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

package com.espertech.esper.epl.join.table;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.support.event.SupportEventBeanFactory;
import com.espertech.esper.epl.join.table.UnindexedEventTable;
import junit.framework.TestCase;

public class TestUnindexedEventTable extends TestCase
{
    public void testFlow()
    {
        UnindexedEventTable rep = new UnindexedEventTable(1);

        EventBean[] addOne = SupportEventBeanFactory.makeEvents(new String[] {"a", "b"});
        rep.add(addOne);
        rep.remove(new EventBean[] {addOne[0]});
    }
}
