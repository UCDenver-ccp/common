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

package edu.ucdenver.ccp.common.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.ColumnOrder;
import edu.ucdenver.ccp.common.file.FileComparisonUtil.LineOrder;
import edu.ucdenver.ccp.common.string.StringConstants;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class FileUtilTest extends DefaultTestCase {

	@Test
	public void testIsDirectoryValid_inputIsDirectory() {
		File directory = folder.newFolder("dir");
		assertNull(FileUtil.isDirectoryValid(directory));
	}

	@Test(expected = NullPointerException.class)
	public void testIsDirectoryValid_inputIsNull() {
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
	public void testIsDirectoryValid_inputDoesNotExist() {
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
	public void testAppendPathElementToDirectory() {
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
	 * ___- .hiddenDirectory<br>
	 * ______- fileInHiddenDir.txt<br>
	 * 
	 * @throws IOException
	 */
	private List<File> setUpSampleDirectoryStructure() throws IOException {
		File dir1 = folder.newFolder("dir1");
		File dir2 = folder.newFolder("dir2");

		folder.newFolder(".hidden");
		File dir11 = FileUtil.appendPathElementsToDirectory(dir1, "dir11");
		File dir111 = FileUtil.appendPathElementsToDirectory(dir11, "dir111");

		assertTrue(dir11.mkdir());
		assertTrue(dir111.mkdir());

		File file0txt = FileUtil.appendPathElementsToDirectory(folder.getRoot(), "file0.txt");
		assertTrue(file0txt.createNewFile());
		File file0xml = FileUtil.appendPathElementsToDirectory(folder.getRoot(), "file0.xml");
		assertTrue(file0xml.createNewFile());

		File file1txt = FileUtil.appendPathElementsToDirectory(dir1, "file1.txt");
		assertTrue(file1txt.createNewFile());
		File file2txt = FileUtil.appendPathElementsToDirectory(dir1, "file2.txt");
		assertTrue(file2txt.createNewFile());
		File file3xml = FileUtil.appendPathElementsToDirectory(dir1, "file3.xml");
		assertTrue(file3xml.createNewFile());
		File file4xml = FileUtil.appendPathElementsToDirectory(dir2, "file4.xml");
		assertTrue(file4xml.createNewFile());
		File file5txt = FileUtil.appendPathElementsToDirectory(dir11, "file5.txt");
		assertTrue(file5txt.createNewFile());
		File file6txt = FileUtil.appendPathElementsToDirectory(dir11, "file6.txt");
		assertTrue(file6txt.createNewFile());
		File file7txt = FileUtil.appendPathElementsToDirectory(dir111, "file7.txt");
		assertTrue(file7txt.createNewFile());
		File file8hidden = FileUtil.appendPathElementsToDirectory(dir111, ".hiddenfile8.txt");
		assertTrue(file8hidden.createNewFile());

		File hiddenDir = folder.newFolder(".hiddenDirectory");
		File fileInHiddenDirectory = FileUtil.appendPathElementsToDirectory(hiddenDir, "fileInhiddenDir.txt");
		assertTrue(fileInHiddenDirectory.createNewFile());

		List<File> files = CollectionsUtil.createList(file0txt, file0xml, file1txt, file2txt, file3xml, file4xml,
				file5txt, file6txt, file7txt);
		Collections.sort(files);
		return files;

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
	public void testFileListingOverDirectory() throws IOException {
		List<File> expectedFileListing = setUpSampleDirectoryStructure();
		assertEquals(String.format("Files should be as expected (and sorted)"), expectedFileListing, FileUtil
				.getFileListing(folder.getRoot(), true));
	}

	@Test
	public void testFileListingOverDirectory_UsingFileSuffixFilter() throws IOException {
		List<File> expectedTxtFileListing = keepTxtFiles(setUpSampleDirectoryStructure());
		assertEquals(String.format("Files should be as expected (and sorted)"), expectedTxtFileListing, FileUtil
				.getFileListing(folder.getRoot(), true, "txt"));
		// assertEquals(String.format("Files should be as expected (and sorted) - DL"),
		// expectedTxtFileListing,
		// DirectoryListing.getFiles(folder.getRoot().getAbsolutePath(), "txt", true));
	}

	/**
	 * Filters out files that are not .txt files
	 * 
	 * @param setUpSampleDirectoryStructure
	 * @return
	 */
	private List<File> keepTxtFiles(List<File> inputFiles) {
		List<File> txtFiles = new ArrayList<File>();
		for (File file : inputFiles)
			if (file.getName().endsWith(".txt"))
				txtFiles.add(file);
		return txtFiles;
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

	@Test
	public void testCopyFileToFile() throws Exception {
		List<String> lines = CollectionsUtil.createList("line1", "line2");
		File fromFile = folder.newFile("fromFile.ascii");
		FileWriterUtil.printLines(lines, fromFile, CharacterEncoding.US_ASCII);

		File toDirectory = folder.newFolder("toDir");
		File toFile = FileUtil.appendPathElementsToDirectory(toDirectory, "toFile.ascii");

		FileUtil.copy(fromFile, toFile);
		FileUtil.validateFile(toFile);
		assertTrue("toFile is not as expected after copy(fromFile, toFile).", FileComparisonUtil.hasExpectedLines(
				toFile, CharacterEncoding.US_ASCII, lines, null, LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
	}

	@Test
	public void testCopyFileToDirectory() throws Exception {
		List<String> lines = CollectionsUtil.createList("line1", "line2");
		File fromFile = folder.newFile("fromFile.ascii");
		FileWriterUtil.printLines(lines, fromFile, CharacterEncoding.US_ASCII);

		File toDirectory = folder.newFolder("toDir");
		File toFile = FileUtil.appendPathElementsToDirectory(toDirectory, fromFile.getName());

		FileUtil.copy(fromFile, toDirectory);
		FileUtil.validateFile(toFile);
		assertTrue("toFile is not as expected after copy(fromFile, toDirectory).", FileComparisonUtil.hasExpectedLines(
				toFile, CharacterEncoding.US_ASCII, lines, null, LineOrder.AS_IN_FILE, ColumnOrder.AS_IN_FILE));
	}

	@Test
	public void testCopyFileToString() throws Exception {
		List<String> lines = CollectionsUtil.createList("line1", "line2:nai\u0308ve");
		File fromFile = folder.newFile("fromFile.utf8");
		FileWriterUtil.printLines(lines, fromFile, CharacterEncoding.UTF_8);

		StringBuilder sb = new StringBuilder();
		sb.append(lines.get(0) + StringConstants.NEW_LINE);
		sb.append(lines.get(1) + StringConstants.NEW_LINE);
		String expectedStr = sb.toString();

		assertEquals(String.format("Text from file should be as expected and use UTF-8 encoding."), expectedStr,
				FileUtil.copyToString(fromFile, CharacterEncoding.UTF_8));
	}

	@Test(expected = MalformedInputException.class)
	public void testCopyFileToString_WithEncodingMismatch() throws Exception {
		List<String> lines = CollectionsUtil.createList("line1", "line2:nai\u0308ve");
		File fromFile = folder.newFile("fromFile.utf8");
		FileWriterUtil.printLines(lines, fromFile, CharacterEncoding.UTF_8);

		FileUtil.copyToString(fromFile, CharacterEncoding.US_ASCII);
	}

	/**
	 * Tests that the getFileSuffix method returns the proper file suffix
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetFileSuffix() throws IOException {
		File txtFile = folder.newFile("test.txt");
		assertEquals(String.format("Expected .txt file suffix to be returned"), ".txt", FileUtil.getFileSuffix(txtFile));

		txtFile = folder.newFile("test.txt.1.2.3.4.5.6.7.8");
		assertEquals(String.format("Expected .8 file suffix to be returned"), ".8", FileUtil.getFileSuffix(txtFile));

		txtFile = folder.newFile("test");
		assertEquals(String.format("Expected no file suffix"), "", FileUtil.getFileSuffix(txtFile));
	}

	/**
	 * Tests that the expected number of lines in a file is returned
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetLineCount() throws IOException {
		File file = folder.newFile("file.ascii");
		FileWriterUtil.printLines(CollectionsUtil.createList("1", "2", "3", "4", "5", "6", "7", "8", "9"), file,
				CharacterEncoding.US_ASCII);
		assertEquals(String.format("getLineCount() should return the expected number of lines"), 9L, FileUtil
				.getLineCount(file, CharacterEncoding.US_ASCII));
	}

	/**
	 * Tests that all file suffixes are removed when using the FileUtil.removeFileSuffixes() method.
	 * 
	 * @throws IOException
	 *             if an error occurs while creating the test files.
	 */
	@Test
	public void testRemoveFileSuffixes() throws IOException {
		File expectedFile = folder.newFile("test");
		File file = folder.newFile("test.txt");
		assertEquals(String.format("Single suffix should be removed."), expectedFile, FileUtil.removeFileSuffixes(file));
		file = folder.newFile("test.txt.1.2.3.4.5.6.7.8");
		assertEquals(String.format("All suffixes should be removed."), expectedFile, FileUtil.removeFileSuffixes(file));
		assertEquals(String.format("File that doesn't have a suffix should be returned as is."), expectedFile, FileUtil
				.removeFileSuffixes(expectedFile));
	}

}
