package edu.ucdenver.ccp.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

public class FileLoaderUtil {

	private static final String FILE_ENCODING_PROPERTY = "file.encoding";
	private static final String DEFAULT_ENCODING = System.getProperty(FILE_ENCODING_PROPERTY);

	public static List<String[]> loadColumnsFromDelimitedFile(File inputFile, String delimiter,
			String commentIndicator, int... columnIndexes) throws IOException, ArrayIndexOutOfBoundsException,
			IllegalArgumentException {
		return loadColumnsFromDelimitedFile(inputFile, DEFAULT_ENCODING, delimiter, commentIndicator, columnIndexes);
	}

	/**
	 * Returns a List<String[]> containing the column values for the input file. One String[] per
	 * line of the file. If you want the entire line, set the delimiter to be null. If there is a
	 * delimiter specified, you must specify at least one column index to return.
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
	public static List<String[]> loadColumnsFromDelimitedFile(File inputFile, String encoding, String delimiter,
			String commentIndicator, int... columnIndexes) throws IOException, ArrayIndexOutOfBoundsException,
			IllegalArgumentException {
		if (delimiter != null && (columnIndexes == null || columnIndexes.length == 0)) {
			throw new IllegalArgumentException(String.format(
					"Cannot parse columns from line. A delimiter \"%s\" has been specified, "
							+ "however no columns have been requested. If you want the entire line, "
							+ "set the delimiter to be null.", delimiter));
		}
		List<String[]> outputColumns = new ArrayList<String[]>();
		for (Iterator<String> lineIter = getLineIterator(inputFile, encoding, commentIndicator); lineIter.hasNext();) {
			String line = lineIter.next();
			outputColumns.add(getColumnsFromLine(line, delimiter, columnIndexes));
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
	private static String[] getColumnsFromLine(String line, String delimiter, int... columnIndexes)
			throws ArrayIndexOutOfBoundsException {
		if (delimiter == null) {
			return new String[] { line };
		} else {
			String[] outputColumns = new String[columnIndexes.length];
			String[] lineTokens = line.split(delimiter);
			int outputIndex = 0;
			for (int columnIndex : columnIndexes) {
				ensureColumnIndexIsValid(columnIndex, line, lineTokens);
				outputColumns[outputIndex++] = lineTokens[columnIndex];
			}
			return outputColumns;
		}
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
	public static List<String> loadColumnFromDelimitedFile(File inputFile, String encoding, String delimiter,
			String commentIndicator, int columnIndex) throws IOException {
		List<String[]> singleColumnAsArrayList = loadColumnsFromDelimitedFile(inputFile, encoding, delimiter,
				commentIndicator, columnIndex);
		List<String> outputList = new ArrayList<String>(singleColumnAsArrayList.size());
		while (!singleColumnAsArrayList.isEmpty()) {
			outputList.add(singleColumnAsArrayList.get(0)[0]);
			singleColumnAsArrayList.remove(0);
		}
		return outputList;
	}

	public static List<String> loadColumnFromDelimitedFile(File inputFile, String delimiter, String commentIndicator,
			int columnIndex) throws IOException {
		return loadColumnFromDelimitedFile(inputFile, DEFAULT_ENCODING, delimiter, commentIndicator, columnIndex);
	}

	/**
	 * Returns a List<String> containing the lines loaded from the input File
	 * 
	 * @param inputFile
	 * @param commentIndicator
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadLinesFromFile(File inputFile, String encoding, String commentIndicator)
			throws IOException {
		return loadColumnFromDelimitedFile(inputFile, encoding, null, commentIndicator, 0);
	}

	/**
	 * 
	 * @param inputFile
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadLinesFromFile(File inputFile, String encoding) throws IOException {
		return loadColumnFromDelimitedFile(inputFile, encoding, null, null, 0);
	}

	public static List<String> loadLinesFromFile(File inputFile) throws IOException {
		return loadColumnFromDelimitedFile(inputFile, DEFAULT_ENCODING, null, null, 0);
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

	/**
	 * Returns an Iterator<String> over the lines of the input file.
	 * 
	 * @param inputFile
	 * @return
	 * @throws IOException
	 */
	public static Iterator<String> getLineIterator(final File inputFile, String encoding, final String commentIndicator)
			throws IOException {
		final LineIterator lineIterator = IOUtils.lineIterator(new FileInputStream(inputFile), encoding);

		return new Iterator<String>() {
			private String nextLine = null;
			private int lineCount = 0;

			@Override
			public boolean hasNext() {
				if (nextLine == null) {
					if (lineIterator.hasNext()) {
						String line = lineIterator.nextLine();
						lineCount++;
						if (isCommentLine(line, commentIndicator)) {
							return hasNext();
						}
						nextLine = line;
						return true;
					}
					return false;
				}
				return true;
			}

			@Override
			public String next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				String lineToReturn = nextLine;
				nextLine = null;
				return lineToReturn;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("The remove() method is not supported for this iterator.");
			}

			/**
			 * Returns true if the line starts with the commentIndicator String. False otherwise,
			 * and if the commentIndicator String is null.
			 * 
			 * @param line
			 * @param commentIndicator
			 * @return
			 */
			private boolean isCommentLine(String line, String commentIndicator) {
				if (commentIndicator == null) {
					return false;
				}
				return line.trim().startsWith(commentIndicator);
			}
		};
	}

	public static Iterator<String> getLineIterator(final File inputFile, final String commentIndicator)
			throws IOException {
		return getLineIterator(inputFile, DEFAULT_ENCODING, commentIndicator);
	}

}
