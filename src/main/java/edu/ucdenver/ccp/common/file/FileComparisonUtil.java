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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;

/**
 * A simple utility for comparing the contents of text files.
 * 
 * @author Bill Baumgartner
 * 
 */
public class FileComparisonUtil {
	private static final Logger logger = Logger.getLogger(FileComparisonUtil.class);

	public enum LineOrder {
		ANY_ORDER, AS_IN_FILE
	}

	public enum ColumnOrder {
		ANY_ORDER, AS_IN_FILE
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
			String columnDelimiterRegex, LineOrder lineOrder, ColumnOrder columnOrder) throws IOException {
		List<String> lines = FileReaderUtil.loadLinesFromFile(outputFile, encoding);
		List<String> expectedLinesInFile = new ArrayList<String>(expectedLines);

		int lineIndex = 0;
		boolean allLinesAsExpected = true;
		for (String line : lines) {
			if (!isAnExpectedLine(line, expectedLinesInFile, lineIndex, lineOrder, columnOrder, columnDelimiterRegex)) {
				logger.info(String.format("Line in file not in expected list: '%s'", line));
				allLinesAsExpected = false;
			}
			lineIndex++;
		}

		return (lines.size() == expectedLines.size() && allLinesAsExpected);
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
			return linesAreEquivalent(line, expectedLines.get(lineIndex), columnOrder, delimiterRegex);
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

}
