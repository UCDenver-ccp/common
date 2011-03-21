package edu.ucdenver.ccp.common.string;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.file.SampleUtf8File;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;


public class CodePointUtilTest extends DefaultTestCase {

	private String utf8String;
	
	@Before
	public void setUp() throws IOException {
		utf8String = ClassPathUtil.getContentsFromClasspathResource(SampleUtf8File.class, SampleUtf8File.FILE_NAME, SampleUtf8File.ENCODING);
	}
	
	
	@Test
	public void testConvertCharacterOffsetToCodePointOffset() throws IOException {
		String expectedString = "ABCD";
		assertEquals("Should be ABCD", expectedString, utf8String.substring(19,23));
		
		assertEquals("Expect code point for A to be 16, cha offset = 19",16, CodePointUtil.convertCharacterOffsetToCodePointOffset(utf8String, 19));
	}
	
	@Test
	public void testConvertCodePointToCharOffset() {
		assertEquals("code point 12 should equal char offset 15", 15, CodePointUtil.convertCodePointOffsetToCharOffset(utf8String, 12));
	}
	
	
	
	
}
