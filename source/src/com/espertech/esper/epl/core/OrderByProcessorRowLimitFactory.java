/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.RowLimitSpec;
import com.espertech.esper.epl.variable.VariableReader;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.util.JavaClassHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An order-by processor that sorts events according to the expressions
 * in the order_by clause.
 */
public class OrderByProcessorRowLimitFactory implements OrderByProcessorFactory {

	private static final Log log = LogFactory.getLog(OrderByProcessorImpl.class);

    private final VariableReader numRowsVariableReader;
    private final VariableReader offsetVariableReader;
    private int currentRowLimit;
    private int currentOffset;

    /**
     * Ctor.
     * @param rowLimitSpec specification for row limit, or null if no row limit is defined
     * @param variableService for retrieving variable state for use with row limiting
     * @throws com.espertech.esper.epl.expression.ExprValidationException if row limit specification validation fails
     */
    public OrderByProcessorRowLimitFactory(RowLimitSpec rowLimitSpec, VariableService variableService)
            throws ExprValidationException
    {
        if (rowLimitSpec.getNumRowsVariable() != null)
        {
            numRowsVariableReader = variableService.getReader(rowLimitSpec.getNumRowsVariable());
            if (numRowsVariableReader == null)
            {
                throw new ExprValidationException("Limit clause variable by name '" + rowLimitSpec.getNumRowsVariable() + "' has not been declared");
            }
            if (!JavaClassHelper.isNumeric(numRowsVariableReader.getType()))
            {
                throw new ExprValidationException("Limit clause requires a variable of numeric type");
            }
        }
        else
        {
            numRowsVariableReader = null;
            currentRowLimit = rowLimitSpec.getNumRows();

            if (currentRowLimit < 0)
            {
                currentRowLimit = Integer.MAX_VALUE;
            }
        }

        if (rowLimitSpec.getOptionalOffsetVariable() != null)
        {
            offsetVariableReader = variableService.getReader(rowLimitSpec.getOptionalOffsetVariable());
            if (offsetVariableReader == null)
            {
                throw new ExprValidationException("Limit clause variable by name '" + rowLimitSpec.getOptionalOffsetVariable() + "' has not been declared");
            }
            if (!JavaClassHelper.isNumeric(offsetVariableReader.getType()))
            {
                throw new ExprValidationException("Limit clause requires a variable of numeric type");
            }
        }
        else
        {
            offsetVariableReader = null;
            if (rowLimitSpec.getOptionalOffset() != null)
            {
                currentOffset = rowLimitSpec.getOptionalOffset();

                if (currentOffset <= 0)
                {
                    throw new ExprValidationException("Limit clause requires a positive offset");
                }
            }
            else
            {
                currentOffset = 0;
            }
        }
    }

    public OrderByProcessor instantiate(AggregationService aggregationService) {
        return new OrderByProcessorRowLimit(numRowsVariableReader, offsetVariableReader,
                currentRowLimit, currentOffset);
    }
}
