package edu.ucdenver.ccp.util.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.ucdenver.ccp.util.collections.CollectionsUtil;


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
		List<String> lines = CollectionsUtil.createList("line 1","line 2","line 3", "line 4");
		FileWriterUtil.printLines(lines, ps);
		ps.close();
		List<String> linesWritten = FileLoaderUtil.loadLinesFromFile(outputFile);
		assertEquals(String.format("Lines read from the output file should equal the lines written to it."), linesWritten, lines);
	}
	
}
