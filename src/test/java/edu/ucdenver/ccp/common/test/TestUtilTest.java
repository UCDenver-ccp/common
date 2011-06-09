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

package edu.ucdenver.ccp.common.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.common.test.TestUtil;

public class TestUtilTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testPopulateTestFile() throws Exception {
		List<String> lines = CollectionsUtil.createList("Line 1", "Line 2", "Line 3");
		File testFile = TestUtil.populateTestFile(folder, "test-file", lines, CharacterEncoding.UTF_8);
		assertTrue(String.format("Test file should exist."), testFile.exists());
		assertEquals(String.format("Lines in file should be as expected."), lines, FileReaderUtil
				.loadLinesFromFile(testFile, CharacterEncoding.UTF_8));
	}
}
