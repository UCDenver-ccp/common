package edu.ucdenver.ccp.util.test;

import java.io.File;
import java.io.IOException;

import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import edu.ucdenver.ccp.util.file.FileUtil;

/**
 * Helper class that launches mock ftp server for use with unit tests.
 * 
 * @author malenkiy
 */
public class MockFtpServer {
	public static String USER_NAME = "anonymous";
	public static String PASSWORD = "password";
	public static String HOME_DIRECTORY = "/";

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
	
	
	public void startServer() {
		server.start();
	}

	public void stopServer() {
		server.stop();
	}
	
}
