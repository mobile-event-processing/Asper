/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.asper.sources.net.sf.cglib.reflect.FastClass;
import com.asper.sources.net.sf.cglib.reflect.FastMethod;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.core.context.util.ContextPropertyRegistry;
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.agg.service.AggregationSupport;
import com.espertech.esper.epl.core.*;
import com.espertech.esper.epl.declexpr.ExprDeclaredNode;
import com.espertech.esper.epl.declexpr.ExprDeclaredNodeImpl;
import com.espertech.esper.epl.enummethod.dot.ExprDeclaredOrLambdaNode;
import com.espertech.esper.epl.enummethod.dot.ExprLambdaGoesNode;
import com.espertech.esper.epl.named.NamedWindowTailViewInstance;
import com.espertech.esper.event.EventBeanUtility;
import com.espertech.esper.schedule.ScheduleParameterException;
import com.espertech.esper.schedule.ScheduleSpec;
import com.espertech.esper.schedule.ScheduleSpecUtil;
import com.espertech.esper.util.JavaClassHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;

public class ExprNodeUtility {

    public static void applyFilterExpressionsIterable(Iterable<EventBean> iterable, List<ExprNode> filterExpressions, ExprEvaluatorContext exprEvaluatorContext, Collection<EventBean> eventsInWindow) {
        EventBean[] events = new EventBean[1];
        for (EventBean theEvent : iterable) {
            events[0] = theEvent;
            boolean add = true;
            for (ExprNode filter : filterExpressions) {
                Object result = filter.getExprEvaluator().evaluate(events, true, exprEvaluatorContext);
                if ((result == null) || (!((Boolean) result))) {
                    add = false;
                    break;
                }
            }
            if (add) {
                eventsInWindow.add(events[0]);
            }
        }
    }

    public static ExprAndNode connectExpressionsByLogicalAnd(List<ExprNode> nodes) {
        if (nodes.size() < 2) {
            throw new IllegalArgumentException("Invalid empty or 1-element list of nodes");
        }
        ExprAndNode andNode = new ExprAndNodeImpl();
        for (ExprNode node : nodes) {
            andNode.addChildNode(node);
        }
        return andNode;
    }

    /**
     * Walk expression returning properties used.
     * @param exprNode to walk
     * @param visitAggregateNodes true to visit aggregation nodes
     * @return list of props
     */
    public static List<Pair<Integer, String>> getExpressionProperties(ExprNode exprNode, boolean visitAggregateNodes)
    {
        ExprNodeIdentifierVisitor visitor = new ExprNodeIdentifierVisitor(visitAggregateNodes);
        exprNode.accept(visitor);
        return visitor.getExprProperties();
    }

    public static boolean isConstantValueExpr(ExprNode exprNode) {
        if (!(exprNode instanceof ExprConstantNode)) {
            return false;
        }
        ExprConstantNode constantNode = (ExprConstantNode) exprNode;
        return constantNode.isConstantValue();
    }

    /**
     * Validates the expression node subtree that has this
     * node as root. Some of the nodes of the tree, including the
     * root, might be replaced in the process.
     * @throws com.espertech.esper.epl.expression.ExprValidationException when the validation fails
     * @return the root node of the validated subtree, possibly
     *         different than the root node of the unvalidated subtree
     */
    public static ExprNode getValidatedSubtree(ExprNode exprNode, ExprValidationContext validationContext) throws ExprValidationException
    {
        if (exprNode instanceof ExprLambdaGoesNode) {
            return exprNode;
        }

        return getValidatedSubtreeInternal(exprNode, validationContext, true);
    }

    private static ExprNode getValidatedSubtreeInternal(ExprNode exprNode, ExprValidationContext validationContext, boolean isTopLevel) throws ExprValidationException
    {
        ExprNode result = exprNode;
        if (exprNode instanceof ExprLambdaGoesNode) {
            return exprNode;
        }

        for (int i = 0; i < exprNode.getChildNodes().size(); i++)
        {
            ExprNode childNode = exprNode.getChildNodes().get(i);
            if (childNode instanceof ExprDeclaredOrLambdaNode) {
                ExprDeclaredOrLambdaNode node = (ExprDeclaredOrLambdaNode) childNode;
                if (node.validated()) {
                    continue;
                }
            }
            ExprNode childNodeValidated = getValidatedSubtreeInternal(childNode, validationContext, false);
            exprNode.getChildNodes().set(i, childNodeValidated);
        }

        try
        {
            exprNode.validate(validationContext);
        }
        catch(ExprValidationException e)
        {
            if (exprNode instanceof ExprIdentNode)
            {
                ExprIdentNode identNode = (ExprIdentNode) exprNode;
                try
                {
                    result = resolveStaticMethodOrField(identNode, e, validationContext);
                }
                catch(ExprValidationException ex)
                {
                    e = ex;
                    result = resolveAsStreamName(identNode, e, validationContext);
                }
            }
            else
            {
                throw e;
            }
        }

        // For top-level expressions check if we perform audit
        if (isTopLevel) {
            if (validationContext.isExpressionAudit()) {
                return (ExprNode) ExprNodeProxy.newInstance(validationContext.getStreamTypeService().getEngineURIQualifier(), validationContext.getStatementName(), result);
            }
        }
        else {
            if (validationContext.isExpressionNestedAudit() && !(result instanceof ExprIdentNode) && !(ExprNodeUtility.isConstantValueExpr(result))) {
                return (ExprNode) ExprNodeProxy.newInstance(validationContext.getStreamTypeService().getEngineURIQualifier(), validationContext.getStatementName(), result);
            }
        }
        
        return result;
    }

    private static ExprNode resolveAsStreamName(ExprIdentNode identNode, ExprValidationException existingException, ExprValidationContext validationContext)
            throws ExprValidationException
    {
        ExprStreamUnderlyingNode exprStream = new ExprStreamUnderlyingNodeImpl(identNode.getUnresolvedPropertyName(), false);

        try
        {
            exprStream.validate(validationContext);
        }
        catch (ExprValidationException ex)
        {
            throw existingException;
        }

        return exprStream;
    }

    // Since static method calls such as "Class.method('a')" and mapped properties "Stream.property('key')"
    // look the same, however as the validation could not resolve "Stream.property('key')" before calling this method,
    // this method tries to resolve the mapped property as a static method.
    // Assumes that this is an ExprIdentNode.
    private static ExprNode resolveStaticMethodOrField(ExprIdentNode identNode, ExprValidationException propertyException, ExprValidationContext validationContext)
    throws ExprValidationException
    {
        // Reconstruct the original string
        StringBuffer mappedProperty = new StringBuffer(identNode.getUnresolvedPropertyName());
        if(identNode.getStreamOrPropertyName() != null)
        {
            mappedProperty.insert(0, identNode.getStreamOrPropertyName() + '.');
        }

        // Parse the mapped property format into a class name, method and single string parameter
        MappedPropertyParseResult parse = parseMappedProperty(mappedProperty.toString());
        if (parse == null)
        {
            ExprConstantNode constNode = resolveIdentAsEnumConst(mappedProperty.toString(), validationContext.getMethodResolutionService());
            if (constNode == null)
            {
                throw propertyException;
            }
            else
            {
                return constNode;
            }
        }

        // If there is a class name, assume a static method is possible.
        if (parse.getClassName() != null)
        {
            List<ExprNode> parameters = Collections.singletonList((ExprNode) new ExprConstantNodeImpl(parse.getArgString()));
            List<ExprChainedSpec> chain = new ArrayList<ExprChainedSpec>();
            chain.add(new ExprChainedSpec(parse.getClassName(), Collections.<ExprNode>emptyList(), false));
            chain.add(new ExprChainedSpec(parse.getMethodName(), parameters, false));
            ExprNode result = new ExprDotNode(chain, validationContext.getMethodResolutionService().isDuckType(), validationContext.getMethodResolutionService().isUdfCache());

            // Validate
            try
            {
                result.validate(validationContext);
            }
            catch(ExprValidationException e)
            {
                throw new ExprValidationException("Failed to resolve enumeration method, date-time method or mapped property '" + mappedProperty + "': " + e.getMessage());
            }

            return result;
        }

        // There is no class name, try a single-row function
        String functionName = parse.getMethodName();
        try
        {
            Pair<Class, EngineImportSingleRowDesc> classMethodPair = validationContext.getMethodResolutionService().resolveSingleRow(functionName);
            List<ExprNode> parameters = Collections.singletonList((ExprNode) new ExprConstantNodeImpl(parse.getArgString()));
            List<ExprChainedSpec> chain = Collections.singletonList(new ExprChainedSpec(classMethodPair.getSecond().getMethodName(), parameters, false));
            ExprNode result = new ExprPlugInSingleRowNode(functionName, classMethodPair.getFirst(), chain, classMethodPair.getSecond());

            // Validate
            try
            {
                result.validate(validationContext);
            }
            catch (RuntimeException e)
            {
                throw new ExprValidationException("Plug-in aggregation function '" + parse.getMethodName() + "' failed validation: " + e.getMessage());
            }

            return result;
        }
        catch (EngineImportUndefinedException e)
        {
            // Not an single-row function
        }
        catch (EngineImportException e)
        {
            throw new IllegalStateException("Error resolving single-row function: " + e.getMessage(), e);
        }

        // Try an aggregation function factory
        try
        {
            AggregationFunctionFactory aggregationFactory = validationContext.getMethodResolutionService().resolveAggregationFactory(parse.getMethodName());
            ExprNode result = new ExprPlugInAggFunctionFactoryNode(false, aggregationFactory, parse.getMethodName());
            result.addChildNode(new ExprConstantNodeImpl(parse.getArgString()));

            // Validate
            try
            {
                result.validate(validationContext);
            }
            catch (RuntimeException e)
            {
                throw new ExprValidationException("Plug-in aggregation function '" + parse.getMethodName() + "' failed validation: " + e.getMessage());
            }

            return result;
        }
        catch (EngineImportUndefinedException e)
        {
            // Not an aggregation function
        }
        catch (EngineImportException e)
        {
            throw new IllegalStateException("Error resolving aggregation: " + e.getMessage(), e);
        }

        // There is no class name, try an aggregation function (AggregationSupport version, deprecated)
        try
        {
            AggregationSupport aggregation = validationContext.getMethodResolutionService().resolveAggregation(parse.getMethodName());
            ExprNode result = new ExprPlugInAggFunctionNode(false, aggregation, parse.getMethodName());
            result.addChildNode(new ExprConstantNodeImpl(parse.getArgString()));

            // Validate
            try
            {
                result.validate(validationContext);
            }
            catch (RuntimeException e)
            {
                throw new ExprValidationException("Plug-in aggregation function '" + parse.getMethodName() + "' failed validation: " + e.getMessage());
            }

            return result;
        }
        catch (EngineImportUndefinedException e)
        {
            // Not an aggregation function
        }
        catch (EngineImportException e)
        {
            throw new IllegalStateException("Error resolving aggregation: " + e.getMessage(), e);
        }

        // absolutly cannot be resolved
        throw propertyException;
    }

    private static ExprConstantNode resolveIdentAsEnumConst(String constant, MethodResolutionService methodResolutionService)
            throws ExprValidationException
    {
        Object enumValue = JavaClassHelper.resolveIdentAsEnumConst(constant, methodResolutionService, null);
        if (enumValue != null)
        {
            return new ExprConstantNodeImpl(enumValue);
        }
        return null;
    }

    /**
     * Parse the mapped property into classname, method and string argument.
     * Mind this has been parsed already and is a valid mapped property.
     * @param property is the string property to be passed as a static method invocation
     * @return descriptor object
     */
    protected static MappedPropertyParseResult parseMappedProperty(String property)
    {
        // get argument
        int indexFirstDoubleQuote = property.indexOf("\"");
        int indexFirstSingleQuote = property.indexOf("'");
        int startArg;
        if ((indexFirstSingleQuote == -1) && (indexFirstDoubleQuote == -1))
        {
            return null;
        }
        if ((indexFirstSingleQuote != -1) && (indexFirstDoubleQuote != -1))
        {
            if (indexFirstSingleQuote < indexFirstDoubleQuote)
            {
                startArg = indexFirstSingleQuote;
            }
            else
            {
                startArg = indexFirstDoubleQuote;
            }
        }
        else if (indexFirstSingleQuote != -1)
        {
            startArg = indexFirstSingleQuote;
        }
        else
        {
            startArg = indexFirstDoubleQuote;
        }

        int indexLastDoubleQuote = property.lastIndexOf("\"");
        int indexLastSingleQuote = property.lastIndexOf("'");
        int endArg;
        if ((indexLastSingleQuote == -1) && (indexLastDoubleQuote == -1))
        {
            return null;
        }
        if ((indexLastSingleQuote != -1) && (indexLastDoubleQuote != -1))
        {
            if (indexLastSingleQuote > indexLastDoubleQuote)
            {
                endArg = indexLastSingleQuote;
            }
            else
            {
                endArg = indexLastDoubleQuote;
            }
        }
        else if (indexLastSingleQuote != -1)
        {
            if (indexLastSingleQuote == indexFirstSingleQuote) {
                return null;
            }
            endArg = indexLastSingleQuote;
        }
        else
        {
            if (indexLastDoubleQuote == indexFirstDoubleQuote) {
                return null;
            }
            endArg = indexLastDoubleQuote;
        }
        String argument = property.substring(startArg + 1, endArg);

        // get method
        String splitDots[] = property.split("[\\.]");
        if (splitDots.length == 0)
        {
            return null;
        }

        // find which element represents the method, its the element with the parenthesis
        int indexMethod = -1;
        for (int i = 0; i < splitDots.length; i++)
        {
            if (splitDots[i].contains("("))
            {
                indexMethod = i;
                break;
            }
        }
        if (indexMethod == -1)
        {
            return null;
        }

        String method = splitDots[indexMethod];
        int indexParan = method.indexOf("(");
        method = method.substring(0, indexParan);
        if (method.length() == 0)
        {
            return null;
        }

        if (splitDots.length == 1)
        {
            // no class name
            return new MappedPropertyParseResult(null, method, argument);
        }


        // get class
        StringBuilder clazz = new StringBuilder();
        for (int i = 0; i < indexMethod; i++)
        {
            if (i > 0)
            {
                clazz.append('.');
            }
            clazz.append(splitDots[i]);
        }

        return new MappedPropertyParseResult(clazz.toString(), method, argument);
    }

    public static boolean isAllConstants(List<ExprNode> parameters) {
        for (ExprNode node : parameters) {
            if (!node.isConstantResult()) {
                return false;
            }
        }
        return true;
    }

    public static ExprIdentNode getExprIdentNode(EventType[] typesPerStream, int streamId, String property) {
        return new ExprIdentNodeImpl(typesPerStream[streamId], property, streamId);
    }

    public static ExprNodeUtilSingleRowMethodDesc resolveSingleRowPluginFunc(String className, String methodName, List<ExprNode> parameters, MethodResolutionService methodResolutionService, boolean allowWildcard, final EventType wildcardType, String resolvedExpression, boolean configuredAsSingleRow) throws ExprValidationException {
        Class[] paramTypes = new Class[parameters.size()];
        ExprEvaluator[] childEvals = new ExprEvaluator[parameters.size()];
        int count = 0;

        boolean allConstants = true;
        for(ExprNode childNode : parameters)
        {
            if (childNode instanceof ExprLambdaGoesNode) {
                throw new ExprValidationException("Unexpected lambda-expression encountered as parameter to UDF or static method '" + methodName + "'");
            }
            if (childNode instanceof ExprNumberSetWildcardMarker) {
                if (wildcardType == null || !allowWildcard) {
                    throw new ExprValidationException("Failed to resolve wildcard parameter to a given event type");
                }
                childEvals[count] = new ExprEvaluator() {
                    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
                        return eventsPerStream[0].getUnderlying();
                    }

                    public Class getType() {
                        return wildcardType.getUnderlyingType();
                    }

                    public Map<String, Object> getEventType() throws ExprValidationException {
                        return null;
                    }
                };
                paramTypes[count] = wildcardType.getUnderlyingType();
                allConstants = false;
                count++;
                continue;
            }
            ExprEvaluator eval = childNode.getExprEvaluator();
            childEvals[count] = eval;
            paramTypes[count] = eval.getType();
            count++;
            if (!(childNode.isConstantResult()))
            {
                allConstants = false;
            }
        }

        // Try to resolve the method
        final FastMethod staticMethod;
        Method method;
        try
        {
            method = methodResolutionService.resolveMethod(className, methodName, paramTypes);
            FastClass declaringClass = FastClass.create(Thread.currentThread().getContextClassLoader(), method.getDeclaringClass());
            staticMethod = declaringClass.getMethod(method);
        }
        catch(Exception e)
        {
            String message;
            if (configuredAsSingleRow) {
                message = e.getMessage();
            }
            else {
                message = "Failed to resolve '" + resolvedExpression + "' to a property, single-row function, script, stream or class name";
            }
            throw new ExprValidationException(message, e);
        }

        return new ExprNodeUtilSingleRowMethodDesc(allConstants, paramTypes, childEvals, method, staticMethod);
    }

    public static void validatePlainExpression(String expressionTextualName, ExprNode expression) throws ExprValidationException {
        ExprNodeSummaryVisitor summaryVisitor = new ExprNodeSummaryVisitor();
        expression.accept(summaryVisitor);
        if (summaryVisitor.isHasAggregation() || summaryVisitor.isHasSubselect() || summaryVisitor.isHasStreamSelect() || summaryVisitor.isHasPreviousPrior()) {
            throw new ExprValidationException("Invalid expression '" + expressionTextualName + "': Aggregation, sub-select, previous or prior functions are not supported in this context");
        }
    }

    public static ExprNode validateSimpleGetSubtree(ExprNode expression, StatementContext statementContext, EventType optionalEventType)
        throws ExprValidationException {

        ExprNodeUtility.validatePlainExpression(expression.toExpressionString(), expression);

        StreamTypeServiceImpl streamTypes;
        if (optionalEventType != null) {
            streamTypes = new StreamTypeServiceImpl(optionalEventType, null, true, statementContext.getEngineURI());
        }
        else {
            streamTypes = new StreamTypeServiceImpl(statementContext.getEngineURI(), false);
        }

        ExprValidationContext validationContext = new ExprValidationContext(streamTypes, statementContext.getMethodResolutionService(), null, statementContext.getSchedulingService(), statementContext.getVariableService(), new ExprEvaluatorContextStatement(statementContext), statementContext.getEventAdapterService(), statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getContextDescriptor());
        return ExprNodeUtility.getValidatedSubtree(expression, validationContext);
    }

    public static Set<String> getPropertyNamesIfAllProps(ExprNode[] expressions) {
        for (ExprNode expression : expressions) {
            if (!(expression instanceof ExprIdentNode)) {
                return null;
            }
        }
        Set<String> uniquePropertyNames = new HashSet<String>();
        for (ExprNode expression : expressions) {
            ExprIdentNode identNode = (ExprIdentNode) expression;
            uniquePropertyNames.add(identNode.getUnresolvedPropertyName());
        }
        return uniquePropertyNames;
    }

    /**
     * Encapsulates the parse result parsing a mapped property as a class and method name with args.
     */
    protected static class MappedPropertyParseResult
    {
        private String className;
        private String methodName;
        private String argString;

        /**
         * Returns class name.
         * @return name of class
         */
        public String getClassName()
        {
            return className;
        }

        /**
         * Returns the method name.
         * @return method name
         */
        public String getMethodName()
        {
            return methodName;
        }

        /**
         * Returns the method argument.
         * @return arg
         */
        public String getArgString()
        {
            return argString;
        }

        /**
         * Returns the parse result of the mapped property.
         * @param className is the class name, or null if there isn't one
         * @param methodName is the method name
         * @param argString is the argument
         */
        public MappedPropertyParseResult(String className, String methodName, String argString)
        {
            this.className = className;
            this.methodName = methodName;
            this.argString = argString;
        }
    }

    public static void acceptChain(ExprNodeVisitor visitor, List<ExprChainedSpec> chainSpec) {
        for (ExprChainedSpec chain : chainSpec) {
            for (ExprNode param : chain.getParameters()) {
                param.accept(visitor);
            }
        }
    }

    public static void acceptChain(ExprNodeVisitorWithParent visitor, List<ExprChainedSpec> chainSpec) {
        for (ExprChainedSpec chain : chainSpec) {
            for (ExprNode param : chain.getParameters()) {
                param.accept(visitor);
            }
        }
    }

    public static void acceptChain(ExprNodeVisitorWithParent visitor, List<ExprChainedSpec> chainSpec, ExprNode parent) {
        for (ExprChainedSpec chain : chainSpec) {
            for (ExprNode param : chain.getParameters()) {
                param.acceptChildnodes(visitor, parent);
            }
        }
    }

    public static final void replaceChildNode(ExprNode parentNode, ExprNode nodeToReplace, ExprNode newNode) {
        int index = parentNode.getChildNodes().indexOf(nodeToReplace);
        if (index == -1) {
            parentNode.replaceUnlistedChildNode(nodeToReplace, newNode);
        }
        else {
            parentNode.getChildNodes().set(index, newNode);
        }
    }

    public static void replaceChainChildNode(ExprNode nodeToReplace, ExprNode newNode, List<ExprChainedSpec> chainSpec) {
        for (ExprChainedSpec chained : chainSpec) {
            int index = chained.getParameters().indexOf(nodeToReplace);
            if (index != -1) {
                chained.getParameters().set(index, newNode);
            }
        }
    }

    public static Set<Pair<Integer, String>> getNonAggregatedProps(EventType[] types, List<ExprNode> exprNodes, ContextPropertyRegistry contextPropertyRegistry)
    { 
        // Determine all event properties in the clause
        Set<Pair<Integer, String>> nonAggProps = new HashSet<Pair<Integer, String>>();
        for (ExprNode node : exprNodes)
        {
            ExprNodeIdentifierVisitor visitor = new ExprNodeIdentifierVisitor(false);
            node.accept(visitor);
            List<Pair<Integer, String>> propertiesNodes = visitor.getExprProperties();
            for (Pair<Integer, String> pair : propertiesNodes) {
                EventType originType = types.length > pair.getFirst() ? types[pair.getFirst()] : null;
                if (originType == null || contextPropertyRegistry == null || !contextPropertyRegistry.isPartitionProperty(originType, pair.getSecond())) {
                    nonAggProps.add(pair);
                }                
            }
        }

        return nonAggProps;
    }

    public static void addNonAggregatedProps(ExprNode exprNode, Set<Pair<Integer, String>> set) {
        ExprNodeIdentifierVisitor visitor = new ExprNodeIdentifierVisitor(false);
        exprNode.accept(visitor);
        set.addAll(visitor.getExprProperties());
    }

    public static Set<Pair<Integer, String>> getAggregatedProperties(List<ExprAggregateNode> aggregateNodes)
    {
        // Get a list of properties being aggregated in the clause.
        Set<Pair<Integer, String>> propertiesAggregated = new HashSet<Pair<Integer, String>>();
        for (ExprNode selectAggExprNode : aggregateNodes)
        {
            ExprNodeIdentifierVisitor visitor = new ExprNodeIdentifierVisitor(true);
            selectAggExprNode.accept(visitor);
            List<Pair<Integer, String>> properties = visitor.getExprProperties();
            propertiesAggregated.addAll(properties);
        }

        return propertiesAggregated;
    }

    public static ExprEvaluator[] getEvaluators(ExprNode[] exprNodes) {
        if (exprNodes == null) {
            return null;
        }
        ExprEvaluator[] eval = new ExprEvaluator[exprNodes.length];
        for (int i = 0; i < exprNodes.length; i++) {
            ExprNode node = exprNodes[i];
            if (node != null) {
                eval[i] = node.getExprEvaluator();
            }
        }
        return eval;
    }

    public static ExprEvaluator[] getEvaluators(List<ExprNode> childNodes)
    {
        ExprEvaluator[] eval = new ExprEvaluator[childNodes.size()];
        for (int i = 0; i < childNodes.size(); i++) {
            eval[i] = childNodes.get(i).getExprEvaluator();
        }
        return eval;
    }

    public static Set<Integer> getIdentStreamNumbers(ExprNode child) {

        Set<Integer> streams = new HashSet<Integer>();
        ExprNodeIdentifierCollectVisitor visitor = new ExprNodeIdentifierCollectVisitor();
        child.accept(visitor);
        for (ExprIdentNode node : visitor.getExprProperties()) {
            streams.add(node.getStreamId());
        }
        return streams;
    }

    /**
     * Returns true if all properties within the expression are witin data window'd streams.
     * @param child expression to interrogate
     * @param streamTypeService streams
     * @return indicator
     */
    public static boolean hasRemoveStream(ExprNode child, StreamTypeService streamTypeService) {

        // Determine whether all streams are istream-only or irstream
        boolean[] isIStreamOnly = streamTypeService.getIStreamOnly();
        boolean isAllIStream = true;    // all true?
        boolean isAllIRStream = true;   // all false?
        for (boolean anIsIStreamOnly : isIStreamOnly) {
            if (!anIsIStreamOnly) {
                isAllIStream = false;
            }
            else {
                isAllIRStream = false;
            }
        }

        // determine if a data-window applies to this max function
        boolean hasDataWindows = true;
        if (isAllIStream) {
            hasDataWindows = false;
        }
        else if (!isAllIRStream) {
            if (streamTypeService.getEventTypes().length > 1) {
                // In a join we assume that a data window is present or implicit via unidirectional
            }
            else {
                hasDataWindows = false;
                // get all aggregated properties to determine if any is from a windowed stream
                ExprNodeIdentifierCollectVisitor visitor = new ExprNodeIdentifierCollectVisitor();
                child.accept(visitor);
                for (ExprIdentNode node : visitor.getExprProperties()) {
                    if (!isIStreamOnly[node.getStreamId()]) {
                        hasDataWindows = true;
                        break;
                    }
                }
            }
        }

        return hasDataWindows;
    }


    /**
     * Apply a filter expression.
     * @param filter expression
     * @param streamZeroEvent the event that represents stream zero
     * @param streamOneEvents all events thate are stream one events
     * @param exprEvaluatorContext context for expression evaluation
     * @return filtered stream one events
     */
    public static EventBean[] applyFilterExpression(ExprEvaluator filter, EventBean streamZeroEvent, EventBean[] streamOneEvents, ExprEvaluatorContext exprEvaluatorContext)
    {
        EventBean[] eventsPerStream = new EventBean[2];
        eventsPerStream[0] = streamZeroEvent;

        EventBean[] filtered = new EventBean[streamOneEvents.length];
        int countPass = 0;

        for (EventBean eventBean : streamOneEvents)
        {
            eventsPerStream[1] = eventBean;

            Boolean result = (Boolean) filter.evaluate(eventsPerStream, true, exprEvaluatorContext);
            if ((result != null) && result)
            {
                filtered[countPass] = eventBean;
                countPass++;
            }
        }

        if (countPass == streamOneEvents.length)
        {
            return streamOneEvents;
        }
        return EventBeanUtility.resizeArray(filtered, countPass);
    }

    /**
     * Apply a filter expression returning a pass indicator.
     * @param filter to apply
     * @param eventsPerStream events per stream
     * @param exprEvaluatorContext context for expression evaluation
     * @return pass indicator
     */
    public static boolean applyFilterExpression(ExprEvaluator filter, EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext)
    {
        Boolean result = (Boolean) filter.evaluate(eventsPerStream, true, exprEvaluatorContext);
        return (result != null) && result;
    }

    /**
     * Compare two expression nodes and their children in exact child-node sequence,
     * returning true if the 2 expression nodes trees are equals, or false if they are not equals.
     * <p>
     * Recursive call since it uses this method to compare child nodes in the same exact sequence.
     * Nodes are compared using the equalsNode method.
     * @param nodeOne - first expression top node of the tree to compare
     * @param nodeTwo - second expression top node of the tree to compare
     * @return false if this or all child nodes are not equal, true if equal
     */
    public static boolean deepEquals(ExprNode nodeOne, ExprNode nodeTwo)
    {
        if (nodeOne.getChildNodes().size() != nodeTwo.getChildNodes().size())
        {
            return false;
        }
        if (!nodeOne.equalsNode(nodeTwo))
        {
            return false;
        }
        for (int i = 0; i < nodeOne.getChildNodes().size(); i++)
        {
            ExprNode childNodeOne = nodeOne.getChildNodes().get(i);
            ExprNode childNodeTwo = nodeTwo.getChildNodes().get(i);

            if (!ExprNodeUtility.deepEquals(childNodeOne, childNodeTwo))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares two expression nodes via deep comparison, considering all
     * child nodes of either side.
     * @param one array of expressions
     * @param two array of expressions
     * @return true if the expressions are equal, false if not
     */
    public static boolean deepEquals(ExprNode[] one, ExprNode[] two)
    {
        if (one.length != two.length)
        {
            return false;
        }
        for (int i = 0; i < one.length; i++)
        {
            if (!ExprNodeUtility.deepEquals(one[i], two[i]))
            {
                return false;
            }
        }
        return true;
    }

    public static boolean deepEquals(List<ExprNode> one, List<ExprNode> two)
    {
        if (one.size() != two.size())
        {
            return false;
        }
        for (int i = 0; i < one.size(); i++)
        {
            if (!ExprNodeUtility.deepEquals(one.get(i), two.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the expression is minimal: does not have a subselect, aggregation and does not need view resources
     * @param expression to inspect
     * @return null if minimal, otherwise name of offending sub-expression
     */
    public static String isMinimalExpression(ExprNode expression)
    {
        ExprNodeSubselectDeclaredDotVisitor subselectVisitor = new ExprNodeSubselectDeclaredDotVisitor();
        expression.accept(subselectVisitor);
        if (subselectVisitor.getSubselects().size() > 0)
        {
            return "a subselect";
        }

        ExprNodeViewResourceVisitor viewResourceVisitor = new ExprNodeViewResourceVisitor();
        expression.accept(viewResourceVisitor);
        if (viewResourceVisitor.getExprNodes().size() > 0)
        {
            return "a function that requires view resources (prior, prev)";
        }

        List<ExprAggregateNode> aggregateNodes = new LinkedList<ExprAggregateNode>();
        ExprAggregateNodeUtil.getAggregatesBottomUp(expression, aggregateNodes);
        if (!aggregateNodes.isEmpty())
        {
            return "an aggregation function";
        }
        return null;
    }

    protected static void toExpressionString(List<ExprChainedSpec> chainSpec, StringBuilder buffer, boolean prefixDot, String functionName)
    {
        String delimiterOuter = "";
        if (prefixDot) {
            delimiterOuter = ".";
        }
        boolean isFirst = true;
        for (ExprChainedSpec element : chainSpec) {
            buffer.append(delimiterOuter);
            if (functionName != null) {
                buffer.append(functionName);
            }
            else {
                buffer.append(element.getName());
            }

            // the first item without dot-prefix and empty parameters should not be appended with parenthesis
            if (!isFirst || prefixDot || !element.getParameters().isEmpty()) {
                toExpressionStringIncludeParen(element.getParameters(), buffer);
            }

            delimiterOuter = ".";
            isFirst = false;
        }
    }

    public static void toExpressionString(List<ExprNode> parameters, StringBuilder buffer) {
        String delimiter = "";
        for (ExprNode param : parameters) {
            buffer.append(delimiter);
            delimiter = ", ";
            buffer.append(param.toExpressionString());
        }
    }

    public static void toExpressionStringIncludeParen(List<ExprNode> parameters, StringBuilder buffer) {
        buffer.append("(");
        toExpressionString(parameters, buffer);
        buffer.append(")");
    }

    public static void validate(List<ExprChainedSpec> chainSpec, ExprValidationContext validationContext) throws ExprValidationException {

        // validate all parameters
        for (ExprChainedSpec chainElement : chainSpec) {
            List<ExprNode> validated = new ArrayList<ExprNode>();
            for (ExprNode expr : chainElement.getParameters()) {
                validated.add(ExprNodeUtility.getValidatedSubtree(expr, validationContext));
            }
            chainElement.setParameters(validated);
        }
    }

    public static List<ExprNode> collectChainParameters(List<ExprChainedSpec> chainSpec) {
        List<ExprNode> result = new ArrayList<ExprNode>();
        for (ExprChainedSpec chainElement : chainSpec) {
            result.addAll(chainElement.getParameters());
        }
        return result;
    }

    public static String printEvaluators(ExprEvaluator[] evaluators) {
        StringWriter writer = new StringWriter();
        String delimiter = "";
        for (int i = 0; i < evaluators.length; i++) {
            writer.append(delimiter);
            writer.append(evaluators[i].getClass().getSimpleName());
            delimiter = ", ";
        }
        return writer.toString();
    }

    public static ScheduleSpec toCrontabSchedule(List<ExprNode> scheduleSpecExpressionList, StatementContext context)
        throws ExprValidationException {

        // Validate the expressions
        ExprEvaluator[] expressions = new ExprEvaluator[scheduleSpecExpressionList.size()];
        int count = 0;
        ExprEvaluatorContextStatement evaluatorContextStmt = new ExprEvaluatorContextStatement(context);
        for (ExprNode parameters : scheduleSpecExpressionList)
        {
            ExprValidationContext validationContext = new ExprValidationContext(new StreamTypeServiceImpl(context.getEngineURI(), false), context.getMethodResolutionService(), null, context.getSchedulingService(), context.getVariableService(), evaluatorContextStmt, context.getEventAdapterService(), context.getStatementName(), context.getStatementId(), context.getAnnotations(), context.getContextDescriptor());
            ExprNode node = ExprNodeUtility.getValidatedSubtree(parameters, validationContext);
            expressions[count++] = node.getExprEvaluator();
        }

        // Build a schedule
        try
        {
            Object[] scheduleSpecParameterList = evaluateExpressions(expressions, evaluatorContextStmt);
            return ScheduleSpecUtil.computeValues(scheduleSpecParameterList);
        }
        catch (ScheduleParameterException e)
        {
            throw new IllegalArgumentException("Invalid schedule specification : " + e.getMessage(), e);
        }
    }

    public static Object[] evaluateExpressions(ExprEvaluator[] parameters, ExprEvaluatorContext exprEvaluatorContext)
    {
        Object[] results = new Object[parameters.length];
        int count = 0;
        for (ExprEvaluator expr : parameters)
        {
            try
            {
                results[count] = expr.evaluate(null, true, exprEvaluatorContext);
                count++;
            }
            catch (RuntimeException ex)
            {
                String message = "Failed expression evaluation in crontab timer-at for parameter " + count + ": " + ex.getMessage();
                log.error(message, ex);
                throw new IllegalArgumentException(message);
            }
        }
        return results;
    }

    private static final Log log = LogFactory.getLog(ExprNodeUtility.class);
}
