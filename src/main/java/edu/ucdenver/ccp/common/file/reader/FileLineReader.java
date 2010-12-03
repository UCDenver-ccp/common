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
import java.io.RandomAccessFile;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;

public class FileLineReader extends LineReader {

	private final BufferedRafReader reader;
	private int lineNumber = 0;

	public FileLineReader(File dataFile, CharacterEncoding encoding) throws IOException {
		super(null);
		FileUtil.validateFile(dataFile);
		reader = new BufferedRafReader(dataFile, encoding);
		lineNumber = 0;
	}

	public FileLineReader(File dataFile, CharacterEncoding encoding, String skipLinePrefix) throws IOException {
		super(skipLinePrefix);
		FileUtil.validateFile(dataFile);
		reader = new BufferedRafReader(dataFile, encoding);
		lineNumber = 0;
	}

	/**
	 * Read next line.
	 * @return instance of {@link FileLine} if available; otherwise, null.
	 */
	@Override
	public FileLine readLine() throws IOException {
		try {
			long byteOffset = reader.getFilePointer();
			String line = reader.readBufferedLine();
			if (line == null)
				return null;
			if (skipLine(line))
				return readLine();
			return new FileLine(line, lineNumber++, byteOffset);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		if (reader != null)
			reader.close();
	}

	/**
	 * Get current file pointer
	 * 
	 * @return file pointer
	 * @throws IOException
	 * @see RandomAccessFile
	 */
	protected long getFilePointer() throws IOException {
		return reader.getFilePointer();
	}

	/**
	 * Set current file pointer.
	 * 
	 * @param position
	 * @throws RuntimeException
	 *             if errors occur
	 * @see RandomAccessFile#seek(long)
	 */
	public void seek(long position) {
		try {
			reader.seek(position);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
