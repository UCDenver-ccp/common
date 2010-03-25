package edu.ucdenver.ccp.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import edu.ucdenver.ccp.util.exception.FileProcessingException;
import edu.ucdenver.ccp.util.exception.runtime.MultipleFailuresException;

public class FileLoaderUtil {

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
	public static List<String> loadColumnFromDelimitedFile(File inputFile, String delimiter, int columnIndex,
			String commentIndicator) throws IOException {
		List<String> outputColumn = new ArrayList<String>();
		for (Iterator<String> lineIter = getLineIterator(inputFile, commentIndicator); lineIter.hasNext();) {
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

	/**
	 * Returns a List<String> containing the lines loaded from the input File
	 * 
	 * @param inputFile
	 * @param commentIndicator
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadLinesFromFile(File inputFile, String commentIndicator) throws IOException {
		return loadColumnFromDelimitedFile(inputFile, null, 0, commentIndicator);
	}

	/**
	 * 
	 * @param inputFile
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadLinesFromFile(File inputFile) throws IOException {
		return loadColumnFromDelimitedFile(inputFile, null, 0, null);
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
	 * @throws FileNotFoundException
	 */
	public static Iterator<String> getLineIterator(final File inputFile, final String commentIndicator)
			throws FileNotFoundException {
		final BufferedReader br = new BufferedReader(new FileReader(inputFile));
		return new Iterator<String>() {
			private String nextLine = null;
			private int lineCount = 0;

			@Override
			public boolean hasNext() {
				if (nextLine == null) {
					try {
						String line = br.readLine();
						lineCount++;
						if (line == null) {
							cleanup(br, null);
							return false;
						}
						if (isCommentLine(line, commentIndicator)) {
							return hasNext();
						}
						nextLine = line;
						return true;
					} catch (IOException e) {
						cleanup(br, new FileProcessingException(lineCount, e));
					}
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

			/**
			 * Closes the reader while keeping track of a potential previously thrown exception
			 * 
			 * @param br
			 * @param previouslyThrownException
			 */
			private void cleanup(BufferedReader br, FileProcessingException previouslyThrownException) {
				String errorMessage = String.format("Error while iterating over file: %s", inputFile.getAbsolutePath());
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						if (previouslyThrownException != null) {
							throw new MultipleFailuresException(errorMessage, e, previouslyThrownException);
						} else {
							throw new RuntimeException(errorMessage, e);
						}
					}
				}
				if (previouslyThrownException != null) {
					throw new RuntimeException(errorMessage, previouslyThrownException);
				}
			}

		};
	}

}
