package edu.ucdenver.ccp.util.string;

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
}
