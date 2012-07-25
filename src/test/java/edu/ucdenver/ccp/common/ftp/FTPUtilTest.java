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
package edu.ucdenver.ccp.common.ftp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.ftp.FTPUtil.FileType;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.common.test.MockFtpServer;

public class FTPUtilTest extends DefaultTestCase {

	private static final String FTP_HOST = "localhost";
	private static final int FTP_PORT = 9981;
	private MockFtpServer ftu;
	private File localDirectory;

	@Before
	public void setUp() throws IOException {
		ftu = new MockFtpServer(FTP_PORT);
		localDirectory = populateLocalDirectory();
		populateFtpDirectory();
		ftu.startServer();
	}

	@After
	public void tearDown() {
		ftu.stopServer();
	}

	@Test
	public void testSyncDirectoryWithFtpDirectory() throws Exception {
		FTPClient ftpClient = FTPUtil.initializeFtpClient(FTP_HOST, FTP_PORT, MockFtpServer.USER_NAME,
				MockFtpServer.PASSWORD);
		FTPUtil.syncLocalDirectoryWithFtpDirectory(ftpClient, localDirectory, null, FileType.ASCII);
		FTPUtil.closeFtpClient(ftpClient);
		assertEquals(String.format("Should be 6 files in the local directory after syncing directories."), 6,
				localDirectory.list().length);
		Set<String> localFileNames = CollectionsUtil.array2Set(localDirectory.list());
		Set<String> expectedFileNames = CollectionsUtil.createSet("file1", "file2", "file3", "file5.txt", "file6.xml",
				"file7.txt");
		assertEquals(String.format("Files names should be as expected."), expectedFileNames, localFileNames);
	}

	@Test
	public void testSyncDirectoryWithFtpDirectoryWithFileSuffix() throws Exception {
		FTPClient ftpClient = FTPUtil.initializeFtpClient(FTP_HOST, FTP_PORT, MockFtpServer.USER_NAME,
				MockFtpServer.PASSWORD);
		FTPUtil.syncLocalDirectoryWithFtpDirectory(ftpClient, localDirectory, ".txt", FileType.ASCII);
		FTPUtil.closeFtpClient(ftpClient);
		assertEquals(String.format("Should be 5 files in the local directory after syncing directories."), 5,
				localDirectory.list().length);
		Set<String> localFileNames = CollectionsUtil.array2Set(localDirectory.list());
		Set<String> expectedFileNames = CollectionsUtil.createSet("file1", "file2", "file3", "file5.txt", "file7.txt");
		assertEquals(String.format("Files names should be as expected."), expectedFileNames, localFileNames);
	}

	private void populateFtpDirectory() {
		ftu.addFile("/file3");
		ftu.addFile("/file5.txt");
		ftu.addFile("/file6.xml");
		ftu.addFile("/file7.txt");
	}

	private File populateLocalDirectory() throws IOException {
		File localDirectory = folder.newFolder("local");
		assertTrue(new File(localDirectory.getAbsolutePath() + File.separator + "file1").createNewFile());
		assertTrue(new File(localDirectory.getAbsolutePath() + File.separator + "file2").createNewFile());
		assertTrue(new File(localDirectory.getAbsolutePath() + File.separator + "file3").createNewFile());
		assertEquals(String.format("Should be 3 files in the local directory before the test begins."), 3,
				localDirectory.list().length);
		return localDirectory;
	}

	@Test
	public void testPause() {
		long before = System.currentTimeMillis();
		FTPUtil.pause(1);
		long after = System.currentTimeMillis();
		assertEquals(String.format("At least 1000 ms should have elapsed (%d)", after - before), 1000, after - before,
				100);
	}
	
	@Test
	public void testRemoteFilePresence() throws SocketException, IOException {
		FTPClient ftpClient = FTPUtil.initializeFtpClient(FTP_HOST, FTP_PORT, MockFtpServer.USER_NAME, MockFtpServer.PASSWORD);
		Collection<String> fileNames = Arrays.asList(ftpClient.listNames());
		assertFalse(fileNames.contains("missing file"));
		assertTrue(fileNames.contains("file3"));
	}
}
