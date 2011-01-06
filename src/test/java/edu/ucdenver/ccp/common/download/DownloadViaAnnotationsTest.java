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
		ftu.addFile("/sampleFile.ascii.gz", ClassPathUtil.getResourceStreamFromClasspath(this.getClass(), SAMPLE_GZ_FILE_NAME));
	}

	@Test
	public void testDownloadControlledByAnnotation() throws Exception {
		File workDirectory = folder.newFolder("workDir");
		boolean clean = true;
		MyFileProcessor fileProcessor = new MyFileProcessor(workDirectory, clean);
		assertEquals("should be file5.ascii", "file5.ascii", fileProcessor.getFileToProcess().getName());
		assertTrue("file should exist locally", fileProcessor.getFileToProcess().exists());
	}

	private class MyFileProcessor {

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
		assertEquals(String.format("Unzipped file should have expected lines"), expectedLinesInSampleGzFile,
				FileReaderUtil.loadLinesFromFile(fileProcessor.getFileToProcess(), CharacterEncoding.US_ASCII));
	}

	@Test
	public void testNoDownloadIfFilePresentAndCleanFalse() throws Exception {
		File workDirectory = folder.newFolder("workDir");
		boolean clean = false;
		File sampleFile = FileUtil.appendPathElementsToDirectory(workDirectory, "sampleFile.ascii");
		List<String> expectedLines = CollectionsUtil
				.createList("This file already exists and doesn't need to be downloaded again.");
		FileWriterUtil.printLines(expectedLines, sampleFile, CharacterEncoding.US_ASCII);
		MyGzFileProcessor_BAD_PORT fileProcessor = new MyGzFileProcessor_BAD_PORT(workDirectory, clean);
		assertEquals("should be sampleFile.ascii", "sampleFile.ascii", fileProcessor.getFileToProcess().getName());
		assertTrue("file should exist locally", fileProcessor.getFileToProcess().exists());
		assertEquals(String.format("Unzipped file should have expected lines - it should not have been overwritten"),
				expectedLines, FileReaderUtil.loadLinesFromFile(fileProcessor.getFileToProcess(), CharacterEncoding.US_ASCII));
	}

	
	@Test
	public void testNoDownloadIfZippedFilePresentAndCleanFalse() throws Exception {
		File workDirectory = folder.newFolder("workDir");
		boolean clean = false;
		ClassPathUtil.copyClasspathResourceToFile(getClass(), SAMPLE_GZ_FILE_NAME, FileUtil.appendPathElementsToDirectory(workDirectory, SAMPLE_GZ_FILE_NAME));
		MyGzFileProcessor_BAD_PORT fileProcessor = new MyGzFileProcessor_BAD_PORT(workDirectory, clean);
		assertEquals("should be sampleFile.ascii", "sampleFile.ascii", fileProcessor.getFileToProcess().getName());
		assertTrue("file should exist locally", fileProcessor.getFileToProcess().exists());
		assertEquals(String.format("Unzipped file should have expected lines - it should not have been overwritten"),
				expectedLinesInSampleGzFile, FileReaderUtil.loadLinesFromFile(fileProcessor.getFileToProcess(), CharacterEncoding.US_ASCII));
	}
	
	private static class MyGzFileProcessor {

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

	/**
	 * Attempting a download using this class will cause a failure due to the incorrect port. This
	 * class can therefore be used to test that a download does not occur.
	 * 
	 * @author bill
	 * 
	 */
	private static class MyGzFileProcessor_BAD_PORT {

		@FtpDownload(server = FTP_HOST, port = 0000, path = "", filename = SAMPLE_GZ_FILE_NAME, filetype = FileType.BINARY)
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