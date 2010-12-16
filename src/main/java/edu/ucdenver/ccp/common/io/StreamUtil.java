package edu.ucdenver.ccp.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;

import edu.ucdenver.ccp.common.file.CharacterEncoding;

/**
 * A utility class with the goal of providing methods that properly use/enforce character encoding
 * with regard to Input/Output Streams.
 * 
 * @author bill
 * 
 */
public class StreamUtil {

	/**
	 * Returns an <code>InputStream</code> that properly enforces character encoding
	 * 
	 * @param is
	 * @param encoding
	 * @return
	 */
	public static InputStream getEncodingSafeInputStream(InputStream is, CharacterEncoding encoding) {
		return new ReaderInputStream(new InputStreamReader(is, encoding.getDecoder()));
	}
	
	public static InputStream getEncodingSafeInputStream(File file, CharacterEncoding encoding) throws FileNotFoundException {
		return getEncodingSafeInputStream(new FileInputStream(file), encoding);
	}
	

	/**
	 * Returns an <code>OutputStream</code> that properly enforces character encoding
	 * 
	 * @param os
	 * @param encoding
	 * @return
	 */
	public static OutputStream getEncodingSafeOutputStream(OutputStream os, CharacterEncoding encoding) {
		return new WriterOutputStream(new OutputStreamWriter(os, encoding.getEncoder()));
	}
	
	
	public static OutputStream getEncodingSafeOutputStream(File file, CharacterEncoding encoding)
			throws FileNotFoundException {
		return getEncodingSafeOutputStream(new FileOutputStream(file), encoding);
	}

}
