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
