package edu.ucdenver.ccp.common.reflection;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides access to private members in classes. Modified from
 * http://snippets.dzone.com/posts/show/2242
 */
public class PrivateAccessor {

	/**
	 * Uses the Reflection API to return the value of the input field name for the input Object.
	 * Inherited fields are supported.
	 * 
	 * @param o
	 *            object
	 * @param fieldName
	 *            object's field name
	 * @return value field value
	 * 
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static Object getPrivateFieldValue(Object o, String fieldName) throws NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		return getPrivateMemberField(o, fieldName).get(o);
	}

	/**
	 * Returns a constant value from a private static field in the input class with the input field
	 * name
	 * 
	 * @param cls
	 * @param fieldName
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Object getPrivateStaticFieldValue(Class<?> cls, String fieldName) {
		try {
			return getPrivateField(cls, fieldName).get(null);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Uses the Reflection API to set the value of a private member variable for the input Object.
	 * 
	 * @param o
	 * @param fieldName
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public static void setPrivateFinalFieldValue(Object o, String fieldName, Object value)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		Field field = getPrivateMemberField(o, fieldName);
		field.setAccessible(true);
		/* The following removes the final modifier from the field */
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(o, value);
	}

	/**
	 * If the field is not visible in the class provided, superclasses are searched.
	 * 
	 * @param o
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 */
	public static Field getPrivateMemberField(Object o, String fieldName) throws NoSuchFieldException {
		Field field = getPrivateField(o.getClass(), fieldName);
		if (field == null)
			throw new NoSuchFieldException(String.format("No '%s' field can be found in class %s", fieldName, o
					.getClass().getName()));
		return field;
	}

	/**
	 * Returns the private field with the specified name from the specified class
	 * 
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Field getPrivateField(Class<?> clazz, String fieldName) {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				return field;
			}
		}
		Class<?> superclass = clazz.getSuperclass();
		if (superclass == null)
			return null;
		return getPrivateField(superclass, fieldName);
	}

	/**
	 * Get all declared and inherited class fields recursively
	 * 
	 * @param clazz
	 *            class to reflect on
	 * @param fields
	 *            to hold return value
	 * @return all declared and inherited fields
	 */
	public static Set<Field> getAllFields(Class<?> clazz, Set<Field> fields) {
		for (Field field : clazz.getDeclaredFields()) {
			fields.add(field);
			field.setAccessible(true);
			fields.add(field);
		}

		Class<?> superclass = clazz.getSuperclass();
		if (superclass == null)
			return fields;

		return getAllFields(superclass, fields);
	}

	/**
	 * Helper method for invoking a private method using the specified set of parameters
	 * 
	 * @param o
	 * @param methodName
	 * @param params
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object invokePrivateMethod(Object o, String methodName, Object... params) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Class<?>[] paramClses = getParameterClasses(params);

		final Method method = o.getClass().getDeclaredMethod(methodName, paramClses);
		method.setAccessible(true);
		return method.invoke(o, params);
	}

	/**
	 * Returns an array of Classes corresponding to the classes of the input array
	 * 
	 * @param params
	 * @return
	 */
	private static Class<?>[] getParameterClasses(Object... params) {
		if (params == null)
			return new Class<?>[0];
		Class<?>[] paramClses = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			paramClses[i] = params[i].getClass();
		}
		return paramClses;
	}

	/**
	 * Invokes a static private method using the specified parameters
	 * 
	 * @param clazz
	 * @param methodName
	 * @param params
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public static Object invokeStaticPrivateMethod(Class<?> clazz, String methodName, Object... params)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException,
			NoSuchMethodException {
		final Method method = clazz.getDeclaredMethod(methodName, getParameterClasses(params));
		method.setAccessible(true);
		return method.invoke(null, params);
	}

	/**
	 * Generate field value extraction message listing all available fields.
	 * 
	 * @param clazz
	 * @param fieldName
	 * @return error message
	 */
	private static String getFieldValueExtractionErrorMessage(Class<?> clazz, String fieldName) {
		List<String> validFieldNames = new ArrayList<String>();
		Set<Field> fields = new HashSet<Field>();
		getAllFields(clazz, fields);
		for (Field field : fields)
			validFieldNames.add(field.getName());
		return String.format("Unable to extract field value from DataRecord for field: %s. Valid field names: %s",
				fieldName, validFieldNames.toString());
	}

	/**
	 * Uses the Reflection API to return the value of the input field name for the input Object.
	 * Inherited fields are supported.
	 * 
	 * @param o
	 *            object
	 * @param fieldName
	 *            object's field name
	 * @return value field value
	 * 
	 * @throws RuntimeException
	 *             if errors occur during field value access
	 */
	public static Object getFieldValue(Object o, String fieldName) {
		try {
			return getPrivateFieldValue(o, fieldName);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(getFieldValueExtractionErrorMessage(o.getClass(), fieldName), e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(getFieldValueExtractionErrorMessage(o.getClass(), fieldName), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(getFieldValueExtractionErrorMessage(o.getClass(), fieldName), e);
		}
	}

}
