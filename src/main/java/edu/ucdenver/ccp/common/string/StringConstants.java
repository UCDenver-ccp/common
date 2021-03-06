package edu.ucdenver.ccp.common.string;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

/**
 * When possible, use Unicode representation for the String Constants:
 * http://unicode.org/Public/UNIDATA/NamesList.txt
 * 
 * @author Bill Baumgartner
 * 
 */
public class StringConstants {
	public static final String BLANK = "";
	public static final String NEW_LINE = "\n";
	
	private static final char LINE_FEED_CHAR = 0x0A;
	public static final String LINE_FEED = Character.toString(LINE_FEED_CHAR);
	private static final char CARRIAGE_RETURN_CHAR = 0x0D;
	public static final String CARRIAGE_RETURN = Character.toString(CARRIAGE_RETURN_CHAR);
	
	public static final String TAB = "\u0009";

	public static final String SPACE = "\u0020";
	public static final String EXCLAMATION_MARK = "\u0021";
	public static final char QUOTATION_MARK_CHAR = 0x22;
	public static final String QUOTATION_MARK = Character.toString(QUOTATION_MARK_CHAR);

	public static final String NUMBER_SIGN = "\u0023";
	public static final String DOLLAR_SIGN = "\u0024";
	public static final String PERCENT_SIGN = "\u0025";
	public static final String AMPERSAND = "\u0026";
	public static final char AMPERSAND_CHAR = 0x26;
	public static final String APOSTROPHE = "\u0027";
	public static final char APOSTROPHE_CHAR = 0x27;
	public static final String LEFT_PARENTHESIS = "\u0028";
	public static final String RIGHT_PARENTHESIS = "\u0029";
	public static final String ASTERISK = "\u002A";
	public static final String PLUS_SIGN = "\u002B";
	public static final String COMMA = "\u002C";
	public static final String HYPHEN_MINUS = "\u002D";
	public static final String FULL_STOP = "\u002E";
	public static final String PERIOD = FULL_STOP;
	public static final String SOLIDUS = "\u002F";
	public static final String FORWARD_SLASH = "\u002F";
	public static final String DIGIT_ZERO = "\u0030";
	public static final String DIGIT_ONE = "\u0031";
	public static final String DIGIT_TWO = "\u0032";
	public static final String DIGIT_THREE = "\u0033";
	public static final String DIGIT_FOUR = "\u0034";
	public static final String DIGIT_FIVE = "\u0035";
	public static final String DIGIT_SIX = "\u0036";
	public static final String DIGIT_SEVEN = "\u0037";
	public static final String DIGIT_EIGHT = "\u0038";
	public static final String DIGIT_NINE = "\u0039";
	public static final String COLON = "\u003A";
	public static final String SEMICOLON = "\u003B";
	public static final String LESS_THAN_SIGN = "\u003C";
	public static final char LESS_THAN_SIGN_CHAR = 0x3C;
	public static final String EQUALS_SIGN = "\u003D";
	public static final String GREATER_THAN_SIGN = "\u003E";
	public static final char GREATER_THAN_SIGN_CHAR = 0x3E;
	public static final String QUESTION_MARK = "\u003F";
	public static final String COMMERCIAL_AT = "\u0040";
	public static final String LATIN_CAPITAL_LETTER_A = "\u0041";
	public static final String LATIN_CAPITAL_LETTER_B = "\u0042";
	public static final String LATIN_CAPITAL_LETTER_C = "\u0043";
	public static final String LATIN_CAPITAL_LETTER_D = "\u0044";
	public static final String LATIN_CAPITAL_LETTER_E = "\u0045";
	public static final String LATIN_CAPITAL_LETTER_F = "\u0046";
	public static final String LATIN_CAPITAL_LETTER_G = "\u0047";
	public static final String LATIN_CAPITAL_LETTER_H = "\u0048";
	public static final String LATIN_CAPITAL_LETTER_I = "\u0049";
	public static final String LATIN_CAPITAL_LETTER_J = "\u004A";
	public static final String LATIN_CAPITAL_LETTER_K = "\u004B";
	public static final String LATIN_CAPITAL_LETTER_L = "\u004C";
	public static final String LATIN_CAPITAL_LETTER_M = "\u004D";
	public static final String LATIN_CAPITAL_LETTER_N = "\u004E";
	public static final String LATIN_CAPITAL_LETTER_O = "\u004F";
	public static final String LATIN_CAPITAL_LETTER_P = "\u0050";
	public static final String LATIN_CAPITAL_LETTER_Q = "\u0051";
	public static final String LATIN_CAPITAL_LETTER_R = "\u0052";
	public static final String LATIN_CAPITAL_LETTER_S = "\u0053";
	public static final String LATIN_CAPITAL_LETTER_T = "\u0054";
	public static final String LATIN_CAPITAL_LETTER_U = "\u0055";
	public static final String LATIN_CAPITAL_LETTER_V = "\u0056";
	public static final String LATIN_CAPITAL_LETTER_W = "\u0057";
	public static final String LATIN_CAPITAL_LETTER_X = "\u0058";
	public static final String LATIN_CAPITAL_LETTER_Y = "\u0059";
	public static final String LATIN_CAPITAL_LETTER_Z = "\u005A";
	public static final String LEFT_SQUARE_BRACKET = "\u005B";
	// public static final String REVERSE_SOLIDUS = "\u005C\u005C";
	public static final char REVERSE_SOLIDUS = 0x5C;

	public static final String RIGHT_SQUARE_BRACKET = "\u005D";
	public static final String CIRCUMFLEX_ACCENT = "\u005E";
	public static final String LOW_LINE = "\u005F";
	public static final String GRAVE_ACCENT = "\u0060";
	public static final String LATIN_SMALL_LETTER_A = "\u0061";
	public static final String LATIN_SMALL_LETTER_B = "\u0062";
	public static final String LATIN_SMALL_LETTER_C = "\u0063";
	public static final String LATIN_SMALL_LETTER_D = "\u0064";
	public static final String LATIN_SMALL_LETTER_E = "\u0065";
	public static final String LATIN_SMALL_LETTER_F = "\u0066";
	public static final String LATIN_SMALL_LETTER_G = "\u0067";
	public static final String LATIN_SMALL_LETTER_H = "\u0068";
	public static final String LATIN_SMALL_LETTER_I = "\u0069";
	public static final String LATIN_SMALL_LETTER_J = "\u006A";
	public static final String LATIN_SMALL_LETTER_K = "\u006B";
	public static final String LATIN_SMALL_LETTER_L = "\u006C";
	public static final String LATIN_SMALL_LETTER_M = "\u006D";
	public static final String LATIN_SMALL_LETTER_N = "\u006E";
	public static final String LATIN_SMALL_LETTER_O = "\u006F";
	public static final String LATIN_SMALL_LETTER_P = "\u0070";
	public static final String LATIN_SMALL_LETTER_Q = "\u0071";
	public static final String LATIN_SMALL_LETTER_R = "\u0072";
	public static final String LATIN_SMALL_LETTER_S = "\u0073";
	public static final String LATIN_SMALL_LETTER_T = "\u0074";
	public static final String LATIN_SMALL_LETTER_U = "\u0075";
	public static final String LATIN_SMALL_LETTER_V = "\u0076";
	public static final String LATIN_SMALL_LETTER_W = "\u0077";
	public static final String LATIN_SMALL_LETTER_X = "\u0078";
	public static final String LATIN_SMALL_LETTER_Y = "\u0079";
	public static final String LATIN_SMALL_LETTER_Z = "\u007A";
	public static final String LEFT_CURLY_BRACKET = "\u007B";
	public static final String VERTICAL_LINE = "\u007C";
	public static final String RIGHT_CURLY_BRACKET = "\u007D";
	public static final String TILDE = "\u007E";

	public static final String TWO_FORWARD_SLASHES = SOLIDUS + SOLIDUS;
	public static final String POUND_SIGN = NUMBER_SIGN;
	public static final char NULL_CHAR = '\u0000';
	public static final String UNDERSCORE = LOW_LINE;

	// 0009 <control>
	// = CHARACTER_TABULATION
	// = horizontal tabulation (HT), tab
	// 000A <control>
	// = LINE_FEED (LF)
	// = new line (NL), end of line (EOL)
	// 000B <control>
	// = LINE_TABULATION
	// = vertical tabulation (VT)
	// 000C <control>
	// = FORM_FEED (FF)
	// 000D <control>
	// = CARRIAGE_RETURN (CR)
	// 000E <control>
	// = SHIFT_OUT
	// * known as LOCKING_SHIFT_ONE in 8_bit environments
	// 000F <control>
	// = SHIFT_IN
	// * known as LOCKING_SHIFT_ZERO in 8_bit environments
	// 0010 <control>
	// = DATA_LINK_ESCAPE
	// 0011 <control>
	// = DEVICE_CONTROL_ONE
	// 0012 <control>
	// = DEVICE_CONTROL_TWO
	// 0013 <control>
	// = DEVICE_CONTROL_THREE
	// 0014 <control>
	// = DEVICE_CONTROL_FOUR
	// 0015 <control>
	// = NEGATIVE_ACKNOWLEDGE
	// 0016 <control>
	// = SYNCHRONOUS_IDLE
	// 0017 <control>
	// = END_OF_TRANSMISSION_BLOCK
	// 0018 <control>
	// = CANCEL
	// 0019 <control>
	// = END_OF_MEDIUM
	// 001A <control>
	// = SUBSTITUTE
	// x (replacement character _ FFFD)
	// 001B <control>
	// = ESCAPE
	// 001C <control>
	// = INFORMATION_SEPARATOR_FOUR
	// = file separator (FS)
	// 001D <control>
	// = INFORMATION_SEPARATOR_THREE
	// = group separator (GS)
	// 001E <control>
	// = INFORMATION_SEPARATOR_TWO
	// = record separator (RS)
	// 001F <control>
	// = INFORMATION_SEPARATOR_ONE
	// = unit separator (US)
	// @ ASCII punctuation and symbols
	// @+ Based on ISO/IEC 646.

	private StringConstants() {
		// this class should not be instantiated
	}
}
