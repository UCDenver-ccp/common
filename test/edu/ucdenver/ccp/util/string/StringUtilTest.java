package edu.ucdenver.ccp.util.string;

import static edu.ucdenver.ccp.util.string.RegExPatterns.getNDigitsPattern;
import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilTest {

	@Test
	public void testIsInteger_validPositiveInput() throws Exception {
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
		assertFalse(StringUtil.isInteger("01"));
		assertFalse(StringUtil.isInteger("this is not a number"));
		assertFalse(StringUtil.isInteger("3.14159"));
		assertFalse(StringUtil.isInteger("-09876543211"));
		assertFalse(StringUtil.isInteger("-000005"));
		assertFalse(StringUtil.isInteger(""));
		assertFalse(StringUtil.isInteger(null));
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
}
