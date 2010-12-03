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

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.reader.LineReader.FileLine;

/**
 * A simple utility for providing an iterator over the lines from a file. The Line data structure
 * that is returned contains not only the text for the given line, but the line number as well as
 * the byte offset indicating the start of the line within the file.
 * 
 * @author bill
 * 
 */
public class FileLineIterator extends LineIterator<FileLine> {
	private static final Logger logger = Logger.getLogger(FileLineIterator.class);

	public FileLineIterator(File inputFile, CharacterEncoding encoding, String skipLinePrefix) throws IOException {
		super(inputFile, encoding, skipLinePrefix);
		logger.info(String.format("Iterating through lines for file: %s", inputFile.getAbsolutePath()));
	}

	@Override
	public FileLineReader initLineReader(Object inputFile, CharacterEncoding encoding, String skipLinePrefix)
			throws IOException {
		return new FileLineReader((File) inputFile, encoding, skipLinePrefix);
	}

}
