package edu.ucdenver.ccp.common.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides access to private members in classes. Modified from
 * http://snippets.dzone.com/posts/show/2242
 */
public class PrivateAccessor {

	/**
	 * 
	 * @param o
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static Object getPrivateFieldValue(Object o, String fieldName) throws NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		return getPrivateField(o, fieldName).get(o);
	}

	/**
	 * If the field is not visible in the class provided, superclasses are searched.
	 * 
	 * @param o
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 */
	public static Field getPrivateField(Object o, String fieldName) throws NoSuchFieldException {
		Field field = getPrivateField(o.getClass(), fieldName);
		if (field == null)
			throw new NoSuchFieldException(String.format("No '%s' field can be found in class %s", fieldName, o
					.getClass().getName()));
		return field;
	}

	private static Field getPrivateField(Class<?> clazz, String fieldName) {
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

	public static Object invokePrivateMethod(Object o, String methodName, Object... params) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

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
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException,
			NoSuchMethodException {
		final Method method = clazz.getDeclaredMethod(methodName, getParameterClasses(params));
		method.setAccessible(true);
		return method.invoke(null, params);
	}

}
