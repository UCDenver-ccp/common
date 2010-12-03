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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.reader.BufferedRafReader;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class BufferedRafReaderTest extends DefaultTestCase {

	private File asciiOnlyFile;
	private File utf8File;
	private String asciiOnlyString = "This file contains only ASCII characters. 12345.";
	private String utf8String = "This file contains a UTF-8 character \u00df (beta).";

	@Before
	public void setUp() throws Exception {
		utf8File = folder.newFile("text.utf8");
		FileWriterUtil.printLines(CollectionsUtil.createList(utf8String), utf8File, CharacterEncoding.UTF_8);
		asciiOnlyFile = folder.newFile("text.ascii");
		FileWriterUtil.printLines(CollectionsUtil.createList(asciiOnlyString), asciiOnlyFile, CharacterEncoding.US_ASCII);

	}

	@Test
	public void testAsciiFileRead() throws Exception {
		BufferedRafReader reader = new BufferedRafReader(asciiOnlyFile, CharacterEncoding.US_ASCII);
		String line = reader.readBufferedLine();
		assertEquals(String.format("Line should match expected ASCII-only line."), asciiOnlyString, line);
	}

	@Test
	public void testUtf8FileRead() throws Exception {
		BufferedRafReader reader = new BufferedRafReader(utf8File, CharacterEncoding.UTF_8);
		String line = reader.readBufferedLine();
		assertEquals(String.format("Line should match expected line containing utf-8 characters."), utf8String, line);
	}

}
