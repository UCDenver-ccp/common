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
	    public static Properties loadPropertiesFromClassPath(String name, Class<?> clazz)
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
