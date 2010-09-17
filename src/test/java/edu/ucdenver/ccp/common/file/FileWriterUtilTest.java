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

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.FileLoaderUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil;

public class FileWriterUtilTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private PrintStream ps;
	private File outputFile;

	@Before
	public void setUp() throws Exception {
		outputFile = folder.newFile("outputFile.txt");
		ps = new PrintStream(outputFile);
	}

	@Test
	public void testPrintLines() throws Exception {
		List<String> lines = CollectionsUtil.createList("line 1", "line 2", "line 3", "line 4");
		FileWriterUtil.printLines(lines, ps);
		ps.close();
		List<String> linesWritten = FileLoaderUtil.loadLinesFromFile(outputFile);
		assertEquals(String.format("Lines read from the output file should equal the lines written to it."),
				linesWritten, lines);
	}

	@Test
	public void testPrintLinesToFile() throws Exception {
		File testFile = folder.newFile("test.txt");
		List<String> expectedLines = CollectionsUtil.createList("line1", "line2", "line3");
		FileWriterUtil.printLines(expectedLines, testFile);
		List<String> lines = FileLoaderUtil.loadLinesFromFile(testFile);
		assertEquals(String.format("Should have the 3 expected lines in the file."), expectedLines, lines);
	}

}
