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

import static edu.ucdenver.ccp.common.string.StringUtil.decode;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.MalformedInputException;
import java.util.List;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.string.StringUtil.RemoveFieldEnclosures;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class StringUtilTest extends DefaultTestCase {

	@Test
	public void testIsInteger_validPositiveInput() {
		assertTrue(StringUtil.isInteger("0"));
		assertTrue(StringUtil.isInteger("00"));
		assertTrue(StringUtil.isInteger("1"));
		assertTrue(StringUtil.isInteger("10"));
		assertTrue(StringUtil.isInteger("1234567890"));
		assertFalse(StringUtil.isInteger("9876543211"));
	}

	@Test
	public void testIsInteger_validNegativeInput() {
		assertTrue(StringUtil.isInteger("-1"));
		assertTrue(StringUtil.isInteger("-10"));
		assertTrue(StringUtil.isInteger("-1234567890"));
		assertFalse(StringUtil.isInteger("-9876543211"));
	}

	@Test
	public void testIsInteger_invalidInput() {
		assertTrue(StringUtil.isInteger("-0"));
		assertTrue(StringUtil.isInteger("01"));
		assertFalse(StringUtil.isInteger("this is not a number"));
		assertFalse(StringUtil.isInteger("3.14159"));
		assertFalse(StringUtil.isInteger("-09876543211"));
		assertTrue(StringUtil.isInteger("-000005"));
		assertFalse(StringUtil.isInteger(""));
		assertFalse(StringUtil.isInteger(null));
	}

	@Test
	public void testIsIntegerGreaterThanZero() {
		assertTrue(StringUtil.isIntegerGreaterThanZero("1"));
		assertTrue(StringUtil.isIntegerGreaterThanZero("10"));
		assertTrue(StringUtil.isIntegerGreaterThanZero("1234567890"));
		assertFalse(StringUtil.isIntegerGreaterThanZero("9876543211"));
		assertFalse(StringUtil.isIntegerGreaterThanZero("0"));
		assertFalse(StringUtil.isIntegerGreaterThanZero("-1"));
		assertFalse(StringUtil.isIntegerGreaterThanZero("this is not a number"));
	}

	@Test
	public void testIsNonNegativeInteger_validNonNegativeInput() {
		assertTrue(StringUtil.isNonNegativeInteger("1"));
		assertTrue(StringUtil.isNonNegativeInteger("10"));
		assertTrue(StringUtil.isNonNegativeInteger("1234567890"));
		assertFalse(StringUtil.isNonNegativeInteger("9876543211"));
	}

	@Test
	public void testIsNonNegativeInteger_negativeInput() {
		assertFalse(StringUtil.isNonNegativeInteger("-1"));
		assertFalse(StringUtil.isNonNegativeInteger("-10"));
		assertFalse(StringUtil.isNonNegativeInteger("-1234567890"));
		assertFalse(StringUtil.isNonNegativeInteger("-9876543211"));
	}

	@Test
	public void testIsNonNegativeInteger_invalidInput() {
		assertTrue(StringUtil.isNonNegativeInteger("-0"));
		assertTrue(StringUtil.isNonNegativeInteger("01"));
		assertFalse(StringUtil.isNonNegativeInteger("this is not a number"));
		assertFalse(StringUtil.isNonNegativeInteger("3.14159"));
		assertFalse(StringUtil.isNonNegativeInteger("-09876543211"));
		assertFalse(StringUtil.isNonNegativeInteger("-000005"));
		assertFalse(StringUtil.isNonNegativeInteger(""));
		assertFalse(StringUtil.isNonNegativeInteger(null));
	}

	@Test
	public void testRemoveRegexSuffix_ValidInput() {
		assertEquals("Suffix should be stripped.", "myFile",
				StringUtil.removeSuffixRegex("myFile.txt.txt.txt.txt.txt", "(\\.txt)+"));
		assertEquals("Suffix should be stripped.", "myFile", StringUtil.removeSuffixRegex("myFile.tgz", "\\..gz"));
	}

	/**
	 * Checks that StringUtil.removeRegexSuffix() works correctly with $ symbol and removes the last
	 * suffix pattern when valid input is used
	 */

	@Test
	public void testRemoveRegexSuffix_ValidInput2() {
		assertEquals("Suffix should be stripped.", "myFile",
				StringUtil.removeSuffixRegex("myFile.txt.txt.txt.txt.txt", "(\\.txt)+$"));
		assertEquals("Suffix should be stripped.", "myFile", StringUtil.removeSuffixRegex("myFile.tgz", "\\..gz$"));
	}

	/**
	 * Checks that StringUtil.removeRegexSuffix() throws an exception when invalid input is used.
	 */

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveRegexSuffix_InValidInput() {
		StringUtil.removeSuffixRegex("myFile.txt.txt.txt.txt.txt", "(\\.abc)+$");

	}

	@Test
	public void testRemoveRegexSuffix_EmptyInput() {
		assertEquals("Suffix should be stripped.", "myFile.txt.txt.txt.txt.txt",
				StringUtil.removeSuffixRegex("myFile.txt.txt.txt.txt.txt", ""));
		assertEquals("Suffix should be stripped.", "myFile.tgz", StringUtil.removeSuffixRegex("myFile.tgz", ""));
	}

	/**
	 * Checks that StringUtil.removeSuffix() works correctly when valid input is used
	 */
	@Test
	public void testRemoveSuffix_ValidInput() {
		assertEquals("Suffix should be stripped.", "myFile.txt", StringUtil.removeSuffix("myFile.txt.gz", ".gz"));
		assertEquals("Suffix should be stripped.", "myFile",
				StringUtil.removeSuffix("myFile.txt.gz.abc.123.xyz-654", ".txt.gz.abc.123.xyz-654"));
	}

	/**
	 * Checks for expected behavior of StringUtil.removeSuffix() if the suffix string is empty
	 */
	@Test
	public void testRemoveSuffix_EmptyInput() {
		assertEquals("Suffix should be stripped.", "myFile.txt", StringUtil.removeSuffix("myFile.txt", ""));
		assertEquals("Suffix should be stripped.", "", StringUtil.removeSuffix("", ""));
	}

	/**
	 * Checks for proper exception if the input string is null when calling
	 * StringUtil.removeSuffix()
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveSuffix_NullInputString() {
		StringUtil.removeSuffix(null, ".gz");
	}

	/**
	 * Checks for proper exception if the suffix string is null when calling
	 * StringUtil.removeSuffix()
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveSuffix_NullSuffix() {
		StringUtil.removeSuffix("File.txt", null);
	}

	/**
	 * Checks for proper exception if the input string does not end with the suffix requested to be
	 * removed when calling StringUtil.removeSuffix()
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveSuffix_InputStringDoesNotHaveSuffix() {
		StringUtil.removeSuffix("File.txt.zip", ".gz");
	}

	/**
	 * Checks for proper exception if the input string is Empty space when calling
	 * StringUtil.removeSuffix()
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveSuffix_EmptySpace() {
		StringUtil.removeSuffix("", ".gz");
	}

	/**
	 * Checks that StringUtil.Prefix() works correctly when valid input is used
	 */

	@Test
	public void testRemoveRegexPrefix_ValidInput() {
		assertEquals("Suffix should be stripped.", "e.txt.txt.txt.txt.txt",
				StringUtil.removePrefixRegex("myFile.txt.txt.txt.txt.txt", "(m?y?Fil)"));
		assertEquals("Suffix should be stripped.", "gz", StringUtil.removePrefixRegex("myFile.tgz", "my.*?t"));
	}

	/**
	 * Checks for expected behavior of StringUtil.removePrefix() if the Prefix string is empty
	 */
	@Test
	public void testRemoveRegexPrefix_EmptyInput() {
		assertEquals("Prefix should be stripped.", "myFile.txt.txt.txt.txt.txt",
				StringUtil.removePrefixRegex("myFile.txt.txt.txt.txt.txt", ""));
		assertEquals("Prefix should be stripped.", "myFile.tgz", StringUtil.removePrefixRegex("myFile.tgz", ""));
	}

	/**
	 * Checks for proper exception if the input string is null when calling
	 * StringUtil.removePrefix()
	 */
	@Test(expected = NullPointerException.class)
	public void testRemovePrefix_NullInputString() {
		StringUtil.removePrefixRegex(null, ".gz");
	}

	/**
	 * Checks for proper exception if the suffix string is null when calling
	 * StringUtil.removePrefix()
	 */
	@Test(expected = NullPointerException.class)
	public void testRemovePrefix_NullSuffix() {
		StringUtil.removePrefixRegex("File.txt", null);
	}

	/**
	 * Checks for proper exception if the input string does not end with the Prefix requested to be
	 * removed when calling StringUtil.removePrefix()
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemovePrefix_InputStringDoesNotHaveSuffix() {
		StringUtil.removePrefixRegex("File.txt.zip", ".gz");
	}

	/**
	 * Checks for proper exception if the input string is Empty space when calling
	 * StringUtil.removePrefix()
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemovePrefix_EmptySpace() {
		StringUtil.removePrefixRegex("", ".gz");
	}

	@Test
	public void testReplaceSuffix_ValidInput() {
		assertEquals("Suffix should be replaced with .tar", "myTarball.tar",
				StringUtil.replaceSuffix("myTarball.tgz", ".tgz", ".tar"));
	}

	/**
	 * Returns true if the beginning of the inputStr matches the regular expression, false
	 * otherwise.
	 */

	@Test
	public void testStartsWithRegex() {
		assertTrue(StringUtil.startsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(4)));

		assertTrue(StringUtil.startsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(3)));
		assertTrue(StringUtil.startsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(2)));
		assertTrue(StringUtil.startsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(1)));
		assertTrue(StringUtil.startsWithRegex("2010-04-06", "^" + RegExPatterns.getNDigitsPattern(4)));

		assertFalse(StringUtil.startsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(5)));
	}

	@Test(expected = NullPointerException.class)
	public void testStartsWithRegex_NullInputString() {
		StringUtil.startsWithRegex(null, RegExPatterns.getNDigitsPattern(1));
	}

	/**
	 * Tests that StringUtil.startsWithRegex properly returns false if the input string is empty,
	 * regardless of the pattern used (unless the pattern is also empty).
	 */
	@Test
	public void testStartsWithRegex_EmptySpaceInputString() {
		assertFalse(StringUtil.startsWithRegex("", RegExPatterns.getNDigitsPattern(1)));
		assertTrue(StringUtil.startsWithRegex("", ""));
	}

	/**
	 * Returns true if the end of the inputStr matches the regular expression, false otherwise
	 */

	@Test
	public void testendsWithRegex() {
		assertTrue(StringUtil.endsWithRegex("2010-04-0655", RegExPatterns.getNDigitsPattern(4)));

		assertTrue(StringUtil.endsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(2)));
		assertTrue(StringUtil.endsWithRegex("2010-04-063", RegExPatterns.getNDigitsPattern(3)));
		assertTrue(StringUtil.endsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(1)));

		assertFalse(StringUtil.endsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(5)));

	}

	/**
	 * Returns true if start and the end of the inputStr matches the regular expression, false
	 * otherwise
	 */

	@Test
	public void teststartsAndEndsWithRegex() {

		assertTrue(StringUtil.startsAndEndsWithRegex("2010-04-0633", RegExPatterns.getNDigitsPattern(3)));
		assertTrue(StringUtil.startsAndEndsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(2)));
		assertTrue(StringUtil.startsAndEndsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(1)));

		assertFalse(StringUtil.startsAndEndsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(5)));
		assertFalse(StringUtil.startsAndEndsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(4)));
		assertFalse(StringUtil.startsAndEndsWithRegex("20-04-0610", RegExPatterns.getNDigitsPattern(4)));

	}

	/**
	 * Tests that StringUtil.startsAndEndsWithRegex throws a null pointer exception if the input
	 * string is null
	 */

	@Test(expected = NullPointerException.class)
	public void teststartsAndEndsWithRegex_NullInputString() {
		StringUtil.startsAndEndsWithRegex(null, RegExPatterns.getNDigitsPattern(1));
	}

	/**
	 * Tests that StringUtil.startsAndEndsWithRegex properly returns false if the input string is
	 * empty, regardless of the pattern used (unless the pattern is also empty).
	 */

	@Test
	public void teststartsAndEndsWithRegex_EmptySpaceInputString() {
		assertFalse(StringUtil.startsAndEndsWithRegex("", RegExPatterns.getNDigitsPattern(1)));
		assertTrue(StringUtil.startsAndEndsWithRegex("", ""));
	}

	/**
	 * Returns [1, 2, 3] as expected result if RemoveFieldEnclosures property is set to TRUE)
	 * 
	 */

	@Test
	public void testDelmitAndTrim_ValidInput() {

		StringUtil.delimitAndTrim(":1:,:2:,:3:", ",", ":", RemoveFieldEnclosures.TRUE);

	}

	@Test
	public void testDelimitAndTrim_RealWorldInput() {
		String input = "\"non-protein coding RNA 181\", \"A1BG antisense RNA (non-protein coding)\", \"A1BG antisense RNA 1 (non-protein coding)\"";
		List<String> output = StringUtil.delimitAndTrim(input, StringConstants.COMMA, StringConstants.QUOTATION_MARK,
				RemoveFieldEnclosures.TRUE);

		List<String> expectedOutput = CollectionsUtil.createList("non-protein coding RNA 181",
				"A1BG antisense RNA (non-protein coding)", "A1BG antisense RNA 1 (non-protein coding)");
		
		assertEquals(expectedOutput, output);

	}

	/**
	 * Returns [:1:,:2:,:3:] as expected result if RemoveFieldEnclosures property is set to FALSE)
	 * 
	 */

	@Test
	public void testDelmitAndTrim_WithoutOptionalFieldEnclosure() {

		StringUtil.delimitAndTrim(":1:,:2:,:3:", ",", ":", RemoveFieldEnclosures.FALSE);

	}

	/**
	 * Returns [] as expected result if the input parameter is given as Empty space)
	 * 
	 */

	@Test
	public void testDelmitAndTrim_WithoutInputData() {

		StringUtil.delimitAndTrim("", ",", ":", RemoveFieldEnclosures.TRUE);

	}

	@Test(expected = NullPointerException.class)
	public void testDelmitAndTrim_WithnullData() {

		StringUtil.delimitAndTrim(null, ",", ":", RemoveFieldEnclosures.TRUE);

	}

	/**
	 * Returns [:1:,:2:,:3:] as expected result if invalid delimiterRegex and
	 * optionalFieldEnclosureRegex are given It will try to match the delimiterRegex and
	 * optionalFieldEnclosureRegex in the input string given, when the delimiterRegex and
	 * optionalFieldEnclosureRegex differs from the input string, it will just ignore the
	 * delimiterRegex and optionalFieldEnclosureRegex and will just print the actual input string
	 */

	@Test
	public void testDelmitAndTrim_WithInvaliddelimiterRegex() {

		StringUtil.delimitAndTrim(":1:,:2:,:3:", ";", "-", RemoveFieldEnclosures.TRUE);

	}

	/**
	 * Returns [:1:, :2:, :3:] as expected result if optionalFieldEnclosureRegex property has a
	 * invalid data from actual input string)
	 * 
	 */

	@Test
	public void testDelmitAndTrim_WithEmptyoptionalFieldEnclosure() {

		StringUtil.delimitAndTrim(":1:,:2:,:3:", ",", "", RemoveFieldEnclosures.TRUE);

	}

	/**
	 * Returns [0] as expected result if String delimiterRegex and optionalFieldEnclosureRegex
	 * property has a invalid data from actual input string)
	 * 
	 */

	@Test
	public void testDelmitAndTrim_WithDigit() {

		System.out.println(StringUtil.delimitAndTrim(StringConstants.DIGIT_ZERO, ",", "", RemoveFieldEnclosures.TRUE));

	}

	@Test
	public void testContainsRegex() {
		assertTrue(StringUtil.containsRegex("2010-04-06", RegExPatterns.getNDigitsPattern(4)));
		assertFalse(StringUtil.containsRegex("2010-04-06", RegExPatterns.getNDigitsPattern(5)));
	}

	@Test
	public void testContainsRegex_emptyInputString() {
		assertFalse(StringUtil.containsRegex("", RegExPatterns.getNDigitsPattern(4)));
	}

	@Test
	public void testCreateRepeatingString() {
		String expectedStr = StringConstants.AMPERSAND + StringConstants.AMPERSAND + StringConstants.AMPERSAND;
		assertEquals(String.format("String should contain 3 ampersands"), expectedStr,
				StringUtil.createRepeatingString(StringConstants.AMPERSAND, 3));

		assertEquals(String.format("String should contain 6 ampersands"), expectedStr + expectedStr,
				StringUtil.createRepeatingString(StringConstants.AMPERSAND + StringConstants.AMPERSAND, 3));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSplitWithFieldDelimiter_ZeroInDelimiter() {
		StringUtil.splitWithFieldEnclosure("\"", String.format(".%s", StringConstants.DIGIT_ZERO),
				StringConstants.QUOTATION_MARK);
	}

	@Test
	public void testSplitWithFieldDelimiter() {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, vol.1-17\",1,10.1172/JCI101100,PMC548872,0,,live";
		String[] expectedTokens = new String[] { "J Clin Invest", "0021-9738", "1558-8238", "1940", "19",
				"\"Index, vol.1-17\"", "1", "10.1172/JCI101100", "PMC548872", "0", "", "live" };
		assertArrayEquals(String.format("One token should include a comma"), expectedTokens,
				StringUtil.splitWithFieldEnclosure(inputStr, StringConstants.COMMA, StringConstants.QUOTATION_MARK));
	}

	@Test
	public void testSplitWithFieldDelimiter_IncludeADelimiterInColumn() {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, vol.1-17\"\",1,10.1172/JCI101100,PMC548872,0,,live";
		String[] expectedTokens = new String[] { "J Clin Invest", "0021-9738", "1558-8238", "1940", "19",
				"\"Index, vol.1-17\"\"", "1", "10.1172/JCI101100", "PMC548872", "0", "", "live" };
		assertArrayEquals(String.format("One token should include a comma"), expectedTokens,
				StringUtil.splitWithFieldEnclosure(inputStr, StringConstants.COMMA, StringConstants.QUOTATION_MARK));
	}

	
	@Test
	public void testSplitWithFieldDelimiter_IncludeAPairOfDelimitersInColumn() {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, \"vol.1-17\"\",1,10.1172/JCI101100,PMC548872,0,,live";
		String[] expectedTokens = new String[] { "J Clin Invest", "0021-9738", "1558-8238", "1940", "19",
				"\"Index, \"vol.1-17\"\"", "1", "10.1172/JCI101100", "PMC548872", "0", "", "live" };
		assertArrayEquals(String.format("One token should include a comma"), expectedTokens,
				StringUtil.splitWithFieldEnclosure(inputStr, StringConstants.COMMA, StringConstants.QUOTATION_MARK));
	}
	
	
	@Test
	public void testSplitWithFieldDelimiter_EmptyColumnsAtEnd() {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, vol.1-17\",1,10.1172/JCI101100,PMC548872,0,,";
		String[] expectedTokens = new String[] { "J Clin Invest", "0021-9738", "1558-8238", "1940", "19",
				"\"Index, vol.1-17\"", "1", "10.1172/JCI101100", "PMC548872", "0", "", "" };
		assertArrayEquals(String.format("One token should include a comma"), expectedTokens,
				StringUtil.splitWithFieldEnclosure(inputStr, StringConstants.COMMA, StringConstants.QUOTATION_MARK));
	}

	@Test
	public void testSplitWithFieldDelimiter_NoColumns() {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, vol.1-17\",1,10.1172/JCI101100,PMC548872,0,,live";
		assertArrayEquals(String.format("One token should include a comma"), new String[] { inputStr },
				StringUtil.splitWithFieldEnclosure(inputStr, StringConstants.SEMICOLON, StringConstants.QUOTATION_MARK));
	}

	@Test
	public void testSplitWithFieldDelimiter_FieldDelimiterNotPresentInText() {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, vol.1-17\",1,10.1172/JCI101100,PMC548872,0,,live";
		String[] expectedTokens = new String[] { "J Clin Invest", "0021-9738", "1558-8238", "1940", "19", "\"Index",
				" vol.1-17\"", "1", "10.1172/JCI101100", "PMC548872", "0", "", "live" };
		assertArrayEquals(String.format("One token should include a comma"), expectedTokens,
				StringUtil.splitWithFieldEnclosure(inputStr, StringConstants.COMMA, StringConstants.SEMICOLON));
	}

	@Test
	public void testSplitWithFieldDelimiter_FieldDelimiterNull() {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, vol.1-17\",1,10.1172/JCI101100,PMC548872,0,,live";
		String[] expectedTokens = new String[] { "J Clin Invest", "0021-9738", "1558-8238", "1940", "19", "\"Index",
				" vol.1-17\"", "1", "10.1172/JCI101100", "PMC548872", "0", "", "live" };
		assertArrayEquals(String.format("One token should include a comma"), expectedTokens,
				StringUtil.splitWithFieldEnclosure(inputStr, StringConstants.COMMA, null));
	}

	@Test
	public void testSplitWithFieldDelimiter_FieldDelimiterIsRegexSpecialCharacter() {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,*Index, vol.1-17*,1,10.1172/JCI101100,PMC548872,0,,live";
		String[] expectedTokens = new String[] { "J Clin Invest", "0021-9738", "1558-8238", "1940", "19",
				"*Index, vol.1-17*", "1", "10.1172/JCI101100", "PMC548872", "0", "", "live" };
		assertArrayEquals(String.format("One token should include a comma"), expectedTokens,
				StringUtil.splitWithFieldEnclosure(inputStr, StringConstants.COMMA, "\\*"));
	}

	@Test
	public void testStripNonAscii() {
		try {

			String none = "simple word";

			// String one = "\u0031"; // 31
			String o_umlaut = "\u00f6"; // c3-b6 o with diaeresis
			String devanagari_one = "\u0967"; // e0-a5-0a7

			String one3byte = devanagari_one;
			String one3byteStripped = "?";

			String two3byte = devanagari_one + devanagari_one;
			String two3byteStripped = "??";

			String one2byte = o_umlaut;
			String one2byteStripped = "?";

			String two2byte = o_umlaut + o_umlaut;
			String two2byteStripped = "??";

			String twoAnd3 = o_umlaut + devanagari_one;
			String twoAnd3Stripped = "??";

			String threeAnd2 = devanagari_one + o_umlaut;
			String threeAnd2Stripped = "??";

			String mixed = devanagari_one + "foo and " + o_umlaut + " bar" + devanagari_one;
			String mixedStripped = "?foo and ? bar?";

			String realData = "We thank Richelle Strom for generating the F2 intercross mice.";
			String realDataStripped = "We thank Richelle Strom for generating the F2 intercross mice.";
			// 01234567890123456789012345678901234567890123456789012345678901

			assertTrue(StringUtil.stripNonAscii("").equals(""));
			assertTrue(StringUtil.stripNonAscii(none).equals(none));
			assertTrue(StringUtil.stripNonAscii(one2byte).equals(one2byteStripped));
			assertTrue(StringUtil.stripNonAscii(one3byte).equals(one3byteStripped));
			assertTrue(StringUtil.stripNonAscii(two3byte).equals(two3byteStripped));
			assertTrue(StringUtil.stripNonAscii(two2byte).equals(two2byteStripped));
			assertTrue(StringUtil.stripNonAscii(twoAnd3).equals(twoAnd3Stripped));
			assertTrue(StringUtil.stripNonAscii(threeAnd2).equals(threeAnd2Stripped));
			assertTrue(StringUtil.stripNonAscii(mixed).equals(mixedStripped));
			assertTrue(StringUtil.stripNonAscii(realData).equals(realDataStripped));

		} catch (java.io.UnsupportedEncodingException x) {
			System.err.println("error:" + x);
			x.printStackTrace();
		}

	}

	@Test
	public void testDelimitAndTrim_WithTrailingDelimiter() {
		String inputStr = "\"D015430\",";
		List<String> expectedTokens = CollectionsUtil.createList("\"D015430\"");
		assertEquals(String.format("One token should be returned"), expectedTokens, StringUtil.delimitAndTrim(inputStr,
				StringConstants.COMMA, StringConstants.QUOTATION_MARK, RemoveFieldEnclosures.FALSE));
	}

	@Test
	public void testDelimitAndTrim_WithTrailingDelimiter_RemoveFieldEnclosures() {
		String inputStr = "\"D015430\",";
		List<String> expectedTokens = CollectionsUtil.createList("D015430");
		assertEquals(String.format("One token should be returned"), expectedTokens, StringUtil.delimitAndTrim(inputStr,
				StringConstants.COMMA, StringConstants.QUOTATION_MARK, RemoveFieldEnclosures.TRUE));
	}

	@Test
	public void testByteArrayToString() throws Exception {
		String asciiString = "naive";
		String utf8String = "nai\u0308ve";

		byte[] asciiByteArray = asciiString.getBytes();
		assertEquals("ASCII bytes should be able to be read using ASCII encoding", asciiString,
				decode(asciiByteArray, CharacterEncoding.US_ASCII));
		assertEquals("ASCII bytes should be able to be read using UTF-8 encoding", asciiString,
				decode(asciiByteArray, CharacterEncoding.UTF_8));
		assertEquals("UTF-8 bytes should be able to be read using UTF-8 encoding", utf8String,
				decode(utf8String.getBytes(CharacterEncoding.UTF_8.getCharacterSetName()), CharacterEncoding.UTF_8));
	}

	@Test(expected = MalformedInputException.class)
	public void testByteArrayToString_WithEncodingMismatch() throws Exception {
		String utf8String = "nai\u0308ve";
		StringUtil.decode(utf8String.getBytes(CharacterEncoding.UTF_8.getCharacterSetName()),
				CharacterEncoding.US_ASCII);
	}

	/*
	 * JUnit recipe
	 * 
	 * 1. Set up 2. Declare the expected results 3. Exercise the unit under test 4. Get the actual
	 * results 5. Assert that the actual results match the expected results
	 */

	/**
	 * Tests that removeLastCharacter(String) works as expected with valid input
	 */
	@Test
	public void testRemoveLastCharacter_validInput() {
		String expectedstr = "xy";
		String inputStr = "xyz";
		String actualValue = StringUtil.removeLastCharacter(inputStr);
		assertEquals("Expected str to be xy", expectedstr, actualValue);
	}

	/**
	 * Tests that removeLastCharacter(null) behaves as expected
	 * 
	 * @return
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveLastCharacter_nullInput() {
		String inputStr = null;
		String actualValue = StringUtil.removeLastCharacter(inputStr);

	}

	/**
	 * Tests that removeLastCharacter("") behaves as expected
	 * 
	 * @return
	 */
	@Test(expected = StringIndexOutOfBoundsException.class)
	public void testRemoveLastCharacter_emptyInput() {
		String inputStr = "";
		String actualValue = StringUtil.removeLastCharacter(inputStr);

	}

	/**
	 * Tests that removeLastCharacter("a") behaves as expected
	 */
	@Test
	public void testRemoveLastCharacter_singleCharacterInput() {
		String expectedstr = "";
		String inputStr = "a";
		String actualValue = StringUtil.removeLastCharacter(inputStr);
		assertEquals("Expected str to be Empty space", expectedstr, actualValue);

	}

	@Test
	public void containsAstralCharsFalse() {
		assertFalse(StringUtil.containsAstralChars("a"));
	}

	@Test
	public void containsAstralCharsTrue() {
		assertTrue(StringUtil.containsAstralChars(String.valueOf(Character.toChars(0x10000))));
	}
}
