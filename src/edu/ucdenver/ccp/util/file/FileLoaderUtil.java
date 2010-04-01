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
			int columnIndex, String commentIndicator) throws IOException {
		List<String> outputColumn = new ArrayList<String>();
		for (Iterator<String> lineIter = getLineIterator(inputFile, encoding, commentIndicator); lineIter.hasNext();) {
			String line = lineIter.next();
			if (delimiter == null) {
				outputColumn.add(line);
			} else {
				String[] lineTokens = line.split(delimiter);
				if (isColumnIndexValid(columnIndex, line, lineTokens)) {
					outputColumn.add(lineTokens[columnIndex]);
				} else {
					throw new ArrayIndexOutOfBoundsException(String.format(
							"Column index %d does not exist on line: %s", columnIndex, line));
				}
			}
		}
		return outputColumn;
	}

	public static List<String> loadColumnFromDelimitedFile(File inputFile, String delimiter, int columnIndex,
			String commentIndicator) throws IOException {
		return loadColumnFromDelimitedFile(inputFile, DEFAULT_ENCODING, delimiter, columnIndex, commentIndicator);
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
		return loadColumnFromDelimitedFile(inputFile, encoding, null, 0, commentIndicator);
	}

	/**
	 * 
	 * @param inputFile
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadLinesFromFile(File inputFile, String encoding) throws IOException {
		return loadColumnFromDelimitedFile(inputFile, encoding, null, 0, null);
	}

	public static List<String> loadLinesFromFile(File inputFile) throws IOException {
		return loadColumnFromDelimitedFile(inputFile, DEFAULT_ENCODING, null, 0, null);
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
	private static boolean isColumnIndexValid(int columnIndex, String line, String[] lineTokens) {
		return (columnIndex > -1 && columnIndex < lineTokens.length - 1);
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
