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

package com.espertech.esper.regression.client;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.dataflow.EPDataFlowInstance;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.bean.SupportBean_ST0;
import com.espertech.esper.support.bean.SupportBean_ST1;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.util.AuditPath;
import com.espertech.esper.util.EventRepresentationEnum;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.Collections;

public class TestAudit extends TestCase {

    private static final Log log = LogFactory.getLog(TestAudit.class);
    private static final Log auditLog = LogFactory.getLog(AuditPath.AUDIT_LOG);

    private EPServiceProvider epService;
    private SupportUpdateListener listener;

    public void setUp()
    {
        listener = new SupportUpdateListener();

        Configuration configuration = SupportConfigFactory.getConfiguration();
        configuration.addEventType("SupportBean", SupportBean.class);
        configuration.addEventType("SupportBean_ST0", SupportBean_ST0.class);
        configuration.addEventType("SupportBean_ST1", SupportBean_ST1.class);
        configuration.getEngineDefaults().getLogging().setAuditPattern("[%u] [%s] [%c] %m");
        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        epService.initialize();
    }

    public void testDocSample() {
        epService.getEPAdministrator().createEPL("create schema OrderEvent(price double)");

        String epl = "@Name('All-Order-Events') @Audit('stream,property') select price from OrderEvent";
        epService.getEPAdministrator().createEPL(epl).addListener(listener);
        
        if (EventRepresentationEnum.getEngineDefault(epService).isObjectArrayEvent()) {
            epService.getEPRuntime().sendEvent(new Object[] {100d}, "OrderEvent");
        }
        else {
            epService.getEPRuntime().sendEvent(Collections.singletonMap("price", 100d), "OrderEvent");
        }
    }

    public void testAudit() throws Exception {

        // stream
        auditLog.info("*** Stream: ");
        EPStatement stmtInput = epService.getEPAdministrator().createEPL("@Name('ABC') @Audit('stream') select * from SupportBean(theString = 'E1')");
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        stmtInput.destroy();

        auditLog.info("*** Named Window And Insert-Into: ");
        EPStatement stmtNW = epService.getEPAdministrator().createEPL("@Name('create') @Audit create window WinOne.win:keepall() as SupportBean");
        EPStatement stmtInsert = epService.getEPAdministrator().createEPL("@Name('insert') @Audit insert into WinOne select * from SupportBean");
        EPStatement stmtConsume = epService.getEPAdministrator().createEPL("@Name('select') @Audit select * from WinOne");
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        stmtNW.destroy();
        stmtInsert.destroy();
        stmtConsume.destroy();

        auditLog.info("*** Insert-Into: ");
        EPStatement stmtInsertInto = epService.getEPAdministrator().createEPL("@Name('insert') @Audit insert into ABC select * from SupportBean");
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        stmtInsertInto.destroy();

        auditLog.info("*** Schedule: ");
        epService.getEPRuntime().sendEvent(new CurrentTimeEvent(0));
        EPStatement stmtSchedule = epService.getEPAdministrator().createEPL("@Name('ABC') @Audit('schedule') select irstream * from SupportBean.win:time(1 sec)");
        stmtSchedule.addListener(listener);
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        listener.reset();
        log.info("Sending time");
        epService.getEPRuntime().sendEvent(new CurrentTimeEvent(2000));
        assertTrue(listener.isInvoked());
        listener.reset();
        stmtSchedule.destroy();

        // exprdef-instances
        auditLog.info("*** Expression-Def: ");
        EPStatement stmtExprDef = epService.getEPAdministrator().createEPL("@Name('ABC') @Audit('exprdef') " +
                "expression DEF { 1 } " +
                "expression INN {  x => x.theString }" +
                "expression OUT { x => INN(x) } " +
                "select DEF(), OUT(sb) from SupportBean sb");
        stmtExprDef.addListener(listener);
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        assertEquals(1, listener.assertOneGetNewAndReset().get("DEF()"));
        stmtExprDef.destroy();

        // pattern-instances
        auditLog.info("*** Pattern-Lifecycle: ");
        EPStatement stmtPatternLife = epService.getEPAdministrator().createEPL("@Name('ABC') @Audit('pattern-instances') select a.intPrimitive as val0 from pattern [every a=SupportBean -> (b=SupportBean_ST0 and not SupportBean_ST1)]");
        stmtPatternLife.addListener(listener);
        log.info("Sending E1");
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        log.info("Sending E2");
        epService.getEPRuntime().sendEvent(new SupportBean("E2", 2));
        log.info("Sending E3");
        epService.getEPRuntime().sendEvent(new SupportBean_ST1("E3", 3));
        stmtPatternLife.destroy();

        // pattern
        auditLog.info("*** Pattern: ");
        EPStatement stmtPattern = epService.getEPAdministrator().createEPL("@Name('ABC') @Audit('pattern') select a.intPrimitive as val0 from pattern [a=SupportBean -> b=SupportBean_ST0]");
        stmtPattern.addListener(listener);
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 1));
        epService.getEPRuntime().sendEvent(new SupportBean_ST0("E2", 2));
        assertEquals(1, listener.assertOneGetNewAndReset().get("val0"));
        stmtPattern.destroy();

        // view
        auditLog.info("*** View: ");
        EPStatement stmtView = epService.getEPAdministrator().createEPL("@Name('ABC') @Audit('view') select intPrimitive from SupportBean.std:lastevent()");
        stmtView.addListener(listener);
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 50));
        assertEquals(50, listener.assertOneGetNewAndReset().get("intPrimitive"));
        stmtView.destroy();

        EPStatement stmtGroupedView = epService.getEPAdministrator().createEPL("@Audit Select * From SupportBean.std:groupwin(theString).win:length(2)");
        stmtGroupedView.addListener(listener);
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 50));
        listener.reset();
        stmtGroupedView.destroy();

        // expression
        auditLog.info("*** Expression: ");
        EPStatement stmtExpr = epService.getEPAdministrator().createEPL("@Name('ABC') @Audit('expression') select intPrimitive*100 as val0, sum(intPrimitive) as val1 from SupportBean");
        stmtExpr.addListener(listener);
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 50));
        assertEquals(5000, listener.assertOneGetNew().get("val0"));
        assertEquals(50, listener.assertOneGetNewAndReset().get("val1"));
        stmtExpr.destroy();

        // expression-detail
        auditLog.info("*** Expression-Nested: ");
        EPStatement stmtExprNested = epService.getEPAdministrator().createEPL("@Name('ABC') @Audit('expression-nested') select ('A'||theString)||'X' as val0 from SupportBean");
        stmtExprNested.addListener(listener);
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 50));
        assertEquals("AE1X", listener.assertOneGetNewAndReset().get("val0"));
        stmtExprNested.destroy();

        // property
        auditLog.info("*** Property: ");
        EPStatement stmtProp = epService.getEPAdministrator().createEPL("@Name('ABC') @Audit('property') select intPrimitive from SupportBean");
        stmtProp.addListener(listener);
        epService.getEPRuntime().sendEvent(new SupportBean("E1", 50));
        assertEquals(50, listener.assertOneGetNewAndReset().get("intPrimitive"));
        stmtProp.destroy();
        
        // with aggregation
        epService.getEPAdministrator().createEPL("@Audit @Name ('create') create window MyWindow.win:keepall() as SupportBean");
        String epl = "@Audit @Name('S0') on SupportBean as sel select count(*) from MyWindow as win having count(*)=3 order by win.intPrimitive";
        epService.getEPAdministrator().createEPL(epl);

        // data flow
        epService.getEPAdministrator().createEPL("@Audit @Name('df') create dataflow MyFlow " +
                "EventBusSource -> a<SupportBean> {filter:theString like 'I%'} " +
                "Filter(a) -> b {filter: true}" +
                "LogSink(b) {log:false}");
        EPDataFlowInstance df = epService.getEPRuntime().getDataFlowRuntime().instantiate("MyFlow");
        df.start();
        epService.getEPRuntime().sendEvent(new SupportBean("I1", 1));
        df.cancel();
    }
}
