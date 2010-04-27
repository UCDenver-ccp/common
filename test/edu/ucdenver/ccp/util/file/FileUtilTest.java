package edu.ucdenver.ccp.util.file;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.ucdenver.ccp.util.collections.CollectionsUtil;

public class FileUtilTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testIsDirectoryValid_inputIsDirectory() throws Exception {
		File directory = folder.newFolder("dir");
		assertNull(FileUtil.isDirectoryValid(directory));
	}

	@Test(expected = NullPointerException.class)
	public void testIsDirectoryValid_inputIsNull() throws Exception {
		File nullFile = null;
		FileUtil.isDirectoryValid(nullFile);
	}

	@Test
	public void testIsDirectoryValid_inputIsFile() throws Exception {
		File file = folder.newFile("file");
		String errorMessage = FileUtil.isDirectoryValid(file);
		assertNotNull(errorMessage);
		assertTrue(String.format("Error message should indicate that the input is not a directory."), errorMessage
				.startsWith("Input directory is not a directory"));
	}

	@Test
	public void testIsDirectoryValid_inputDoesNotExist() throws Exception {
		File directory = folder.newFolder("dir2");
		File directoryNotExist = new File(directory + File.separator + "thisDirectoryDoesNotExist");
		String errorMessage = FileUtil.isDirectoryValid(directoryNotExist);
		assertNotNull(errorMessage);
		assertTrue(String.format("Error message should indicate that the input directory does not exist."),
				errorMessage.startsWith("Directory does not exist"));
	}

	@Test
	public void testValidateValidFile() throws Exception {
		File newFile = folder.newFile("newfile");
		assertNull("No error message should be returned", FileUtil.isFileValid(newFile));
	}

	@Test(expected = FileNotFoundException.class)
	public void testValidateInvalidFile() throws Exception {
		File newFolder = folder.newFolder("newfolder");
		FileUtil.validateFile(newFolder);
	}
	
	@Test
	public void testAppendPathElementToDirectory() throws Exception {
		String directoryName = String.format("%sthis%sis%sa%sdirectory", File.separator, File.separator, File.separator, File.separator);
		File directory = new File(directoryName);
		String fileName = "this.is.a.file.name";
		assertEquals(String.format("Resulting file should be directory/fileName."),new File(directoryName + File.separator + fileName), FileUtil.appendPathElementsToDirectory(directory, fileName));
	}
}
