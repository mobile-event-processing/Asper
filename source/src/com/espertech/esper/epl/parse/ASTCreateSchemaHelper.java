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

import com.espertech.esper.client.EPException;
import com.espertech.esper.epl.generated.EsperEPL2Ast;
import com.espertech.esper.epl.spec.ColumnDesc;
import com.espertech.esper.epl.spec.CreateSchemaDesc;
import org.antlr.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ASTCreateSchemaHelper {

    public static CreateSchemaDesc walkCreateSchema(Tree node) throws EPException {
        CreateSchemaDesc.AssignedType assignedType = CreateSchemaDesc.AssignedType.NONE;
        if (node.getChildCount() > 1) {
            String keywordNodeText = node.getChild(1).getText();
            assignedType = CreateSchemaDesc.AssignedType.parseKeyword(keywordNodeText);
        }

        return getSchemaDesc(node.getChild(0), assignedType);
    }

    private static CreateSchemaDesc getSchemaDesc(Tree node, CreateSchemaDesc.AssignedType assignedType) throws EPException {
        String schemaName = node.getChild(0).getText();

        List<ColumnDesc> columnTypes = getColTypeList(node);

        // get model-after types (could be multiple for variants)
        Set<String> typeNames = new LinkedHashSet<String>();
        for (int i = 0; i < node.getChildCount(); i++) {
            if (node.getChild(i).getType() == EsperEPL2Ast.VARIANT_LIST) {
                for (int j = 0; j < node.getChild(i).getChildCount(); j++) {
                    typeNames.add(node.getChild(i).getChild(j).getText());
                }
            }
        }

        // get inherited and start timestamp and end timestamps
        String startTimestamp = null;
        String endTimestamp = null;
        Set<String> inherited = new LinkedHashSet<String>();
        Set<String> copyFrom = new LinkedHashSet<String>();
        for (int i = 0; i < node.getChildCount(); i++) {
            Tree p = node.getChild(i);
            if (p.getType() == EsperEPL2Ast.CREATE_SCHEMA_EXPR_QUAL) {
                String childName = p.getChild(0).getText().toLowerCase();
                if (childName.equals("inherits")) {
                    for (int j = 1; j < p.getChildCount(); j++) {
                        if (p.getChild(j).getType() == EsperEPL2Ast.EXPRCOL) {
                            for (int k = 0; k < p.getChild(j).getChildCount(); k++) {
                                inherited.add(p.getChild(j).getChild(k).getText());
                            }
                        }
                    }
                    continue;
                }
                else if (childName.equals("starttimestamp")) {
                    startTimestamp = p.getChild(1).getChild(0).getText();
                    continue;
                }
                else if (childName.equals("endtimestamp")) {
                    endTimestamp = p.getChild(1).getChild(0).getText();
                    continue;
                }
                else if (childName.equals("copyfrom")) {
                    Tree parent = p.getChild(1);
                    for (int j = 0; j < parent.getChildCount(); j++) {
                        copyFrom.add(parent.getChild(j).getText());
                    }
                    continue;
                }
                throw new EPException("Expected 'inherits', 'starttimestamp', 'endtimestamp' or 'copyfrom' keyword after create-schema clause but encountered '" + p.getChild(0).getText() + "'");
            }
        }

        return new CreateSchemaDesc(schemaName, typeNames, columnTypes, inherited, assignedType, startTimestamp, endTimestamp, copyFrom);
    }

    public static List<ColumnDesc> getColTypeList(Tree node)
    {
        List<ColumnDesc> result = new ArrayList<ColumnDesc>();
        for (int nodeNum = 0; nodeNum < node.getChildCount(); nodeNum++) {
            if (node.getChild(nodeNum).getType() == EsperEPL2Ast.CREATE_COL_TYPE_LIST)
            {
                Tree parent = node.getChild(nodeNum);
                for (int i = 0; i < parent.getChildCount(); i++)
                {
                    String name = parent.getChild(i).getChild(0).getText();
                    String type = parent.getChild(i).getChild(1).getText();
                    boolean array = parent.getChild(i).getChildCount() > 2;
                    result.add(new ColumnDesc(name, type, array));
                }
            }
        }
        return result;
    }
}
