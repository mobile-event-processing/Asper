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

package com.espertech.esper.support.util;

public class IndexAssertion {
    private final String hint;
    private final String whereClause;
    private final String expectedIndexName;
    private final String indexBackingClass;
    private final Boolean unique;
    private final IndexAssertionEventSend eventSendAssertion;
    private final IndexAssertionFAF fafAssertion;

    public IndexAssertion(String hint, String whereClause) {
        this(hint, whereClause, null, null, null, null, null);
    }

    public IndexAssertion(String hint, String whereClause, String expectedIndexName, String indexBackingClass, IndexAssertionEventSend eventSendAssertion) {
        this(hint, whereClause, expectedIndexName, indexBackingClass, null, eventSendAssertion, null);
    }

    public IndexAssertion(String hint, String whereClause, String expectedIndexName, String indexBackingClass, IndexAssertionFAF fafAssertion) {
        this(hint, whereClause, expectedIndexName, indexBackingClass, null, null, fafAssertion);
    }

    public IndexAssertion(String hint, String whereClause, boolean unique, IndexAssertionEventSend eventSendAssertion) {
        this(hint, whereClause, null, null, unique, eventSendAssertion, null);
    }

    public IndexAssertion(String hint, String whereClause, boolean unique, IndexAssertionFAF fafAssertion) {
        this(hint, whereClause, null, null, unique, null, fafAssertion);
    }

    public IndexAssertion(String hint, String whereClause, String expectedIndexName, String indexBackingClass, Boolean unique, IndexAssertionEventSend eventSendAssertion, IndexAssertionFAF fafAssertion) {
        this.hint = hint;
        this.whereClause = whereClause;
        this.expectedIndexName = expectedIndexName;
        this.indexBackingClass = indexBackingClass;
        this.unique = unique;
        this.eventSendAssertion = eventSendAssertion;
        this.fafAssertion = fafAssertion;
    }

    public String getHint() {
        return hint;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public IndexAssertionEventSend getEventSendAssertion() {
        return eventSendAssertion;
    }

    public String getExpectedIndexName() {
        return expectedIndexName;
    }

    public String getIndexBackingClass() {
        return indexBackingClass;
    }

    public IndexAssertionFAF getFafAssertion() {
        return fafAssertion;
    }

    public Boolean getUnique() {
        return unique;
    }
}
