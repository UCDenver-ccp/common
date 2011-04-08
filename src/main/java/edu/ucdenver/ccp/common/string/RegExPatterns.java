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

package edu.ucdenver.ccp.common.string;

public class RegExPatterns {

	/**
	 * Matches a string that contains only digits, but must have at least one digit
	 */
	public static final String HAS_NUMBERS_ONLY = "^\\d+$";

	/**
	 * Matches a string that contains only digits, but may have an optional negative sign at the
	 * beginning
	 */
	public static final String HAS_NUMBERS_ONLY_OPT_NEG = "^-?\\d+$";

	/**
	 * Matches a string that contains only digits with the first digit being a zero, and may have an
	 * optional negative sign at the beginning
	 */
	public static final String HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START = "^-?0\\d*$";

	/**
	 * Matches a tab character.
	 */
	public static final String TAB = "\\t";

	public static final String IS_NUMBER_OR_HYPHEN = "(\\-|\\d+)";

	/**
	 * For matching a method name, e.g. getText() or getId()
	 */
	public static final String GETTER_METHOD_NAME_PATTERN = "^get[A-Z]";

	/**
	 * Matches a letter or a number. Differs from \w in that it does not match underscore
	 */
	public static final String IS_LETTER_OR_NUMBER = "[a-zA-Z0-9]";

	/**
	 * Matches the pipe character
	 */
	public static final String PIPE = "\\|";

	private RegExPatterns() {
		// this class should not be instantiated
	}

	/**
	 * Returns a pattern for matching n consecutive digits
	 * 
	 * @param n
	 * @return
	 */
	public static final String getNDigitsPattern(int n) {
		return String.format("\\d{%d}", n);
	}
	
	/**
	 * Prepares a string to be used in a Regular Expression search. This method places a backslash in front of
	 * characters that need to be escaped in order to be recognized in a regular expression
	 * 
	 * @param regex
	 *            the String to be converted into a Regular Expression search string
	 * @return the converted String
	 */
	public static String escapeCharacterForRegEx(String regex) {
		final String[] escapedChars = { "\\", "(", ")", "{", "}", "+", "*", "?", "[", "]", "^", "$", ".", "|" };
		final String escape = "\\";
		int splitIndex = -1;
		String tmpStr = "";
		for (int i = 0; i < escapedChars.length; i++) {
			// escape characters for substr
			if ((splitIndex = regex.indexOf(escapedChars[i])) > -1) {
				int fromIndex = 0;
				while (splitIndex > -1) { // check for mulitiple of same escapedChar in regex
					// insert \ in front of the escaped character
					tmpStr = regex.substring(0, splitIndex) + escape + regex.substring(splitIndex);
					regex = tmpStr;
					fromIndex = splitIndex + 2;
					splitIndex = regex.indexOf(escapedChars[i], fromIndex);
				}
			}
		}
		return regex;
	}


}
