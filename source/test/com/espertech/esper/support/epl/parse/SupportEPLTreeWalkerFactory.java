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

package com.espertech.esper.support.epl.parse;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.epl.core.EngineImportService;
import com.espertech.esper.epl.core.EngineImportServiceImpl;
import com.espertech.esper.epl.declexpr.ExprDeclaredServiceImpl;
import com.espertech.esper.epl.parse.EPLTreeWalker;
import com.espertech.esper.epl.spec.SelectClauseStreamSelectorEnum;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.epl.variable.VariableServiceImpl;
import com.espertech.esper.pattern.PatternNodeFactoryImpl;
import com.espertech.esper.support.event.SupportEventAdapterService;
import com.espertech.esper.support.schedule.SupportSchedulingServiceImpl;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;

public class SupportEPLTreeWalkerFactory
{
    public static EPLTreeWalker makeWalker(Tree tree, CommonTokenStream tokenStream, EngineImportService engineImportService, VariableService variableService)
    {
        return new EPLTreeWalker(new CommonTreeNodeStream(tree), tokenStream, engineImportService, variableService, new SupportSchedulingServiceImpl(), SelectClauseStreamSelectorEnum.ISTREAM_ONLY, "uri", new Configuration(), new PatternNodeFactoryImpl(), null, null, new ExprDeclaredServiceImpl());
    }

    public static EPLTreeWalker makeWalker(Tree tree, CommonTokenStream tokenStream)
    {
        return makeWalker(tree, tokenStream, new EngineImportServiceImpl(true, true, true), new VariableServiceImpl(0, null, SupportEventAdapterService.getService(), null));
    }
}
