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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.CharacterEncoding;

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
	
	public StreamLineIterator(File inputFile, CharacterEncoding encoding) throws IOException {
		this(inputFile, encoding, null);
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
