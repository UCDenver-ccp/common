package edu.ucdenver.ccp.common.file;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

/**
 * This enum ties together various character encodings with canonical file suffixes used to indicate
 * the encoding of a particular file.
 * 
 * @author Center for Computational Pharmacology; ccpsupport@ucdenver.edu
 * 
 */
public enum CharacterEncoding {

	/**
	 * Signifies the ASCII character set
	 */
	US_ASCII("US-ASCII", ".ascii"),
	/**
	 * Signifies UTF-8 character set
	 */
	UTF_8("UTF-8", ".utf8"),
	/**
	 * Signifies ISO-8859-1 character set
	 */
	ISO_8859_1("ISO-8859-1", ".iso8859-1");

	/**
	 * The official name of the character set
	 */
	private final String characterSetName;

	/**
	 * A standard file suffix to use with this particular character set
	 */
	private final String fileSuffix;

	/**
	 * Private constructor for initializing CharacterEncoding enum elements
	 * 
	 * @param characterSetName
	 * @param fileSuffix
	 */
	private CharacterEncoding(String characterSetName, String fileSuffix) {
		this.characterSetName = characterSetName;
		this.fileSuffix = fileSuffix;
	}

	/**
	 * 
	 * @return the name of the character set
	 */
	public String getCharacterSetName() {
		return characterSetName;
	}

	/**
	 * @return the file suffix associated with this particular character set
	 */
	public String getFileSuffix() {
		return fileSuffix;
	}

	/**
	 * @return a <code>CharsetEncoder</code> used to encode characters. Fails loudly if an encoding
	 *         discrepancy is observed.
	 */
	public CharsetEncoder getEncoder() {
		return Charset.forName(getCharacterSetName()).newEncoder().onMalformedInput(CodingErrorAction.REPORT)
				.onUnmappableCharacter(CodingErrorAction.REPORT);
	}

	/**
	 * 
	 * @return a <code>CharsetDecoder</code> used to decode characters. Fails loudly if an encoding
	 *         discrepancy is observed.
	 */
	public CharsetDecoder getDecoder() {
		return Charset.forName(getCharacterSetName()).newDecoder().onMalformedInput(CodingErrorAction.REPORT)
				.onUnmappableCharacter(CodingErrorAction.REPORT);
	}

	/**
	 * Given a file name as input, this method returns the character encoding-specific file name by
	 * appending the encoding-specific file suffix if necessary
	 * 
	 * @param fileName
	 * @param encoding
	 * @return
	 */
	public static String getEncodingSpecificFileName(String fileName, CharacterEncoding encoding) {
		String encodingSpecificFileName = fileName;
		String fileSuffix = encoding.getFileSuffix();
		if (!hasEncodingSpecificFileName(fileName, encoding))
			encodingSpecificFileName = fileName + fileSuffix;
		return encodingSpecificFileName;
	}

	/**
	 * Given an input File reference, this method returns a File reference with the appropriate
	 * character encoding-specific file suffix
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 */
	public static File getEncodingSpecificFile(File file, CharacterEncoding encoding) {
		File encodingSpecificFile = file;
		String fileSuffix = encoding.getFileSuffix();
		if (!hasEncodingSpecificFileName(file, encoding))
			encodingSpecificFile = new File(file.getAbsolutePath() + fileSuffix);
		return encodingSpecificFile;
	}

	/**
	 * Returns true if the input file name has the expected character encoding-specific file suffix
	 * 
	 * @param fileName
	 * @param encoding
	 * @return
	 */
	public static boolean hasEncodingSpecificFileName(String fileName, CharacterEncoding encoding) {
		return fileName.endsWith(encoding.getFileSuffix());
	}

	/**
	 * Returns true if the name of the input file reference has the expected character
	 * encoding-specific file suffix
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 */
	public static boolean hasEncodingSpecificFileName(File file, CharacterEncoding encoding) {
		return hasEncodingSpecificFileName(file.getName(), encoding);
	}

	/**
	 * @param encodingStr
	 * @return
	 */
	public static CharacterEncoding getEncoding(String encodingStr) {
		return CharacterEncoding.valueOf(encodingStr.replaceAll("-", "_").toUpperCase());
	}

}
