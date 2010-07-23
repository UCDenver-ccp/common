package edu.ucdenver.ccp.common.xml;

public class XmlUtil {

	/**
	 * Returns the input String with XML special characters converted to their XML-safe formats.
	 * 
	 * @param inputStr
	 * @return
	 */
	public static String convertXmlEscapeCharacters(String inputStr) {
		return inputStr.replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;").replaceAll("<",
				"&lt;").replaceAll(">", "&gt;");
	}
}
