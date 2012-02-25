/* Copyright (C) 2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP common library.
 * The CCP common library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
