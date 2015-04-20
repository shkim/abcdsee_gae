/*
 * Copyright (c) 2009, Juraj Vitko, insose.com
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY Juraj Vitko ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Juraj Vitko BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.insose.gae.pager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Utils {

	public static Object reflexiveGet(Object instance, String fieldName) {
		
		Class clazz = instance.getClass();
		Field field = findClassField(clazz, fieldName);
		
		try {
			
			String getterPrefix = field.getType().equals(Boolean.TYPE) ? "is" : "get";
			String fn = field.getName();
			String getterBody = Character.toUpperCase(fn.charAt(0)) + fn.substring(1);
			String getterName = getterPrefix + getterBody;
			
			Method getter = clazz.getDeclaredMethod(getterName, (Class[]) null);
			getter.setAccessible(true);
			
			return getter.invoke(instance, (Object[]) null);
		}
		catch(SecurityException e) {
			throw new RuntimeException(e);
		}
		catch(NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		catch(InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static Field findClassField(Class clazz, String fieldName) {
		for(Class c = clazz; c != Object.class; c = c.getSuperclass()) {
			try {
				return c.getDeclaredField(fieldName);
			}
			catch(SecurityException e) {
				throw new RuntimeException(e);
			}
			catch(NoSuchFieldException e) {
				continue;
			}
		}
		throw new RuntimeException("no field [" + fieldName
				+ "] in class [" + clazz.getName() + "]");
	}

	public static Class<?> classForName(String typeName) {
		
		if(typeName.equals("boolean"))
			return boolean.class;
		if(typeName.equals("byte"))
			return byte.class;
		if(typeName.equals("char"))
			return char.class;
		if(typeName.equals("short"))
			return short.class;
		if(typeName.equals("int"))
			return int.class;
		if(typeName.equals("long"))
			return long.class;
		if(typeName.equals("float"))
			return float.class;
		if(typeName.equals("double"))
			return double.class;
		
		try {
			return Class.forName(typeName);
		}
		catch(ClassNotFoundException e) {
			return null;
		}
	}
}
