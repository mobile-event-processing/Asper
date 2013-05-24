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
import com.espertech.esper.epl.core.EngineImportService;
import com.espertech.esper.epl.generated.EsperEPL2GrammarParser;
import com.espertech.esper.epl.spec.*;
import org.antlr.runtime.tree.Tree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class ASTGraphHelper
{
    private static final Log log = LogFactory.getLog(ASTGraphHelper.class);

    public static CreateDataFlowDesc walkCreateDataFlow(Tree parent, Map<Tree, Object> astGraphNodeMap, EngineImportService engineImportService)
    {
        String graphName = parent.getChild(0).getText();

        List<GraphOperatorSpec> ops = new ArrayList<GraphOperatorSpec>();
        List<CreateSchemaDesc> schemas = new ArrayList<CreateSchemaDesc>();

        for (int i = 1; i < parent.getChildCount(); i++) {
            Tree child = parent.getChild(i);
            if (child.getType() == EsperEPL2GrammarParser.CREATE_SCHEMA_EXPR) {
                schemas.add(ASTCreateSchemaHelper.walkCreateSchema(child));
            }
            else {
                ops.add(parseOp(child, astGraphNodeMap, engineImportService));
            }
        }
        return new CreateDataFlowDesc(graphName, ops, schemas);
    }

    private static GraphOperatorSpec parseOp(Tree parent, Map<Tree, Object> astGraphNodeMap, EngineImportService engineImportService)
    {
        int current = 0;
        String operatorName = parent.getChild(current++).getText();

        GraphOperatorInput input = new GraphOperatorInput();
        if (parent.getChildCount() > current && parent.getChild(current).getType() == EsperEPL2GrammarParser.GOPPARAM) {
            parseParams(parent.getChild(current), input);
            current++;
        }

        GraphOperatorOutput output = new GraphOperatorOutput();
        if (parent.getChildCount() > current && parent.getChild(current).getType() == EsperEPL2GrammarParser.GOPOUT) {
            parseOutput(parent.getChild(current), output);
            current++;
        }

        GraphOperatorDetail detail = null;
        if (parent.getChildCount() > current && parent.getChild(current).getType() == EsperEPL2GrammarParser.GOPCFG) {
            Tree detailRoot = parent.getChild(current);
            Map<String, Object> configs = new LinkedHashMap<String, Object>();
            for (int i = 0; i < detailRoot.getChildCount(); i++) {
                Tree itemRoot = detailRoot.getChild(i);

                String name;
                Object value = astGraphNodeMap.remove(itemRoot);
                if (itemRoot.getType() == EsperEPL2GrammarParser.GOPCFGITM) {
                    name = itemRoot.getChild(0).getText();
                }
                else {
                    name = "select";
                }
                configs.put(name, value);
            }
            detail = new GraphOperatorDetail(configs);
        }

        List<Tree> annotationsTrees = ASTUtil.findAllNodes(parent, EsperEPL2GrammarParser.ANNOTATION);
        List<AnnotationDesc> annotations;
        if (!annotationsTrees.isEmpty()) {
            annotations = new ArrayList<AnnotationDesc>();
            for (Tree tree : annotationsTrees) {
                annotations.add(ASTAnnotationHelper.walk(tree, engineImportService));
            }
        }
        else {
            annotations = Collections.emptyList();
        }

        return new GraphOperatorSpec(operatorName, input, output, detail, annotations);
    }

    private static void parseParams(Tree paramsRoot, GraphOperatorInput input) {
        for (int i = 0; i < paramsRoot.getChildCount(); i++) {
            Tree child = paramsRoot.getChild(i);
            if (child.getType() == EsperEPL2GrammarParser.GOPPARAMITM) {
                String[] streamNames = parseParamsStreamNames(child);
                String aliasName = child.getChild(child.getChildCount() - 1).getType() == EsperEPL2GrammarParser.AS ?
                        child.getChild(child.getChildCount() - 1).getChild(0).getText() : null;
                input.getStreamNamesAndAliases().add(new GraphOperatorInputNamesAlias(streamNames, aliasName));
            }
        }
    }

    private static String[] parseParamsStreamNames(Tree paramsRoot) {
        List<String> paramNames = new ArrayList<String>(1);
        for (int i = 0; i < paramsRoot.getChildCount(); i++) {
            Tree child = paramsRoot.getChild(i);
            if (child.getType() == EsperEPL2GrammarParser.CLASS_IDENT) {
                paramNames.add(child.getText());
            }
        }
        return paramNames.toArray(new String[paramNames.size()]);
    }

    private static void parseOutput(Tree outputRoot, GraphOperatorOutput output) {
        for (int i = 0; i < outputRoot.getChildCount(); i++) {
            Tree child = outputRoot.getChild(i);
            if (child.getType() == EsperEPL2GrammarParser.GOPOUTITM) {
                String streamName = child.getChild(0).getText();

                List<GraphOperatorOutputItemType> types = new ArrayList<GraphOperatorOutputItemType>();
                for (int childRootId = 1; childRootId < child.getChildCount(); childRootId++) {
                    GraphOperatorOutputItemType type = parseType(child.getChild(childRootId));
                    types.add(type);
                }
                output.getItems().add(new GraphOperatorOutputItem(streamName, types));
            }
        }
    }

    private static GraphOperatorOutputItemType parseType(Tree typeRoot) {

        if (typeRoot.getChild(0).getType() == EsperEPL2GrammarParser.QUESTION) {
            return new GraphOperatorOutputItemType(true, null, null);
        }

        String className = typeRoot.getChild(0).getText();
        List<GraphOperatorOutputItemType> typeParams = new ArrayList<GraphOperatorOutputItemType>();
        for (int childRootId = 1; childRootId < typeRoot.getChildCount(); childRootId++) {
            GraphOperatorOutputItemType type = parseType(typeRoot.getChild(childRootId));
            typeParams.add(type);
        }
        return new GraphOperatorOutputItemType(false, className, typeParams);
    }

}
