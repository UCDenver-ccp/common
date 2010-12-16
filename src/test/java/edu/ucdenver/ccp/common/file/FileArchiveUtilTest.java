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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class FileArchiveUtilTest extends DefaultTestCase {

	private final List<String> expectedLinesInFile = CollectionsUtil.createList("This is line 1.", "This is line 2.");
	private final Set<String> expectedFileNamesOnFirstLevel = CollectionsUtil.createSet("file1.txt", "file2.txt",
			"file3.txt", "dir1");
	private final Set<String> expectedFileNamesOnSecondLevel = CollectionsUtil.createSet("file4.txt", "file5.txt",
			"file6.txt");

	private final String SAMPLE_FILE_6_NAME = "file6.txt";
	private final String SAMPLE_DIRECTORY_NAME = "dir1";

	private final String SAMPLE_GZIPPED_FILE_NAME = "sampleFile.txt.gz";
	private final String SAMPLE_TARBALL_FILE_NAME = "sampleTarball.tar";
	private final String SAMPLE_ZIPPED_TARBALL_FILE_NAME = "sampleTarball.tgz";
	private final String SAMPLE_ZIP_FILE_NAME = "sampleZipFile.zip";
	private final String SAMPLE_UNIX_COMPRESS_FILE_NAME = "sampleUnixCompressFile.txt.Z";

	@Test
	public void testUnzipGzFile() throws Exception {
		GZIPInputStream gis = new GZIPInputStream(ClassPathUtil.getResourceStreamFromClasspath(this.getClass(), SAMPLE_GZIPPED_FILE_NAME));
		/* other class doesn't work either:
		 * GZIPInputStream gis = new GZIPInputStream(getResourceFromClasspath(
		 * 	 this.getClass().forName("edu.ucdenver.ccp.common.file.FileArchiveUtil"), 
		 *   SAMPLE_GZIPPED_FILE_NAME))*/
		File outputDirectory = folder.newFolder("unzippedGZFile");
		File unzippedFile = new File(outputDirectory.getAbsolutePath() + File.separator + "sampleFile.txt");
		FileArchiveUtil.unzip(gis, unzippedFile.getName(), outputDirectory);
		List<String> linesFromUnzippedFile = FileReaderUtil.loadLinesFromFile(unzippedFile, CharacterEncoding.US_ASCII);
		assertTrue(String.format("The unzipped file should now exist."), unzippedFile.exists());
		assertEquals(String.format("There should be two lines in the unzipped file."), expectedLinesInFile,
				linesFromUnzippedFile);
	}
	
	
	@Test
	public void testUnzipUnixCompressFile() throws Exception {
		UncompressInputStream uis = new UncompressInputStream(ClassPathUtil.getResourceStreamFromClasspath(this.getClass(), SAMPLE_UNIX_COMPRESS_FILE_NAME));
		File outputDirectory = folder.newFolder("unzippedUnixCompressFile");
		File unzippedFile = FileUtil.appendPathElementsToDirectory(outputDirectory, "sampleUnixCompressFile.txt");
		FileArchiveUtil.unzip(uis, unzippedFile.getName(), outputDirectory);
		List<String> linesFromUnzippedFile = FileReaderUtil.loadLinesFromFile(unzippedFile, CharacterEncoding.US_ASCII);
		assertTrue(String.format("The unzipped file should now exist (unix compress)."), unzippedFile.exists());
		assertEquals(String.format("There should be two lines in the unzipped file (unix compress)."), expectedLinesInFile,
				linesFromUnzippedFile);
	}

	@Test
	public void testUntarFile() throws Exception {
		File tarFile = copyResourceToFile(SAMPLE_TARBALL_FILE_NAME);
		File outputDirectory = folder.newFolder("untarredFile");
		FileArchiveUtil.unpackTarFile(tarFile, outputDirectory);
		validateUnpackedDirectoryStructure(outputDirectory);
	}

	@Test
	public void testUnzipTGZFile() throws Exception {
		File zippedTarFile = copyResourceToFile(SAMPLE_ZIPPED_TARBALL_FILE_NAME);
		File outputDirectory = folder.newFolder("unzipedTGZFile");
		FileArchiveUtil.unzip(zippedTarFile, outputDirectory);
		File expectedUnzippedTarFile = new File(outputDirectory.getAbsolutePath() + File.separator
				+ SAMPLE_TARBALL_FILE_NAME);
		assertTrue(String.format("The unzipped file should now exist."), expectedUnzippedTarFile.exists());
	}

	@Test
	public void testUnPackZippedTarFile() throws Exception {
		File tarFile = copyResourceToFile(SAMPLE_ZIPPED_TARBALL_FILE_NAME);
		File outputDirectory = folder.newFolder("untarredunzippedFile");
		FileArchiveUtil.unpackTarFile(tarFile, outputDirectory);
		validateUnpackedDirectoryStructure(outputDirectory);
	}

	@Test
	public void testUnzippedFile() throws Exception {
		File zipFile = copyResourceToFile(SAMPLE_ZIP_FILE_NAME);
		File outputDirectory = folder.newFolder("unzippedFile");
		FileArchiveUtil.unzip(zipFile, outputDirectory);
		validateUnpackedDirectoryStructure(outputDirectory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void TestUntarNonTarFile() throws Exception {
		File zipFile = copyResourceToFile(SAMPLE_ZIP_FILE_NAME);
		File outputDirectory = folder.newFolder("unzippedFile");
		FileArchiveUtil.unpackTarFile(zipFile, outputDirectory);
	}

	/**
	 * All of the tar and zip files contain the same directory structure. This method validates that
	 * things unpacked as expected.
	 * 
	 * @param outputDirectory
	 * @throws IOException
	 */
	private void validateUnpackedDirectoryStructure(File outputDirectory) throws IOException {
		assertEquals(String.format("Should be 4 entries on the first level (3 files + 1 directory)"),
				expectedFileNamesOnFirstLevel, CollectionsUtil.array2Set(outputDirectory.list()));
		File dir1Directory = new File(outputDirectory.getAbsolutePath() + File.separator + SAMPLE_DIRECTORY_NAME);
		assertTrue("The directory should exist", dir1Directory.exists());
		assertTrue("The directory needs to be a directory", dir1Directory.isDirectory());
		assertEquals(String.format("Should be 3 entries on the second level (3 files)"),
				expectedFileNamesOnSecondLevel, CollectionsUtil.array2Set(dir1Directory.list()));
		File file6txtFile = new File(dir1Directory.getAbsolutePath() + File.separator + SAMPLE_FILE_6_NAME);
		List<String> linesFromUnzippedFile = FileReaderUtil.loadLinesFromFile(file6txtFile, CharacterEncoding.US_ASCII);
		assertTrue(String.format("The unzipped file should now exist."), file6txtFile.exists());
		assertEquals(String.format("There should be two lines in the unzipped file."), expectedLinesInFile,
				linesFromUnzippedFile);
	}

	/**
	 * Gets the file off of the classpath and copies it into the temporary folder, returning a
	 * reference to the new file.
	 * 
	 * @param resourceName
	 * @return
	 * @throws Exception
	 */
	private File copyResourceToFile(String resourceName) throws Exception {
		File file = folder.newFile(resourceName);
		ClassPathUtil.copyClasspathResourceToFile(FileArchiveUtil.class, resourceName, file);
		return file;
	}

	@Test
	public void testGzipFile() throws Exception {
		File testFile = folder.newFile("test.ascii");
		File zippedTestFile = folder.newFile("test.ascii.gz");
		File unzippedFolder = folder.newFolder("unzipped");
		List<String> expectedLines = CollectionsUtil.createList("line1", "line2", "line3");
		FileWriterUtil.printLines(expectedLines, testFile, CharacterEncoding.US_ASCII);
		FileArchiveUtil.gzipFile(testFile, zippedTestFile);
		FileArchiveUtil.unzip(zippedTestFile, unzippedFolder);
		File unzippedTestFile = new File(unzippedFolder.getAbsolutePath() + File.separator + "test.ascii");
		assertTrue(String.format("Unzipped file must exist"), unzippedTestFile.exists());
		List<String> lines = FileReaderUtil.loadLinesFromFile(unzippedTestFile, CharacterEncoding.US_ASCII);
		assertEquals(String
				.format("Lines in unzipped file should match lines put into original file prior to gzipping."),
				expectedLines, lines);
	}
}
