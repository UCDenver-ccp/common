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
