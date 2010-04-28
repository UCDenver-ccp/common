package edu.ucdenver.ccp.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.ucdenver.ccp.util.collections.CollectionsUtil;
import edu.ucdenver.ccp.util.file.FileLoaderUtil;

public class TestUtilTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testPopulateTestFile() throws Exception {
		List<String> lines = CollectionsUtil.createList("Line 1", "Line 2", "Line 3");
		File testFile = TestUtil.populateTestFile(folder, "test-file", lines);
		assertTrue(String.format("Test file should exist."), testFile.exists());
		assertEquals(String.format("Lines in file should be as expected."), lines, FileLoaderUtil
				.loadLinesFromFile(testFile));
	}
}
