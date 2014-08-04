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

import com.espertech.esper.antlr.ASTUtil;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.support.epl.parse.SupportParserHelper;
import junit.framework.TestCase;
import org.antlr.runtime.tree.Tree;

import java.util.List;
import java.util.Map;

public class TestJsonParser extends TestCase {

    public void testParse() throws Exception {
        Object result;

        assertEquals("abc", parseLoadJson("\"abc\""));
        assertEquals("http://www.uri.com", parseLoadJson("\"http://www.uri.com\""));
        assertEquals("new\nline", parseLoadJson("\"new\\nline\""));
        assertEquals(" ~ ", parseLoadJson("\" \\u007E \""));
        assertEquals("/", parseLoadJson("\"\\/\""));
        assertEquals(Boolean.TRUE, parseLoadJson("true"));
        assertEquals(false, parseLoadJson("false"));
        assertEquals(null, parseLoadJson("null"));
        assertEquals(10, parseLoadJson("10"));
        assertEquals(-10, parseLoadJson("-10"));
        assertEquals(20L, parseLoadJson("20L"));
        assertEquals(5.5d, parseLoadJson("5.5"));

        result = parseLoadJson("{\"name\":\"myname\",\"value\":5}");
        EPAssertionUtil.assertPropsMap((Map)result, "name,value".split(","), "myname", 5);

        result = parseLoadJson("[\"one\",2]");
        EPAssertionUtil.assertEqualsExactOrder(new Object[] {"one", 2}, (List) result);

        result = parseLoadJson("{\"one\": { 'a' : 2 } }");
        Map inner = (Map) ((Map) result).get("one");
        assertEquals(1, inner.size());

        String json = "{\n" +
                "    \"glossary\": {\n" +
                "        \"title\": \"example glossary\",\n" +
                "\t\t\"GlossDiv\": {\n" +
                "            \"title\": \"S\",\n" +
                "\t\t\t\"GlossList\": {\n" +
                "                \"GlossEntry\": {\n" +
                "                    \"ID\": \"SGML\",\n" +
                "\t\t\t\t\t\"SortAs\": \"SGML\",\n" +
                "\t\t\t\t\t\"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
                "\t\t\t\t\t\"Acronym\": \"SGML\",\n" +
                "\t\t\t\t\t\"Abbrev\": \"ISO 8879:1986\",\n" +
                "\t\t\t\t\t\"GlossDef\": {\n" +
                "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
                "\t\t\t\t\t\t\"GlossSeeAlso\": [\"GML\", \"XML\"]\n" +
                "                    },\n" +
                "\t\t\t\t\t\"GlossSee\": \"markup\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        Tree tree = parseJson(json);
        ASTUtil.dumpAST(tree);
        Object loaded = parseLoadJson(json);
        assertEquals("{glossary={title=example glossary, GlossDiv={title=S, GlossList={GlossEntry={ID=SGML, SortAs=SGML, GlossTerm=Standard Generalized Markup Language, Acronym=SGML, Abbrev=ISO 8879:1986, GlossDef={para=A meta-markup language, used to create markup languages such as DocBook., GlossSeeAlso=[GML, XML]}, GlossSee=markup}}}}}", loaded.toString());
    }

    private Object parseLoadJson(String expression) throws Exception {
        Tree tree = parseJson(expression);
        ASTUtil.dumpAST(tree);
        return ASTJsonHelper.walk(tree);
    }

    private Tree parseJson(String expression) throws Exception {
        return SupportParserHelper.parseJson(expression).getFirst();
    }
}
