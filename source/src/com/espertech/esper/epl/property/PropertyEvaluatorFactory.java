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

package com.espertech.esper.epl.property;

import com.espertech.esper.client.*;
import com.espertech.esper.epl.core.*;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.EventAdapterServiceHelper;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.util.UuidGenerator;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Factory for property evaluators.
 */
public class PropertyEvaluatorFactory
{
    /**
     * Makes the property evaluator.
     * @param spec is the property specification
     * @param sourceEventType the event type
     * @param optionalSourceStreamName the source stream name
     * @param eventAdapterService for event instances
     * @param methodResolutionService for resolving UDF
     * @param timeProvider provides time
     * @param variableService for resolving variables
     * @param engineURI engine URI
     * @return propert evaluator
     * @throws ExprValidationException if any expressions could not be verified
     */
    public static PropertyEvaluator makeEvaluator(PropertyEvalSpec spec,
                                                  EventType sourceEventType,
                                                  String optionalSourceStreamName,
                                                  EventAdapterService eventAdapterService,
                                                  MethodResolutionService methodResolutionService,
                                                  final TimeProvider timeProvider,
                                                  VariableService variableService,
                                                  String engineURI,
                                                  String statementId,
                                                  String statementName,
                                                  Annotation[] annotations,
                                                  Collection<Integer> assignedTypeNumberStack,
                                                  ConfigurationInformation configuration)
            throws ExprValidationException
    {
        int length = spec.getAtoms().size();
        ContainedEventEval[] containedEventEvals = new ContainedEventEval[length];
        FragmentEventType fragmentEventTypes[] = new FragmentEventType[length];
        EventType currentEventType = sourceEventType;
        ExprEvaluator whereClauses[] = new ExprEvaluator[length];

        List<EventType> streamEventTypes = new ArrayList<EventType>();
        List<String> streamNames = new ArrayList<String>();
        Map<String, Integer> streamNameAndNumber = new HashMap<String,Integer>();
        List<String> expressionTexts = new ArrayList<String>();
        ExprEvaluatorContext validateContext = new ExprEvaluatorContextTimeOnly(timeProvider);

        streamEventTypes.add(sourceEventType);
        streamNames.add(optionalSourceStreamName);
        streamNameAndNumber.put(optionalSourceStreamName, 0);
        expressionTexts.add(sourceEventType.getName());

        List<SelectClauseElementCompiled> cumulativeSelectClause = new ArrayList<SelectClauseElementCompiled>();
        for (int i = 0; i < length; i++)
        {
            PropertyEvalAtom atom = spec.getAtoms().get(i);
            ContainedEventEval containedEventEval = null;
            String expressionText = null;
            EventType streamEventType = null;
            FragmentEventType fragmentEventType = null;

            // Resolve directly as fragment event type if possible
            if (atom.getSplitterExpression() instanceof ExprIdentNode) {
                String propertyName = ((ExprIdentNode) atom.getSplitterExpression()).getFullUnresolvedName();
                fragmentEventType = currentEventType.getFragmentType(propertyName);
                if (fragmentEventType != null) {
                    EventPropertyGetter getter = currentEventType.getGetter(propertyName);
                    if (getter != null) {
                        containedEventEval = new ContainedEventEvalGetter(getter);
                        expressionText = propertyName;
                        streamEventType = fragmentEventType.getFragmentType();
                    }
                }
            }

            // evaluate splitter expression
            if (containedEventEval == null) {
                ExprNodeUtility.validatePlainExpression("contained-event expression", atom.getSplitterExpression());

                EventType[] availableTypes = streamEventTypes.toArray(new EventType[streamEventTypes.size()]);
                String[] availableStreamNames = streamNames.toArray(new String[streamNames.size()]);
                boolean[] isIStreamOnly = new boolean[streamNames.size()];
                Arrays.fill(isIStreamOnly, true);
                StreamTypeService streamTypeService = new StreamTypeServiceImpl(availableTypes, availableStreamNames, isIStreamOnly, engineURI, false);
                ExprValidationContext validationContext = new ExprValidationContext(streamTypeService, methodResolutionService, null, timeProvider, variableService, validateContext, eventAdapterService, statementName, statementId, annotations, null);
                ExprNode validatedExprNode = ExprNodeUtility.getValidatedSubtree(atom.getSplitterExpression(), validationContext);
                ExprEvaluator evaluator = validatedExprNode.getExprEvaluator();

                // determine result type
                if (atom.getOptionalResultEventType() == null) {
                    throw new ExprValidationException("Missing @type(name) declaration providing the event type name of the return type for expression '" + atom.getSplitterExpression().toExpressionString() + "'");
                }
                streamEventType = eventAdapterService.getExistsTypeByName(atom.getOptionalResultEventType());
                if (streamEventType == null) {
                    throw new ExprValidationException("Event type by name '" + atom.getOptionalResultEventType() + "' could not be found");
                }
                EventBeanFactory eventBeanFactory = EventAdapterServiceHelper.getFactoryForType(streamEventType, eventAdapterService);

                // check expression result type against eventtype expected underlying type
                Class returnType = evaluator.getType();
                if (returnType.isArray()) {
                    if ((!JavaClassHelper.isSubclassOrImplementsInterface(returnType.getComponentType(), streamEventType.getUnderlyingType()))) {
                        throw new ExprValidationException("Event type '" + streamEventType.getName() + "' underlying type " + streamEventType.getUnderlyingType().getName() +
                                " cannot be assigned a value of type " + JavaClassHelper.getClassNameFullyQualPretty(returnType));
                    }
                }
                else if (JavaClassHelper.isImplementsInterface(returnType, Iterable.class)) {
                    // fine, assumed to return the right type
                }
                else {
                    throw new ExprValidationException("Return type of expression '" + atom.getSplitterExpression().toExpressionString() + "' is '" + returnType.getName() + "', expected an Iterable or array result");
                }

                containedEventEval = new ContainedEventEvalExprNode(evaluator, eventBeanFactory);
                expressionText = validatedExprNode.toExpressionString();
                fragmentEventType = new FragmentEventType(streamEventType, true, false);
            }

            // validate where clause, if any
            streamEventTypes.add(streamEventType);
            streamNames.add(atom.getOptionalAsName());
            streamNameAndNumber.put(atom.getOptionalAsName(), i + 1);
            expressionTexts.add(expressionText);

            if (atom.getOptionalWhereClause() != null)
            {
                EventType[] whereTypes = streamEventTypes.toArray(new EventType[streamEventTypes.size()]);
                String[] whereStreamNames = streamNames.toArray(new String[streamNames.size()]);
                boolean[] isIStreamOnly = new boolean[streamNames.size()];
                Arrays.fill(isIStreamOnly, true);
                StreamTypeService streamTypeService = new StreamTypeServiceImpl(whereTypes, whereStreamNames, isIStreamOnly, engineURI, false);
                ExprValidationContext validationContext = new ExprValidationContext(streamTypeService, methodResolutionService, null, timeProvider, variableService, validateContext, eventAdapterService, statementName, statementId, annotations, null);
                whereClauses[i] = ExprNodeUtility.getValidatedSubtree(atom.getOptionalWhereClause(), validationContext).getExprEvaluator();
            }

            // validate select clause
            if (atom.getOptionalSelectClause() != null)
            {
                EventType[] whereTypes = streamEventTypes.toArray(new EventType[streamEventTypes.size()]);
                String[] whereStreamNames = streamNames.toArray(new String[streamNames.size()]);
                boolean[] isIStreamOnly = new boolean[streamNames.size()];
                Arrays.fill(isIStreamOnly, true);
                StreamTypeService streamTypeService = new StreamTypeServiceImpl(whereTypes, whereStreamNames, isIStreamOnly, engineURI, false);
                ExprValidationContext validationContext = new ExprValidationContext(streamTypeService, methodResolutionService, null, timeProvider, variableService, validateContext, eventAdapterService, statementName, statementId, annotations, null);

                for (SelectClauseElementRaw raw : atom.getOptionalSelectClause().getSelectExprList())
                {
                    if (raw instanceof SelectClauseStreamRawSpec)
                    {
                        SelectClauseStreamRawSpec rawStreamSpec = (SelectClauseStreamRawSpec) raw;
                        if (!streamNames.contains(rawStreamSpec.getStreamName()))
                        {
                            throw new ExprValidationException("Property rename '" + rawStreamSpec.getStreamName() + "' not found in path");
                        }
                        SelectClauseStreamCompiledSpec streamSpec = new SelectClauseStreamCompiledSpec(rawStreamSpec.getStreamName(), rawStreamSpec.getOptionalAsName());
                        int streamNumber = streamNameAndNumber.get(rawStreamSpec.getStreamName());
                        streamSpec.setStreamNumber(streamNumber);
                        cumulativeSelectClause.add(streamSpec);
                    }
                    else if (raw instanceof SelectClauseExprRawSpec)
                    {
                        SelectClauseExprRawSpec exprSpec = (SelectClauseExprRawSpec) raw;
                        ExprNode exprCompiled = ExprNodeUtility.getValidatedSubtree(exprSpec.getSelectExpression(), validationContext);
                        String resultName = exprSpec.getOptionalAsName();
                        if (resultName == null)
                        {
                            resultName = exprCompiled.toExpressionString();
                        }
                        cumulativeSelectClause.add(new SelectClauseExprCompiledSpec(exprCompiled, resultName, exprSpec.getOptionalAsName()));

                        String isMinimal = ExprNodeUtility.isMinimalExpression(exprCompiled);
                        if (isMinimal != null)
                        {
                            throw new ExprValidationException("Expression in a property-selection may not utilize " + isMinimal);
                        }
                    }
                    else if (raw instanceof SelectClauseElementWildcard)
                    {
                        // wildcards are stream selects: we assign a stream name (any) and add a stream wildcard select
                        String streamNameAtom = atom.getOptionalAsName();
                        if (streamNameAtom == null)
                        {
                            streamNameAtom = UuidGenerator.generate();
                        }

                        SelectClauseStreamCompiledSpec streamSpec = new SelectClauseStreamCompiledSpec(streamNameAtom, atom.getOptionalAsName());
                        int streamNumber = i + 1;
                        streamSpec.setStreamNumber(streamNumber);
                        cumulativeSelectClause.add(streamSpec);
                    }
                    else
                    {
                        throw new IllegalStateException("Unknown select clause item:" + raw);
                    }
                }
            }

            currentEventType = fragmentEventType.getFragmentType();
            fragmentEventTypes[i] = fragmentEventType;
            containedEventEvals[i] = containedEventEval;
        }

        if (cumulativeSelectClause.isEmpty())
        {
            if (length == 1)
            {
                return new PropertyEvaluatorSimple(containedEventEvals[0], fragmentEventTypes[0], whereClauses[0], expressionTexts.get(0));
            }
            else
            {
                return new PropertyEvaluatorNested(containedEventEvals, fragmentEventTypes, whereClauses, expressionTexts);
            }
        }
        else
        {
            PropertyEvaluatorAccumulative accumulative = new PropertyEvaluatorAccumulative(containedEventEvals, fragmentEventTypes, whereClauses, expressionTexts);

            EventType[] whereTypes = streamEventTypes.toArray(new EventType[streamEventTypes.size()]);
            String[] whereStreamNames = streamNames.toArray(new String[streamNames.size()]);
            boolean[] isIStreamOnly = new boolean[streamNames.size()];
            Arrays.fill(isIStreamOnly, true);
            StreamTypeService streamTypeService = new StreamTypeServiceImpl(whereTypes, whereStreamNames, isIStreamOnly, engineURI, false);

            SelectExprProcessor selectExpr = SelectExprProcessorFactory.getProcessor(assignedTypeNumberStack, cumulativeSelectClause, false, null, null, streamTypeService, eventAdapterService, null, null, null, methodResolutionService, validateContext, variableService, timeProvider, engineURI, statementId, statementName, annotations, null, configuration, null);
            return new PropertyEvaluatorSelect(selectExpr, accumulative);
        }
    }
}