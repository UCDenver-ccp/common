package edu.ucdenver.ccp.common.string;

import static edu.ucdenver.ccp.common.string.RegExPatterns.HAS_NUMBERS_ONLY_OPT_NEG;
import static edu.ucdenver.ccp.common.string.RegExPatterns.HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START;
import static edu.ucdenver.ccp.common.string.RegExPatterns.HAS_NUMBERS_ONLY;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class StringUtil {
	/**
	 * Returns true if the input string is an integer, false otherwise.
	 * 
	 * @param inputStr
	 * @return
	 */
	public static boolean isInteger(String inputStr) {
		if (inputStr != null && inputStr.equals(StringConstants.DIGIT_ZERO))
			return true;
		return inputStr == null ? false : (inputStr.matches(HAS_NUMBERS_ONLY_OPT_NEG) && !inputStr
				.matches(HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
	}

	/**
	 * Returns true if the input string is a non-negative integer, false otherwise.
	 * 
	 * @param inputStr
	 * @return
	 */
	public static boolean isNonNegativeInteger(String inputStr) {
		if (inputStr != null && inputStr.equals(StringConstants.DIGIT_ZERO))
			return true;
		return inputStr == null ? false : (inputStr.matches(HAS_NUMBERS_ONLY) && !inputStr
				.matches(HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
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
	 * Returns true if the beginning of the inputStr matches the regular expression, false
	 * otherwise.
	 * 
	 * @param inputStr
	 * @param regexStr
	 * @return
	 */
	public static boolean startsWithRegex(String inputStr, String regexStr) {
		if (!regexStr.startsWith("^")) {
			regexStr = "^" + regexStr;
		}
		return containsRegex(inputStr, regexStr);
	}

	
	public static boolean containsRegex(String inputStr, String regexStr) {
		Pattern p = Pattern.compile(regexStr);
		return p.matcher(inputStr).find();
	}
	
	/**
	 * Converts the input InputStream to a String
	 * 
	 * @param is
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static String convertStream(InputStream is, String encoding) throws IOException {
		StringWriter sw = new StringWriter();
		try {
			IOUtils.copy(is, sw, encoding);
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
			return inputStr.split(delimiterRegex,-1);
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
		Pattern fieldPattern = Pattern.compile(optionalFieldEnclosureRegex + ".*?"
				+ optionalFieldEnclosureRegex);
		Matcher matcher = fieldPattern.matcher(inputStr);
		while (matcher.find()) {
			copyOfInputStr = copyOfInputStr.replace(matcher.group(), createRepeatingString(StringConstants.DIGIT_ZERO,
					matcher.group().length()));
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

}
