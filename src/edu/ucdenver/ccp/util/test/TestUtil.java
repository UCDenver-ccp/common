package edu.ucdenver.ccp.util.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.junit.internal.ArrayComparisonFailure;
import org.junit.rules.TemporaryFolder;

import edu.ucdenver.ccp.util.string.RegExPatterns;

/**
 * A collection of utility methods useful when writing unit tests.
 * 
 * @author Bill Baumgartner
 * 
 */
public class TestUtil {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(TestUtil.class);

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
