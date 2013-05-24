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

import com.asper.sources.javax.script.CompiledScript;
import com.espertech.esper.epl.spec.ExpressionScriptCompiled;


public class ExpressionScriptCompiledJSR223 implements ExpressionScriptCompiled {
    private final CompiledScript compiled;

    public ExpressionScriptCompiledJSR223(CompiledScript compiled) {
        this.compiled = compiled;
    }

    public CompiledScript getCompiled() {
        return compiled;
    }

    public Class getKnownReturnType() {
        return null;
    }
}
