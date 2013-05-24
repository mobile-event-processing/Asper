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

package com.espertech.esper.core.context.stmt;

public class AIRegistryExprSingle extends AIRegistryExprBase {

    public AIRegistrySubselect allocateAIRegistrySubselect() {
        return new AIRegistrySubselectSingle();
    }

    public AIRegistryPrevious allocateAIRegistryPrevious() {
        return new AIRegistryPreviousSingle();
    }

    public AIRegistryPrior allocateAIRegistryPrior() {
        return new AIRegistryPriorSingle();
    }
}
