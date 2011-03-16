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

import java.io.File;
import java.io.IOException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;

/**
 * A simple utility for providing an iterator over the lines from a file. The Line data structure
 * that is returned contains not only the text for the given line, but the line number as well as
 * the byte offset indicating the start of the line within the file.
 * 
 * @author bill
 * 
 */
public class FileLineIterator extends LineIterator<FileLine> {

	/**
	 * Initializes a new FileLineIterator using the specified input file and character encoding.
	 * 
	 * @param inputFile
	 * @param encoding
	 * @param skipLinePrefix
	 *            lines that begin with this specified prefix are skipped (not returned by the
	 *            iterator)
	 * @throws IOException
	 */
	public FileLineIterator(File inputFile, CharacterEncoding encoding, String skipLinePrefix) throws IOException {
		super(inputFile, encoding, skipLinePrefix);
	}

	/**
	 * Initializes a new FileLineIterator using the specified input file and character encoding. The
	 * skip line prefix is set to null.
	 * 
	 * @param inputFile
	 * @param encoding
	 * @throws IOException
	 */
	public FileLineIterator(File inputFile, CharacterEncoding encoding) throws IOException {
		super(inputFile, encoding, null);
	}

	/**
	 * Initializes a FileLineReader for reading the lines of the input file
	 * 
	 * @see edu.ucdenver.ccp.common.file.reader.LineIterator#initLineReader(java.lang.Object,
	 *      edu.ucdenver.ccp.common.file.CharacterEncoding, java.lang.String)
	 */
	@Override
	public FileLineReader initLineReader(Object inputFile, CharacterEncoding encoding, String skipLinePrefix)
			throws IOException {
		return new FileLineReader((File) inputFile, encoding, skipLinePrefix);
	}

}
