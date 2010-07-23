package edu.ucdenver.ccp.common.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.junit.internal.ArrayComparisonFailure;
import org.junit.rules.TemporaryFolder;

import edu.ucdenver.ccp.common.string.RegExPatterns;

/**
 * A collection of utility methods useful when writing unit tests.
 * 
 * @author Bill Baumgartner
 * 
 */
public class TestUtil {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(TestUtil.class);

	/**
	 * Creates a file in the provided TemporaryFolder and populates it with the specified lines.
	 * 
	 * @param folder
	 * @param fileName
	 * @param lines
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static File populateTestFile(TemporaryFolder folder, String fileName, List<String> lines)
			throws FileNotFoundException, IOException {
		File file = folder.newFile(fileName);
		return populateTestFile(file, lines);
	}

	/**
	 * Populates the input file with the specified lines
	 * 
	 * @param file
	 * @param lines
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static File populateTestFile(File file, List<String> lines) throws FileNotFoundException, IOException {
		PrintStream ps = new PrintStream(file);
		for (String line : lines) {
			ps.println(line);
		}
		ps.close();
		return file;
	}

	public static void conductBeanComparison(Object expected, Object observed) throws ArrayComparisonFailure,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		assertEquals(String.format("Beans must be the same class in order to be compared."), expected.getClass()
				.getName(), observed.getClass().getName());
		Collection<Method> getterMethods = getGetterMethods(expected);
		for (Method method : getterMethods) {
			String fieldName = method.getName().substring(3);
			if (method.getReturnType().getName().startsWith("[I")) {
				assertArrayEquals(String.format("Value for field %s must be the same.", fieldName), (int[]) method
						.invoke(expected), (int[]) method.invoke(observed));
			} else if (method.getReturnType().getName().startsWith("[B")) {
				assertArrayEquals(String.format("Value for field %s must be the same.", fieldName), (byte[]) method
						.invoke(expected), (byte[]) method.invoke(observed));
			} else if (method.getReturnType().getName().startsWith("[C")) {
				assertArrayEquals(String.format("Value for field %s must be the same.", fieldName), (char[]) method
						.invoke(expected), (char[]) method.invoke(observed));
			} else if (method.getReturnType().getName().startsWith("[F")) {
				assertArrayEquals(String.format("Value for field %s must be the same.", fieldName), (float[]) method
						.invoke(expected), (float[]) method.invoke(observed), 0.000001f);
			} else if (method.getReturnType().getName().startsWith("[D")) {
				assertArrayEquals(String.format("Value for field %s must be the same.", fieldName), (double[]) method
						.invoke(expected), (double[]) method.invoke(observed), 0.000001d);
			} else if (method.getReturnType().getName().startsWith("[J")) {
				assertArrayEquals(String.format("Value for field %s must be the same.", fieldName), (long[]) method
						.invoke(expected), (long[]) method.invoke(observed));
			} else if (method.getReturnType().getName().startsWith("[S")) {
				assertArrayEquals(String.format("Value for field %s must be the same.", fieldName), (short[]) method
						.invoke(expected), (short[]) method.invoke(observed));
			} else if (method.getReturnType().getName().startsWith("[")) {
				assertArrayEquals(String.format("Value for field %s must be the same.", fieldName), (Object[]) method
						.invoke(expected), (Object[]) method.invoke(observed));
			} else {
				assertEquals(String.format("Value for field %s must be the same.", fieldName), method.invoke(expected),
						method.invoke(observed));
			}
		}
	}

	private static Collection<Method> getGetterMethods(Object obj) {
		Collection<Method> getterMethodNames = new ArrayList<Method>();
		for (Method method : obj.getClass().getMethods()) {
			if (isGetterMethod(method)) {
				getterMethodNames.add(method);
			}
		}
		return getterMethodNames;
	}

	/**
	 * Returns true if the method starts with get followed by an upper case letter, false otherwise
	 * 
	 * @param method
	 * @return
	 */
	private static boolean isGetterMethod(Method method) {
		Pattern getMethodPattern = Pattern.compile(RegExPatterns.GETTER_METHOD_NAME_PATTERN);
		Matcher m = getMethodPattern.matcher(method.getName());
		return m.find();
	}

}
