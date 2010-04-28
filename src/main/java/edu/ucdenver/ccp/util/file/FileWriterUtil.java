package edu.ucdenver.ccp.util.file;

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
	public static void printLines(List<String> lines, PrintStream ps) {
		for (String line : lines) {
			ps.println(line);
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
	public static void printLines(List<String> lines, File file) throws FileNotFoundException {
		PrintStream ps = new PrintStream(file);
		printLines(lines, ps);
		ps.close();
	}

}
