package edu.ucdenver.ccp.util.string;

import static edu.ucdenver.ccp.util.string.RegExPatterns.HAS_NUMBERS_ONLY;
import static edu.ucdenver.ccp.util.string.RegExPatterns.HAS_NUMBERS_ONLY_OPT_NEG;
import static edu.ucdenver.ccp.util.string.RegExPatterns.HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START;
import static org.junit.Assert.*;

import org.junit.Test;

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

}
