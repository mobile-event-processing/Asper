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

package com.espertech.esper.dataflow.ops;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.dataflow.EPDataFlowSignalFinalMarker;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.dataflow.annotations.DataFlowContext;
import com.espertech.esper.dataflow.annotations.DataFlowOpParameter;
import com.espertech.esper.dataflow.annotations.DataFlowOperator;
import com.espertech.esper.dataflow.interfaces.*;
import com.espertech.esper.dataflow.util.GraphTypeDesc;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.event.EventBeanManufacturer;
import com.espertech.esper.event.EventTypeUtility;
import com.espertech.esper.event.WriteablePropertyDescriptor;
import com.espertech.esper.util.TypeWidener;
import com.espertech.esper.util.TypeWidenerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

@DataFlowOperator
public class BeaconSource implements DataFlowSourceOperator {
    private static final Log log = LogFactory.getLog(BeaconSource.class);

    private final static List<String> PARAMETER_PROPERTIES = Arrays.asList("iterations", "initialDelay", "period");

    @DataFlowContext
    private EPDataFlowEmitter graphContext;

    @DataFlowOpParameter
    private long iterations;

    @DataFlowOpParameter
    private double initialDelay;

    @DataFlowOpParameter
    private double interval;

    private Map<String, Object> allProperties = new LinkedHashMap<String, Object>();

    private long initialDelayMSec;
    private long periodDelayMSec;
    private long lastSendTime;
    private long iterationNumber;
    private boolean produceEventBean;

    private ExprEvaluator[] evaluators;
    private EventBeanManufacturer manufacturer;

    @DataFlowOpParameter(all=true)
    public void setProperty(String name, Object value) {
        allProperties.put(name, value);
    }

    public DataFlowOpInitializeResult initialize(DataFlowOpInitializateContext context) throws Exception {
        initialDelayMSec = (long) (initialDelay * 1000);
        periodDelayMSec = (long) (interval * 1000);

        if (context.getOutputPorts().size() != 1) {
            throw new IllegalArgumentException("BeaconSource operator requires one output stream but produces " + context.getOutputPorts().size() + " streams");
        }

        // Check if a type is declared
        DataFlowOpOutputPort port = context.getOutputPorts().get(0);
        if (port.getOptionalDeclaredType() != null && port.getOptionalDeclaredType().getEventType() != null) {
            EventType outputEventType = port.getOptionalDeclaredType().getEventType();
            produceEventBean = port.getOptionalDeclaredType() != null && !port.getOptionalDeclaredType().isUnderlying();

            // compile properties to populate
            Set<String> props = allProperties.keySet();
            props.removeAll(PARAMETER_PROPERTIES);
            WriteablePropertyDescriptor[] writables = setupProperties(props.toArray(new String[props.size()]), outputEventType, context.getStatementContext());
            manufacturer = context.getServicesContext().getEventAdapterService().getManufacturer(outputEventType, writables, context.getServicesContext().getEngineImportService());

            int index = 0;
            evaluators = new ExprEvaluator[outputEventType.getPropertyDescriptors().length];
            for (WriteablePropertyDescriptor writeable : writables) {

                ExprNode exprNode = (ExprNode) allProperties.get(writeable.getPropertyName());
                if (exprNode == null) {
                    evaluators[index] = new ExprEvaluator() {
                        public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
                            return null;
                        }
                        public Class getType() {
                            return null;
                        }
                        public Map<String, Object> getEventType() throws ExprValidationException {
                            return null;
                        }
                    };
                }
                else {
                    ExprNode validated = ExprNodeUtility.validateSimpleGetSubtree(exprNode, context.getStatementContext(), null);
                    final ExprEvaluator exprEvaluator = validated.getExprEvaluator();
                    final TypeWidener widener = TypeWidenerFactory.getCheckPropertyAssignType(validated.toExpressionString(), exprEvaluator.getType(),
                                                writeable.getType(), writeable.getPropertyName());
                    if (widener != null) {
                        evaluators[index] = new ExprEvaluator() {
                            public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
                                Object value = exprEvaluator.evaluate(eventsPerStream, isNewData, context);
                                return widener.widen(value);
                            }
                            public Class getType() {
                                return null;
                            }
                            public Map<String, Object> getEventType() throws ExprValidationException {
                                return null;
                            }
                        };
                    }
                    else {
                        evaluators[index] = exprEvaluator;
                    }
                }
                index++;
            }

            return null;    // no changing types
        }

        // No type has been declared, we can create one
        String anonymousTypeName = context.getDataflowName() + "-beacon";
        Map<String, Object> types = new LinkedHashMap<String, Object>();
        Set<String> props = allProperties.keySet();
        props.removeAll(PARAMETER_PROPERTIES);

        int count = 0;
        evaluators = new ExprEvaluator[props.size()];
        for (String propertyName : props) {
            ExprNode exprNode = (ExprNode) allProperties.get(propertyName);
            ExprNode validated = ExprNodeUtility.validateSimpleGetSubtree(exprNode, context.getStatementContext(), null);
            final Object value = validated.getExprEvaluator().evaluate(null, true, context.getAgentInstanceContext());
            if (value == null) {
                types.put(propertyName, null);
            }
            else {
                types.put(propertyName, value.getClass());
            }
            evaluators[count] = new ExprEvaluator() {
                public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
                    return value;
                }
                public Class getType() {
                    return null;
                }
                public Map<String, Object> getEventType() throws ExprValidationException {
                    return null;
                }
            };
            count++;
        }
        
        EventType type = context.getServicesContext().getEventAdapterService().createAnonymousObjectArrayType(anonymousTypeName, types);
        return new DataFlowOpInitializeResult(new GraphTypeDesc[] {new GraphTypeDesc(false, true, type)});
    }

    public void next() {
        if (iterationNumber == 0 && initialDelayMSec > 0) {
            try {
                Thread.sleep(initialDelayMSec, 0);
            }
            catch (InterruptedException e) {
                graphContext.submitSignal(new EPDataFlowSignalFinalMarker() {});
            }
        }

        if (iterationNumber > 0 && periodDelayMSec > 0) {
            long nsecDelta = lastSendTime - System.nanoTime();
            long sleepTime = periodDelayMSec - nsecDelta / 1000000;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                }
                catch (InterruptedException e) {
                    graphContext.submitSignal(new EPDataFlowSignalFinalMarker() {});
                }
            }
        }

        if (iterations > 0 && iterationNumber >= iterations) {
            graphContext.submitSignal(new EPDataFlowSignalFinalMarker() {});
        }
        else {
            iterationNumber++;
            if (evaluators != null) {
                Object[] row = new Object[evaluators.length];
                for (int i = 0; i < row.length; i++) {
                    row[i] = evaluators[i].evaluate(null, true, null);
                }
                if (log.isDebugEnabled()) {
                    log.debug("BeaconSource submitting row " + Arrays.toString(row));
                }

                Object outputEvent = row;
                if (manufacturer != null) {
                    if (!produceEventBean) {
                        outputEvent = manufacturer.makeUnderlying(row);
                    }
                    else {
                        outputEvent = manufacturer.make(row);
                    }
                }
                graphContext.submit(outputEvent);
            }
            else {
                if (log.isDebugEnabled()) {
                    log.debug("BeaconSource submitting empty row");
                }
                graphContext.submit(new Object[0]);
            }

            if (interval > 0) {
                lastSendTime = System.nanoTime();
            }
        }
    }

    public void open(DataFlowOpOpenContext openContext) {
        // no action
    }

    public void close(DataFlowOpCloseContext openContext) {
        // no action
    }

    private static WriteablePropertyDescriptor[] setupProperties(String[] propertyNamesOffered, EventType outputEventType, StatementContext statementContext)
            throws ExprValidationException
    {
        Set<WriteablePropertyDescriptor> writeables = statementContext.getEventAdapterService().getWriteableProperties(outputEventType);

        List<WriteablePropertyDescriptor> writablesList = new ArrayList<WriteablePropertyDescriptor>();

        for (int i = 0; i < propertyNamesOffered.length; i++) {
            String propertyName = propertyNamesOffered[i];
            WriteablePropertyDescriptor writable = EventTypeUtility.findWritable(propertyName, writeables);
            if (writable == null) {
                throw new ExprValidationException("Failed to find writable property '" + propertyName + "' for event type '" + outputEventType.getName() + "'");
            }
            writablesList.add(writable);
        }

        return writablesList.toArray(new WriteablePropertyDescriptor[writablesList.size()]);
    }
}
