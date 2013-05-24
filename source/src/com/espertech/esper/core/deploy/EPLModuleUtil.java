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

package com.espertech.esper.core.deploy;

import com.espertech.esper.antlr.NoCaseSensitiveStream;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.deploy.Module;
import com.espertech.esper.client.deploy.ModuleItem;
import com.espertech.esper.client.deploy.ParseException;
import com.espertech.esper.core.service.StatementEventTypeRef;
import com.espertech.esper.epl.generated.EsperEPL2GrammarLexer;
import com.espertech.esper.epl.generated.EsperEPL2GrammarParser;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.EventTypeSPI;
import com.espertech.esper.filter.FilterService;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EPLModuleUtil
{
    private static Log log = LogFactory.getLog(EPLModuleUtil.class);

    /**
     * Newline character.
     */
    public static final String newline = System.getProperty("line.separator");

    public static Module readInternal(InputStream stream, String resourceName) throws IOException, ParseException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringWriter buffer = new StringWriter();
        String strLine;
        while ((strLine = br.readLine()) != null)   {
            buffer.append(strLine);
            buffer.append(newline);
        }
        stream.close();

        return parseInternal(buffer.toString(), resourceName);
    }

    public static Module parseInternal(String buffer, String resourceName) throws IOException, ParseException {

        List<EPLModuleParseItem> semicolonSegments = EPLModuleUtil.parse(buffer.toString());
        List<ParseNode> nodes = new ArrayList<ParseNode>();
        for (EPLModuleParseItem segment : semicolonSegments) {
            nodes.add(EPLModuleUtil.getModule(segment, resourceName));
        }

        String moduleName = null;
        int count = 0;
        for (ParseNode node : nodes) {
            if (node instanceof ParseNodeComment) {
                continue;
            }
            if (node instanceof ParseNodeModule) {
                if (moduleName != null) {
                    throw new ParseException("Duplicate use of the 'module' keyword for resource '" + resourceName + "'");
                }
                if (count > 0) {
                    throw new ParseException("The 'module' keyword must be the first declaration in the module file for resource '" + resourceName + "'");
                }
                moduleName = ((ParseNodeModule) node).getModuleName();
            }
            count++;
        }

        Set<String> uses = new LinkedHashSet<String>();
        Set<String> imports = new LinkedHashSet<String>();
        count = 0;
        for (ParseNode node : nodes) {
            if ((node instanceof ParseNodeComment) || (node instanceof ParseNodeModule)) {
                continue;
            }
            String message = "The 'uses' and 'import' keywords must be the first declaration in the module file or follow the 'module' declaration";
            if (node instanceof ParseNodeUses) {
                if (count > 0) {
                    throw new ParseException(message);
                }
                uses.add(((ParseNodeUses) node).getUses());
                continue;
            }
            if (node instanceof ParseNodeImport) {
                if (count > 0) {
                    throw new ParseException(message);
                }
                imports.add(((ParseNodeImport) node).getImported());
                continue;
            }
            count++;
        }

        List<ModuleItem> items = new ArrayList<ModuleItem>();
        for (ParseNode node : nodes) {
            if ((node instanceof ParseNodeComment) || (node instanceof ParseNodeExpression)) {
                boolean isComments = (node instanceof ParseNodeComment);
                items.add(new ModuleItem(node.getItem().getExpression(), isComments, node.getItem().getLineNum(), node.getItem().getStartChar(), node.getItem().getEndChar()));
            }
        }

        return new Module(moduleName, resourceName, uses, imports, items, buffer);
    }

    public static List<EventType> undeployTypes(Set<String> referencedTypes, StatementEventTypeRef statementEventTypeRef, EventAdapterService eventAdapterService, FilterService filterService)
    {
        List<EventType> undeployedTypes = new ArrayList<EventType>();
        for (String typeName : referencedTypes) {

            boolean typeInUse = statementEventTypeRef.isInUse(typeName);
            if (typeInUse) {
                if (log.isDebugEnabled()) {
                    log.debug("Event type '" + typeName + "' is in use, not removing type");
                }
                continue;
            }

            if (log.isDebugEnabled()) {
                log.debug("Event type '" + typeName + "' is no longer in use, removing type");
            }
            EventType type = eventAdapterService.getExistsTypeByName(typeName);
            if (type != null) {
                EventTypeSPI spi = (EventTypeSPI) type;
                if (!spi.getMetadata().isApplicationPreConfigured()) {
                    eventAdapterService.removeType(typeName);
                    undeployedTypes.add(spi);
                    filterService.removeType(spi);
                }
            }
        }
        return undeployedTypes;
    }

    public static ParseNode getModule(EPLModuleParseItem item, String resourceName) throws ParseException, IOException {
        CharStream input = new NoCaseSensitiveStream(new StringReader(item.getExpression()));

        EsperEPL2GrammarLexer lex = new EsperEPL2GrammarLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lex);

        List tokens = tokenStream.getTokens();
        int beginIndex = 0;
        boolean isMeta = false;
        boolean isModule = false;
        boolean isUses = false;
        boolean isExpression = false;

        while (beginIndex < tokens.size()) {
            Token t = (Token) tokens.get(beginIndex);
            if ((t.getType() == EsperEPL2GrammarParser.WS) ||
                (t.getType() == EsperEPL2GrammarParser.SL_COMMENT) ||
                (t.getType() == EsperEPL2GrammarParser.ML_COMMENT)) {
                beginIndex++;
                continue;
            }
            String tokenText = t.getText().trim().toLowerCase();
            if (tokenText.equals("module")) {
                isModule = true; isMeta = true;
            }
            else if (tokenText.equals("uses")) {
                isUses = true; isMeta = true;
            }
            else if (tokenText.equals("import")) {
                isMeta = true;
            }
            else {
                isExpression = true;
                break;
            }
            beginIndex++;
            beginIndex++;   // skip space
            break;
        }

        if (isExpression) {
            return new ParseNodeExpression(item);
        }
        if (!isMeta) {
            return new ParseNodeComment(item);
        }

        // check meta tag (module, uses, import)
        StringWriter buffer = new StringWriter();
        for (int i = beginIndex; i < tokens.size(); i++)
        {
            Token t = (Token) tokens.get(i);
            if ((t.getType() != EsperEPL2GrammarParser.IDENT) &&
                (t.getType() != EsperEPL2GrammarParser.DOT) && 
                (t.getType() != EsperEPL2GrammarParser.STAR) &&
                (!t.getText().matches("[a-zA-Z]*"))) {
                throw getMessage(isModule, isUses, resourceName, t.getType());
            }
            buffer.append(t.getText().trim());
        }

        String result = buffer.toString().trim();
        if (result.length() == 0) {
            throw getMessage(isModule, isUses, resourceName, -1);
        }

        if (isModule) {
            return new ParseNodeModule(item, result);
        }
        else if (isUses) {
            return new ParseNodeUses(item, result);
        }
        return new ParseNodeImport(item, result);
    }

    private static ParseException getMessage(boolean module, boolean uses, String resourceName, int type)
    {
        String message = "Keyword '";
        if (module) {
            message += "module";
        }
        else if (uses) {
            message += "uses";
        }
        else {
            message += "import";
        }
        message += "' must be followed by a name or package name (set of names separated by dots) for resource '" + resourceName + "'";

        if (type != -1) {
            String tokenName = EsperEPL2GrammarParser.getLexerTokenParaphrases().get(type);
            if (tokenName == null) {
                tokenName = EsperEPL2GrammarParser.getParserTokenParaphrases().get(type);
            }
            if (tokenName != null) {
                message += ", unexpected reserved keyword " + tokenName + " was encountered as part of the name";
            }
        }
        return new ParseException(message);
    }

    public static List<EPLModuleParseItem> parse(String module) {

        CharStream input;
        try
        {
            input = new NoCaseSensitiveStream(new StringReader(module));
        }
        catch (IOException ex)
        {
            log.error("Exception reading model expression: " + ex.getMessage(), ex);
            return null;
        }

        EsperEPL2GrammarLexer lex = new EsperEPL2GrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);

        List<EPLModuleParseItem> statements = new ArrayList<EPLModuleParseItem>();
        StringWriter current = new StringWriter();
        Integer lineNum = null;
        int charPosStart = 0;
        int charPos = 0;
        for (Object token : tokens.getTokens()) // Call getTokens first before invoking tokens.size! ANTLR problem
        {
            Token t = (Token) token;
            if (t.getType() == EsperEPL2GrammarParser.SEMI) {
                if (current.toString().trim().length() > 0) {
                    statements.add(new EPLModuleParseItem(current.toString().trim(), lineNum == null ? 0 : lineNum, charPosStart, charPos));
                    lineNum = null;
                }
                current = new StringWriter();
            }
            else {
                if ((lineNum == null) && (t.getType() != EsperEPL2GrammarParser.WS)) {
                    lineNum = t.getLine();
                    charPosStart = charPos;
                }
                current.append(t.getText());
                charPos += t.getText().length();
            }
        }

        if (current.toString().trim().length() > 0) {
            statements.add(new EPLModuleParseItem(current.toString().trim(), lineNum == null ? 0 : lineNum, 0, 0));
        }
        return statements;
    }

    public static Module readFile(File file) throws IOException, ParseException {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return EPLModuleUtil.readInternal(inputStream, file.getAbsolutePath());
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.debug("Error closing input stream", e);
                }
            }
        }
    }

    public static Module readResource(String resource) throws IOException, ParseException {
        String stripped = resource.startsWith("/") ? resource.substring(1) : resource;

        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader!=null) {
            stream = classLoader.getResourceAsStream( stripped );
        }
        if ( stream == null ) {
            stream = EPDeploymentAdminImpl.class.getResourceAsStream( resource );
        }
        if ( stream == null ) {
            stream = EPDeploymentAdminImpl.class.getClassLoader().getResourceAsStream( stripped );
        }
        if ( stream == null ) {
           throw new IOException("Failed to find resource '" + resource + "' in classpath");
        }

        try {
            return EPLModuleUtil.readInternal(stream, resource);
        }
        finally {
            try {
                stream.close();
            } catch (IOException e) {
                log.debug("Error closing input stream", e);
            }
        }
    }
}
