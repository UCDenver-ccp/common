package edu.ucdenver.ccp.common.download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.download.DownloadUtil;
import edu.ucdenver.ccp.common.download.FtpDownload;
import edu.ucdenver.ccp.common.file.FileLoaderUtil;
import edu.ucdenver.ccp.common.ftp.FTPUtil.FileType;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.common.test.MockFtpServer;

public class DownloadViaAnnotationsTest extends DefaultTestCase {

	private static final String FTP_HOST = "localhost";
	private static final int FTP_PORT = 9981;
	private static final String SAMPLE_GZ_FILE_NAME = "sampleFile.txt.gz";
	private MockFtpServer ftu;

	private final List<String> expectedLinesInSampleGzFile = CollectionsUtil.createList("This is line 1.",
			"This is line 2.");

	@Before
	public void setUp() throws IOException {
		ftu = new MockFtpServer(FTP_PORT);
		populateFtpDirectory();
		ftu.startServer();
	}

	@After
	public void tearDown() {
		ftu.stopServer();
	}

	private void populateFtpDirectory() throws IOException {
		ftu.addFile("/file3");
		ftu.addFile("/file5.txt");
		ftu.addFile("/file6.xml");
		ftu.addFile("/file7.txt");
		ftu.addFile("/sampleFile.txt.gz", getResourceFromClasspath(this.getClass(), SAMPLE_GZ_FILE_NAME));
	}

	@Test
	public void testDownloadControlledByAnnotation() throws Exception {
		File workDirectory = folder.newFolder("workDir");
		boolean clean = true;
		MyFileProcessor fileProcessor = new MyFileProcessor(workDirectory, clean);
		assertEquals("should be file5.txt", "file5.txt", fileProcessor.getFileToProcess().getName());
		assertTrue("file should exist locally", fileProcessor.getFileToProcess().exists());
	}

	private class MyFileProcessor {

		@FtpDownload(server = FTP_HOST, port = FTP_PORT, path = "", filename = "file5.txt", filetype = FileType.ASCII)
		private File fileToProcess;

		public MyFileProcessor(File workDirectory, boolean clean) throws SocketException, IOException,
				IllegalArgumentException, IllegalAccessException {
			DownloadUtil.download(this, workDirectory, MockFtpServer.USER_NAME, MockFtpServer.PASSWORD, clean);
		}

		public File getFileToProcess() {
			return fileToProcess;
		}
	}

	@Test
	public void testDownloadAndUnzipControlledByAnnotation() throws Exception {
		File workDirectory = folder.newFolder("workDir");
		boolean clean = true;
		MyGzFileProcessor fileProcessor = new MyGzFileProcessor(workDirectory, clean);
		assertEquals("should be sampleFile.txt", "sampleFile.txt", fileProcessor.getFileToProcess().getName());
		assertTrue("file should exist locally", fileProcessor.getFileToProcess().exists());
		assertEquals(String.format("Unzipped file should have expected lines"), expectedLinesInSampleGzFile,
				FileLoaderUtil.loadLinesFromFile(fileProcessor.getFileToProcess(), DEFAULT_ENCODING));
	}

	private class MyGzFileProcessor {

		@FtpDownload(server = FTP_HOST, port = FTP_PORT, path = "", filename = SAMPLE_GZ_FILE_NAME, filetype = FileType.BINARY)
		private File fileToProcess;

		public MyGzFileProcessor(File workDirectory, boolean clean) throws SocketException, IOException,
				IllegalArgumentException, IllegalAccessException {
			DownloadUtil.download(this, workDirectory, MockFtpServer.USER_NAME, MockFtpServer.PASSWORD, clean);
		}

		public File getFileToProcess() {
			return fileToProcess;
		}
	}

}