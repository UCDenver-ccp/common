package edu.ucdenver.ccp.common.file.reader;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
import edu.ucdenver.ccp.common.file.SampleUtf8File;
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
	 * Encoding to use when generating sample files to be used by tests in this class
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
		StreamLineIterator fli = new StreamLineIterator(simpleFile, CharacterEncoding.UTF_8);
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
		StreamLineIterator fli = new StreamLineIterator(simpleFile, CharacterEncoding.UTF_8);
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
		InputStream sampleFileStream = ClassPathUtil.getResourceStreamFromClasspath(SampleUtf8File.class, SampleUtf8File.FILE_NAME);
		StreamLineIterator sli = new StreamLineIterator(sampleFileStream, SampleUtf8File.ENCODING, null);
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
		InputStream sampleFileStream = ClassPathUtil.getResourceStreamFromClasspath(SampleUtf8File.class, SampleUtf8File.FILE_NAME);
		BufferedReader br = FileReaderUtil.initBufferedReader(sampleFileStream, SampleUtf8File.ENCODING);
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
	 * Checks that the code point offsets are valid when reading a line with CRLF line breaks
	 * 
	 * @throws IOException
	 *             if an error occurs while reading the file
	 */
	@Test
	public void testLineIteratorOnFileWithCRLFLineTerminators_checkCodePointOffsets() throws IOException {
		InputStream sampleFileStream = ClassPathUtil.getResourceStreamFromClasspath(SampleUtf8File.class,SampleUtf8File.FILE_NAME);
		StreamLineIterator sli = new StreamLineIterator(sampleFileStream, SampleUtf8File.ENCODING, null);
		assertTrue(sli.hasNext());
		Line nextLine = sli.next();
		assertEquals("Code point offset for first line is incorrect.", SampleUtf8File.LINE1.getCodePointOffset(),
				nextLine.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 2 is incorrect.", SampleUtf8File.LINE2.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 3 is incorrect.", SampleUtf8File.LINE3.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 4 is incorrect.", SampleUtf8File.LINE4.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 5 is incorrect.", SampleUtf8File.LINE5.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 6 is incorrect.", SampleUtf8File.LINE6.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 7 is incorrect.", SampleUtf8File.LINE7.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 8 is incorrect.", SampleUtf8File.LINE8.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 9 is incorrect.", SampleUtf8File.LINE9.getCodePointOffset(), nextLine
				.getCodePointOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Code point offset for line 10 is incorrect.", SampleUtf8File.LINE10.getCodePointOffset(),
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
		InputStream sampleFileStream = ClassPathUtil.getResourceStreamFromClasspath(SampleUtf8File.class, SampleUtf8File.FILE_NAME);
		StreamLineIterator sli = new StreamLineIterator(sampleFileStream, SampleUtf8File.ENCODING, null);
		assertTrue(sli.hasNext());
		Line nextLine = sli.next();
		assertEquals("Character offset for first line is incorrect.", SampleUtf8File.LINE1.getCharacterOffset(),
				nextLine.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 2 is incorrect.", SampleUtf8File.LINE2.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 3 is incorrect.", SampleUtf8File.LINE3.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 4 is incorrect.", SampleUtf8File.LINE4.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 5 is incorrect.", SampleUtf8File.LINE5.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 6 is incorrect.", SampleUtf8File.LINE6.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 7 is incorrect.", SampleUtf8File.LINE7.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 8 is incorrect.", SampleUtf8File.LINE8.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 9 is incorrect.", SampleUtf8File.LINE9.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertTrue(sli.hasNext());
		nextLine = sli.next();
		assertEquals("Character offset for line 10 is incorrect.", SampleUtf8File.LINE10.getCharacterOffset(), nextLine
				.getCharacterOffset());
		assertFalse(sli.hasNext());
	}

}
