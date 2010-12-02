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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;

public class FileWriterUtil {
	private static final Logger logger = Logger.getLogger(FileWriterUtil.class);

	/**
	 * Creates a BufferedWriter that validates the encoding of the characters it outputs If the
	 * input file does not have an encoding-specific file name, the file name is altered and an
	 * warning is logged.
	 * 
	 * @param outputFile
	 * @param encoding
	 * @return
	 * @throws FileNotFoundException
	 */
	public static BufferedWriter initBufferedWriter(File outputFile, CharacterEncoding encoding)
			throws FileNotFoundException {
		if (!CharacterEncoding.hasEncodingSpecificFileName(outputFile, encoding)) {
			String oldPath = outputFile.getAbsolutePath();
			outputFile = CharacterEncoding.getEncodingSpecificFile(outputFile, encoding);
			logger.warn(String.format(
					"File name has been adjusted to reflect character encoding used. '%s' was changed to '%s'",
					oldPath, outputFile.getName()));
		}
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), encoding.getEncoder()));
	}

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

	public static PrintStream openPrintStream(File outputFile, String encoding, boolean append)
			throws UnsupportedEncodingException, FileNotFoundException {
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
