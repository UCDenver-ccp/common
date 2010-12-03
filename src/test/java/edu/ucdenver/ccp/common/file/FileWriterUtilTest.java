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

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.UnmappableCharacterException;
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
		List<String> linesWritten = FileLoaderUtil.loadLinesFromFile(outputFile, CharacterEncoding.US_ASCII);
		assertEquals(String.format("Lines read from the output file should equal the lines written to it."),
				lines, linesWritten);
	}

	@Test
	public void testPrintLinesToFile() throws Exception {
		File testFile = folder.newFile("test.ascii");
		List<String> expectedLines = CollectionsUtil.createList("line1", "line2", "line3");
		FileWriterUtil.printLines(expectedLines, testFile, CharacterEncoding.US_ASCII);
		List<String> lines = FileLoaderUtil.loadLinesFromFile(testFile, CharacterEncoding.US_ASCII);
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
		List<String> linesWritten = FileLoaderUtil.loadLinesFromFile(outputFile, encoding);
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
		List<String> linesWritten = FileLoaderUtil.loadLinesFromFile(outputFile, encoding);
		assertEquals(String.format("Lines read from the output file should equal the lines written to it."),
				lines, linesWritten);
	}
	

}
