package edu.ucdenver.ccp.common.string;

import static edu.ucdenver.ccp.common.string.RegExPatterns.GETTER_METHOD_NAME_PATTERN;
import static edu.ucdenver.ccp.common.string.RegExPatterns.HAS_NUMBERS_ONLY;
import static edu.ucdenver.ccp.common.string.RegExPatterns.HAS_NUMBERS_ONLY_OPT_NEG;
import static edu.ucdenver.ccp.common.string.RegExPatterns.HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START;
import static edu.ucdenver.ccp.common.string.RegExPatterns.getNDigitsPattern;
import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;
import static edu.ucdenver.ccp.common.string.RegExPatterns.IS_NUMBER_OR_HYPHEN;

public class RegExPatternsTest {

	@Test
	public void test_has_numbers_only_pattern_match_expected() throws Exception {
		assertTrue(matchesEntireInput("123456", HAS_NUMBERS_ONLY));
		assertTrue(matchesEntireInput("0", HAS_NUMBERS_ONLY));
		assertTrue(matchesEntireInput("000203005005060448692", HAS_NUMBERS_ONLY));
	}

	@Test
	public void test_has_numbers_only_pattern_match_not_expected() throws Exception {
		assertFalse(matchesEntireInput("-0", HAS_NUMBERS_ONLY));
		assertFalse(matchesEntireInput(" 0", HAS_NUMBERS_ONLY));
		assertFalse(matchesEntireInput("1234b56436", HAS_NUMBERS_ONLY));
	}

	@Test
	public void test_has_numbers_only_opt_neg_pattern_match_expected() throws Exception {
		assertTrue(matchesEntireInput("123456", HAS_NUMBERS_ONLY_OPT_NEG));
		assertTrue(matchesEntireInput("0", HAS_NUMBERS_ONLY_OPT_NEG));
		assertTrue(matchesEntireInput("000203005005060448692", HAS_NUMBERS_ONLY_OPT_NEG));
		assertTrue(matchesEntireInput("-123456", HAS_NUMBERS_ONLY_OPT_NEG));
		assertTrue(matchesEntireInput("-0", HAS_NUMBERS_ONLY_OPT_NEG));
		assertTrue(matchesEntireInput("-000203005005060448692", HAS_NUMBERS_ONLY_OPT_NEG));

	}

	@Test
	public void test_has_numbers_only_opt_neg_pattern_match_not_expected() throws Exception {
		assertFalse(matchesEntireInput(" 0", HAS_NUMBERS_ONLY_OPT_NEG));
		assertFalse(matchesEntireInput("- 0", HAS_NUMBERS_ONLY_OPT_NEG));
		assertFalse(matchesEntireInput("-1234b56436", HAS_NUMBERS_ONLY_OPT_NEG));
	}

	@Test
	public void test_has_numbers_only_opt_neg_zero_start_pattern_match_expected() throws Exception {
		assertTrue(matchesEntireInput("0123456", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertTrue(matchesEntireInput("0", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertTrue(matchesEntireInput("000203005005060448692", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertTrue(matchesEntireInput("-0123456", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertTrue(matchesEntireInput("-0", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertTrue(matchesEntireInput("-000203005005060448692", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));

	}

	@Test
	public void test_has_numbers_only_opt_neg_zero_start_pattern_match_not_expected() throws Exception {
		assertFalse(matchesEntireInput(" 0", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertFalse(matchesEntireInput("-123456", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertFalse(matchesEntireInput("-1234b56436", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
	}

	private boolean matchesEntireInput(String text, String pattern) {
		return text.matches(pattern);
	}

	@Test
	public void test_getter_method_name_pattern() throws Exception {
		assertTrue(matchesPartialInput("getText", GETTER_METHOD_NAME_PATTERN));
		assertTrue(matchesPartialInput("getID", GETTER_METHOD_NAME_PATTERN));
		assertFalse(matchesPartialInput("getter", GETTER_METHOD_NAME_PATTERN));
		assertFalse(matchesPartialInput("abcText", GETTER_METHOD_NAME_PATTERN));
	}

	private boolean matchesPartialInput(String text, String pattern) {
		Pattern p = Pattern.compile(pattern);
		return p.matcher(text).find();
	}

	@Test
	public void testgetNDigitsPattern() throws Exception {
		assertTrue(matchesEntireInput("2010", getNDigitsPattern(4)));
		assertFalse(matchesEntireInput("2010", getNDigitsPattern(0)));
		assertFalse(matchesEntireInput("2010", getNDigitsPattern(3)));
		assertFalse(matchesEntireInput("2010", getNDigitsPattern(5)));

		assertTrue(matchesPartialInput("abc def 546 fjg", getNDigitsPattern(3)));
		assertTrue(matchesPartialInput("abc def 546 fjg", getNDigitsPattern(2)));
		assertTrue(matchesPartialInput("abc def 546 fjg", getNDigitsPattern(1)));
		assertTrue(matchesPartialInput("abc def 546 fjg", getNDigitsPattern(0)));
		assertFalse(matchesPartialInput("abc def 546 fjg", getNDigitsPattern(4)));
	}

	@Test
	public void testIsNumberOrHyphen() throws Exception {
		assertTrue(matchesEntireInput("99", IS_NUMBER_OR_HYPHEN));
		assertTrue(matchesEntireInput("-", IS_NUMBER_OR_HYPHEN));
		assertFalse(matchesEntireInput("-99", IS_NUMBER_OR_HYPHEN));
		assertFalse(matchesEntireInput("abc", IS_NUMBER_OR_HYPHEN));
		assertFalse(matchesEntireInput("9abc", IS_NUMBER_OR_HYPHEN));
		assertFalse(matchesEntireInput("-abc", IS_NUMBER_OR_HYPHEN));
	}

}
