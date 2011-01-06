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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.reader.StreamLineIterator;
import edu.ucdenver.ccp.common.file.reader.LineReader.Line;
import edu.ucdenver.ccp.common.string.StringUtil;

public class FileReaderUtil {

	private FileReaderUtil() {
		// do not instantiate
	}

	/**
	 * Returns a BufferedReader initialized to read the input character encoding from the input File
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 * @throws FileNotFoundException
	 */
	public static BufferedReader initBufferedReader(File file, CharacterEncoding encoding) throws FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding.getDecoder()));
	}

	/**
	 * Returns a BufferedReader initialized to read the input character encoding from the specified
	 * InputStream
	 * 
	 * @param inputStream
	 * @param encoding
	 * @return
	 */
	public static BufferedReader initBufferedReader(InputStream inputStream, CharacterEncoding encoding) {
		return new BufferedReader(new InputStreamReader(inputStream, encoding.getDecoder()));
	}

	/**
	 * Returns a List<String[]> containing the column values for the input file. One String[] per
	 * line of the file. If you want the entire line, set the delimiter to be null. If there is a
	 * delimiter specified, you must specify at least one column index to return. TODO: track the
	 * number of columns that are returned. If there is a line with a different number of columns
	 * than expected, throw an exception.
	 * 
	 * @param inputFile
	 * @param encoding
	 * @param delimiter
	 * @param commentIndicator
	 * @param columnIndexes
	 * @return
	 * @throws IOException
	 * @throws ArrayIndexOutOfBoundsException
	 *             - if a requested column index does not exist
	 * @throws IllegalArgumentException
	 *             - if a delimiter is specified, but no column indexes are requested
	 */
	public static List<String[]> loadColumnsFromDelimitedFile(File inputFile, CharacterEncoding encoding,
			String delimiter, String commentIndicator, int... columnIndexes) throws IOException,
			ArrayIndexOutOfBoundsException, IllegalArgumentException {
		if (delimiter != null && (columnIndexes == null || columnIndexes.length == 0)) {
			throw new IllegalArgumentException(String.format(
					"Cannot parse columns from line. A delimiter \"%s\" has been specified, "
							+ "however no columns have been requested. If you want the entire line, "
							+ "set the delimiter to be null.", delimiter));
		}
		List<String[]> outputColumns = new ArrayList<String[]>();
		for (StreamLineIterator lineIter = new StreamLineIterator(inputFile, encoding, commentIndicator); lineIter
				.hasNext();) {
			Line line = lineIter.next();
			outputColumns.add(getColumnsFromLine(line.getText(), delimiter, columnIndexes));
		}
		return outputColumns;
	}

	/**
	 * Parses the input line and returns a String[] containing the requested columns of that line.
	 * If the delimiter is null or if there are no column indexes specified, the entire line is
	 * returned.
	 * 
	 * @param line
	 * @param delimiter
	 * @param columnIndexes
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public static String[] getColumnsFromLine(String line, String delimiterRegex, int... columnIndexes)
			throws ArrayIndexOutOfBoundsException {
		return getColumnsFromLine(line, delimiterRegex, null, columnIndexes);
	}

	public static String[] getColumnsFromLine(String line, String delimiterRegex, String fieldEnclosingRegex,
			int... columnIndexes) {
		if (delimiterRegex == null) {
			return new String[] { line };
		}
		
		String[] lineTokens = StringUtil.splitWithFieldEnclosure(line, delimiterRegex, fieldEnclosingRegex);
		if (columnIndexes == null || columnIndexes.length == 0) {
			columnIndexes = CollectionsUtil.createZeroBasedSequence(lineTokens.length);
		}
		String[] outputColumns = new String[columnIndexes.length];
		int outputIndex = 0;
		for (int columnIndex : columnIndexes) {
			ensureColumnIndexIsValid(columnIndex, line, lineTokens);
			outputColumns[outputIndex++] = lineTokens[columnIndex];
		}
		return outputColumns;
	}

	/**
	 * Returns a List<String> containing the contents of the requested column in the input file,
	 * assuming the file contains columns delimited by the delimiter String.
	 * 
	 * @param inputFile
	 * @param delimiter
	 * @param columnIndex
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadColumnFromDelimitedFile(File inputFile, CharacterEncoding encoding,
			String delimiter, String commentIndicator, int columnIndex) throws IOException {
		List<String[]> singleColumnAsArrayList = loadColumnsFromDelimitedFile(inputFile, encoding, delimiter,
				commentIndicator, columnIndex);
		List<String> outputList = new ArrayList<String>(singleColumnAsArrayList.size());
		while (!singleColumnAsArrayList.isEmpty()) {
			outputList.add(singleColumnAsArrayList.get(0)[0]);
			singleColumnAsArrayList.remove(0);
		}
		return outputList;
	}

	/**
	 * Returns a List<String> containing the lines loaded from the input File
	 * 
	 * @param inputFile
	 * @param commentIndicator
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadLinesFromFile(File inputFile, CharacterEncoding encoding, String commentIndicator)
			throws IOException {
		return loadColumnFromDelimitedFile(inputFile, encoding, null, commentIndicator, 0);
	}

	/**
	 * 
	 * @param inputFile
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadLinesFromFile(File inputFile, CharacterEncoding encoding) throws IOException {
		return loadColumnFromDelimitedFile(inputFile, encoding, null, null, 0);
	}

	/**
	 * Returns true if the input String[] contains a column for the input column Index, false
	 * otherwise.
	 * 
	 * @param columnIndex
	 * @param lineTokens
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private static boolean isColumnIndexValid(int columnIndex, String[] lineTokens) {
		return (columnIndex > -1 && columnIndex < lineTokens.length);
	}

	/**
	 * Checks that the columnIndex index exists in the input String[]. If the index does not exist,
	 * an exception is thrown.
	 * 
	 * @param columnIndex
	 * @param line
	 * @param lineTokens
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private static void ensureColumnIndexIsValid(int columnIndex, String line, String[] lineTokens)
			throws ArrayIndexOutOfBoundsException {
		if (!isColumnIndexValid(columnIndex, lineTokens)) {
			throw new ArrayIndexOutOfBoundsException(String.format(
					"Column index %d does not exist on line. There are only %d columns on line: %s", columnIndex,
					lineTokens.length, line));
		}
	}

}
