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
