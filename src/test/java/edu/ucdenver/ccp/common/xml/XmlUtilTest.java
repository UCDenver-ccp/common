package edu.ucdenver.ccp.common.xml;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ucdenver.ccp.common.xml.XmlUtil;

public class XmlUtilTest {

	@Test
	public void testConvertXmlEscapeCharacters_quote() throws Exception {
		String inputText = "This phrase has \"quotation marks\" in it.";
		String expectedText = "This phrase has &quot;quotation marks&quot; in it.";
		assertEquals(String.format("Should have converted quotes."), expectedText, XmlUtil
				.convertXmlEscapeCharacters(inputText));
	}
	
	@Test
	public void testConvertXmlEscapeCharacters_all() throws Exception {
		String inputText = "This 'phrase has everything in it\" & <more>.";
		String expectedText = "This &apos;phrase has everything in it&quot; &amp; &lt;more&gt;.";
		assertEquals(String.format("Should have converted everything."), expectedText, XmlUtil
				.convertXmlEscapeCharacters(inputText));
	}
}
