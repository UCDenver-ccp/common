/**
 * 
 */
package edu.ucdenver.ccp.common.digest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 *
 */
public class DigestUtil {

	/**
	 * @param fieldValues
	 * @return
	 */
	public static String getBase64Sha1Digest(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.reset();
			byte[] sha1digest = digest.digest(input.getBytes("UTF-8"));
			return Base64.encodeBase64URLSafeString(sha1digest);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
}
