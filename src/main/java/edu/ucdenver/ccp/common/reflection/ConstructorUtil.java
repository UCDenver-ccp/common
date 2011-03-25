package edu.ucdenver.ccp.common.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for invoking constructors
 * 
 * @author bill
 * 
 */
public class ConstructorUtil {

	/**
	 * Returns an instantiation of the class specified by the input {@link String} using the
	 * arguments provided.
	 * 
	 * @param className
	 * @param arguments
	 * @return
	 */
	public static Object invokeConstructor(String className, Object... arguments) {
		try {
			Class<?> cls = Class.forName(className);
			Class<?>[] parameterTypes = new Class<?>[arguments.length];
			int i = 0;
			for (Object argument : arguments)
				parameterTypes[i++] = argument.getClass();
			Constructor<?> constructor = cls.getConstructor(parameterTypes);
			if (!constructor.isAccessible())
				constructor.setAccessible(true);
			return constructor.newInstance(arguments);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	
	
}
