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
		baseFile = folder.newFile("base");
		FileWriterUtil.printLines(getBaseFileLines(), baseFile);
	}

	private List<String> getBaseFileLines() {
		return CollectionsUtil.createList("11\t12\t13\t14\t15", "21\t22\t23\t24\t25", "31\t32\t33\t34\t35",
				"41\t42\t43\t44\t45");
	}

	private void initializeBaseFileLinesInOrderColumnsInOrder() throws IOException {
		baseFileLinesInOrderColumnsInOrder = folder.newFile("base.order.order");
		FileWriterUtil.printLines(getBaseFileLines(), baseFileLinesInOrderColumnsInOrder);
	}

	private void initializeBaseFileLinesMixedColumnsMixed() throws IOException {
		baseFileLinesMixedOrderColumnsMixedOrder = folder.newFile("base.mixed.mixed");
		FileWriterUtil.printLines(getBaseFileLinesMixedColumnsMixedLines(), baseFileLinesMixedOrderColumnsMixedOrder);
	}

	private List<String> getBaseFileLinesMixedColumnsMixedLines() {
		return CollectionsUtil.createList("44\t42\t43\t45\t41", "31\t32\t33\t34\t35", "12\t11\t13\t14\t15",
				"21\t22\t24\t23\t25");
	}

	private void initializeBaseFileLinesInOrderColumnsMixed() throws IOException {
		baseFileLinesInOrderColumnsMixedOrder = folder.newFile("base.order.mixed");
		FileWriterUtil.printLines(getBaseFileLinesInOrderColumnsMixedLines(), baseFileLinesInOrderColumnsMixedOrder);
	}

	private List<String> getBaseFileLinesInOrderColumnsMixedLines() {
		return CollectionsUtil.createList("12\t11\t13\t14\t15", "21\t22\t24\t23\t25", "31\t32\t33\t34\t35",
				"44\t42\t43\t45\t41");
	}

	private void initializeBaseFileLinesMixedColumnsInOrder() throws IOException {
		baseFileLinesMixedOrderColumnsInOrder = folder.newFile("base.mixed.order");
		FileWriterUtil.printLines(getBaseFileLinesMixedColumnsInOrderLines(), baseFileLinesMixedOrderColumnsInOrder);
	}

	private List<String> getBaseFileLinesMixedColumnsInOrderLines() {
		return CollectionsUtil.createList("21\t22\t23\t24\t25", "11\t12\t13\t14\t15", "41\t42\t43\t44\t45",
				"31\t32\t33\t34\t35");
	}

	private void initializeBaseFileMissingLine() throws IOException {
		baseFileMissingLine = folder.newFile("base.missing");
		ArrayList<String> lines = new ArrayList<String>(getBaseFileLines());
		lines.remove(lines.size() - 1);
		FileWriterUtil.printLines(lines, baseFileMissingLine);
	}

	@Test
	public void testFileComparison_basefiles() throws Exception {
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFile, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFile, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.AS_IN_FILE, ColumnOrder.ANY_ORDER));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFile, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFile, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.ANY_ORDER, ColumnOrder.ANY_ORDER));
	}

	@Test
	public void testFileComparison_basefilesLinesMixedColumnsInOrder() throws Exception {
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsInOrder, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsInOrder, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.AS_IN_FILE, ColumnOrder.ANY_ORDER));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsInOrder, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsInOrder, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.ANY_ORDER, ColumnOrder.ANY_ORDER));
	}
	
	@Test
	public void testFileComparison_basefilesLinesMixedColumnsMixed() throws Exception {
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsMixedOrder, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsMixedOrder, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.AS_IN_FILE, ColumnOrder.ANY_ORDER));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsMixedOrder, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFileLinesMixedOrderColumnsMixedOrder, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.ANY_ORDER, ColumnOrder.ANY_ORDER));
	}
	
	
	@Test
	public void testFileComparison_basefilesLinesInOrderColumnsMixed() throws Exception {
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesInOrderColumnsMixedOrder, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFileLinesInOrderColumnsMixedOrder, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.AS_IN_FILE, ColumnOrder.ANY_ORDER));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileLinesInOrderColumnsMixedOrder, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE));
		assertTrue(FileComparisonUtil.hasExpectedLines(baseFileLinesInOrderColumnsMixedOrder, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.ANY_ORDER, ColumnOrder.ANY_ORDER));
	}
	
	@Test
	public void testFileComparison_basefilesMissingLine() throws Exception {
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileMissingLine, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileMissingLine, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.AS_IN_FILE, ColumnOrder.ANY_ORDER));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileMissingLine, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.ANY_ORDER, ColumnOrder.AS_IN_FILE));
		assertFalse(FileComparisonUtil.hasExpectedLines(baseFileMissingLine, getBaseFileLines(), COLUMN_DELIMITER_REGEX,
				LineOrder.ANY_ORDER, ColumnOrder.ANY_ORDER));
	}
	
	
	
}
