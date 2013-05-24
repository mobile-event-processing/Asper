/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.parse;

import com.espertech.esper.antlr.ASTUtil;
import com.espertech.esper.antlr.NoCaseSensitiveStream;
import com.espertech.esper.client.EPException;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.epl.generated.EsperEPL2Ast;
import com.espertech.esper.epl.generated.EsperEPL2GrammarLexer;
import com.espertech.esper.epl.generated.EsperEPL2GrammarParser;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.Tree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Helper class for parsing an expression and walking a parse tree.
 */
public class ParseHelper {
    /**
     * Newline.
     */
    public final static String newline = System.getProperty("line.separator");

    /**
     * Walk parse tree starting at the rule the walkRuleSelector supplies.
     *
     * @param ast                     - ast to walk
     * @param walker                  - walker instance
     * @param walkRuleSelector        - walk rule
     * @param expression              - the expression we are walking in string form
     * @param eplStatementForErrorMsg - statement text for error messages
     */
    public static void walk(Tree ast, EPLTreeWalker walker, WalkRuleSelector walkRuleSelector, String expression, String eplStatementForErrorMsg) {
        // Walk tree
        try {
            if (log.isDebugEnabled()) {
                log.debug(".walk Walking AST using walker " + walker.getClass().getName());
            }

            walkRuleSelector.invokeWalkRule(walker);

            if (log.isDebugEnabled()) {
                log.debug(".walk AST tree after walking");
                ASTUtil.dumpAST(ast);
            }
        } catch (RuntimeException e) {
            log.info("Error walking statement [" + expression + "]", e);
            if (e.getCause() instanceof RecognitionException) {
                throw ExceptionConvertor.convert((RecognitionException) e.getCause(), eplStatementForErrorMsg, walker);
            } else {
                throw e;
            }
        } catch (RecognitionException e) {
            log.info("Error walking statement [" + expression + "]", e);
            throw ExceptionConvertor.convert(e, eplStatementForErrorMsg, walker);
        }
    }

    /**
     * Parse expression using the rule the ParseRuleSelector instance supplies.
     *
     * @param expression           - text to parse
     * @param parseRuleSelector    - parse rule to select
     * @param addPleaseCheck       - true to include depth paraphrase
     * @param eplStatementErrorMsg - text for error
     * @return AST - syntax tree
     * @throws EPException when the AST could not be parsed
     */
    public static ParseResult parse(String expression, String eplStatementErrorMsg, boolean addPleaseCheck, ParseRuleSelector parseRuleSelector, boolean rewriteScript) throws EPException {
        if (log.isDebugEnabled()) {
            log.debug(".parse Parsing expr=" + expression);
        }

        CharStream input;
        try {
            input = new NoCaseSensitiveStream(new StringReader(expression));
        } catch (IOException ex) {
            throw new EPException("IOException parsing expression '" + expression + '\'', ex);
        }

        EsperEPL2GrammarLexer lex = new EsperEPL2GrammarLexer(input);
        TokenRewriteStream tokens = new TokenRewriteStream(lex);
        EsperEPL2GrammarParser parser = new EsperEPL2GrammarParser(tokens);

        Tree tree;
        try {
            tree = parseRuleSelector.invokeParseRule(parser);
        } catch (RuntimeException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error parsing statement [" + eplStatementErrorMsg + "]", e);
            }
            if (e.getCause() instanceof RecognitionException) {
                throw ExceptionConvertor.convertStatement((RecognitionException) e.getCause(), eplStatementErrorMsg, addPleaseCheck, parser);
            } else {
                throw e;
            }
        } catch (RecognitionException ex) {
            if (rewriteScript && isContainsScriptExpression(tokens)) {
                ScriptResult rewriteExpression = rewriteTokensScript(tokens);
                ParseResult result = parse(rewriteExpression.getRewrittenEPL(), eplStatementErrorMsg, addPleaseCheck, parseRuleSelector, false);
                return new ParseResult(result.getTree(), result.getExpressionWithoutAnnotations(), result.getTokenStream(), rewriteExpression.getScripts());
            }

            log.debug("Error parsing statement [" + expression + "]", ex);
            throw ExceptionConvertor.convertStatement(ex, eplStatementErrorMsg, addPleaseCheck, parser);
        }

        // if we are re-writing scripts and contain a script, then rewrite
        if (rewriteScript && isContainsScriptExpression(tokens)) {
            ScriptResult rewriteExpression = rewriteTokensScript(tokens);
            ParseResult result = parse(rewriteExpression.getRewrittenEPL(), rewriteExpression.getRewrittenEPL(), addPleaseCheck, parseRuleSelector, false);
            return new ParseResult(result.getTree(), result.getExpressionWithoutAnnotations(), result.getTokenStream(), rewriteExpression.getScripts());
        }

        if (log.isDebugEnabled()) {
            log.debug(".parse Dumping AST...");
            ASTUtil.dumpAST(tree);
        }

        return new ParseResult(tree, getNoAnnotation(expression, tree, tokens), tokens, Collections.<String>emptyList());
    }

    private static ScriptResult rewriteTokensScript(TokenRewriteStream tokens) {
        List<String> scripts = new ArrayList<String>();

        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == EsperEPL2Ast.EXPRESSIONDECL) {
                Token tokenBefore = getTokenBefore(i, tokens);
                boolean isCreateExpressionClause = tokenBefore != null && tokenBefore.getType() == EsperEPL2Ast.CREATE;
                Pair<String, Integer> nameAndNameStart = findScriptName(i + 1, tokens);

                int startIndex = findStartTokenScript(nameAndNameStart.getSecond(), tokens, EsperEPL2Ast.LBRACK);
                if (startIndex != -1) {
                    int endIndex = findEndTokenScript(startIndex + 1, tokens, EsperEPL2Ast.RBRACK, EsperEPL2GrammarParser.getAfterScriptTokens(), !isCreateExpressionClause);
                    if (endIndex != -1) {

                        StringWriter writer = new StringWriter();
                        for (int j = startIndex + 1; j < endIndex; j++) {
                            writer.append(tokens.get(j).getText());
                        }
                        scripts.add(writer.toString());

                        rewriteScript(startIndex, endIndex, tokens);
                    }
                }
            }
        }

        String rewrittenEPL = tokens.toString();
        return new ScriptResult(rewrittenEPL, scripts);
    }

    private static Token getTokenBefore(int i, TokenRewriteStream tokens) {
        int position = i-1;
        while (position >= 0) {
            Token t = tokens.get(position);
            if (t.getChannel() != 99) {
                return t;
            }
            position--;
        }
        return null;
    }

    private static Pair<String, Integer> findScriptName(int start, TokenRewriteStream tokens) {
        String lastIdent = null;
        int lastIdentIndex = 0;
        for (int i = start; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == EsperEPL2Ast.IDENT) {
                lastIdent = tokens.get(i).getText();
                lastIdentIndex = i;
            }
            if (tokens.get(i).getType() == EsperEPL2Ast.LPAREN) {
                break;
            }
            // find beginning of script, ignore brackets
            if (tokens.get(i).getType() == EsperEPL2Ast.LBRACK && tokens.get(i+1).getType() != EsperEPL2Ast.RBRACK) {
                break;
            }
        }
        if (lastIdent == null) {
            throw new IllegalStateException("Failed to parse expression name");
        }
        return new Pair<String, Integer>(lastIdent, lastIdentIndex);
    }


    private static void rewriteScript(int startIndex, int endIndex, TokenRewriteStream tokens) {
        if (startIndex >= endIndex - 1) {
            return;
        }

        Token[] tokensCapture = new Token[endIndex - startIndex - 1];
        int count = 0;
        for (int i = startIndex + 1; i < endIndex; i++) {
            tokensCapture[count] = tokens.get(i);
            count++;
        }
        tokens.delete(startIndex + 1, endIndex - 1);
        int start = endIndex -1;
        tokens.insertAfter(start, "'");
        for (int i = 0; i < tokensCapture.length; i++) {
            if (tokensCapture[i].getType() == EsperEPL2Ast.QUOTED_STRING_LITERAL) {
                tokens.insertAfter(start, "\\'");
                tokens.insertAfter(start, tokensCapture[i].getText().substring(1, tokensCapture[i].getText().length() - 1));
                tokens.insertAfter(start, "\\'");
            }
            else {
                tokens.insertAfter(start, tokensCapture[i].getText());
            }
        }
        tokens.insertAfter(start, "'");
    }

    private static int findStartTokenScript(int startIndex, TokenRewriteStream tokens, int tokenTypeSearch) {
        int found = -1;
        for (int i = startIndex; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == tokenTypeSearch) {
                return i;
            }
        }
        return found;
    }

    private static int findEndTokenScript(int startIndex, TokenRewriteStream tokens, int tokenTypeSearch, Set<Integer> afterScriptTokens, boolean requireAfterScriptToken) {
        int found = -1;
        for (int i = startIndex; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == tokenTypeSearch) {
                if (!requireAfterScriptToken) {
                    return i;
                }
                // The next non-comment token must be among the afterScriptTokens, i.e. SELECT/INSERT/ON/DELETE/UPDATE
                // Find next non-comment token.
                for (int j = i + 1; j < tokens.size(); j++) {
                    Token next = tokens.get(j);
                    if (next.getChannel() == 0) {
                        if (afterScriptTokens.contains(next.getType())) {
                            found = i;
                        }
                        break;
                    }
                }
            }
            if (found != -1) {
                break;
            }
        }
        return found;
    }

    private static boolean isContainsScriptExpression(TokenRewriteStream tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == EsperEPL2Ast.EXPRESSIONDECL) {
                int startIndex = findStartTokenScript(i + 1, tokens, EsperEPL2Ast.LBRACK);
                if (startIndex != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getNoAnnotation(String expression, Tree tree, CommonTokenStream tokens) {
        Token lastAnnotationToken = null;
        for (int i = 0; i < tree.getChildCount(); i++) {
            if (tree.getChild(i).getType() == EsperEPL2Ast.ANNOTATION) {
                lastAnnotationToken = tokens.get(tree.getChild(i).getTokenStopIndex());
            } else {
                break;
            }
        }

        if (lastAnnotationToken == null) {
            return null;
        }

        try {
            int line = lastAnnotationToken.getLine();
            int charpos = lastAnnotationToken.getCharPositionInLine();
            int fromChar = charpos + lastAnnotationToken.getText().length();
            if (line == 1) {
                return expression.substring(fromChar).trim();
            }

            String[] lines = expression.split("\r\n|\r|\n");
            StringBuilder buf = new StringBuilder();
            buf.append(lines[line - 1].substring(fromChar));
            for (int i = line; i < lines.length; i++) {
                buf.append(lines[i]);
                if (i < lines.length - 1) {
                    buf.append(newline);
                }
            }
            return buf.toString().trim();
        } catch (RuntimeException ex) {
            log.error("Error determining non-annotated expression sting: " + ex.getMessage(), ex);
        }
        return null;
    }

    private static class ScriptResult {
        private final String rewrittenEPL;
        private final List<String> scripts;

        private ScriptResult(String rewrittenEPL, List<String> scripts) {
            this.rewrittenEPL = rewrittenEPL;
            this.scripts = scripts;
        }

        public String getRewrittenEPL() {
            return rewrittenEPL;
        }

        public List<String> getScripts() {
            return scripts;
        }
    }

    private static Log log = LogFactory.getLog(ParseHelper.class);
}
