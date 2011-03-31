package edu.ucdenver.ccp.common.properties;

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



	@Test(expected = NullPointerException.class)
	public void testLoadPropertiesFile() {
		
		PropertiesUtil.loadProperties(null);
		
	}
	

	@Test(expected = RuntimeException.class)
	
	public void testLoadPropertiesFile_InvalidData() {
		
	
		
		
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
