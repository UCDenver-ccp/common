package edu.ucdenver.ccp.common.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.file.DirectoryListing;



public class DirectoryListingTest {
	
	
	String suffix=".junk";
	File testDir;
	File sysTempDir;
	File subDir;
	File file1;
	File file2;
	File file3;
	File file4;
	
	@Before
	public void createDirs() throws IOException {
		
	    sysTempDir = new File(System.getProperty("java.io.tmpdir"));
	    String dirName = UUID.randomUUID().toString();
	    testDir = new File(sysTempDir, dirName);
	    if (!testDir.mkdir()) {
	    	throw new IOException(
	    		"failed to create directory in test of PMCCorpusParserTest");
	    }
	    
	    subDir = new File(testDir, "subdir");
	    if (!subDir.mkdir()) {
	    	throw new IOException(
	    		"failed to create subdirectory in test of PMCCorpusParserTest");
	    }
	    
	    file1 = new File(testDir,"file1" + suffix );
	    file1.createNewFile();
	    file2 = new File(testDir,"file2" + suffix );
	    file2.createNewFile();
	    file3 = new File(subDir,"file3" + suffix );
	    file3.createNewFile();
	    file4 = new File(subDir,"file4" + suffix );
	    file4.createNewFile();
	}
	
	@After
	public void deleteDirs() {
		System.out.println(file1.getPath());
		System.out.println(file2.getPath());
		System.out.println(file3.getPath());
		System.out.println(file4.getPath());
		
		file1.delete();
		file2.delete();
		file3.delete();
		file4.delete();
		subDir.delete();
		testDir.delete();
	}
	
	@Test
	public void TestOneLevel() {
		List<File> list = DirectoryListing.getFiles(testDir.getPath(), suffix, false);
		assertTrue(list.contains(file1));
		assertTrue(list.contains(file2));
		assertFalse(list.contains(file3));
		assertFalse(list.contains(file4));
		
	}
	
	@Test
	public void TestTwoLevels() {
		List<File> list = DirectoryListing.getFiles(testDir.getPath(), suffix, true);
		assertTrue(list.contains(file1));
		assertTrue(list.contains(file2));
		assertTrue(list.contains(file3));
		assertTrue(list.contains(file4));
	}

}
