/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core.start;

import com.espertech.esper.core.service.EPServicesContext;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.named.NamedWindowProcessor;
import com.espertech.esper.epl.named.NamedWindowProcessorInstance;
import com.espertech.esper.epl.spec.CreateIndexDesc;
import com.espertech.esper.epl.spec.StatementSpecCompiled;
import com.espertech.esper.epl.virtualdw.VirtualDWView;
import com.espertech.esper.view.ViewProcessingException;
import com.espertech.esper.view.Viewable;
import com.espertech.esper.view.ViewableDefaultImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Starts and provides the stop method for EPL statements.
 */
public class EPStatementStartMethodCreateIndex extends EPStatementStartMethodBase
{
    private static final Log log = LogFactory.getLog(EPStatementStartMethodCreateIndex.class);

    public EPStatementStartMethodCreateIndex(StatementSpecCompiled statementSpec) {
        super(statementSpec);
    }

    public EPStatementStartResult startInternal(EPServicesContext services, StatementContext statementContext, boolean isNewStatement, boolean isRecoveringStatement, boolean isRecoveringResilient) throws ExprValidationException, ViewProcessingException {
        final CreateIndexDesc spec = statementSpec.getCreateIndexDesc();
        final NamedWindowProcessor processor = services.getNamedWindowService().getProcessor(spec.getWindowName());
        if (processor == null) {
            throw new ExprValidationException("A named window by name '" + spec.getWindowName() + "' does not exist");
        }
        final NamedWindowProcessorInstance processorInstance = processor.getProcessorInstance(getDefaultAgentInstanceContext(statementContext));

        EPStatementStopMethod stopMethod;
        if (processor.isVirtualDataWindow()) {
            final VirtualDWView virtualDWView = processorInstance.getRootViewInstance().getVirtualDataWindow();
            virtualDWView.handleStartIndex(spec);
            stopMethod = new EPStatementStopMethod() {
                public void stop() {
                    virtualDWView.handleStopIndex(spec);
                }
            };
        }
        else {
            processorInstance.getRootViewInstance().addExplicitIndex(spec.isUnique(), spec.getWindowName(), spec.getIndexName(), spec.getColumns());
            stopMethod = new EPStatementStopMethod() {
                public void stop()
                {
                    processorInstance.getRootViewInstance().removeExplicitIndex(spec.getIndexName());
                }
            };
        }

        Viewable viewable = new ViewableDefaultImpl(processor.getNamedWindowType());
        return new EPStatementStartResult(viewable, stopMethod, null);
    }
}
