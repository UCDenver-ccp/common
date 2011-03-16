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
	private int lineNumber = 0;

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
		reader = FileReaderUtil.initBufferedReader(inputStream, encoding);
	}

	/**
	 * Use FileLineReader instead
	 * 
	 * @param inputFile
	 * @param encoding
	 * @param skipLinePrefix
	 * @throws IOException
	 */
	@Deprecated
	public StreamLineReader(File inputFile, CharacterEncoding encoding, String skipLinePrefix) throws IOException {
		super(skipLinePrefix);
		lineNumber = 0;
		reader = FileReaderUtil.initBufferedReader(inputFile, encoding);
	}

	/**
	 * @see edu.ucdenver.ccp.common.file.reader.LineReader#readLine()
	 */
	@Override
	public Line getNextLine() throws IOException {
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

		if (skipLine(lineText)) {
			lineNumber++;
			return readLine();
		}
		return new Line(lineText, lineTerminator, getCharacterOffset(), getCodePointOffset(), lineNumber++);
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
