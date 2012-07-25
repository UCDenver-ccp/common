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
package edu.ucdenver.ccp.common.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import edu.ucdenver.ccp.common.file.FileUtil;

/**
 * Helper class that launches mock ftp server for use with unit tests.
 * 
 * @author malenkiy
 */
public class MockFtpServer {
	public static final String USER_NAME = "anonymous";
	public static final String PASSWORD = "password";
	public static final String HOME_DIRECTORY = "/";

	FakeFtpServer server;
	protected UnixFakeFileSystem fs;

	public MockFtpServer() {
		server = new FakeFtpServer();
		init();
	}

	public MockFtpServer(int port) {
		server = new FakeFtpServer();
		server.setServerControlPort(port);
		init();
	}

	private void init() {
		fs = new UnixFakeFileSystem();
		server.setFileSystem(fs);
		UserAccount account = new UserAccount(USER_NAME, PASSWORD, HOME_DIRECTORY);
		server.addUserAccount(account);
	}

	public void addFile(String path, String contents) {
		FileEntry fileEntry = new FileEntry(path, contents);
		fs.add(fileEntry);
	}
	
	public void addFile(String path) {
		FileEntry fileEntry = new FileEntry(path);
		fs.add(fileEntry);
	}

	public void addFile(String path, File contents) throws IOException {
		FileUtil.validateFile(contents);
		FileEntry fileEntry = new FileEntry(path);
		fileEntry.setContents(FileUtil.toByteArray(contents));
		fs.add(fileEntry);
	}
	
	public void addFile(String path, InputStream is) throws IOException {
		FileEntry fileEntry = new FileEntry(path);
		fileEntry.setContents(IOUtils.toByteArray(is));
		fs.add(fileEntry);
	}
	
	
	public void startServer() {
		server.start();
	}

	public void stopServer() {
		server.stop();
	}
	
}
