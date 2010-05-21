package edu.ucdenver.ccp.util.download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.util.ftp.FTPUtil.FileType;
import edu.ucdenver.ccp.util.test.DefaultTestCase;
import edu.ucdenver.ccp.util.test.MockFtpServer;

public class DownloadViaAnnotationsTest extends DefaultTestCase {

	private static final String FTP_HOST = "localhost";
	private static final int FTP_PORT = 9981;
	private MockFtpServer ftu;

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

	private void populateFtpDirectory() {
		ftu.addFile("/file3");
		ftu.addFile("/file5.txt");
		ftu.addFile("/file6.xml");
		ftu.addFile("/file7.txt");
	}

	@Test
	public void testDownloadControlledByAnnotation() throws Exception {
		File workDirectory = folder.newFolder("workDir");
		boolean clean = true;
		MyFileProcessor fileProcessor = new MyFileProcessor(workDirectory, clean);
		assertEquals("should be file5.txt", "file5.txt", fileProcessor
				.getFileToProcess().getName());
		assertTrue("file should exist locally", fileProcessor.getFileToProcess().exists());
	}

	private class MyFileProcessor {

		@FtpDownload(server = FTP_HOST, port = FTP_PORT, path = "", filename = "file5.txt", 
				filetype = FileType.ASCII)
		private File fileToProcess;

		public MyFileProcessor(File workDirectory, boolean clean)
				throws SocketException, IOException, IllegalArgumentException,
				IllegalAccessException {
			DownloadUtil.download(this, workDirectory,MockFtpServer.USER_NAME, MockFtpServer.PASSWORD, clean);
		}

		public File getFileToProcess() {
			return fileToProcess;
		}
	}

}