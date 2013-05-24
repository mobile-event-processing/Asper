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

package com.espertech.esper.util;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.annotation.AuditEnum;
import com.espertech.esper.event.EventBeanUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Global boolean for enabling and disable audit path reporting.
 */
public class AuditPath {

    private static final Log auditLogDestination = LogFactory.getLog(AuditPath.AUDIT_LOG);

    /**
     * Log destination for the query plan logging.
     */
    public static final String QUERYPLAN_LOG = "com.espertech.esper.queryplan"; 

    /**
     * Log destination for the JDBC logging.
     */
    public static final String JDBC_LOG = "com.espertech.esper.jdbc"; 

    /**
     * Log destination for the audit logging.
     */
    public static final String AUDIT_LOG = "com.espertech.esper.audit"; 

    /**
     * Public access.
     */
    public static boolean isAuditEnabled = false;

    private static String auditPattern;

    public static void setAuditPattern(String auditPattern) {
        AuditPath.auditPattern = auditPattern;
    }

    public static void auditInsertInto(String engineURI, String statementName, EventBean theEvent) {
        auditLog(engineURI, statementName, AuditEnum.INSERT, EventBeanUtility.summarize(theEvent));
    }

    public static void auditLog(String engineURI, String statementName, AuditEnum category, String message) {
        if (auditPattern == null) {
            StringBuilder buf = new StringBuilder();
            buf.append("Statement ");
            buf.append(statementName);
            buf.append(" ");
            buf.append(category.getPrettyPrintText());
            buf.append(" ");
            buf.append(message);
            auditLogDestination.info(buf.toString());
        }
        else {
            String result = auditPattern.replace("%s", statementName).replace("%u", engineURI).replace("%c", category.getValue()).replace("%m", message);
            auditLogDestination.info(result);
        }
    }

    public static boolean isInfoEnabled() {
        return auditLogDestination.isInfoEnabled();
    }
}