package edu.ucdenver.ccp.common.string;

import java.util.Iterator;

public class CodePointUtil {

	/**
	 * Taken from http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5003547
	 * 
	 * @param s
	 * @return
	 */
	public static Iterable<Integer> getCodePoints(final String s) {
		return new Iterable<Integer>() {
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					int nextIndex = 0;

					public boolean hasNext() {
						return nextIndex < s.length();
					}

					public Integer next() {
						int result = s.codePointAt(nextIndex);
						nextIndex += Character.charCount(result);
						return result;
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	/**
	 * Computes the character offset for the specified code point offset in the
	 * input string
	 * 
	 * @param inputStr
	 * @param codePointOffset
	 * @return the character offset for the specified code point offset
	 */
	public static int convertCodePointOffsetToCharOffset(String inputStr,
			int codePointOffset) {
		return inputStr.offsetByCodePoints(0, codePointOffset);
	}

	/**
	 * Computes the code point offset for the specified character offset in the
	 * input string
	 * 
	 * @param inputStr
	 * @param charOffset
	 * @return
	 */
	public static int convertCharacterOffsetToCodePointOffset(String inputStr,
			int charOffset) {
		return Character.codePointCount(inputStr, 0, charOffset);
	}
	
	
//	public static int[] convertCodePointSpanToCharacterSpan(String inputStr, int codePointSpanStart, int codePointSpanEnd) {
//		
//	}
//	
//	public static int[] convertCharacterSpanToCodePointSpan(String inputStr, int charSpanStart, int charSpanEnd) {
//		
//	}
	

}
