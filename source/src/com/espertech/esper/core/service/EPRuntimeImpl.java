/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.service;

import com.espertech.esper.client.*;
import com.espertech.esper.client.context.ContextPartitionSelector;
import com.espertech.esper.client.dataflow.EPDataFlowRuntime;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.client.time.CurrentTimeSpanEvent;
import com.espertech.esper.client.time.TimerControlEvent;
import com.espertech.esper.client.time.TimerEvent;
import com.espertech.esper.client.util.EventRenderer;
import com.espertech.esper.collection.ArrayBackedCollection;
import com.espertech.esper.collection.DualWorkQueue;
import com.espertech.esper.collection.ThreadWorkQueue;
import com.espertech.esper.core.context.util.EPStatementAgentInstanceHandle;
import com.espertech.esper.core.context.util.EPStatementAgentInstanceHandleComparator;
import com.espertech.esper.core.start.EPPreparedExecuteMethod;
import com.espertech.esper.core.thread.*;
import com.espertech.esper.epl.annotation.AnnotationUtil;
import com.espertech.esper.epl.declexpr.ExprDeclaredNode;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.ExprNodeSubselectDeclaredDotVisitor;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.metric.MetricReportingPath;
import com.espertech.esper.epl.script.AgentInstanceScriptContext;
import com.espertech.esper.epl.spec.SelectClauseStreamSelectorEnum;
import com.espertech.esper.epl.spec.StatementSpecCompiled;
import com.espertech.esper.epl.spec.StatementSpecRaw;
import com.espertech.esper.epl.spec.util.StatementSpecRawAnalyzer;
import com.espertech.esper.epl.variable.VariableReader;
import com.espertech.esper.event.util.EventRendererImpl;
import com.espertech.esper.filter.FilterHandle;
import com.espertech.esper.filter.FilterHandleCallback;
import com.espertech.esper.schedule.ScheduleHandle;
import com.espertech.esper.schedule.ScheduleHandleCallback;
import com.espertech.esper.schedule.SchedulingServiceSPI;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.timer.TimerCallback;
import com.espertech.esper.util.ExecutionPathDebugLog;
import com.espertech.esper.util.MetricUtil;
import com.espertech.esper.util.ThreadLogUtil;
import com.espertech.esper.util.UuidGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implements runtime interface. Also accepts timer callbacks for synchronizing time events with regular events
 * sent in.
 */
public class EPRuntimeImpl implements EPRuntimeSPI, EPRuntimeEventSender, TimerCallback, InternalEventRouteDest
{
    private EPServicesContext services;
    private boolean isLatchStatementInsertStream;
    private boolean isUsingExternalClocking;
    private boolean isSubselectPreeval;
    private boolean isPrioritized;
    private volatile UnmatchedListener unmatchedListener;
    private AtomicLong routedInternal;
    private AtomicLong routedExternal;
    private EventRenderer eventRenderer;
    private InternalEventRouter internalEventRouter;
    private ExprEvaluatorContext engineFilterAndDispatchTimeContext;
    private ThreadWorkQueue threadWorkQueue;
    private ThreadLocal<ArrayBackedCollection<FilterHandle>> matchesArrayThreadLocal;
    private ThreadLocal<ArrayBackedCollection<ScheduleHandle>> scheduleArrayThreadLocal;
    private ThreadLocal<Map<EPStatementAgentInstanceHandle, ArrayDeque<FilterHandleCallback>>> matchesPerStmtThreadLocal;
    private ThreadLocal<Map<EPStatementAgentInstanceHandle, Object>> schedulePerStmtThreadLocal;

    /**
     * Constructor.
     * @param services - references to services
     */
    public EPRuntimeImpl(final EPServicesContext services)
    {
        this.services = services;
        this.threadWorkQueue = new ThreadWorkQueue();
        isLatchStatementInsertStream = this.services.getEngineSettingsService().getEngineSettings().getThreading().isInsertIntoDispatchPreserveOrder();
        isUsingExternalClocking = !this.services.getEngineSettingsService().getEngineSettings().getThreading().isInternalTimerEnabled();
        isSubselectPreeval = services.getEngineSettingsService().getEngineSettings().getExpression().isSelfSubselectPreeval();
        isPrioritized = services.getEngineSettingsService().getEngineSettings().getExecution().isPrioritized();
        routedInternal = new AtomicLong();
        routedExternal = new AtomicLong();
        engineFilterAndDispatchTimeContext = new ExprEvaluatorContext()
        {
            private ExpressionResultCacheService expressionResultCacheService = services.getExpressionResultCacheSharable();

            public TimeProvider getTimeProvider() {
                return services.getSchedulingService();
            }

            public ExpressionResultCacheService getExpressionResultCacheService() {
                return expressionResultCacheService;
            }

            public int getAgentInstanceId() {
                return -1;
            }

            public EventBean getContextProperties() {
                return null;
            }

            public AgentInstanceScriptContext getAgentInstanceScriptContext() {
                return null;
            }

            public String getStatementName() {
                return null;
            }

            public String getEngineURI() {
                return null;
            }

            public String getStatementId() {
                return null;
            }

            public StatementAgentInstanceLock getAgentInstanceLock() {
                return null;
            }
        };

        initThreadLocals();

        services.getThreadingService().initThreading(services, this);
    }

    /**
     * Sets the route for events to use
     * @param internalEventRouter router
     */
    public void setInternalEventRouter(InternalEventRouter internalEventRouter)
    {
        this.internalEventRouter = internalEventRouter;
    }

    public long getRoutedInternal()
    {
        return routedInternal.get();
    }

    public long getRoutedExternal()
    {
        return routedExternal.get();
    }

    public void timerCallback()
    {
        long msec = services.getTimeSource().getTimeMillis();

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled() && (ExecutionPathDebugLog.isTimerDebugEnabled)))
        {
            log.debug(".timerCallback Evaluating scheduled callbacks, time is " + msec);
        }

        CurrentTimeEvent currentTimeEvent = new CurrentTimeEvent(msec);
        sendEvent(currentTimeEvent);
    }

    public void sendEvent(Object theEvent) throws EPException
    {
        if (theEvent == null)
        {
            log.fatal(".sendEvent Null object supplied");
            return;
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            if ((!(theEvent instanceof CurrentTimeEvent)) || (ExecutionPathDebugLog.isTimerDebugEnabled))
            {
                log.debug(".sendEvent Processing event " + theEvent);
            }
        }

        // Process event
        if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isInboundThreading()))
        {
            services.getThreadingService().submitInbound(new InboundUnitSendEvent(theEvent, this));
        }
        else
        {
            processEvent(theEvent);
        }
    }

    public void sendEvent(org.w3c.dom.Node document) throws EPException
    {
        if (document == null)
        {
            log.fatal(".sendEvent Null object supplied");
            return;
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".sendEvent Processing DOM node event " + document);
        }

        // Process event
        if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isInboundThreading()))
        {
            services.getThreadingService().submitInbound(new InboundUnitSendDOM(document, services, this));
        }
        else
        {
            // Get it wrapped up, process event
            EventBean eventBean = wrapEvent(document);
            processEvent(eventBean);
        }
    }

    public EventBean wrapEvent(Node node) {
        return services.getEventAdapterService().adapterForDOM(node);
    }

    public void route(org.w3c.dom.Node document) throws EPException
    {
        if (document == null)
        {
            log.fatal(".sendEvent Null object supplied");
            return;
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".sendEvent Processing DOM node event " + document);
        }

        // Get it wrapped up, process event
        EventBean eventBean = services.getEventAdapterService().adapterForDOM(document);
        threadWorkQueue.addBack(eventBean);
    }

    public void sendEvent(Map map, String mapEventTypeName) throws EPException
    {
        if (map == null)
        {
            throw new IllegalArgumentException("Invalid null event object");
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".sendMap Processing event " + map);
        }

        if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isInboundThreading()))
        {
            services.getThreadingService().submitInbound(new InboundUnitSendMap(map, mapEventTypeName, services, this));
        }
        else
        {
            // Process event
            EventBean eventBean = wrapEvent(map, mapEventTypeName);
            processWrappedEvent(eventBean);
        }
    }

    public void sendEvent(Object[] propertyValues, String objectArrayEventTypeName) throws EPException
    {
        if (propertyValues == null)
        {
            throw new IllegalArgumentException("Invalid null event object");
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".sendMap Processing event " + Arrays.toString(propertyValues));
        }

        if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isInboundThreading()))
        {
            services.getThreadingService().submitInbound(new InboundUnitSendObjectArray(propertyValues, objectArrayEventTypeName, services, this));
        }
        else
        {
            // Process event
            EventBean eventBean = wrapEvent(propertyValues, objectArrayEventTypeName);
            processWrappedEvent(eventBean);
        }
    }

    public EventBean wrapEvent(Map map, String eventTypeName) {
        return services.getEventAdapterService().adapterForMap(map, eventTypeName);
    }

    public EventBean wrapEvent(Object[] objectArray, String eventTypeName) {
        return services.getEventAdapterService().adapterForObjectArray(objectArray, eventTypeName);
    }

    public void route(Map map, String eventTypeName) throws EPException
    {
        if (map == null)
        {
            throw new IllegalArgumentException("Invalid null event object");
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".route Processing event " + map);
        }

        // Process event
        EventBean theEvent = services.getEventAdapterService().adapterForMap(map, eventTypeName);
        if (internalEventRouter.isHasPreprocessing())
        {
            theEvent = internalEventRouter.preprocess(theEvent,engineFilterAndDispatchTimeContext);
            if (theEvent == null)
            {
                return;
            }
        }
        threadWorkQueue.addBack(theEvent);
    }

    public void route(Object[] objectArray, String eventTypeName) throws EPException
    {
        if (objectArray == null)
        {
            throw new IllegalArgumentException("Invalid null event object");
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".route Processing event " + Arrays.toString(objectArray));
        }

        // Process event
        EventBean theEvent = services.getEventAdapterService().adapterForObjectArray(objectArray, eventTypeName);
        if (internalEventRouter.isHasPreprocessing())
        {
            theEvent = internalEventRouter.preprocess(theEvent,engineFilterAndDispatchTimeContext);
            if (theEvent == null)
            {
                return;
            }
        }
        threadWorkQueue.addBack(theEvent);
    }

    public long getNumEventsEvaluated()
    {
        return services.getFilterService().getNumEventsEvaluated();
    }

    public void resetStats() {
        services.getFilterService().resetStats();
        routedInternal.set(0);
        routedExternal.set(0);
    }

    public void routeEventBean(EventBean theEvent)
    {
        threadWorkQueue.addBack(theEvent);
    }

    public void route(Object theEvent)
    {
        routedExternal.incrementAndGet();

        if (internalEventRouter.isHasPreprocessing())
        {
            EventBean eventBean = services.getEventAdapterService().adapterForBean(theEvent);
            theEvent = internalEventRouter.preprocess(eventBean,engineFilterAndDispatchTimeContext);
            if (theEvent == null)
            {
                return;
            }
        }
                
        threadWorkQueue.addBack(theEvent);
    }

    // Internal route of events via insert-into, holds a statement lock
    public void route(EventBean theEvent, EPStatementHandle epStatementHandle, boolean addToFront)
    {
        routedInternal.incrementAndGet();

        if (isLatchStatementInsertStream)
        {
            if (addToFront) {
                Object latch = epStatementHandle.getInsertIntoFrontLatchFactory().newLatch(theEvent);
                threadWorkQueue.addFront(latch);
            }
            else {
                Object latch = epStatementHandle.getInsertIntoBackLatchFactory().newLatch(theEvent);
                threadWorkQueue.addBack(latch);
            }
        }
        else
        {
            if (addToFront) {
                threadWorkQueue.addFront(theEvent);
            }
            else {
                threadWorkQueue.addBack(theEvent);
            }
        }
    }

    /**
     * Process an unwrapped event.
     * @param theEvent to process.
     */
    public void processEvent(Object theEvent)
    {
        if (theEvent instanceof TimerEvent)
        {
            processTimeEvent((TimerEvent) theEvent);
            return;
        }

        EventBean eventBean;

        if (theEvent instanceof EventBean)
        {
            eventBean = (EventBean) theEvent;
        }
        else
        {
            eventBean = wrapEvent(theEvent);
        }

        processWrappedEvent(eventBean);
    }

    public EventBean wrapEvent(Object theEvent) {
        return services.getEventAdapterService().adapterForBean(theEvent);
    }

    public void processWrappedEvent(EventBean eventBean)
    {
        if (internalEventRouter.isHasPreprocessing())
        {
            eventBean = internalEventRouter.preprocess(eventBean, engineFilterAndDispatchTimeContext);
            if (eventBean == null)
            {
                return;
            }
        }

        // Acquire main processing lock which locks out statement management
        services.getEventProcessingRWLock().acquireReadLock();
        try
        {
            processMatches(eventBean);
        }
        catch (RuntimeException ex)
        {
            matchesArrayThreadLocal.get().clear();
            throw new EPException(ex);
        }
        finally
        {
            services.getEventProcessingRWLock().releaseReadLock();
        }

        // Dispatch results to listeners
        // Done outside of the read-lock to prevent lockups when listeners create statements
        dispatch();

        // Work off the event queue if any events accumulated in there via a route() or insert-into
        processThreadWorkQueue();
    }

    private void processTimeEvent(TimerEvent theEvent)
    {
        if (theEvent instanceof TimerControlEvent)
        {
            TimerControlEvent timerControlEvent = (TimerControlEvent) theEvent;
            if (timerControlEvent.getClockType() == TimerControlEvent.ClockType.CLOCK_INTERNAL)
            {
                // Start internal clock which supplies CurrentTimeEvent events every 100ms
                // This may be done without delay thus the write lock indeed must be reentrant.
                services.getTimerService().startInternalClock();
                isUsingExternalClocking = false;
            }
            else
            {
                // Stop internal clock, for unit testing and for external clocking
                services.getTimerService().stopInternalClock(true);
                isUsingExternalClocking = true;
            }

            return;
        }

        if (theEvent instanceof CurrentTimeEvent) {
            CurrentTimeEvent current = (CurrentTimeEvent) theEvent;
            long currentTime = current.getTimeInMillis();

            // Evaluation of all time events is protected from statement management
            if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()) && (ExecutionPathDebugLog.isTimerDebugEnabled))
            {
                log.debug(".processTimeEvent Setting time and evaluating schedules for time " + currentTime);
            }

            if (isUsingExternalClocking && (currentTime == services.getSchedulingService().getTime()))
            {
                if (log.isWarnEnabled())
                {
                    log.warn("Duplicate time event received for currentTime " + currentTime);
                }
            }
            services.getSchedulingService().setTime(currentTime);

            if (MetricReportingPath.isMetricsEnabled)
            {
                services.getMetricsReportingService().processTimeEvent(currentTime);
            }

            processSchedule();

            // Let listeners know of results
            dispatch();

            // Work off the event queue if any events accumulated in there via a route()
            processThreadWorkQueue();

            return;
        }

        // handle time span
        CurrentTimeSpanEvent span = (CurrentTimeSpanEvent) theEvent;
        long targetTime = span.getTargetTimeInMillis();
        long currentTime = services.getSchedulingService().getTime();
        Long optionalResolution = span.getOptionalResolution();

        if (isUsingExternalClocking && (targetTime < currentTime))
        {
            if (log.isWarnEnabled())
            {
                log.warn("Past or current time event received for currentTime " + targetTime);
            }
        }

        // Evaluation of all time events is protected from statement management
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()) && (ExecutionPathDebugLog.isTimerDebugEnabled))
        {
            log.debug(".processTimeEvent Setting time span and evaluating schedules for time " + targetTime + " optional resolution " + span.getOptionalResolution());
        }

        while(currentTime < targetTime) {

            if ((optionalResolution != null) && (optionalResolution > 0)) {
                currentTime += optionalResolution;
            }
            else {
                Long nearest = services.getSchedulingService().getNearestTimeHandle();
                if (nearest == null) {
                    currentTime = targetTime;
                }
                else {
                    currentTime = nearest;
                }
            }
            if (currentTime > targetTime) {
                currentTime = targetTime;
            }

            // Evaluation of all time events is protected from statement management
            if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()) && (ExecutionPathDebugLog.isTimerDebugEnabled))
            {
                log.debug(".processTimeEvent Setting time and evaluating schedules for time " + currentTime);
            }

            services.getSchedulingService().setTime(currentTime);

            if (MetricReportingPath.isMetricsEnabled)
            {
                services.getMetricsReportingService().processTimeEvent(currentTime);
            }

            processSchedule();

            // Let listeners know of results
            dispatch();

            // Work off the event queue if any events accumulated in there via a route()
            processThreadWorkQueue();
        }
    }

    private void processSchedule()
    {
        ArrayBackedCollection<ScheduleHandle> handles = scheduleArrayThreadLocal.get();

        // Evaluation of schedules is protected by an optional scheduling service lock and then the engine lock
        // We want to stay in this order for allowing the engine lock as a second-order lock to the
        // services own lock, if it has one.
        services.getEventProcessingRWLock().acquireReadLock();
        try
        {
            services.getSchedulingService().evaluate(handles);
        }
        finally
        {
            services.getEventProcessingRWLock().releaseReadLock();
        }

        services.getEventProcessingRWLock().acquireReadLock();
        try
        {
            processScheduleHandles(handles);
        }
        catch (RuntimeException ex)
        {
            handles.clear();
            throw ex;
        }
        finally
        {
            services.getEventProcessingRWLock().releaseReadLock();
        }
    }

    private void processScheduleHandles(ArrayBackedCollection<ScheduleHandle> handles)
    {
        if (ThreadLogUtil.ENABLED_TRACE)
        {
            ThreadLogUtil.trace("Found schedules for", handles.size());
        }

        if (handles.size() == 0)
        {
            return;
        }

        // handle 1 result separatly for performance reasons
        if (handles.size() == 1)
        {
            Object[] handleArray = handles.getArray();
            EPStatementHandleCallback handle = (EPStatementHandleCallback) handleArray[0];

            if ((MetricReportingPath.isMetricsEnabled) && (handle.getAgentInstanceHandle().getStatementHandle().getMetricsHandle().isEnabled()))
            {
                long cpuTimeBefore = MetricUtil.getCPUCurrentThread();
                long wallTimeBefore = MetricUtil.getWall();

                processStatementScheduleSingle(handle, services, engineFilterAndDispatchTimeContext);

                long wallTimeAfter = MetricUtil.getWall();
                long cpuTimeAfter = MetricUtil.getCPUCurrentThread();
                long deltaCPU = cpuTimeAfter - cpuTimeBefore;
                long deltaWall = wallTimeAfter - wallTimeBefore;
                services.getMetricsReportingService().accountTime(handle.getAgentInstanceHandle().getStatementHandle().getMetricsHandle(), deltaCPU, deltaWall, 1);
            }
            else
            {
                if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isTimerThreading()))
                {
                    services.getThreadingService().submitTimerWork(new TimerUnitSingle(services, this, handle, this.engineFilterAndDispatchTimeContext));
                }
                else
                {
                    processStatementScheduleSingle(handle, services, engineFilterAndDispatchTimeContext);
                }
            }

            handles.clear();
            return;
        }

        Object[] matchArray = handles.getArray();
        int entryCount = handles.size();

        // sort multiple matches for the event into statements
        Map<EPStatementAgentInstanceHandle, Object> stmtCallbacks = schedulePerStmtThreadLocal.get();
        stmtCallbacks.clear();
        for (int i = 0; i < entryCount; i++)    // need to use the size of the collection
        {
            EPStatementHandleCallback handleCallback = (EPStatementHandleCallback) matchArray[i];
            EPStatementAgentInstanceHandle handle = handleCallback.getAgentInstanceHandle();
            ScheduleHandleCallback callback = handleCallback.getScheduleCallback();

            Object entry = stmtCallbacks.get(handle);

            // This statement has not been encountered before
            if (entry == null)
            {
                stmtCallbacks.put(handle, callback);
                continue;
            }

            // This statement has been encountered once before
            if (entry instanceof ScheduleHandleCallback)
            {
                ScheduleHandleCallback existingCallback = (ScheduleHandleCallback) entry;
                ArrayDeque<ScheduleHandleCallback> entries = new ArrayDeque<ScheduleHandleCallback>();
                entries.add(existingCallback);
                entries.add(callback);
                stmtCallbacks.put(handle, entries);
                continue;
            }

            // This statement has been encountered more then once before
            ArrayDeque<ScheduleHandleCallback> entries = (ArrayDeque<ScheduleHandleCallback>) entry;
            entries.add(callback);
        }
        handles.clear();

        for (Map.Entry<EPStatementAgentInstanceHandle, Object> entry : stmtCallbacks.entrySet())
        {
            EPStatementAgentInstanceHandle handle = entry.getKey();
            Object callbackObject = entry.getValue();

            if ((MetricReportingPath.isMetricsEnabled) && (handle.getStatementHandle().getMetricsHandle().isEnabled()))
            {
                long cpuTimeBefore = MetricUtil.getCPUCurrentThread();
                long wallTimeBefore = MetricUtil.getWall();

                processStatementScheduleMultiple(handle, callbackObject, services, this.engineFilterAndDispatchTimeContext);

                long wallTimeAfter = MetricUtil.getWall();
                long cpuTimeAfter = MetricUtil.getCPUCurrentThread();
                long deltaCPU = cpuTimeAfter - cpuTimeBefore;
                long deltaWall = wallTimeAfter - wallTimeBefore;
                int numInput = (callbackObject instanceof Collection) ? ((Collection) callbackObject).size() : 1;
                services.getMetricsReportingService().accountTime(handle.getStatementHandle().getMetricsHandle(), deltaCPU, deltaWall, numInput);
            }
            else
            {
                if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isTimerThreading()))
                {
                    services.getThreadingService().submitTimerWork(new TimerUnitMultiple(services, this, handle, callbackObject, this.engineFilterAndDispatchTimeContext));
                }
                else
                {
                    processStatementScheduleMultiple(handle, callbackObject, services, this.engineFilterAndDispatchTimeContext);
                }
            }

            if ((isPrioritized) && (handle.isPreemptive()))
            {
                break;
            }            
        }
    }

    /**
     * Works off the thread's work queue.
     */
    public void processThreadWorkQueue()
    {
        DualWorkQueue queues = threadWorkQueue.getThreadQueue();

        if (queues.getFrontQueue().isEmpty()) {
            boolean haveDispatched = services.getNamedWindowService().dispatch(engineFilterAndDispatchTimeContext);
            if (haveDispatched)
            {
                // Dispatch results to listeners
                dispatch();

                if (!queues.getFrontQueue().isEmpty()) {
                    processThreadWorkQueueFront(queues);
                }
            }
        }
        else {
            processThreadWorkQueueFront(queues);
        }

        Object item;
        while ( (item = queues.getBackQueue().poll()) != null)
        {
            if (item instanceof InsertIntoLatchSpin)
            {
                processThreadWorkQueueLatchedSpin((InsertIntoLatchSpin) item);
            }
            else if (item instanceof InsertIntoLatchWait)
            {
                processThreadWorkQueueLatchedWait((InsertIntoLatchWait) item);
            }
            else
            {
                processThreadWorkQueueUnlatched(item);
            }

            boolean haveDispatched = services.getNamedWindowService().dispatch(engineFilterAndDispatchTimeContext);
            if (haveDispatched)
            {
                dispatch();
            }

            if (!queues.getFrontQueue().isEmpty()) {
                processThreadWorkQueue();
            }
        }
    }

    private void processThreadWorkQueueFront(DualWorkQueue queues) {
        Object item;
        while ( (item = queues.getFrontQueue().poll()) != null)
        {
            if (item instanceof InsertIntoLatchSpin)
            {
                processThreadWorkQueueLatchedSpin((InsertIntoLatchSpin) item);
            }
            else if (item instanceof InsertIntoLatchWait)
            {
                processThreadWorkQueueLatchedWait((InsertIntoLatchWait) item);
            }
            else
            {
                processThreadWorkQueueUnlatched(item);
            }

            boolean haveDispatched = services.getNamedWindowService().dispatch(engineFilterAndDispatchTimeContext);
            if (haveDispatched)
            {
                dispatch();
            }
        }
    }

    private void processThreadWorkQueueLatchedWait(InsertIntoLatchWait insertIntoLatch)
    {
        // wait for the latch to complete
        EventBean eventBean = insertIntoLatch.await();

        services.getEventProcessingRWLock().acquireReadLock();
        try
        {
            processMatches(eventBean);
        }
        catch (RuntimeException ex)
        {
            matchesArrayThreadLocal.get().clear();
            throw ex;
        }
        finally
        {
            insertIntoLatch.done();
            services.getEventProcessingRWLock().releaseReadLock();
        }

        dispatch();
    }

    private void processThreadWorkQueueLatchedSpin(InsertIntoLatchSpin insertIntoLatch)
    {
        // wait for the latch to complete
        EventBean eventBean = insertIntoLatch.await();

        services.getEventProcessingRWLock().acquireReadLock();
        try
        {
            processMatches(eventBean);
        }
        catch (RuntimeException ex)
        {
            matchesArrayThreadLocal.get().clear();
            throw ex;
        }
        finally
        {
            insertIntoLatch.done();
            services.getEventProcessingRWLock().releaseReadLock();
        }

        dispatch();
    }

    private void processThreadWorkQueueUnlatched(Object item)
    {
        EventBean eventBean;
        if (item instanceof EventBean)
        {
            eventBean = (EventBean) item;
        }
        else
        {
            eventBean = services.getEventAdapterService().adapterForBean(item);
        }

        services.getEventProcessingRWLock().acquireReadLock();
        try
        {
            processMatches(eventBean);
        }
        catch (RuntimeException ex)
        {
            matchesArrayThreadLocal.get().clear();
            throw ex;
        }
        finally
        {
            services.getEventProcessingRWLock().releaseReadLock();
        }

        dispatch();
    }

    private void processMatches(EventBean theEvent)
    {
        // get matching filters
        ArrayBackedCollection<FilterHandle> matches = matchesArrayThreadLocal.get();
        long version = services.getFilterService().evaluate(theEvent, matches);

        if (ThreadLogUtil.ENABLED_TRACE)
        {
            ThreadLogUtil.trace("Found matches for underlying ", matches.size(), theEvent.getUnderlying());
        }

        if (matches.size() == 0)
        {
            if (unmatchedListener != null)
            {
                services.getEventProcessingRWLock().releaseReadLock();  // Allow listener to create new statements
                try {
                    unmatchedListener.update(theEvent);
                }
                catch (Throwable t) {
                    log.error("Exception thrown by unmatched listener: " + t.getMessage(), t);
                }
                finally {
                    // acquire read lock for release by caller
                    services.getEventProcessingRWLock().acquireReadLock();
                }
            }
            return;
        }

        Map<EPStatementAgentInstanceHandle, ArrayDeque<FilterHandleCallback>> stmtCallbacks = matchesPerStmtThreadLocal.get();
        Object[] matchArray = matches.getArray();
        int entryCount = matches.size();

        for (int i = 0; i < entryCount; i++)
        {
            EPStatementHandleCallback handleCallback = (EPStatementHandleCallback) matchArray[i];
            EPStatementAgentInstanceHandle handle = handleCallback.getAgentInstanceHandle();

            // Self-joins require that the internal dispatch happens after all streams are evaluated.
            // Priority or preemptive settings also require special ordering.
            if (handle.isCanSelfJoin() || isPrioritized)
            {
                ArrayDeque<FilterHandleCallback> callbacks = stmtCallbacks.get(handle);
                if (callbacks == null)
                {
                    callbacks = new ArrayDeque<FilterHandleCallback>();
                    stmtCallbacks.put(handle, callbacks);
                }
                callbacks.add(handleCallback.getFilterCallback());
                continue;
            }

            if ((MetricReportingPath.isMetricsEnabled) && (handle.getStatementHandle().getMetricsHandle().isEnabled()))
            {
                long cpuTimeBefore = MetricUtil.getCPUCurrentThread();
                long wallTimeBefore = MetricUtil.getWall();

                processStatementFilterSingle(handle, handleCallback, theEvent, version);

                long wallTimeAfter = MetricUtil.getWall();
                long cpuTimeAfter = MetricUtil.getCPUCurrentThread();
                long deltaCPU = cpuTimeAfter - cpuTimeBefore;
                long deltaWall = wallTimeAfter - wallTimeBefore;
                services.getMetricsReportingService().accountTime(handle.getStatementHandle().getMetricsHandle(), deltaCPU, deltaWall, 1);
            }
            else
            {
                if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isRouteThreading()))
                {
                    services.getThreadingService().submitRoute(new RouteUnitSingle(this, handleCallback, theEvent, version));
                }
                else
                {
                    processStatementFilterSingle(handle, handleCallback, theEvent, version);
                }
            }
        }
        matches.clear();
        if (stmtCallbacks.isEmpty())
        {
            return;
        }

        for (Map.Entry<EPStatementAgentInstanceHandle, ArrayDeque<FilterHandleCallback>> entry : stmtCallbacks.entrySet())
        {
            EPStatementAgentInstanceHandle handle = entry.getKey();
            ArrayDeque<FilterHandleCallback> callbackList = entry.getValue();

            if ((MetricReportingPath.isMetricsEnabled) && (handle.getStatementHandle().getMetricsHandle().isEnabled()))
            {
                long cpuTimeBefore = MetricUtil.getCPUCurrentThread();
                long wallTimeBefore = MetricUtil.getWall();

                processStatementFilterMultiple(handle, callbackList, theEvent, version);

                long wallTimeAfter = MetricUtil.getWall();
                long cpuTimeAfter = MetricUtil.getCPUCurrentThread();
                long deltaCPU = cpuTimeAfter - cpuTimeBefore;
                long deltaWall = wallTimeAfter - wallTimeBefore;
                services.getMetricsReportingService().accountTime(handle.getStatementHandle().getMetricsHandle(), deltaCPU, deltaWall, callbackList.size());
            }
            else
            {
                if ((ThreadingOption.isThreadingEnabled) && (services.getThreadingService().isRouteThreading()))
                {
                    services.getThreadingService().submitRoute(new RouteUnitMultiple(this, callbackList, theEvent, handle, version));
                }
                else
                {
                    processStatementFilterMultiple(handle, callbackList, theEvent, version);
                }

                if ((isPrioritized) && (handle.isPreemptive()))
                {
                    break;
                }
            }
        }
        stmtCallbacks.clear();
    }

    /**
     * Processing multiple schedule matches for a statement.
     * @param handle statement handle
     * @param callbackObject object containing matches
     * @param services engine services
     * @param exprEvaluatorContext context for expression evaluatiom
     */
    public static void processStatementScheduleMultiple(EPStatementAgentInstanceHandle handle, Object callbackObject, EPServicesContext services, ExprEvaluatorContext exprEvaluatorContext)
    {
        handle.getStatementAgentInstanceLock().acquireWriteLock(services.getStatementLockFactory());
        try
        {
            if (!handle.isDestroyed()) {
                if (handle.isHasVariables())
                {
                    services.getVariableService().setLocalVersion();
                }

                if (callbackObject instanceof ArrayDeque)
                {
                    ArrayDeque<ScheduleHandleCallback> callbackList = (ArrayDeque<ScheduleHandleCallback>) callbackObject;
                    for (ScheduleHandleCallback callback : callbackList)
                    {
                        callback.scheduledTrigger(services.getExtensionServicesContext());
                    }
                }
                else
                {
                    ScheduleHandleCallback callback = (ScheduleHandleCallback) callbackObject;
                    callback.scheduledTrigger(services.getExtensionServicesContext());
                }

                // internal join processing, if applicable
                handle.internalDispatch(exprEvaluatorContext);
            }
        }
        catch (RuntimeException ex) {
            services.getExceptionHandlingService().handleException(ex, handle);
        }
        finally
        {
            handle.getStatementAgentInstanceLock().releaseWriteLock(services.getStatementLockFactory());
        }
    }

    /**
     * Processing single schedule matche for a statement.
     * @param handle statement handle
     * @param services engine services
     * @param exprEvaluatorContext context for expression evaluatiom
     */
    public static void processStatementScheduleSingle(EPStatementHandleCallback handle, EPServicesContext services,ExprEvaluatorContext exprEvaluatorContext)
    {
        StatementAgentInstanceLock statementLock = handle.getAgentInstanceHandle().getStatementAgentInstanceLock();
        statementLock.acquireWriteLock(services.getStatementLockFactory());
        try
        {
            if (!handle.getAgentInstanceHandle().isDestroyed()) {
                if (handle.getAgentInstanceHandle().isHasVariables()) {
                    services.getVariableService().setLocalVersion();
                }

                handle.getScheduleCallback().scheduledTrigger(services.getExtensionServicesContext());
                handle.getAgentInstanceHandle().internalDispatch(exprEvaluatorContext);
            }
        }
        catch (RuntimeException ex) {
            services.getExceptionHandlingService().handleException(ex, handle.getAgentInstanceHandle());
        }
        finally
        {
            handle.getAgentInstanceHandle().getStatementAgentInstanceLock().releaseWriteLock(services.getStatementLockFactory());
        }
    }

    /**
     * Processing multiple filter matches for a statement.
     * @param handle statement handle
     * @param callbackList object containing callbacks
     * @param theEvent to process
     * @param version filter version
     */
    public void processStatementFilterMultiple(EPStatementAgentInstanceHandle handle, ArrayDeque<FilterHandleCallback> callbackList, EventBean theEvent, long version)
    {
        handle.getStatementAgentInstanceLock().acquireWriteLock(services.getStatementLockFactory());
        try
        {
            if (handle.isHasVariables())
            {
                services.getVariableService().setLocalVersion();
            }
            if (!handle.isCurrentFilter(version)) {
                if (handle.getFilterFaultHandler() != null) {
                    handle.getFilterFaultHandler().handleFilterFault(theEvent, version);
                }

                callbackList.clear();
                ArrayDeque<FilterHandle> callbackListNew = getCallbackList(theEvent, handle.getStatementId());
                for (FilterHandle callback : callbackListNew)
                {
                    EPStatementHandleCallback handleCallbackFilter = (EPStatementHandleCallback) callback;
                    callbackList.add(handleCallbackFilter.getFilterCallback());
                }
            }

            if (isSubselectPreeval)
            {
                // sub-selects always go first
                for (FilterHandleCallback callback : callbackList)
                {
                    if (callback.isSubSelect())
                    {
                        callback.matchFound(theEvent, callbackList);
                    }
                }

                for (FilterHandleCallback callback : callbackList)
                {
                    if (!callback.isSubSelect())
                    {
                        callback.matchFound(theEvent, callbackList);
                    }
                }
            }
            else
            {
                // sub-selects always go last
                for (FilterHandleCallback callback : callbackList)
                {
                    if (!callback.isSubSelect())
                    {
                        callback.matchFound(theEvent, callbackList);
                    }
                }

                for (FilterHandleCallback callback : callbackList)
                {
                    if (callback.isSubSelect())
                    {
                        callback.matchFound(theEvent, callbackList);
                    }
                }
            }

            // internal join processing, if applicable
            handle.internalDispatch(this.engineFilterAndDispatchTimeContext);
        }
        catch (RuntimeException ex) {
            services.getExceptionHandlingService().handleException(ex, handle);
        }
        finally
        {
            handle.getStatementAgentInstanceLock().releaseWriteLock(services.getStatementLockFactory());
        }
    }

    private ArrayDeque<FilterHandle> getCallbackList(EventBean theEvent, String statementId) {
        ArrayDeque<FilterHandle> callbacks = new ArrayDeque<FilterHandle>();
        services.getFilterService().evaluate(theEvent, callbacks, statementId);
        return callbacks;
    }

    /**
     * Process a single match.
     * @param handle statement
     * @param handleCallback callback
     * @param theEvent event to indicate
     * @param version filter version
     */
    public void processStatementFilterSingle(EPStatementAgentInstanceHandle handle, EPStatementHandleCallback handleCallback, EventBean theEvent, long version)
    {
        handle.getStatementAgentInstanceLock().acquireWriteLock(services.getStatementLockFactory());
        try
        {
            if (handle.isHasVariables())
            {
                services.getVariableService().setLocalVersion();
            }
            if (!handle.isCurrentFilter(version)) {
                if (handle.getFilterFaultHandler() != null) {
                    handle.getFilterFaultHandler().handleFilterFault(theEvent, version);
                }

                ArrayDeque<FilterHandle> callbackList = getCallbackList(theEvent, handle.getStatementId());
                for (FilterHandle callback : callbackList)
                {
                    EPStatementHandleCallback handleCallbackFilter = (EPStatementHandleCallback) callback;
                    handleCallbackFilter.getFilterCallback().matchFound(theEvent, null);
                }
            }
            else {
                handleCallback.getFilterCallback().matchFound(theEvent, null);
            }

            // internal join processing, if applicable
            handle.internalDispatch(engineFilterAndDispatchTimeContext);
        }
        catch (RuntimeException ex) {
            services.getExceptionHandlingService().handleException(ex, handle);
        }
        finally
        {
            handleCallback.getAgentInstanceHandle().getStatementAgentInstanceLock().releaseWriteLock(services.getStatementLockFactory());
        }
    }

    /**
     * Dispatch events.
     */
    public void dispatch()
    {
        try
        {
            services.getDispatchService().dispatch();
        }
        catch (RuntimeException ex)
        {
            throw new EPException(ex);
        }
    }

    /**
     * Destroy for destroying an engine instance: sets references to null and clears thread-locals
     */
    public void destroy()
    {
        services = null;

        removeFromThreadLocals();
        matchesArrayThreadLocal = null;
        matchesPerStmtThreadLocal = null;
        scheduleArrayThreadLocal = null;
        schedulePerStmtThreadLocal = null;
    }

    public void initialize() {
        initThreadLocals();
        threadWorkQueue = new ThreadWorkQueue();
    }

    public void setUnmatchedListener(UnmatchedListener listener)
    {
        this.unmatchedListener = listener;
    }

    public void setVariableValue(String variableName, Object variableValue) throws EPException
    {
        VariableReader reader = services.getVariableService().getReader(variableName);
        if (reader == null) {
            throw new VariableNotFoundException("Variable by name '" + variableName + "' has not been declared");
        }
        if (reader.isConstant()) {
            throw new VariableConstantValueException("Variable by name '" + variableName + "' is declared as constant and may not be assigned a new value");
        }

        services.getVariableService().getReadWriteLock().writeLock().lock();
        try {
            services.getVariableService().checkAndWrite(reader.getVariableNumber(), variableValue);
            services.getVariableService().commit();
        }
        finally {
            services.getVariableService().getReadWriteLock().writeLock().unlock();
        }
    }

    public void setVariableValue(Map<String, Object> variableValues) throws EPException
    {
        // verify
        for (Map.Entry<String, Object> entry : variableValues.entrySet())
        {
            String variableName = entry.getKey();
            VariableReader reader = services.getVariableService().getReader(variableName);
            if (reader == null) {
                throw new VariableNotFoundException("Variable by name '" + variableName + "' has not been declared");
            }
            if (reader.isConstant()) {
                throw new VariableConstantValueException("Variable by name '" + variableName + "' is declared as constant and may not be assigned a new value");
            }
        }

        // set values
        services.getVariableService().getReadWriteLock().writeLock().lock();
        try {
            for (Map.Entry<String, Object> entry : variableValues.entrySet())
            {
                String variableName = entry.getKey();
                VariableReader reader = services.getVariableService().getReader(variableName);
                try
                {
                    services.getVariableService().checkAndWrite(reader.getVariableNumber(), entry.getValue());
                }
                catch (RuntimeException ex)
                {
                    services.getVariableService().rollback();
                    throw ex;
                }
            }
            services.getVariableService().commit();
        }
        finally {
            services.getVariableService().getReadWriteLock().writeLock().unlock();
        }
    }

    public Object getVariableValue(String variableName) throws EPException
    {
        services.getVariableService().setLocalVersion();
        VariableReader reader = services.getVariableService().getReader(variableName);
        if (reader == null) {
            throw new VariableNotFoundException("Variable by name '" + variableName + "' has not been declared");
        }
        Object value = reader.getValue();
        if (value == null || reader.getEventType() == null) {
            return value;
        }
        return ((EventBean) value).getUnderlying();
    }

    public Map<String, Object> getVariableValue(Set<String> variableNames) throws EPException
    {
        services.getVariableService().setLocalVersion();
        Map<String, Object> values = new HashMap<String, Object>();
        for (String variableName : variableNames)
        {
            VariableReader reader = services.getVariableService().getReader(variableName);
            if (reader == null)
            {
                throw new VariableNotFoundException("Variable by name '" + variableName + "' has not been declared");
            }

            Object value = reader.getValue();
            if (value != null && reader.getEventType() != null) {
                value = ((EventBean) value).getUnderlying();
            }
            values.put(variableName, value);
        }
        return values;
    }

    public Map<String, Object> getVariableValueAll() throws EPException
    {
        services.getVariableService().setLocalVersion();
        Map<String, VariableReader> variables = services.getVariableService().getVariables();
        Map<String, Object> values = new HashMap<String, Object>();
        for (Map.Entry<String, VariableReader> entry : variables.entrySet())
        {
            Object value = entry.getValue().getValue();
            values.put(entry.getValue().getVariableName(), value);
        }
        return values;
    }

    public Map<String, Class> getVariableTypeAll()
    {
        Map<String, VariableReader> variables = services.getVariableService().getVariables();
        Map<String, Class> values = new HashMap<String, Class>();
        for (Map.Entry<String, VariableReader> entry : variables.entrySet())
        {
            Class type = entry.getValue().getType();
            values.put(entry.getValue().getVariableName(), type);
        }
        return values;
    }

    public Class getVariableType(String variableName)
    {
        VariableReader reader = services.getVariableService().getReader(variableName);
        if (reader == null)
        {
            return null;
        }
        return reader.getType();
    }

    public EPOnDemandQueryResult executeQuery(String epl, ContextPartitionSelector[] contextPartitionSelectors) {
        if (contextPartitionSelectors == null) {
            throw new IllegalArgumentException("No context partition selectors provided");
        }
        return executeQueryInternal(epl, contextPartitionSelectors);
    }

    public EPOnDemandQueryResult executeQuery(String epl) {
        return executeQueryInternal(epl, null);
    }

    private EPOnDemandQueryResult executeQueryInternal(String epl, ContextPartitionSelector[] contextPartitionSelectors)
    {
        try
        {
            EPPreparedExecuteMethod executeMethod = getExecuteMethod(epl);
            EPPreparedQueryResult result = executeMethod.execute(contextPartitionSelectors);
            return new EPQueryResultImpl(result);
        }
        catch (EPStatementException ex)
        {
            throw ex;
        }
        catch (Throwable t)
        {
            String message = "Error executing statement: " + t.getMessage();
            log.info(message, t);
            throw new EPStatementException(message, epl);
        }
    }

    public EPOnDemandPreparedQuery prepareQuery(String epl)
    {
        try
        {
            EPPreparedExecuteMethod startMethod = getExecuteMethod(epl);
            return new EPPreparedQueryImpl(startMethod, epl);
        }
        catch (EPStatementException ex)
        {
            throw ex;
        }
        catch (Throwable t)
        {
            String message = "Error executing statement: " + t.getMessage();
            log.debug(message, t);
            throw new EPStatementException(message, epl);
        }
    }

    private EPPreparedExecuteMethod getExecuteMethod(String epl)
    {
        String stmtName = UuidGenerator.generate();
        String stmtId = UuidGenerator.generate();

        try
        {
            StatementSpecRaw spec = EPAdministratorHelper.compileEPL(epl, epl, true, stmtName, services, SelectClauseStreamSelectorEnum.ISTREAM_ONLY);
            Annotation[] annotations = AnnotationUtil.compileAnnotations(spec.getAnnotations(), services.getEngineImportService(), epl);
            StatementContext statementContext =  services.getStatementContextFactory().makeContext(stmtId, stmtName, epl, services, null, true, annotations, null, true, spec);

            // walk subselects, declared expressions, dot-expressions
            ExprNodeSubselectDeclaredDotVisitor visitor;
            try {
                visitor = StatementSpecRawAnalyzer.walkSubselectAndDeclaredDotExpr(spec);
            }
            catch (ExprValidationException ex) {
                throw new EPStatementException(ex.getMessage(), epl);
            }

            StatementSpecCompiled compiledSpec = StatementLifecycleSvcImpl.compile(spec, epl, statementContext, true, annotations, visitor.getSubselects(), Collections.<ExprDeclaredNode>emptyList(), services);
            return new EPPreparedExecuteMethod(compiledSpec, services, statementContext);
        }
        catch (EPStatementException ex)
        {
            throw ex;
        }
        catch (Throwable t)
        {
            String message = "Error executing statement: " + t.getMessage();
            log.debug(message, t);
            throw new EPStatementException(message, t, epl);
        }
    }

    public EventSender getEventSender(String eventTypeName)
    {
        return services.getEventAdapterService().getStaticTypeEventSender(this, eventTypeName, services.getThreadingService());
    }

    public EventSender getEventSender(URI uri[]) throws EventTypeException
    {
        return services.getEventAdapterService().getDynamicTypeEventSender(this, uri, services.getThreadingService());
    }

    public EventRenderer getEventRenderer()
    {
        if (eventRenderer == null)
        {
            eventRenderer = new EventRendererImpl();
        }
        return eventRenderer;
    }

    public long getCurrentTime()
    {
        return services.getSchedulingService().getTime();
    }

    public Long getNextScheduledTime() {
        return services.getSchedulingService().getNearestTimeHandle();
    }

    public Map<String, Long> getStatementNearestSchedules() {
        return getStatementNearestSchedulesInternal(services.getSchedulingService(), services.getStatementLifecycleSvc());
    }

    protected static Map<String, Long> getStatementNearestSchedulesInternal(SchedulingServiceSPI schedulingService, StatementLifecycleSvc statementLifecycleSvc) {
        Map<String, Long> schedulePerStatementId = schedulingService.getStatementSchedules();
        Map<String, Long> result = new HashMap<String, Long>();
        for (Map.Entry<String, Long> schedule : schedulePerStatementId.entrySet()) {
            String stmtName = statementLifecycleSvc.getStatementNameById(schedule.getKey());
            if (stmtName != null) {
                result.put(stmtName, schedule.getValue());
            }
        }
        return result;
    }

    public String getEngineURI() {
        return services.getEngineURI();
    }

    public EPDataFlowRuntime getDataFlowRuntime() {
        return services.getDataFlowService();
    }

    private void removeFromThreadLocals() {
        if (matchesArrayThreadLocal != null) {
            matchesArrayThreadLocal.remove();
        }
        if (matchesPerStmtThreadLocal != null) {
            matchesPerStmtThreadLocal.remove();
        }
        if (scheduleArrayThreadLocal != null) {
            scheduleArrayThreadLocal.remove();
        }
        if (schedulePerStmtThreadLocal != null) {
            schedulePerStmtThreadLocal.remove();
        }
    }

    private void initThreadLocals() {
        removeFromThreadLocals();

        matchesArrayThreadLocal = new ThreadLocal<ArrayBackedCollection<FilterHandle>>()
        {
            protected synchronized ArrayBackedCollection<FilterHandle> initialValue()
            {
                return new ArrayBackedCollection<FilterHandle>(100);
            }
        };

        scheduleArrayThreadLocal = new ThreadLocal<ArrayBackedCollection<ScheduleHandle>>()
        {
            protected synchronized ArrayBackedCollection<ScheduleHandle> initialValue()
            {
                return new ArrayBackedCollection<ScheduleHandle>(100);
            }
        };

        matchesPerStmtThreadLocal =
                new ThreadLocal<Map<EPStatementAgentInstanceHandle, ArrayDeque<FilterHandleCallback>>>()
                {
                    protected synchronized Map<EPStatementAgentInstanceHandle, ArrayDeque<FilterHandleCallback>> initialValue()
                    {
                        if (isPrioritized)
                        {
                            return new TreeMap<EPStatementAgentInstanceHandle, ArrayDeque<FilterHandleCallback>>(EPStatementAgentInstanceHandleComparator.INSTANCE);
                        }
                        else
                        {
                            return new HashMap<EPStatementAgentInstanceHandle, ArrayDeque<FilterHandleCallback>>(10000);
                        }
                    }
                };

        schedulePerStmtThreadLocal = new ThreadLocal<Map<EPStatementAgentInstanceHandle, Object>>()
        {
            protected synchronized Map<EPStatementAgentInstanceHandle, Object> initialValue()
            {
                if (isPrioritized)
                {
                    return new TreeMap<EPStatementAgentInstanceHandle, Object>(EPStatementAgentInstanceHandleComparator.INSTANCE);
                }
                else
                {
                    return new HashMap<EPStatementAgentInstanceHandle, Object>(10000);
                }
            }
        };
    }

    private static final Log log = LogFactory.getLog(EPRuntimeImpl.class);
}
