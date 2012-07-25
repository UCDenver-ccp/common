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

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.ucdenver.ccp.common.file.CharacterEncoding;

/**
 * Abstract class for creating an iterator over the lines from some collection (e.g. a file)
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
	protected final LineReader<T> reader;

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
	public abstract LineReader<T> initLineReader(Object fileOrStream, CharacterEncoding encoding, String skipLinePrefix)
			throws IOException;

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (nextLine != null)
			return true;
		try {
			T line = reader.readLine();
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
	
	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
