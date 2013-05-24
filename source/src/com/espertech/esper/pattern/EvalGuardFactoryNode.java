/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern;

import com.espertech.esper.epl.spec.PatternGuardSpec;
import com.espertech.esper.pattern.guard.GuardFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class represents a guard in the evaluation tree representing an event expressions.
 */
public class EvalGuardFactoryNode extends EvalNodeFactoryBase
{
    private static final long serialVersionUID = -6426206281275755119L;
    private PatternGuardSpec patternGuardSpec;
    private transient GuardFactory guardFactory;

    /**
     * Constructor.
     * @param patternGuardSpec - factory for guard construction
     */
    protected EvalGuardFactoryNode(PatternGuardSpec patternGuardSpec)
    {
        this.patternGuardSpec = patternGuardSpec;
    }

    public EvalNode makeEvalNode(PatternAgentInstanceContext agentInstanceContext) {
        EvalNode child = EvalNodeUtil.makeEvalNodeSingleChild(this.getChildNodes(), agentInstanceContext);
        return new EvalGuardNode(agentInstanceContext, this, child);
    }

    /**
     * Returns the guard object specification to use for instantiating the guard factory and guard.
     * @return guard specification
     */
    public PatternGuardSpec getPatternGuardSpec()
    {
        return patternGuardSpec;
    }

    /**
     * Supplies the guard factory to the node.
     * @param guardFactory is the guard factory
     */
    public void setGuardFactory(GuardFactory guardFactory)
    {
        this.guardFactory = guardFactory;
    }

    /**
     * Returns the guard factory.
     * @return guard factory
     */
    public GuardFactory getGuardFactory()
    {
        return guardFactory;
    }

    public final String toString()
    {
        return ("EvalGuardNode guardFactory=" + guardFactory +
                "  children=" + this.getChildNodes().size());
    }

    public boolean isFilterChildNonQuitting() {
        return false;
    }

    public boolean isStateful() {
        return true;
    }

    private static final Log log = LogFactory.getLog(EvalGuardFactoryNode.class);
}
