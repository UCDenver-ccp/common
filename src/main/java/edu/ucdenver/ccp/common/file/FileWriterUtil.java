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
import java.util.Iterator;
import java.util.List;

public class FileWriterUtil {

	/**
	 * The WriteMode enum is closely tied to the boolean "append" argument that is found in the
	 * constructor of the FileOutputStream class. It can be used to control whether a file is
	 * overwritten or if new content is appended to what is already there. Use this enum in place of
	 * the boolean append parameter for more readable code.
	 * 
	 * @author Center for Computational Pharmacology; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum WriteMode {
		/**
		 * Use WriteMode.APPEND if you want to append content to the end of a file
		 */
		APPEND(true),
		/**
		 * Use WriteMode.OVERWRITE if you want to overwrite a file (and in the process delete any
		 * previous content)
		 */
		OVERWRITE(false);

		private final boolean append;

		private WriteMode(boolean append) {
			this.append = append;
		}

		public boolean append() {
			return append;
		}

	}

	/**
	 * The FileSuffixEnforcement enum is a proxy for a boolean that is used to specify whether
	 * character encoding-specific file suffixes must be used. See initBufferedWriter().
	 * 
	 * @author Center for Computational Pharmacology; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum FileSuffixEnforcement {
		ON, OFF
	}

	/**
	 * Creates a BufferedWriter that uses proper character encoding validation.
	 * 
	 * @param outputFile
	 * @param encoding
	 *            the CharacterEncoding to use when writing to the output file
	 * @param writeMode
	 *            WriteMode.APPEND to append to the output file, WriteMode.OVERWRITE to overwrite
	 *            the output file
	 * @param suffixEnforcement
	 *            if FileSuffixEnforcement.ON then the output file must have the appropriate
	 *            character encoding-specific file suffix to avoid an IllegalArgumentException. If
	 *            FileSuffixEnforcement.OFF then the file name suffix is not checked. (any suffix
	 *            will be permitted).
	 * @return
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 *             thrown if file suffix enforcement is active and the specified output file name
	 *             suffix does not match the expected character encoding-specific suffix
	 */
	public static BufferedWriter initBufferedWriter(File outputFile, CharacterEncoding encoding, WriteMode writeMode,
			FileSuffixEnforcement suffixEnforcement) throws FileNotFoundException {
		if (suffixEnforcement.equals(FileSuffixEnforcement.ON))
			if (!CharacterEncoding.hasEncodingSpecificFileName(outputFile, encoding)) {
				String errorMessage = String
						.format(
								"Illegal file name detected. File suffix enforcement is active and the file name suffix "
										+ "for file: %s does not match the expected character encoding-specific suffix (%s). Use "
										+ "CharacterEncoding.getEncodingSpecificFile(file, encoding) to ensure a valid file name "
										+ "when file suffix enforcement is active. Full file path:%s", outputFile
										.getName(), encoding.getFileSuffix(), outputFile.getAbsolutePath());
				throw new IllegalArgumentException(errorMessage);
			}
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, writeMode.append()), encoding
				.getEncoder()));
	}

	/**
	 * Creates a BufferedWriter that uses proper character encoding validation. By default this
	 * BufferedWriter will overwrite the specified output file and character encoding-specific file
	 * suffix enforcement is active.
	 * 
	 * @param outputFile
	 * @param encoding
	 *            the CharacterEncoding to use when writing to the output file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 *             thrown if file suffix enforcement is active and the specified output file name
	 *             suffix does not match the expected character encoding-specific suffix
	 */
	public static BufferedWriter initBufferedWriter(File outputFile, CharacterEncoding encoding)
			throws FileNotFoundException {
		return initBufferedWriter(outputFile, encoding, WriteMode.OVERWRITE, FileSuffixEnforcement.ON);
	}

	/**
	 * Prints the input list of lines to the input PrintStream
	 * 
	 * @param lines
	 * @param ps
	 * @throws IOException
	 */
	public static void printLines(List<?> lines, BufferedWriter writer) throws IOException {
		for (Object line : lines) {
			writer.write(line.toString());
			writer.newLine();
		}
	}

	/**
	 * Prints the input list of lines to the specified file. The file is overwritten with the input
	 * lines.
	 * 
	 * @param lines
	 * @param file
	 * @throws IOException
	 */
	public static void printLines(List<?> lines, File file, CharacterEncoding encoding) throws IOException {
		BufferedWriter writer = initBufferedWriter(file, encoding);
		printLines(lines, writer);
		writer.close();
	}

	/**
	 * Prints the input list of lines to the specified file.
	 * 
	 * @param lines
	 * @param file
	 * @throws IOException
	 */
	public static void printLines(List<?> lines, File file, CharacterEncoding encoding, WriteMode writeMode,
			FileSuffixEnforcement suffixEnforementPolicy) throws IOException {
		BufferedWriter writer = initBufferedWriter(file, encoding, writeMode, suffixEnforementPolicy);
		printLines(lines, writer);
		writer.close();
	}
	
	/**
	 * Prints the contents of the input Iterator to the specified line.
	 * @param lineIterator
	 * @param file
	 * @param encoding
	 * @throws IOException
	 */
	public static void printLines(Iterator<?> lineIterator, File file, CharacterEncoding encoding) throws IOException {
		BufferedWriter writer = initBufferedWriter(file, encoding);
		while (lineIterator.hasNext()) {
			writer.write(lineIterator.next().toString());
			writer.newLine();
		}
		writer.close();
	}

}
