package edu.ucdenver.ccp.common.file.reader;

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
