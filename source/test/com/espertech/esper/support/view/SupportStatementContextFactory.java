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

package com.espertech.esper.support.view;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationEngineDefaults;
import com.espertech.esper.client.hook.ConditionHandler;
import com.espertech.esper.client.hook.ExceptionHandler;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.core.service.*;
import com.espertech.esper.core.thread.ThreadingServiceImpl;
import com.espertech.esper.epl.agg.service.AggregationServiceFactoryServiceImpl;
import com.espertech.esper.epl.core.EngineImportServiceImpl;
import com.espertech.esper.epl.core.EngineSettingsService;
import com.espertech.esper.epl.core.MethodResolutionServiceImpl;
import com.espertech.esper.epl.expression.ExprEvaluatorContext;
import com.espertech.esper.epl.named.NamedWindowServiceImpl;
import com.espertech.esper.epl.spec.PluggableObjectCollection;
import com.espertech.esper.epl.spec.PluggableObjectRegistryImpl;
import com.espertech.esper.epl.variable.VariableServiceImpl;
import com.espertech.esper.event.vaevent.ValueAddEventServiceImpl;
import com.espertech.esper.pattern.PatternObjectResolutionServiceImpl;
import com.espertech.esper.schedule.ScheduleBucket;
import com.espertech.esper.schedule.SchedulingService;
import com.espertech.esper.support.event.SupportEventAdapterService;
import com.espertech.esper.support.schedule.SupportSchedulingServiceImpl;
import com.espertech.esper.util.ManagedReadWriteLock;
import com.espertech.esper.view.ViewEnumHelper;
import com.espertech.esper.view.ViewFactoryContext;
import com.espertech.esper.view.ViewResolutionServiceImpl;

import java.net.URI;
import java.util.Collections;

public class SupportStatementContextFactory
{
    public static ExprEvaluatorContext makeEvaluatorContext() {
        return new ExprEvaluatorContextStatement(null);
    }

    public static AgentInstanceContext makeAgentInstanceContext(SchedulingService stub) {
        return new AgentInstanceContext(makeContext(stub), null, -1, null, null, null);
    }

    public static AgentInstanceContext makeAgentInstanceContext() {
        return new AgentInstanceContext(makeContext(), null, -1, null, null, null);
    }

    public static AgentInstanceViewFactoryChainContext makeAgentInstanceViewFactoryContext(SchedulingService stub) {
        AgentInstanceContext agentInstanceContext = makeAgentInstanceContext(stub);
        return new AgentInstanceViewFactoryChainContext(agentInstanceContext, false, null, null);
    }

    public static AgentInstanceViewFactoryChainContext makeAgentInstanceViewFactoryContext() {
        AgentInstanceContext agentInstanceContext = makeAgentInstanceContext();
        return new AgentInstanceViewFactoryChainContext(agentInstanceContext, false, null, null);
    }

    public static StatementContext makeContext()
    {
        SupportSchedulingServiceImpl sched = new SupportSchedulingServiceImpl();
        return makeContext(sched);
    }

    public static ViewFactoryContext makeViewContext()
    {
        StatementContext stmtContext = makeContext();
        return new ViewFactoryContext(stmtContext, 1, 1, "somenamespacetest", "somenametest");
    }

    public static StatementContext makeContext(SchedulingService stub)
    {
        VariableServiceImpl variableService = new VariableServiceImpl(1000, null, SupportEventAdapterService.getService(), null);
        Configuration config = new Configuration();
        config.getEngineDefaults().getViewResources().setAllowMultipleExpiryPolicies(true);

        StatementContextEngineServices stmtEngineServices = new StatementContextEngineServices("engURI",
                SupportEventAdapterService.getService(),
                new NamedWindowServiceImpl(null, variableService, false, new ManagedReadWriteLock("dummyeplock", true), new ExceptionHandlingService("engURI", Collections.<ExceptionHandler>emptyList(), Collections.<ConditionHandler>emptyList()), false, null),
                null,
                new EngineSettingsService(new Configuration().getEngineDefaults(), new URI[0]),
                new ValueAddEventServiceImpl(),
                config,
                null,
                null,
                null,
                null);

        return new StatementContext(stmtEngineServices,
                "stmtId",
                null,
                "stmtName",
                "exprHere",
                stub,
                new ScheduleBucket(1),
                null,
                new ViewResolutionServiceImpl(new PluggableObjectRegistryImpl(new PluggableObjectCollection[] {ViewEnumHelper.getBuiltinViews()}), null, null),
                new PatternObjectResolutionServiceImpl(null),
                null,
                null,
                new MethodResolutionServiceImpl(new EngineImportServiceImpl(true, true, true), null),
                null,
                null,
                new StatementResultServiceImpl("name", null, null, new ThreadingServiceImpl(new ConfigurationEngineDefaults.Threading())), // statement result svc
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                AggregationServiceFactoryServiceImpl.DEFAULT_FACTORY);
    }
}
