/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.common.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;

/**
 * A simple utility for comparing the contents of text files.
 * 
 * @author Bill Baumgartner
 * 
 */
public class FileComparisonUtil {
	/**
	 * This logger is used to log differences among the files being compared
	 */
	private static final Logger logger = Logger.getLogger(FileComparisonUtil.class);

	/**
	 * File comparisons can specify whether or not line ordering should be considered when comparing
	 * files.
	 * 
	 * @author bill
	 * 
	 */
	public enum LineOrder {
		/**
		 * Signifies that when comparing two files the lines can appear in any order
		 */
		ANY_ORDER,
		/**
		 * Signifies that when comparing two files the appearance of lines in one file must match
		 * the order of appearance in the other file exactly.
		 */
		AS_IN_FILE
	}

	/**
	 * File comparisons can specify whether or not column ordering should be considered when
	 * comparing files.
	 * 
	 * @author bill
	 * 
	 */
	public enum ColumnOrder {
		/**
		 * Signifies that columns can appear in any order when comparing two files
		 */
		ANY_ORDER,
		/**
		 * Signifies that columns must appear in the same order for files to match
		 */
		AS_IN_FILE
	}

	/**
	 * File comparisons can specify whether a line should be trimmed (leading and trailing
	 * whitespace removed) before comparison
	 * 
	 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public enum LineTrim {
		/**
		 * 
		 */
		ON,
		OFF
	}

	public static boolean hasExpectedLines(File outputFile, CharacterEncoding encoding, List<String> expectedLines,
			String columnDelimiterRegex, LineOrder lineOrder, ColumnOrder columnOrder) throws IOException {
		return hasExpectedLines(outputFile, encoding, expectedLines, columnDelimiterRegex, lineOrder, columnOrder,
				LineTrim.OFF);
	}

	/**
	 * Returns true if the input list of lines matches those found in the input file, based on the
	 * LineOrder and ColumnOrder properties.
	 * 
	 * @param outputFile
	 * @param expectedLines
	 * @param columnDelimiterRegex
	 * @param lineOrder
	 * @param columnOrder
	 * @return
	 * @throws IOException
	 */
	public static boolean hasExpectedLines(File outputFile, CharacterEncoding encoding, List<String> expectedLines,
			String columnDelimiterRegex, LineOrder lineOrder, ColumnOrder columnOrder, LineTrim lineTrim)
			throws IOException {
		List<String> lines = FileReaderUtil.loadLinesFromFile(outputFile, encoding);
		List<String> remainingExpectedLines = new ArrayList<String>(expectedLines);
		if (lines.size() == 0 && expectedLines.size() > 0)
			logger.info("File contains no output.");

		List<String> trimmedExpectedLines = new ArrayList<String>(expectedLines);
		if (lineTrim.equals(LineTrim.ON)) {
			remainingExpectedLines = new ArrayList<String>();
			trimmedExpectedLines = new ArrayList<String>();
			for (String line : expectedLines) {
				trimmedExpectedLines.add(line.trim());
				remainingExpectedLines.add(line.trim());
			}
		}

		int lineIndex = 0;
		boolean allLinesAsExpected = true;
		for (String line : lines) {
			if (lineTrim.equals(LineTrim.ON))
				line = line.trim();
			if (isAnExpectedLine(line, trimmedExpectedLines, lineIndex, lineOrder, columnOrder, columnDelimiterRegex)) {
				remainingExpectedLines.remove(line);
			} else {
				logger.info(String.format("Line (%d) in file of actual output, not in expected list: '%s'", lineIndex, line));
				allLinesAsExpected = false;
			}
			lineIndex++;
		}
		boolean hasExpectedLines = (lines.size() == expectedLines.size() && allLinesAsExpected);
		if (!hasExpectedLines) {
			logger.info("File of actual output does not contain expected lines. # lines in file: " + lines.size()
					+ " # expected lines: " + expectedLines.size() + " Expected lines matched those in actual output file: "
					+ allLinesAsExpected);
			for (String line : remainingExpectedLines) {
				logger.info(String.format("EXPECTED LINE not in file: '%s'", line));
			}
		}
		return hasExpectedLines;
	}

	/**
	 * Returns true if the input line matches one of the expected lines based on the LineOrder and
	 * ColumnOrder properties.
	 * 
	 * @param line
	 * @param expectedLines
	 * @param lineIndex
	 * @param lineOrder
	 * @param columnOrder
	 * @param delimiterRegex
	 * @return
	 */
	private static boolean isAnExpectedLine(String line, List<String> expectedLines, int lineIndex,
			LineOrder lineOrder, ColumnOrder columnOrder, String delimiterRegex) {
		if (lineOrder == LineOrder.AS_IN_FILE) {
			if (expectedLines.size() > lineIndex)
				return linesAreEquivalent(line, expectedLines.get(lineIndex), columnOrder, delimiterRegex);
			return false;
		} else if (lineOrder == LineOrder.ANY_ORDER) {
			return lineInExpectedSet(line, expectedLines, columnOrder, delimiterRegex);
		} else
			throw new RuntimeException(String.format("Unknown LineOrder: %s", lineOrder.toString()));
	}

	/**
	 * Returns true if the input line is a member of the expected lines list based on the LineOrder
	 * and ColumnOrder properties.
	 * 
	 * @param line
	 * @param expectedLines
	 * @param columnOrder
	 * @param delimiterRegex
	 * @return
	 */
	private static boolean lineInExpectedSet(String line, List<String> expectedLines, ColumnOrder columnOrder,
			String delimiterRegex) {
		if (columnOrder == ColumnOrder.AS_IN_FILE) {
			if (expectedLines.contains(line)) {
				expectedLines.remove(line);
				return true;
			}
			return false;
		} else if (columnOrder == ColumnOrder.ANY_ORDER) {
			String[] lineToks = line.split(delimiterRegex, -1);
			for (String expectedLine : expectedLines) {
				String[] expectedLineToks = expectedLine.split(delimiterRegex, -1);
				if (lineToks.length == expectedLineToks.length) {
					if (CollectionsUtil.array2Set(lineToks).equals(CollectionsUtil.array2Set(expectedLineToks))) {
						expectedLines.remove(expectedLine);
						return true;
					}
				}
			}
			return false;
		} else
			throw new RuntimeException(String.format("Unknown ColumnOrder: %s", columnOrder.toString()));
	}

	/**
	 * Returns true if the input line is as expected, false otherwise. If columnOrder is ANY_ORDER
	 * then columns can be rearranged.
	 * 
	 * @param line
	 * @param expectedLine
	 * @param columnOrder
	 * @param delimiterRegex
	 * @return
	 */
	private static boolean linesAreEquivalent(String line, String expectedLine, ColumnOrder columnOrder,
			String delimiterRegex) {
		if (columnOrder == ColumnOrder.AS_IN_FILE) {
			return line.equals(expectedLine);
		} else if (columnOrder == ColumnOrder.ANY_ORDER) {
			String[] lineToks = line.split(delimiterRegex, -1);
			String[] expectedLineToks = expectedLine.split(delimiterRegex, -1);
			if (lineToks.length == expectedLineToks.length) {
				return CollectionsUtil.array2Set(lineToks).equals(CollectionsUtil.array2Set(expectedLineToks));
			}

			return false;
		} else
			throw new RuntimeException(String.format("Unknown ColumnOrder: %s", columnOrder.toString()));
	}

	/**
	 * Computes the MD5 CheckSum for the input file and writes it to a file in the same directory
	 * called [INPUT_FILE_NAME].md5
	 * 
	 * @param inputFile
	 * @return
	 */
	public static File createMd5ChecksumFile(File inputFile) {
		try {
			String md5sum = computeMd5Checksum(inputFile) + " " + inputFile.getName();
			File checkSumFile = getChecksumFile(inputFile);
			FileWriterUtil.printLines(CollectionsUtil.createList(md5sum), checkSumFile, CharacterEncoding.UTF_8,
					WriteMode.OVERWRITE, FileSuffixEnforcement.OFF);
			return checkSumFile;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Simply appends .md5 to the input file name
	 * 
	 * @param inputFile
	 * @return
	 */
	private static File getChecksumFile(File inputFile) {
		File checkSumFile = new File(inputFile.getAbsolutePath() + ".md5");
		return checkSumFile;
	}

	/**
	 * The format of MD5 check sum files are not always the same. This interface allows the user to
	 * specify the check sum extraction from a line (presumably from a file)
	 * 
	 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public static interface CheckSumExtractor {
		/**
		 * @param line
		 * @return the check sum extracted from the input line
		 */
		public String extractCheckSumFromLine(String line);
	}

	/**
	 * Can be used for the case where the MD5 sum file format is:<br>
	 * [CHECKSUM] [FILENAME]
	 * 
	 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
	 * 
	 */
	public static class DefaultCheckSumExtractor implements CheckSumExtractor {
		/**
		 * assumes the file format consists of the checksum then a space then the file name
		 */
		@Override
		public String extractCheckSumFromLine(String line) {
			return line.split("\\s+")[0];
		}
	}

	/**
	 * @param inputFile
	 * @param checkSumFile
	 * @param checkSumExtractor
	 * @return true if the MD5 checksum in the checkSumFile equals the MD5 checksum computed on the
	 *         inputFile, false otherwise
	 */
	public static boolean fileHasExpectedMd5Checksum(File inputFile, File checkSumFile,
			CheckSumExtractor checkSumExtractor) {
		try {
			String checkSumLine = FileReaderUtil.loadLinesFromFile(checkSumFile, CharacterEncoding.UTF_8).get(0);
			String expectedChecksum = checkSumExtractor.extractCheckSumFromLine(checkSumLine);
			String actualCheckSum = computeMd5Checksum(inputFile);
			if (!expectedChecksum.equals(actualCheckSum))
				logger.warn("MD5 check sum failure. Expected: '" + expectedChecksum + "' but was: '" + actualCheckSum + "'");
			return expectedChecksum.equals(actualCheckSum);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Uses the {@link DefaultCheckSumExtractor}
	 * 
	 * @param inputFile
	 * @param checkSumFile
	 * @return true if the MD5 checksum in the checkSumFile equals the MD5 checksum computed on the
	 *         inputFile, false otherwise
	 */
	public static boolean fileHasExpectedMd5Checksum(File inputFile, File checkSumFile) {
		return fileHasExpectedMd5Checksum(inputFile, checkSumFile, new DefaultCheckSumExtractor());
	}

	/**
	 * Assumes there is a file in the same directory as the input file called [INPUT_FILE_NAME].md5
	 * that contains the expected MD5 checksum for the input file.
	 * 
	 * @param inputFile
	 * @return true if the MD5 checksum in the checkSumFile equals the MD5 checksum computed on the
	 *         inputFile, false otherwise
	 */
	public static boolean fileHasExpectedMd5Checksum(File inputFile) {
		File checkSumFile = getChecksumFile(inputFile);
		return fileHasExpectedMd5Checksum(inputFile, checkSumFile);
	}

	/**
	 * Assumes there is a file in the same directory as the input file called [INPUT_FILE_NAME].md5
	 * that contains the expected MD5 checksum for the input file.
	 * 
	 * @param inputFile
	 * @param checkSumExtractor
	 *            used to extract the check sum from the first line in the check sum file
	 * @return true if the MD5 checksum in the checkSumFile equals the MD5 checksum computed on the
	 *         inputFile, false otherwise
	 */
	public static boolean fileHasExpectedMd5Checksum(File inputFile, CheckSumExtractor checkSumExtractor) {
		File checkSumFile = getChecksumFile(inputFile);
		return fileHasExpectedMd5Checksum(inputFile, checkSumFile, checkSumExtractor);
	}

	/**
	 * Computes the MD5 CheckSum for the input file
	 * 
	 * @param inputFile
	 * @return the MD5 CheckSum
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String computeMd5Checksum(File inputFile) throws FileNotFoundException, IOException {
		return DigestUtils.md5Hex(new FileInputStream(inputFile));
	}

}
