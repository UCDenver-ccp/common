package edu.ucdenver.ccp.common.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class FileUtilTest extends DefaultTestCase {

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
		String directoryName = String.format("%sthis%sis%sa%sdirectory", File.separator, File.separator,
				File.separator, File.separator);
		File directory = new File(directoryName);
		String fileName = "this.is.a.file.name";
		assertEquals(String.format("Resulting file should be directory/fileName."), new File(directoryName
				+ File.separator + fileName), FileUtil.appendPathElementsToDirectory(directory, fileName));
	}

	/**
	 * root<br>
	 * ___- file0.txt<br>
	 * ___ - file0.xml<br>
	 * ___- dir1<br>
	 * ______- file1.txt<br>
	 * ______- file2.txt<br>
	 * ______- file3.xml<br>
	 * ______- dir11<br>
	 * _________- file5.txt<br>
	 * _________- file6.txt<br>
	 * _________- dir111<br>
	 * ____________- file7.txt<br>
	 * ___- dir2<br>
	 * ______- file4.xml<br>
	 * 
	 * @throws IOException
	 */
	private void setUpSampleDirectoryStructure() throws IOException {
		File dir1 = folder.newFolder("dir1");
		File dir2 = folder.newFolder("dir2");
		@SuppressWarnings("unused")
		File hiddenDir = folder.newFolder(".hidden");
		File dir11 = FileUtil.appendPathElementsToDirectory(dir1, "dir11");
		File dir111 = FileUtil.appendPathElementsToDirectory(dir11, "dir111");

		assertTrue(dir11.mkdir());
		assertTrue(dir111.mkdir());

		assertTrue(FileUtil.appendPathElementsToDirectory(folder.getRoot(), "file0.txt").createNewFile());
		assertTrue(FileUtil.appendPathElementsToDirectory(folder.getRoot(), "file0.xml").createNewFile());

		assertTrue(FileUtil.appendPathElementsToDirectory(dir1, "file1.txt").createNewFile());
		assertTrue(FileUtil.appendPathElementsToDirectory(dir1, "file2.txt").createNewFile());
		assertTrue(FileUtil.appendPathElementsToDirectory(dir1, "file3.xml").createNewFile());
		assertTrue(FileUtil.appendPathElementsToDirectory(dir2, "file4.xml").createNewFile());
		assertTrue(FileUtil.appendPathElementsToDirectory(dir11, "file5.txt").createNewFile());
		assertTrue(FileUtil.appendPathElementsToDirectory(dir11, "file6.txt").createNewFile());
		assertTrue(FileUtil.appendPathElementsToDirectory(dir111, "file7.txt").createNewFile());
		assertTrue(FileUtil.appendPathElementsToDirectory(dir111, ".hiddenfile8.txt").createNewFile());
	}

	@Test
	public void testFileIteratorOverDirectory() throws Exception {
		setUpSampleDirectoryStructure();
		Set<String> expectedFileNames = CollectionsUtil.createSet("file0.txt", "file0.xml", "file1.txt", "file2.txt",
				"file3.xml", "file4.xml", "file5.txt", "file6.txt", "file7.txt");
		checkFileIterator(FileUtil.getFileIterator(folder.getRoot(), true, (String[]) null), expectedFileNames);
		checkFileIterator(FileUtil.getFileIterator(folder.getRoot(), true), expectedFileNames);
	}

	@Test
	public void testFileIteratorOverDirectory_RecurseFalse() throws Exception {
		setUpSampleDirectoryStructure();
		Set<String> expectedFileNames = CollectionsUtil.createSet("file0.txt", "file0.xml");
		checkFileIterator(FileUtil.getFileIterator(folder.getRoot(), false, (String[]) null), expectedFileNames);
		checkFileIterator(FileUtil.getFileIterator(folder.getRoot(), false), expectedFileNames);
	}

	@Test
	public void testFileIteratorOverDirectory_UseFileSuffixes() throws Exception {
		setUpSampleDirectoryStructure();
		Set<String> expectedFileNames = CollectionsUtil.createSet("file0.txt", "file1.txt", "file2.txt", "file5.txt",
				"file6.txt", "file7.txt");
		checkFileIterator(FileUtil.getFileIterator(folder.getRoot(), true, "txt"), expectedFileNames);
		checkFileIterator(FileUtil.getFileIterator(folder.getRoot(), true, ".txt"), expectedFileNames);
	}

	@Test
	public void testFileIterator_pointedAtFile() throws Exception {
		File file99 = folder.newFile("file99.txt");
		Set<String> expectedFileNames = CollectionsUtil.createSet("file99.txt");
		checkFileIterator(FileUtil.getFileIterator(file99, true), expectedFileNames);
		checkFileIterator(FileUtil.getFileIterator(file99, true, ".txt"), expectedFileNames);
		checkFileIterator(FileUtil.getFileIterator(file99, true, "txt"), expectedFileNames);
		checkFileIterator(FileUtil.getFileIterator(file99, true, "xml"), new HashSet<String>());
	}

	@Test
	public void testFileIterator_pointedAtHiddenFile() throws Exception {
		File hiddenFile = folder.newFile(".hiddenfile.txt");
		Set<String> expectedFileNames = CollectionsUtil.createSet();
		checkFileIterator(FileUtil.getFileIterator(hiddenFile, true), expectedFileNames);
		checkFileIterator(FileUtil.getFileIterator(hiddenFile, true, ".txt"), expectedFileNames);
	}
	
	private void checkFileIterator(Iterator<File> fileIter, Set<String> expectedFileNames) {
		int count = 0;
		while (fileIter.hasNext()) {
			File file = fileIter.next();
			assertTrue(String.format("File name (%s) should be in expected set.", file.getName()), expectedFileNames
					.contains(file.getName()));
			count++;
		}
		assertEquals(String.format("Expected %d files to be returned by iterator, but only observed %d",
				expectedFileNames.size(), count), expectedFileNames.size(), count);

	}
}
