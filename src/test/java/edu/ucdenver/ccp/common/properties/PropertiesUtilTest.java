package edu.ucdenver.ccp.common.properties;

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

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.string.StringUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class PropertiesUtilTest extends DefaultTestCase {

	private File samplePropertiesFile; 
	
	@Before
	public void setUp() throws Exception {
		initializeSamplePropertiesFile();
	}

	/**
	 * Initializes a sample properties file to be used for testing
	 * @throws IOException 
	 */
	private void initializeSamplePropertiesFile() throws IOException {
		List<String> lines = CollectionsUtil.createList("PROPERTY_1=value1","PROPERTY_2=value2");
		samplePropertiesFile = folder.newFile("sample.properties");
		FileWriterUtil.printLines(lines, samplePropertiesFile, CharacterEncoding.UTF_8, WriteMode.OVERWRITE, FileSuffixEnforcement.OFF);
	}



	public void testLoadPropertiesString() {
		String s = null;
		PropertiesUtil.loadProperties(s);
		//TODO: add assert statement
	}
	

	@Test(expected = RuntimeException.class)
	public void testLoadPropertiesFile_InvalidData() {
		File f = new File("this file does not exist");
		PropertiesUtil.loadProperties(f);
	}

	@Test
	public void testhasProperty() {
		
		Properties properties = PropertiesUtil.loadProperties(samplePropertiesFile);
		String propertyName = "PROPERTY_1";
		assertTrue(PropertiesUtil.hasProperty(properties, propertyName));
		
	}

	@Test
	public void testhasProperty_EmptySpace() {
		
		Properties properties = PropertiesUtil.loadProperties(samplePropertiesFile);
		String propertyName = "";
		assertFalse(PropertiesUtil.hasProperty(properties, propertyName));
		
	}

	@Test(expected = NullPointerException.class)
	public void testhasProperty_WithNullInput() {
		
		Properties properties = PropertiesUtil.loadProperties(samplePropertiesFile);
		String propertyName = null;
		assertTrue(PropertiesUtil.hasProperty(properties, propertyName));
		
	}

	@Test
	public void testhasProperty_InvalidInput() {
		
		Properties properties = PropertiesUtil.loadProperties(samplePropertiesFile);
		String propertyName = "Invalid Input";
		assertFalse(PropertiesUtil.hasProperty(properties, propertyName));
		
	}

	
	
	@Test
	public void testGetPropertyValue() {
		Properties properties = PropertiesUtil.loadProperties(samplePropertiesFile);
		String expectedValue = "value1";
		String propertyName = "PROPERTY_1";
		String actualValue = PropertiesUtil.getPropertyValue(properties, propertyName);
		assertEquals("Expected property value to be value1", expectedValue, actualValue);
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void testGetPropertyValue_propertyNameDoesNotExist() {
		Properties properties = PropertiesUtil.loadProperties(samplePropertiesFile);
		String propertyName = "THIS_PROPERTY_NAME_IS_NOT_IN_THE_SAMPLE_FILE";
		PropertiesUtil.getPropertyValue(properties, propertyName);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetPropertyValue_propertyNameIsEmpty() {
		Properties properties = PropertiesUtil.loadProperties(samplePropertiesFile);
		String propertyName = "";
		PropertiesUtil.getPropertyValue(properties, propertyName);
	}
	
	
	@Test(expected=NullPointerException.class)
	public void testGetPropertyValue_propertyNameIsNull() {
		Properties properties = PropertiesUtil.loadProperties(samplePropertiesFile);
		String propertyName = null;
		PropertiesUtil.getPropertyValue(properties, propertyName);
	}

	@Ignore("Not implemented yet")
	@Test
	public void testGetPropertiesMapProperties() {
		fail("Not yet implemented");
	}

	@Ignore("Not implemented yet")
	@Test
	public void testGetPropertiesMapInputStream() {
		fail("Not yet implemented");
	}

}
