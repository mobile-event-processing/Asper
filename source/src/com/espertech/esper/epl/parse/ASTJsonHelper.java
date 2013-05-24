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
import com.espertech.esper.epl.generated.EsperEPL2Ast;
import org.antlr.runtime.tree.Tree;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Walker to annotation stuctures.
 */
public class ASTJsonHelper
{
    /**
     * Walk an annotation root name or child node (nested annotations).
     * @param node annotation walk node
     * @return annotation descriptor
     * @throws com.espertech.esper.epl.parse.ASTWalkException if the walk failed
     */
    public static Object walk(Tree node) throws ASTWalkException
    {
        if (node.getType() == EsperEPL2Ast.JSON_OBJECT) {
            return walkObject(node);
        }
        if (node.getType() == EsperEPL2Ast.JSON_ARRAY) {
            return walkArray(node);
        }
        if (node.getType() == EsperEPL2Ast.STRING_TYPE) {
            return extractString(node.getText());
        }
        return ASTConstantHelper.parse(node);
    }

    private static String extractString(String text) {
        StringBuffer sb = new StringBuffer(text);
        int startPoint = 1;
        for (;;) {
            int slashIndex = sb.indexOf("\\", startPoint);
            if (slashIndex == -1) {
                break;
            }
            char escapeType = sb.charAt(slashIndex + 1);
            switch (escapeType) {
                case'u':
                    String unicode = extractUnicode(sb, slashIndex);
                    sb.replace(slashIndex, slashIndex + 6, unicode); // backspace
                    break; // back to the loop

                    // note: Java's character escapes match JSON's, which is why it looks like we're replacing
                // "\b" with "\b". We're actually replacing 2 characters (slash-b) with one (backspace).
                case 'b':
                    sb.replace(slashIndex, slashIndex + 2, "\b");
                    break;
                case 't':
                    sb.replace(slashIndex, slashIndex + 2, "\t");
                    break;
                case 'n':
                    sb.replace(slashIndex, slashIndex + 2, "\n");
                    break;
                case 'f':
                    sb.replace(slashIndex, slashIndex + 2, "\f");
                    break;
                case 'r':
                    sb.replace(slashIndex, slashIndex + 2, "\r");
                    break;
                case '\'':
                    sb.replace(slashIndex, slashIndex + 2, "\'");
                    break;
                case '\"':
                    sb.replace(slashIndex, slashIndex + 2, "\"");
                    break;
                case '\\':
                    sb.replace(slashIndex, slashIndex + 2, "\\");
                    break;
                case '/':
                    sb.replace(slashIndex, slashIndex + 2, "/");
                    break;
                default:
                    break;
            }
            startPoint = slashIndex+1;
        }
        sb.deleteCharAt(0);
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static String extractUnicode(StringBuffer sb, int slashIndex) {
        String result;
        String code = sb.substring(slashIndex + 2, slashIndex + 6);
        int charNum = Integer.parseInt(code, 16); // hex to integer
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
            osw.write(charNum);
            osw.flush();
            result = baos.toString("UTF-8"); // Thanks to Silvester Pozarnik for the tip about adding "UTF-8" here
        }
        catch (Exception e) {
            throw new ASTWalkException("Failed to obtain for unicode '" + charNum + "'");
        }
        return result;
    }

    private static Map<String, Object> walkObject(Tree node) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (int i = 0; i < node.getChildCount(); i++) {
            Tree child = node.getChild(i);
            if (child.getType() != EsperEPL2Ast.JSON_FIELD) {
                throw new IllegalStateException("Unexpected node type " + child.getType() + " at text " + child.getText());
            }
            Pair<String, Object> value = walkJSONField(child);
            map.put(value.getFirst(), value.getSecond());
        }
        return map;
    }

    private static List<Object> walkArray(Tree node) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < node.getChildCount(); i++) {
            Object value = walk(node.getChild(i));
            list.add(value);
        }
        return list;
    }

    private static Pair<String, Object> walkJSONField(Tree node) {
        String name;
        if (node.getChild(0).getType() == EsperEPL2Ast.STRING_TYPE || node.getChild(0).getType() == EsperEPL2Ast.STRING_LITERAL || node.getChild(0).getType() == EsperEPL2Ast.QUOTED_STRING_LITERAL) {
            name = extractString(node.getChild(0).getText());
        }
        else {
            name = node.getChild(0).getText();
        }
        Object value = walk(node.getChild(1));
        return new Pair<String, Object>(name, value);
    }
}
