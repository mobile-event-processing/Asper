/*
 * Copyright 2003,2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.asper.sources.net.sf.cglib.reflect;

import com.asper.sources.net.sf.cglib.core.Signature;
import com.asper.sources.org.objectweb.asm.Type;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FastClass
{
    private Class type;

    protected FastClass()
    {
        throw new Error("Using the FastClass empty constructor--please report to the cglib-devel mailing list");
    }

    protected FastClass(Class type)
    {
        this.type = type;
    }

    public static FastClass create(Class type)
    {
        return create(type.getClassLoader(), type);
    }

    public static FastClass create(ClassLoader loader, Class type)
    {
        return new FastClass(type);
    }

    public Object invoke(String name, Class[] parameterTypes, Object obj, Object[] args) throws InvocationTargetException
    {
        return invoke(getIndex(name, parameterTypes), obj, args);
    }

    public static final Class[] EMPTY_CLASS_ARRAY = {};

    public Object newInstance() throws InvocationTargetException
    {
        return newInstance(getIndex(EMPTY_CLASS_ARRAY), null);
    }

    public Object newInstance(Class[] parameterTypes, Object[] args) throws InvocationTargetException
    {
        return newInstance(getIndex(parameterTypes), args);
    }

    public FastMethod getMethod(Method method)
    {
        return new FastMethod(this, method);
    }

    public FastConstructor getConstructor(Constructor constructor)
    {
        return new FastConstructor(this, constructor);
    }

    public FastMethod getMethod(String name, Class[] parameterTypes)
    {
        try
        {
            return getMethod(type.getMethod(name, parameterTypes));
        } catch (NoSuchMethodException e)
        {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    public FastConstructor getConstructor(Class[] parameterTypes)
    {
        try
        {
            return getConstructor(type.getConstructor(parameterTypes));
        } catch (NoSuchMethodException e)
        {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    public String getName()
    {
        return type.getName();
    }

    public Class getJavaClass()
    {
        return type;
    }

    public String toString()
    {
        return type.toString();
    }

    public int hashCode()
    {
        return type.hashCode();
    }

    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof FastClass))
        {
            return false;
        }
        return type.equals(((FastClass) o).type);
    }

    /**
     * Return the index of the matching method. The index may be used
     * later to invoke the method with less overhead. If more than one
     * method matches (i.e. they differ by return type only), one is
     * chosen arbitrarily.
     *
     * @param name           the method name
     * @param parameterTypes the parameter array
     * @return the index, or <code>-1</code> if none is found.
     * @see #invoke(int, Object, Object[])
     */
    public int getIndex(String name, Class[] parameterTypes)
    {
        Method[] methods = type.getMethods();
        int index = 0;

        for (Method method : methods)
        {
            if (method.getName().equals(name))
            {
                Class<?>[] types = method.getParameterTypes();
                int length = types.length;
                boolean found = false;

                if (parameterTypes.length == length)
                {
                    found = true;

                    for (int i = 0; i < length; i++)
                    {
                        Class a = parameterTypes[i];
                        Class b = types[i];

                        if (!a.equals(b))
                            found = false;
                    }
                }

                if (found)
                    return index;
            }

            index++;
        }

        return -1;
    }

    /**
     * Return the index of the matching constructor. The index may be used
     * later to create a new instance with less overhead.
     *
     * @param parameterTypes the parameter array
     * @return the constructor index, or <code>-1</code> if none is found.
     * @see #newInstance(int, Object[])
     */
    public int getIndex(Class[] parameterTypes)
    {
        Method[] methods = type.getMethods();
        int index = 0;

        for (Method method : methods)
        {
            Class<?>[] types = method.getParameterTypes();
            int length = types.length;
            boolean found = false;

            if (parameterTypes.length == length)
            {
                found = true;

                for (int i = 0; i < length; i++)
                {
                    Class a = parameterTypes[i];
                    Class b = types[i];

                    if (!a.equals(b))
                        found = false;
                }
            }

            if (found)
                return index;
            else
                index++;
        }


        return -1;
    }

    public int getIndex(Signature signatureA)
    {
        Method[] methods = type.getMethods();
        int index = 0;

        for (Method method : methods)
        {
            Signature signatureB = new Signature(method.getName(), Type.getReturnType(method), Type.getArgumentTypes(method));
            if(signatureA.equals(signatureB))
                return index;
            else
                index++;
        }

        return -1;
    }

    /**
     * Invoke the method with the specified index.
     *
     * @param index the method index
     * @param obj   the object the underlying method is invoked from
     * @param args  the arguments used for the method call
     * @throws java.lang.reflect.InvocationTargetException
     *          if the underlying method throws an exception
     *
     */
    public Object invoke(int index, Object obj, Object[] args) throws InvocationTargetException
    {
        Method method = type.getMethods()[index];

        try
        {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e)
        {
            throw new InvocationTargetException(e);
        }
    }

    /**
     * Create a new instance using the specified constructor index and arguments.
     *
     * @param index the constructor index
     * @param args  the arguments passed to the constructor
     * @throws java.lang.reflect.InvocationTargetException
     *          if the constructor throws an exception
     *
     */
    public Object newInstance(int index, Object[] args) throws InvocationTargetException
    {
        try
        {
            return type.getConstructors()[index].newInstance(args);
        } catch (InstantiationException e)
        {
            throw new InvocationTargetException(e);
        } catch (IllegalAccessException e)
        {
            throw new InvocationTargetException(e);
        }
    }

    /**
     * Returns the maximum method index for this class.
     */
    public int getMaxIndex()
    {
        return type.getMethods().length;
    }

    protected static String getSignatureWithoutReturnType(String name, Class[] parameterTypes)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(name);
        sb.append('(');
        for (int i = 0; i < parameterTypes.length; i++)
        {
            sb.append(Type.getDescriptor(parameterTypes[i]));
        }
        sb.append(')');
        return sb.toString();
    }
}
