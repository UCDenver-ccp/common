package edu.ucdenver.ccp.util.string;

import static edu.ucdenver.ccp.util.string.RegExPatterns.HAS_NUMBERS_ONLY_OPT_NEG;
import static edu.ucdenver.ccp.util.string.RegExPatterns.HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START;

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

}
