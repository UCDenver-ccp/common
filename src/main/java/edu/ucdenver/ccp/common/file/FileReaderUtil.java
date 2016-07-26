package edu.ucdenver.ccp.common.file;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.reader.Line;
import edu.ucdenver.ccp.common.file.reader.StreamLineIterator;
import edu.ucdenver.ccp.common.string.StringUtil;

/**
 * Utility method helpful when reading the content of a file
 * 
 * @author bill
 * 
 */
public class FileReaderUtil {

	/**
	 * Private constructor; this class should not be instantiated
	 */
	/* @formatter:off */
	private FileReaderUtil() {/* do not instantiate */
	}
	/* @formatter:on */

	/**
	 * Returns a BufferedReader initialized to read the input character encoding from the input File
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 * @throws IOException 
	 */
	public static BufferedReader initBufferedReader(File file, CharacterEncoding encoding) throws IOException {
		if (file.getName().endsWith(".gz")) {
			return initBufferedReader(new GZIPInputStream(new FileInputStream(file)), encoding);
		}
		return initBufferedReader(new FileInputStream(file), encoding);
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
		return loadColumnsFromDelimitedFile(new FileInputStream(inputFile), encoding, delimiter, commentIndicator,
				columnIndexes);
	}

	/**
	 * Returns a List<String[]> containing the column values for the input file. One String[] per
	 * line of the file. If you want the entire line, set the delimiter to be null. If there is a
	 * delimiter specified, you must specify at least one column index to return. TODO: track the
	 * number of columns that are returned. If there is a line with a different number of columns
	 * than expected, throw an exception.
	 * 
	 * @param inputStream
	 *            input stream
	 * @param encoding
	 *            encoding
	 * @param delimiter
	 *            delimiter
	 * @param commentIndicator
	 *            prefix indicating comment line
	 * @param columnIndexes
	 * @return
	 * @throws IOException
	 * @throws ArrayIndexOutOfBoundsException
	 *             - if a requested column index does not exist
	 * @throws IllegalArgumentException
	 *             - if a delimiter is specified, but no column indexes are requested
	 */
	public static List<String[]> loadColumnsFromDelimitedFile(InputStream inputStream, CharacterEncoding encoding,
			String delimiter, String commentIndicator, int... columnIndexes) throws IOException,
			ArrayIndexOutOfBoundsException, IllegalArgumentException {
		if (delimiter != null && (columnIndexes == null || columnIndexes.length == 0)) {
			throw new IllegalArgumentException(String.format(
					"Cannot parse columns from line. A delimiter \"%s\" has been specified, "
							+ "however no columns have been requested. If you want the entire line, "
							+ "set the delimiter to be null.", delimiter));
		}
		List<String[]> outputColumns = new ArrayList<String[]>();
		for (StreamLineIterator lineIter = new StreamLineIterator(inputStream, encoding, commentIndicator); lineIter
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

	/**
	 * Returns the columns extracted from the input line using a regular expression delimiter. If
	 * the field enclosure regex is also set, then any delimiters inside a field are not treated as
	 * column delimiters.
	 * 
	 * @param line
	 * @param delimiterRegex
	 * @param fieldEnclosingRegex
	 * @param columnIndexes
	 * @return
	 */
	public static String[] getColumnsFromLine(String line, String delimiterRegex, String fieldEnclosingRegex,
			int... columnIndexes) {
		if (delimiterRegex == null) {
			return new String[] { line };
		}

		String[] lineTokens = StringUtil.splitWithFieldEnclosure(line, delimiterRegex, fieldEnclosingRegex);
		int[] cIndexes = columnIndexes;
		if (columnIndexes == null || columnIndexes.length == 0) {
			cIndexes = CollectionsUtil.createZeroBasedSequence(lineTokens.length);
		}
		String[] outputColumns = new String[cIndexes.length];
		int outputIndex = 0;
		for (int columnIndex : cIndexes) {
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
	 * @param encoding
	 * @param delimiter
	 * @param commentIndicator
	 * @param columnIndex
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadColumnFromDelimitedFile(File inputFile, CharacterEncoding encoding,
			String delimiter, String commentIndicator, int columnIndex) throws IOException {
		FileInputStream inputStream = new FileInputStream(inputFile);
		try {
		return loadColumnFromDelimitedFile(inputStream, encoding, delimiter, commentIndicator,
				columnIndex);
		} finally {
			inputStream.close();
		}
	}

	/**
	 * Returns a List<String> containing the contents of the requested column in the input file,
	 * assuming the file contains columns delimited by the delimiter String.
	 * 
	 * @param inputStream
	 * @param encoding
	 * @param delimiter
	 * @param commentIndicator
	 * @param columnIndex
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadColumnFromDelimitedFile(InputStream inputStream, CharacterEncoding encoding,
			String delimiter, String commentIndicator, int columnIndex) throws IOException {
		List<String[]> singleColumnAsArrayList = loadColumnsFromDelimitedFile(inputStream, encoding, delimiter,
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
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadLinesFromFile(InputStream inputStream, CharacterEncoding encoding)
			throws IOException {
		return loadColumnFromDelimitedFile(inputStream, encoding, null, null, 0);
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
