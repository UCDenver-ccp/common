package edu.ucdenver.ccp.util.string;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringBufferUtilTest {

	@Test
	public void testPrependToStringBuffer() throws Exception {
		String originalText = "This is some text.";
		String prependedText = "Prepended Text Here.";
		StringBuffer sb = new StringBuffer();
		sb.append(originalText);
		assertEquals(String.format("Should only have originalText at this point."), sb.toString(), originalText);
		sb = StringBufferUtil.prepend(sb, prependedText);
		assertEquals(String.format("Should now have prepended text at the beginning."), sb.toString(), prependedText
				+ originalText);
	}

}
