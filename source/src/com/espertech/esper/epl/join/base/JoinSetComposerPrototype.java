/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.join.base;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.view.Viewable;

import java.util.Set;

/**
 * Interface for a prototype populating a join tuple result set from new data and old data for each stream.
 */
public interface JoinSetComposerPrototype
{
    public JoinSetComposerDesc create(Viewable[] streamViews, boolean isFireAndForget);
}
