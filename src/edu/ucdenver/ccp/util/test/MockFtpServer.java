package edu.ucdenver.ccp.util.test;

import org.mockftpserver.fake.*;
import org.mockftpserver.fake.filesystem.*;

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

	public void startServer() {
		server.start();
	}

	public void stopServer() {
		server.stop();
	}
	
}
