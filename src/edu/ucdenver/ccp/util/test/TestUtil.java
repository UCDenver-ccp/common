package edu.ucdenver.ccp.util.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.junit.rules.TemporaryFolder;

/**
 * A collection of utility methods useful when writing unit tests.
 * 
 * @author Bill Baumgartner
 * 
 */
public class TestUtil {

	/**
	 * Creates a file in the provided TemporaryFolder and populates it with the specified lines.
	 * 
	 * @param folder
	 * @param fileName
	 * @param lines
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static File populateTestFile(TemporaryFolder folder, String fileName, List<String> lines)
			throws FileNotFoundException, IOException {
		File file = folder.newFile(fileName);
		PrintStream ps = new PrintStream(file);
		for (String line : lines) {
			ps.println(line);
		}
		ps.close();
		return file;
	}

}
