package edu.ucdenver.ccp.common.file.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.reader.Line.LineTerminator;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

/**
 * Tests the LineIterator over a simple file and a complex UTF-8 file
 * 
 * @author bill
 * 
 */
public class LineIteratorTest extends DefaultTestCase {

	/**
	 * Name of a sample UTF-8 file with CRLF line breaks. File is on the classpath in the same
	 * package as this test class.
	 */
	private static final String SAMPLE_FILE_NAME = "astral-combine-CRLF.utf8";

	/**
	 * Sample file uses UTF_8 character encoding
	 */
	private static final CharacterEncoding SAMPLE_ENCODING = CharacterEncoding.UTF_8;

	/**
	 * Tests that the line iterator returns the currect number of lines on a simple file (no weird
	 * line break characters);
	 * 
	 * @throws IOException
	 *             if an error occurs while reading the sample file
	 */
	@Test
	public void testLineIteratorOnSimpleFile() throws IOException {
		File simpleFile = createSimpleSampleFile();
		FileLineIterator fli = new FileLineIterator(simpleFile, CharacterEncoding.UTF_8);
		long lineNumber = -1;
		while (fli.hasNext()) {
			Line nextLine = fli.next();
			lineNumber = nextLine.getLineNumber();
		}
		assertEquals(String.format(
				"File should have 5 lines so line number should equal 4 (zero offset) but equals %d", lineNumber), 4,
				lineNumber);
	}

	/**
	 * Checks that the character offsets are expected for each line
	 * 
	 * @throws IOException
	 *             if an error occurs while reading the sample file
	 */
	@Test
	public void testLineIteratorOnSimpleFile_checkCharacterOffsets() throws IOException {
		File simpleFile = createSimpleSampleFile();
		FileLineIterator fli = new FileLineIterator(simpleFile, CharacterEncoding.UTF_8);
		assertTrue(fli.hasNext());
		Line nextLine = fli.next();
		assertEquals(String.format("Character offset for first line should be 0 but was %d", nextLine
				.getCharacterOffset()), 0, nextLine.getCharacterOffset());
		assertTrue(fli.hasNext());
		nextLine = fli.next();
		assertEquals(
				String.format("Character offset for line 2 should be 5 but was %d", nextLine.getCharacterOffset()), 6,
				nextLine.getCharacterOffset());
		assertTrue(fli.hasNext());
		nextLine = fli.next();
		assertEquals(String
				.format("Character offset for line 3 should be 12 but was %d", nextLine.getCharacterOffset()), 12,
				nextLine.getCharacterOffset());
		assertTrue(fli.hasNext());
		nextLine = fli.next();
		assertEquals(String
				.format("Character offset for line 4 should be 18 but was %d", nextLine.getCharacterOffset()), 18,
				nextLine.getCharacterOffset());
		assertTrue(fli.hasNext());
		nextLine = fli.next();
		assertEquals(String
				.format("Character offset for line 5 should be 24 but was %d", nextLine.getCharacterOffset()), 24,
				nextLine.getCharacterOffset());
		assertFalse(fli.hasNext());
	}

	/**
	 * Creates a simple sample file to use for testing the LineIterator
	 * 
	 * @return simple file with 5 lines.
	 * @throws IOException
	 *             if an error occurs while writing the simple sample file
	 */
	private File createSimpleSampleFile() throws IOException {
		List<String> lines = CollectionsUtil.createList("line1", "line2", "line3", "line4", "line5");
		File file = folder.newFile("simple.utf8");
		FileWriterUtil.printLines(lines, file, SAMPLE_ENCODING);
		return file;
	}

	/**
	 * Tests that the LineIterator returns the correct number of lines for the sample file
	 * 
	 * @throws IOException
	 *             if an error occurs
	 */
	@Test
	public void testLineIteratorOnFileWithCRLFLineTerminators() throws IOException {
		InputStream sampleFileStream = ClassPathUtil.getResourceStreamFromClasspath(getClass(), SAMPLE_FILE_NAME);
		StreamLineIterator sli = new StreamLineIterator(sampleFileStream, SAMPLE_ENCODING, null);
		long lineNumber = -1;
		while (sli.hasNext()) {
			Line nextLine = sli.next();
			lineNumber = nextLine.getLineNumber();
		}
		assertEquals(String.format(
				"File should have 10 lines so line number should equal 9 (zero offset) but equals %d", lineNumber), 9,
				lineNumber);
	}

	/**
	 * Similar to the test for LineIterator, this tests that {@link BufferedReader} also produces
	 * the expected number of lines.
	 * 
	 * @throws IOException
	 *             if an error occurs
	 */
	@Test
	public void testBufferedReaderOnFileWithCRLFLineTerminators() throws IOException {
		InputStream sampleFileStream = ClassPathUtil.getResourceStreamFromClasspath(getClass(), SAMPLE_FILE_NAME);
		BufferedReader br = FileReaderUtil.initBufferedReader(sampleFileStream, SAMPLE_ENCODING);
		try {
			@SuppressWarnings("unused")
			String line;
			int lineCount = 0;
			while ((line = br.readLine()) != null)
				lineCount++;
			assertEquals(String.format("File should have 10 lines but had %d", lineCount), 10, lineCount);
		} finally {
			br.close();
			sampleFileStream.close();
		}
	}

	/**
	 * Enum representing lines in the astral-combine-CRLF.utf8 file used in this test
	 * 
	 * @author bill
	 * 
	 */
	private enum SampleFileLine {
		/**
		 * Line 1: Line#1\r
		 */
		LINE1(6, 6, LineTerminator.CRLF),
		/**
		 * Line 2: \x{1D49C}\x{212C}\x{1D49E}\x{1D49F} is ABCD\r
		 */
		LINE2(12, 15, LineTerminator.CRLF),
		/**
		 * Line 3: let \x{1D49E}\x{304} = sqrt(\x{1D49C}\x{B2} + \x{212C}\x{B2})\r
		 */
		LINE3(22, 24, LineTerminator.CRLF),
		/**
		 * Line 4: let \x{1D4B1}\x{305} = 4/3 \x{B7} \x{3C0} \x{B7} \x{1D4C7}\x{B3}\r
		 */
		LINE4(21, 23, LineTerminator.CRLF),
		/**
		 * Line 5: let \x{1D4B1}\x{305} = 4\x{2044}3\x{2062}\x{3C0}\x{2062}\x{1D4C7}\x{B3}\r
		 */
		LINE5(17, 19, LineTerminator.CRLF),
		/**
		 * Line 6: let \x{2133}\x{304} = 60 * \x{1D4AE}\x{305}\r
		 */
		LINE6(16, 17, LineTerminator.CRLF),
		/**
		 * Line 7: let \x{210B} = 60 * \x{2133}\r
		 */
		LINE7(14, 14, LineTerminator.CRLF),
		/**
		 * Line 8: let \x{1D49F}\x{305} = 24 * \x{210B}\r
		 */
		LINE8(15, 16, LineTerminator.CRLF),
		/**
		 * Line 9: let \x{1D4B4} = 365.2425 * \x{210B}\r
		 */
		LINE9(20, 21, LineTerminator.CRLF),
		/**
		 * Line 10: Line#10\r
		 */
		LINE10(7, 7, LineTerminator.CRLF);

		/**
		 * The number of characters on the line (excluding the line terminator)
		 */
		private final int characterCount;

		/**
		 * The number of Unicode characters on the line (excluding the line terminator)
		 */
		private final int codePointCount;

		/**
		 * The line terminator used at the end of the line
		 */
		private final LineTerminator terminator;

		/**
		 * private constructor used by enum
		 * 
		 * @param characterCount
		 *            the number of characters on the line
		 * @param terminator
		 *            the line terminator used at the end of the line
		 */
		private SampleFileLine(int codePointCount, int characterCount, LineTerminator terminator) {
			this.codePointCount = codePointCount;
			this.characterCount = characterCount;
			this.terminator = terminator;
		}

		/**
		 * @return the length of this line in terms of characters (character count + line terminator
		 *         length)
		 */
		public int getCharacterLineLength() {
			return characterCount + terminator.length();
		}

		/**
		 * @return the length of this line in terms of code points
		 */
		public int getCodePointLineLength() {
			return codePointCount + terminator.length();
		}

		/**
		 * @return the character offset for this line (the count of all character that occur before
		 *         this line)
		 */
		public int getCharacterOffset() {
			int offset = 0;
			for (SampleFileLine line : SampleFileLine.values()) {
				if (this.equals(line))
					break;
				offset += line.getCharacterLineLength();
			}
			return offset;
		}

		/**
		 * @return the code point for this line (the count of all Unicode characters that occur
		 *         before this line)
		 */
		public int getCodePointOffset() {
			int offset = 0;
			for (SampleFileLine line : SampleFileLine.values()) {
				if (this.equals(line))
					break;
				offset += line.getCodePointLineLength();
			}
			return offset;
		}

	}

	/**
	 * Checks that the code point offsets are valid when reading a line with CRLF line breaks
	 * 
	 * @throws IOException
	 *             if an error occurs while reading the file
	 */
	@Test
	public void testLineIteratorOnFileWithCRLFLineTerminators_checkCodePointOffsets() throws IOException {
		InputStream sampleFileStream = ClassPathUtil.getResourceStreamFromClasspath(getClass(), SAMPLE_FILE_NAME);
		StreamLineIterator sli = new StreamLineIterator(sampleFileStream, SAMPLE_ENCODING, null);
		assertTrue(sli.hasNext());
		Line nextLine = sli.next();
		assertEquals("Code point offset for first line is incorrect.", SampleFileLine.LINE1.getCodePointOffset(),
				nextLine.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 2 is incorrect.", SampleFileLine.LINE2.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 3 is incorrect.", SampleFileLine.LINE3.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 4 is incorrect.", SampleFileLine.LINE4.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 5 is incorrect.", SampleFileLine.LINE5.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 6 is incorrect.", SampleFileLine.LINE6.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 7 is incorrect.", SampleFileLine.LINE7.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 8 is incorrect.", SampleFileLine.LINE8.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 9 is incorrect.", SampleFileLine.LINE9.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 10 is incorrect.", SampleFileLine.LINE10.getCodePointOffset(),
				nextLine.getCodePointOffset());
		assertFalse(sli.hasNext());
	}

	/**
	 * Checks that the character offsets are valid when reading a line with CRLF line breaks
	 * 
	 * @throws IOException
	 *             if an error occurs while reading the file
	 */
	@Test
	public void testLineIteratorOnFileWithCRLFLineTerminators_checkCharacterOffsets() throws IOException {
		InputStream sampleFileStream = ClassPathUtil.getResourceStreamFromClasspath(getClass(), SAMPLE_FILE_NAME);
		StreamLineIterator sli = new StreamLineIterator(sampleFileStream, SAMPLE_ENCODING, null);
		assertTrue(sli.hasNext());
		Line nextLine = sli.next();
		assertEquals("Character offset for first line is incorrect.", SampleFileLine.LINE1.getCharacterOffset(),
				nextLine.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 2 is incorrect.", SampleFileLine.LINE2.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 3 is incorrect.", SampleFileLine.LINE3.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 4 is incorrect.", SampleFileLine.LINE4.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 5 is incorrect.", SampleFileLine.LINE5.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 6 is incorrect.", SampleFileLine.LINE6.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 7 is incorrect.", SampleFileLine.LINE7.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 8 is incorrect.", SampleFileLine.LINE8.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 9 is incorrect.", SampleFileLine.LINE9.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 10 is incorrect.", SampleFileLine.LINE10.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertFalse(sli.hasNext());
	}

}
