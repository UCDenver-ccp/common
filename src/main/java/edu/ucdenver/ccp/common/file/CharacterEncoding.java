package edu.ucdenver.ccp.common.file;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * This enum ties together various character encodings with canonical file suffixes used to indicate
 * the encoding of a particular file.
 * 
 * @author Center for Computational Pharmacology; ccpsupport@ucdenver.edu
 * 
 */
public enum CharacterEncoding {

	US_ASCII("US-ASCII", ".ascii"), UTF_8("UTF-8", ".utf8"), ISO_8859_1("ISO-8859-1", ".iso8859-1");

	private final String characterSetName;
	private final String fileSuffix;

	private CharacterEncoding(String characterSetName, String fileSuffix) {
		this.characterSetName = characterSetName;
		this.fileSuffix = fileSuffix;
	}

	public String getCharacterSetName() {
		return characterSetName;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	public CharsetEncoder getEncoder() {
		return Charset.forName(getCharacterSetName()).newEncoder();
	}

	public CharsetDecoder getDecoder() {
		return Charset.forName(getCharacterSetName()).newDecoder();
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
		String fileSuffix = encoding.getFileSuffix();
		if (!hasEncodingSpecificFileName(fileName, encoding))
			fileName = fileName + fileSuffix;
		return fileName;
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
		String fileSuffix = encoding.getFileSuffix();
		if (!hasEncodingSpecificFileName(file, encoding))
			file = new File(file.getAbsolutePath() + fileSuffix);
		return file;
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

}
