package edu.ucdenver.ccp.util.file;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.ucdenver.ccp.util.collections.CollectionsUtil;

public class FileArchiveUtilTest {

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

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testUnzipGzFile() throws Exception {
		GZIPInputStream gis = new GZIPInputStream(FileArchiveUtilTest.class
				.getResourceAsStream(SAMPLE_GZIPPED_FILE_NAME));
		File outputDirectory = folder.newFolder("unzippedGZFile");
		File unzippedFile = new File(outputDirectory.getAbsolutePath() + File.separator + "sampleFile.txt");
		FileArchiveUtil.unzip(gis, unzippedFile.getName(), outputDirectory);
		List<String> linesFromUnzippedFile = FileLoaderUtil.loadLinesFromFile(unzippedFile);
		assertTrue(String.format("The unzipped file should now exist."), unzippedFile.exists());
		assertEquals(String.format("There should be two lines in the unzipped file."), expectedLinesInFile,
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
	
	@Test(expected=IllegalArgumentException.class)
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
		List<String> linesFromUnzippedFile = FileLoaderUtil.loadLinesFromFile(file6txtFile);
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
		FileUtil.copy(FileArchiveUtilTest.class.getResourceAsStream(resourceName), file);
		return file;
	}
}
