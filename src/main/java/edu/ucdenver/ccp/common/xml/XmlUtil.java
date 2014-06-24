package edu.ucdenver.ccp.common.xml;

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

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.string.StringConstants;

/**
 * Utility class for dealing with XML content
 * 
 * @author bill
 * 
 */
public class XmlUtil {

	/**
	 * XML code for >
	 */
	public static final String GREATER_THAN_SIGN_CODE = "&gt;";

	/**
	 * XML code for <
	 */
	public static final String LESS_THAN_SIGN_CODE = "&lt;";

	/**
	 * XML code for '
	 */
	public static final String APOSTROPHE_CODE = "&apos;";

	/**
	 * XML code for "
	 */
	public static final String QUOTATION_MARK_CODE = "&quot;";

	/**
	 * XML code for &
	 */
	public static final String AMPERSAND_CODE = "&amp;";

	/**
	 * Returns the input String with XML special characters converted to their XML-safe formats, e.g
	 * & --> &amp;
	 * 
	 * @param inputStr
	 *            the {@link String} to be processed
	 * @return the input string with all XML special characters replaced by their corresponding XML
	 *         codes.
	 */
	public static String convertXmlEscapeCharacters(String inputStr) {
		return inputStr.replaceAll(StringConstants.AMPERSAND, AMPERSAND_CODE)
				.replaceAll(StringConstants.QUOTATION_MARK, QUOTATION_MARK_CODE)
				.replaceAll(StringConstants.APOSTROPHE, APOSTROPHE_CODE)
				.replaceAll(StringConstants.LESS_THAN_SIGN, LESS_THAN_SIGN_CODE)
				.replaceAll(StringConstants.GREATER_THAN_SIGN, GREATER_THAN_SIGN_CODE);
	}

	/**
	 * Converts the input character into a {@link String}, replacing it with its corresponding XML
	 * code if necessary, e.g. & --> &amp;
	 * 
	 * @param c
	 *            the character to process
	 * @return a {@link String} representation of the input character, with XML code replacement if
	 *         necessary
	 */
	public static String escapeForXml(char c) {
		switch (c) {
		case StringConstants.AMPERSAND_CHAR:
			return AMPERSAND_CODE;
		case StringConstants.APOSTROPHE_CHAR:
			return APOSTROPHE_CODE;
		case StringConstants.QUOTATION_MARK_CHAR:
			return QUOTATION_MARK_CODE;
		case StringConstants.LESS_THAN_SIGN_CHAR:
			return LESS_THAN_SIGN_CODE;
		case StringConstants.GREATER_THAN_SIGN_CHAR:
			return GREATER_THAN_SIGN_CODE;
		default:
			return Character.toString(c);
		}
	}

	/**
	 * Returns an XML header, e.g.
	 * 
	 * <pre>
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * </pre>
	 * 
	 * @param encoding
	 * @return
	 */
	public static String getXmlHeader1_0(CharacterEncoding encoding) {
		return getXmlHeader1_0(encoding.getCharacterSetName());
	}

	/**
	 * Returns an XML header, e.g.
	 * 
	 * <pre>
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * </pre>
	 * 
	 * @param encoding
	 *            the encoding to specify in the XML header
	 * @return an XML heading line
	 */
	public static String getXmlHeader1_0(String encoding) {
		return String.format("<?xml version=\"1.0\" encoding=\"%s\"?>",
				encoding.replaceAll(StringConstants.UNDERSCORE, StringConstants.HYPHEN_MINUS));
	}
}
