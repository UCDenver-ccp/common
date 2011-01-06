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

package edu.ucdenver.ccp.common.ftp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.ftp.FTPUtil;
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
		FTPUtil.pause(4);
		long after = System.currentTimeMillis();
		assertEquals(String.format("At least 4000 ms should have elapsed (%d)", after - before), 4000, after - before,
				100);
	}
}
