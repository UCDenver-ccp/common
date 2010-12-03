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

public class StreamLineIterator extends LineIterator<Line> {

	private static final Logger logger = Logger.getLogger(StreamLineIterator.class);

	public StreamLineIterator(InputStream inputStream, CharacterEncoding encoding, String skipLinePrefix) throws IOException {
		super(inputStream, encoding, skipLinePrefix);
		logger.info(String.format("Iterating through lines of InputStream"));
	}

	public StreamLineIterator(File inputFile, CharacterEncoding encoding, String skipLinePrefix) throws IOException {
		super(new FileInputStream(inputFile), encoding, skipLinePrefix);
		logger.info(String.format("Iterating through lines for file: %s", inputFile.getAbsolutePath()));
	}

	@Override
	public StreamLineReader initLineReader(Object inputStream, CharacterEncoding encoding, String skipLinePrefix)
			throws IOException {
		return new StreamLineReader((InputStream) inputStream, encoding, skipLinePrefix);
	}
	
}
