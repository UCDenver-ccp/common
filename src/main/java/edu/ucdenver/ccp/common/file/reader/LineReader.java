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

package edu.ucdenver.ccp.common.file.reader;

import java.io.Closeable;
import java.io.IOException;

/**
 * Abstract class for reading a collection of {@link Line} objects.
 * 
 * @author bill
 * 
 */
public abstract class LineReader<T extends Line> implements Closeable {

	/**
	 * The skipLinePrefix member variable is used as an indication that a line should be skipped
	 * (and not "read" by the LineReader). If a line starts with the skip line prefix then it is
	 * skipped
	 */
	protected final String skipLinePrefix;

	/**
	 * Tracks the character offset for each line (the number of characters to appear before a line)
	 */
	private long cumulativeCharacterOffset;

	/**
	 * Tracks the code point offset for each line (the number of Unicode characters that appear
	 * before a line). Unicode characters are not restricted to 16 bits (size of the Java char
	 * class) so they will often be represented as 2 (or perhaps more) chars. The
	 * cumulativeCodePointOffset will differ from the cumulativeCsharacterOffset in this case.
	 */
	private long cumulativeCodePointOffset;

	/**
	 * Constructor for the abstract LineReader
	 * 
	 * @param skipLinePrefix
	 *            if a line starts with the skip line prefix, then it is skipped (and not returned
	 *            by the LineReader)
	 */
	public LineReader(String skipLinePrefix) {
		this.cumulativeCharacterOffset = 0;
		this.cumulativeCodePointOffset = 0;
		this.skipLinePrefix = skipLinePrefix;
	}

	/**
	 * Public method for reading the next line from the collection
	 * 
	 * @return the next {@link Line} instance
	 * @throws IOException
	 *             if there's an error while reading the next line
	 */
	public final T readLine() throws IOException {
		T line = getNextLine();
		updateCharacterOffset(line);
		return line;
	}

	/**
	 * Updates the character offset by adding the number of characters in the current line to the
	 * cumulative count plus the length of the line terminator used
	 * 
	 * @param line
	 *            the line whose characters must be counted and added to the cumulative character
	 *            offset count (plus the length of the line terminator used)
	 */
	private void updateCharacterOffset(Line line) {
		if (line != null) {
			cumulativeCharacterOffset += (line.getText().toCharArray().length + line.getLineTerminator().length());
			cumulativeCodePointOffset += (line.getText().codePointCount(0, line.getText().length()) + line
					.getLineTerminator().terminator().codePointCount(0, line.getLineTerminator().terminator().length()));
		}
	}

	/**
	 * Abstract method that defines how an implementation of LineReader reads a line
	 * 
	 * @return the most recently read Line
	 * @throws IOException
	 */
	protected abstract T getNextLine() throws IOException;

	/**
	 * This method checks to see if a line should be skipped. It returns true if the line starts
	 * with the skip line prefix, fale otherwise.
	 * 
	 * @param line
	 * @return
	 */
	protected boolean skipLine(String line) {
		if (skipLinePrefix == null)
			return false;
		return line.trim().startsWith(skipLinePrefix);
	}

	/**
	 * @return the cumulative character offset up to the current point in the collection of lines
	 *         being read
	 */
	protected long getCharacterOffset() {
		return cumulativeCharacterOffset;
	}

	/**
	 * @return the cumulative code point offset up to the current line
	 */
	protected long getCodePointOffset() {
		return cumulativeCodePointOffset;
	}

}
