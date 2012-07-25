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

import edu.ucdenver.ccp.common.file.reader.Line.LineTerminator;

/**
 * Enum representing lines in the sample-CRLF.utf8 file located in the classpath
 * 
 * @author bill
 * 
 */
public enum SampleUtf8File {
	/**
	 * Line 1: Line#1\r
	 */
	LINE1(6, 6, LineTerminator.CRLF),
	/**
	 * Line 2: \x{1D49C}\x{212C}\x{1D49E}\x{1D49F} is ABCD\r
	 */
	LINE2(12, 15, LineTerminator.CRLF),
	/**
	 * Line 3: let \x{1D49E}\x{304} = sqrt(\x{1D49C}\x{B2} + \x{212C}\x{B2})\r
	 */
	LINE3(22, 24, LineTerminator.CRLF),
	/**
	 * Line 4: let \x{1D4B1}\x{305} = 4/3 \x{B7} \x{3C0} \x{B7}
	 * \x{1D4C7}\x{B3}\r
	 */
	LINE4(21, 23, LineTerminator.CRLF),
	/**
	 * Line 5: let \x{1D4B1}\x{305} =
	 * 4\x{2044}3\x{2062}\x{3C0}\x{2062}\x{1D4C7}\x{B3}\r
	 */
	LINE5(17, 19, LineTerminator.CRLF),
	/**
	 * Line 6: let \x{2133}\x{304} = 60 * \x{1D4AE}\x{305}\r
	 */
	LINE6(16, 17, LineTerminator.CRLF),
	/**
	 * Line 7: let \x{210B} = 60 * \x{2133}\r
	 */
	LINE7(14, 14, LineTerminator.CRLF),
	/**
	 * Line 8: let \x{1D49F}\x{305} = 24 * \x{210B}\r
	 */
	LINE8(15, 16, LineTerminator.CRLF),
	/**
	 * Line 9: let \x{1D4B4} = 365.2425 * \x{210B}\r
	 */
	LINE9(20, 21, LineTerminator.CRLF),
	/**
	 * Line 10: Line#10\r
	 */
	LINE10(7, 7, LineTerminator.CRLF);

	/**
	 * The name of the sample file
	 */
	public static final String FILE_NAME = "sample-CRLF.utf8";

	/**
	 * The character encoding used by the sample file
	 */
	public static final CharacterEncoding ENCODING = CharacterEncoding.UTF_8;

	/**
	 * The number of characters on the line (excluding the line terminator)
	 */
	private final int characterCount;

	/**
	 * The number of Unicode characters on the line (excluding the line
	 * terminator)
	 */
	private final int codePointCount;

	/**
	 * The line terminator used at the end of the line
	 */
	private final LineTerminator terminator;

	/**
	 * private constructor used by enum
	 * 
	 * @param characterCount
	 *            the number of characters on the line
	 * @param terminator
	 *            the line terminator used at the end of the line
	 */
	private SampleUtf8File(int codePointCount, int characterCount,
			LineTerminator terminator) {
		this.codePointCount = codePointCount;
		this.characterCount = characterCount;
		this.terminator = terminator;
	}

	/**
	 * @return the length of this line in terms of characters (character count +
	 *         line terminator length)
	 */
	public int getCharacterLineLength() {
		return characterCount + terminator.length();
	}

	/**
	 * @return the length of this line in terms of code points
	 */
	public int getCodePointLineLength() {
		return codePointCount + terminator.length();
	}

	/**
	 * @return the character offset for this line (the count of all character
	 *         that occur before this line)
	 */
	public int getCharacterOffset() {
		int offset = 0;
		for (SampleUtf8File line : SampleUtf8File.values()) {
			if (this.equals(line))
				break;
			offset += line.getCharacterLineLength();
		}
		return offset;
	}

	/**
	 * @return the code point for this line (the count of all Unicode characters
	 *         that occur before this line)
	 */
	public int getCodePointOffset() {
		int offset = 0;
		for (SampleUtf8File line : SampleUtf8File.values()) {
			if (this.equals(line))
				break;
			offset += line.getCodePointLineLength();
		}
		return offset;
	}

}
