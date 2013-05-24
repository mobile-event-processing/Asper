/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.declexpr;

import com.espertech.esper.client.EventType;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.StreamTypeServiceImpl;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.spec.ExpressionDeclItem;
import com.espertech.esper.util.SerializableObjectCopier;

import java.util.ArrayList;
import java.util.List;

/**
 * Expression instance as declared elsewhere.
 *
 * (1) Statement parse: Expression tree from expression body gets deep-copied.
 * (2) Statement create (lifecyle event): Subselect visitor compiles Subselect-list
 * (3) Statement start:
 *     a) event types of each stream determined
 *     b) subselects filter expressions get validated and subselect started
 * (4) Remaining expressions get validated
 */
public interface ExprDeclaredNode extends ExprNode
{
    public List<ExprNode> getChainParameters();
    public ExpressionDeclItem getPrototype();
    public void setSubselectOuterStreamNames(String[] outerStreamNames,
                                             EventType[] outerEventTypesSelect,
                                             String[] outerEventTypeNames,
                                             String engineURI,
                                             ExprSubselectNode subselect,
                                             String subexpressionStreamName,
                                             EventType subselectStreamType,
                                             String subselecteventTypeName) throws ExprValidationException;
}