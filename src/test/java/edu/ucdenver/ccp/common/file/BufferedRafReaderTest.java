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
		utf8File = folder.newFile("utf8.txt");
		byte[] utf8Bytes = utf8String.getBytes("UTF-8");
		FileOutputStream fos = new FileOutputStream(utf8File);
		fos.write(utf8Bytes);
		fos.close();

		asciiOnlyFile = folder.newFile("asciss.txt");
		FileWriterUtil.printLines(CollectionsUtil.createList(asciiOnlyString), asciiOnlyFile);

		FileUtil.copy(utf8File, new File("/tmp/utf8.txt"));
		FileUtil.copy(asciiOnlyFile, new File("/tmp/ascii.txt"));

	}

	@Test
	public void testAsciiFileRead() throws Exception {
		BufferedRafReader reader = new BufferedRafReader(asciiOnlyFile, System.getProperty("file.encoding"));
		String line = reader.readBufferedLine();
		assertEquals(String.format("Line should match expected ASCII-only line."), asciiOnlyString, line);
	}

	@Test
	public void testUtf8FileRead() throws Exception {
		BufferedRafReader reader = new BufferedRafReader(utf8File, "UTF-8");
		String line = reader.readBufferedLine();
		assertEquals(String.format("Line should match expected line containing utf-8 characters."), utf8String, line);
	}

}
