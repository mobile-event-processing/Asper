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

package com.espertech.esper.epl.parse;

import junit.framework.TestCase;
import com.espertech.esper.epl.generated.EsperEPL2GrammarParser;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

public class TestASTConstantHelper extends TestCase
{
    public void testParse()
    {
        assertEquals(5, ASTConstantHelper.parse(makeAST(EsperEPL2GrammarParser.NUM_INT, "5")));
        assertEquals(-1, ASTConstantHelper.parse(makeAST(EsperEPL2GrammarParser.INT_TYPE, "-1")));
        assertEquals(35983868567L, ASTConstantHelper.parse(makeAST(EsperEPL2GrammarParser.LONG_TYPE, "35983868567")));
        assertEquals(1.45656f, ASTConstantHelper.parse(makeAST(EsperEPL2GrammarParser.FLOAT_TYPE, "1.45656")));
        assertEquals(-3.346456456d, ASTConstantHelper.parse(makeAST(EsperEPL2GrammarParser.DOUBLE_TYPE, "-3.346456456")));
        assertEquals("a", ASTConstantHelper.parse(makeAST(EsperEPL2GrammarParser.STRING_TYPE, "'a'")));
        assertEquals(true, ASTConstantHelper.parse(makeAST(EsperEPL2GrammarParser.BOOL_TYPE, "true")));
        assertNull(ASTConstantHelper.parse(makeAST(EsperEPL2GrammarParser.NULL_TYPE, null)));
    }

    private Tree makeAST(int type, String text)
    {
        CommonTree ast = new CommonTree();
        ast.token = new CommonToken(type, text);
        return ast;
    }
}
