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

import com.espertech.esper.epl.core.EngineImportService;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.generated.EsperEPL2GrammarParser;
import com.espertech.esper.epl.parse.ASTJsonHelper;
import com.espertech.esper.epl.parse.ParseHelper;
import com.espertech.esper.epl.parse.ParseResult;
import com.espertech.esper.epl.parse.ParseRuleSelector;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

import java.util.Map;

public class JsonUtil {
    public static Object parsePopulate(String json, Class topClass, EngineImportService engineImportService) throws ExprValidationException {
        ParseRuleSelector startRuleSelector = new ParseRuleSelector()
        {
            public Tree invokeParseRule(EsperEPL2GrammarParser parser) throws RecognitionException
            {
                EsperEPL2GrammarParser.startJsonValueRule_return r = parser.startJsonValueRule();
                return (Tree) r.getTree();
            }
        };
        ParseResult parseResult = ParseHelper.parse(json, json, true, startRuleSelector, false);
        Tree tree = parseResult.getTree();
        Object parsed = ASTJsonHelper.walk(tree);

        if (!(parsed instanceof Map)) {
            throw new ExprValidationException("Failed to map value to object of type " + topClass.getName() + ", expected Json Map/Object format, received " + (parsed != null ? parsed.getClass().getSimpleName() : "null"));
        }
        Map<String, Object> objectProperties = (Map<String, Object>) parsed;
        return PopulateUtil.instantiatePopulateObject(objectProperties, topClass, engineImportService);
    }
}
