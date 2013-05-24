/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.start;

import com.espertech.esper.client.EventType;
import com.espertech.esper.client.annotation.HookType;
import com.espertech.esper.client.hook.SQLColumnTypeConversion;
import com.espertech.esper.client.hook.SQLOutputRowConversion;
import com.espertech.esper.core.context.activator.*;
import com.espertech.esper.core.context.factory.StatementAgentInstanceFactorySelect;
import com.espertech.esper.core.context.subselect.SubSelectActivationCollection;
import com.espertech.esper.core.context.subselect.SubSelectStrategyCollection;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.core.context.util.ContextPropertyRegistry;
import com.espertech.esper.core.context.util.EPStatementAgentInstanceHandle;
import com.espertech.esper.core.service.EPServicesContext;
import com.espertech.esper.core.service.ExprEvaluatorContextStatement;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.core.service.StreamJoinAnalysisResult;
import com.espertech.esper.epl.core.*;
import com.espertech.esper.epl.db.DatabasePollingViewableFactory;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.join.base.HistoricalViewableDesc;
import com.espertech.esper.epl.join.base.JoinSetComposerPrototype;
import com.espertech.esper.epl.join.base.JoinSetComposerPrototypeFactory;
import com.espertech.esper.epl.named.NamedWindowProcessor;
import com.espertech.esper.epl.named.NamedWindowProcessorInstance;
import com.espertech.esper.epl.named.NamedWindowService;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.epl.view.OutputProcessViewFactory;
import com.espertech.esper.epl.view.OutputProcessViewFactoryFactory;
import com.espertech.esper.filter.FilterSpecCompiled;
import com.espertech.esper.pattern.EvalRootFactoryNode;
import com.espertech.esper.pattern.PatternContext;
import com.espertech.esper.rowregex.EventRowRegexNFAViewFactory;
import com.espertech.esper.util.CollectionUtil;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.util.StopCallback;
import com.espertech.esper.view.HistoricalEventViewable;
import com.espertech.esper.view.ViewFactory;
import com.espertech.esper.view.ViewFactoryChain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Starts and provides the stop method for EPL statements.
 */
public class EPStatementStartMethodSelectDesc
{
    private static final Log log = LogFactory.getLog(EPStatementStartMethodSelectDesc.class);

    private final StatementAgentInstanceFactorySelect statementAgentInstanceFactorySelect;
    private final SubSelectStrategyCollection subSelectStrategyCollection;
    private final ViewResourceDelegateUnverified viewResourceDelegateUnverified;
    private final ResultSetProcessorFactoryDesc resultSetProcessorPrototypeDesc;
    private final EPStatementStopMethod stopMethod;

    public EPStatementStartMethodSelectDesc(StatementAgentInstanceFactorySelect statementAgentInstanceFactorySelect, SubSelectStrategyCollection subSelectStrategyCollection, ViewResourceDelegateUnverified viewResourceDelegateUnverified, ResultSetProcessorFactoryDesc resultSetProcessorPrototypeDesc, EPStatementStopMethod stopMethod) {
        this.statementAgentInstanceFactorySelect = statementAgentInstanceFactorySelect;
        this.subSelectStrategyCollection = subSelectStrategyCollection;
        this.viewResourceDelegateUnverified = viewResourceDelegateUnverified;
        this.resultSetProcessorPrototypeDesc = resultSetProcessorPrototypeDesc;
        this.stopMethod = stopMethod;
    }

    public StatementAgentInstanceFactorySelect getStatementAgentInstanceFactorySelect() {
        return statementAgentInstanceFactorySelect;
    }

    public SubSelectStrategyCollection getSubSelectStrategyCollection() {
        return subSelectStrategyCollection;
    }

    public ViewResourceDelegateUnverified getViewResourceDelegateUnverified() {
        return viewResourceDelegateUnverified;
    }

    public ResultSetProcessorFactoryDesc getResultSetProcessorPrototypeDesc() {
        return resultSetProcessorPrototypeDesc;
    }

    public EPStatementStopMethod getStopMethod() {
        return stopMethod;
    }
}
