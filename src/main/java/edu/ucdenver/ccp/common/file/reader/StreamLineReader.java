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
package edu.ucdenver.ccp.common.file.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.common.file.reader.Line.LineTerminator;

/**
 * This class reads lines from an input stream
 * 
 * @author bill
 * 
 */
public class StreamLineReader extends LineReader<Line> {

	/**
	 * A BufferedReader is used to read the lines from the input <code>InputStream</code>
	 */
	private final BufferedReader reader;

	/**
	 * Used to store the line number
	 */
	private long lineNumber = 0;

	private long byteOffset = 0;

	/**
	 * Initializes a new <code>StreamLineReader</code> to read from the input
	 * <code>InputStream</code>
	 * 
	 * @param inputStream
	 * @param encoding
	 * @param skipLinePrefix
	 */
	public StreamLineReader(InputStream inputStream, CharacterEncoding encoding, String skipLinePrefix) {
		super(skipLinePrefix);
		lineNumber = 0;
		byteOffset = 0;
		reader = FileReaderUtil.initBufferedReader(inputStream, encoding);
	}

	/**
	 * 
	 * @param inputFile
	 * @param encoding
	 * @param skipLinePrefix
	 * @throws IOException
	 */
	public StreamLineReader(File inputFile, CharacterEncoding encoding, String skipLinePrefix) throws IOException {
		super(skipLinePrefix);
		lineNumber = 0;
		byteOffset = 0;
		reader = FileReaderUtil.initBufferedReader(inputFile, encoding);
	}

	
	public StreamLineReader(File inputFile, CharacterEncoding encoding) throws IOException {
		this(inputFile, encoding, null);
	}
	
	/**
	 * @see edu.ucdenver.ccp.common.file.reader.LineReader#readLine()
	 */
	@Override
	protected Line getNextLine() throws IOException {
		LineTerminator lineTerminator = null;
		StringBuffer buffer = new StringBuffer();
		int c = -1;
		boolean eol = false;
		while (!eol) {
			switch (c = reader.read()) {
			case -1:
			case '\n':
				eol = true;
				lineTerminator = LineTerminator.LF;
				break;
			case '\r':
				eol = true;
				lineTerminator = LineTerminator.CR;
				reader.mark(1);
				if ((reader.read()) != '\n')
					reader.reset();
				else
					lineTerminator = LineTerminator.CRLF;
				break;
			default:
				buffer.append((char) c);
				break;
			}
		}

		if ((c == -1) && (buffer.length() == 0)) {
			return null;
		}
		String lineText = buffer.toString();
		byteOffset = byteOffset + lineText.getBytes().length;

		if (skipLine(lineText)) {
			lineNumber++;
			return readLine();
		}
		return new Line(lineText, lineTerminator, getCharacterOffset(), getCodePointOffset(), lineNumber++, byteOffset);
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if (reader != null)
			reader.close();
	}

}
