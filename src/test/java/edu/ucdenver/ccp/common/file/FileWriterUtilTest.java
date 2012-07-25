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

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.UnmappableCharacterException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class FileWriterUtilTest extends DefaultTestCase {

	private BufferedWriter writer;
	private File outputFile;

	@Before
	public void setUp() throws Exception {
		outputFile = folder.newFile("outputFile.ascii");
		writer = FileWriterUtil.initBufferedWriter(outputFile, CharacterEncoding.US_ASCII);
	}

	@Test
	public void testPrintLines() throws Exception {
		List<String> lines = CollectionsUtil.createList("line 1", "line 2", "line 3", "line 4");
		FileWriterUtil.printLines(lines, writer);
		writer.close();
		List<String> linesWritten = FileReaderUtil.loadLinesFromFile(outputFile, CharacterEncoding.US_ASCII);
		assertEquals(String.format("Lines read from the output file should equal the lines written to it."),
				lines, linesWritten);
	}

	@Test
	public void testPrintLinesToFile() throws Exception {
		File testFile = folder.newFile("test.ascii");
		List<String> expectedLines = CollectionsUtil.createList("line1", "line2", "line3");
		FileWriterUtil.printLines(expectedLines, testFile, CharacterEncoding.US_ASCII);
		List<String> lines = FileReaderUtil.loadLinesFromFile(testFile, CharacterEncoding.US_ASCII);
		assertEquals(String.format("Should have the 3 expected lines in the file."), expectedLines, lines);
	}
	
	@Test
	public void testBufferedWriterCreation() throws Exception {
		CharacterEncoding encoding = CharacterEncoding.US_ASCII;
		outputFile = CharacterEncoding.getEncodingSpecificFile(outputFile, encoding);
		BufferedWriter writer = FileWriterUtil.initBufferedWriter(outputFile, encoding);
		String line1 = "line 1";
		String line2 = "line 2";
		writer.write(line1);
		writer.newLine();
		writer.write(line2);
		writer.newLine();
		writer.close();
		List<String> lines = CollectionsUtil.createList(line1, line2);
		List<String> linesWritten = FileReaderUtil.loadLinesFromFile(outputFile, encoding);
		assertEquals(String.format("Lines read from the output file should equal the lines written to it."),
				lines, linesWritten);
	}
	
	
	@Test(expected=UnmappableCharacterException.class)
	public void testBufferedWriter_throwsExceptionIfEncodingConflict() throws Exception {
		CharacterEncoding encoding = CharacterEncoding.US_ASCII;
		outputFile = CharacterEncoding.getEncodingSpecificFile(outputFile, encoding);
		BufferedWriter writer = FileWriterUtil.initBufferedWriter(outputFile, encoding);
		writer.write("\u0327");
		writer.newLine();
		writer.close();
	}
	
	@Test
	public void testBufferedWriter_withUtf8() throws Exception {
		CharacterEncoding encoding = CharacterEncoding.UTF_8;
		File outputFile = CharacterEncoding.getEncodingSpecificFile(folder.newFile("text.utf8"), encoding);
		BufferedWriter writer = FileWriterUtil.initBufferedWriter(outputFile, encoding);
		String line1 = "fa\u0327ade";
		String line2 = "nai\u0308ve";
		writer.write(line1);
		writer.newLine();
		writer.write(line2);
		writer.newLine();
		writer.close();
		List<String> lines = CollectionsUtil.createList(line1, line2);
		List<String> linesWritten = FileReaderUtil.loadLinesFromFile(outputFile, encoding);
		
		assertEquals(String.format("Lines read from the output file should equal the lines written to it."),
				lines, linesWritten);
		
	}
	
}
