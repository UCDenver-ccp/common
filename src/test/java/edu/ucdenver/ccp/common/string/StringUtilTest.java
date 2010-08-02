package edu.ucdenver.ccp.common.string;

import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.ucdenver.ccp.common.string.RegExPatterns;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.common.string.StringUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class StringUtilTest extends DefaultTestCase {

	@Test
	public void testIsInteger_validPositiveInput() throws Exception {
		assertTrue(StringUtil.isInteger("0"));
		assertTrue(StringUtil.isInteger("1"));
		assertTrue(StringUtil.isInteger("10"));
		assertTrue(StringUtil.isInteger("1234567890"));
		assertTrue(StringUtil.isInteger("9876543211"));
	}

	@Test
	public void testIsInteger_validNegativeInput() throws Exception {
		assertTrue(StringUtil.isInteger("-1"));
		assertTrue(StringUtil.isInteger("-10"));
		assertTrue(StringUtil.isInteger("-1234567890"));
		assertTrue(StringUtil.isInteger("-9876543211"));
	}

	@Test
	public void testIsInteger_invalidInput() throws Exception {
		assertFalse(StringUtil.isInteger("-0"));
		assertFalse(StringUtil.isInteger("01"));
		assertFalse(StringUtil.isInteger("this is not a number"));
		assertFalse(StringUtil.isInteger("3.14159"));
		assertFalse(StringUtil.isInteger("-09876543211"));
		assertFalse(StringUtil.isInteger("-000005"));
		assertFalse(StringUtil.isInteger(""));
		assertFalse(StringUtil.isInteger(null));
	}
	
	@Test
	public void testIsNonNegativeInteger_validNonNegativeInput() throws Exception {
		assertTrue(StringUtil.isNonNegativeInteger("1"));
		assertTrue(StringUtil.isNonNegativeInteger("10"));
		assertTrue(StringUtil.isNonNegativeInteger("1234567890"));
		assertTrue(StringUtil.isNonNegativeInteger("9876543211"));
	}
	
	
	@Test
	public void testIsNonNegativeInteger_negativeInput() throws Exception {
		assertFalse(StringUtil.isNonNegativeInteger("-1"));
		assertFalse(StringUtil.isNonNegativeInteger("-10"));
		assertFalse(StringUtil.isNonNegativeInteger("-1234567890"));
		assertFalse(StringUtil.isNonNegativeInteger("-9876543211"));
	}
	
	@Test
	public void testIsNonNegativeInteger_invalidInput() throws Exception {
		assertFalse(StringUtil.isNonNegativeInteger("-0"));
		assertFalse(StringUtil.isNonNegativeInteger("01"));
		assertFalse(StringUtil.isNonNegativeInteger("this is not a number"));
		assertFalse(StringUtil.isNonNegativeInteger("3.14159"));
		assertFalse(StringUtil.isNonNegativeInteger("-09876543211"));
		assertFalse(StringUtil.isNonNegativeInteger("-000005"));
		assertFalse(StringUtil.isNonNegativeInteger(""));
		assertFalse(StringUtil.isNonNegativeInteger(null));
	}

	@Test
	public void testRemoveSuffix_ValidInput() throws Exception {
		assertEquals("Suffix should be stripped.", "myFile.txt", StringUtil.removeSuffix("myFile.txt.gz", ".gz"));
		assertEquals("Suffix should be stripped.", "myFile", StringUtil.removeSuffix("myFile.txt.gz.abc.123.xyz-654",
				".txt.gz.abc.123.xyz-654"));
	}

	@Test
	public void testRemoveSuffix_EmptyInput() throws Exception {
		assertEquals("Suffix should be stripped.", "myFile.txt", StringUtil.removeSuffix("myFile.txt", ""));
		assertEquals("Suffix should be stripped.", "", StringUtil.removeSuffix("", ""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveSuffix_NullInputString() throws Exception {
		StringUtil.removeSuffix(null, ".gz");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveSuffix_NullSuffix() throws Exception {
		StringUtil.removeSuffix("File.txt", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveSuffix_InputStringDoesNotHaveSuffix() throws Exception {
		StringUtil.removeSuffix("File.txt.zip", ".gz");
	}

	@Test
	public void testReplaceSuffix_ValidInput() throws Exception {
		assertEquals("Suffix should be replaced with .tar", "myTarball.tar", StringUtil.replaceSuffix("myTarball.tgz",
				".tgz", ".tar"));
	}

	@Test
	public void testStartsWithRegex() throws Exception {
		assertTrue(StringUtil.startsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(4)));
		assertTrue(StringUtil.startsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(3)));
		assertTrue(StringUtil.startsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(2)));
		assertTrue(StringUtil.startsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(1)));
		assertTrue(StringUtil.startsWithRegex("2010-04-06", "^" + RegExPatterns.getNDigitsPattern(4)));

		assertFalse(StringUtil.startsWithRegex("2010-04-06", RegExPatterns.getNDigitsPattern(5)));
	}

	@Test
	public void testContainsRegex() throws Exception {
		assertTrue(StringUtil.containsRegex("2010-04-06", RegExPatterns.getNDigitsPattern(4)));
		assertFalse(StringUtil.containsRegex("2010-04-06", RegExPatterns.getNDigitsPattern(5)));
	}

	@Test
	public void testCreateRepeatingString() throws Exception {
		String expectedStr = StringConstants.AMPERSAND + StringConstants.AMPERSAND + StringConstants.AMPERSAND;
		assertEquals(String.format("String should contain 3 ampersands"), expectedStr, StringUtil
				.createRepeatingString(StringConstants.AMPERSAND, 3));

		assertEquals(String.format("String should contain 6 ampersands"), expectedStr + expectedStr, StringUtil
				.createRepeatingString(StringConstants.AMPERSAND + StringConstants.AMPERSAND, 3));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSplitWithFieldDelimiter_ZeroInDelimiter() throws Exception {
		StringUtil.splitWithFieldEnclosure("\"", String.format(".%s", StringConstants.DIGIT_ZERO), Character
				.toString(StringConstants.QUOTATION_MARK));
	}

	@Test
	public void testSplitWithFieldDelimiter() throws Exception {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, vol.1-17\",1,10.1172/JCI101100,PMC548872,0,,live";
		String[] expectedTokens = new String[] { "J Clin Invest", "0021-9738", "1558-8238", "1940", "19",
				"\"Index, vol.1-17\"", "1", "10.1172/JCI101100", "PMC548872", "0", "", "live" };
		assertArrayEquals(String.format("One token should include a comma"), expectedTokens, StringUtil
				.splitWithFieldEnclosure(inputStr, StringConstants.COMMA, Character
						.toString(StringConstants.QUOTATION_MARK)));
	}
	
	@Test
	public void testSplitWithFieldDelimiter_EmptyColumnsAtEnd() throws Exception {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, vol.1-17\",1,10.1172/JCI101100,PMC548872,0,,";
		String[] expectedTokens = new String[] { "J Clin Invest", "0021-9738", "1558-8238", "1940", "19",
				"\"Index, vol.1-17\"", "1", "10.1172/JCI101100", "PMC548872", "0", "", "" };
		assertArrayEquals(String.format("One token should include a comma"), expectedTokens, StringUtil
				.splitWithFieldEnclosure(inputStr, StringConstants.COMMA, Character
						.toString(StringConstants.QUOTATION_MARK)));
	}

	@Test
	public void testSplitWithFieldDelimiter_NoColumns() throws Exception {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, vol.1-17\",1,10.1172/JCI101100,PMC548872,0,,live";
		assertArrayEquals(String.format("One token should include a comma"), new String[] { inputStr }, StringUtil
				.splitWithFieldEnclosure(inputStr, StringConstants.SEMICOLON, Character
						.toString(StringConstants.QUOTATION_MARK)));
	}

	@Test
	public void testSplitWithFieldDelimiter_FieldDelimiterNotPresentInText() throws Exception {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, vol.1-17\",1,10.1172/JCI101100,PMC548872,0,,live";
		String[] expectedTokens = new String[] { "J Clin Invest", "0021-9738", "1558-8238", "1940", "19", "\"Index",
				" vol.1-17\"", "1", "10.1172/JCI101100", "PMC548872", "0", "", "live" };
		assertArrayEquals(String.format("One token should include a comma"), expectedTokens, StringUtil
				.splitWithFieldEnclosure(inputStr, StringConstants.COMMA, StringConstants.SEMICOLON));
	}

	@Test
	public void testSplitWithFieldDelimiter_FieldDelimiterNull() throws Exception {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,\"Index, vol.1-17\",1,10.1172/JCI101100,PMC548872,0,,live";
		String[] expectedTokens = new String[] { "J Clin Invest", "0021-9738", "1558-8238", "1940", "19", "\"Index",
				" vol.1-17\"", "1", "10.1172/JCI101100", "PMC548872", "0", "", "live" };
		assertArrayEquals(String.format("One token should include a comma"), expectedTokens, StringUtil
				.splitWithFieldEnclosure(inputStr, StringConstants.COMMA, null));
	}

	@Test
	public void testSplitWithFieldDelimiter_FieldDelimiterIsRegexSpecialCharacter() throws Exception {
		String inputStr = "J Clin Invest,0021-9738,1558-8238,1940,19,*Index, vol.1-17*,1,10.1172/JCI101100,PMC548872,0,,live";
		String[] expectedTokens = new String[] { "J Clin Invest", "0021-9738", "1558-8238", "1940", "19",
				"*Index, vol.1-17*", "1", "10.1172/JCI101100", "PMC548872", "0", "", "live" };
		assertArrayEquals(String.format("One token should include a comma"), expectedTokens, StringUtil
				.splitWithFieldEnclosure(inputStr, StringConstants.COMMA, "\\*"));
	}

}
