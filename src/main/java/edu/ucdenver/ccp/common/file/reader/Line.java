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

import edu.ucdenver.ccp.common.string.StringConstants;

/**
 * Simple class for defining a line
 * 
 * @author bill
 * 
 */
public class Line {

	/**
	 * Representation of the three types of line breaks
	 * 
	 */
	public enum LineTerminator {
		/**
		 * Carriage Return (\r)
		 */
		CR(StringConstants.CARRIAGE_RETURN),
		/**
		 * Line Feed (\n)
		 */
		LF(StringConstants.LINE_FEED),
		/**
		 * Carriage Return/Line Feed Combination (\r\n) - this is used by Windows
		 */
		CRLF(StringConstants.CARRIAGE_RETURN + StringConstants.LINE_FEED);

		/**
		 * The line terminator being represented
		 */
		private final String terminator;

		/**
		 * Private constructor used by enum
		 * 
		 * @param terminator
		 *            the line terminator being represented
		 */
		private LineTerminator(String terminator) {
			this.terminator = terminator;
		}

		/**
		 * @return the line terminator
		 */
		public String terminator() {
			return terminator;
		}

		/**
		 * @return the length of the line terminator
		 */
		public int length() {
			return terminator.length();
		}
	}

	/**
	 * The text contained on this Line
	 */
	private final String text;

	/**
	 * Stores the character offset of the line. The offset is likely relative to a larger file from
	 * where the line is being read.
	 */
	private final long characterOffset;

	/**
	 * Stores the code point offset for this line. This is a count of the number of Unicode
	 * characters observed prior to this line. Unicode characters are not restricted to 16 bits
	 * (size of the Java char class) so they will often be represented as 2 (or perhaps more) chars.
	 * The codePointOffset will differ from the characterOffset in this case.
	 */
	private final long codePointOffset;

	/**
	 * Stores the line number (relative to the specific collection from where the line was read)
	 */
	private final long lineNumber;

	/**
	 * Stores the number of bytes from the beginning of the file to the start of the text
	 * represented by this line
	 */
	private final long byteOffset;

	/**
	 * The terminator that indicated the end of this line
	 */
	private final LineTerminator lineTerminator;

	/**
	 * Initializes a new {@link Line}
	 * 
	 * @param text
	 *            The text contained on this Line
	 * @param lineTerminator
	 *            the line terminator that was found at the end of this line
	 * @param characterOffset
	 *            Stores the character offset for the line. The offset is likely relative to a
	 *            larger file from where the line is being read.
	 * @param codePointOffset
	 *            Stores the code point offset for this line. This is a count of the number of
	 *            Unicode characters observed prior to this line. Unicode characters are not
	 *            restricted to 16 bits (size of the Java char class) so they will often be
	 *            represented as 2 (or perhaps more) chars. The codePointOffset will differ from the
	 *            characterOffset in this case.
	 * @param lineNumber
	 *            Stores the line number (relative to the specific collection from where the line
	 *            was read)
	 */
	public Line(String text, LineTerminator lineTerminator, long characterOffset, long codePointOffset, long lineNumber, long byteOffset) {
		super();
		this.text = text;
		this.lineTerminator = lineTerminator;
		this.characterOffset = characterOffset;
		this.codePointOffset = codePointOffset;
		this.lineNumber = lineNumber;
		this.byteOffset = byteOffset;
	}

	/**
	 * @return the text of this particular line
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the line terminator found on this particular line
	 */
	public LineTerminator getLineTerminator() {
		return lineTerminator;
	}

	/**
	 * @return the line number for this particular line
	 */
	public long getLineNumber() {
		return lineNumber;
	}

	/**
	 * @return the character offset for this line (the number of characters that have appeared
	 *         before this line)
	 */
	public long getCharacterOffset() {
		return characterOffset;
	}

	/**
	 * @return the code point offset for this line (the number of graphemes that have appeared
	 *         before this line)
	 */
	public long getCodePointOffset() {
		return codePointOffset;
	}

	/**
	 * @return the byte offset - the number of bytes from the beginning of the file to the start of
	 *         the text represented by this line
	 */
	public long getByteOffset() {
		return byteOffset;
	}

	/**
	 * Returns a string representation of this line including the line number
	 */
	@Override
	public String toString() {
		return String.format("(Line:%d Offset:%d) %s", lineNumber, characterOffset, text);
	}

}
