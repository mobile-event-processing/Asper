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

package com.espertech.esper.rowregex;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;

import java.util.List;
import java.util.Collections;

/**
 * End state in the regex NFA states.
 */
public class RegexNFAStateEnd extends RegexNFAStateBase
{
    /**
     * Ctor.
     */
    public RegexNFAStateEnd() {
        super("endstate", null, -1, false, null);
    }

    public boolean matches(EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext)
    {
        throw new UnsupportedOperationException();
    }

    public List<RegexNFAState> getNextStates()
    {
        return Collections.EMPTY_LIST;
    }
}
