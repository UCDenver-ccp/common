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
package edu.ucdenver.ccp.common.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;

import edu.ucdenver.ccp.common.file.CharacterEncoding;

/**
 * A utility class with the goal of providing methods that properly use/enforce character encoding
 * with regard to Input/Output Streams.
 * 
 * @author bill
 * 
 */
public class StreamUtil {

	/**
	 * Returns an <code>InputStream</code> that properly enforces character encoding
	 * 
	 * @param is
	 * @param encoding
	 * @return
	 */
	public static InputStream getEncodingSafeInputStream(InputStream is, CharacterEncoding encoding) {
		return new ReaderInputStream(new InputStreamReader(is, encoding.getDecoder()));
	}

	/**
	 * Returns an <code>InputStream</code> from a file that properly enforces character encoding
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream getEncodingSafeInputStream(File file, CharacterEncoding encoding)
			throws FileNotFoundException {
		return getEncodingSafeInputStream(new FileInputStream(file), encoding);
	}

	/**
	 * Returns an <code>OutputStream</code> that properly enforces character encoding
	 * 
	 * @param os
	 * @param encoding
	 * @return
	 */
	public static OutputStream getEncodingSafeOutputStream(OutputStream os, CharacterEncoding encoding) {
		return new WriterOutputStream(new OutputStreamWriter(os, encoding.getEncoder()));
	}

	/**
	 * Returns an <code>OutputStream</code> from a file that properly enforces character encoding
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 * @throws FileNotFoundException
	 */
	public static OutputStream getEncodingSafeOutputStream(File file, CharacterEncoding encoding)
			throws FileNotFoundException {
		return getEncodingSafeOutputStream(new FileOutputStream(file), encoding);
	}

	/**
	 * Returns the input <code>InputStreamReader</code> to a <code>String</code>
	 * 
	 * @param isr
	 * @return
	 * @throws IOException
	 */
	public static String toString(InputStreamReader isr) throws IOException {
		StringBuilder sb = new StringBuilder();
		try {
			Reader reader = new BufferedReader(isr);
			int ch;
			while ((ch = reader.read()) > -1)
				sb.append((char) ch);
			return sb.toString();
		} finally {
			if (isr != null)
				isr.close();
		}
	}

}
