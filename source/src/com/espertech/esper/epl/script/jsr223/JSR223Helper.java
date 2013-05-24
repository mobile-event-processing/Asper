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

package com.espertech.esper.epl.script.jsr223;

import com.asper.sources.javax.script.*;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.ExpressionScriptProvided;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class JSR223Helper {

    private static final Log log = LogFactory.getLog(JSR223Helper.class);

    public static CompiledScript verifyCompileScript(ExpressionScriptProvided script, String dialect) throws ExprValidationException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName(dialect);
        if (engine == null) {
            throw new ExprValidationException("Failed to obtain script engine for dialect '" + dialect + "' for script '" + script.getName() + "'");
        }
        engine.put(ScriptEngine.FILENAME, script.getName());

        Compilable compilingEngine = (Compilable)engine;
        try {
            return compilingEngine.compile(script.getExpression());
        }
        catch (ScriptException ex) {
            String message = "Exception compiling script '" + script.getName() + "' of dialect '" + dialect + "': " + getScriptCompileMsg(ex);
            log.info(message, ex);
            throw new ExprValidationException(message, ex);
        }
    }

    public static String getScriptCompileMsg(ScriptException ex) {
        if (ex.getLineNumber() != 1 && ex.getColumnNumber() != -1) {
            return "At line " + ex.getLineNumber() + " column " + ex.getColumnNumber() + ": " + ex.getMessage();
        }
        else {
            return ex.getMessage();
        }
    }
}
