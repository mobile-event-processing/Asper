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

package com.espertech.esper.epl.join.hint;

import com.espertech.esper.client.EPException;
import com.espertech.esper.client.annotation.HintEnum;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.epl.annotation.AnnotationException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndexHint {

    private final List<SelectorInstructionPair> pairs;

    public IndexHint(List<SelectorInstructionPair> pairs) {
        this.pairs = pairs;
    }

    public static IndexHint getIndexHint(Annotation[] annotations) {
        List<String> hints = HintEnum.INDEX.getHintAssignedValues(annotations);
        if (hints == null) {
            return null;
        }
        List<SelectorInstructionPair> pairs = new ArrayList<SelectorInstructionPair>();
        for (String hint : hints) {
            String[] hintAtoms = HintEnum.splitCommaUnlessInParen(hint);
            List<IndexHintSelector> selectors = new ArrayList<IndexHintSelector>();
            List<IndexHintInstruction> instructions = new ArrayList<IndexHintInstruction>();
            for (int i = 0; i < hintAtoms.length; i++) {
                String hintAtom = hintAtoms[i];
                if (hintAtom.toLowerCase().trim().equals("bust")) {
                    instructions.add(new IndexHintInstructionBust());
                }
                else if (hintAtom.toLowerCase().trim().equals("explicit")) {
                    instructions.add(new IndexHintInstructionExplicit());
                }
                else if (checkValue("subquery", hintAtom.toLowerCase())) {
                    int subqueryNum = extractValue(hintAtom);
                    selectors.add(new IndexHintSelectorSubquery(subqueryNum));
                }
                else {
                    instructions.add(new IndexHintInstructionIndexName(hintAtom.trim()));
                }
            }
            pairs.add(new SelectorInstructionPair(selectors, instructions));
        }
        return new IndexHint(pairs);
    }

    public List<IndexHintInstruction> getInstructionsSubquery(int subqueryNumber) {
        for (SelectorInstructionPair pair : pairs) {
            if (pair.getSelector().isEmpty()) { // empty selector mean hint applies to all
                return pair.getInstructions();
            }
            for (IndexHintSelector selector : pair.getSelector()) {
                if (selector.matchesSubquery(subqueryNumber)) {
                    return pair.getInstructions();
                }
            }
        }
        return Collections.emptyList();
    }

    public List<IndexHintInstruction> getInstructionsFireAndForget() {
        for (SelectorInstructionPair pair : pairs) {
            if (pair.getSelector().isEmpty()) { // empty selector mean hint applies to all
                return pair.getInstructions();
            }
        }
        return Collections.emptyList();
    }

    private static boolean checkValue(String type, String value) {
        int indexOpen = value.indexOf('(');
        if (indexOpen != -1) {
            String noparen = value.substring(0, indexOpen);
            if (type.equals(noparen)) {
                return true;
            }
        }
        return false;
    }

    private static int extractValue(String text) {
        int indexOpen = text.indexOf('(');
        int indexClosed = text.lastIndexOf(')');
        if (indexOpen != -1) {
            String value = text.substring(indexOpen + 1, indexClosed);
            try {
                return Integer.parseInt(value);
            }
            catch (Exception ex) {
                throw new EPException("Failed to parse '" + value + "' as an index hint integer value");
            }
        }
        throw new IllegalStateException("Not a parentheses value");
    }
}
