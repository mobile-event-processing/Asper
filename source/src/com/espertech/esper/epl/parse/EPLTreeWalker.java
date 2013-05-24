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
import com.espertech.esper.client.ConfigurationInformation;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.core.context.mgr.ContextManagementService;
import com.espertech.esper.core.context.util.ContextDescriptor;
import com.espertech.esper.core.service.EPAdministratorHelper;
import com.espertech.esper.epl.agg.access.AggregationAccessType;
import com.espertech.esper.epl.agg.service.AggregationSupport;
import com.espertech.esper.epl.core.EngineImportException;
import com.espertech.esper.epl.core.EngineImportService;
import com.espertech.esper.epl.core.EngineImportSingleRowDesc;
import com.espertech.esper.epl.core.EngineImportUndefinedException;
import com.espertech.esper.epl.db.DatabasePollingViewableFactory;
import com.espertech.esper.epl.declexpr.ExprDeclaredHelper;
import com.espertech.esper.epl.declexpr.ExprDeclaredNodeImpl;
import com.espertech.esper.epl.declexpr.ExprDeclaredService;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.generated.EsperEPL2Ast;
import com.espertech.esper.epl.script.ExprNodeScript;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.pattern.EvalFactoryNode;
import com.espertech.esper.pattern.PatternNodeFactory;
import com.espertech.esper.pattern.guard.GuardEnum;
import com.espertech.esper.rowregex.*;
import com.espertech.esper.schedule.SchedulingService;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.type.*;
import com.espertech.esper.type.StringValue;
import com.espertech.esper.util.CollectionUtil;
import com.espertech.esper.util.PlaceholderParseException;
import com.espertech.esper.util.PlaceholderParser;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeNodeStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Called during the walks of a EPL expression AST tree as specified in the grammar file.
 * Constructs filter and view specifications etc.
 */
public class EPLTreeWalker extends EsperEPL2Ast
{
    // private holding areas for accumulated info
    private Map<Tree, ExprNode> astExprNodeMap = new HashMap<Tree, ExprNode>();
    private final Stack<Map<Tree, ExprNode>> astExprNodeMapStack;

    private final Map<Tree, EvalFactoryNode> astPatternNodeMap = new HashMap<Tree, EvalFactoryNode>();

    private final Map<Tree, RowRegexExprNode> astRowRegexNodeMap = new HashMap<Tree, RowRegexExprNode>();

    private final Map<Tree, Object> astGOPNodeMap = new HashMap<Tree, Object>();

    private FilterSpecRaw filterSpec;
    private final List<ViewSpec> viewSpecs = new LinkedList<ViewSpec>();

    // AST Walk result
    private List<ExprSubstitutionNode> substitutionParamNodes = new ArrayList<ExprSubstitutionNode>();
    private StatementSpecRaw statementSpec;
    private final Stack<StatementSpecRaw> statementSpecStack;

    private List<SelectClauseElementRaw> propertySelectRaw;
    private PropertyEvalSpec propertyEvalSpec;
    private List<OnTriggerMergeMatched> mergeMatcheds;
    private List<OnTriggerMergeAction> mergeActions;
    private Map<EvalFactoryNode, String> evalNodeExpressions;
    private ContextDescriptor contextDescriptor;

    private final EngineImportService engineImportService;
    private final VariableService variableService;
    private final TimeProvider timeProvider;
    private final ExprEvaluatorContext exprEvaluatorContext;
    private final SelectClauseStreamSelectorEnum defaultStreamSelector;
    private final String engineURI;
    private final ConfigurationInformation configurationInformation;
    private final SchedulingService schedulingService;
    private final PatternNodeFactory patternNodeFactory;
    private final ContextManagementService contextManagementService;
    private final CommonTokenStream tokenStream;
    private final List<String> scriptBodies;
    private final ExprDeclaredService exprDeclaredService;
    private final List<ExpressionScriptProvided> scriptExpressions;
    private final ExpressionDeclDesc expressionDeclarations;

    /**
     * Ctor.
     * @param engineImportService is required to resolve lib-calls into static methods or configured aggregation functions
     * @param variableService for variable access
     * @param input is the tree nodes to walk
     * @param defaultStreamSelector - the configuration for which insert or remove streams (or both) to produce
     * @param engineURI engine URI
     * @param configurationInformation configuration info
     */
    public EPLTreeWalker(TreeNodeStream input,
                         CommonTokenStream tokenStream,
                         EngineImportService engineImportService,
                         VariableService variableService,
                         SchedulingService schedulingService,
                         SelectClauseStreamSelectorEnum defaultStreamSelector,
                         String engineURI,
                         ConfigurationInformation configurationInformation,
                         PatternNodeFactory patternNodeFactory,
                         ContextManagementService contextManagementService,
                         List<String> scriptBodies,
                         ExprDeclaredService exprDeclaredService)
    {
        super(input);
        this.tokenStream = tokenStream;
        this.engineImportService = engineImportService;
        this.variableService = variableService;
        this.defaultStreamSelector = defaultStreamSelector;
        this.timeProvider = schedulingService;
        this.patternNodeFactory = patternNodeFactory;
        this.exprEvaluatorContext = new ExprEvaluatorContextTimeOnly(timeProvider);
        this.engineURI = engineURI;
        this.configurationInformation = configurationInformation;
        this.schedulingService = schedulingService;
        this.contextManagementService = contextManagementService;
        this.scriptBodies = scriptBodies;
        this.exprDeclaredService = exprDeclaredService;

        if (defaultStreamSelector == null)
        {
            throw new IllegalArgumentException("Default stream selector is null");
        }

        statementSpec = new StatementSpecRaw(defaultStreamSelector);
        statementSpecStack = new Stack<StatementSpecRaw>();
        astExprNodeMapStack = new Stack<Map<Tree, ExprNode>>();
        
        // statement-global items
        expressionDeclarations = new ExpressionDeclDesc();
        statementSpec.setExpressionDeclDesc(expressionDeclarations);
        scriptExpressions = new ArrayList<ExpressionScriptProvided>(1);
        statementSpec.setScriptExpressions(scriptExpressions);
    }

    /**
     * Pushes a statement into the stack, creating a new empty statement to fill in.
     * The leave node method for lookup statements pops from the stack.
     * The leave node method for lookup statements pops from the stack.
     */
    protected void pushStmtContext() {
        if (log.isDebugEnabled())
        {
            log.debug(".pushStmtContext");
        }
        statementSpecStack.push(statementSpec);
        astExprNodeMapStack.push(astExprNodeMap);

        statementSpec = new StatementSpecRaw(defaultStreamSelector);
        astExprNodeMap = new HashMap<Tree, ExprNode>();
    }

    /**
     * Returns statement specification.
     * @return statement spec.
     */
    public StatementSpecRaw getStatementSpec()
    {
        return statementSpec;
    }

    /**
     * Leave AST node and process it's type and child nodes.
     * @param node is the node to complete
     * @throws ASTWalkException if the node tree walk operation failed
     */
    protected void leaveNode(Tree node) throws ASTWalkException
    {
        if (log.isDebugEnabled())
        {
            log.debug(".leaveNode " + node);
        }

        switch (node.getType())
        {
            case STREAM_EXPR:
                leaveStreamExpr(node);
                break;
            case EVENT_FILTER_EXPR:
                leaveStreamFilter(node);
                break;
            case PATTERN_FILTER_EXPR:
                leavePatternFilter(node);
                break;
            case PATTERN_INCL_EXPR:
                return;
            case VIEW_EXPR:
                leaveView(node);
                break;
            case SELECTION_EXPR:
                leaveSelectClause(node);
                break;
            case WILDCARD_SELECT:
            	leaveWildcardSelect();
            	break;
            case SELECTION_ELEMENT_EXPR:
                leaveSelectionElement(node);
                break;
            case SELECTION_STREAM:
                leaveSelectionStream(node);
                break;
            case PROPERTY_SELECTION_ELEMENT_EXPR:
                leavePropertySelectionElement(node);
                break;
            case PROPERTY_SELECTION_STREAM:
                leavePropertySelectionStream(node);
                break;
            case PROPERTY_WILDCARD_SELECT:
            	leavePropertyWildcardSelect();
            	break;
            case EVENT_FILTER_PROPERTY_EXPR_ATOM:
            	leavePropertySelectAtom(node);
            	break;
            case EVENT_PROP_EXPR:
                leaveEventPropertyExpr(node);
                break;
            case EVAL_AND_EXPR:
                leaveJoinAndExpr(node);
                break;
            case EVAL_OR_EXPR:
                leaveJoinOrExpr(node);
                break;
            case EVAL_EQUALS_EXPR:
            case EVAL_NOTEQUALS_EXPR:
            case EVAL_IS_EXPR:
            case EVAL_ISNOT_EXPR:
                leaveEqualsExpr(node);
                break;
            case EVAL_EQUALS_GROUP_EXPR:
            case EVAL_NOTEQUALS_GROUP_EXPR:
            case EVAL_IS_GROUP_EXPR:
            case EVAL_ISNOT_GROUP_EXPR:
                leaveEqualsGroupExpr(node);
                break;
            case WHERE_EXPR:
                leaveWhereClause();
                break;
            case NUM_INT:
            case INT_TYPE:
            case LONG_TYPE:
            case BOOL_TYPE:
            case FLOAT_TYPE:
            case DOUBLE_TYPE:
            case STRING_TYPE:
            case NULL_TYPE:
                leaveConstant(node);
                break;
            case SUBSTITUTION:
                leaveSubstitution(node);
                break;
            case STAR:
            case MINUS:
            case PLUS:
            case DIV:
            case MOD:
                leaveMath(node);
                break;
            case BAND:
            case BOR:
            case BXOR:
            	leaveBitWise(node);
            	break;
             case LT:
            case GT:
            case LE:
            case GE:
                leaveRelationalOp(node);
                break;
            case COALESCE:
                leaveCoalesce(node);
                break;
            case NOT_EXPR:
                leaveExprNot(node);
                break;
            case PATTERN_NOT_EXPR:
                leavePatternNot(node);
                break;
            case SUM:
            case AVG:
            case COUNT:
            case MEDIAN:
            case STDDEV:
            case AVEDEV:
            case FIRST_AGGREG:
            case LAST_AGGREG:
            case WINDOW_AGGREG:
                leaveAggregate(node);
                break;
            case DOT_EXPR:
            	leaveDotExpr(node);
                break;
            case LIB_FUNC_CHAIN:
            	leaveLibFunctionChain(node);
                break;
            case LEFT_OUTERJOIN_EXPR:
            case RIGHT_OUTERJOIN_EXPR:
            case FULL_OUTERJOIN_EXPR:
            case INNERJOIN_EXPR:
                leaveOuterInnerJoin(node);
                break;
            case GROUP_BY_EXPR:
                leaveGroupBy(node);
                break;
            case HAVING_EXPR:
                leaveHavingClause();
                break;
            case ORDER_BY_EXPR:
            	break;
            case ORDER_ELEMENT_EXPR:
            	leaveOrderByElement(node);
            	break;
            case EVENT_LIMIT_EXPR:
            case TIMEPERIOD_LIMIT_EXPR:
            case CRONTAB_LIMIT_EXPR:
            case WHEN_LIMIT_EXPR:
            case TERM_LIMIT_EXPR:
            case AFTER_LIMIT_EXPR:
            	leaveOutputLimit(node);
            	break;
            case ROW_LIMIT_EXPR:
            	leaveRowLimit(node);
            	break;
            case INSERTINTO_EXPR:
            	leaveInsertInto(node);
            	break;
            case CONCAT:
            	leaveConcat(node);
            	break;
            case CASE:
                leaveCaseNode(node, false);
                break;
            case CASE2:
                leaveCaseNode(node, true);
                break;
            case EVERY_EXPR:
                leaveEvery(node);
                break;
            case EVERY_DISTINCT_EXPR:
                leaveEveryDistinct(node);
                break;
            case FOLLOWED_BY_EXPR:
                leaveFollowedBy(node);
                break;
            case OR_EXPR:
                leaveOr(node);
                break;
            case AND_EXPR:
                leaveAnd(node);
                break;
            case GUARD_EXPR:
                leaveGuard(node);
                break;
            case OBSERVER_EXPR:
                leaveObserver(node);
                break;
            case MATCH_UNTIL_EXPR:
                leaveMatch(node);
                break;
            case IN_SET:
            case NOT_IN_SET:
                leaveInSet(node);
                break;
            case IN_RANGE:
            case NOT_IN_RANGE:
                leaveInRange(node);
                break;
            case BETWEEN:
            case NOT_BETWEEN:
                leaveBetween(node);
                break;
            case LIKE:
            case NOT_LIKE:
                leaveLike(node);
                break;
            case REGEXP:
            case NOT_REGEXP:
                leaveRegexp(node);
                break;
            case PREVIOUS:
            case PREVIOUSTAIL:
            case PREVIOUSWINDOW:
            case PREVIOUSCOUNT:
                leavePrevious(node);
                break;
            case PRIOR:
                leavePrior(node);
                break;
            case ARRAY_EXPR:
                leaveArray(node);
                break;
            case SUBSELECT_EXPR:
                leaveSubselectRow(node);
                break;
            case EXISTS_SUBSELECT_EXPR:
                leaveSubselectExists(node);
                break;
            case IN_SUBSELECT_EXPR:
            case NOT_IN_SUBSELECT_EXPR:
                leaveSubselectIn(node);
                break;
            case IN_SUBSELECT_QUERY_EXPR:
                leaveSubselectQueryIn(node);
                break;
            case INSTANCEOF:
                leaveInstanceOf(node);
                break;
            case TYPEOF:
                leaveTypeOf(node);
                break;
            case EXISTS:
                leaveExists(node);
                break;
            case ISTREAM:
                leaveIStreamBuiltin(node);
                break;
            case CAST:
                leaveCast(node);
                break;
            case CURRENT_TIMESTAMP:
                leaveTimestamp(node);
                break;
            case CREATE_WINDOW_EXPR:
                leaveCreateWindow(node);
                break;
            case CREATE_INDEX_EXPR:
                leaveCreateIndex(node);
                break;
            case CREATE_SCHEMA_EXPR:
                leaveCreateSchema(node);
                break;
            case CREATE_EXPR:
                leaveCreateExpression(node);
                break;
            case CREATE_WINDOW_SELECT_EXPR:
                leaveCreateWindowSelect();
                break;
            case CREATE_VARIABLE_EXPR:
                leaveCreateVariable(node);
                break;
            case ON_EXPR:
                leaveOnExpr(node);
                break;
            case UPDATE_EXPR:
                leaveUpdateExpr(node);
                break;
            case TIME_PERIOD:
                leaveTimePeriod(node);
                break;
            case NUMBERSETSTAR:
                leaveNumberSetStar(node);
                break;
            case NUMERIC_PARAM_FREQUENCY:
                leaveNumberSetFrequency(node);
                break;
            case NUMERIC_PARAM_RANGE:
                leaveNumberSetRange(node);
                break;
            case NUMERIC_PARAM_LIST:
                leaveNumberSetList(node);
                break;
            case LAST_OPERATOR:
            case LAST:
                leaveLastNumberSetOperator(node);
                break;
            case LW:
                leaveLastWeekdayNumberSetOperator(node);
                break;
            case WEEKDAY_OPERATOR:
                leaveWeekdayNumberSetOperator(node);
                break;
            case OBJECT_PARAM_ORDERED_EXPR:
                leaveObjectParamOrderedExpression(node);
                break;
            case ANNOTATION:
                leaveAnnotation(node);
                break;
            case MATCHREC_MEASURE_ITEM:
                leaveMatchRecognizeMeasureItem(node);
                break;
            case MATCHREC_PATTERN:
                leaveMatchRecognizePattern(node);
                break;
            case MATCHREC_PATTERN_NESTED:
                leaveMatchRecognizePatternNested(node);
                break;
            case MATCHREC_PATTERN_CONCAT:
                leaveMatchRecognizePatternConcat(node);
                break;
            case MATCHREC_PATTERN_ALTER:
                leaveMatchRecognizePatternAlter(node);
                break;
            case MATCHREC_PATTERN_ATOM:
                leaveMatchRecognizePatternAtom(node);
                break;
            case MATCHREC_DEFINE_ITEM:
                leaveMatchRecognizeDefineItem(node);
                break;
            case PARTITIONITEM:
                leaveMatchRecognizePartition(node);
                break;
            case MATCH_RECOGNIZE:
                leaveMatchRecognize(node);
                break;
            case ON_SELECT_EXPR:
                leaveOnSelect(node);
                break;
            case ON_STREAM:
                leaveOnStream(node);
                break;
            case FOR:
                leaveForClause(node);
                break;
            case MERGE_MAT:
            case MERGE_UNM:
                leaveMergeMatchedUnmatched(node);
                break;
            case MERGE_DEL:
                leaveMergeDelClause(node);
                break;
            case MERGE_UPD:
                leaveMergeUpdClause(node);
                break;
            case MERGE_INS:
                leaveMergeInsClause(node);
                break;
            case EXPRESSIONDECL:
                leaveExpressionDecl(node);
                break;
            case NEWKW:
                leaveNewKeyword(node);
                break;
            case CONTEXT:
                leaveContext(node);
                break;
            case CREATE_CTX:
                leaveCreateContext(node);
                break;
            case CREATE_DATAFLOW:
                leaveCreateDataflow(node);
                break;
            case GOPCFGITM:
            case GOPCFGEXP:
            case GOPCFGEPL:
                leaveGraphDetail(node);
                break;
            case JSON_ARRAY:
            case JSON_OBJECT:
                leaveJsonConstant(node);
                break;
            default:
                throw new ASTWalkException("Unhandled node type encountered, type '" + node.getType() +
                        "' with text '" + node.getText() + '\'');
        }

        // For each AST child node of this AST node that generated an ExprNode add the child node to the expression node.
        // This is for automatic expression tree building.
        if (!astExprNodeMap.isEmpty())
        {
            mapChildASTToChildExprNode(node);
        }

        // For each AST child node of this AST node that generated an EvalNode add the EvalNode as a child
        if (!astPatternNodeMap.isEmpty())
        {
            EvalFactoryNode thisPatternNode = astPatternNodeMap.get(node);
            for (int i = 0; i < node.getChildCount(); i++)
            {
                Tree childNode = node.getChild(i);
                EvalFactoryNode childEvalNode = astPatternNodeMap.get(childNode);
                if (childEvalNode != null)
                {
                    thisPatternNode.addChildNode(childEvalNode);
                    astPatternNodeMap.remove(childNode);
                }
            }
        }

        // For each AST child node of this AST node that generated an RowRegexExprNode add the RowRegexExprNode as a child
        if (!astRowRegexNodeMap.isEmpty())
        {
            RowRegexExprNode thisRegexNode = astRowRegexNodeMap.get(node);
            for (int i = 0; i < node.getChildCount(); i++)
            {
                Tree childNode = node.getChild(i);
                RowRegexExprNode childEvalNode = astRowRegexNodeMap.get(childNode);
                if (childEvalNode != null)
                {
                    thisRegexNode.addChildNode(childEvalNode);
                    astRowRegexNodeMap.remove(childNode);
                }
            }
        }

        switch (node.getType())
        {
            case SUM:
            case AVG:
            case COUNT:
            case MEDIAN:
            case STDDEV:
            case AVEDEV:
            case FIRST_AGGREG:
            case LAST_AGGREG:
            case WINDOW_AGGREG:
                postLeaveAggregate(node);
                break;
            default:
                break;
        }
    }

    private void mapChildASTToChildExprNode(Tree node)
    {
        ExprNode thisEvalNode = astExprNodeMap.get(node);
        for (int i = 0; i < node.getChildCount(); i++)
        {
            Tree childNode = node.getChild(i);

            ExprNode childEvalNode = astExprNodeMap.get(childNode);
            // If there was an expression node generated for the child node, and there is a current expression node,
            // add it to the current expression node (thisEvalNode)
            if ((childEvalNode != null) && (thisEvalNode != null))
            {
                thisEvalNode.addChildNode(childEvalNode);
                astExprNodeMap.remove(childNode);
            }
        }
    }

    private void leaveCreateWindow(Tree node)
    {
        log.debug(".leaveCreateWindow");

        String windowName = node.getChild(0).getText();

        String eventName = null;
        Tree eventNameNode = ASTUtil.findFirstNode(node, CLASS_IDENT);
        if (eventNameNode != null)
        {
            eventName = eventNameNode.getText();
        }
        if (eventName == null)
        {
            eventName = "java.lang.Object";
        }

        boolean isRetainUnion = false;
        boolean isRetainIntersection = false;
        for (int i = 0; i < node.getChildCount(); i++)
        {
            if (node.getChild(i).getType() == RETAINUNION)
            {
                isRetainUnion = true;
                break;
            }
            if (node.getChild(i).getType() == RETAININTERSECTION)
            {
                isRetainIntersection = true;
                break;
            }
        }
        StreamSpecOptions streamSpecOptions = new StreamSpecOptions(false,isRetainUnion,isRetainIntersection);

        // handle table-create clause, i.e. (col1 type, col2 type)
        List<ColumnDesc> colums = ASTCreateSchemaHelper.getColTypeList(node);

        boolean isInsert = false;
        ExprNode insertWhereExpr = null;
        Tree insertNode = ASTUtil.findFirstNode(node, INSERT);
        if (insertNode != null)
        {
            isInsert = true;
            if (insertNode.getChildCount() > 0)
            {
                insertWhereExpr = ASTExprHelper.getRemoveExpr(insertNode.getChild(0),  this.astExprNodeMap);
            }
        }

        CreateWindowDesc desc = new CreateWindowDesc(windowName, viewSpecs, streamSpecOptions, isInsert, insertWhereExpr, colums, eventName);
        statementSpec.setCreateWindowDesc(desc);

        // this is good for indicating what is being selected from
        FilterSpecRaw rawFilterSpec = new FilterSpecRaw(eventName, new LinkedList<ExprNode>(), null);
        FilterStreamSpecRaw streamSpec = new FilterStreamSpecRaw(rawFilterSpec, new LinkedList<ViewSpec>(), null, streamSpecOptions);
        statementSpec.getStreamSpecs().add(streamSpec);
    }

    private void leaveCreateIndex(Tree node)
    {
        log.debug(".leaveCreateIndex");

        String indexName = node.getChild(0).getText();
        String windowName = node.getChild(1).getText();

        Tree nodeExpr = node.getChild(2);
        List<CreateIndexItem> columns = new ArrayList<CreateIndexItem>();

        boolean unique = false;
        for (int i = 0; i < nodeExpr.getChildCount(); i++)
        {
            CreateIndexType type = CreateIndexType.HASH;
            Tree child = nodeExpr.getChild(i);
            if (child.getType() == INDEXCOL)
            {
                String columnName = child.getChild(0).getText();
                if (child.getChildCount() == 2) {
                    String typeName = child.getChild(1).getText();
                    try {
                        type = CreateIndexType.valueOf(typeName.toUpperCase());
                    }
                    catch (RuntimeException ex) {
                        throw new ASTWalkException("Invalid column index type '" + typeName + "' encountered, please use any of the following index type names " + Arrays.asList(CreateIndexType.values()));
                    }
                }
                columns.add(new CreateIndexItem(columnName, type));
            }
        }

        int last = node.getChildCount() - 1;
        if (node.getChild(last).getType() == IDENT) {
            String ident = node.getChild(last).getText();
            if (ident.toLowerCase().trim().equals("unique")) {
                unique = true;
            }
            else {
                throw new ASTWalkException("Invalid keyword '" + ident + "' in create-index encountered, expected 'unique'");
            }
        }

        statementSpec.setCreateIndexDesc(new CreateIndexDesc(unique, indexName, windowName, columns));
    }

    private void leaveCreateSchema(Tree node)
    {
        log.debug(".leaveCreateSchema");
        CreateSchemaDesc createSchema = ASTCreateSchemaHelper.walkCreateSchema(node);
        statementSpec.getStreamSpecs().add(new FilterStreamSpecRaw(new FilterSpecRaw(Object.class.getName(), Collections.<ExprNode>emptyList(), null), Collections.<ViewSpec>emptyList(), null, new StreamSpecOptions()));
        statementSpec.setCreateSchemaDesc(createSchema);
    }

    private void leaveCreateExpression(Tree node)
    {
        log.debug(".leaveCreateExpression");
        Pair<ExpressionDeclItem, ExpressionScriptProvided> pair = ASTExpressionDeclHelper.walkExpressionDecl(node.getChild(0), scriptBodies, astExprNodeMap);
        statementSpec.setCreateExpressionDesc(new CreateExpressionDesc(pair));
    }

    private void leaveCreateVariable(Tree node)
    {
        log.debug(".leaveCreateVariable");

        Tree child = node.getChild(0);
        String variableType = child.getText();
        child = node.getChild(1);
        String variableName = child.getText();

        int start = 2;
        boolean constant = false;
        boolean array = false;
        if (node.getChildCount() > start && node.getChild(start).getType() == IDENT) {
            String text = node.getChild(start).getText().toLowerCase().trim();
            if (text.equals("constant") || text.equals("const")) {
                constant = true;
            }
            else {
                throw new EPException("Expected 'constant' or 'const' keyword after create for create-variable syntax but encountered '" + text + "'");
            }
            start++;
        }
        if (node.getChildCount() > start && node.getChild(start).getType() == LBRACK) {
            array = true;
            start++;
        }

        ExprNode assignment = null;
        if (node.getChildCount() > start) {
            child = node.getChild(start);
            assignment = astExprNodeMap.remove(child);
        }

        CreateVariableDesc desc = new CreateVariableDesc(variableType, variableName, assignment, constant, array);
        statementSpec.setCreateVariableDesc(desc);
    }

    private void leaveCreateWindowSelect()
    {
        log.debug(".leaveCreateWindowSelect");
    }

    private void leaveOnExpr(Tree node)
    {
        log.debug(".leaveOnExpr");

        // determine on-delete or on-select
        boolean isOnDelete = false;
        Tree typeChildNode = null;

        for (int i = 0; i < node.getChildCount(); i++)
        {
        	Tree childNode = node.getChild(i);

            if (childNode.getType() == ON_DELETE_EXPR)
            {
                typeChildNode = childNode;
                isOnDelete = true;
            }
            else if (childNode.getType() == ON_SELECT_EXPR)
            {
                typeChildNode = childNode;
            }
            else if (childNode.getType() == ON_UPDATE_EXPR)
            {
                typeChildNode = childNode;
            }
            else if (childNode.getType() == ON_SET_EXPR)
            {
                typeChildNode = childNode;
            }
            else if (childNode.getType() == ON_MERGE_EXPR)
            {
                typeChildNode = childNode;
            }
        }
        if (typeChildNode == null)
        {
            throw new IllegalStateException("Could not determine on-expr type");
        }

        if (typeChildNode.getType() == ON_MERGE_EXPR) {
            String windowName = typeChildNode.getChild(0).getText();
            String asName = null;
            if (typeChildNode.getChild(1).getType() == IDENT) {
                asName = typeChildNode.getChild(1).getText();
            }

            OnTriggerMergeDesc desc = new OnTriggerMergeDesc(windowName, asName, mergeMatcheds == null ? Collections.<OnTriggerMergeMatched>emptyList() : mergeMatcheds);
            statementSpec.setOnTriggerDesc(desc);
        }
        else if (typeChildNode.getType() != ON_SET_EXPR)
        {
            // The ON_EXPR_FROM contains the window name
            UniformPair<String> windowName = getWindowName(typeChildNode);
            boolean deleteAndSelect = typeChildNode.getChild(0).getType() == DELETE;
            if (windowName == null)
            {
                // on the statement spec, the deepest spec is the outermost
                List<OnTriggerSplitStream> splitStreams = new ArrayList<OnTriggerSplitStream>();
                for (int i = 1; i <= statementSpecStack.size() - 1; i++)
                {
                    StatementSpecRaw raw = statementSpecStack.get(i);
                    splitStreams.add(new OnTriggerSplitStream(raw.getInsertIntoDesc(), raw.getSelectClauseSpec(), raw.getFilterExprRootNode()));
                }
                splitStreams.add(new OnTriggerSplitStream(statementSpec.getInsertIntoDesc(), statementSpec.getSelectClauseSpec(), statementSpec.getFilterExprRootNode()));
                if (!statementSpecStack.isEmpty())
                {
                    statementSpec = statementSpecStack.get(0);
                }
                boolean isFirst = isSelectInsertFirst(node);
                statementSpec.setOnTriggerDesc(new OnTriggerSplitStreamDesc(OnTriggerType.ON_SPLITSTREAM, isFirst, splitStreams));
                statementSpecStack.clear();
            }
            else if (typeChildNode.getType() == ON_UPDATE_EXPR) {
                List<OnTriggerSetAssignment> assignments = ASTExprHelper.getOnTriggerSetAssignments(typeChildNode, astExprNodeMap);
                statementSpec.setOnTriggerDesc(new OnTriggerWindowUpdateDesc(windowName.getFirst(), windowName.getSecond(), assignments));
                statementSpec.setFilterExprRootNode(getRemoveFirstByType(typeChildNode, WHERE_EXPR));
            }
            else
            {
                statementSpec.setOnTriggerDesc(new OnTriggerWindowDesc(windowName.getFirst(), windowName.getSecond(), isOnDelete ? OnTriggerType.ON_DELETE : OnTriggerType.ON_SELECT, deleteAndSelect));
            }
        }
        else
        {
            List<OnTriggerSetAssignment> assignments = ASTExprHelper.getOnTriggerSetAssignments(typeChildNode, astExprNodeMap);
            statementSpec.setOnTriggerDesc(new OnTriggerSetDesc(assignments));
        }
    }

    private void leaveOnStream(Tree node)
    {
        log.debug(".leaveOnStream");

        // get optional filter stream as-name
        Tree childNode = node.getChild(1);
        String streamAsName = null;
        if ((childNode != null) && (childNode.getType() == IDENT))
        {
            streamAsName = childNode.getText();
        }

        // get stream to use (pattern or filter)
        StreamSpecRaw streamSpec;
        if (node.getChild(0).getType() == EVENT_FILTER_EXPR)
        {
            streamSpec = new FilterStreamSpecRaw(filterSpec, new ArrayList<ViewSpec>(), streamAsName, new StreamSpecOptions());
        }
        else if (node.getChild(0).getType() == PATTERN_INCL_EXPR)
        {
            if ((astPatternNodeMap.size() > 1) || ((astPatternNodeMap.isEmpty())))
            {
                throw new ASTWalkException("Unexpected AST tree contains zero or more then 1 child elements for root");
            }
            // Get expression node sub-tree from the AST nodes placed so far
            EvalFactoryNode evalNode = astPatternNodeMap.values().iterator().next();
            streamSpec = new PatternStreamSpecRaw(evalNode, evalNodeExpressions, viewSpecs, streamAsName, new StreamSpecOptions());
            if (evalNodeExpressions != null) {
                evalNodeExpressions = new HashMap<EvalFactoryNode, String>();
            }
            astPatternNodeMap.clear();
        }
        else
        {
            throw new IllegalStateException("Invalid AST type node, cannot map to stream specification");
        }

        statementSpec.getStreamSpecs().add(streamSpec);
    }

    private void leaveForClause(Tree node)
    {
        log.debug(".leaveForClause");

        if (statementSpec.getForClauseSpec() == null) {
            statementSpec.setForClauseSpec(new ForClauseSpec());
        }
        String ident = node.getChild(0).getText();
        List<ExprNode> expressions = ASTExprHelper.getExprNodes(node, 1, astExprNodeMap);
        statementSpec.getForClauseSpec().getClauses().add(new ForClauseItemSpec(ident, expressions));
    }

    private void leaveMergeMatchedUnmatched(Tree node)
    {
        log.debug(".leaveMergeMatchedUnmatched");

        boolean matched = node.getType() == MERGE_MAT;
        if (mergeMatcheds == null) {
            mergeMatcheds = new ArrayList<OnTriggerMergeMatched>();
        }
        ExprNode filterSpec = null;
        if (node.getChildCount() > 0) {
            filterSpec = ASTExprHelper.getRemoveExpr(node.getChild(node.getChildCount() - 1), astExprNodeMap);
        }
        mergeMatcheds.add(new OnTriggerMergeMatched(matched, filterSpec, mergeActions));
        mergeActions = null;
    }

    private void leaveMergeDelClause(Tree node)
    {
        log.debug(".leaveMergeDelClause");

        if (mergeActions == null) {
            mergeActions = new ArrayList<OnTriggerMergeAction>();
        }
        Tree whereCondNode = ASTUtil.findFirstNode(node, WHERE_EXPR);
        ExprNode whereCond = whereCondNode != null ? ASTExprHelper.getRemoveExpr(whereCondNode.getChild(0), astExprNodeMap) : null;
        mergeActions.add(new OnTriggerMergeActionDelete(whereCond));
    }

    private void leaveMergeUpdClause(Tree node)
    {
        log.debug(".leaveMergeUpdClause");

        if (mergeActions == null) {
            mergeActions = new ArrayList<OnTriggerMergeAction>();
        }
        Tree whereCondNode = ASTUtil.findFirstNode(node, WHERE_EXPR);
        ExprNode whereCond = whereCondNode != null ? ASTExprHelper.getRemoveExpr(whereCondNode.getChild(0), astExprNodeMap) : null;

        List<OnTriggerSetAssignment> sets = ASTExprHelper.getOnTriggerSetAssignments(node, astExprNodeMap);
        mergeActions.add(new OnTriggerMergeActionUpdate(whereCond, sets));
    }

    private void leaveMergeInsClause(Tree node)
    {
        log.debug(".leaveMergeInsClause");

        Tree whereCondNode = ASTUtil.findFirstNode(node, WHERE_EXPR);
        ExprNode whereCond = whereCondNode != null ? ASTExprHelper.getRemoveExpr(whereCondNode.getChild(0), astExprNodeMap) : null;

        List<SelectClauseElementRaw> expressions = new ArrayList<SelectClauseElementRaw>(statementSpec.getSelectClauseSpec().getSelectExprList());
        statementSpec.getSelectClauseSpec().getSelectExprList().clear();

        Tree optInsertNameNode = ASTUtil.findFirstNode(node, CLASS_IDENT);
        String optionalInsertName = optInsertNameNode != null ? optInsertNameNode.getText() : null;

        List<String> columsList = Collections.emptyList();
        for (int i = 0; i < node.getChildCount(); i++) {
            if (node.getChild(i).getType() == EXPRCOL) {
                columsList = ASTLibHelper.getIdentList(node.getChild(i));
            }
        }

        if (mergeActions == null) {
            mergeActions = new ArrayList<OnTriggerMergeAction>();
        }
        mergeActions.add(new OnTriggerMergeActionInsert(whereCond, optionalInsertName, columsList, expressions));
    }

    private void leaveUpdateExpr(Tree node)
    {
        log.debug(".leaveUpdateExpr");

        String eventTypeName = node.getChild(0).getText();
        FilterStreamSpecRaw streamSpec = new FilterStreamSpecRaw(new FilterSpecRaw(eventTypeName, Collections.<ExprNode>emptyList(), null), new ArrayList<ViewSpec>(1), eventTypeName, new StreamSpecOptions());
        statementSpec.getStreamSpecs().add(streamSpec);

        String optionalStreamName = null;
        if ((node.getChildCount() > 1) && (node.getChild(1).getType() == IDENT))
        {
            optionalStreamName = node.getChild(1).getText();
        }

        List<OnTriggerSetAssignment> assignments = ASTExprHelper.getOnTriggerSetAssignments(node, astExprNodeMap);
        ExprNode whereClause = this.getRemoveFirstByType(node, WHERE_EXPR);
        statementSpec.setUpdateDesc(new UpdateDesc(optionalStreamName, assignments, whereClause));
    }

    private UniformPair<String> getWindowName(Tree typeChildNode)
    {
        String windowName = null;
        String windowStreamName = null;

        for (int i = 0; i < typeChildNode.getChildCount(); i++)
        {
        	Tree child = typeChildNode.getChild(i);
            if (child.getType() == ON_EXPR_FROM)
            {
                windowName = child.getChild(0).getText();
                if (child.getChildCount() > 1)
                {
                    windowStreamName = child.getChild(1).getText();
                }
                break;
            }
        }
        if (windowName == null)
        {
            return null;
        }
        return new UniformPair<String>(windowName, windowStreamName);
    }


    private void leavePrevious(Tree node)
    {
        log.debug(".leavePrevious");

        PreviousType previousType;
        if (node.getType() == PREVIOUS) {
            previousType = PreviousType.PREV;
        }
        else if (node.getType() == PREVIOUSTAIL) {
            previousType = PreviousType.PREVTAIL;
        }
        else if (node.getType() == PREVIOUSWINDOW) {
            previousType = PreviousType.PREVWINDOW;
        }
        else if (node.getType() == PREVIOUSCOUNT) {
            previousType = PreviousType.PREVCOUNT;
        }
        else {
            throw new IllegalStateException("Failed to handle type '" + node.getType() + "'");
        }

        ExprPreviousNode previousNode = new ExprPreviousNode(previousType);
        astExprNodeMap.put(node, previousNode);
    }

    private void leavePrior(Tree node)
    {
        log.debug(".leavePrior");

        ExprPriorNode priorNode = new ExprPriorNode();
        astExprNodeMap.put(node, priorNode);
    }

    private void leaveInstanceOf(Tree node)
    {
        log.debug(".leaveInstanceOf");

        // get class identifiers
        List<String> classes = new ArrayList<String>();
        for (int i = 1; i < node.getChildCount(); i++)
        {
            Tree classIdent = node.getChild(i);
            classes.add(classIdent.getText());
        }

        String idents[] = classes.toArray(new String[classes.size()]);
        ExprInstanceofNode instanceofNode = new ExprInstanceofNode(idents);
        astExprNodeMap.put(node, instanceofNode);
    }

    private void leaveTypeOf(Tree node)
    {
        log.debug(".leaveTypeOf");
        ExprTypeofNode typeofNode = new ExprTypeofNode();
        astExprNodeMap.put(node, typeofNode);
    }

    private void leaveExists(Tree node)
    {
        log.debug(".leaveExists");

        ExprPropertyExistsNode instanceofNode = new ExprPropertyExistsNode();
        astExprNodeMap.put(node, instanceofNode);
    }

    private void leaveIStreamBuiltin(Tree node)
    {
        log.debug(".leaveIStreamBuiltin");

        ExprIStreamNode istreamNode = new ExprIStreamNode();
        astExprNodeMap.put(node, istreamNode);
    }

    private void leaveCast(Tree node)
    {
        log.debug(".leaveCast");

        String classIdent = node.getChild(1).getText();
        ExprCastNode castNode = new ExprCastNode(classIdent);
        astExprNodeMap.put(node, castNode);
    }

    private void leaveTimestamp(Tree node)
    {
        log.debug(".leaveTimestamp");

        ExprTimestampNode timeNode = new ExprTimestampNode();
        astExprNodeMap.put(node, timeNode);
    }

    private void leaveTimePeriod(Tree node)
    {
        log.debug(".leaveTimePeriod");

        ExprTimePeriod timeNode = ASTExprHelper.getTimePeriodExpr(node, astExprNodeMap);
        astExprNodeMap.put(node, timeNode);
    }

    private void leaveNumberSetStar(Tree node)
    {
        log.debug(".leaveNumberSetStar");
        ExprNumberSetWildcard exprNode = new ExprNumberSetWildcard();
        astExprNodeMap.put(node, exprNode);
    }

    private void leaveNumberSetFrequency(Tree node)
    {
        log.debug(".leaveNumberSetFrequency");
        ExprNumberSetFrequency exprNode = new ExprNumberSetFrequency();
        astExprNodeMap.put(node, exprNode);
    }

    private void leaveNumberSetRange(Tree node)
    {
        log.debug(".leaveNumberSetRange");
        ExprNumberSetRange exprNode = new ExprNumberSetRange();
        astExprNodeMap.put(node, exprNode);
    }

    private void leaveNumberSetList(Tree node)
    {
        log.debug(".leaveNumberSetList");
        ExprNumberSetList exprNode = new ExprNumberSetList();
        astExprNodeMap.put(node, exprNode);
    }

    private void leaveLastNumberSetOperator(Tree node)
    {
        log.debug(".leaveLastNumberSetOperator");
        ExprNumberSetCronParam exprNode = new ExprNumberSetCronParam(CronOperatorEnum.LASTDAY);
        astExprNodeMap.put(node, exprNode);
    }

    private void leaveLastWeekdayNumberSetOperator(Tree node)
    {
        log.debug(".leaveLastWeekdayNumberSetOperator");
        ExprNumberSetCronParam exprNode = new ExprNumberSetCronParam(CronOperatorEnum.LASTWEEKDAY);
        astExprNodeMap.put(node, exprNode);
    }

    private void leaveWeekdayNumberSetOperator(Tree node)
    {
        log.debug(".leaveWeekdayNumberSetOperator");
        ExprNumberSetCronParam exprNode = new ExprNumberSetCronParam(CronOperatorEnum.WEEKDAY);
        astExprNodeMap.put(node, exprNode);
    }

    private void leaveObjectParamOrderedExpression(Tree node)
    {
        log.debug(".leaveObjectParamOrderedExpression");

        boolean isDescending = false;
        if ((node.getChildCount() > 1) && (node.getChild(1).getText().toUpperCase().equals("DESC")))
        {
            isDescending = true;
        }
        ExprOrderedExpr exprNode = new ExprOrderedExpr(isDescending);
        astExprNodeMap.put(node, exprNode);
    }

    private void leaveAnnotation(Tree node)
    {
        log.debug(".leaveAnnotation");
        statementSpec.getAnnotations().add(ASTAnnotationHelper.walk(node, this.engineImportService));
    }

    private void leaveArray(Tree node)
    {
        log.debug(".leaveArray");

        ExprArrayNode arrayNode = new ExprArrayNode();
        astExprNodeMap.put(node, arrayNode);
    }

    private void leaveSubselectRow(Tree node)
    {
        log.debug(".leaveSubselectRow");

        StatementSpecRaw currentSpec = popStacks();
        ExprSubselectRowNode subselectNode = new ExprSubselectRowNode(currentSpec);
        astExprNodeMap.put(node, subselectNode);
    }

    private void leaveSubselectExists(Tree node)
    {
        log.debug(".leaveSubselectExists");

        StatementSpecRaw currentSpec = popStacks();
        ExprSubselectNode subselectNode = new ExprSubselectExistsNode(currentSpec);
        astExprNodeMap.put(node, subselectNode);
    }

    private void leaveSubselectIn(Tree node)
    {
        log.debug(".leaveSubselectIn");

        Tree nodeSubquery = node.getChild(1);

        boolean isNot = false;
        if (node.getType() == NOT_IN_SUBSELECT_EXPR)
        {
            isNot = true;
        }

        ExprSubselectInNode subqueryNode = (ExprSubselectInNode) astExprNodeMap.remove(nodeSubquery);
        subqueryNode.setNotIn(isNot);

        astExprNodeMap.put(node, subqueryNode);
    }

    private void leaveSubselectQueryIn(Tree node)
    {
        log.debug(".leaveSubselectQueryIn");

        StatementSpecRaw currentSpec = popStacks();
        ExprSubselectNode subselectNode = new ExprSubselectInNode(currentSpec);
        astExprNodeMap.put(node, subselectNode);
    }

    private StatementSpecRaw popStacks()
    {
        log.debug(".popStacks");

        StatementSpecRaw currentSpec = statementSpec;
        statementSpec = statementSpecStack.pop();

        if (currentSpec.isHasVariables())
        {
            statementSpec.setHasVariables(true);
        }
        if (currentSpec.getReferencedVariables() != null) {
            for (String var : currentSpec.getReferencedVariables()) {
                addVariable(statementSpec, var);
            }
        }

        astExprNodeMap = astExprNodeMapStack.pop();

        return currentSpec;
    }

    /**
     * End processing of the AST tree for stand-alone pattern expressions.
     * @throws ASTWalkException is the walk failed
     */
    protected void endPattern() throws ASTWalkException
    {
        log.debug(".endPattern");

        if ((astPatternNodeMap.size() > 1) || ((astPatternNodeMap.isEmpty())))
        {
            throw new ASTWalkException("Unexpected AST tree contains zero or more then 1 child elements for root");
        }

        // Get expression node sub-tree from the AST nodes placed so far
        EvalFactoryNode evalNode = astPatternNodeMap.values().iterator().next();

        PatternStreamSpecRaw streamSpec = new PatternStreamSpecRaw(evalNode, evalNodeExpressions, new LinkedList<ViewSpec>(), null, new StreamSpecOptions());
        if (evalNodeExpressions != null) {
            evalNodeExpressions = new HashMap<EvalFactoryNode, String>();
        }
        statementSpec.getStreamSpecs().add(streamSpec);
        statementSpec.setSubstitutionParameters(substitutionParamNodes);

        astPatternNodeMap.clear();
    }

    /**
     * End processing of the AST tree, check that expression nodes found their homes.
     * @throws ASTWalkException is the walk failed
     */
    protected void end() throws ASTWalkException
    {
        log.debug(".end");

        if (astExprNodeMap.size() > 1)
        {
            throw new ASTWalkException("Unexpected AST tree contains left over child elements," +
                    " not all expression nodes have been removed from AST-to-expression nodes map");
        }
        if (astPatternNodeMap.size() > 1)
        {
            throw new ASTWalkException("Unexpected AST tree contains left over child elements," +
                    " not all pattern nodes have been removed from AST-to-pattern nodes map");
        }

        statementSpec.setSubstitutionParameters(substitutionParamNodes);
    }

    private void leaveSelectionElement(Tree node) throws ASTWalkException
    {
        log.debug(".leaveSelectionElement");

        if ((astExprNodeMap.size() > 1) || ((astExprNodeMap.isEmpty())))
        {
            throw new ASTWalkException("Unexpected AST tree contains zero or more then 1 child element for root");
        }

        // Get expression node sub-tree from the AST nodes placed so far
        ExprNode exprNode = astExprNodeMap.values().iterator().next();
        astExprNodeMap.clear();

        // Get list element name
        String optionalName = null;
        if (node.getChildCount() > 1)
        {
            optionalName = node.getChild(1).getText();
        }

        // Add as selection element
        statementSpec.getSelectClauseSpec().add(new SelectClauseExprRawSpec(exprNode, optionalName));
    }

    private void leavePropertySelectionElement(Tree node) throws ASTWalkException
    {
        log.debug(".leavePropertySelectionElement");

        if ((astExprNodeMap.size() > 1) || ((astExprNodeMap.isEmpty())))
        {
            throw new ASTWalkException("Unexpected AST tree contains zero or more then 1 child element for root");
        }

        // Get expression node sub-tree from the AST nodes placed so far
        ExprNode exprNode = astExprNodeMap.values().iterator().next();
        astExprNodeMap.clear();

        // Get list element name
        String optionalName = null;
        if (node.getChildCount() > 1)
        {
            optionalName = node.getChild(1).getText();
        }

        // Add as selection element
        if (propertySelectRaw == null)
        {
            propertySelectRaw = new ArrayList<SelectClauseElementRaw>();
        }
        this.propertySelectRaw.add(new SelectClauseExprRawSpec(exprNode, optionalName));
    }

    private void leavePropertySelectionStream(Tree node) throws ASTWalkException
    {
        log.debug(".leavePropertySelectionStream");

        String streamName = node.getChild(0).getText();

        // Get element name
        String optionalName = null;
        if (node.getChildCount() > 1)
        {
            optionalName = node.getChild(1).getText();
        }

        // Add as selection element
        if (propertySelectRaw == null)
        {
            propertySelectRaw = new ArrayList<SelectClauseElementRaw>();
        }
        this.propertySelectRaw.add(new SelectClauseStreamRawSpec(streamName, optionalName));
    }

    private void leaveSelectionStream(Tree node) throws ASTWalkException
    {
        log.debug(".leaveSelectionStream");

        String streamName = node.getChild(0).getText();

        // Get element name
        String optionalName = null;
        if (node.getChildCount() > 1)
        {
            optionalName = node.getChild(1).getText();
        }

        // Add as selection element
        statementSpec.getSelectClauseSpec().add(new SelectClauseStreamRawSpec(streamName, optionalName));
    }

    private void leaveWildcardSelect()
    {
    	log.debug(".leaveWildcardSelect");
        statementSpec.getSelectClauseSpec().add(new SelectClauseElementWildcard());
    }

    private void leavePropertyWildcardSelect()
    {
    	log.debug(".leavePropertyWildcardSelect");
        if (propertySelectRaw == null)
        {
            propertySelectRaw = new ArrayList<SelectClauseElementRaw>();
        }
        this.propertySelectRaw.add(new SelectClauseElementWildcard());
    }

    private void leavePropertySelectAtom(Tree node)
    {
    	log.debug(".leavePropertySelectAtom");

        // initialize if not set
        if (propertyEvalSpec == null)
        {
            propertyEvalSpec = new PropertyEvalSpec();
        }

        // get select clause
        SelectClauseSpecRaw optionalSelectClause = new SelectClauseSpecRaw();
        if (propertySelectRaw != null)
        {
            optionalSelectClause.getSelectExprList().addAll(propertySelectRaw);
            propertySelectRaw = null;
        }

        // get the splitter expression
        ExprNode splitterExpression;
        if (node.getChild(0).getType() == SELECT) {
            splitterExpression = astExprNodeMap.remove(node.getChild(1));
        }
        else {
            splitterExpression = astExprNodeMap.remove(node.getChild(0));
        }

        // get where-clause, if any
        Tree optionalWhereClauseTree = ASTUtil.findFirstNode(node, WHERE_EXPR);
        ExprNode optionalWhereClause = optionalWhereClauseTree == null ? null : astExprNodeMap.remove(optionalWhereClauseTree.getChild(0));

        Tree propertyAsNameTree = ASTUtil.findFirstNode(node, IDENT);
        String optionalAsName = propertyAsNameTree == null ? null : propertyAsNameTree.getText();

        String splitterEventTypeName = null;
        Tree splitterEventTypeNameNode = ASTUtil.findFirstNode(node, ATCHAR);
        if (splitterEventTypeNameNode != null && splitterEventTypeNameNode.getChild(0).getText().equals("type")) {
            splitterEventTypeName = splitterEventTypeNameNode.getChild(1).getText();
        }

        PropertyEvalAtom atom = new PropertyEvalAtom(splitterExpression, splitterEventTypeName, optionalAsName, optionalSelectClause, optionalWhereClause);
        propertyEvalSpec.add(atom);
    }

    private void leaveView(Tree node) throws ASTWalkException
    {
        log.debug(".leaveView");
        String objectNamespace = node.getChild(0).getText();
        String objectName = node.getChild(1).getText();
        List<ExprNode> viewParameters = ASTExprHelper.getExprNodes(node, 2, astExprNodeMap);
        viewSpecs.add(new ViewSpec(objectNamespace, objectName, viewParameters));
    }

    private void leaveMatchRecognizeMeasureItem(Tree node) throws ASTWalkException
    {
        log.debug(".leaveMatchRecognizeMeasureItem");

        if (statementSpec.getMatchRecognizeSpec() == null)
        {
            statementSpec.setMatchRecognizeSpec(new MatchRecognizeSpec());
        }

        Tree currentNode = node.getChild(0);
        ExprNode exprNode = astExprNodeMap.get(currentNode);
        if (exprNode == null)
        {
            throw new IllegalStateException("Expression node for AST node not found for type " + currentNode.getType() + " and text " + currentNode.getText());
        }
        astExprNodeMap.remove(currentNode);

        String name = null;
        if (node.getChildCount() > 1)
        {
            name = node.getChild(1).getText();
        }
        statementSpec.getMatchRecognizeSpec().addMeasureItem(new MatchRecognizeMeasureItem(exprNode, name));
    }

    private void leaveMatchRecognizePatternAtom(Tree node) throws ASTWalkException
    {
        log.debug(".leaveMatchRecognizePatternAtom");

        String first = node.getChild(0).getText();
        RegexNFATypeEnum type = RegexNFATypeEnum.SINGLE;
        if (node.getChildCount() > 2)
        {
            type = RegexNFATypeEnum.fromString(node.getChild(1).getText(), node.getChild(2).getText());
        }
        else if (node.getChildCount() > 1)
        {
            type = RegexNFATypeEnum.fromString(node.getChild(1).getText(), null);
        }

        RowRegexExprNodeAtom item = new RowRegexExprNodeAtom(first, type);
        astRowRegexNodeMap.put(node, item);
    }

    private void leaveMatchRecognizePatternAlter(Tree node) throws ASTWalkException
    {
        log.debug(".leaveMatchRecognizePatternAlter");

        RowRegexExprNodeAlteration alterNode = new RowRegexExprNodeAlteration();
        astRowRegexNodeMap.put(node, alterNode);
    }

    private void leaveMatchRecognizePatternConcat(Tree node) throws ASTWalkException
    {
        RowRegexExprNodeConcatenation concatNode = new RowRegexExprNodeConcatenation();
        astRowRegexNodeMap.put(node, concatNode);
    }

    private void leaveMatchRecognizePatternNested(Tree node) throws ASTWalkException
    {
        RegexNFATypeEnum type = RegexNFATypeEnum.SINGLE;
        if (node.getChildCount() > 2)
        {
            type = RegexNFATypeEnum.fromString(node.getChild(1).getText(), node.getChild(2).getText());
        }
        else if (node.getChildCount() > 1)
        {
            type = RegexNFATypeEnum.fromString(node.getChild(1).getText(), null);
        }
        RowRegexExprNodeNested nestedNode = new RowRegexExprNodeNested(type);
        astRowRegexNodeMap.put(node, nestedNode);
    }

    private void leaveMatchRecognizePattern(Tree node) throws ASTWalkException
    {
        Tree currentNode = node.getChild(0);
        RowRegexExprNode exprNode = this.astRowRegexNodeMap.get(currentNode);
        if (exprNode == null)
        {
            throw new IllegalStateException("Expression node for AST node not found for type " + currentNode.getType() + " and text " + currentNode.getText());
        }
        astRowRegexNodeMap.remove(currentNode);
        statementSpec.getMatchRecognizeSpec().setPattern(exprNode);
    }

    private void leaveMatchRecognizeDefineItem(Tree node) throws ASTWalkException
    {
        log.debug(".leaveMatchRecognizeDefineItem");
        String first = node.getChild(0).getText();

        Tree currentNode = node.getChild(1);
        ExprNode exprNode = astExprNodeMap.get(currentNode);
        if (exprNode == null)
        {
            throw new IllegalStateException("Expression node for AST node not found for type " + currentNode.getType() + " and text " + currentNode.getText());
        }
        astExprNodeMap.remove(currentNode);
        statementSpec.getMatchRecognizeSpec().getDefines().add(new MatchRecognizeDefineItem(first, exprNode));
    }

    private void leaveMatchRecognize(Tree node) throws ASTWalkException
    {
        log.debug(".leaveMatchRecognize");

        boolean allMatches = false;
        for (int i = 0; i < node.getChildCount(); i++)
        {
            if (node.getChild(i).getType() == ALL)
            {
                allMatches = true;
            }
        }

        MatchRecognizeSkipEnum skip;
        for (int i = 0; i < node.getChildCount(); i++)
        {
            if (node.getChild(i).getType() == MATCHREC_AFTER_SKIP)
            {
                skip = ASTMatchRecognizeHelper.parseSkip(node.getChild(i));
                statementSpec.getMatchRecognizeSpec().getSkip().setSkip(skip);
            }
        }

        for (int i = 0; i < node.getChildCount(); i++)
        {
            if (node.getChild(i).getType() == MATCHREC_INTERVAL)
            {
                Tree intervalParent = node.getChild(i);
                if (!intervalParent.getChild(0).getText().toLowerCase().equals("interval"))
                {
                    throw new ASTWalkException("Invalid interval-clause within match-recognize, expecting keyword INTERVAL");
                }
                ExprNode expression = astExprNodeMap.remove(intervalParent.getChild(1));
                ExprTimePeriod timePeriodExpr = (ExprTimePeriod) expression;
                statementSpec.getMatchRecognizeSpec().setInterval(new MatchRecognizeInterval(timePeriodExpr));
            }
        }

        statementSpec.getMatchRecognizeSpec().setAllMatches(allMatches);
    }

    private void leaveOnSelect(Tree node) throws ASTWalkException
    {
        log.debug(".leaveOnSelect");

        for (int i = 0; i < node.getChildCount(); i++)
        {
            if (node.getChild(i).getType() == DISTINCT)
            {
                statementSpec.getSelectClauseSpec().setDistinct(true);
            }
        }
    }

    private void leaveMatchRecognizePartition(Tree node) throws ASTWalkException
    {
        log.debug(".leaveMatchRecognizePartition");
        if (statementSpec.getMatchRecognizeSpec() == null)
        {
            statementSpec.setMatchRecognizeSpec(new MatchRecognizeSpec());
        }
        statementSpec.getMatchRecognizeSpec().getPartitionByExpressions().addAll(ASTExprHelper.getExprNodes(node, 0, astExprNodeMap));
    }

    private void leaveStreamExpr(Tree node)
    {
        log.debug(".leaveStreamExpr");

        // Determine the optional stream name
        // Search for identifier node that carries the stream name in an "from Class.win:time().std:getEnumerationSource() as StreamName"
        Tree streamNameNode = null;
        for (int i = 1; i < node.getChildCount(); i++)
        {
            Tree child = node.getChild(i);
            if (child.getType() == IDENT)
            {
                streamNameNode = child;
                break;
            }
        }
        String streamName = null;
        if (streamNameNode != null)
        {
            streamName = streamNameNode.getText();
        }

        // The first child node may be a "stream" keyword
        boolean isUnidirectional = false;
        boolean isRetainUnion = false;
        boolean isRetainIntersection = false;
        for (int i = 0; i < node.getChildCount(); i++)
        {
            if (node.getChild(i).getType() == UNIDIRECTIONAL)
            {
                isUnidirectional = true;
                break;
            }
            if (node.getChild(i).getType() == RETAINUNION)
            {
                isRetainUnion = true;
                break;
            }
            if (node.getChild(i).getType() == RETAININTERSECTION)
            {
                isRetainIntersection = true;
                break;
            }
        }

        // Convert to a stream specification instance
        StreamSpecRaw streamSpec;
        StreamSpecOptions options = new StreamSpecOptions(isUnidirectional, isRetainUnion, isRetainIntersection);

        // If the first subnode is a filter node, we have a filter stream specification
        if (node.getChild(0).getType() == EVENT_FILTER_EXPR)
        {
            streamSpec = new FilterStreamSpecRaw(filterSpec, viewSpecs, streamName, options);
        }
        else if (node.getChild(0).getType() == PATTERN_INCL_EXPR)
        {
            if ((astPatternNodeMap.size() > 1) || ((astPatternNodeMap.isEmpty())))
            {
                throw new ASTWalkException("Unexpected AST tree contains zero or more then 1 child elements for root");
            }

            // Get expression node sub-tree from the AST nodes placed so far
            EvalFactoryNode evalNode = astPatternNodeMap.values().iterator().next();

            streamSpec = new PatternStreamSpecRaw(evalNode, evalNodeExpressions, viewSpecs, streamName, options);
            if (evalNodeExpressions != null) {
                evalNodeExpressions = new HashMap<EvalFactoryNode, String>();
            }
            astPatternNodeMap.clear();
        }
        else if (node.getChild(0).getType() == DATABASE_JOIN_EXPR)
        {
            Tree dbrootNode = node.getChild(0);
            String dbName = dbrootNode.getChild(0).getText();
            String sqlWithParams = StringValue.parseString(dbrootNode.getChild(1).getText().trim());

            // determine if there is variables used
            List<PlaceholderParser.Fragment> sqlFragments;
            try
            {
                sqlFragments = PlaceholderParser.parsePlaceholder(sqlWithParams);
                for (PlaceholderParser.Fragment fragment : sqlFragments)
                {
                    if (!(fragment instanceof PlaceholderParser.ParameterFragment)) {
                        continue;
                    }

                    // Parse expression, store for substitution parameters
                    String expression = fragment.getValue();
                    if (expression.toUpperCase().equals(DatabasePollingViewableFactory.SAMPLE_WHERECLAUSE_PLACEHOLDER)) {
                        continue;
                    }

                    if (expression.trim().length() == 0) {
                        throw new ASTWalkException("Missing expression within ${...} in SQL statement");
                    }
                    String toCompile = "select * from java.lang.Object where " + expression;
                    StatementSpecRaw raw = EPAdministratorHelper.compileEPL(toCompile, expression, false, null, SelectClauseStreamSelectorEnum.ISTREAM_ONLY,
                            engineImportService, variableService, schedulingService, engineURI, configurationInformation, patternNodeFactory, contextManagementService, exprDeclaredService);

                    if ((raw.getSubstitutionParameters() != null) && (raw.getSubstitutionParameters().size() > 0)) {
                        throw new ASTWalkException("EPL substitution parameters are not allowed in SQL ${...} expressions, consider using a variable instead");
                    }

                    if (raw.isHasVariables()) {
                        statementSpec.setHasVariables(true);
                    }

                    // add expression
                    if (statementSpec.getSqlParameters() == null) {
                        statementSpec.setSqlParameters(new HashMap<Integer, List<ExprNode>>());
                    }
                    List<ExprNode> listExp = statementSpec.getSqlParameters().get(statementSpec.getStreamSpecs().size());
                    if (listExp == null) {
                        listExp = new ArrayList<ExprNode>();
                        statementSpec.getSqlParameters().put(statementSpec.getStreamSpecs().size(), listExp);
                    }
                    listExp.add(raw.getFilterRootNode());
                }
            }
            catch (PlaceholderParseException ex)
            {
                log.warn("Failed to parse SQL text '" + sqlWithParams + "' :" + ex.getMessage());
                // Let the view construction handle the validation
            }

            String sampleSQL = null;
            if (dbrootNode.getChildCount() > 2)
            {
                sampleSQL = dbrootNode.getChild(2).getText();
                sampleSQL = StringValue.parseString(sampleSQL.trim());
            }

            streamSpec = new DBStatementStreamSpec(streamName, viewSpecs, dbName, sqlWithParams, sampleSQL);
        }
        else if (node.getChild(0).getType() == METHOD_JOIN_EXPR)
        {
            Tree methodRootNode = node.getChild(0);
            String prefixIdent = methodRootNode.getChild(0).getText();
            String className = methodRootNode.getChild(1).getText();

            int indexDot = className.lastIndexOf('.');
            String classNamePart;
            String methodNamePart;
            if (indexDot == -1)
            {
                classNamePart = className;
                methodNamePart = null;
            }
            else
            {
                classNamePart = className.substring(0, indexDot);
                methodNamePart = className.substring(indexDot + 1);
            }
            List<ExprNode> exprNodes = ASTExprHelper.getExprNodes(methodRootNode, 2, astExprNodeMap);

            streamSpec = new MethodStreamSpec(streamName, viewSpecs, prefixIdent, classNamePart, methodNamePart, exprNodes);
        }
        else
        {
            throw new ASTWalkException("Unexpected AST child node to stream expression, type=" + node.getChild(0).getType());
        }
        viewSpecs.clear();
        statementSpec.getStreamSpecs().add(streamSpec);
    }

    private void leaveEventPropertyExpr(Tree node)
    {
        log.debug(".leaveEventPropertyExpr");

        if (node.getChildCount() == 0)
        {
            throw new IllegalStateException("Empty event property expression encountered");
        }

        ExprNode exprNode;
        String propertyName;

        // The stream name may precede the event property name, but cannot be told apart from the property name:
        //      s0.p1 could be a nested property, or could be stream 's0' and property 'p1'

        // A single entry means this must be the property name.
        // And a non-simple property means that it cannot be a stream name.
        if ((node.getChildCount() == 1) || (node.getChild(0).getType() != EVENT_PROP_SIMPLE))
        {
            propertyName = ASTFilterSpecHelper.getPropertyName(node, 0);
            exprNode = new ExprIdentNodeImpl(propertyName);

            Pair<String, String> mappedPropertyPair = ASTFilterSpecHelper.getMappedPropertyPair(node);
            if (mappedPropertyPair != null) {
                List<ExprNode> params = Collections.<ExprNode>singletonList(new ExprConstantNodeImpl(mappedPropertyPair.getSecond()));
                ExprNodeScript scriptNode = ExprDeclaredHelper.getExistsScript(getDefaultDialect(), mappedPropertyPair.getFirst(), params, scriptExpressions, exprDeclaredService);
                if (scriptNode != null) {
                    exprNode = scriptNode;
                }
            }
        }
        // --> this is more then one child node, and the first child node is a simple property
        // we may have a stream name in the first simple property, or a nested property
        // i.e. 's0.p0' could mean that the event has a nested property to 's0' of name 'p0', or 's0' is the stream name
        else
        {
            String leadingIdentifier = node.getChild(0).getChild(0).getText();
            String streamOrNestedPropertyName = ASTFilterSpecHelper.escapeDot(leadingIdentifier);
            propertyName = ASTFilterSpecHelper.getPropertyName(node, 1);

            if (variableService.getReader(leadingIdentifier) != null)
            {
                exprNode = new ExprVariableNodeImpl(leadingIdentifier + "." + propertyName, variableService);
                statementSpec.setHasVariables(true);
                addVariable(statementSpec, propertyName);
            }
            else if (contextDescriptor != null && contextDescriptor.getContextPropertyRegistry().isContextPropertyPrefix(streamOrNestedPropertyName)) {
                exprNode = new ExprContextPropertyNode(propertyName);
            }
            else {
                exprNode = new ExprIdentNodeImpl(propertyName, streamOrNestedPropertyName);
            }
        }

        if (variableService.getReader(propertyName) != null)
        {
            exprNode = new ExprVariableNodeImpl(propertyName, variableService);
            statementSpec.setHasVariables(true);
            addVariable(statementSpec, propertyName);
        }

        astExprNodeMap.put(node, exprNode);
    }

    private void addVariable(StatementSpecRaw statementSpec, String propertyName) {
        if (statementSpec.getReferencedVariables() == null) {
            statementSpec.setReferencedVariables(new HashSet<String>());
        }
        statementSpec.getReferencedVariables().add(propertyName);
    }

    private void leaveLibFunctionOld(Tree parent, Tree node)
    {
    	log.debug(".leaveLibFunctionOld");

        String childNodeText = node.getChild(0).getText();
        if (node.getChild(0).getType() == CLASS_IDENT)
        {
            String className = node.getChild(0).getText();
            List<ExprChainedSpec> chained = getLibFuncChain(parent);
            chained.add(0, new ExprChainedSpec(className, Collections.<ExprNode>emptyList(), true));
            astExprNodeMap.put(node, new ExprDotNode(chained, configurationInformation.getEngineDefaults().getExpression().isDuckTyping(), configurationInformation.getEngineDefaults().getExpression().isUdfCache()));
            return;
        }

        boolean isDistinct = false;
        if ((node.getChild(1) != null) && (node.getChild(1).getType() == DISTINCT))
        {
            isDistinct = true;
        }

        // try plug-in single-row function
        try
        {
            Pair<Class, EngineImportSingleRowDesc> classMethodPair = engineImportService.resolveSingleRow(childNodeText);
            List<ExprChainedSpec> spec = new ArrayList<ExprChainedSpec>();
            List<ExprNode> childExpressions = ASTLibHelper.getExprNodesLibFunc(0, node, astExprNodeMap);
            spec.add(new ExprChainedSpec(classMethodPair.getSecond().getMethodName(), childExpressions, true));
            astExprNodeMap.put(node, new ExprPlugInSingleRowNode(childNodeText, classMethodPair.getFirst(), spec, classMethodPair.getSecond()));
            return;
        }
        catch (EngineImportUndefinedException e)
        {
            // Not an single-row function
        }
        catch (EngineImportException e)
        {
            throw new IllegalStateException("Error resolving single-row function: " + e.getMessage(), e);
        }

        // try plug-in aggregation function
        try
        {
            AggregationFunctionFactory aggregationFactory = engineImportService.resolveAggregationFactory(childNodeText);
            astExprNodeMap.put(node, new ExprPlugInAggFunctionFactoryNode(isDistinct, aggregationFactory, childNodeText));
            return;
        }
        catch (EngineImportUndefinedException e)
        {
            // Not an aggretaion function
        }
        catch (EngineImportException e)
        {
            throw new IllegalStateException("Error resolving aggregation: " + e.getMessage(), e);
        }

        // try plug-in aggregation function (AggregationSupport, deprecated)
        try
        {
            AggregationSupport aggregation = engineImportService.resolveAggregation(childNodeText);
            astExprNodeMap.put(node, new ExprPlugInAggFunctionNode(isDistinct, aggregation, childNodeText));
            return;
        }
        catch (EngineImportUndefinedException e)
        {
            // Not an aggretaion function
        }
        catch (EngineImportException e)
        {
            throw new IllegalStateException("Error resolving aggregation: " + e.getMessage(), e);
        }

        // special case for min,max
        if ((childNodeText.toLowerCase().equals("max")) || (childNodeText.toLowerCase().equals("min")) ||
            (childNodeText.toLowerCase().equals("fmax")) || (childNodeText.toLowerCase().equals("fmin")))
        {
            handleMinMax(node);
            return;
        }

        // try built-in expanded set of aggregation functions
        ExprNode extentedBuiltIn = engineImportService.resolveAggExtendedBuiltin(childNodeText, isDistinct);
        if (extentedBuiltIn != null)
        {
            astExprNodeMap.put(node, extentedBuiltIn);
            return;
        }

        // try expression declaration local statement
        String expressionName = node.getChild(0).getText();
        List<ExprChainedSpec> expressionChain = getLibFuncChain(parent);
        ExprDeclaredNodeImpl declaredNode = ExprDeclaredHelper.getExistsDeclaredExpr(expressionName, expressionChain.get(0).getParameters(), expressionDeclarations.getExpressions(), exprDeclaredService);
        if (declaredNode != null) {
            astExprNodeMap.put(node, declaredNode);
            return;
        }

        // try scripting expression
        ExprNodeScript scriptNode = ExprDeclaredHelper.getExistsScript(getDefaultDialect(), expressionName, expressionChain.get(0).getParameters(), scriptExpressions, exprDeclaredService);
        if (scriptNode != null) {
            astExprNodeMap.put(node, scriptNode);
            return;
        }

        // Could be a mapped property with an expression-parameter "mapped(expr)" or array property with an expression-parameter "array(expr)".
        astExprNodeMap.put(node, new ExprDotNode(Collections.singletonList(expressionChain.get(0)), false, false));
    }

    private void leaveDotExpr(Tree node)
    {
    	log.debug(".leaveDotExpr");
        List<ExprChainedSpec> chainSpec = getLibFuncChain(node);
        astExprNodeMap.put(node, new ExprDotNode(chainSpec, configurationInformation.getEngineDefaults().getExpression().isDuckTyping(),
                configurationInformation.getEngineDefaults().getExpression().isUdfCache()));
    }

    private void leaveLibFunctionChain(Tree node)
    {
    	log.debug(".leaveLibFunctionChain");

        // Single chain can include a class name or property name.
        // As the current node does not generate any expression for this 1-element chain, forward expression to this node.
        if (node.getChildCount() == 1) {
            leaveLibFunctionOld(node, node.getChild(0));
            mapChildASTToChildExprNode(node.getChild(0));
            ExprNode generated = astExprNodeMap.remove(node.getChild(0));
            astExprNodeMap.put(node, generated);
            return;
        }

        String className = node.getChild(0).getChild(0).getText();
        List<ExprChainedSpec> chained = this.getLibFuncChain(node);

        // try plug-in single-row function
        try
        {
            Pair<Class, EngineImportSingleRowDesc> classMethodPair = engineImportService.resolveSingleRow(className);
            chained.get(0).setName(classMethodPair.getSecond().getMethodName());
            astExprNodeMap.put(node, new ExprPlugInSingleRowNode(className, classMethodPair.getFirst(), chained, classMethodPair.getSecond()));
            return;
        }
        catch (EngineImportUndefinedException e)
        {
            // Not an single-row function
        }
        catch (EngineImportException e)
        {
            throw new IllegalStateException("Error resolving single-row function: " + e.getMessage(), e);
        }

        // if the class name is the first
        boolean duckType = configurationInformation.getEngineDefaults().getExpression().isDuckTyping();
        boolean udfCache = configurationInformation.getEngineDefaults().getExpression().isUdfCache();
        if (!className.equals(chained.get(0).getName())) {
            chained.add(0, new ExprChainedSpec(className, Collections.<ExprNode>emptyList(), true));
        }

        ExprDotNode dotNode = new ExprDotNode(chained, duckType, udfCache);

        // try expression declaration local statement
        String name = chained.get(0).getName();
        ExprDeclaredNodeImpl declaredNode = ExprDeclaredHelper.getExistsDeclaredExpr(name, chained.get(0).getParameters(), expressionDeclarations.getExpressions(), exprDeclaredService);
        if (declaredNode != null) {
            dotNode.addChildNode(declaredNode);
            chained.remove(0);
        }
        else {
            ExprNodeScript scriptNode = ExprDeclaredHelper.getExistsScript(getDefaultDialect(), name, chained.get(0).getParameters(), scriptExpressions, exprDeclaredService);
            if (scriptNode != null) {
                dotNode.addChildNode(scriptNode);
                chained.remove(0);
            }
        }

        astExprNodeMap.put(node, dotNode);
    }

    private void leaveEqualsExpr(Tree node)
    {
        log.debug(".leaveEqualsExpr");

        boolean isNot = false;
        if (node.getType() == EVAL_NOTEQUALS_EXPR || node.getType() == EVAL_ISNOT_EXPR)
        {
            isNot = true;
        }

        boolean isIs = false;
        if (node.getType() == EVAL_IS_EXPR || node.getType() == EVAL_ISNOT_EXPR)
        {
            isIs = true;
        }

        ExprEqualsNode identNode = new ExprEqualsNodeImpl(isNot, isIs);
        astExprNodeMap.put(node, identNode);
    }

    private void leaveEqualsGroupExpr(Tree node)
    {
        log.debug(".leaveEqualsGroupExpr");

        boolean isNot = false;
        if (node.getType() == EVAL_NOTEQUALS_GROUP_EXPR)
        {
            isNot = true;
        }

        boolean isAll = false;
        if (node.getChild(1).getType() == ALL)
        {
            isAll = true;
        }

        if ((node.getChildCount() > 2) && (node.getChild(2).getType() == SUBSELECT_GROUP_EXPR))
        {
            StatementSpecRaw currentSpec = popStacks();
            ExprSubselectAllSomeAnyNode subselectNode = new ExprSubselectAllSomeAnyNode(currentSpec, isNot, isAll, null);
            astExprNodeMap.put(node, subselectNode);
        }
        else
        {
            ExprEqualsAllAnyNode groupNode = new ExprEqualsAllAnyNode(isNot, isAll);
            astExprNodeMap.put(node, groupNode);
        }
    }

    private void leaveJoinAndExpr(Tree node)
    {
        log.debug(".leaveJoinAndExpr");
        ExprAndNode identNode = new ExprAndNodeImpl();
        astExprNodeMap.put(node, identNode);
    }

    private void leaveJoinOrExpr(Tree node)
    {
        log.debug(".leaveJoinOrExpr");
        ExprOrNode identNode = new ExprOrNode();
        astExprNodeMap.put(node, identNode);
    }

    private void leaveConstant(Tree node)
    {
        log.debug(".leaveConstant value '" + node.getText() + "'");
        ExprConstantNode constantNode = new ExprConstantNodeImpl(ASTConstantHelper.parse(node));
        astExprNodeMap.put(node, constantNode);
    }

    private void leaveJsonConstant(Tree node)
    {
        log.debug(".leaveJsonConstant value '" + node.getText() + "'");
        ExprConstantNode constantNode = new ExprConstantNodeImpl(ASTJsonHelper.walk(node));
        astExprNodeMap.put(node, constantNode);
    }

    private void leaveSubstitution(Tree node)
    {
        log.debug(".leaveSubstitution");

        // Add the substitution parameter node, for later replacement
        int currentSize = this.substitutionParamNodes.size();
        ExprSubstitutionNode substitutionNode = new ExprSubstitutionNode(currentSize + 1);
        substitutionParamNodes.add(substitutionNode);

        astExprNodeMap.put(node, substitutionNode);
    }

    private void leaveMath(Tree node)
    {
        log.debug(".leaveMath");

        MathArithTypeEnum mathArithTypeEnum;

        switch (node.getType())
        {
            case DIV :
                mathArithTypeEnum = MathArithTypeEnum.DIVIDE;
                break;
            case STAR :
                mathArithTypeEnum = MathArithTypeEnum.MULTIPLY;
                break;
            case PLUS :
                mathArithTypeEnum = MathArithTypeEnum.ADD;
                break;
            case MINUS :
                mathArithTypeEnum = MathArithTypeEnum.SUBTRACT;
                break;
            case MOD :
                mathArithTypeEnum = MathArithTypeEnum.MODULO;
                break;
            default :
                throw new IllegalArgumentException("Node type " + node.getType() + " not a recognized math node type");
        }

        ExprMathNode mathNode = new ExprMathNode(mathArithTypeEnum,
                configurationInformation.getEngineDefaults().getExpression().isIntegerDivision(),
                configurationInformation.getEngineDefaults().getExpression().isDivisionByZeroReturnsNull());
        astExprNodeMap.put(node, mathNode);
    }

    // Min/Max nodes can be either an aggregate or a per-row function depending on the number or arguments
    private void handleMinMax(Tree libNode)
    {
        log.debug(".handleMinMax");

        // Determine min or max
        Tree childNode = libNode.getChild(0);
        String childNodeText = childNode.getText().toLowerCase();
        MinMaxTypeEnum minMaxTypeEnum;
        boolean filtered = childNodeText.startsWith("f");
        if (childNodeText.equals("min") || childNodeText.equals("fmin"))
        {
            minMaxTypeEnum = MinMaxTypeEnum.MIN;
        }
        else if (childNodeText.equals("max") || childNodeText.equals("fmax"))
        {
            minMaxTypeEnum = MinMaxTypeEnum.MAX;
        }
        else
        {
            throw new IllegalArgumentException("Node type " + childNode.getType() + ' ' + childNode.getText() + " not a recognized min max node");
        }

        // Determine distinct or not
        Tree nextNode = libNode.getChild(1);
        boolean isDistinct = false;
        if (nextNode.getType() == DISTINCT)
        {
            isDistinct = true;
        }

        // Error if more then 3 nodes with distinct since it's an aggregate function
        if ((libNode.getChildCount() > 4) && (isDistinct) && !filtered)
        {
            throw new ASTWalkException("The distinct keyword is not valid in per-row min and max " +
                    "functions with multiple sub-expressions");
        }

        ExprNode minMaxNode;
        if ((!isDistinct) && (libNode.getChildCount() > 3) && !filtered)
        {
            // use the row function
            minMaxNode = new ExprMinMaxRowNode(minMaxTypeEnum);
        }
        else
        {
            // use the aggregation function
            minMaxNode = new ExprMinMaxAggrNode(isDistinct, minMaxTypeEnum, filtered);
        }
        astExprNodeMap.put(libNode, minMaxNode);
    }

    private void leaveCoalesce(Tree node)
    {
        log.debug(".leaveCoalesce");

        ExprNode coalesceNode = new ExprCoalesceNode();
        astExprNodeMap.put(node, coalesceNode);
    }

    private void leaveAggregate(Tree node)
    {
        log.debug(".leaveAggregate");

        boolean isDistinct = false;
        if ((node.getChild(0) != null) && (node.getChild(0).getType() == DISTINCT))
        {
            isDistinct = true;
        }

        // NOTE: Also see "postLeaveAggregate" below which appends the filter expression
        boolean hasFilter = ASTUtil.findFirstNode(node, AGG_FILTER_EXPR) != null;

        ExprAggregateNode aggregateNode;
        ExprNode childNode = null;

        switch (node.getType())
        {
            case AVG:
                aggregateNode = new ExprAvgNode(isDistinct, hasFilter);
                break;
            case SUM:
                aggregateNode = new ExprSumNode(isDistinct, hasFilter);
                break;
            case COUNT:
                aggregateNode = new ExprCountNode(isDistinct, hasFilter);
                break;
            case MEDIAN:
                aggregateNode = new ExprMedianNode(isDistinct, hasFilter);
                break;
            case STDDEV:
                aggregateNode = new ExprStddevNode(isDistinct, hasFilter);
                break;
            case AVEDEV:
                aggregateNode = new ExprAvedevNode(isDistinct, hasFilter);
                break;
            case FIRST_AGGREG:
            case WINDOW_AGGREG:
            case LAST_AGGREG:
                boolean isWildcard = false;
                String streamWildcard = null;
                if (node.getChildCount() > 0 && node.getChild(0).getType() == ACCESS_AGG) {
                    Tree wildcardNode = ASTUtil.findFirstNode(node.getChild(0), PROPERTY_WILDCARD_SELECT);
                    Tree streamWCNode = ASTUtil.findFirstNode(node.getChild(0), PROPERTY_SELECTION_STREAM);
                    if (wildcardNode != null) {
                        isWildcard = true;
                    }
                    else if (streamWCNode != null) {
                        streamWildcard = streamWCNode.getChild(0).getText();
                    }
                    else {
                        childNode = astExprNodeMap.remove(node.getChild(0).getChild(0));
                    }
                }
                else {  // no parameter case
                    isWildcard = true;
                }

                if (node.getType() == FIRST_AGGREG) {
                    aggregateNode = new ExprAccessAggNode(AggregationAccessType.FIRST, isWildcard, streamWildcard);
                }
                else if (node.getType() == WINDOW_AGGREG) {
                    aggregateNode = new ExprAccessAggNode(AggregationAccessType.WINDOW, isWildcard, streamWildcard);
                }
                else {
                    aggregateNode = new ExprAccessAggNode(AggregationAccessType.LAST, isWildcard, streamWildcard);
                }
                break;
            default:
                throw new IllegalArgumentException("Node type " + node.getType() + " not a recognized aggregate node type");
        }

        if (childNode != null) {
            aggregateNode.addChildNode(childNode);
        }
        astExprNodeMap.put(node, aggregateNode);
    }

    private void postLeaveAggregate(Tree node)
    {
        Tree optionalFilterNode = ASTUtil.findFirstNode(node, AGG_FILTER_EXPR);
        if (optionalFilterNode == null) {
            return;
        }
        ExprNode currentAggNode = astExprNodeMap.get(node);
        ExprNode filter = astExprNodeMap.remove(optionalFilterNode.getChild(0));
        currentAggNode.addChildNode(filter);
    }

    private void leaveRelationalOp(Tree node)
    {
        log.debug(".leaveRelationalOp");

        RelationalOpEnum relationalOpEnum;

        switch (node.getType())
        {
            case LT :
                relationalOpEnum = RelationalOpEnum.LT;
                break;
            case GT :
                relationalOpEnum = RelationalOpEnum.GT;
                break;
            case LE :
                relationalOpEnum = RelationalOpEnum.LE;
                break;
            case GE :
                relationalOpEnum = RelationalOpEnum.GE;
                break;
            default :
                throw new IllegalArgumentException("Node type " + node.getType() + " not a recognized relational op node type");
        }

        boolean isAll = false;
        boolean isAny = false;
        if (node.getChild(1).getType() == ALL)
        {
            isAll = true;
        }
        if ((node.getChild(1).getType() == ANY) || (node.getChild(1).getType() == SOME))
        {
            isAny = true;
        }

        ExprNode result;
        if (isAll || isAny)
        {
            if ((node.getChildCount() > 2) && (node.getChild(2).getType() == SUBSELECT_GROUP_EXPR))
            {
                StatementSpecRaw currentSpec = popStacks();
                result = new ExprSubselectAllSomeAnyNode(currentSpec, false, isAll, relationalOpEnum);
            }
            else
            {
                result = new ExprRelationalOpAllAnyNode(relationalOpEnum, isAll);
            }
        }
        else
        {
            result = new ExprRelationalOpNodeImpl(relationalOpEnum);
        }

        astExprNodeMap.put(node, result);
    }

    private void leaveBitWise(Tree node)
    {
        log.debug(".leaveBitWise");

        BitWiseOpEnum bitWiseOpEnum;
        switch (node.getType())
        {
	        case BAND :
	        	bitWiseOpEnum = BitWiseOpEnum.BAND;
	            break;
	        case BOR :
	        	bitWiseOpEnum = BitWiseOpEnum.BOR;
	            break;
	        case BXOR :
	        	bitWiseOpEnum = BitWiseOpEnum.BXOR;
	            break;
	        default :
	            throw new IllegalArgumentException("Node type " + node.getType() + " not a recognized bit wise node type");
        }

	    ExprBitWiseNode bwNode = new ExprBitWiseNode(bitWiseOpEnum);
	    astExprNodeMap.put(node, bwNode);
    }

    private void leaveWhereClause()
    {
        log.debug(".leaveWhereClause");

        if (astExprNodeMap.size() != 1)
        {
            throw new IllegalStateException("Where clause generated zero or more then one expression nodes");
        }

        // Just assign the single root ExprNode not consumed yet
        statementSpec.setFilterRootNode(astExprNodeMap.values().iterator().next());
        astExprNodeMap.clear();
    }

    private void leaveHavingClause()
    {
        log.debug(".leaveHavingClause");

        if (astExprNodeMap.size() != 1)
        {
            throw new IllegalStateException("Having clause generated zero or more then one expression nodes");
        }

        // Just assign the single root ExprNode not consumed yet
        statementSpec.setHavingExprRootNode(astExprNodeMap.values().iterator().next());
        astExprNodeMap.clear();
    }

    private void leaveOutputLimit(Tree node) throws ASTWalkException
    {
        log.debug(".leaveOutputLimit");

        OutputLimitSpec spec = ASTOutputLimitHelper.buildOutputLimitSpec(node, astExprNodeMap, variableService, engineURI, timeProvider, exprEvaluatorContext);
        statementSpec.setOutputLimitSpec(spec);

        if (spec.getVariableName() != null)
        {
            statementSpec.setHasVariables(true);
            addVariable(statementSpec, spec.getVariableName());
        }
    }

    private void leaveRowLimit(Tree node) throws ASTWalkException
    {
        log.debug(".leaveRowLimit");

        RowLimitSpec spec = ASTOutputLimitHelper.buildRowLimitSpec(node);
        statementSpec.setRowLimitSpec(spec);

        if ((spec.getNumRowsVariable() != null) || (spec.getOptionalOffsetVariable() != null))
        {
            statementSpec.setHasVariables(true);
            addVariable(statementSpec, spec.getOptionalOffsetVariable());
        }
    }

    private void leaveOuterInnerJoin(Tree node)
    {
        log.debug(".leaveOuterInnerJoin");

        OuterJoinType joinType;
        switch (node.getType())
        {
            case LEFT_OUTERJOIN_EXPR:
                joinType = OuterJoinType.LEFT;
                break;
            case RIGHT_OUTERJOIN_EXPR:
                joinType = OuterJoinType.RIGHT;
                break;
            case FULL_OUTERJOIN_EXPR:
                joinType = OuterJoinType.FULL;
                break;
            case INNERJOIN_EXPR:
                joinType = OuterJoinType.INNER;
                break;
            default:
                throw new IllegalArgumentException("Node type " + node.getType() + " not a recognized outer join node type");
        }

        // always starts with ON-token, so as to not produce an empty node
        ExprIdentNode left = null;
        ExprIdentNode right = null;
        ExprIdentNode[] addLeftArr = null;
        ExprIdentNode[] addRightArr = null;

        // get subnodes representing the on-expression, if provided
        if (node.getChildCount() > 1) {
            left = (ExprIdentNode) astExprNodeMap.get(node.getChild(1));
            right = (ExprIdentNode) astExprNodeMap.get(node.getChild(2));

            // remove from AST-to-expression node map
            astExprNodeMap.remove(node.getChild(1));
            astExprNodeMap.remove(node.getChild(2));

            // get optional additional
            if (node.getChildCount() > 3) {
                ArrayList<ExprIdentNode> addLeft = new ArrayList<ExprIdentNode>();
                ArrayList<ExprIdentNode> addRight = new ArrayList<ExprIdentNode>();
                for (int i = 3; i < node.getChildCount(); i+=2)
                {
                    Tree child = node.getChild(i);
                    addLeft.add((ExprIdentNode)astExprNodeMap.remove(child));
                    addRight.add((ExprIdentNode)astExprNodeMap.remove(node.getChild(i + 1)));
                }
                addLeftArr = addLeft.toArray(new ExprIdentNode[addLeft.size()]);
                addRightArr = addRight.toArray(new ExprIdentNode[addRight.size()]);
            }
        }

        OuterJoinDesc outerJoinDesc = new OuterJoinDesc(joinType, left, right, addLeftArr, addRightArr);
        statementSpec.getOuterJoinDescList().add(outerJoinDesc);
    }

    private void leaveGroupBy(Tree node)
    {
        log.debug(".leaveGroupBy");

        // there must be some expressions under the group by in our map
        if (astExprNodeMap.size() < 1)
        {
            throw new IllegalStateException("Group-by clause generated no expression nodes");
        }

        // For each child to the group-by AST node there must be a generated ExprNode
        for (int i = 0; i < node.getChildCount(); i++)
        {
        	Tree child = node.getChild(i);
            // get top expression node for the child node
            ExprNode exprNode = astExprNodeMap.get(child);

            if (exprNode == null)
            {
                throw new IllegalStateException("Expression node as a result of group-by child node not found in collection");
            }

            statementSpec.getGroupByExpressions().add(exprNode);
        }

        // Clear the map - all expression node should be gone
        astExprNodeMap.clear();
    }

    private void leaveInsertInto(Tree node)
    {
        log.debug(".leaveInsertInto");

        int count = 0;
        Tree child = node.getChild(count);

        // istream or rstream
        SelectClauseStreamSelectorEnum selector = SelectClauseStreamSelectorEnum.ISTREAM_ONLY;
        if (child.getType() == RSTREAM)
        {
            selector = SelectClauseStreamSelectorEnum.RSTREAM_ONLY;
            child = node.getChild(++count);
        }
        if (child.getType() == IRSTREAM)
        {
            selector = SelectClauseStreamSelectorEnum.RSTREAM_ISTREAM_BOTH;
            child = node.getChild(++count);
        }
        if (child.getType() == ISTREAM)
        {
            child = node.getChild(++count);
        }

        // type name
        String eventTypeName = child.getText();
        InsertIntoDesc insertIntoDesc = new InsertIntoDesc(selector, eventTypeName);

        // optional columns
        child = node.getChild(++count);
        if ((child != null) && (child.getType() == EXPRCOL))
        {
            // Each child to the insert-into AST node represents a column name
            for (int i = 0; i < child.getChildCount(); i++)
            {
                Tree childNode = child.getChild(i);
                insertIntoDesc.add(childNode.getText());
            }
        }

        statementSpec.setInsertIntoDesc(insertIntoDesc);
    }

    private void leaveOrderByElement(Tree node) throws ASTWalkException
    {
        log.debug(".leaveOrderByElement");
        if ((astExprNodeMap.size() > 1) || ((astExprNodeMap.isEmpty())))
        {
            throw new ASTWalkException("Unexpected AST tree contains zero or more then 1 child element for root");
        }

        // Get expression node sub-tree from the AST nodes placed so far
        ExprNode exprNode = astExprNodeMap.values().iterator().next();
        astExprNodeMap.clear();

        // Get optional ascending or descending qualifier
        boolean descending = false;
        if (node.getChildCount() > 1)
        {
            descending = node.getChild(1).getType() == DESC;
        }

        // Add as order-by element
        statementSpec.getOrderByList().add(new OrderByItem(exprNode, descending));
    }

    private void leaveConcat(Tree node)
    {
        ExprConcatNode concatNode = new ExprConcatNode();
        astExprNodeMap.put(node, concatNode);
    }

    private void leaveEvery(Tree node)
    {
        log.debug(".leaveEvery");
        EvalFactoryNode everyNode = this.patternNodeFactory.makeEveryNode();
        addEvalNodeExpression(everyNode, node);
    }

    private void leaveEveryDistinct(Tree node)
    {
        log.debug(".leaveEveryDistinct");
        List<ExprNode> exprNodes = ASTExprHelper.getExprNodes(node.getChild(0), 0, astExprNodeMap);
        EvalFactoryNode everyNode = this.patternNodeFactory.makeEveryDistinctNode(exprNodes);
        addEvalNodeExpression(everyNode, node);
    }

    private void leaveStreamFilter(Tree node)
    {
        log.debug(".leaveStreamFilter");

        // for event streams we keep the filter spec around for use when the stream definition is completed
        filterSpec = ASTExprHelper.walkFilterSpec(node, propertyEvalSpec, astExprNodeMap);

        // set property eval to null
        propertyEvalSpec = null;

        // clear the sub-nodes for the filter since the event property expressions have been processed
        // by building the spec
        astExprNodeMap.clear();
    }

    private void leavePatternFilter(Tree node)
    {
        log.debug(".leavePatternFilter");

        int count = 0;
        Tree startNode = node.getChild(0);
        String optionalPatternTagName = null;
        if (startNode.getType() == IDENT)
        {
            optionalPatternTagName = startNode.getText();
            startNode = node.getChild(++count);
        }

        // Determine event type
        String eventName = startNode.getText();
        count++;

        // get property expression if any
        if ((node.getChildCount() > count) && (node.getChild(count).getType() == EVENT_FILTER_PROPERTY_EXPR))
        {
            ++count;
        }

        Integer consumption = null;
        if ((node.getChildCount() > count) && (node.getChild(count).getType() == ATCHAR))
        {
            Tree filterConsumeAnno = node.getChild(count);
            String name = filterConsumeAnno.getChild(0).getText();
            if (!name.toUpperCase().equals("CONSUME")) {
                throw new EPException("Unexpected pattern filter @ annotation, expecting 'consume' but received '" + name + "'");
            }
            Object number = filterConsumeAnno.getChildCount() < 2 ? null : ASTConstantHelper.parse(filterConsumeAnno.getChild(1));
            if (number != null) {
                consumption = ((Number) number).intValue();
            }
            else {
                consumption = 1;
            }
            count++;
        }

        List<ExprNode> exprNodes = ASTExprHelper.getExprNodes(node, count, astExprNodeMap);

        FilterSpecRaw rawFilterSpec = new FilterSpecRaw(eventName, exprNodes, propertyEvalSpec);
        propertyEvalSpec = null;
        EvalFactoryNode filterNode = patternNodeFactory.makeFilterNode(rawFilterSpec, optionalPatternTagName, consumption);
        addEvalNodeExpression(filterNode, node);
    }

    private void leaveFollowedBy(Tree node)
    {
        log.debug(".leaveFollowedBy");
        ExprNode[] maxExpressions = new ExprNode[node.getChildCount() - 1];
        List<EvalFactoryNode> childNodes = new ArrayList<EvalFactoryNode>();
        for (int i = 0; i < node.getChildCount(); i++) {
            Tree child = node.getChild(i);
            if (child.getType() != FOLLOWED_BY_ITEM) {
                throw new ASTWalkException("Unexpected child node for followed-by item");
            }
            if (i == 0) {
                childNodes.add(astPatternNodeMap.remove(child.getChild(0)));    // first pattern sub-expression cannot have max
            }
            else {
                int current = 0;
                if (child.getChildCount() == 2) {
                    maxExpressions[i - 1] = astExprNodeMap.remove(child.getChild(current));
                    current++;
                }
                childNodes.add(astPatternNodeMap.remove(child.getChild(current)));
            }
        }
        List<ExprNode> expressions = Collections.emptyList();
        if (!CollectionUtil.isAllNullArray(maxExpressions)) {
            expressions = Arrays.asList(maxExpressions); // can contain null elements as max/no-max can be mixed
        }
        EvalFactoryNode fbNode = patternNodeFactory.makeFollowedByNode(expressions, configurationInformation.getEngineDefaults().getPatterns().getMaxSubexpressions() != null);
        fbNode.addChildNodes(childNodes);
        addEvalNodeExpression(fbNode, node);
    }

    private void addEvalNodeExpression(EvalFactoryNode evalNode, Tree node) {
        astPatternNodeMap.put(node, evalNode);
        if (evalNodeExpressions == null) {
            evalNodeExpressions = new HashMap<EvalFactoryNode, String>();
        }
        evalNodeExpressions.put(evalNode, ASTExprHelper.getExpressionText(this.tokenStream, node));
    }

    private void leaveAnd(Tree node)
    {
        log.debug(".leaveAnd");
        EvalFactoryNode andNode = patternNodeFactory.makeAndNode();
        addEvalNodeExpression(andNode, node);
    }

    private void leaveOr(Tree node)
    {
        log.debug(".leaveOr");
        EvalFactoryNode orNode = patternNodeFactory.makeOrNode();
        addEvalNodeExpression(orNode, node);
    }

    private void leaveInSet(Tree node)
    {
        log.debug(".leaveInSet");

        ExprInNode inNode = new ExprInNodeImpl(node.getType() == NOT_IN_SET);
        astExprNodeMap.put(node, inNode);
    }

    private void leaveInRange(Tree node)
    {
        log.debug(".leaveInRange");

        // The second node must be braces
        Tree bracesNode = node.getChild(1);
        if ((bracesNode.getType() != LBRACK) && ((bracesNode.getType() != LPAREN)))
        {
            throw new IllegalStateException("Invalid in-range syntax, no braces but type '" + bracesNode.getType() + "'");
        }
        boolean isLowInclude = bracesNode.getType() == LBRACK;

        // The fifth node must be braces
        bracesNode = node.getChild(4);
        if ((bracesNode.getType() != RBRACK) && ((bracesNode.getType() != RPAREN)))
        {
            throw new IllegalStateException("Invalid in-range syntax, no braces but type '" + bracesNode.getType() + "'");
        }
        boolean isHighInclude = bracesNode.getType() == RBRACK;

        ExprBetweenNode betweenNode = new ExprBetweenNodeImpl(isLowInclude, isHighInclude, node.getType() == NOT_IN_RANGE);
        astExprNodeMap.put(node, betweenNode);
    }

    private void leaveBetween(Tree node)
    {
        log.debug(".leaveBetween");

        ExprBetweenNode betweenNode = new ExprBetweenNodeImpl(true, true, node.getType() == NOT_BETWEEN);
        astExprNodeMap.put(node, betweenNode);
    }

    private void leaveLike(Tree node)
    {
        log.debug(".leaveLike");

        boolean isNot = node.getType() == NOT_LIKE;
        ExprLikeNode likeNode = new ExprLikeNode(isNot);
        astExprNodeMap.put(node, likeNode);
    }

    private void leaveRegexp(Tree node)
    {
        log.debug(".leaveRegexp");

        boolean isNot = node.getType() == NOT_REGEXP;
        ExprRegexpNode regExpNode = new ExprRegexpNode(isNot);
        astExprNodeMap.put(node, regExpNode);
    }

    private void leaveExprNot(Tree node)
    {
        log.debug(".leaveExprNot");
        ExprNotNode notNode = new ExprNotNode();
        astExprNodeMap.put(node, notNode);
    }

    private void leavePatternNot(Tree node)
    {
        log.debug(".leavePatternNot");
        EvalFactoryNode notNode = this.patternNodeFactory.makeNotNode();
        addEvalNodeExpression(notNode, node);
    }

    private void leaveGuard(Tree node) throws ASTWalkException
    {
        log.debug(".leaveGuard");
        String objectNamespace;
        String objectName;
        List<ExprNode> obsParameters;
        if (node.getChild(1).getType() == IDENT && node.getChild(2).getType() == IDENT) {
            objectNamespace = node.getChild(1).getText();
            objectName = node.getChild(2).getText();
            obsParameters = ASTExprHelper.getExprNodes(node, 3, astExprNodeMap);
        }
        else {
            objectNamespace = GuardEnum.WHILE_GUARD.getNamespace();
            objectName = GuardEnum.WHILE_GUARD.getName();
            obsParameters = ASTExprHelper.getExprNodes(node, 1, astExprNodeMap);
        }

        PatternGuardSpec guardSpec = new PatternGuardSpec(objectNamespace, objectName, obsParameters);
        EvalFactoryNode guardNode = patternNodeFactory.makeGuardNode(guardSpec);
        addEvalNodeExpression(guardNode, node);
    }

    private void leaveCaseNode(Tree node, boolean inCase2)
    {
        if (log.isDebugEnabled())
        {
            log.debug(".leaveCase2Node inCase2=" + inCase2);
        }

        if (astExprNodeMap.isEmpty())
        {
            throw new ASTWalkException("Unexpected AST tree contains zero child element for case node");
        }
        if (astExprNodeMap.size() == 1)
        {
            throw new ASTWalkException("AST tree doesn not contain at least when node for case node");
        }

        ExprCaseNode caseNode = new ExprCaseNode(inCase2);
        astExprNodeMap.put(node, caseNode);
    }

    private void leaveExpressionDecl(Tree node)
    {
        if (log.isDebugEnabled())
        {
            log.debug(".leaveExpressionEval");
        }
        Pair<ExpressionDeclItem, ExpressionScriptProvided> pair = ASTExpressionDeclHelper.walkExpressionDecl(node, scriptBodies, astExprNodeMap);
        if (pair.getFirst() != null) {
            expressionDeclarations.add(pair.getFirst());
        }
        else {
            scriptExpressions.add(pair.getSecond());
        }
    }

    private void leaveNewKeyword(Tree node)
    {
        if (log.isDebugEnabled())
        {
            log.debug(".leaveNewKeyword");
        }

        List<String> columnNames = new ArrayList<String>();
        List<ExprNode> expressions = new ArrayList<ExprNode>();
        for (int i = 0; i < node.getChildCount(); i++) {
            Tree child = node.getChild(i);
            if (child.getType() != NEW_ITEM) {
                throw new IllegalStateException("Expected new-item node not found");
            }
            String property = ASTFilterSpecHelper.getPropertyName(child.getChild(0), 0);
            columnNames.add(property);

            ExprNode expr;
            if (child.getChildCount() > 1) {
                expr = astExprNodeMap.remove(child.getChild(1));
            }
            else {
                expr = new ExprIdentNodeImpl(property);
            }
            expressions.add(expr);
        }
        String[] columns = columnNames.toArray(new String[columnNames.size()]);
        ExprNewNode newNode = new ExprNewNode(columns);
        newNode.getChildNodes().addAll(expressions);
        astExprNodeMap.put(node, newNode);
    }

    private void leaveContext(Tree node)
    {
        if (log.isDebugEnabled()) {
            log.debug(".leaveContext");
        }
        String contextName = node.getChild(0).getText();
        statementSpec.setOptionalContextName(contextName);
        contextDescriptor = contextManagementService.getContextDescriptor(contextName);
    }

    private void leaveCreateContext(Tree node)
    {
        if (log.isDebugEnabled()) {
            log.debug(".leaveCreateContext");
        }

        CreateContextDesc contextDesc = ASTContextHelper.walkCreateContext(node, astExprNodeMap, astPatternNodeMap, propertyEvalSpec, filterSpec);
        filterSpec = null;
        propertyEvalSpec = null;
        statementSpec.setCreateContextDesc(contextDesc);
    }

    private void leaveCreateDataflow(Tree node)
    {
        if (log.isDebugEnabled()) {
            log.debug(".leaveCreateDataflow");
        }

        CreateDataFlowDesc graphDesc = ASTGraphHelper.walkCreateDataFlow(node, astGOPNodeMap, engineImportService);
        statementSpec.setCreateDataFlowDesc(graphDesc);
    }

    private void leaveGraphDetail(Tree node)
    {
        if (log.isDebugEnabled()) {
            log.debug(".leaveGraphDetail");
        }

        Object value;
        if (node.getType() == GOPCFGITM) {
            value = astExprNodeMap.remove(node.getChild(1));
        }
        else if (node.getType() == GOPCFGEXP) {
            value = astExprNodeMap.remove(node.getChild(0));
        }
        else {
            StatementSpecRaw newSpec = new StatementSpecRaw(defaultStreamSelector);
            newSpec.getAnnotations().addAll(statementSpec.getAnnotations());

            StatementSpecRaw existingSpec = statementSpec;
            value = existingSpec;
            existingSpec.setAnnotations(Collections.<AnnotationDesc>emptyList());  // clearing property-level annotations

            statementSpec = newSpec;
        }
        astGOPNodeMap.put(node, value);
    }

    private void leaveObserver(Tree node) throws ASTWalkException
    {
        log.debug(".leaveObserver");

        // Get the object information from AST
        String objectNamespace = node.getChild(0).getText();
        String objectName = node.getChild(1).getText();
        List<ExprNode> obsParameters = ASTExprHelper.getExprNodes(node, 2, astExprNodeMap);

        PatternObserverSpec observerSpec = new PatternObserverSpec(objectNamespace, objectName, obsParameters);
        EvalFactoryNode observerNode = this.patternNodeFactory.makeObserverNode(observerSpec);
        addEvalNodeExpression(observerNode, node);
    }

    private void leaveMatch(Tree node) throws ASTWalkException
    {
        log.debug(".leaveMatch");

        boolean hasRange = true;
        int type = node.getChild(0).getType();
        ExprNode low = null;
        ExprNode high = null;
        boolean allowZeroLowerBounds = false;
        if (type == MATCH_UNTIL_RANGE_HALFOPEN) // [expr:]
        {
            low = astExprNodeMap.remove(node.getChild(0).getChild(0));
        }
        else if (type == MATCH_UNTIL_RANGE_HALFCLOSED) // [:expr]
        {
            high = astExprNodeMap.remove(node.getChild(0).getChild(0));
        }
        else if (type == MATCH_UNTIL_RANGE_BOUNDED) // [expr]
        {
            low = astExprNodeMap.remove(node.getChild(0).getChild(0));
            high = low;
        }
        else if (type == MATCH_UNTIL_RANGE_CLOSED) // [expr:expr]
        {
            low = astExprNodeMap.remove(node.getChild(0).getChild(0));
            high = astExprNodeMap.remove(node.getChild(0).getChild(1));
            allowZeroLowerBounds = true;
        }
        else
        {
            hasRange = false;
        }

        boolean tightlyBound = ASTMatchUntilHelper.validate(low, high, allowZeroLowerBounds);
        if ((node.getChildCount() == 2) && (hasRange) && (!tightlyBound))
        {
            throw new ASTWalkException("Variable bounds repeat operator requires an until-expression");
        }

        EvalFactoryNode fbNode = this.patternNodeFactory.makeMatchUntilNode(low, high);
        addEvalNodeExpression(fbNode, node);
    }

    private void leaveSelectClause(Tree node)
    {
        log.debug(".leaveSelectClause");

        int nodeType = node.getChild(0).getType();
        if (nodeType == RSTREAM)
        {
            statementSpec.setSelectStreamDirEnum(SelectClauseStreamSelectorEnum.RSTREAM_ONLY);
        }
        if (nodeType == ISTREAM)
        {
            statementSpec.setSelectStreamDirEnum(SelectClauseStreamSelectorEnum.ISTREAM_ONLY);
        }
        if (nodeType == IRSTREAM)
        {
            statementSpec.setSelectStreamDirEnum(SelectClauseStreamSelectorEnum.RSTREAM_ISTREAM_BOTH);
        }

        boolean isDistinct = false;
        for (int i = 0; i < node.getChildCount(); i++)
        {
            if (node.getChild(i).getType() == DISTINCT)
            {
                isDistinct = true;
            }
        }
        statementSpec.getSelectClauseSpec().setDistinct(isDistinct);
    }

    private ExprNode getRemoveFirstByType(Tree parent, int type) {
        ExprNode exprNode = null;
        for (int i = 0; i < parent.getChildCount(); i++)
        {
            if (parent.getChild(i).getType() == type)
            {
                exprNode = astExprNodeMap.get(parent.getChild(i).getChild(0));
                if (exprNode == null)
                {
                    throw new IllegalStateException("Expression node for AST node not found for type " + parent.getChild(i).getType() + " and text " + parent.getChild(i).getText());
                }
                astExprNodeMap.remove(parent.getChild(i));
            }
        }
        return exprNode;
    }

    private boolean isSelectInsertFirst(Tree child)
    {
        for (int i = 0; i < child.getChildCount(); i++)
        {
            if (child.getChild(i).getType() == ON_SELECT_INSERT_OUTPUT)
            {
                if (child.getChild(i).getChild(0).getType() == ALL)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private List<ExprChainedSpec> getLibFuncChain(Tree parent) {

        List<ExprChainedSpec> chained = new ArrayList<ExprChainedSpec>();
        for (int i = 0; i < parent.getChildCount(); i++) {
            Tree chainElement = parent.getChild(i);
            if (chainElement.getType() != LIB_FUNCTION) {
                continue;
            }

            ExprChainedSpec chainSpec = ASTLibHelper.getLibFunctionChainSpec(chainElement, astExprNodeMap);
            chained.add(chainSpec);
        }
        return chained;
    }

    private String getDefaultDialect() {
        return configurationInformation.getEngineDefaults().getScripts().getDefaultDialect();
    }

    private static final Log log = LogFactory.getLog(EPLTreeWalker.class);
}
