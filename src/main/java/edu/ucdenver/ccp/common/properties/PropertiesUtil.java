/*
 * Copyright (C) 2009 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
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
 * 
 */
package edu.ucdenver.ccp.common.properties;

import java.io.*;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public abstract class PropertiesUtil {

	private static final Logger logger = Logger.getLogger(PropertiesUtil.class);

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
			logger.error(message, ex);
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
	public boolean hasProperty(Properties properties, String propertyName) {
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

}
