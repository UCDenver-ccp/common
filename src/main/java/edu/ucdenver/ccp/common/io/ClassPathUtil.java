package edu.ucdenver.ccp.common.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.string.StringConstants;
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
			is = clazz.getClassLoader().getResourceAsStream(resourceName);
			if (is == null)
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
	 * Copies a resource (file on the classpath) to the specified file
	 * 
	 * @param resourceName
	 * @param directory
	 * @return
	 * @throws IOException
	 */
	public static File copyClasspathResourceToFile(Class<?> clazz, String resourceName, File file) throws IOException {
		FileUtil.copy(getResourceStreamFromClasspath(clazz, resourceName), file);
		return file;
	}

	public static File copyClasspathResourceToFile(String resourcePath, File file) throws IOException {
		FileUtil.copy(ClassLoader.getSystemResourceAsStream(resourcePath), file);
		return file;
	}

	/**
	 * Copies a resource (file on the classpath) to the specified directory
	 * 
	 * @param clazz
	 * @param resourceName
	 * @param directory
	 * @throws IOException
	 */
	public static File copyClasspathResourceToDirectory(Class<?> clazz, String resourceName, File directory)
			throws IOException {
		return copyClasspathResourceToFile(clazz, resourceName, new File(directory, resourceName));
	}

	public static File copyClasspathResourceToDirectory(String resourcePath, File directory) throws IOException {
		String resourceName = resourcePath.substring(resourcePath.lastIndexOf(StringConstants.FORWARD_SLASH) + 1);
		return copyClasspathResourceToFile(resourcePath, new File(directory, resourceName));
	}

	/**
	 * This function digs through the classpath to find either a file or the contents of a
	 * directory, whether the file/dir is in a directory or a jar. adapted from
	 * http://www.uofr.net/~greg/java/get-resource-listing.html
	 * 
	 * @param clazz
	 * @param resourceName
	 * @return a list of Strings which are paths you would search the classpath with.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static List<String> listResourceDirectory(Class<?> clazz, String resourceName) throws IOException,
			URISyntaxException {
		List<String> resourceNames = new ArrayList<String>();

		URL dirUrl = clazz.getClassLoader().getResource(resourceName);
		logger.debug("" + dirUrl);

		// with OSGI this comes back as:
		// bundleresource://2.fwk1520314875/test_dir/test_file.txt
		// without:
		// file:/Users/roederc/subversion_workspace/common/src/test/resources/test_dir/test_file.txt

		URI uri = null;
		if (dirUrl != null && dirUrl.getProtocol().equals("file")) {
			uri = dirUrl.toURI();
			String uriString = uri.getPath();
			String resourcePath = uriString.substring(0, uriString.indexOf(resourceName));
			File dirFile = new File(uri.getPath());
			if (dirFile.isDirectory()) {
				for (File f : dirFile.listFiles()) {
					resourceNames.add(f.getPath().substring(resourcePath.length()));
					logger.debug("ClassPathUtil adding from directory:" + f.getPath().substring(resourcePath.length()));
				}
			} else {
				// single file
				resourceNames.add(dirFile.getPath().substring(resourcePath.length()));
				logger.debug("ClassPathUtil adding from single file:"
						+ dirFile.getPath().substring(resourcePath.length()));
			}
		} else if (dirUrl != null && dirUrl.getProtocol().equals("jar")) {
			String jarPath = dirUrl.getPath();
			// looks like: file:/Users/roederc/junit-4.8.2.jar!/org/junit
			jarPath = jarPath.substring(5, jarPath.indexOf("!"));
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(resourceName) && !name.endsWith("/")) {
					logger.debug("ClassPathUtil adding from jar:" + name);
					resourceNames.add(name);
				}
			}
		}

		return resourceNames;
	}

}
