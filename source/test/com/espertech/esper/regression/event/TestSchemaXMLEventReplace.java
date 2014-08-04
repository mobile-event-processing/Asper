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

package com.espertech.esper.regression.event;

import com.espertech.esper.client.*;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.support.client.SupportConfigFactory;
import com.espertech.esper.support.event.EventTypeAssertionUtil;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathConstants;

public class TestSchemaXMLEventReplace extends TestCase
{
    public static final String CLASSLOADER_SCHEMA_URI = "regression/simpleSchema.xsd";
    public static final String CLASSLOADER_SCHEMA_VERSION2_URI = "regression/simpleSchema_version2.xsd";

    private EPServiceProvider epService;

    public void testSchemaReplace() throws Exception
    {
        ConfigurationEventTypeXMLDOM eventTypeMeta = new ConfigurationEventTypeXMLDOM();
        eventTypeMeta.setRootElementName("simpleEvent");
        String schemaUri = TestSchemaXMLEventReplace.class.getClassLoader().getResource(CLASSLOADER_SCHEMA_URI).toString();
        eventTypeMeta.setSchemaResource(schemaUri);
        eventTypeMeta.addNamespacePrefix("ss", "samples:schemas:simpleSchema");
        eventTypeMeta.addXPathProperty("customProp", "count(/ss:simpleEvent/ss:nested3/ss:nested4)", XPathConstants.NUMBER);

        Configuration configuration = SupportConfigFactory.getConfiguration();
        configuration.addEventType("TestXMLSchemaType", eventTypeMeta);

        epService = EPServiceProviderManager.getProvider("TestSchemaXML", configuration);
        epService.initialize();

        String stmtSelectWild = "select * from TestXMLSchemaType";
        EPStatement wildStmt = epService.getEPAdministrator().createEPL(stmtSelectWild);
        EventType type = wildStmt.getEventType();
        EventTypeAssertionUtil.assertConsistency(type);

        EPAssertionUtil.assertEqualsAnyOrder(new Object[]{
                new EventPropertyDescriptor("nested1", Node.class, null, false, false, false, false, true),
                new EventPropertyDescriptor("prop4", String.class, null, false, false, false, false, false),
                new EventPropertyDescriptor("nested3", Node.class, null, false, false, false, false, true),
                new EventPropertyDescriptor("customProp", Double.class, null, false, false, false, false, false),
        }, type.getPropertyDescriptors());

        // update type and replace
        schemaUri = TestSchemaXMLEventReplace.class.getClassLoader().getResource(CLASSLOADER_SCHEMA_VERSION2_URI).toString();
        eventTypeMeta.setSchemaResource(schemaUri);
        eventTypeMeta.addXPathProperty("countProp", "count(/ss:simpleEvent/ss:nested3/ss:nested4)", XPathConstants.NUMBER);
        epService.getEPAdministrator().getConfiguration().replaceXMLEventType("TestXMLSchemaType", eventTypeMeta);

        wildStmt = epService.getEPAdministrator().createEPL(stmtSelectWild);
        type = wildStmt.getEventType();
        EventTypeAssertionUtil.assertConsistency(type);

        EPAssertionUtil.assertEqualsAnyOrder(new Object[]{
                new EventPropertyDescriptor("nested1", Node.class, null, false, false, false, false, true),
                new EventPropertyDescriptor("prop4", String.class, null, false, false, false, false, false),
                new EventPropertyDescriptor("prop5", String.class, null, false, false, false, false, false),
                new EventPropertyDescriptor("nested3", Node.class, null, false, false, false, false, true),
                new EventPropertyDescriptor("customProp", Double.class, null, false, false, false, false, false),
                new EventPropertyDescriptor("countProp", Double.class, null, false, false, false, false, false),
        }, type.getPropertyDescriptors());
    }

    private static final Log log = LogFactory.getLog(TestSchemaXMLEventReplace.class);
}
