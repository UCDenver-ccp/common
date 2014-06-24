package edu.ucdenver.ccp.common.string;

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
	
	
	public static final String SPACE = "\\s";
	
	public static final String IS_PUNCTUATION = "\\p{Punct}";

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
