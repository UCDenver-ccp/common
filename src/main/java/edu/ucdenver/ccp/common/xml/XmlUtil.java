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

package edu.ucdenver.ccp.common.xml;

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
		return inputStr.replaceAll(StringConstants.AMPERSAND, AMPERSAND_CODE).replaceAll(
				StringConstants.QUOTATION_MARK, QUOTATION_MARK_CODE).replaceAll(StringConstants.APOSTROPHE,
				APOSTROPHE_CODE).replaceAll(StringConstants.LESS_THAN_SIGN, LESS_THAN_SIGN_CODE).replaceAll(
				StringConstants.GREATER_THAN_SIGN, GREATER_THAN_SIGN_CODE);
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
	 *            the encoding to specify in the XML header
	 * @return an XML heading line
	 */
	public static String getXmlHeader1_0(CharacterEncoding encoding) {
		return String.format("<?xml version=\"1.0\" encoding=\"%s\"?>", encoding.name().replace(
				StringConstants.UNDERSCORE, StringConstants.HYPHEN_MINUS));
	}
}
