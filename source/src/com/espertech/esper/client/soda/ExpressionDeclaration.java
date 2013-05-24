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

package com.espertech.esper.client.soda;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

/**
 * Represents a single expression declaration that applies to a given statement.
 */
public class ExpressionDeclaration implements Serializable {

    private static final long serialVersionUID = 8445897497101986441L;

    private String name;
    private List<String> parameterNames;
    private Expression expression;

    /**
     * Ctor.
     */
    public ExpressionDeclaration() {
    }

    /**
     * Ctor.
     * @param name of expression
     * @param parameterNames expression paramater names
     * @param expression the expression body
     */
    public ExpressionDeclaration(String name, List<String> parameterNames, Expression expression) {
        this.name = name;
        this.parameterNames = parameterNames;
        this.expression = expression;
    }

    /**
     * Returns expression name.
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets expression name.
     * @param name name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the expression body.
     * @return expression body
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Sets the expression body.
     * @param expression body to set
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    /**
     * Returns the paramater names.
     * @return paramater names
     */
    public List<String> getParameterNames() {
        return parameterNames;
    }

    /**
     * Sets the paramater names.
     * @param parameterNames paramater names to set
     */
    public void setParameterNames(List<String> parameterNames) {
        this.parameterNames = parameterNames;
    }

    /**
     * Print.
     * @param writer to print to
     * @param expressionDeclarations expression declarations
     * @param formatter for newline-whitespace formatting
     */
    public static void toEPL(StringWriter writer, List<ExpressionDeclaration> expressionDeclarations, EPStatementFormatter formatter) {
        if ((expressionDeclarations == null) || (expressionDeclarations.isEmpty())) {
            return;
        }

        for (ExpressionDeclaration part : expressionDeclarations) {
            if (part.getName() == null) {
                continue;
            }
            formatter.beginExpressionDecl(writer);
            part.toEPL(writer);
        }
    }

    /**
     * Print part.
     * @param writer to write to
     */
    public void toEPL(StringWriter writer) {
        writer.append("expression ");
        writer.append(name);
        writer.append(" {");
        if (parameterNames != null && parameterNames.size() == 1) {
            writer.append(parameterNames.get(0));
        }
        else if (parameterNames != null && !parameterNames.isEmpty()) {
            String delimiter = "";
            writer.append("(");
            for (String name : parameterNames) {
                writer.append(delimiter);
                writer.append(name);
                delimiter = ", ";
            }
            writer.append(")");
        }

        if (parameterNames != null && !parameterNames.isEmpty()) {
            writer.append(" => ");
        }
        if (expression != null) {
            expression.toEPL(writer, ExpressionPrecedenceEnum.MINIMUM);
        }
        writer.append("}");
    }
}
