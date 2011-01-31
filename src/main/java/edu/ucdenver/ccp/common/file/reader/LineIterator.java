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

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.reader.LineReader.Line;

/**
 * Abstract class for creating an iterator over the lines from some collecion (e.g. a file)
 * 
 * @author bill
 * 
 * @param <T>
 */
public abstract class LineIterator<T extends Line> implements Iterator<T> {

	/**
	 * Stores the next line to return
	 */
	protected T nextLine = null;
	/**
	 * Stores the <code>LineReader</code> used to extract the lines returned by this
	 * <code>Iterator</code> implementation
	 */
	protected final LineReader reader;

	/**
	 * Initializes a new <code>LineIterator</code> to the input object (typically a file or stream)
	 * 
	 * @param fileOrStream
	 * @param encoding
	 * @param skipLinePrefix
	 * @throws IOException
	 */
	public LineIterator(Object fileOrStream, CharacterEncoding encoding, String skipLinePrefix) throws IOException {
		reader = initLineReader(fileOrStream, encoding, skipLinePrefix);
	}

	/**
	 * Helper method for initializing the <code>LineReader</code>. To be implemented by subclasses
	 * of <code>LineIterator</code>
	 * 
	 * @param fileOrStream
	 * @param encoding
	 * @param skipLinePrefix
	 * @return
	 * @throws IOException
	 */
	public abstract LineReader initLineReader(Object fileOrStream, CharacterEncoding encoding, String skipLinePrefix)
			throws IOException;

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (nextLine != null)
			return true;
		try {
			@SuppressWarnings("unchecked")
			T line = (T) reader.readLine();
			if (line == null)
				return false;
			nextLine = line;
			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		T lineToReturn = nextLine;
		nextLine = null;
		return lineToReturn;
	}

	/**
	 * This method is not implemented for the LineIterator class.
	 * 
	 * @throws UnsupportedOperationException
	 *             if this method is called
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"The remove() operation is not supported by LineIterator and its subclasses.");
	}

}
