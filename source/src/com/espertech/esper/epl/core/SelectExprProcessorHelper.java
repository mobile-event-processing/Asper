/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.client.*;
import com.espertech.esper.epl.core.eval.*;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.spec.CreateSchemaDesc;
import com.espertech.esper.epl.spec.InsertIntoDesc;
import com.espertech.esper.epl.spec.SelectClauseExprCompiledSpec;
import com.espertech.esper.epl.spec.SelectClauseStreamCompiledSpec;
import com.espertech.esper.event.*;
import com.espertech.esper.event.arr.ObjectArrayEventType;
import com.espertech.esper.event.bean.BeanEventType;
import com.espertech.esper.event.map.MapEventType;
import com.espertech.esper.event.vaevent.ValueAddEventProcessor;
import com.espertech.esper.event.vaevent.ValueAddEventService;
import com.espertech.esper.event.vaevent.VariantEventType;
import com.espertech.esper.util.CollectionUtil;
import com.espertech.esper.util.EventRepresentationUtil;
import com.espertech.esper.util.JavaClassHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Processor for select-clause expressions that handles a list of selection items represented by
 * expression nodes. Computes results based on matching events.
 */
public class SelectExprProcessorHelper
{
	private static final Log log = LogFactory.getLog(SelectExprProcessorHelper.class);

    private final Collection<Integer> assignedTypeNumberStack;
    private final List<SelectClauseExprCompiledSpec> selectionList;
    private final List<SelectExprStreamDesc> selectedStreams;
    private final InsertIntoDesc insertIntoDesc;
    private final boolean isUsingWildcard;
    private final StreamTypeService typeService;
    private final EventAdapterService eventAdapterService;
    private final ValueAddEventService valueAddEventService;
    private final SelectExprEventTypeRegistry selectExprEventTypeRegistry;
    private final MethodResolutionService methodResolutionService;
    private final String statementId;
    private final Annotation[] annotations;
    private final ConfigurationInformation configuration;

    /**
     * Ctor.
     * @param selectionList - list of select-clause items
     * @param insertIntoDesc - descriptor for insert-into clause contains column names overriding select clause names
     * @param isUsingWildcard - true if the wildcard (*) appears in the select clause
     * @param typeService -service for information about streams
     * @param eventAdapterService - service for generating events and handling event types
     * @param valueAddEventService - service that handles update events
     * @param selectExprEventTypeRegistry - service for statement to type registry
     * @param methodResolutionService - for resolving methods
     * @throws com.espertech.esper.epl.expression.ExprValidationException thrown if any of the expressions don't validate
     */
    public SelectExprProcessorHelper(Collection<Integer> assignedTypeNumberStack,
                                   List<SelectClauseExprCompiledSpec> selectionList,
                                   List<SelectExprStreamDesc> selectedStreams,
                                   InsertIntoDesc insertIntoDesc,
                                   boolean isUsingWildcard,
                                   StreamTypeService typeService,
                                   EventAdapterService eventAdapterService,
                                   ValueAddEventService valueAddEventService,
                                   SelectExprEventTypeRegistry selectExprEventTypeRegistry,
                                   MethodResolutionService methodResolutionService,
                                   String statementId,
                                   Annotation[] annotations,
                                   ConfigurationInformation configuration) throws ExprValidationException
    {
        this.assignedTypeNumberStack = assignedTypeNumberStack;
        this.selectionList = selectionList;
        this.selectedStreams = selectedStreams;
        this.insertIntoDesc = insertIntoDesc;
        this.eventAdapterService = eventAdapterService;
        this.isUsingWildcard = isUsingWildcard;
        this.typeService = typeService;
        this.valueAddEventService = valueAddEventService;
        this.selectExprEventTypeRegistry = selectExprEventTypeRegistry;
        this.methodResolutionService = methodResolutionService;
        this.statementId = statementId;
        this.annotations = annotations;
        this.configuration = configuration;
    }

    public SelectExprProcessor getEvaluator() throws ExprValidationException {

        // Get the named and un-named stream selectors (i.e. select s0.* from S0 as s0), if any
        List<SelectClauseStreamCompiledSpec> namedStreams = new ArrayList<SelectClauseStreamCompiledSpec>();
        List<SelectExprStreamDesc> unnamedStreams = new ArrayList<SelectExprStreamDesc>();
        for (SelectExprStreamDesc spec : selectedStreams)
        {
            if (spec.getStreamSelected() != null && spec.getStreamSelected().getOptionalName() == null) {
                unnamedStreams.add(spec);
            }
            else if (spec.getExpressionSelectedAsStream() != null) { // handle special "transpose(...)" function
                unnamedStreams.add(spec);
            }
            else
            {
                namedStreams.add(spec.getStreamSelected());
                if (spec.getStreamSelected().isProperty())
                {
                    throw new ExprValidationException("The property wildcard syntax must be used without column name");
                }
            }
        }

        // Error if there are more then one un-named streams (i.e. select s0.*, s1.* from S0 as s0, S1 as s1)
        // Thus there is only 1 unnamed stream selector maximum.
        if (unnamedStreams.size() > 1)
        {
            throw new ExprValidationException("A column name must be supplied for all but one stream if multiple streams are selected via the stream.* notation");
        }

        if (selectedStreams.isEmpty() && selectionList.isEmpty() && !isUsingWildcard)
        {
            throw new IllegalArgumentException("Empty selection list not supported");
        }

        for (SelectClauseExprCompiledSpec entry : selectionList)
        {
            if (entry.getAssignedName() == null)
            {
                throw new IllegalArgumentException("Expected name for each expression has not been supplied");
            }
        }

        // Verify insert into clause
        if (insertIntoDesc != null)
        {
            verifyInsertInto(insertIntoDesc, selectionList);
        }

        // Build a subordinate wildcard processor for joins
        SelectExprProcessor joinWildcardProcessor = null;
        if(typeService.getStreamNames().length > 1 && isUsingWildcard)
        {
        	joinWildcardProcessor = SelectExprJoinWildcardProcessorFactory.create(assignedTypeNumberStack, statementId, typeService.getStreamNames(), typeService.getEventTypes(), eventAdapterService, null, selectExprEventTypeRegistry, methodResolutionService, annotations, configuration);
        }

        // Resolve underlying event type in the case of wildcard select
        EventType eventType = null;
        boolean singleStreamWrapper = false;
        if(isUsingWildcard)
        {
        	if(joinWildcardProcessor != null)
        	{
        		eventType = joinWildcardProcessor.getResultEventType();
        	}
        	else
        	{
        		eventType = typeService.getEventTypes()[0];
        		if(eventType instanceof WrapperEventType)
        		{
        			singleStreamWrapper = true;
        		}
        	}
        }

        // Get expression nodes
        ExprEvaluator[] exprEvaluators = new ExprEvaluator[selectionList.size()];
        ExprNode[] exprNodes = new ExprNode[selectionList.size()];
        Object[] expressionReturnTypes = new Object[selectionList.size()];
        for (int i = 0; i < selectionList.size(); i++)
        {
            ExprNode expr = selectionList.get(i).getSelectExpression();
            exprNodes[i] = expr;
            exprEvaluators[i] = expr.getExprEvaluator();
            Map<String, Object> eventTypeExpr = exprEvaluators[i].getEventType();
            if (eventTypeExpr == null) {
                expressionReturnTypes[i] = exprEvaluators[i].getType();
            }
            else {
                final ExprEvaluator innerExprEvaluator = expr.getExprEvaluator();
                final EventType mapType = eventAdapterService.createAnonymousMapType(statementId + "_innereval_" + CollectionUtil.toString(assignedTypeNumberStack, "_") + "_" + i, eventTypeExpr);
                ExprEvaluator evaluatorFragment = new ExprEvaluator() {
                    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
                    {
                        Map<String, Object> values = (Map<String, Object>) innerExprEvaluator.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
                        if (values == null) {
                            values = Collections.emptyMap();
                        }
                        return eventAdapterService.adapterForTypedMap(values, mapType);
                    }
                    public Class getType()
                    {
                        return Map.class;
                    }
                    public Map<String, Object> getEventType() {
                        return null;
                    }
                };

                expressionReturnTypes[i] = mapType;
                exprEvaluators[i] = evaluatorFragment;
            }
        }

        // Get column names
        String[] columnNames;
        String[] columnNamesAsProvided;
        if ((insertIntoDesc != null) && (!insertIntoDesc.getColumnNames().isEmpty()))
        {
            columnNames = insertIntoDesc.getColumnNames().toArray(new String[insertIntoDesc.getColumnNames().size()]);
            columnNamesAsProvided = columnNames;
        }
        else  if (!selectedStreams.isEmpty()) { // handle stream selection column names
            int numStreamColumnsJoin = 0;
            if (isUsingWildcard && typeService.getEventTypes().length > 1)
            {
                numStreamColumnsJoin = typeService.getEventTypes().length;
            }
            columnNames = new String[selectionList.size() + namedStreams.size() + numStreamColumnsJoin];
            columnNamesAsProvided = new String[columnNames.length];
            int count = 0;
            for (SelectClauseExprCompiledSpec aSelectionList : selectionList)
            {
                columnNames[count] = aSelectionList.getAssignedName();
                columnNamesAsProvided[count] = aSelectionList.getProvidedName();
                count++;
            }
            for (SelectClauseStreamCompiledSpec aSelectionList : namedStreams)
            {
                columnNames[count] = aSelectionList.getOptionalName();
                columnNamesAsProvided[count] = aSelectionList.getOptionalName();
                count++;
            }
            // for wildcard joins, add the streams themselves
            if (isUsingWildcard && typeService.getEventTypes().length > 1)
            {
                for (String streamName : typeService.getStreamNames())
                {
                    columnNames[count] = streamName;
                    columnNamesAsProvided[count] = streamName;
                    count++;
                }
            }
        }
        else    // handle regular column names
        {
            columnNames = new String[selectionList.size()];
            columnNamesAsProvided = new String[selectionList.size()];
            for (int i = 0; i < selectionList.size(); i++)
            {
                columnNames[i] = selectionList.get(i).getAssignedName();
                columnNamesAsProvided[i] = selectionList.get(i).getProvidedName();
            }
        }

        // Find if there is any fragments selected
        EventType targetType= null;
        if (insertIntoDesc != null)
        {
            targetType = eventAdapterService.getExistsTypeByName(insertIntoDesc.getEventTypeName());
        }

        // Find if there is any fragment event types:
        // This is a special case for fragments: select a, b from pattern [a=A -> b=B]
        // We'd like to maintain 'A' and 'B' EventType in the Map type, and 'a' and 'b' EventBeans in the event bean
        for (int i = 0; i < selectionList.size(); i++)
        {
            if (!(exprNodes[i] instanceof ExprIdentNode))
            {
                continue;
            }

            ExprIdentNode identNode = (ExprIdentNode) exprNodes[i];
            String propertyName = identNode.getResolvedPropertyName();
            final int streamNum = identNode.getStreamId();

            EventType eventTypeStream = typeService.getEventTypes()[streamNum];
            if (eventTypeStream instanceof NativeEventType)
            {
                continue;   // we do not transpose the native type for performance reasons
            }

            FragmentEventType fragmentType = eventTypeStream.getFragmentType(propertyName);
            if ((fragmentType == null) || (fragmentType.isNative()))
            {
                continue;   // we also ignore native Java classes as fragments for performance reasons
            }

            // may need to unwrap the fragment if the target type has this underlying type
            FragmentEventType targetFragment = null;
            if (targetType != null)
            {
                targetFragment = targetType.getFragmentType(columnNames[i]);
            }
            if ((targetType != null) &&
                (fragmentType.getFragmentType().getUnderlyingType() == expressionReturnTypes[i]) &&
                ((targetFragment == null) || (targetFragment != null && targetFragment.isNative())) )
            {
                ExprEvaluator evaluatorFragment;

                // A match was found, we replace the expression
                final EventPropertyGetter getter = eventTypeStream.getGetter(propertyName);
                final Class returnType = eventTypeStream.getPropertyType(propertyName);
                evaluatorFragment = new ExprEvaluator() {
                    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
                    {
                        EventBean streamEvent = eventsPerStream[streamNum];
                        if (streamEvent == null)
                        {
                            return null;
                        }
                        return getter.get(streamEvent);
                    }
                    public Class getType()
                    {
                        return returnType;
                    }
                    @Override
                    public Map<String, Object> getEventType() {
                        return null;
                    }
                };
                exprEvaluators[i] = evaluatorFragment;
            }
            // same for arrays: may need to unwrap the fragment if the target type has this underlying type
            else if ((targetType != null) && expressionReturnTypes[i] instanceof Class &&
                (fragmentType.getFragmentType().getUnderlyingType() == ((Class) expressionReturnTypes[i]).getComponentType()) &&
                ((targetFragment == null) || (targetFragment != null && targetFragment.isNative())) )
            {
                ExprEvaluator evaluatorFragment;
                final EventPropertyGetter getter = eventTypeStream.getGetter(propertyName);
                final Class returnType = JavaClassHelper.getArrayType(eventTypeStream.getPropertyType(propertyName));
                evaluatorFragment = new ExprEvaluator() {
                    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
                    {
                        EventBean streamEvent = eventsPerStream[streamNum];
                        if (streamEvent == null)
                        {
                            return null;
                        }
                        return getter.get(streamEvent);
                    }
                    public Class getType()
                    {
                        return returnType;
                    }
                    @Override
                    public Map<String, Object> getEventType() {
                        return null;
                    }
                };
                exprEvaluators[i] = evaluatorFragment;
            }
            else
            {
                ExprEvaluator evaluatorFragment;
                final EventPropertyGetter getter =  eventTypeStream.getGetter(propertyName);
                final Class returnType = eventTypeStream.getFragmentType(propertyName).getFragmentType().getUnderlyingType();

                // A match was found, we replace the expression
                evaluatorFragment = new ExprEvaluator() {

                    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
                    {
                        EventBean streamEvent = eventsPerStream[streamNum];
                        if (streamEvent == null)
                        {
                            return null;
                        }
                        return getter.getFragment(streamEvent);
                    }

                    public Class getType()
                    {
                        return returnType;
                    }

                    public Map<String, Object> getEventType() {
                        return null;
                    }
                };

                exprEvaluators[i] = evaluatorFragment;
                if (!fragmentType.isIndexed())
                {
                    expressionReturnTypes[i] = fragmentType.getFragmentType();
                }
                else
                {
                    expressionReturnTypes[i] = new EventType[] {fragmentType.getFragmentType()};
                }
            }
        }

        // Find if there is any stream expression (ExprStreamNode) :
        // This is a special case for stream selection: select a, b from A as a, B as b
        // We'd like to maintain 'A' and 'B' EventType in the Map type, and 'a' and 'b' EventBeans in the event bean
        for (int i = 0; i < selectionList.size(); i++)
        {
            if (!(exprEvaluators[i] instanceof ExprStreamUnderlyingNode))
            {
                continue;
            }

            ExprStreamUnderlyingNode undNode = (ExprStreamUnderlyingNode) exprEvaluators[i];
            final int streamNum = undNode.getStreamId();
            final Class returnType = undNode.getExprEvaluator().getType();
            EventType eventTypeStream = typeService.getEventTypes()[streamNum];

            // A match was found, we replace the expression
            ExprEvaluator evaluator = new ExprEvaluator() {

                public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext)
                {
                    return eventsPerStream[streamNum];
                }

                public Class getType()
                {
                    return returnType;
                }

                public Map<String, Object> getEventType() {
                    return null;
                }
            };

            exprEvaluators[i] = evaluator;
            expressionReturnTypes[i] = eventTypeStream;
        }

        // Build event type that reflects all selected properties
        Map<String, Object> selPropertyTypes = new LinkedHashMap<String, Object>();
        int count = 0;
        for (int i = 0; i < exprEvaluators.length; i++)
        {
            Object expressionReturnType = expressionReturnTypes[count];
            selPropertyTypes.put(columnNames[count], expressionReturnType);
            count++;
        }
        if (!selectedStreams.isEmpty()) {
            for (SelectClauseStreamCompiledSpec element : namedStreams)
            {
                EventType eventTypeStream = typeService.getEventTypes()[element.getStreamNumber()];
                selPropertyTypes.put(columnNames[count], eventTypeStream);
                count++;
            }
            if (isUsingWildcard && typeService.getEventTypes().length > 1)
            {
                for (int i = 0; i < typeService.getEventTypes().length; i++)
                {
                    EventType eventTypeStream = typeService.getEventTypes()[i];
                    selPropertyTypes.put(columnNames[count], eventTypeStream);
                    count++;
                }
            }
        }

        // Handle stream selection
        EventType underlyingEventType = null;
        int underlyingStreamNumber = 0;
        boolean underlyingIsFragmentEvent = false;
        EventPropertyGetter underlyingPropertyEventGetter = null;
        ExprEvaluator underlyingExprEvaluator = null;
        boolean useMapOutput = EventRepresentationUtil.isMap(annotations, configuration, CreateSchemaDesc.AssignedType.NONE);

        if (!selectedStreams.isEmpty()) {
            // Resolve underlying event type in the case of wildcard or non-named stream select.
            // Determine if the we are considering a tagged event or a stream name.
            if((isUsingWildcard) || (!unnamedStreams.isEmpty()))
            {
                if (!unnamedStreams.isEmpty())
                {
                    if (unnamedStreams.get(0).getStreamSelected() != null) {
                        SelectClauseStreamCompiledSpec streamSpec = unnamedStreams.get(0).getStreamSelected();

                        // the tag.* syntax for :  select tag.* from pattern [tag = A]
                        underlyingStreamNumber = streamSpec.getStreamNumber();
                        if (streamSpec.isFragmentEvent())
                        {
                            EventType compositeMap = typeService.getEventTypes()[underlyingStreamNumber];
                            FragmentEventType fragment = compositeMap.getFragmentType(streamSpec.getStreamName());
                            underlyingEventType = fragment.getFragmentType();
                            underlyingIsFragmentEvent = true;
                        }
                        // the property.* syntax for :  select property.* from A
                        else if (streamSpec.isProperty())
                        {
                            String propertyName = streamSpec.getStreamName();
                            Class propertyType = streamSpec.getPropertyType();
                            int streamNumber = streamSpec.getStreamNumber();

                            if (JavaClassHelper.isJavaBuiltinDataType(streamSpec.getPropertyType()))
                            {
                                throw new ExprValidationException("The property wildcard syntax cannot be used on built-in types as returned by property '" + propertyName + "'");
                            }

                            // create or get an underlying type for that Class
                            underlyingEventType = eventAdapterService.addBeanType(propertyType.getName(), propertyType, false, false, false);
                            selectExprEventTypeRegistry.add(underlyingEventType);
                            underlyingPropertyEventGetter = typeService.getEventTypes()[streamNumber].getGetter(propertyName);
                            if (underlyingPropertyEventGetter == null)
                            {
                                throw new ExprValidationException("Unexpected error resolving property getter for property " + propertyName);
                            }
                        }
                        // the stream.* syntax for:  select a.* from A as a
                        else
                        {
                            underlyingEventType = typeService.getEventTypes()[underlyingStreamNumber];
                        }
                    }
                    // handle case where the unnamed stream is a "transpose" function
                    else {
                        ExprNode expression = unnamedStreams.get(0).getExpressionSelectedAsStream().getSelectExpression();
                        Class returnType = expression.getExprEvaluator().getType();
                        underlyingEventType = eventAdapterService.addBeanType(returnType.getName(), returnType, false, false, false);
                        selectExprEventTypeRegistry.add(underlyingEventType);
                        underlyingExprEvaluator = expression.getExprEvaluator();
                    }
                }
                else
                {
                    // no un-named stream selectors, but a wildcard was specified
                    if (typeService.getEventTypes().length == 1)
                    {
                        // not a join, we are using the selected event
                        underlyingEventType = typeService.getEventTypes()[0];
                        if(underlyingEventType instanceof WrapperEventType)
                        {
                            singleStreamWrapper = true;
                        }
                    }
                    else
                    {
                        // For joins, all results are placed in a map with properties for each stream
                        underlyingEventType = null;
                    }
                }
            }
        }

        SelectExprContext selectExprContext = new SelectExprContext(exprEvaluators, columnNames, eventAdapterService);

        if (insertIntoDesc == null)
        {
            if (!selectedStreams.isEmpty()) {
                EventType resultEventType;
                if (underlyingEventType != null)
                {
                    resultEventType = eventAdapterService.createAnonymousWrapperType(statementId + "_wrapout_" + CollectionUtil.toString(assignedTypeNumberStack, "_"), underlyingEventType, selPropertyTypes);
                    return new EvalSelectStreamWUnderlying(selectExprContext, resultEventType, namedStreams, isUsingWildcard,
                            unnamedStreams, singleStreamWrapper, underlyingIsFragmentEvent, underlyingStreamNumber, underlyingPropertyEventGetter, underlyingExprEvaluator);
                }
                else
                {
                    resultEventType = eventAdapterService.createAnonymousMapType(statementId + "_mapout_" + CollectionUtil.toString(assignedTypeNumberStack, "_"), selPropertyTypes);
                    return new EvalSelectStreamNoUnderlyingMap(selectExprContext, resultEventType, namedStreams, isUsingWildcard);
                }
            }

            if (isUsingWildcard)
            {
        	    EventType resultEventType = eventAdapterService.createAnonymousWrapperType(statementId + "_wrapoutwild_" + CollectionUtil.toString(assignedTypeNumberStack, "_"), eventType, selPropertyTypes);
                if (singleStreamWrapper) {
                    return new EvalSelectWildcardSSWrapper(selectExprContext, resultEventType);
                }
                if (joinWildcardProcessor == null) {
                    return new EvalSelectWildcard(selectExprContext, resultEventType);
                }
                return new EvalSelectWildcardJoin(selectExprContext, resultEventType, joinWildcardProcessor);
            }

            EventType resultEventType;
            if (!useMapOutput) {
                resultEventType = eventAdapterService.createAnonymousObjectArrayType(statementId + "_result_" + CollectionUtil.toString(assignedTypeNumberStack, "_"), selPropertyTypes);
            }
            else {
                resultEventType = eventAdapterService.createAnonymousMapType(statementId + "_result_" + CollectionUtil.toString(assignedTypeNumberStack, "_"), selPropertyTypes);
            }
            if (selectExprContext.getExpressionNodes().length == 0) {
                return new EvalSelectNoWildcardEmptyProps(selectExprContext, resultEventType);
            }
            else {
                if (!useMapOutput) {
                    return new EvalSelectNoWildcardObjectArray(selectExprContext, resultEventType);
                }
                return new EvalSelectNoWildcardMap(selectExprContext, resultEventType);
            }
        }

        EventType vaeInnerEventType = null;
        boolean singleColumnWrapOrBeanCoercion = false;       // Additional single-column coercion for non-wrapped type done by SelectExprInsertEventBeanFactory
        boolean isRevisionEvent = false;

        try
        {
            if (!selectedStreams.isEmpty()) {
                EventType resultEventType;
                if (underlyingEventType != null)    // a single stream was selected via "stream.*" and there is no column name
                {
                    // recast as a Map-type
                    if (underlyingEventType instanceof MapEventType && targetType instanceof MapEventType) {
                        return new EvalSelectStreamWUnderlyingRecastMap(selectExprContext, selectedStreams.get(0).getStreamSelected().getStreamNumber(), targetType);
                    }

                    // recast as a Object-array-type
                    if (underlyingEventType instanceof ObjectArrayEventType && targetType instanceof ObjectArrayEventType) {
                        return new EvalSelectStreamWUnderlyingRecastObjectArray(selectExprContext, selectedStreams.get(0).getStreamSelected().getStreamNumber(), targetType);
                    }

                    // recast as a Bean-type
                    if (underlyingEventType instanceof BeanEventType && targetType instanceof BeanEventType) {
                        SelectClauseExprCompiledSpec expressionAsStream = selectedStreams.get(0).getExpressionSelectedAsStream();
                        if (expressionAsStream != null) {
                            return new EvalSelectStreamWUnderlyingRecastBean(selectExprContext, expressionAsStream, underlyingEventType, targetType, exprEvaluators.length);
                        }
                        else {
                            return new EvalInsertBeanRecast(targetType, eventAdapterService, selectedStreams.get(0).getStreamSelected().getStreamNumber(), typeService.getEventTypes());
                        }
                    }

                    // wrap if no recast possible
                    resultEventType = eventAdapterService.addWrapperType(insertIntoDesc.getEventTypeName(), underlyingEventType, selPropertyTypes, false, true);
                    return new EvalSelectStreamWUnderlying(selectExprContext, resultEventType, namedStreams, isUsingWildcard,
                            unnamedStreams, singleStreamWrapper, underlyingIsFragmentEvent, underlyingStreamNumber, underlyingPropertyEventGetter, underlyingExprEvaluator);
                }
                else    // there are one or more streams selected with column name such as "stream.* as columnOne"
                {
                    EventType existingType = eventAdapterService.getExistsTypeByName(insertIntoDesc.getEventTypeName());
                    if (existingType instanceof BeanEventType) {
                        String name = selectedStreams.get(0).getStreamSelected().getStreamName();
                        String alias = selectedStreams.get(0).getStreamSelected().getOptionalName();
                        String syntaxUsed = name + ".*" + (alias != null ? " as " + alias : "");
                        String syntaxInstead = name + (alias != null ? " as " + alias : "");
                        throw new ExprValidationException("The '" + syntaxUsed + "' syntax is not allowed when inserting into an existing bean event type, use the '" + syntaxInstead + "' syntax instead");
                    }
                    if (existingType == null || existingType instanceof MapEventType) {
                        resultEventType = eventAdapterService.addNestableMapType(insertIntoDesc.getEventTypeName(), selPropertyTypes, null, false, false, false, false, true);
                        Set<String> propertiesToUnwrap = getEventBeanToObjectProps(selPropertyTypes, resultEventType);
                        if (propertiesToUnwrap.isEmpty()) {
                            return new EvalSelectStreamNoUnderlyingMap(selectExprContext, resultEventType, namedStreams, isUsingWildcard);
                        }
                        else {
                            return new EvalSelectStreamNoUndWEventBeanToObj(selectExprContext, resultEventType, namedStreams, isUsingWildcard, propertiesToUnwrap);
                        }
                    }
                    else {
                        Set<String> propertiesToUnwrap = getEventBeanToObjectProps(selPropertyTypes, existingType);
                        if (propertiesToUnwrap.isEmpty()) {
                            return new EvalSelectStreamNoUnderlyingObjectArray(selectExprContext, existingType, namedStreams, isUsingWildcard);
                        }
                        else {
                            return new EvalSelectStreamNoUndWEventBeanToObjObjArray(selectExprContext, existingType, namedStreams, isUsingWildcard, propertiesToUnwrap);
                        }
                    }
                }
            }

            ValueAddEventProcessor vaeProcessor = valueAddEventService.getValueAddProcessor(insertIntoDesc.getEventTypeName());
            EventType resultEventType;
            if (isUsingWildcard)
            {
                if (vaeProcessor != null)
                {
                    resultEventType = vaeProcessor.getValueAddEventType();
                    isRevisionEvent = true;
                    vaeProcessor.validateEventType(eventType);
                }
                else
                {
                    EventType existingType = eventAdapterService.getExistsTypeByName(insertIntoDesc.getEventTypeName());

                    SelectExprProcessor existingTypeProcessor = null;
                    if (existingType != null)
                    {
                        // we may get away with re-casting an existing bean-event-type event to another bean-event-type
                        if ( (existingType instanceof BeanEventType) &&
                             (typeService.getEventTypes()[0] instanceof BeanEventType) &&
                             (selPropertyTypes.isEmpty())) {
                            return new EvalInsertBeanRecast(existingType, eventAdapterService, 0, typeService.getEventTypes());
                        }
                        if ( (existingType instanceof WrapperEventType) &&
                             (typeService.getEventTypes()[0] instanceof BeanEventType) &&
                             (exprEvaluators.length == 0)) {

                            WrapperEventType wrapperType = (WrapperEventType) existingType;
                            if (wrapperType.getUnderlyingEventType() instanceof BeanEventType) {
                                return new EvalInsertBeanWrapRecast(wrapperType, eventAdapterService, 0, typeService.getEventTypes());
                            }
                        }

                        existingTypeProcessor = SelectExprInsertEventBeanFactory.getInsertUnderlyingNonJoin(eventAdapterService, existingType, isUsingWildcard, typeService, exprEvaluators, columnNames, expressionReturnTypes, methodResolutionService.getEngineImportService(), insertIntoDesc, columnNamesAsProvided);
                    }

                    if (existingTypeProcessor != null) {
                        return existingTypeProcessor;
                    }
                    else if (existingType != null && selPropertyTypes.isEmpty() && existingType instanceof MapEventType) {
                        resultEventType = existingType;
                        return new EvalInsertCoercionMap(resultEventType, eventAdapterService);
                    }
                    else if (existingType != null && selPropertyTypes.isEmpty() && existingType instanceof ObjectArrayEventType) {
                        resultEventType = existingType;
                        return new EvalInsertCoercionObjectArray(resultEventType, eventAdapterService);
                    }
                    else if (selPropertyTypes.isEmpty() && eventType instanceof BeanEventType) {
                        BeanEventType beanEventType = (BeanEventType) eventType;
                        resultEventType = eventAdapterService.addBeanTypeByName(insertIntoDesc.getEventTypeName(), beanEventType.getUnderlyingType(), false);
                    }
                    else {
                        resultEventType = eventAdapterService.addWrapperType(insertIntoDesc.getEventTypeName(), eventType, selPropertyTypes, false, true);
                    }
                }

                if (singleStreamWrapper) {
                    if (!isRevisionEvent) {
                        return new EvalInsertWildcardSSWrapper(selectExprContext, resultEventType);
                    }
                    else {
                        return new EvalInsertWildcardSSWrapperRevision(selectExprContext, resultEventType, vaeProcessor);
                    }
                }
                if (joinWildcardProcessor == null) {
                    if (!isRevisionEvent) {
                        if (resultEventType instanceof WrapperEventType) {
                            return new EvalInsertWildcardWrapper(selectExprContext, resultEventType);
                        }
                        else {
                            return new EvalInsertWildcardBean(selectExprContext, resultEventType);
                        }
                    }
                    else {
                        if (exprEvaluators.length == 0) {
                            return new EvalInsertWildcardRevision(selectExprContext, resultEventType, vaeProcessor);
                        }
                        else {
                           EventType wrappingEventType = eventAdapterService.addWrapperType(insertIntoDesc.getEventTypeName() + "_wrapped", eventType, selPropertyTypes, false, true);
                           return new EvalInsertWildcardRevisionWrapper(selectExprContext, resultEventType, vaeProcessor, wrappingEventType);
                        }
                    }
                }
                else {
                    if (!isRevisionEvent) {
                        return new EvalInsertWildcardJoin(selectExprContext, resultEventType, joinWildcardProcessor);
                    }
                    else {
                        return new EvalInsertWildcardJoinRevision(selectExprContext, resultEventType, joinWildcardProcessor, vaeProcessor);
                    }
                }
            }

            // not using wildcard
            resultEventType = null;
            if ((columnNames.length == 1) && (insertIntoDesc.getColumnNames().size() == 0))
            {
                EventType existingType = eventAdapterService.getExistsTypeByName(insertIntoDesc.getEventTypeName());
                if (existingType != null)
                {
                    // check if the existing type and new type are compatible
                    Object columnOneType = expressionReturnTypes[0];
                    if (existingType instanceof WrapperEventType)
                    {
                        WrapperEventType wrapperType = (WrapperEventType) existingType;
                        // Map and Object both supported
                        if (wrapperType.getUnderlyingEventType().getUnderlyingType() == columnOneType)
                        {
                            singleColumnWrapOrBeanCoercion = true;
                            resultEventType = existingType;
                        }
                    }
                    if ((existingType instanceof BeanEventType) && (columnOneType instanceof Class))
                    {
                        BeanEventType beanType = (BeanEventType) existingType;
                        // Map and Object both supported
                        if (JavaClassHelper.isSubclassOrImplementsInterface((Class) columnOneType, beanType.getUnderlyingType()))
                        {
                            singleColumnWrapOrBeanCoercion = true;
                            resultEventType = existingType;
                        }
                    }
                }
            }
            if (singleColumnWrapOrBeanCoercion) {
                if (!isRevisionEvent) {
                    if (resultEventType instanceof WrapperEventType) {
                        WrapperEventType wrapper = (WrapperEventType) resultEventType;
                        if (wrapper.getUnderlyingEventType() instanceof MapEventType) {
                            return new EvalInsertNoWildcardSingleColCoercionMapWrap(selectExprContext, resultEventType);
                        }
                        else if (wrapper.getUnderlyingEventType() instanceof ObjectArrayEventType) {
                            return new EvalInsertNoWildcardSingleColCoercionObjectArrayWrap(selectExprContext, resultEventType);
                        }
                        else if (wrapper.getUnderlyingEventType() instanceof VariantEventType) {
                            VariantEventType variantEventType = (VariantEventType) wrapper.getUnderlyingEventType();
                            vaeProcessor = valueAddEventService.getValueAddProcessor(variantEventType.getName());
                            return new EvalInsertNoWildcardSingleColCoercionBeanWrapVariant(selectExprContext, resultEventType, vaeProcessor);
                        }
                        else {
                            return new EvalInsertNoWildcardSingleColCoercionBeanWrap(selectExprContext, resultEventType);
                        }
                    }
                    else {
                        if (resultEventType instanceof BeanEventType) {
                            return new EvalInsertNoWildcardSingleColCoercionBean(selectExprContext, resultEventType);
                        }
                    }
                }
                else {
                    if (resultEventType instanceof MapEventType) {
                        return new EvalInsertNoWildcardSingleColCoercionRevisionMap(selectExprContext, resultEventType, vaeProcessor, vaeInnerEventType);
                    }
                    else if (resultEventType instanceof ObjectArrayEventType) {
                        return new EvalInsertNoWildcardSingleColCoercionRevisionObjectArray(selectExprContext, resultEventType, vaeProcessor, vaeInnerEventType);
                    }
                    else if (resultEventType instanceof BeanEventType) {
                        return new EvalInsertNoWildcardSingleColCoercionRevisionBean(selectExprContext, resultEventType, vaeProcessor, vaeInnerEventType);
                    }
                    else {
                        return new EvalInsertNoWildcardSingleColCoercionRevisionBeanWrap(selectExprContext, resultEventType, vaeProcessor, vaeInnerEventType);
                    }
                }
            }
            if (resultEventType == null)
            {
                if (vaeProcessor != null)
                {
                    // Use an anonymous type if the target is not a variant stream
                    if (valueAddEventService.getValueAddProcessor(insertIntoDesc.getEventTypeName()) == null) {
                        resultEventType = eventAdapterService.createAnonymousMapType(statementId + "_vae_" + CollectionUtil.toString(assignedTypeNumberStack, "_"), selPropertyTypes);
                    }
                    else {
                        String statementName = "stmt_" + statementId + "_insert";
                        resultEventType = eventAdapterService.addNestableMapType(statementName, selPropertyTypes, null, false, false, false, false, true);
                    }
                }
                else
                {
                    EventType existingType = eventAdapterService.getExistsTypeByName(insertIntoDesc.getEventTypeName());

                    if (existingType == null) {
                        // The type may however be an auto-import or fully-qualified class name
                        Class clazz = null;
                        try {
                            clazz = this.methodResolutionService.resolveClass(insertIntoDesc.getEventTypeName());
                        }
                        catch (EngineImportException e) {
                            log.debug("Target stream name '" + insertIntoDesc.getEventTypeName() + "' is not resolved as a class name");
                        }
                        if (clazz != null) {
                            existingType = eventAdapterService.addBeanType(clazz.getName(), clazz, false, false, false);
                        }
                    }

                    SelectExprProcessor selectExprInsertEventBean = null;
                    if (existingType != null) {
                        selectExprInsertEventBean = SelectExprInsertEventBeanFactory.getInsertUnderlyingNonJoin(eventAdapterService, existingType, isUsingWildcard, typeService, exprEvaluators, columnNames, expressionReturnTypes, methodResolutionService.getEngineImportService(), insertIntoDesc, columnNamesAsProvided);
                    }
                    if (selectExprInsertEventBean != null) {
                        return selectExprInsertEventBean;
                    }
                    else
                    {
                        boolean useMap = EventRepresentationUtil.isMap(annotations, configuration, CreateSchemaDesc.AssignedType.NONE);
                        if (useMap) {
                            resultEventType = eventAdapterService.addNestableMapType(insertIntoDesc.getEventTypeName(), selPropertyTypes, null, false, false, false, false, true);
                        }
                        else {
                            resultEventType = eventAdapterService.addNestableObjectArrayType(insertIntoDesc.getEventTypeName(), selPropertyTypes, null, false, false, false, false, true);
                        }
                    }
                }
            }
            if (vaeProcessor != null)
            {
                vaeProcessor.validateEventType(resultEventType);
                vaeInnerEventType = resultEventType;
                resultEventType = vaeProcessor.getValueAddEventType();
                isRevisionEvent = true;
            }

            if (!isRevisionEvent) {
                if (resultEventType instanceof MapEventType) {
                    return new EvalInsertNoWildcardMap(selectExprContext, resultEventType);
                }
                else {
                    return new EvalInsertNoWildcardObjectArray(selectExprContext, resultEventType);
                }
            }
            else {
                return new EvalInsertNoWildcardRevision(selectExprContext, resultEventType, vaeProcessor, vaeInnerEventType);
            }
        }
        catch (EventAdapterException ex)
        {
            log.debug("Exception provided by event adapter: " + ex.getMessage(), ex);
            throw new ExprValidationException(ex.getMessage(), ex);
        }
    }

    // Determine which properties provided by the Map must be downcast from EventBean to Object
    private static Set<String> getEventBeanToObjectProps(Map<String, Object> selPropertyTypes, EventType resultEventType) {

        if (!(resultEventType instanceof BaseNestableEventType)) {
            return Collections.emptySet();
        }
        BaseNestableEventType mapEventType = (BaseNestableEventType) resultEventType;
        Set<String> props = null;
        for (Map.Entry<String, Object> entry : selPropertyTypes.entrySet()) {
            if (entry.getValue() instanceof BeanEventType && mapEventType.getTypes().get(entry.getKey()) instanceof Class) {
                if (props == null) {
                    props = new HashSet<String>();
                }
                props.add(entry.getKey());
            }
        }
        if (props == null) {
            return Collections.emptySet();
        }
        return props;
    }

    private static void verifyInsertInto(InsertIntoDesc insertIntoDesc,
                                         List<SelectClauseExprCompiledSpec> selectionList)
        throws ExprValidationException
    {
        // Verify all column names are unique
        Set<String> names = new HashSet<String>();
        for (String element : insertIntoDesc.getColumnNames())
        {
            if (names.contains(element))
            {
                throw new ExprValidationException("Property name '" + element + "' appears more then once in insert-into clause");
            }
            names.add(element);
        }

        // Verify number of columns matches the select clause
        if ( (!insertIntoDesc.getColumnNames().isEmpty()) &&
             (insertIntoDesc.getColumnNames().size() != selectionList.size()) )
        {
            throw new ExprValidationException("Number of supplied values in the select clause does not match insert-into clause");
        }
    }
}
