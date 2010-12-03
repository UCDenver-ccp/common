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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.CharacterEncoding;

public class StringUtil {

	private static final Logger logger = Logger.getLogger(StringUtil.class);

	/**
	 * Returns true if the input string is an integer, false otherwise.
	 * 
	 * @param inputStr
	 * @return
	 */
	public static boolean isInteger(String inputStr) {
		try {
			Integer.parseInt(inputStr);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	/**
	 * Returns true if the input string is a non-negative integer, false otherwise.
	 * 
	 * @param inputStr
	 * @return
	 */
	public static boolean isNonNegativeInteger(String inputStr) {
		int value = -1;

		try {
			value = Integer.parseInt(inputStr);
		} catch (NumberFormatException e) {
			return false;
		}

		return value >= 0;
	}

	/**
	 * Returns true if the input string is a non-negative integer and is not zero
	 * 
	 * @param inputStr
	 * @return
	 */
	public static boolean isIntegerGreaterThanZero(String inputStr) {
		inputStr = inputStr.trim();
		return isNonNegativeInteger(inputStr) && !inputStr.equals(StringConstants.DIGIT_ZERO);
	}

	/**
	 * Returns a String consisting of the input String with specified suffix removed. If the input
	 * String does not end with the specified suffix, an IllegalArgumentException is thrown.
	 * 
	 * @param inputStr
	 * @param suffix
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String removeSuffix(String inputStr, String suffix) throws IllegalArgumentException {
		if (inputStr != null && suffix != null) {
			if (inputStr.endsWith(suffix)) {
				return removeNTrailingCharacters(inputStr, suffix.length());
			}
		}
		throw new IllegalArgumentException(String.format(
				"Invalid input. Cannot remove suffix. Input String \"%s\" does not end with suffix \"%s\"", inputStr,
				suffix));
	}

	/**
	 * Returns a String after removing n characters at the end of the input String
	 * 
	 * @param inputStr
	 * @param n
	 * @return
	 */
	public static String removeNTrailingCharacters(String inputStr, int n) {
		return inputStr.substring(0, inputStr.length() - n);
	}

	/**
	 * Returns a String after removing the final character of the input String
	 * 
	 * @param inputStr
	 * @return
	 */
	public static String removeLastCharacter(String inputStr) {
		return removeNTrailingCharacters(inputStr, 1);
	}

	/**
	 * Replaces one suffix with another on the input String
	 * 
	 * @param inputStr
	 * @param suffix
	 * @param replacementSuffix
	 * @return
	 */
	public static String replaceSuffix(String inputStr, String suffix, String replacementSuffix) {
		return removeSuffix(inputStr, suffix) + replacementSuffix;
	}

	/**
	 * Removes a suffix from the input String if the specified regular expression matches
	 * 
	 * @param inputStr
	 * @param regexStr
	 * @return
	 */
	public static String removeSuffixRegex(String inputStr, String regexStr) {
		if (endsWithRegex(inputStr, regexStr)) {
			regexStr = checkForRegexEndPattern(regexStr);
			Pattern p = Pattern.compile(regexStr);
			Matcher m = p.matcher(inputStr);
			if (m.find())
				return inputStr.substring(0, inputStr.length() - m.group().length());
		}
		throw new IllegalArgumentException(String.format("Cannot remove suffix regex \"%s\" from String \"%s\".",
				regexStr, inputStr));
	}

	private static String checkForRegexEndPattern(String regexStr) {
		if (!regexStr.endsWith("$")) {
			regexStr = regexStr + "$";
		}
		return regexStr;
	}

	/**
	 * Removes a prefix from the input String if the specified regular expression matches
	 * 
	 * @param inputStr
	 * @param regexStr
	 * @return
	 */
	public static String removePrefixRegex(String inputStr, String regexStr) {
		if (startsWithRegex(inputStr, regexStr)) {
			regexStr = checkForRegexStartPattern(regexStr);
			Pattern p = Pattern.compile(regexStr);
			Matcher m = p.matcher(inputStr);
			if (m.find())
				return inputStr.substring(m.group().length());
		}
		throw new IllegalArgumentException(String.format("Cannot remove prefix regex \"%s\" from String \"%s\".",
				regexStr, inputStr));
	}

	private static String checkForRegexStartPattern(String regexStr) {
		if (!regexStr.startsWith("^")) {
			regexStr = "^" + regexStr;
		}
		return regexStr;
	}

	/**
	 * Returns true if the beginning of the inputStr matches the regular expression, false
	 * otherwise.
	 * 
	 * @param inputStr
	 * @param regexStr
	 * @return
	 */
	public static boolean startsWithRegex(String inputStr, String regexStr) {
		regexStr = checkForRegexStartPattern(regexStr);
		return containsRegex(inputStr, regexStr);
	}

	/**
	 * Returns true if the end of the inputStr matches the regular expression, false otherwise
	 * 
	 * @param inputStr
	 * @param regexStr
	 * @return
	 */
	public static boolean endsWithRegex(String inputStr, String regexStr) {
		regexStr = checkForRegexEndPattern(regexStr);
		return containsRegex(inputStr, regexStr);
	}

	/**
	 * Returns true if the input String contains a match to the specified regular expression
	 * 
	 * @param inputStr
	 * @param regexStr
	 * @return
	 */
	public static boolean containsRegex(String inputStr, String regexStr) {
		Pattern p = Pattern.compile(regexStr);
		return p.matcher(inputStr).find();
	}

	/**
	 * Returns true if the input string both starts and ends with the specified regex, false
	 * otherwise
	 * 
	 * @param inputStr
	 * @param regexStr
	 * @return
	 */
	private static boolean startsAndEndsWithRegex(String inputStr, String regexStr) {
		return startsWithRegex(inputStr, regexStr) && endsWithRegex(inputStr, regexStr);
	}

	/**
	 * Converts the input InputStream to a String
	 * 
	 * @param is
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static String convertStream(InputStream is, CharacterEncoding encoding) throws IOException {
		StringWriter sw = new StringWriter();
		try {
			IOUtils.copy(is, sw, encoding.getCharacterSetName());
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(sw);
		}
		return sw.toString();
	}

	/**
	 * Splits the input string, but ignores any delimiters inside a field. For example, you might be
	 * splitting a comma-delimited line but have a field indicated by quotation marks that can
	 * contain a comma. e.g. token 1,token 2,"token, 3 has a comma",token 4
	 * 
	 * @param inputStr
	 * @param delimiters
	 * @return
	 */
	public static String[] splitWithFieldEnclosure(String inputStr, String delimiterRegex,
			String optionalFieldEnclosureRegex) {
		if (optionalFieldEnclosureRegex == null || !containsRegex(inputStr, optionalFieldEnclosureRegex))
			return inputStr.split(delimiterRegex, -1);
		String copyOfInputStr = normalizeInputFields(inputStr, delimiterRegex, optionalFieldEnclosureRegex);
		List<String> tokens = new ArrayList<String>();
		int previousDelimiterEndIndex = 0;
		Matcher matcher = Pattern.compile(delimiterRegex).matcher(copyOfInputStr);
		while (matcher.find()) {
			int delimiterStartIndex = matcher.start();
			int delimiterEndIndex = matcher.end();
			tokens.add(inputStr.substring(previousDelimiterEndIndex, delimiterStartIndex));
			previousDelimiterEndIndex = delimiterEndIndex;
		}
		if (previousDelimiterEndIndex == 0)
			tokens.add(inputStr);
		else
			tokens.add(inputStr.substring(previousDelimiterEndIndex));
		return tokens.toArray(new String[tokens.size()]);
	}

	/**
	 * The version of splitWithFieldEnclosure provides a boolean flag which allows the field
	 * enclosures to be optionally removed. If removeOptionalFieldEnclosure is set to true then the
	 * field enclosures are removed. For example, if the following comma-delimited String is split
	 * using a colon field enclosure: ":1:,:2:,:3:" this method will output [":1:",":2:",":3:"] if
	 * removeOptionalFieldEnclosure = false or ["1","2","3"] if removeOptionalFieldEnclosure = true
	 * 
	 * @param inputStr
	 * @param delimiterRegex
	 * @param optionalFieldEnclosureRegex
	 * @param removeOptionalFieldEnclosure
	 * @return
	 */
	public static String[] splitWithFieldEnclosure(String inputStr, String delimiterRegex,
			String optionalFieldEnclosureRegex, RemoveFieldEnclosures removeOptionalFieldEnclosure) {
		String[] tokens = splitWithFieldEnclosure(inputStr, delimiterRegex, optionalFieldEnclosureRegex);
		if (removeOptionalFieldEnclosure.equals(RemoveFieldEnclosures.TRUE))
			for (int i = 0; i < tokens.length; i++)
				if (StringUtil.startsAndEndsWithRegex(tokens[i], optionalFieldEnclosureRegex)) {
					tokens[i] = StringUtil.removePrefixRegex(tokens[i], optionalFieldEnclosureRegex);
					tokens[i] = StringUtil.removeSuffixRegex(tokens[i], optionalFieldEnclosureRegex);
				}
		return tokens;
	}

	public enum RemoveFieldEnclosures {
		TRUE, FALSE;
	}

	/**
	 * Delimits the input String as described in splitWithFieldEnclosure, but returns only non-empty
	 * tokens
	 * 
	 * @param inputStr
	 * @param delimiterRegex
	 * @param optionalFieldEnclosureRegex
	 * @return
	 */
	public static List<String> delimitAndTrim(String inputStr, String delimiterRegex,
			String optionalFieldEnclosureRegex, RemoveFieldEnclosures removeOptionalFieldEnclosure) {
		String[] tokens = splitWithFieldEnclosure(inputStr, delimiterRegex, optionalFieldEnclosureRegex,
				removeOptionalFieldEnclosure);
		List<String> nonEmptyTokens = new ArrayList<String>();
		for (String token : tokens)
			if (!token.trim().isEmpty())
				nonEmptyTokens.add(token.trim());
		return nonEmptyTokens;
	}

	/**
	 * Given an inputStr with possible delimiters hidden inside fields, this method returns a String
	 * where the fields have been normalized (turned into a string of zero's)
	 * 
	 * @param inputStr
	 * @param delimiterRegex
	 * @param optionalFieldEnclosureCharacter
	 * @return
	 */
	private static String normalizeInputFields(String inputStr, String delimiterRegex,
			String optionalFieldEnclosureRegex) {
		String tempReplacementStr = StringConstants.DIGIT_ZERO;
		if (delimiterRegex.contains(tempReplacementStr))
			throw new IllegalArgumentException(
					"Warning. Potential error exists in current use of splitWithFieldDelimiter(). "
							+ "This method uses the character '%s' internally during a split procedure. The "
							+ "input delimiter contains the character '%s'. Therefore there is a potential conflict. "
							+ "Please use a different delimiter. Exiting...");
		String copyOfInputStr = inputStr;
		Pattern fieldPattern = Pattern.compile(optionalFieldEnclosureRegex + ".*?" + optionalFieldEnclosureRegex);
		Matcher matcher = fieldPattern.matcher(inputStr);
		while (matcher.find()) {
			copyOfInputStr = copyOfInputStr.replace(matcher.group(),
					createRepeatingString(StringConstants.DIGIT_ZERO, matcher.group().length()));
		}
		return copyOfInputStr;
	}

	/**
	 * Returns a sequence of the input string repeated length times.
	 * 
	 * @param inputStr
	 * @param length
	 * @return
	 */
	public static String createRepeatingString(String inputStr, int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(inputStr);
		}
		return sb.toString();
	}

	/**
	 * Delimit values with provided delimiter and strip surrounding single characters (ex:
	 * double-quotes) .
	 * 
	 * @param values
	 *            to delimit
	 * @param delim
	 *            delimiter.
	 * 
	 * @return converted values.
	 */
	public static Collection<String> delimitAndTrim(String values, String delim) {
		if (values == null)
			return Collections.<String> emptyList();

		List<String> list = Arrays.asList(values.split(delim));
		List<String> trimmed = new ArrayList<String>();
		for (String v : list)
			if (v.trim().length() > 0) {
				trimmed.add(v.substring(1, v.length() - 1));
				char firstChar = v.charAt(0);
				char lastChar = v.charAt(v.length() - 1);
				if (firstChar != lastChar)
					logger.warn(String
							.format("The StringUtil.delimitAndTrim(values,delim) method may not be behaving as you expect. " +
									"This method trims the first and last characters of each element after delimiting the " +
									"input String (useful for removing things like surrounding quotation marks). This " +
									"warning is being displayed because the first and last characters being trimmed " +
									"do not match (first='%s'; last='%s').",
									firstChar, lastChar));
			}
		return trimmed;
	}

	public static String stripNonAscii(String input) throws UnsupportedEncodingException {
		// In UTF-8, you can look at the bit pattern of a byte to tell
		// if it is part of a
		// single byte character, a two-byte character, or more.
		// A byte that starts with a zero bit is a one-byte character.
		// Bytes that start with a 1, are multi byte and the number of ones
		// before a zero is the number of bytes in the multi-byte characters.
		// See the examples in the code below.
		// http://canonical.org/~kragen/strlen-utf8.html
		// http://en.wikipedia.org/wiki/UTF-8

		byte dst[] = input.getBytes("UTF-8");
		StringBuffer buf = new StringBuffer();
		int index = 0;
		while (index < dst.length) {
			int cleanByte = dst[index] & 0xFF;

			// 0xxx xxxx: 1 byte
			if (cleanByte < 128 && cleanByte >= 0) {
				// its safe, just add it to the string
				buf.append((char) cleanByte);
				index++;
			}
			// 10xx xxxx is a continuation byte, and is dealt with below.
			// 110x xxxx: 2 byte
			else if (cleanByte >= 128 + 64 && cleanByte < 128 + 64 + 32) {
				buf.append("?");
				index += 2; // consume this byte and its continuation byte
			}
			// 1110 xxxx: 3 byte
			else if (cleanByte >= 128 + 64 + 32 && cleanByte < 128 + 64 + 32 + 16) {
				buf.append('?');
				index += 3; // consume this byte and its continuation bytes
			}
			// 1111 0xxx: 4 byte
			else if (cleanByte >= 128 + 64 + 32 + 16 && cleanByte < 128 + 64 + 32 + 16 + 8) {
				buf.append('?');
				index += 4; // consume this byte and its continuation bytes
			} else {
				buf.append('?');
				index += 1;
			}
		}
		return buf.toString();
	}

}
