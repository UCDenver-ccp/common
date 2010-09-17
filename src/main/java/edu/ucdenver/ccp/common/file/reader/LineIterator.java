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

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.reader.LineReader.Line;


public abstract class LineIterator<T extends Line> implements Iterator<T> {
private static final Logger logger = Logger.getLogger(LineIterator.class);
	
	protected T nextLine = null;
	protected final LineReader reader;
	
	public LineIterator(Object fileOrStream, String encoding, String skipLinePrefix) throws IOException {
		reader = initLineReader(fileOrStream, encoding, skipLinePrefix);
	}
	
	public abstract LineReader initLineReader(Object fileOrStream, String encoding, String skipLinePrefix) throws IOException;

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

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		T lineToReturn = nextLine;
		nextLine = null;
		return lineToReturn;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("The remove() operation is not supported by LineIterator and its subclasses.");
	}
	
}
