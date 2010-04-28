package edu.ucdenver.ccp.util.string;

import static edu.ucdenver.ccp.util.string.RegExPatterns.HAS_NUMBERS_ONLY_OPT_NEG;
import static edu.ucdenver.ccp.util.string.RegExPatterns.HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START;

import java.util.regex.Pattern;

public class StringUtil {

	/**
	 * Returns true if the input string is an integer, false otherwise.
	 * 
	 * @param inputStr
	 * @return
	 */
	public static boolean isInteger(String inputStr) {
		return inputStr == null ? false : (inputStr.matches(HAS_NUMBERS_ONLY_OPT_NEG) && !inputStr
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
				return inputStr.substring(0, inputStr.length() - suffix.length());
			}
		}
		throw new IllegalArgumentException(String.format(
				"Invalid input. Cannot remove suffix. Input String \"%s\" does not end with suffix \"%s\"", inputStr,
				suffix));
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
		Pattern p = Pattern.compile(regexStr);
		return p.matcher(inputStr).find();
	}

}
