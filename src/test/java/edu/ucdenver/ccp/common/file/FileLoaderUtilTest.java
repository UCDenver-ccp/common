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
package edu.ucdenver.ccp.common.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.reader.StreamLineIterator;
import edu.ucdenver.ccp.common.string.RegExPatterns;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class FileLoaderUtilTest extends DefaultTestCase {

	private static final String COMMENT_INDICATOR = StringConstants.TWO_FORWARD_SLASHES;

	private static final String IS_COLUMN_INDEX_VALID_METHOD_NAME = "isColumnIndexValid";

	private File oneColumnFile;
	private File emptyFile;
	private File fiveColumnFile;
	private File fiveColumnFileWithErrorOnLineThree;
	private File fiveColumnFileWithCommentOnLineFour;

	@Before
	public void setUp() throws Exception {
		createSampleFiles();
	}

	/**
	 * Create the sample files that will be used to test the FileLoaderUtil
	 * 
	 * @throws IOException
	 */
	private void createSampleFiles() throws IOException {
		createEmptyFile();
		createOneColumnFile();
		createFiveColumnFile();
		createFiveColumnFileWithErrorOnLineThree();
		createFiveColumnFileWithCommentOnLineFour();
	}

	/**
	 * Creates an empty file
	 * 
	 * @throws IOException
	 */
	private void createEmptyFile() throws IOException {
		emptyFile = folder.newFile("emptyFile");
		PrintStream ps = new PrintStream(emptyFile);
		ps.close();
	}

	/**
	 * Creates a file with one column of data
	 * 
	 * @throws IOException
	 */
	private void createOneColumnFile() throws IOException {
		oneColumnFile = folder.newFile("oneColumnFile");
		PrintStream ps = new PrintStream(oneColumnFile);
		printLinesToStream(ps, getOneColumnLines());
		ps.close();
	}

	private List<String> getOneColumnLines() {
		return CollectionsUtil.createList("11", "21", "31", "41", "51", "61");
	}

	/**
	 * Creates a file with five columns of data.
	 * 
	 * @throws IOException
	 */
	private void createFiveColumnFile() throws IOException {
		fiveColumnFile = folder.newFile("fiveColumnFile");
		PrintStream ps = new PrintStream(fiveColumnFile);
		printLinesToStream(ps, getFiveColumnLines());
		ps.close();
	}

	private List<String> getFiveColumnLines() {
		return CollectionsUtil.createList("11\t12\t13\t14\t15", "21\t22\t23\t24\t25", "31\t32\t33\t34\t35",
				"41\t42\t43\t44\t45", "51\t52\t53\t54\t55", "61\t62\t63\t64\t65");
	}

	/**
	 * Creates a file with five columns of data, except on line three where there are only 4
	 * columns.
	 * 
	 * @throws IOException
	 */
	private void createFiveColumnFileWithErrorOnLineThree() throws IOException {
		fiveColumnFileWithErrorOnLineThree = folder.newFile("fiveColumnFileWithErrorLineThree");
		PrintStream ps = new PrintStream(fiveColumnFileWithErrorOnLineThree);
		printLinesToStream(ps, getFiveColumnLinesWithMissingDataOnLineThree());
		ps.close();
	}

	private List<String> getFiveColumnLinesWithMissingDataOnLineThree() {
		return CollectionsUtil.createList("11\t12\t13\t14\t15", "21\t22\t23\t24\t25", "31\t32\t33\t34",
				"41\t42\t43\t44\t45", "51\t52\t53\t54\t55", "61\t62\t63\t64\t65");
	}

	/**
	 * Creates a file with five columns of data.
	 * 
	 * @throws IOException
	 */
	private void createFiveColumnFileWithCommentOnLineFour() throws IOException {
		fiveColumnFileWithCommentOnLineFour = folder.newFile("fiveColumnFileWithCommentOnLineFour");
		PrintStream ps = new PrintStream(fiveColumnFileWithCommentOnLineFour);
		printLinesToStream(ps, getFiveColumnLinesWithCommentOnLineFour());
		ps.close();
	}

	private List<String> getFiveColumnLinesWithCommentOnLineFour() {
		return CollectionsUtil.createList("11\t12\t13\t14\t15", "21\t22\t23\t24\t25", "31\t32\t33\t34\t35",
				COMMENT_INDICATOR + "41\t42\t43\t44\t45", "51\t52\t53\t54\t55", "61\t62\t63\t64\t65");
	}

	private void printLinesToStream(PrintStream ps, List<String> lines) {
		for (String line : lines) {
			ps.println(line);
		}
	}

	@Test(expected = NoSuchElementException.class)
	public void testExceptionThrownIfFileHasNoLines() throws Exception {
		StreamLineIterator lineIter = new StreamLineIterator(emptyFile, CharacterEncoding.US_ASCII, null);
		lineIter.next();
	}

	@Test
	public void testIteratorOnOneColumnFile() throws Exception {
		List<String> expectedLines = getOneColumnLines();
		int index = 0;
		for (StreamLineIterator lineIter = new StreamLineIterator(oneColumnFile,CharacterEncoding.US_ASCII, null); lineIter.hasNext();) {
			assertEquals(String.format("Lines should be returned in order."), expectedLines.get(index++), lineIter
					.next().getText());
		}
	}

	@Test
	public void testLoadLinesFromSingleColumnFile() throws Exception {
		List<String> expectedColumn = getOneColumnLines();
		assertEquals(String.format("The single column in this file should be extracted"), expectedColumn,
				FileReaderUtil.loadLinesFromFile(oneColumnFile, CharacterEncoding.US_ASCII));
	}

	@Test
	public void testLoadLinesFromFiveColumnFile() throws Exception {
		List<String> expectedColumn = getFiveColumnLines();
		assertEquals(String.format("All five columns in this file should be extracted"), expectedColumn, FileReaderUtil
				.loadLinesFromFile(fiveColumnFile, CharacterEncoding.US_ASCII));
	}

	@Test
	public void testLoadFirstColumnFromSingleColumnFile() throws Exception {
		List<String> expectedColumn = getOneColumnLines();
		assertEquals(String.format("The single column in this file should be extracted"), expectedColumn,
				FileReaderUtil.loadColumnFromDelimitedFile(oneColumnFile,CharacterEncoding.US_ASCII, null, null, 0));
	}

	@Test
	public void testIteratorOnFiveColumnFile() throws Exception {
		List<String> expectedLines = getFiveColumnLines();
		int index = 0;
		for (StreamLineIterator lineIter = new StreamLineIterator(fiveColumnFile, CharacterEncoding.US_ASCII,null); lineIter.hasNext();) {
			assertEquals(String.format("Lines should be returned in order."), expectedLines.get(index++), lineIter
					.next().getText());
		}
	}

	@Test
	public void testLoadThirdColumnFromFiveColumnFile() throws Exception {
		List<String> expectedColumn = CollectionsUtil.createList("13", "23", "33", "43", "53", "63");
		assertEquals(String.format("The single column in this file should be extracted"), expectedColumn,
				FileReaderUtil.loadColumnFromDelimitedFile(fiveColumnFile, CharacterEncoding.US_ASCII,RegExPatterns.TAB, null, 2));
	}

	@Test
	public void testLoadThirdColumnFromFiveColumnFileWithMissingDataOnLineThree() throws Exception {
		List<String> expectedColumn = CollectionsUtil.createList("13", "23", "33", "43", "53", "63");
		assertEquals(String.format("The single column in this file should be extracted"), expectedColumn,
				FileReaderUtil.loadColumnFromDelimitedFile(fiveColumnFileWithErrorOnLineThree, CharacterEncoding.US_ASCII,RegExPatterns.TAB, null,
						2));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testLoadFifthColumnFromFiveColumnFileWithMissingDataOnLineThree() throws Exception {
		FileReaderUtil.loadColumnFromDelimitedFile(fiveColumnFileWithErrorOnLineThree, CharacterEncoding.US_ASCII,RegExPatterns.TAB, null, 5);
	}

	@Test
	public void testLoadThirdColumnFromFiveColumnFileWithCommentOnLineFour() throws Exception {
		List<String> expectedColumn = CollectionsUtil.createList("13", "23", "33", "53", "63");
		assertEquals(String.format("The single column in this file should be extracted"), expectedColumn,
				FileReaderUtil.loadColumnFromDelimitedFile(fiveColumnFileWithCommentOnLineFour, CharacterEncoding.US_ASCII,RegExPatterns.TAB,
						COMMENT_INDICATOR, 2));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testInvalidColumnIndex_too_high_ReturnsException() throws Exception {
		FileReaderUtil.loadColumnFromDelimitedFile(fiveColumnFileWithCommentOnLineFour,CharacterEncoding.US_ASCII, RegExPatterns.TAB, null, 15);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testInvalidColumnIndex_too_low_ReturnsException() throws Exception {
		FileReaderUtil.loadColumnFromDelimitedFile(fiveColumnFileWithCommentOnLineFour, CharacterEncoding.US_ASCII,RegExPatterns.TAB, null, -1);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testRemoveThrowsException() throws Exception {
		StreamLineIterator lineIter = new StreamLineIterator(oneColumnFile, CharacterEncoding.US_ASCII, null);
		lineIter.remove();
	}

	@Test
	public void testLoadColumnsTwoThreeAndFiveFromFiveColumnFile() throws Exception {
		List<String[]> expectedColumns = CollectionsUtil.createList(new String[] { "12", "13", "15" }, new String[] {
				"22", "23", "25" }, new String[] { "32", "33", "35" }, new String[] { "42", "43", "45" }, new String[] {
				"52", "53", "55" }, new String[] { "62", "63", "65" });

		List<String[]> extractedColumns = FileReaderUtil.loadColumnsFromDelimitedFile(fiveColumnFile, CharacterEncoding.US_ASCII,
				RegExPatterns.TAB, null, 1, 2, 4);
		compareListsOfStringArrays(expectedColumns, extractedColumns);
	}

	@Test
	public void testNullDelimiterReturnsEntireLine() throws Exception {
		List<String[]> expectedColumns = CollectionsUtil.createList(new String[] { getFiveColumnLines().get(0) },
				new String[] { getFiveColumnLines().get(1) }, new String[] { getFiveColumnLines().get(2) },
				new String[] { getFiveColumnLines().get(3) }, new String[] { getFiveColumnLines().get(4) },
				new String[] { getFiveColumnLines().get(5) });

		List<String[]> extractedColumns = FileReaderUtil.loadColumnsFromDelimitedFile(fiveColumnFile,CharacterEncoding.US_ASCII, null, null, 2, 3,
				5);
		compareListsOfStringArrays(expectedColumns, extractedColumns);
	}

	/**
	 * Asserts that the input lists of String[] arrays are equal
	 * 
	 * @param expectedList
	 * @param observedList
	 */
	private void compareListsOfStringArrays(List<String[]> expectedList, List<String[]> observedList) {
		assertEquals(String.format("# of lines in observed list should be as expected."), expectedList.size(),
				observedList.size());
		for (int i = 0; i < expectedList.size(); i++) {
			assertArrayEquals(String.format("Line %d should be as expected.", i), expectedList.get(i), observedList
					.get(i));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentExceptionIfDelimiterButNoColumnIndexes() throws Exception {
		FileReaderUtil.loadColumnsFromDelimitedFile(fiveColumnFile,CharacterEncoding.US_ASCII, RegExPatterns.TAB, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentExceptionIfDelimiterButEmptyColumnIndexes() throws Exception {
		FileReaderUtil.loadColumnsFromDelimitedFile(fiveColumnFile, CharacterEncoding.US_ASCII,RegExPatterns.TAB, null, new int[0]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentExceptionIfDelimiterButNullColumnIndexes() throws Exception {
		FileReaderUtil.loadColumnsFromDelimitedFile(fiveColumnFile,CharacterEncoding.US_ASCII, RegExPatterns.TAB, null, null);
	}

	@Test
	public void testIsColumnIndexValid() throws Exception {
		assertTrue(callIsColumnIndexValid(0, new String[3]));
		assertTrue(callIsColumnIndexValid(1, new String[3]));
		assertTrue(callIsColumnIndexValid(2, new String[3]));
		assertFalse(callIsColumnIndexValid(3, new String[3]));
	}

	private boolean callIsColumnIndexValid(int columnIndex, String[] columns) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method method = FileReaderUtil.class.getDeclaredMethod(IS_COLUMN_INDEX_VALID_METHOD_NAME, int.class,
				String[].class);
		method.setAccessible(true);
		return (Boolean) method.invoke(null, columnIndex, columns);
	}

}
