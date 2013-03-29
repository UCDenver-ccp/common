/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.ftp.FTPUtil.FileType;
import edu.ucdenver.ccp.common.io.ClassPathUtil;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.common.test.MockFtpServer;

public class DownloadViaAnnotationsTest extends DefaultTestCase {

	private static final String FTP_HOST = "localhost";
	private static final int FTP_PORT = 9981;
	private static final String SAMPLE_GZ_FILE_NAME = "sampleFile.ascii.gz";
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
		ftu.addFile("/file5.ascii");
		ftu.addFile("/file6.xml");
		ftu.addFile("/file7.ascii");
		ftu.addFile("/refseq-catalog-55.txt");
		ftu.addFile("/sampleFile.ascii.gz",
				ClassPathUtil.getResourceStreamFromClasspath(this.getClass(), SAMPLE_GZ_FILE_NAME));
	}

	@Test
	public void testDownloadControlledByAnnotation() throws Exception {
		File workDirectory = folder.newFolder("workDir");
		boolean clean = true;
		MyFileProcessor fileProcessor = new MyFileProcessor(workDirectory, clean);
		assertEquals("should be file5.ascii", "file5.ascii", fileProcessor.getFileToProcess().getName());
		assertTrue("file should exist locally", fileProcessor.getFileToProcess().exists());
		assertTrue("Ready semaphore file should also exist", new File(workDirectory, "file5.ascii.ready").exists());
	}

	private static class MyFileProcessor {

		@FtpDownload(server = FTP_HOST, port = FTP_PORT, path = "", filename = "file5.ascii", filetype = FileType.ASCII)
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
		assertEquals("should be sampleFile.ascii", "sampleFile.ascii", fileProcessor.getFileToProcess().getName());
		assertTrue("file should exist locally", fileProcessor.getFileToProcess().exists());
		assertTrue("Ready semaphore file should also exist", new File(workDirectory, "sampleFile.ascii.ready").exists());
		assertEquals(String.format("Unzipped file should have expected lines"), expectedLinesInSampleGzFile,
				FileReaderUtil.loadLinesFromFile(fileProcessor.getFileToProcess(), CharacterEncoding.US_ASCII));
	}
	
	
	@Test
	public void testDownloadAndNoUnzipControlledByAnnotation() throws Exception {
		File workDirectory = folder.newFolder("workDir");
		boolean clean = true;
		MyGzFileProcessor_NoDecompress fileProcessor = new MyGzFileProcessor_NoDecompress(workDirectory, clean);
		assertEquals("should be sampleFile.ascii", SAMPLE_GZ_FILE_NAME, fileProcessor.getFileToProcess().getName());
		assertTrue("file should exist locally", fileProcessor.getFileToProcess().exists());
		assertTrue("Ready semaphore file should also exist", new File(workDirectory, SAMPLE_GZ_FILE_NAME + ".ready").exists());
		
	}

	@Test
	public void testNoDownloadIfFilePresentAndCleanFalse() throws Exception {
		File workDirectory = folder.newFolder("workDir");
		boolean clean = false;
		File sampleFile = FileUtil.appendPathElementsToDirectory(workDirectory, "sampleFile.ascii");
		List<String> expectedLines = CollectionsUtil
				.createList("This file already exists and doesn't need to be downloaded again.");
		FileWriterUtil.printLines(expectedLines, sampleFile, CharacterEncoding.US_ASCII);
		File readySemaphoreFile = new File(workDirectory, "sampleFile.ascii.ready");
		assertTrue(readySemaphoreFile.createNewFile());
		MyGzFileProcessor_BAD_PORT fileProcessor = new MyGzFileProcessor_BAD_PORT(workDirectory, clean);
		assertEquals("should be sampleFile.ascii", "sampleFile.ascii", fileProcessor.getFileToProcess().getName());
		assertTrue("file should exist locally", fileProcessor.getFileToProcess().exists());
		assertEquals(String.format("Unzipped file should have expected lines - it should not have been overwritten"),
				expectedLines,
				FileReaderUtil.loadLinesFromFile(fileProcessor.getFileToProcess(), CharacterEncoding.US_ASCII));
	}

	@Test
	public void testNoDownloadIfZippedFilePresentAndCleanFalse() throws Exception {
		File workDirectory = folder.newFolder("workDir");
		boolean clean = false;
		ClassPathUtil.copyClasspathResourceToFile(getClass(), SAMPLE_GZ_FILE_NAME,
				FileUtil.appendPathElementsToDirectory(workDirectory, SAMPLE_GZ_FILE_NAME));
		File readySemaphoreFile = new File(workDirectory, "sampleFile.ascii.ready");
		assertTrue(readySemaphoreFile.createNewFile());
		MyGzFileProcessor_BAD_PORT fileProcessor = new MyGzFileProcessor_BAD_PORT(workDirectory, clean);
		assertEquals("should be sampleFile.ascii", "sampleFile.ascii", fileProcessor.getFileToProcess().getName());
		assertTrue("file should exist locally", fileProcessor.getFileToProcess().exists());
		assertEquals(String.format("Unzipped file should have expected lines - it should not have been overwritten"),
				expectedLinesInSampleGzFile,
				FileReaderUtil.loadLinesFromFile(fileProcessor.getFileToProcess(), CharacterEncoding.US_ASCII));
	}

	private static class MyGzFileProcessor {

		@FtpDownload(server = FTP_HOST, port = FTP_PORT, path = "", filename = SAMPLE_GZ_FILE_NAME, filetype = FileType.BINARY, decompress=true)
		private File fileToProcess;

		public MyGzFileProcessor(File workDirectory, boolean clean) throws SocketException, IOException,
				IllegalArgumentException, IllegalAccessException {
			DownloadUtil.download(this, workDirectory, MockFtpServer.USER_NAME, MockFtpServer.PASSWORD, clean);
		}

		public File getFileToProcess() {
			return fileToProcess;
		}
	}
	private static class MyGzFileProcessor_NoDecompress {
		
		@FtpDownload(server = FTP_HOST, port = FTP_PORT, path = "", filename = SAMPLE_GZ_FILE_NAME, filetype = FileType.BINARY)
		private File fileToProcess;
		
		public MyGzFileProcessor_NoDecompress(File workDirectory, boolean clean) throws SocketException, IOException,
		IllegalArgumentException, IllegalAccessException {
			DownloadUtil.download(this, workDirectory, MockFtpServer.USER_NAME, MockFtpServer.PASSWORD, clean);
		}
		
		public File getFileToProcess() {
			return fileToProcess;
		}
	}

	// archive test for later implementation of regex-capable ftp file download
	// @Test
	// public void testDownloadUsingRegexMatch() throws Exception {
	// File workDirectory = folder.newFolder("workDir");
	// boolean clean = true;
	// MyFileProcessor_UsesRegexFileName fileProcessor = new
	// MyFileProcessor_UsesRegexFileName(workDirectory, clean);
	// assertEquals("should be refseq-catalog-55.txt", "refseq-catalog-55.txt",
	// fileProcessor.getFileToProcess().getName());
	// assertTrue("file should exist locally", fileProcessor.getFileToProcess().exists());
	// assertTrue("Ready semaphore file should also exist", new
	// File(workDirectory,"refseq-catalog-55.txt.ready").exists());
	// }
	// private static class MyFileProcessor_UsesRegexFileName {
	//
	// @FtpDownload(server = FTP_HOST, port = FTP_PORT, path = "", filenameRegex =
	// "refseq-catalog-\\d+\\.txt", filetype = FileType.BINARY)
	// private File fileToProcess;
	//
	// public MyFileProcessor_UsesRegexFileName(File workDirectory, boolean clean) throws
	// SocketException, IOException,
	// IllegalArgumentException, IllegalAccessException {
	// DownloadUtil.download(this, workDirectory, MockFtpServer.USER_NAME, MockFtpServer.PASSWORD,
	// clean);
	// }
	//
	// public File getFileToProcess() {
	// return fileToProcess;
	// }
	// }

	/**
	 * Attempting a download using this class will cause a failure due to the incorrect port. This
	 * class can therefore be used to test that a download does not occur.
	 * 
	 * @author bill
	 * 
	 */
	private static class MyGzFileProcessor_BAD_PORT {

		@FtpDownload(server = FTP_HOST, port = 0000, path = "", filename = SAMPLE_GZ_FILE_NAME, filetype = FileType.BINARY, decompress=true)
		private File fileToProcess;

		public MyGzFileProcessor_BAD_PORT(File workDirectory, boolean clean) throws SocketException, IOException,
				IllegalArgumentException, IllegalAccessException {
			DownloadUtil.download(this, workDirectory, MockFtpServer.USER_NAME, MockFtpServer.PASSWORD, clean);
		}

		public File getFileToProcess() {
			return fileToProcess;
		}
	}

}