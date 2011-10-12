/* Copyright (C) 2009, 2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP common library.
 * The CCP common library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package edu.ucdenver.ccp.common.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;

import edu.ucdenver.ccp.common.io.ClassPathUtil;

/**
 * Utility class for dealing with Java Properties objects
 * 
 * @author bill
 * 
 */
public class PropertiesUtil {

	/**
	 * Loads a properties file from the specified path
	 * 
	 * @param path
	 * @return
	 */
	public static Properties loadProperties(String propertiesFileName) {
		return loadProperties(new File(propertiesFileName));
	}

	/**
	 * Loads a properties from a File object
	 * 
	 * @param file
	 * @return
	 */
	public static Properties loadProperties(File file) {
		Properties properties = new Properties();
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
			properties.load(fis);
		} catch (IOException ex) {
			String path = file == null ? null : file.getAbsolutePath();
			String message = "Problem loading properties file: " + path;
			throw new RuntimeException(message, ex);
		} finally {
			IOUtils.closeQuietly(fis);
		}
		return properties;
	}

	/**
	 * Returns true if there is a property with the name specified in propertyName
	 * 
	 * @param properties
	 * @param propertyName
	 * @return
	 */
	public static boolean hasProperty(Properties properties, String propertyName) {
		return (properties.getProperty(propertyName) != null);
	}

	/**
	 * Returns the property value for the input property name. If that property does not exist, and
	 * IllegalArgumentException if thrown.
	 * 
	 * @param properties
	 * @param propertyName
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String getPropertyValue(Properties properties, String propertyName) throws IllegalArgumentException {
		String propertyValue = properties.getProperty(propertyName);
		if (propertyValue == null) {
			throw new IllegalArgumentException(String.format(
					"Expected to find property named \"%s\", but none could be found.", propertyName));
		}
		return propertyValue;
	}

	/**
	 * Converts the input Properties into a <code>Map<String, String></code>
	 * 
	 * @param properties
	 * @return
	 */
	public static Map<String, String> getPropertiesMap(Properties properties) {
		Map<String, String> propertiesMap = new HashMap<String, String>();
		for (Entry<Object, Object> entry : properties.entrySet())
			propertiesMap.put(entry.getKey().toString(), entry.getValue().toString());
		return propertiesMap;
	}

	/**
	 * Loads a <code>Properties</code> object from the <code>InputStream</code> and returns a
	 * <code>Map<String, String></code> contains the property key and values. Closes the stream.
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> getPropertiesMap(InputStream inputStream) throws IOException {
		try {
			Properties properties = new Properties();
			properties.load(inputStream);
			return getPropertiesMap(properties);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}
	

	    /**
	     * Looks up a resource named 'name' in the classpath. 
	     * 
	     * @param name classpath resource name
	     * 
	     * @return resource converted to java.util.Properties [may be null if the
	     * resource was not found and THROW_ON_LOAD_FAILURE is false]
	     * @throws IllegalArgumentException if the resource was not found
	     */
	    public static Properties loadPropertiesFromClassLoader(String name, Class<?> clazz)
	    {
    		InputStream in = null;
    		Properties result = null;
	    	try {
	    		result = new Properties ();
	    		in = ClassPathUtil.getResourceStreamFromClasspath(clazz, name);
	    		result.load (in); // Can throw IOException
	    	} catch (IOException ex) {
				String message = "Problem loading properties stream.";
				throw new RuntimeException(message, ex);
			} finally {
	    		IOUtils.closeQuietly(in);
	    	}
            return result;
	    }
}
