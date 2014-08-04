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

package com.espertech.esper.support.epl;

import com.espertech.esper.support.bean.SupportBean;
import com.espertech.esper.support.bean.SupportBeanNumeric;
import com.espertech.esper.support.bean.SupportBean_S0;
import com.espertech.esper.support.bean.SupportMarketDataBean;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupportStaticMethodLib 
{
    private static List<Object[]> invocations = new ArrayList<Object[]>();

    public static List<Object[]> getInvocations() {
        return invocations;
    }

    public static void setInvocations(List<Object[]> invocations) {
        SupportStaticMethodLib.invocations = invocations;
    }

    public static boolean compareEvents(SupportMarketDataBean beanOne, SupportBean beanTwo)
    {
        return beanOne.getSymbol().equals(beanTwo.getTheString());
    }

    public static Map fetchMapArrayMetadata()
    {
        Map<String, Class> values = new HashMap<String, Class>();
        values.put("mapstring", String.class);
        values.put("mapint", Integer.class);
        return values;
    }

    public static Map fetchSingleValueMetadata()
    {
        Map<String, Class> values = new HashMap<String, Class>();
        values.put("result", Integer.class);
        return values;
    }

    public static Map[] fetchResult12(Integer value)
    {
        if (value == null)
        {
            return new Map[0];
        }

        Map[] result = new Map[2];
        result[0] = new HashMap<String, Integer>();
        result[0].put("value", 1);
        result[1] = new HashMap<String, Integer>();
        result[1].put("value", 2);
        return result;
    }

    public static Map fetchResult12Metadata()
    {
        Map<String, Class> values = new HashMap<String, Class>();
        values.put("value", Integer.class);
        return values;
    }

    public static Map[] fetchResult23(Integer value)
    {
        if (value == null)
        {
            return new Map[0];
        }

        Map[] result = new Map[2];
        result[0] = new HashMap<String, Integer>();
        result[0].put("value", 2);
        result[1] = new HashMap<String, Integer>();
        result[1].put("value", 3);
        return result;
    }

    public static Map fetchResult23Metadata()
    {
        Map<String, Class> values = new HashMap<String, Class>();
        values.put("value", Integer.class);
        values.put("valueTwo", Integer.class);
        return values;
    }

    public static String join(SupportBean bean) {
        return bean.getTheString() + " " + Integer.toString(bean.getIntPrimitive());
    }

    public static Map[] fetchResult100()
    {
        Map[] result = new Map[100];
        int count = 0;
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                result[count] = new HashMap<String, Integer>();
                result[count].put("col1", i);
                result[count].put("col2", j);
                count++;
            }
        }
        return result;
    }

    public static Map fetchResult100Metadata()
    {
        Map<String, Class> values = new HashMap<String, Class>();
        values.put("col1", Integer.class);
        values.put("col2", Integer.class);
        return values;
    }

    public static Map[] fetchBetween(Integer lower, Integer upper)
    {
        if (lower == null || upper == null)
        {
            return new Map[0];
        }

        if (upper < lower)
        {
            return new Map[0];
        }
        
        int delta = upper - lower + 1;
        Map[] result = new Map[delta];
        int count = 0;
        for (int i = lower; i <= upper; i++)
        {
            Map<String, Integer> values = new HashMap<String, Integer>();
            values.put("value", i);
            result[count++] = values;
        }
        return result;
    }

    public static Map[] fetchBetweenString(Integer lower, Integer upper)
    {
        if (lower == null || upper == null)
        {
            return new Map[0];
        }

        if (upper < lower)
        {
            return new Map[0];
        }

        int delta = upper - lower + 1;
        Map[] result = new Map[delta];
        int count = 0;
        for (int i = lower; i <= upper; i++)
        {
            Map<String, String> values = new HashMap<String, String>();
            values.put("value", Integer.toString(i));
            result[count++] = values;
        }
        return result;
    }

    public static Map fetchBetweenMetadata()
    {
        Map<String, Class> values = new HashMap<String, Class>();
        values.put("value", Integer.class);
        return values;
    }

    public static Map fetchBetweenStringMetadata()
    {
        Map<String, Class> values = new HashMap<String, Class>();
        values.put("value", String.class);
        return values;
    }

    public static Map[] fetchMapArray(String theString, int id)
    {
        if (id < 0)
        {
            return null;
        }

        if (id == 0)
        {
            return new Map[0];
        }

        Map[] rows = new Map[id];
        for (int i = 0; i < id; i++)
        {
            Map<String, Object> values = new HashMap<String, Object>();
            rows[i] = values;

            values.put("mapstring", "|" + theString + "_" + i + "|");
            values.put("mapint", i + 100);
        }

        return rows;
    }

    public static Map fetchMapMetadata()
    {
        Map<String, Class> values = new HashMap<String, Class>();
        values.put("mapstring", String.class);
        values.put("mapint", Integer.class);
        return values;
    }

    public static Map fetchMap(String theString, int id)
    {
        if (id < 0)
        {
            return null;
        }

        Map<String, Object> values = new HashMap<String, Object>();
        if (id == 0)
        {
            return values;
        }
        
        values.put("mapstring", "|" + theString + "|");
        values.put("mapint", id + 1);
        return values;
    }

    public static Map fetchIdDelimitedMetadata()
    {
        Map<String, Class> values = new HashMap<String, Class>();
        values.put("result", String.class);
        return values;
    }

    public static Map fetchIdDelimited(Integer value)
    {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("result", "|" + value + "|");
        return values;
    }

    public static Map convertEventMap(Map<String, Object> values)
    {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("one", values.get("one"));
        result.put("two", "|" + values.get("two") + "|");
        return result;
    }

    public static Object[] convertEventObjectArray(Object[] values)
    {
        return new Object[] {values[0], "|" + values[1] + "|"};
    }

    public static SupportBean convertEvent(SupportMarketDataBean bean)
    {
        return new SupportBean(bean.getSymbol(), (bean.getVolume()).intValue());
    }

    public static Object staticMethod(Object object)
	{
		return object;
	}
	
    public static int arrayLength(Object object)
	{
        if (!object.getClass().isArray()) {
            return -1;
        }
		return Array.getLength(object);
	}

	public static void throwException() throws Exception
	{
		throw new Exception("throwException text here");
	}

    public static SupportBean throwExceptionBeanReturn() throws Exception
    {
        throw new Exception("throwException text here");
    }

    public static boolean isStringEquals(String value, String compareTo)
    {
        return value.equals(compareTo);
    }

    public static double minusOne(double value)
    {
        return value - 1;
    }

    public static int plusOne(int value)
    {
        return value + 1;
    }

    public static String appendPipe(String theString, String value)
    {
        return theString + "|" + value;
    }

    public static SupportBean_S0 fetchObjectAndSleep(String fetchId, int passThroughNumber, long msecSleepTime)
    {
        try
        {
            Thread.sleep(msecSleepTime);
        }
        catch (InterruptedException e)
        {
        }
        return new SupportBean_S0(passThroughNumber, "|" + fetchId + "|");
    }

    public static FetchedData fetchObjectNoArg()
    {
        return new FetchedData("2");
    }

    public static FetchedData fetchObject(String id)
    {
        if (id == null)
        {
            return null;
        }
        return new FetchedData("|" + id + "|");
    }

    public static FetchedData[] fetchArrayNoArg()
    {
        return new FetchedData[] { new FetchedData("1") }; 
    }

    public static FetchedData[] fetchArrayGen(int numGenerate)
    {
        if (numGenerate < 0)
        {
            return null;
        }
        if (numGenerate == 0)
        {
            return new FetchedData[0];
        }
        if (numGenerate == 1)
        {
            return new FetchedData[] { new FetchedData("A") };
        }

        FetchedData[] fetched = new FetchedData[numGenerate];
        for (int i = 0; i < numGenerate; i++)
        {
            int c = 'A' + i;
            fetched[i] = new FetchedData(Character.toString((char)c));
        }
        return fetched;
    }

    public static long passthru(long value)
    {
        return value;
    }

    public static void sleep(long msec)
    {
        try
        {
            Thread.sleep(msec);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException("Interrupted during sleep", e);
        }
    }

    public static boolean sleepReturnTrue(long msec)
    {
        try
        {
            Thread.sleep(msec);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException("Interrupted during sleep", e);
        }
        return true;
    }

    public static String delimitPipe(String theString)
    {
        if (theString == null)
        {
            return "|<null>|";
        }
        return "|" + theString + "|";
    }

    public static class FetchedData
    {
        private String id;

        public FetchedData(String id)
        {
            this.id = id;
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }
    }

    public static boolean volumeGreaterZero(SupportMarketDataBean bean)
    {
        return bean.getVolume() > 0;
    }

    public static BigInteger myBigIntFunc(BigInteger val)
    {
        return val;
    }

    public static BigDecimal myBigDecFunc(BigDecimal val)
    {
        return val;
    }

    public static Map<String, String> myMapFunc()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("A", "A1");
        map.put("B", "B1");
        return map;
    }

    public static int[] myArrayFunc()
    {
        return new int[] {100, 200, 300};
    }

    public static int arraySumIntBoxed(Integer[] array)
    {
        int sum = 0;
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == null)
            {
                continue;
            }
            sum += array[i];
        }
        return sum;
    }

    public static double arraySumDouble(Double[] array)
    {
        double sum = 0;
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == null)
            {
                continue;
            }
            sum += array[i];
        }
        return sum;
    }

    public static double arraySumString(String[] array)
    {
        double sum = 0;
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == null)
            {
                continue;
            }
            sum += Double.parseDouble(array[i]);
        }
        return sum;
    }

    public static boolean alwaysTrue(Object[] input) {
        invocations.add(input);
        return true;
    }

    public static double arraySumObject(Object[] array)
    {
        double sum = 0;
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == null)
            {
                continue;
            }
            if (array[i] instanceof Number)
            {
                sum += ((Number) array[i]).doubleValue();
            }
            else
            {
                sum += Double.parseDouble(array[i].toString());
            }
        }
        return sum;
    }

    public static SupportBean makeSupportBean(String theString, Integer intPrimitive) {
        return new SupportBean(theString, intPrimitive);
    }

    public static SupportBeanNumeric makeSupportBeanNumeric(Integer intOne, Integer intTwo) {
        return new SupportBeanNumeric(intOne, intTwo);
    }
}
