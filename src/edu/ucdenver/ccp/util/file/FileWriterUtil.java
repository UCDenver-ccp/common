package edu.ucdenver.ccp.util.file;

import java.io.PrintStream;
import java.util.List;

public class FileWriterUtil {

	/**
	 * Prints the input list of lines to the input PrintStream
	 * 
	 * @param lines
	 * @param ps
	 */
	public static void printLines(List<String> lines, PrintStream ps) {
		for (String line : lines) {
			ps.println(line);
		}
	}

}
