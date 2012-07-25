/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
	
	
	/**
	 * Returns a substring of the input string based on the input code points
	 * 
	 * @param inputStr
	 * @param codePointStart
	 * @param codePointEnd
	 * @return
	 */
	public static String substringByCodePoint(String inputStr, int codePointStart, int codePointEnd) {
		int charOffsetStart = convertCodePointOffsetToCharOffset(inputStr, codePointStart);
		int charOffsetEnd = convertCodePointOffsetToCharOffset(inputStr, codePointEnd);
		return inputStr.substring(charOffsetStart, charOffsetEnd);
	}
	
	
//	public static int[] convertCodePointSpanToCharacterSpan(String inputStr, int codePointSpanStart, int codePointSpanEnd) {
//		
//	}
//	
//	public static int[] convertCharacterSpanToCodePointSpan(String inputStr, int charSpanStart, int charSpanEnd) {
//		
//	}
	

}
