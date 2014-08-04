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

import junit.framework.TestCase;

public class TestParserTool extends TestCase {

    public void testSpecialTrans() {
        String text = "        public String getDescription() {\n" +
    "            return \"1216:1: unaryExpression : ( MINUS eventProperty -> ^( UNARY_MINUS eventProperty ) | constant | substitution | LPAREN expression RPAREN (d= DOT libFunctionNoClass (d= DOT libFunctionNoClass )* )? -> {$d != null}? ^( DOT_EXPR expression ( libFunctionNoClass )+ ) -> expression | eventPropertyOrLibFunction | ( builtinFunc )=> ( builtinFunc ) | arrayExpression | subSelectExpression | existsSubSelectExpression );\";\n" +
    "        }\n" +
    "        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {\n" +
    "            TokenStream input = (TokenStream)_input;\n" +
    "        \tint _s = s;\n" +
    "            switch ( s ) {\n" +
    "                    case 0 : \n" +
    "                        int LA174_0 = input.LA(1);\n" +
    "                        int index174_0 = input.index();\n" +
    "                        input.rewind();\n" +
    "                        s = -1;\n" +
    "                        if ( (LA174_0==MINUS) ) {s = 1;}\n" +
    "                        else if ( ((LA174_0>=BOOLEAN_TRUE && LA174_0<=VALUE_NULL)||LA174_0==NUM_DOUBLE||LA174_0==PLUS||(LA174_0>=STRING_LITERAL && LA174_0<=QUOTED_STRING_LITERAL)||LA174_0==NUM_INT||(LA174_0>=NUM_LONG && LA174_0<=NUM_FLOAT)) ) {s = 2;}\n" +
    "                        else if ( (LA174_0==CURRENT_TIMESTAMP) && (synpred3_EsperEPL2Grammar())) {s = 64;}\n" +
    "                        else if ( (LA174_0==LCURLY) ) {s = 65;}\n" +
    "                        input.seek(index174_0);\n" +
    "                        if ( s>=0 ) return s;\n" +
    "                        break;\n" +
    "                    case 1 : \n" +
    "                        int LA174_185 = input.LA(1);\n" +
    "                        int index174_185 = input.index();\n" +
    "                        input.rewind();\n" +
    "                        s = -1;\n" +
    "                        if ( (LA174_185==ALL) && (synpred3_EsperEPL2Grammar())) {s = 1071;}\n" +
    "                        else if ( (LA174_185==DISTINCT) && (synpred3_EsperEPL2Grammar())) {s = 1072;}\n" +
    "                        else if ( (LA174_185==CASE) && (synpred3_EsperEPL2Grammar())) {s = 1073;}\n" +
    "                        else if ( (LA174_185==STAR) && (synpred3_EsperEPL2Grammar())) {s = 1140;}\n" +
    "                        input.seek(index174_185);\n" +
    "                        if ( s>=0 ) return s;\n" +
    "                        break;\n" +
    "            }\n" +
    "            if (state.backtracking>0) {state.failed=true; return -1;}\n" +
    "            NoViableAltException nvae =\n" +
    "                new NoViableAltException(getDescription(), 174, _s, input);\n" +
    "            error(nvae);\n" +
    "            throw nvae;\n" +
    "        }\n" +
    "    }\n" +
    "    public static final BitSet FOLLOW_annotationNoEnum_in_startPatternExpressionRule1660 = new BitSet(new long[]{0x000000000000E000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000800000230000L,0x0000000000000080L});\n" +
    "    public static final BitSet FOLLOW_patternExpression_in_startPatternExpressionRule1665 = new BitSet(new long[]{0x0000000000000000L});";

        String textOut = ParserTool.transform(text);
        System.out.println(textOut);
    }
}
