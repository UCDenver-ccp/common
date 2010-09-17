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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamLineReader extends LineReader {

	private final BufferedReader reader;
	private int lineNumber = 0;

	public StreamLineReader(InputStream inputStream, String encoding, String skipLinePrefix) throws IOException {
		super(skipLinePrefix);
		lineNumber = 0;
		reader = new BufferedReader(new InputStreamReader(inputStream, encoding));
	}

	public StreamLineReader(File inputFile, String encoding, String skipLinePrefix) throws IOException {
		super(skipLinePrefix);
		lineNumber = 0;
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encoding));
	}

	@Override
	public Line readLine() throws IOException {
		String lineText = reader.readLine();
		if (lineText == null)
			return null;
		if (skipLine(lineText))
			return readLine();
		return new Line(lineText, lineNumber++);
	}

	@Override
	public void close() throws IOException {
		if (reader != null)
			reader.close();
	}

}
