package edu.ucdenver.ccp.common.test;


import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.test.DefaultTestCase;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;


public class DefaultTestCaseTest extends DefaultTestCase {
	
	File dir=null;
	File file=null;
	
	@Before
	public void setup() throws IOException {
		//folder.create();
		file = folder.newFile("test1.tree");
		dir = folder.getRoot();
		assertNotNull(dir);
		assertNotNull(file);
		
		System.out.println("file:" + file.getAbsolutePath());
		System.out.println("file:" + dir.getAbsolutePath());
	}
	
	/** 
	 * There's a test in nlp that fails with similar code.
	 * That code runs when the create() method is not commented out.
	 * ...and will need more investigation.
	 */
	@Test
	public void test() {
		assertNotNull(dir);
		assertNotNull(file);
		
		System.out.println("file:" + file.getAbsolutePath());
		System.out.println("file:" + dir.getAbsolutePath());
	}
	
	@Test
	public void test_folder_creation() {
		long start = System.nanoTime();
		for (int i=1; i<10000; i++)
			folder.newFolder("folder" + i);
		
		long end = System.nanoTime();
		long durationMilli = (end - start)/1000000;
		assertTrue (durationMilli < 10000);

	}
}
