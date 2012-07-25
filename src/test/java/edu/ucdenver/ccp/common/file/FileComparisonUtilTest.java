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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.FileComparisonUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.ColumnOrder;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.LineOrder;
import edu.ucdenver.ccp.common.string.RegExPatterns;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class FileComparisonUtilTest extends DefaultTestCase {
	private static final String COLUMN_DELIMITER_REGEX = RegExPatterns.TAB;

	private static final CharacterEncoding ENCODING = CharacterEncoding.UTF_8;
	private File baseFile;
	private File baseFileLinesInOrderColumnsInOrder;
	private File baseFileLinesMixedOrderColumnsInOrder;
	private File baseFileLinesInOrderColumnsMixedOrder;
	private File baseFileLinesMixedOrderColumnsMixedOrder;
	private File baseFileMissingLine;

	@Before
	public void setUp() throws Exception {
		initializeFilesToCompare();
	}

	private void initializeFilesToCompare() throws IOException {
		initializeBaseFile();
		initializeBaseFileLinesInOrderColumnsInOrder();
		initializeBaseFileLinesMixedColumnsInOrder();
		initializeBaseFileLinesInOrderColumnsMixed();
		initializeBaseFileLinesMixedColumnsMixed();
		initializeBaseFileMissingLine();
	}

	private void initializeBaseFile() throws IOException {
		baseFile = folder.newFile("base.utf8");
		FileWriterUtil.printLines(getBaseFileLines(), baseFile, ENCODING);
	}

	private List<String> getBaseFileLines() {
		List<String> fileLines = CollectionsUtil.createList("11\t12\t13\t14\t15", "21\t22\t23\t24\t25",
				"31\t32\t33\t34\t35", "41\t42\t43\t44\t45");
		return fileLines;
	}

	private void initializeBaseFileLinesInOrderColumnsInOrder() throws IOException {
		baseFileLinesInOrderColumnsInOrder = folder.newFile("base.order.order.utf8");
		FileWriterUtil.printLines(getBaseFileLines(), baseFileLinesInOrderColumnsInOrder, ENCODING);
	}

	private void initializeBaseFileLinesMixedColumnsMixed() throws IOException {
		baseFileLinesMixedOrderColumnsMixedOrder = folder.newFile("base.mixed.mixed.utf8");
		FileWriterUtil.printLines(getBaseFileLinesMixedColumnsMixedLines(), baseFileLinesMixedOrderColumnsMixedOrder,
				ENCODING);
	}

	private List<String> getBaseFileLinesMixedColumnsMixedLines() {
		return CollectionsUtil.createList("44\t42\t43\t45\t41", "31\t32\t33\t34\t35", "12\t11\t13\t14\t15",
				"21\t22\t24\t23\t25");
	}

	private void initializeBaseFileLinesInOrderColumnsMixed() throws IOException {
		baseFileLinesInOrderColumnsMixedOrder = folder.newFile("base.order.mixed.utf8");
		FileWriterUtil.printLines(getBaseFileLinesInOrderColumnsMixedLines(), baseFileLinesInOrderColumnsMixedOrder,
				ENCODING);
	}

	private List<String> getBaseFileLinesInOrderColumnsMixedLines() {
		return CollectionsUtil.createList("12\t11\t13\t14\t15", "21\t22\t24\t23\t25", "31\t32\t33\t34\t35",
				"44\t42\t43\t45\t41");
	}

	private void initializeBaseFileLinesMixedColumnsInOrder() throws IOException {
		baseFileLinesMixedOrderColumnsInOrder = folder.newFile("base.mixed.order.utf8");
		FileWriterUtil.printLines(getBaseFileLinesMixedColumnsInOrderLines(), baseFileLinesMixedOrderColumnsInOrder,
				ENCODING);
	}

	private List<String> getBaseFileLinesMixedColumnsInOrderLines() {
		return CollectionsUtil.createList("21\t22\t23\t24\t25", "11\t12\t13\t14\t15", "41\t42\t43\t44\t45",
				"31\t32\t33\t34\t35");
	}

	private void initializeBaseFileMissingLine() throws IOException {
		baseFileMissingLine = folder.newFile("base.missing.utf8");
		ArrayList<String> lines = new ArrayList<String>(getBaseFileLines());
		lines.remove(lines.size() - 1);
		FileWriterUtil.printLines(lines, baseFileMissingLine, ENCODING);
	}

	@Test
	public void testFileComparison_basefiles() throws Exception {
		for (String line : getBaseFileLines())
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFile, ENCODING, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFile, ENCODING, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.AS_IN_FILE, ColumnOrder.ANY_ORDER));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFile, ENCODING, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFile, ENCODING, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.ANY_ORDER, ColumnOrder.ANY_ORDER));
	}

	@Test
	public void testFileComparison_basefilesLinesMixedColumnsInOrder() throws Exception {
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsInOrder, ENCODING,
				getBaseFileLines(), COLUMN_DELIMITER_REGEX, LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsInOrder, ENCODING,
				getBaseFileLines(), COLUMN_DELIMITER_REGEX, LineOrder.AS_IN_FILE, ColumnOrder.ANY_ORDER));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsInOrder, ENCODING,
				getBaseFileLines(), COLUMN_DELIMITER_REGEX, LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsInOrder, ENCODING,
				getBaseFileLines(), COLUMN_DELIMITER_REGEX, LineOrder.ANY_ORDER, ColumnOrder.ANY_ORDER));
	}

	@Test
	public void testFileComparison_basefilesLinesMixedColumnsMixed() throws Exception {
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsMixedOrder, ENCODING,
				getBaseFileLines(), COLUMN_DELIMITER_REGEX, LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsMixedOrder, ENCODING,
				getBaseFileLines(), COLUMN_DELIMITER_REGEX, LineOrder.AS_IN_FILE, ColumnOrder.ANY_ORDER));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsMixedOrder, ENCODING,
				getBaseFileLines(), COLUMN_DELIMITER_REGEX, LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsMixedOrder, ENCODING,
				getBaseFileLines(), COLUMN_DELIMITER_REGEX, LineOrder.ANY_ORDER, ColumnOrder.ANY_ORDER));
	}

	@Test
	public void testFileComparison_basefilesLinesInOrderColumnsMixed() throws Exception {
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesInOrderColumnsMixedOrder, ENCODING,
				getBaseFileLines(), COLUMN_DELIMITER_REGEX, LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFileLinesInOrderColumnsMixedOrder, ENCODING,
				getBaseFileLines(), COLUMN_DELIMITER_REGEX, LineOrder.AS_IN_FILE, ColumnOrder.ANY_ORDER));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesInOrderColumnsMixedOrder, ENCODING,
				getBaseFileLines(), COLUMN_DELIMITER_REGEX, LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFileLinesInOrderColumnsMixedOrder, ENCODING,
				getBaseFileLines(), COLUMN_DELIMITER_REGEX, LineOrder.ANY_ORDER, ColumnOrder.ANY_ORDER));
	}

	@Test
	public void testFileComparison_basefilesMissingLine() throws Exception {
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileMissingLine, ENCODING, getBaseFileLines(),
				COLUMN_DELIMITER_REGEX, LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileMissingLine, ENCODING, getBaseFileLines(),
				COLUMN_DELIMITER_REGEX, LineOrder.AS_IN_FILE, ColumnOrder.ANY_ORDER));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileMissingLine, ENCODING, getBaseFileLines(),
				COLUMN_DELIMITER_REGEX, LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileMissingLine, ENCODING, getBaseFileLines(),
				COLUMN_DELIMITER_REGEX, LineOrder.ANY_ORDER, ColumnOrder.ANY_ORDER));
	}
	
	@Test
	public void testMd5Comparison() {
		FileComparisonUtil.createMd5ChecksumFile(baseFile);
		assertTrue(FileComparisonUtil.fileHasExpectedMd5Checksum(baseFile));
		assertTrue(FileComparisonUtil.fileHasExpectedMd5Checksum(baseFile, new File(baseFile.getAbsolutePath() + ".md5")));
	}

}
