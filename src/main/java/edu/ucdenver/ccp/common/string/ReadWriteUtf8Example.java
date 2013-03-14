package edu.ucdenver.ccp.common.string;

import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileOutputStream;
import java.nio.charset.Charset;



/**
 * This class demonstrates issues when reading and writing
 * astral or supplementary UTF-8 characters, those with 
 * encodings > FFFF (encodings, not unicode codepoints).
 * Unlike base-plane characters that take two bytes and
 * can be represented in a single Java Character, astral-plane
 * characters take more of both bytes and characters.
 * This has, AFAIK, the side-effect of maing a single code
 * point (when astral), look like two.
 *
 * ** important ** 
 *  compile like this: javac -encoding UTF-8 ReadWriteUtf8.java
 * 
 * The source below includes a UTF-8 character, \u2013 (hex), but
 * it won't compile properly without the -encoding flag as above.
 *
 * Exercise:
 *     Run this program and notice that the output file, 
 * one codepoint, is three bytes long. Notice that the 
 * output tells you it's two characters long.
 *     Run od -x on it and see that the hex number written
 * is E28093 (remember byte-order).
 *     Google and see that the UTF-8 encoding, xE28092 is
 * not the same as the unicode code pointnumber, 2013.
 *     Read the code carefully and notice that all the file
 * read or write opens include the encoding.
 *
 */
public class ReadWriteUtf8Example {

	public static void main(String args[]) {
		BufferedWriter bw = null;
		BufferedReader br = null;
		try {

			// WRITE
			File newFile = new File("en-dash");
			OutputStream fos = new FileOutputStream(newFile);
			bw = new BufferedWriter(
					new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder()) );
			bw.write("\u2013");
			bw.close();

			// READ
	 		PrintStream out = new PrintStream(System.out, true, "UTF-8");
			br = new BufferedReader(
				new InputStreamReader(new FileInputStream(newFile), Charset.forName("UTF-8") ));
	
			while (br.ready()) {
				String s = br.readLine();
				out.println(s);			

				// TEST
				if (s.equals("\u2013")) {
					out.println("yay" + s.length());
				}

				// Wrong output:
				char[] charArray = s.toCharArray();
				System.out.println("char array:" + charArray.length + "  \"" + charArray[0] + "\"");

				// No such beast:
				//char[] charArrayUTF8 = s.toCharArray("UTF-8");
				//System.out.println("char array:" + charArrayUTF8.length + "  \"" + charArrayUTF8[0] + charArrayUTF8[1] + "\""  );

				// Doesn't look right either.
				byte[] byteArray = s.getBytes();
				System.out.println("byte array:" + byteArray.length + "  \"" + byteArray[0] + "\"");

				// This gets the UTF-8 encoding (not unicode code-point).
				byte[] byteArrayUTF8 = s.getBytes("UTF-8");
				System.out.println("UTF8 byte array length:" + byteArrayUTF8.length  );
				System.out.println("UTF8 byte array:" + byteArrayUTF8.length + "  \"" 
					+ byteArrayUTF8[0] + 
					+ byteArrayUTF8[1] + 
					+ byteArrayUTF8[2] + 
					"\"");

				// This (falsely) gets you  the character in the default encoding
				// (usually mac roman).
				System.out.println(" char:" + s.charAt(0) );
	
				// This gets you the unicode code point in decimal, 8211, hex 2013
				// UTF-8 encoding implied or at least dealt-with.
				System.out.println("Unicode: (dec)" + s.codePointAt(0));
			}
			br.close();
		}
		catch (UnsupportedEncodingException x) {
			System.err.println("encoding error:" + x);
		}
		catch (IOException x) {
			System.err.println("i/o error:" + x);
		}
		finally {
			try {
				bw.close();
				br.close();
			}
			catch(IOException x) {
			}
		}
	}

}
