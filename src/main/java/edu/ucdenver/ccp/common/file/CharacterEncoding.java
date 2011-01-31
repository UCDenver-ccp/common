package edu.ucdenver.ccp.common.file;

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

}
