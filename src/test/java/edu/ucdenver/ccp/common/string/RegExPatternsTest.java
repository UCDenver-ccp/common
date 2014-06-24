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

import static edu.ucdenver.ccp.common.string.RegExPatterns.GETTER_METHOD_NAME_PATTERN;
import static edu.ucdenver.ccp.common.string.RegExPatterns.HAS_NUMBERS_ONLY;
import static edu.ucdenver.ccp.common.string.RegExPatterns.HAS_NUMBERS_ONLY_OPT_NEG;
import static edu.ucdenver.ccp.common.string.RegExPatterns.HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START;
import static edu.ucdenver.ccp.common.string.RegExPatterns.IS_LETTER_OR_NUMBER;
import static edu.ucdenver.ccp.common.string.RegExPatterns.IS_NUMBER_OR_HYPHEN;
import static edu.ucdenver.ccp.common.string.RegExPatterns.PIPE;
import static edu.ucdenver.ccp.common.string.RegExPatterns.getNDigitsPattern;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

public class RegExPatternsTest {

	@Test
	public void test_has_numbers_only_pattern_match_expected() {
		assertTrue(matchesEntireInput("123456", HAS_NUMBERS_ONLY));
		assertTrue(matchesEntireInput("0", HAS_NUMBERS_ONLY));
		assertTrue(matchesEntireInput("000203005005060448692", HAS_NUMBERS_ONLY));
	}

	@Test
	public void test_has_numbers_only_pattern_match_not_expected() {
		assertFalse(matchesEntireInput("-0", HAS_NUMBERS_ONLY));
		assertFalse(matchesEntireInput(" 0", HAS_NUMBERS_ONLY));
		assertFalse(matchesEntireInput("1234b56436", HAS_NUMBERS_ONLY));
	}

	@Test
	public void test_has_numbers_only_opt_neg_pattern_match_expected() {
		assertTrue(matchesEntireInput("123456", HAS_NUMBERS_ONLY_OPT_NEG));
		assertTrue(matchesEntireInput("0", HAS_NUMBERS_ONLY_OPT_NEG));
		assertTrue(matchesEntireInput("000203005005060448692", HAS_NUMBERS_ONLY_OPT_NEG));
		assertTrue(matchesEntireInput("-123456", HAS_NUMBERS_ONLY_OPT_NEG));
		assertTrue(matchesEntireInput("-0", HAS_NUMBERS_ONLY_OPT_NEG));
		assertTrue(matchesEntireInput("-000203005005060448692", HAS_NUMBERS_ONLY_OPT_NEG));

	}

	@Test
	public void test_has_numbers_only_opt_neg_pattern_match_not_expected() {
		assertFalse(matchesEntireInput(" 0", HAS_NUMBERS_ONLY_OPT_NEG));
		assertFalse(matchesEntireInput("- 0", HAS_NUMBERS_ONLY_OPT_NEG));
		assertFalse(matchesEntireInput("-1234b56436", HAS_NUMBERS_ONLY_OPT_NEG));
	}

	@Test
	public void test_has_numbers_only_opt_neg_zero_start_pattern_match_expected() {
		assertTrue(matchesEntireInput("0123456", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertTrue(matchesEntireInput("0", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertTrue(matchesEntireInput("000203005005060448692", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertTrue(matchesEntireInput("-0123456", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertTrue(matchesEntireInput("-0", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertTrue(matchesEntireInput("-000203005005060448692", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));

	}

	@Test
	public void test_has_numbers_only_opt_neg_zero_start_pattern_match_not_expected() {
		assertFalse(matchesEntireInput(" 0", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertFalse(matchesEntireInput("-123456", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
		assertFalse(matchesEntireInput("-1234b56436", HAS_NUMBERS_ONLY_OPT_NEG_ZERO_START));
	}

	private boolean matchesEntireInput(String text, String pattern) {
		return text.matches(pattern);
	}

	@Test
	public void test_getter_method_name_pattern() {
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
	public void testgetNDigitsPattern() {
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
	public void testIsNumberOrHyphen() {
		assertTrue(matchesEntireInput("99", IS_NUMBER_OR_HYPHEN));
		assertTrue(matchesEntireInput("-", IS_NUMBER_OR_HYPHEN));
		assertFalse(matchesEntireInput("-99", IS_NUMBER_OR_HYPHEN));
		assertFalse(matchesEntireInput("abc", IS_NUMBER_OR_HYPHEN));
		assertFalse(matchesEntireInput("9abc", IS_NUMBER_OR_HYPHEN));
		assertFalse(matchesEntireInput("-abc", IS_NUMBER_OR_HYPHEN));
	}

	@Test
	public void testIsLetterOrNumber() {
		assertTrue(matchesEntireInput("a", IS_LETTER_OR_NUMBER));
		assertTrue(matchesEntireInput("0", IS_LETTER_OR_NUMBER));
		assertFalse(matchesEntireInput("_", IS_LETTER_OR_NUMBER));
	}
	
	@Test
	public void testMatchesPipe() {
		assertTrue(StringConstants.VERTICAL_LINE.matches(PIPE));
	}

}
