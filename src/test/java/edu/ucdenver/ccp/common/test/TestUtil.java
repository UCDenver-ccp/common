/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.common.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.junit.internal.ArrayComparisonFailure;
import org.junit.rules.TemporaryFolder;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
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
	public static File populateTestFile(TemporaryFolder folder, String fileName, List<String> lines, CharacterEncoding encoding)
			throws FileNotFoundException, IOException {
		File file = folder.newFile(fileName);
		return populateTestFile(file, lines, encoding);
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
	public static File populateTestFile(File file, List<String> lines, CharacterEncoding encoding) throws FileNotFoundException, IOException {
		FileWriterUtil.printLines(lines, file, encoding, WriteMode.OVERWRITE, FileSuffixEnforcement.OFF);
		return file;
	}

	public static void conductBeanComparison(Object expected, Object observed) throws ArrayComparisonFailure,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		assertEquals(String.format("Beans must be the same class in order to be compared."), expected.getClass()
				.getName(), observed.getClass().getName());
		Collection<Method> getterMethods = getGetterMethods(expected);
		for (Method method : getterMethods) {
			String fieldName = method.getName().substring(3);
			if (!fieldName.equals("ByteOffset"))
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
					assertArrayEquals(String.format("Value for field %s must be the same.", fieldName),
							(float[]) method.invoke(expected), (float[]) method.invoke(observed), 0.000001f);
				} else if (method.getReturnType().getName().startsWith("[D")) {
					assertArrayEquals(String.format("Value for field %s must be the same.", fieldName),
							(double[]) method.invoke(expected), (double[]) method.invoke(observed), 0.000001d);
				} else if (method.getReturnType().getName().startsWith("[J")) {
					assertArrayEquals(String.format("Value for field %s must be the same.", fieldName), (long[]) method
							.invoke(expected), (long[]) method.invoke(observed));
				} else if (method.getReturnType().getName().startsWith("[S")) {
					assertArrayEquals(String.format("Value for field %s must be the same.", fieldName),
							(short[]) method.invoke(expected), (short[]) method.invoke(observed));
				} else if (method.getReturnType().getName().startsWith("[")) {
					assertArrayEquals(String.format("Value for field %s must be the same.", fieldName),
							(Object[]) method.invoke(expected), (Object[]) method.invoke(observed));
				} else {
					assertEquals(String.format("Value for field %s must be the same.", fieldName), method
							.invoke(expected), method.invoke(observed));
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
	
	public static <T> void checkNext(Iterator<T> iter, T expectedValue) {
		if (iter.hasNext())
			assertEquals(String.format("Value should be as expected"), expectedValue, iter.next());
		else
			fail("Iterator expected to have another value but did not.");
	}

}
