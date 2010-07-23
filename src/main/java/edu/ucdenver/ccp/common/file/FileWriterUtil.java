package edu.ucdenver.ccp.common.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

public class FileWriterUtil {

	/**
	 * Prints the input list of lines to the input PrintStream
	 * 
	 * @param lines
	 * @param ps
	 */
	public static void printLines(List<?> lines, PrintStream ps) {
		for (Object line : lines) {
			ps.println(line.toString());
		}
	}

	/**
	 * Prints the input list of lines to the specified file. The file is overwritten with the input
	 * lines.
	 * 
	 * @param lines
	 * @param file
	 * @throws FileNotFoundException
	 */
	public static void printLines(List<?> lines, File file) throws FileNotFoundException {
		PrintStream ps = new PrintStream(file);
		printLines(lines, ps);
		ps.close();
	}

}
