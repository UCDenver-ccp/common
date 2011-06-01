package edu.ucdenver.ccp.common.io;


import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

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
	static Logger logger = Logger.getLogger(ClassPathUtil.class);

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
	
	/**
	 * This function digs through the classpath to find either a file or the contents of a directory,
	 * whether the file/dir is in a directory or a jar.
	 * adapted from http://www.uofr.net/~greg/java/get-resource-listing.html
	 * 
	 * @param clazz
	 * @param resourceName
	 * @return a list of Strings which are paths you would search the classpath with.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static List<String> listResourceDirectory(Class clazz, String resourceName) 
	throws IOException, URISyntaxException {
		List<String> list = new ArrayList<String>();
		
		URL dirUrl = clazz.getClassLoader().getResource(resourceName);
		logger.debug("" + dirUrl);
		
		// with OSGI this comes back as: 
		// bundleresource://2.fwk1520314875/test_dir/test_file.txt
		// without:
		// file:/Users/roederc/subversion_workspace/common/src/test/resources/test_dir/test_file.txt
		
		URI uri=null;
		if (dirUrl != null && dirUrl.getProtocol().equals("file")) {
			uri = dirUrl.toURI();
			String[] strings = new File(uri.toString()).list();
			// input: foo/bar
			// output: /Users/roederc/foo/bar/baz/f1, /Users/roederc/foo/bar/baz/f2, etc.
			// should be: foo/bar/baz/f1, foo/bar/baz/f2
			// find that first part, remove as you iterate
			String uriString = uri.getPath();
			String firstPart = uriString.substring(0,uriString.indexOf(resourceName));
			File dirFile = new File(uri.getPath());
			if (dirFile.isDirectory()) {
				// directory
				for (File f : dirFile.listFiles()) {
					list.add(f.getPath().substring(firstPart.length()));
					logger.debug("ClassPathUtil adding from directory:" +f.getPath().substring(firstPart.length()));
				}
				return list;
			}
			else {
				// single file
				list.add(dirFile.getPath().substring(firstPart.length()));
				logger.debug("ClassPathUtil adding from single file:" + dirFile.getPath().substring(firstPart.length()));
			}
		}
		
		if (dirUrl != null && dirUrl.getProtocol().equals("jar")) {
			String jarPath = dirUrl.getPath();
			// looks like: file:/Users/roederc/junit-4.8.2.jar!/org/junit
			jarPath = jarPath.substring(5,jarPath.indexOf("!"));
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(resourceName) && !name.endsWith("/")) {
					logger.debug("ClassPathUtil adding from jar:" + name);
					list.add(name);
				}
			}
		}

		return list;
	}

}
