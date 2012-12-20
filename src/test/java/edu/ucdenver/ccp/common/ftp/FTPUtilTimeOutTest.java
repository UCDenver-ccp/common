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
import java.lang.StringBuilder;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.ftp.FTPUtil.FileType;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import edu.ucdenver.ccp.common.test.MockFtpServer;

public class FTPUtilTimeOutTest extends DefaultTestCase {

	private static final String FTP_HOST = "localhost";
	private static final int FTP_PORT = 9981;
	private MockFtpServer ftu;
	private File localDirectory;

	@Before
	public void setUp() throws IOException {
		ftu = new MockFtpServer(FTP_PORT);
		localDirectory = populateLocalDirectory();
		populateFtpDirectoryWithBigFiles();
		ftu.startServer();
	}

	@After
	public void tearDown() {
		ftu.stopServer();
	}

	private void populateFtpDirectoryWithBigFiles() {
		StringBuilder sa = new StringBuilder();
		for (int i=1; i<1000000; i++) {
			sa.append("" + i);
		}
		ftu.addFile("/file3", sa.toString());
		ftu.addFile("/file5.txt", sa.toString());
		ftu.addFile("/file6.xml", sa.toString());
		ftu.addFile("/file7.txt", sa.toString());
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


	@Test(expected = java.net.SocketTimeoutException.class)
	public void testTimeout() 
	throws SocketException, IOException {
		FTPClient ftpClient = FTPUtil.initializeFtpClient(FTP_HOST, FTP_PORT, 
				MockFtpServer.USER_NAME, MockFtpServer.PASSWORD);
		ftpClient.setSoTimeout(1);
		FTPUtil.syncLocalDirectoryWithFtpDirectory(ftpClient, localDirectory, null, FileType.ASCII);
		FTPUtil.closeFtpClient(ftpClient);

		assertEquals(String.format("Should be 0 files in the local directory after syncing directories."), 0,
				localDirectory.list().length);
		
	}

	@Test
	public void testTimeout2() 
	throws SocketException, IOException {
		FTPClient ftpClient = FTPUtil.initializeFtpClient(FTP_HOST, FTP_PORT, 
				MockFtpServer.USER_NAME, MockFtpServer.PASSWORD);
		FTPUtil.syncLocalDirectoryWithFtpDirectory(ftpClient, localDirectory, null, FileType.ASCII);
		FTPUtil.closeFtpClient(ftpClient);

		assertEquals(String.format("Should be 0 files in the local directory after syncing directories."), 6,
				localDirectory.list().length);
		
	}
}
