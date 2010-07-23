package edu.ucdenver.ccp.common.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;

/**
 * Provides access to private members in classes. Modified from
 * http://snippets.dzone.com/posts/show/2242
 */
public class PrivateAccessor {

	public static Object getPrivateField(Object o, String fieldName) {
		// Check we have valid arguments...
		Assert.assertNotNull(o);
		Assert.assertNotNull(fieldName);

		// Go and find the private field...
		final Field fields[] = o.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; ++i) {
			if (fieldName.equals(fields[i].getName())) {
				try {
					fields[i].setAccessible(true);
					return fields[i].get(o);
				} catch (IllegalAccessException ex) {
					Assert.fail("IllegalAccessException accessing " + fieldName);
				}
			}
		}
		Assert.fail("Field '" + fieldName + "' not found");
		return null;
	}

	public static Object invokePrivateMethod(Object o, String methodName, Object... params) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Assert.assertNotNull(o);
		Assert.assertNotNull(methodName);
//		Assert.assertNotNull(params);

		Class<?>[] paramClses = getParameterClasses(params);

		final Method method = o.getClass().getDeclaredMethod(methodName, paramClses);
		method.setAccessible(true);
		return method.invoke(o, params);
	}

	private static Class<?>[] getParameterClasses(Object... params) {
		if (params == null)
			return new Class<?>[0];
		Class<?>[] paramClses = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			paramClses[i] = params[i].getClass();
		}
		return paramClses;
	}
	
	
	public static Object invokeStaticPrivateMethod(Class<?> clazz, String methodName, Object... params)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		final Method method = clazz.getDeclaredMethod(methodName, getParameterClasses(params));
		method.setAccessible(true);
		return method.invoke(null, params);
	}
	

}
