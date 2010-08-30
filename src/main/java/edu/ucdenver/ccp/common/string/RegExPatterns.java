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

}
