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

import com.espertech.esper.collection.Pair;
import junit.framework.TestCase;
import com.espertech.esper.support.epl.parse.SupportParserHelper;
import com.espertech.esper.antlr.ASTUtil;
import org.antlr.runtime.CommonTokenStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.antlr.runtime.tree.Tree;

public class TestASTFilterSpecHelper extends TestCase
{
    public void testGetPropertyName() throws Exception
    {
        final String PROPERTY = "a('aa').b[1].c";

        // Should parse and result in the exact same property name
        Pair<Tree, CommonTokenStream> parsed = SupportParserHelper.parseEventProperty(PROPERTY);
        Tree propertyNameExprNode = parsed.getFirst();
        ASTUtil.dumpAST(propertyNameExprNode);
        String propertyName = ASTFilterSpecHelper.getPropertyName(propertyNameExprNode, 0);
        assertEquals(PROPERTY, propertyName);

        // Try AST with tokens separated, same property name
        parsed = SupportParserHelper.parseEventProperty("a(    'aa'   ). b [ 1 ] . c");
        propertyNameExprNode = parsed.getFirst();
        propertyName = ASTFilterSpecHelper.getPropertyName(propertyNameExprNode, 0);
        assertEquals(PROPERTY, propertyName);
    }

    public void testGetPropertyNameEscaped() throws Exception
    {
        final String PROPERTY = "a\\.b\\.c";
        Pair<Tree, CommonTokenStream> parsed = SupportParserHelper.parseEventProperty(PROPERTY);
        Tree propertyNameExprNode = parsed.getFirst();
        ASTUtil.dumpAST(propertyNameExprNode);
        String propertyName = ASTFilterSpecHelper.getPropertyName(propertyNameExprNode, 0);
        assertEquals(PROPERTY, propertyName);
    }

    public void testEscapeDot() throws Exception
    {
        String [][] inout = new String[][] {
                {"a", "a"},
                {"", ""},
                {" ", " "},
                {".", "\\."},
                {". .", "\\. \\."},
                {"a.", "a\\."},
                {".a", "\\.a"},
                {"a.b", "a\\.b"},
                {"a..b", "a\\.\\.b"},
                {"a\\.b", "a\\.b"},
                {"a\\..b", "a\\.\\.b"},
                {"a.\\..b", "a\\.\\.\\.b"},
                {"a.b.c", "a\\.b\\.c"}
        };

        for (int i = 0; i < inout.length; i++)
        {
            String input = inout[i][0];
            String expected = inout[i][1];
            assertEquals("for input " + input, expected, ASTFilterSpecHelper.escapeDot(input));
        }
    }

    public void testUnescapeIndexOf() throws Exception
    {
        Object [][] inout = new Object[][] {
                {"a", -1},
                {"", -1},
                {" ", -1},
                {".", 0},
                {" . .", 1},
                {"a.", 1},
                {".a", 0},
                {"a.b", 1},
                {"a..b", 1},
                {"a\\.b", -1},
                {"a.\\..b", 1},
                {"a\\..b", 3},
                {"a.b.c", 1},
                {"abc.", 3}
        };

        for (int i = 0; i < inout.length; i++)
        {
            String input = (String) inout[i][0];
            int expected = (Integer) inout[i][1];
            assertEquals("for input " + input, expected, ASTFilterSpecHelper.unescapedIndexOfDot(input));
        }
    }

    public void testUnescapeDot() throws Exception
    {
        String [][] inout = new String[][] {
                {"a", "a"},
                {"", ""},
                {" ", " "},
                {".", "."},
                {" . .", " . ."},
                {"a\\.", "a."},
                {"\\.a", ".a"},
                {"a\\.b", "a.b"},
                {"a.b", "a.b"},
                {".a", ".a"},
                {"a.", "a."},
                {"a\\.\\.b", "a..b"},
                {"a\\..\\.b", "a...b"},
                {"a.\\..b", "a...b"},
                {"a\\..b", "a..b"},
                {"a.b\\.c", "a.b.c"},
        };

        for (int i = 0; i < inout.length; i++)
        {
            String input = inout[i][0];
            String expected = inout[i][1];
            assertEquals("for input " + input, expected, ASTFilterSpecHelper.unescapeDot(input));
        }
    }

    private static final Log log = LogFactory.getLog(TestASTFilterSpecHelper.class);
}
