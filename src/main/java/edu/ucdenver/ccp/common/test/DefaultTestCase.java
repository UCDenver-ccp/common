/* Copyright (C) 2010 Center for Computational Pharmacology, University of Colorado School of Medicine
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

package edu.ucdenver.ccp.common.test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.io.ClassPathUtil;

/**
 * A super class for other test classes. It sets up a logger among other things.
 * 
 * @author Bill Baumgartner
 * 
 */
public class DefaultTestCase {
	private static final Logger logger = Logger.getLogger(DefaultTestCase.class);

	static {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	/**
	 * If folder doesn't appear to be created, check your junit version.
	 * It works on 4.8.2, but has failed with 4.4 or 4.5.
	 */
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();


//	/**
//	 * Helper method for grabbing a file from the classpath and returning an InputStream
//	 * 
//	 * @param clazz
//	 * @param resourceName
//	 * @return
//	 */
//	protected InputStream getResourceFromClasspath(Class<?> clazz, String resourceName) {
//		InputStream is = clazz.getResourceAsStream(resourceName);
//		if (is == null) {
//			logger.error("resource not found in classpath: " + resourceName 
//					+ " class is: " + clazz.getCanonicalName());
//		}
//		Assert.assertNotNull("Resource not found: " + resourceName, is);
//		return is;
//	}
//
//	/**
//	 * Extracts the contents of a resource on the classpath and returns them as a String
//	 * 
//	 * @param clazz
//	 * @param resourceName
//	 * @return
//	 * @throws IOException
//	 */
//	protected String getContentsFromClasspathResource(Class<?> clazz, String resourceName, CharacterEncoding encoding) throws IOException {
//		return StringUtil.convertStream(getResourceFromClasspath(clazz, resourceName), encoding);
//	}

	/**
	 *Helper method for grabbing a file from the classpath and copying it to the temporary folder.
	 * This method can be used for testing applications that require File input, but are incapable
	 * of consuming InputStreams.
	 * 
	 * @param clazz
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	protected File copyClasspathResourceToTemporaryFile(Class<?> clazz, String resourceName) throws IOException {
		File tempFile = folder.newFile(resourceName);
		FileUtil.copy(ClassPathUtil.getResourceStreamFromClasspath(clazz, resourceName), tempFile);
		logger.debug(String.format("created file: %s", tempFile.getAbsolutePath()));
		return tempFile;
	}
	
	protected void assertEmpty(String errorMessage, Collection<?> collection) {
		Assert.assertTrue(errorMessage, collection.isEmpty());
	}
}
