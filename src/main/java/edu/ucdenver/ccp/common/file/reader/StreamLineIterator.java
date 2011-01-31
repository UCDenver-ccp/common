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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.reader.LineReader.Line;

/**
 * This class is used to iterate over lines obtained from a Stream.
 * 
 * @author bill
 * 
 */
public class StreamLineIterator extends LineIterator<Line> {

	/**
	 * Logger used to output name of file being iterated over
	 */
	private static final Logger logger = Logger.getLogger(StreamLineIterator.class);

	/**
	 * Initializes a <code>StreamLineReader</code> to read from the input <code>InputStream</code>
	 * 
	 * @param inputStream
	 * @param encoding
	 * @param skipLinePrefix
	 * @throws IOException
	 */
	public StreamLineIterator(InputStream inputStream, CharacterEncoding encoding, String skipLinePrefix)
			throws IOException {
		super(inputStream, encoding, skipLinePrefix);
	}

	/**
	 * Initializes a <code>StreamLineReader</code> to read from the input <code>File</code>. TODO:
	 * This constructor belongs in the FileLineIterator class and FileLineIterator should be a
	 * subclass of StreamLineIterator
	 * 
	 * @param inputFile
	 * @param encoding
	 * @param skipLinePrefix
	 * @throws IOException
	 */
	public StreamLineIterator(File inputFile, CharacterEncoding encoding, String skipLinePrefix) throws IOException {
		super(new FileInputStream(inputFile), encoding, skipLinePrefix);
		logger.debug(String.format("Iterating through lines for file: %s", inputFile.getAbsolutePath()));
	}

	/**
	 * Initializes a new <code>StreamLineReader</code> to be used by the
	 * <code>StreamLineIterator</code>
	 */
	@Override
	public StreamLineReader initLineReader(Object inputStream, CharacterEncoding encoding, String skipLinePrefix) {
		return new StreamLineReader((InputStream) inputStream, encoding, skipLinePrefix);
	}

}
