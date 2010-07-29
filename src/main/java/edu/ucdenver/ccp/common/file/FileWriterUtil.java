package edu.ucdenver.ccp.common.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
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

	
	public static PrintStream openPrintStream(File outputFile, String encoding, boolean append) throws UnsupportedEncodingException, FileNotFoundException {
		boolean autoflush = true;
		return new PrintStream(new FileOutputStream(outputFile, append), autoflush, encoding);
	}
	
	
	public static void closePrintStream(PrintStream ps, File outputFile) throws IOException {
		try {
			if (ps.checkError())
				throw new IOException(String.format("Error writing to file: %s", outputFile.getAbsolutePath()));
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
	}
	
}
