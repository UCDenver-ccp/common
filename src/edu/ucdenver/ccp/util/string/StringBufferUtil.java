package edu.ucdenver.ccp.util.string;

public class StringBufferUtil {

	/**
	 * Inserts the input String at the beginning of the StringBuffer text. (Note: this method is
	 * likely slow as insert causes a copy of the entire StringBuffer)
	 * 
	 * @param sb
	 * @param s
	 * @return
	 */
	public static StringBuffer prepend(StringBuffer sb, String s) {
		return sb.insert(0, s);
	}
}
