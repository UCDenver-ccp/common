package edu.ucdenver.ccp.common.io;

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

	/**
	 * If the input resource path cannot be found on the classpath then this method tries the input
	 * path with either a forward slash removed or added to the front, depending on if one exists or
	 * not
	 * 
	 * @param resourcePath
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if the input resource path cannot be found on the classpath
	 */
	public static File copyClasspathResourceToFile(String resourcePath, File file) throws IOException {
		InputStream stream = ClassPathUtil.class.getResourceAsStream(resourcePath);
		if (stream == null) {
			String modifiedPath;
			logger.warn("Invalid resource path on classpath: " + resourcePath);
			if (resourcePath.startsWith("/")) {
				modifiedPath = resourcePath.substring(1);
			} else {
				modifiedPath = "/" + resourcePath;
			}
			logger.warn("Trying path variant: " + modifiedPath);
			stream = ClassPathUtil.class.getResourceAsStream(modifiedPath);
			if (stream == null) {
				throw new IllegalArgumentException("Unable to resolve resource on classpath: " + resourcePath);
			}
		}
		FileUtil.copy(stream, file);
		return file;
	}

	/**
	 * Copies a resource (file on the classpath) to the specified directory
	 * 
	 * @param clazz the class to use to get a ClassLoader to find the resource
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
