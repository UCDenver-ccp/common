package edu.ucdenver.ccp.common.file.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.reader.Line.LineTerminator;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class FileLineReaderTest extends DefaultTestCase {

	/**
	 * Character encoding to use when creating/reading sample files in this test
	 */
	private static final CharacterEncoding ENCODING = CharacterEncoding.UTF_8;

	/**
	 * String to use to indicate a line-to-be-skipped in the sample files used in this test
	 */
	private static final String SKIP_LINE_PREFIX = StringConstants.POUND_SIGN;

	/**
	 * Tests that the FileLine line number gets set properly when some lines are skipped.
	 * 
	 * @throws IOException
	 *             if an error occurs reading or writing the sample file
	 */
	@Test
	public void testLineNumbersWhenSkippingLines() throws IOException {
		File sampleFile = populateSampleFile();
		FileLineReader flr = new FileLineReader(sampleFile, ENCODING, SKIP_LINE_PREFIX);
		FileLine line = flr.readLine();
		assertEquals("first line should have been skipped, so this line should be line number 1 (zero offset)", 1, line
				.getLineNumber());
		line = flr.readLine();
		assertEquals("third line should have been skipped, so this line should be line number 3 (zero offset)", 3, line
				.getLineNumber());
		line = flr.readLine();
		assertEquals("this line should be line number 4 (zero offset)", 4, line.getLineNumber());
		assertNull(flr.readLine());
	}

	/**
	 * Tests that the correct line terminators are returned
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetLineTerminator() throws IOException {
		File sampleFile = populateSampleFile();
		FileLineReader flr = new FileLineReader(sampleFile, ENCODING, SKIP_LINE_PREFIX);
		FileLine line = flr.readLine();
		assertEquals("Terminator on first line returned should be LF", LineTerminator.LF, line.getLineTerminator());
		line = flr.readLine();
		assertEquals("Terminator on second line returned should be CRLF", LineTerminator.CRLF, line.getLineTerminator());
		line = flr.readLine();
		assertEquals("Terminator on third line returned should be LF", LineTerminator.LF, line.getLineTerminator());
	}

	/**
	 * @return a sample file containing 5 lines, lines 1 and 3 are commented out
	 * @throws IOException
	 *             if an error occurs while creating the sample file
	 */
	private File populateSampleFile() throws IOException {
		List<String> lines = CollectionsUtil.createList(SKIP_LINE_PREFIX + "line1", "line2", SKIP_LINE_PREFIX
				+ "line3\r", "line4\r", "line5");
		File file = folder.newFile("sample.utf8");
		FileWriterUtil.printLines(lines, file, ENCODING);
		return file;
	}

}
