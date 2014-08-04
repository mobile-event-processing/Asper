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

package com.espertech.esper.epl.core;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationEngineDefaults;
import com.espertech.esper.core.service.StatementResultService;
import com.espertech.esper.core.service.StatementResultServiceImpl;
import com.espertech.esper.core.thread.ThreadingServiceImpl;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.SelectClauseElementCompiled;
import com.espertech.esper.epl.spec.SelectClauseElementWildcard;
import com.espertech.esper.epl.spec.SelectClauseExprCompiledSpec;
import com.espertech.esper.epl.spec.SelectClauseStreamCompiledSpec;
import com.espertech.esper.support.epl.SupportExprNodeFactory;
import com.espertech.esper.support.epl.SupportStreamTypeSvc3Stream;
import com.espertech.esper.support.event.SupportEventAdapterService;
import junit.framework.TestCase;

import java.util.*;

public class TestSelectExprProcessorFactory extends TestCase
{
    private List<SelectClauseStreamCompiledSpec> listOfStreamsSelected = new ArrayList<SelectClauseStreamCompiledSpec>();
    private StatementResultService statementResultService = new StatementResultServiceImpl("name", null, null, new ThreadingServiceImpl(new ConfigurationEngineDefaults.Threading()));
    private SelectExprEventTypeRegistry selectExprEventTypeRegistry = new SelectExprEventTypeRegistry(new HashSet<String>());

    public void testGetProcessorInvalid() throws Exception
    {
        List<SelectClauseElementCompiled> selectionList = new LinkedList<SelectClauseElementCompiled>();
        ExprNode identNode = SupportExprNodeFactory.makeIdentNode("doubleBoxed", "s0");
        ExprNode mathNode = SupportExprNodeFactory.makeMathNode();
        selectionList.add(new SelectClauseExprCompiledSpec(identNode, "result", "result"));
        selectionList.add(new SelectClauseExprCompiledSpec(mathNode, "result", "result"));

        try
        {
            SelectExprProcessorFactory.getProcessor(Collections.<Integer>emptyList(), selectionList, false, null, null,
                    new SupportStreamTypeSvc3Stream(), null, null, null, null, null, null, null, null, null, null, null, null, null, new Configuration(), null);
            fail();
        }
        catch (ExprValidationException ex)
        {
            // Expected
        }
    }

    public void testGetProcessorWildcard() throws Exception
    {
        List<SelectClauseElementCompiled> selectionList = new LinkedList<SelectClauseElementCompiled>();
        selectionList.add(new SelectClauseElementWildcard());
        SelectExprProcessor processor = SelectExprProcessorFactory.getProcessor(Collections.<Integer>emptyList(), selectionList, false, null, null,
                new SupportStreamTypeSvc3Stream(), SupportEventAdapterService.getService(), statementResultService, null, selectExprEventTypeRegistry, null, null, null, null, null, null, null, null, null, new Configuration(), null);
        assertTrue(processor instanceof SelectExprResultProcessor);
    }

    public void testGetProcessorValid() throws Exception
    {
        List<SelectClauseElementCompiled> selectionList = new LinkedList<SelectClauseElementCompiled>();
        ExprNode identNode = SupportExprNodeFactory.makeIdentNode("doubleBoxed", "s0");
        selectionList.add(new SelectClauseExprCompiledSpec(identNode, "result", null));
        SelectExprProcessor processor = SelectExprProcessorFactory.getProcessor(Collections.<Integer>emptyList(), selectionList, false, null, null,
                new SupportStreamTypeSvc3Stream(), SupportEventAdapterService.getService(), statementResultService, null, selectExprEventTypeRegistry, null, null, null, null, null, null, null, null, null, new Configuration(), null);
        assertTrue(processor != null);
    }

    public void testVerifyNameUniqueness() throws Exception
    {
        // try valid case
        List<SelectClauseElementCompiled> elements = new LinkedList<SelectClauseElementCompiled>();
        elements.add(new SelectClauseExprCompiledSpec(null, "xx", null));
        elements.add(new SelectClauseExprCompiledSpec(null, "yy", null));
        elements.add(new SelectClauseStreamCompiledSpec("win", null));
        elements.add(new SelectClauseStreamCompiledSpec("s2", "abc"));

        SelectExprProcessorFactory.verifyNameUniqueness(elements);

        // try invalid case
        elements.add(new SelectClauseExprCompiledSpec(null, "yy", null));
        try
        {
            SelectExprProcessorFactory.verifyNameUniqueness(elements);
            fail();
        }
        catch (ExprValidationException ex)
        {
            // expected
        }

        // try invalid case
        elements.clear();
        elements.add(new SelectClauseExprCompiledSpec(null, "abc", null));
        elements.add(new SelectClauseStreamCompiledSpec("s0", "abc"));
        try
        {
            SelectExprProcessorFactory.verifyNameUniqueness(elements);
            fail();
        }
        catch (ExprValidationException ex)
        {
            // expected
        }
    }
}
