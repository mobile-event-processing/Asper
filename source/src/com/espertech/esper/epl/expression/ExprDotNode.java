/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.client.*;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.epl.core.EngineImportService;
import com.espertech.esper.epl.core.PropertyResolutionDescriptor;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.datetime.eval.ExprDotNodeFilterAnalyzerDesc;
import com.espertech.esper.epl.enummethod.dot.*;
import com.espertech.esper.epl.variable.VariableReader;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.EventTypeUtility;
import com.espertech.esper.util.JavaClassHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Represents an Dot-operator expression, for use when "(expression).method(...).method(...)"
 */
public class ExprDotNode extends ExprNodeBase implements ExprNodeInnerNodeProvider
{
    private static final long serialVersionUID = 8105121208330622813L;

    private final List<ExprChainedSpec> chainSpec;
    private final boolean isDuckTyping;
    private final boolean isUDFCache;

    private transient ExprEvaluator exprEvaluator;
    private boolean isReturnsConstantResult;

    private transient ExprDotNodeFilterAnalyzerDesc exprDotNodeFilterAnalyzerDesc;

    public ExprDotNode(List<ExprChainedSpec> chainSpec, boolean isDuckTyping, boolean isUDFCache)
    {
        this.chainSpec = chainSpec;
        this.isDuckTyping = isDuckTyping;
        this.isUDFCache = isUDFCache;
    }

    public void validate(ExprValidationContext validationContext) throws ExprValidationException
    {
        // validate all parameters
        ExprNodeUtility.validate(chainSpec, validationContext);

        // determine if there are enumeration method expressions in the chain
        boolean hasEnumerationMethod = false;
        for (ExprChainedSpec chain : chainSpec) {
            if (EnumMethodEnum.isEnumerationMethod(chain.getName())) {
                hasEnumerationMethod = true;
                break;
            }
        }
        int prefixedStreamNumber = prefixedStreamName(chainSpec, validationContext.getStreamTypeService());

        // The root node expression may provide the input value:
        //   Such as "window(*).doIt(...)" or "(select * from Window).doIt()" or "prevwindow(sb).doIt(...)", in which case the expression to act on is a child expression
        //
        StreamTypeService streamTypeService = validationContext.getStreamTypeService();
        if (!this.getChildNodes().isEmpty()) {
            // the root expression is the first child node
            ExprNode rootNode = this.getChildNodes().get(0);
            ExprEvaluator rootNodeEvaluator = rootNode.getExprEvaluator();

            // the root expression may also provide a lambda-function input (Iterator<EventBean>)
            // Determine collection-type and evaluator if any for root node
            Pair<ExprEvaluatorEnumeration, ExprDotEvalTypeInfo> enumSrc = getEnumerationSource(rootNode, validationContext.getStreamTypeService(), validationContext.getEventAdapterService(), validationContext.getStatementId(), hasEnumerationMethod);

            ExprDotEvalTypeInfo typeInfo;
            if (enumSrc.getSecond() == null) {
                typeInfo = ExprDotEvalTypeInfo.scalarOrUnderlying(rootNodeEvaluator.getType());    // not a collection type, treat as scalar
            }
            else {
                typeInfo = enumSrc.getSecond();
            }

            ExprDotNodeRealizedChain evals = ExprDotNodeUtility.getChainEvaluators(typeInfo, chainSpec, validationContext, isDuckTyping, new ExprDotNodeFilterAnalyzerInputExpr());
            exprEvaluator = new ExprDotEvalRootChild(rootNodeEvaluator, enumSrc.getFirst(), typeInfo, evals.getChain(), evals.getChainWithUnpack());
            return;
        }

        // No root node, and this is a 1-element chain i.e. "something(param,...)".
        // Plug-in single-row methods are not handled here.
        // Plug-in aggregation methods are not handled here.
        if (chainSpec.size() == 1) {
            ExprChainedSpec spec = chainSpec.get(0);
            if (spec.getParameters().isEmpty()) {
                throw handleNotFound(spec.getName());
            }

            // single-parameter can resolve to a property
            Pair<PropertyResolutionDescriptor, String> propertyInfoPair = null;
            try {
                propertyInfoPair = ExprIdentNodeUtil.getTypeFromStream(streamTypeService, spec.getName(), streamTypeService.hasPropertyAgnosticType(), false);
            }
            catch (ExprValidationPropertyException ex) {
                // fine
            }

            // if not a property then try built-in single-row non-grammar functions
            if (propertyInfoPair == null && spec.getName().toLowerCase().equals(EngineImportService.EXT_SINGLEROW_FUNCTION_TRANSPOSE)) {
                if (spec.getParameters().size() != 1) {
                    throw new ExprValidationException("The " + EngineImportService.EXT_SINGLEROW_FUNCTION_TRANSPOSE + " function requires a single parameter expression");
                }
                exprEvaluator = new ExprDotEvalTransposeAsStream(chainSpec.get(0).getParameters().get(0).getExprEvaluator());
            }
            else if (spec.getParameters().size() != 1) {
                throw handleNotFound(spec.getName());
            }
            else {
                if (propertyInfoPair == null) {
                    throw new ExprValidationException("Unknown single-row function, aggregation function or mapped or indexed property named '" + spec.getName() + "' could not be resolved");
                }
                exprEvaluator = getPropertyPairEvaluator(spec.getParameters().get(0).getExprEvaluator(), propertyInfoPair, validationContext);
            }
            return;
        }

        // handle the case where the first chain spec element is a stream name.
        if (prefixedStreamNumber != -1) {

            ExprChainedSpec specAfterStreamName = chainSpec.get(1);

            // Attempt to resolve as property
            Pair<PropertyResolutionDescriptor, String> propertyInfoPair = null;
            try {
                String propName = chainSpec.get(0).getName() + "." + specAfterStreamName.getName();
                propertyInfoPair = ExprIdentNodeUtil.getTypeFromStream(streamTypeService, propName, streamTypeService.hasPropertyAgnosticType(), false);
            }
            catch (ExprValidationPropertyException ex) {
                // fine
            }
            if (propertyInfoPair != null) {
                if (specAfterStreamName.getParameters().size() != 1) {
                    throw handleNotFound(specAfterStreamName.getName());
                }
                exprEvaluator = getPropertyPairEvaluator(specAfterStreamName.getParameters().get(0).getExprEvaluator(), propertyInfoPair, validationContext);
            }
            else {
                // Attempt to resolve as event-underlying object instance method
                EventType eventType = validationContext.getStreamTypeService().getEventTypes()[prefixedStreamNumber];
                Class type = eventType.getUnderlyingType();

                List<ExprChainedSpec> remainderChain = new ArrayList<ExprChainedSpec>(chainSpec);
                remainderChain.remove(0);

                ExprValidationException methodEx = null;
                ExprDotEval[] underlyingMethodChain = null;
                try {
                    ExprDotEvalTypeInfo typeInfo = ExprDotEvalTypeInfo.scalarOrUnderlying(type);
                    underlyingMethodChain = ExprDotNodeUtility.getChainEvaluators(typeInfo, remainderChain, validationContext, false, new ExprDotNodeFilterAnalyzerInputStream(prefixedStreamNumber)).getChainWithUnpack();
                }
                catch (ExprValidationException ex) {
                    methodEx = ex;
                    // expected - may not be able to find the methods on the underlying
                }

                ExprDotEval[] eventTypeMethodChain = null;
                ExprValidationException enumDatetimeEx = null;
                try {
                    ExprDotEvalTypeInfo typeInfo = ExprDotEvalTypeInfo.event(eventType);
                    ExprDotNodeRealizedChain chain = ExprDotNodeUtility.getChainEvaluators(typeInfo, remainderChain, validationContext, false, new ExprDotNodeFilterAnalyzerInputStream(prefixedStreamNumber));
                    eventTypeMethodChain = chain.getChainWithUnpack();
                    exprDotNodeFilterAnalyzerDesc = chain.getFilterAnalyzerDesc();
                }
                catch (ExprValidationException ex) {
                    enumDatetimeEx = ex;
                    // expected - may not be able to find the methods on the underlying
                }

                if (underlyingMethodChain != null) {
                    exprEvaluator = new ExprDotEvalStreamMethod(prefixedStreamNumber, underlyingMethodChain);
                }
                else if (eventTypeMethodChain != null) {
                    exprEvaluator = new ExprDotEvalStreamEventBean(prefixedStreamNumber, eventTypeMethodChain);
                }
                else {
                    if (ExprDotNodeUtility.isDatetimeOrEnumMethod(remainderChain.get(0).getName())) {
                        throw enumDatetimeEx;
                    }
                    throw new ExprValidationException("Failed to solve '" + remainderChain.get(0).getName() + "' to either an date-time or enumeration method, an event property or a method on the event underlying object: " + methodEx.getMessage(), methodEx);
                }
            }
            return;
        }

        // There no root node, in this case the classname or property name is provided as part of the chain.
        // Such as "MyClass.myStaticLib(...)" or "mycollectionproperty.doIt(...)"
        //
        List<ExprChainedSpec> modifiedChain = new ArrayList<ExprChainedSpec>(chainSpec);
        ExprChainedSpec firstItem = modifiedChain.remove(0);

        Pair<PropertyResolutionDescriptor, String> propertyInfoPair = null;
        try {
            propertyInfoPair = ExprIdentNodeUtil.getTypeFromStream(streamTypeService, firstItem.getName(), streamTypeService.hasPropertyAgnosticType(), true);
        }
        catch (ExprValidationPropertyException ex) {
            // not a property
        }

        // If property then treat it as such
        if (propertyInfoPair != null) {

            String propertyName = propertyInfoPair.getFirst().getPropertyName();
            int streamId = propertyInfoPair.getFirst().getStreamNum();
            EventType streamType = streamTypeService.getEventTypes()[streamId];
            ExprDotEvalTypeInfo typeInfo;
            ExprEvaluatorEnumeration enumerationEval = null;
            ExprDotEvalTypeInfo inputType;
            ExprEvaluator rootNodeEvaluator = null;
            EventPropertyGetter getter;

            if (firstItem.getParameters().isEmpty()) {
                getter = streamType.getGetter(propertyInfoPair.getFirst().getPropertyName());

                Pair<ExprEvaluatorEnumeration, ExprDotEvalTypeInfo> propertyEval = getPropertyEnumerationSource(propertyInfoPair.getFirst().getPropertyName(), streamId, streamType, hasEnumerationMethod);
                typeInfo = propertyEval.getSecond();
                enumerationEval = propertyEval.getFirst();
                inputType = propertyEval.getSecond();
                rootNodeEvaluator = new PropertyExprEvaluatorNonLambda(streamId, getter, propertyInfoPair.getFirst().getPropertyType());
            }
            else {
                // property with parameter - mapped or indexed property
                EventPropertyDescriptor desc = EventTypeUtility.getNestablePropertyDescriptor(streamTypeService.getEventTypes()[propertyInfoPair.getFirst().getStreamNum()], firstItem.getName());
                if (firstItem.getParameters().size() > 1) {
                    throw new ExprValidationException("Property '" + firstItem.getName() + "' may not be accessed passing 2 or more parameters");
                }
                ExprEvaluator paramEval = firstItem.getParameters().get(0).getExprEvaluator();
                typeInfo = ExprDotEvalTypeInfo.scalarOrUnderlying(desc.getPropertyComponentType());
                inputType = typeInfo;
                getter = null;
                if (desc.isMapped()) {
                    if (paramEval.getType() != String.class) {
                        throw new ExprValidationException("Parameter expression to mapped property '" + propertyName + "' is expected to return a string-type value but returns " + JavaClassHelper.getClassNameFullyQualPretty(paramEval.getType()));
                    }
                    EventPropertyGetterMapped mappedGetter = propertyInfoPair.getFirst().getStreamEventType().getGetterMapped(propertyInfoPair.getFirst().getPropertyName());
                    if (mappedGetter == null) {
                        throw new ExprValidationException("Mapped property named '" + propertyName + "' failed to obtain getter-object");
                    }
                    rootNodeEvaluator = new PropertyExprEvaluatorNonLambdaMapped(streamId, mappedGetter, paramEval, desc.getPropertyComponentType());
                }
                if (desc.isIndexed()) {
                    if (JavaClassHelper.getBoxedType(paramEval.getType()) != Integer.class) {
                        throw new ExprValidationException("Parameter expression to mapped property '" + propertyName + "' is expected to return a Integer-type value but returns " + JavaClassHelper.getClassNameFullyQualPretty(paramEval.getType()));
                    }
                    EventPropertyGetterIndexed indexedGetter = propertyInfoPair.getFirst().getStreamEventType().getGetterIndexed(propertyInfoPair.getFirst().getPropertyName());
                    if (indexedGetter == null) {
                        throw new ExprValidationException("Mapped property named '" + propertyName + "' failed to obtain getter-object");
                    }
                    rootNodeEvaluator = new PropertyExprEvaluatorNonLambdaIndexed(streamId, indexedGetter, paramEval, desc.getPropertyComponentType());
                }
            }
            if (typeInfo == null) {
                throw new ExprValidationException("Property '" + propertyName + "' is not a mapped or indexed property");
            }

            // try to build chain based on the input (non-fragment)
            ExprDotNodeRealizedChain evals;
            ExprDotNodeFilterAnalyzerInputProp filterAnalyzerInputProp = new ExprDotNodeFilterAnalyzerInputProp(propertyInfoPair.getFirst().getStreamNum(), propertyInfoPair.getFirst().getPropertyName());
            try {
                evals = ExprDotNodeUtility.getChainEvaluators(inputType, modifiedChain, validationContext, isDuckTyping, filterAnalyzerInputProp);
            }
            catch (ExprValidationException ex) {

                // try building the chain based on the fragment event type (i.e. A.after(B) based on A-configured start time where A is a fragment)
                FragmentEventType fragment = propertyInfoPair.getFirst().getFragmentEventType();
                if (fragment == null) {
                    throw ex;
                }

                ExprDotEvalTypeInfo fragmentTypeInfo;
                if (fragment.isIndexed()) {
                    fragmentTypeInfo = ExprDotEvalTypeInfo.eventColl(fragment.getFragmentType());
                }
                else {
                    fragmentTypeInfo = ExprDotEvalTypeInfo.event(fragment.getFragmentType());
                }

                evals = ExprDotNodeUtility.getChainEvaluators(fragmentTypeInfo, modifiedChain, validationContext, isDuckTyping, filterAnalyzerInputProp);
                rootNodeEvaluator = new PropertyExprEvaluatorNonLambdaFragment(streamId, getter, fragment.getFragmentType().getUnderlyingType());
            }

            exprEvaluator = new ExprDotEvalRootChild(rootNodeEvaluator, enumerationEval, inputType, evals.getChain(), evals.getChainWithUnpack());
            exprDotNodeFilterAnalyzerDesc = evals.getFilterAnalyzerDesc();
            return;
        }

        // If variable then resolve as such
        VariableReader variableReader = validationContext.getVariableService().getReader(firstItem.getName());
        if (variableReader != null) {
            ExprDotEvalTypeInfo typeInfo;
            ExprDotStaticMethodWrap wrap;
            if (variableReader.getType().isArray()) {
                typeInfo = ExprDotEvalTypeInfo.componentColl(variableReader.getType().getComponentType());
                wrap = new ExprDotStaticMethodWrapArrayScalar(variableReader.getVariableName(), variableReader.getType().getComponentType());
            }
            else if (variableReader.getEventType() != null) {
                typeInfo = ExprDotEvalTypeInfo.event(variableReader.getEventType());
                wrap = null;
            }
            else {
                typeInfo = ExprDotEvalTypeInfo.scalarOrUnderlying(variableReader.getType());
                wrap = null;
            }

            ExprDotNodeRealizedChain evals = ExprDotNodeUtility.getChainEvaluators(typeInfo, modifiedChain, validationContext, false, new ExprDotNodeFilterAnalyzerInputStatic());
            exprEvaluator = new ExprDotEvalVariable(variableReader, wrap, evals.getChainWithUnpack());
            return;
        }

        // If class then resolve as class
        ExprChainedSpec secondItem = modifiedChain.remove(0);

        boolean allowWildcard = validationContext.getStreamTypeService().getEventTypes().length == 1;
        EventType streamZeroType = null;
        if (validationContext.getStreamTypeService().getEventTypes().length > 0) {
            streamZeroType = validationContext.getStreamTypeService().getEventTypes()[0];
        }
        ExprNodeUtilSingleRowMethodDesc singleRowMethod = ExprNodeUtility.resolveSingleRowPluginFunc(firstItem.getName(), secondItem.getName(), secondItem.getParameters(), validationContext.getMethodResolutionService(), allowWildcard, streamZeroType, firstItem.getName() + "." + secondItem.getName(), false);

        boolean isConstantParameters = singleRowMethod.isAllConstants() && isUDFCache;
        isReturnsConstantResult = isConstantParameters && modifiedChain.isEmpty();

        // this may return a pair of null if there is no lambda or the result cannot be wrapped for lambda-function use
        ExprDotStaticMethodWrap optionalLambdaWrap = ExprDotStaticMethodWrapFactory.make(singleRowMethod.getReflectionMethod(), validationContext.getEventAdapterService(), modifiedChain);
        ExprDotEvalTypeInfo typeInfo = optionalLambdaWrap != null ? optionalLambdaWrap.getTypeInfo() : ExprDotEvalTypeInfo.scalarOrUnderlying(singleRowMethod.getReflectionMethod().getReturnType());

        ExprDotNodeRealizedChain evals = ExprDotNodeUtility.getChainEvaluators(typeInfo, modifiedChain, validationContext, false, new ExprDotNodeFilterAnalyzerInputStatic());
        exprEvaluator = new ExprDotEvalStaticMethod(validationContext.getStatementName(), firstItem.getName(), singleRowMethod.getFastMethod(), singleRowMethod.getChildEvals(), isConstantParameters, optionalLambdaWrap, evals.getChainWithUnpack(), false);
    }

    public ExprDotNodeFilterAnalyzerDesc getExprDotNodeFilterAnalyzerDesc() {
        return exprDotNodeFilterAnalyzerDesc;
    }

    private ExprEvaluator getPropertyPairEvaluator(ExprEvaluator parameterEval, Pair<PropertyResolutionDescriptor, String> propertyInfoPair, ExprValidationContext validationContext)
            throws ExprValidationException
    {
        String propertyName = propertyInfoPair.getFirst().getPropertyName();
        EventPropertyDescriptor propertyDesc = EventTypeUtility.getNestablePropertyDescriptor(propertyInfoPair.getFirst().getStreamEventType(), propertyName);
        if (propertyDesc == null || (!propertyDesc.isMapped() && !propertyDesc.isIndexed())) {
            throw new ExprValidationException("Unknown single-row function, aggregation function or mapped or indexed property named '" + propertyName + "' could not be resolved");
        }

        int streamNum = propertyInfoPair.getFirst().getStreamNum();
        if (propertyDesc.isMapped()) {
            if (parameterEval.getType() != String.class) {
                throw new ExprValidationException("Parameter expression to mapped property '" + propertyDesc.getPropertyName() + "' is expected to return a string-type value but returns " + JavaClassHelper.getClassNameFullyQualPretty(parameterEval.getType()));
            }
            EventPropertyGetterMapped mappedGetter = propertyInfoPair.getFirst().getStreamEventType().getGetterMapped(propertyInfoPair.getFirst().getPropertyName());
            if (mappedGetter == null) {
                throw new ExprValidationException("Mapped property named '" + propertyName + "' failed to obtain getter-object");
            }
            return new ExprDotEvalPropertyExprMapped(validationContext.getStatementName(), propertyDesc.getPropertyName(), streamNum, parameterEval, propertyDesc.getPropertyComponentType(), mappedGetter);
        }
        else {
            if (JavaClassHelper.getBoxedType(parameterEval.getType()) != Integer.class) {
                throw new ExprValidationException("Parameter expression to indexed property '" + propertyDesc.getPropertyName() + "' is expected to return a Integer-type value but returns " + JavaClassHelper.getClassNameFullyQualPretty(parameterEval.getType()));
            }
            EventPropertyGetterIndexed indexedGetter = propertyInfoPair.getFirst().getStreamEventType().getGetterIndexed(propertyInfoPair.getFirst().getPropertyName());
            if (indexedGetter == null) {
                throw new ExprValidationException("Indexed property named '" + propertyName + "' failed to obtain getter-object");
            }
            return new ExprDotEvalPropertyExprIndexed(validationContext.getStatementName(), propertyDesc.getPropertyName(), streamNum, parameterEval, propertyDesc.getPropertyComponentType(), indexedGetter);
        }
    }

    private int prefixedStreamName(List<ExprChainedSpec> chainSpec, StreamTypeService streamTypeService) {
        if (chainSpec.size() < 1) {
            return -1;
        }
        ExprChainedSpec spec = chainSpec.get(0);
        if (spec.getParameters().size() > 0 && !spec.isProperty()) {
            return -1;
        }
        return streamTypeService.getStreamNumForStreamName(spec.getName());
    }

    public void accept(ExprNodeVisitor visitor) {
        super.accept(visitor);
        ExprNodeUtility.acceptChain(visitor, chainSpec);
    }

    public void accept(ExprNodeVisitorWithParent visitor) {
        super.accept(visitor);
        ExprNodeUtility.acceptChain(visitor, chainSpec);
    }

    public void acceptChildnodes(ExprNodeVisitorWithParent visitor, ExprNode parent) {
        super.acceptChildnodes(visitor, parent);
        ExprNodeUtility.acceptChain(visitor, chainSpec, this);
    }

    public void replaceUnlistedChildNode(ExprNode nodeToReplace, ExprNode newNode) {
        ExprNodeUtility.replaceChainChildNode(nodeToReplace, newNode, chainSpec);
    }

    public List<ExprChainedSpec> getChainSpec()
    {
        return chainSpec;
    }

    public ExprEvaluator getExprEvaluator()
    {
        return exprEvaluator;
    }

    public boolean isConstantResult()
    {
        return isReturnsConstantResult;
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();
        if (!this.getChildNodes().isEmpty()) {
            buffer.append('(');
            buffer.append(this.getChildNodes().get(0).toExpressionString());
            buffer.append(")");
        }
        ExprNodeUtility.toExpressionString(chainSpec, buffer, !this.getChildNodes().isEmpty(), null);
        return buffer.toString();
    }

    public Map<String, Object> getEventType() {
        return null;
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprDotNode))
        {
            return false;
        }

        ExprDotNode other = (ExprDotNode) node;
        if (other.chainSpec.size() != this.chainSpec.size()) {
            return false;
        }
        for (int i = 0; i < chainSpec.size(); i++) {
            if (!(this.chainSpec.get(i).equals(other.chainSpec.get(i)))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<ExprNode> getAdditionalNodes() {
        return ExprNodeUtility.collectChainParameters(chainSpec);
    }

    public static Pair<ExprEvaluatorEnumeration, ExprDotEvalTypeInfo> getEnumerationSource(ExprNode inputExpression, StreamTypeService streamTypeService, EventAdapterService eventAdapterService, String statementId, boolean hasEnumerationMethod) throws ExprValidationException {
        ExprEvaluator rootNodeEvaluator = inputExpression.getExprEvaluator();
        ExprEvaluatorEnumeration rootLambdaEvaluator = null;
        ExprDotEvalTypeInfo info = null;

        if (rootNodeEvaluator instanceof ExprEvaluatorEnumeration) {
            rootLambdaEvaluator = (ExprEvaluatorEnumeration) rootNodeEvaluator;

            if (rootLambdaEvaluator.getEventTypeCollection(eventAdapterService) != null) {
                info = ExprDotEvalTypeInfo.eventColl(rootLambdaEvaluator.getEventTypeCollection(eventAdapterService));
            }
            else if (rootLambdaEvaluator.getEventTypeSingle(eventAdapterService, statementId) != null) {
                info = ExprDotEvalTypeInfo.event(rootLambdaEvaluator.getEventTypeSingle(eventAdapterService, statementId));
            }
            else if (rootLambdaEvaluator.getComponentTypeCollection() != null) {
                info = ExprDotEvalTypeInfo.componentColl(rootLambdaEvaluator.getComponentTypeCollection());
            }
            else {
                rootLambdaEvaluator = null; // not a lambda evaluator
            }
        }
        else if (inputExpression instanceof ExprIdentNode) {
            ExprIdentNode identNode = (ExprIdentNode) inputExpression;
            int streamId = identNode.getStreamId();
            EventType streamType = streamTypeService.getEventTypes()[streamId];
            return getPropertyEnumerationSource(identNode.getResolvedPropertyName(), streamId, streamType, hasEnumerationMethod);
        }
        return new Pair<ExprEvaluatorEnumeration, ExprDotEvalTypeInfo>(rootLambdaEvaluator, info);
    }

    public static Pair<ExprEvaluatorEnumeration, ExprDotEvalTypeInfo> getPropertyEnumerationSource(String propertyName, int streamId, EventType streamType, boolean hasEnumerationMethod) {

        EventPropertyGetter getter = streamType.getGetter(propertyName);
        FragmentEventType fragmentEventType = streamType.getFragmentType(propertyName);
        Class propertyType = streamType.getPropertyType(propertyName);
        ExprDotEvalTypeInfo typeInfo = ExprDotEvalTypeInfo.scalarOrUnderlying(propertyType);  // assume scalar for now

        // no enumeration methods, no need to expose as an enumeration
        if (!hasEnumerationMethod) {
            return new Pair<ExprEvaluatorEnumeration, ExprDotEvalTypeInfo>(null, typeInfo);
        }

        ExprEvaluatorEnumeration enumEvaluator = null;
        if (getter != null && fragmentEventType != null && fragmentEventType.isIndexed()) {
            enumEvaluator = new PropertyExprEvaluatorEventCollection(propertyName, streamId, fragmentEventType.getFragmentType(), getter);
            typeInfo = ExprDotEvalTypeInfo.eventColl(fragmentEventType.getFragmentType());
        }
        else {
            EventPropertyDescriptor desc = EventTypeUtility.getNestablePropertyDescriptor(streamType, propertyName);
            if (desc != null && desc.isIndexed() && !desc.isRequiresIndex() && desc.getPropertyComponentType() != null) {
                if (JavaClassHelper.isImplementsInterface(propertyType, Collection.class)) {
                    enumEvaluator = new PropertyExprEvaluatorScalarCollection(propertyName, streamId, getter, desc.getPropertyComponentType());
                }
                else if (JavaClassHelper.isImplementsInterface(propertyType, Iterable.class)) {
                    enumEvaluator = new PropertyExprEvaluatorScalarIterable(propertyName, streamId, getter, desc.getPropertyComponentType());
                }
                else if (propertyType.isArray()) {
                    enumEvaluator = new PropertyExprEvaluatorScalarArray(propertyName, streamId, getter, desc.getPropertyComponentType());
                }
                else {
                    throw new IllegalStateException("Property indicated indexed-type but failed to find proper collection adapter for use with enumeration methods");
                }
                typeInfo = ExprDotEvalTypeInfo.componentColl(desc.getPropertyComponentType());
            }
        }
        return new Pair<ExprEvaluatorEnumeration, ExprDotEvalTypeInfo>(enumEvaluator, typeInfo);
    }

    private ExprValidationException handleNotFound(String name) {
        return new ExprValidationException("Unknown single-row function, expression declaration, script or aggregation function named '" + name + "' could not be resolved");
    }
}

