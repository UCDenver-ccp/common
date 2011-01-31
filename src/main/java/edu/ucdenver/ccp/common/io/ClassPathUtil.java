package edu.ucdenver.ccp.common.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.string.StringUtil;

/**
 * Utility class for working with files/resources on the classpath
 * 
 * @author bill
 * 
 */
public class ClassPathUtil {

	/**
	 * Helper method for grabbing a file from the classpath and returning an InputStream
	 * 
	 * @param clazz
	 * @param resourceName
	 * @return
	 */
	public static InputStream getResourceStreamFromClasspath(Class<?> clazz, String resourceName) {
		InputStream is = clazz.getResourceAsStream(resourceName);
		if (is == null) {
			throw new RuntimeException("resource not found in classpath: " + resourceName + " class is: "
					+ clazz.getCanonicalName());
		}
		return is;
	}

	/**
	 * Extracts the contents of a resource on the classpath and returns them as a String
	 * 
	 * @param clazz
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	public static String getContentsFromClasspathResource(Class<?> clazz, String resourceName,
			CharacterEncoding encoding) throws IOException {
		return StringUtil.convertStream(getResourceStreamFromClasspath(clazz, resourceName), encoding);
	}

	/**
	 * Copies a resource (file on the classpath) to the specified directory
	 * 
	 * @param resourceName
	 * @param directory
	 * @return
	 * @throws IOException
	 */
	public static void copyClasspathResourceToFile(Class<?> clazz, String resourceName, File file) throws IOException {
		FileUtil.copy(getResourceStreamFromClasspath(clazz, resourceName), file);
	}

}
