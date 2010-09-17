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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.FileUtil;

public class FTPUtil {
	private static final Logger logger = Logger.getLogger(FTPUtil.class);
	private static final int BUFFER_SIZE = 32768000;
	private static final String ANONYMOUS = "anonymous";

	public static enum FileType {
		ASCII(FTPClient.ASCII_FILE_TYPE), BINARY(FTPClient.BINARY_FILE_TYPE);
		private final int type;

		public int type() {
			return type;
		}

		private FileType(int type) {
			this.type = type;
		}
	};

	/**
	 * Initializes a FTPClient
	 * 
	 * @param ftpServer
	 * @param ftpUsername
	 * @param ftpPassword
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 */
	public static FTPClient initializeFtpClient(String ftpServer, String ftpUsername, String ftpPassword)
			throws SocketException, IOException {
		FTPClient ftpClient = connect(ftpServer, -1);
		return login(ftpServer, ftpUsername, ftpPassword, ftpClient);
	}

	/**
	 * Initializes a FTPClient to a specific port
	 * 
	 * @param ftpServer
	 * @param port
	 * @param ftpUsername
	 * @param ftpPassword
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 */
	public static FTPClient initializeFtpClient(String ftpServer, int port, String ftpUsername, String ftpPassword)
			throws SocketException, IOException {
		FTPClient ftpClient = connect(ftpServer, port);
		return login(ftpServer, ftpUsername, ftpPassword, ftpClient);
	}

	public static FTPClient initializeFtpClient(String ftpServer) throws SocketException, IOException {
		FTPClient ftpClient = connect(ftpServer, -1);
		return login(ftpServer, ANONYMOUS, ANONYMOUS, ftpClient);
	}

	/**
	 * Makes the FTP connection using the specified port. If port < 0, then the default port is
	 * used.
	 * 
	 * @param ftpServer
	 * @param port
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 */
	private static FTPClient connect(String ftpServer, int port) throws SocketException, IOException {
		FTPClient ftpClient = new FTPClient();
		if (port > 0)
			ftpClient.connect(ftpServer, port);
		else
			ftpClient.connect(ftpServer);
		return ftpClient;
	}

	/**
	 * Logs into the specified ftp server using the given username and password pairing.
	 * 
	 * @param ftpServer
	 * @param ftpUsername
	 * @param ftpPassword
	 * @param ftpClient
	 * @return
	 * @throws IOException
	 */
	private static FTPClient login(String ftpServer, String ftpUsername, String ftpPassword, FTPClient ftpClient)
			throws IOException {
		ftpClient.login(ftpUsername, ftpPassword);
		checkFtpConnection(ftpClient, ftpServer, ftpUsername, ftpPassword);
		logger.info(String.format("Connected to FTP Server: %s.", ftpServer));
		return ftpClient;
	}

	/**
	 * Checks the FTPClient response code. If the connection was refused, then an IOException is
	 * thrown.
	 * 
	 * @param ftpServer
	 * @param ftpUsername
	 * @param ftpPassword
	 * @param ftpClient
	 * @throws IOException
	 */
	private static void checkFtpConnection(FTPClient ftpClient, String ftpServer, String ftpUsername, String ftpPassword)
			throws IOException {
		if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
			closeFtpClient(ftpClient);
			throw new IOException(String.format("FTP server (%s) refused connection for user:%s password:%s",
					ftpServer, ftpUsername, ftpPassword));
		}
	}

	/**
	 * Attempts to close the FTPClient
	 * 
	 * @param ftpClient
	 * @throws IOException
	 */
	public static void closeFtpClient(FTPClient ftpClient) throws IOException {
		logger.info("Closing FTP connection.");
		if (ftpClient != null) {
			if (ftpClient.isConnected()) {
				ftpClient.logout();
				ftpClient.disconnect();
			}
		}
	}

	public static File downloadFile(String ftpServer, int port, String remotePath, String fileName, FileType fileType,
			File workDirectory, String username, String password) throws IOException {
		FTPClient ftpClient = FTPUtil.initializeFtpClient(ftpServer, port, username, password);
		FTPUtil.navigateToFtpDirectory(ftpClient, remotePath);
		File downloadedFile = FTPUtil.downloadFile(ftpClient, fileName, fileType, workDirectory);
		FTPUtil.closeFtpClient(ftpClient);
		return downloadedFile;
	}

	public static File downloadFile(String ftpServer, String remotePath, String fileName, FileType fileType,
			File workDirectory) throws IOException {
		FTPClient ftpClient = FTPUtil.initializeFtpClient(ftpServer, -1, "anonymous", "anonymous");
		FTPUtil.navigateToFtpDirectory(ftpClient, remotePath);
		File downloadedFile = FTPUtil.downloadFile(ftpClient, fileName, fileType, workDirectory);
		FTPUtil.closeFtpClient(ftpClient);
		return downloadedFile;
	}

	/**
	 * Downloads a file by name from the connected FTP server to the local storage directory.
	 * 
	 * @param ftpClient
	 * @param ftpFileName
	 * @param localStorageDirectory
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static File downloadFile(FTPClient ftpClient, String ftpFileName, FTPUtil.FileType ftpFileType,
			File localStorageDirectory) throws FileNotFoundException, IOException {
		OutputStream localOutputStream = null;
		File outputFile = FileUtil.appendPathElementsToDirectory(localStorageDirectory, ftpFileName);
		try {
			localOutputStream = new FileOutputStream(outputFile);
			downloadFile(ftpClient, ftpFileName, ftpFileType, localOutputStream);
			return outputFile;
		} finally {
			if (localOutputStream != null) {
				localOutputStream.close();
			}
		}
	}

	/**
	 * Downloads a file by name from the connection FTP server to the OutputStream
	 * 
	 * @param ftpClient
	 * @param ftpFileName
	 * @param ftpFileType
	 * @param localOutputStream
	 * @throws IOException
	 */
	public static void downloadFile(FTPClient ftpClient, String ftpFileName, FTPUtil.FileType ftpFileType,
			OutputStream localOutputStream) throws IOException {
		checkFtpClientConnection(ftpClient);
		logger.info(String.format("Downloading file: %s", ftpFileName));
		ftpClient.setFileType(ftpFileType.type());
		ftpClient.enterLocalPassiveMode();
		ftpClient.setBufferSize(BUFFER_SIZE);

		if (!ftpClient.retrieveFile(ftpFileName, localOutputStream)) {
			throw new IOException(String.format("Download failed for file: %s", ftpFileName));
		}
	}

	/**
	 * Checks that the FTPClient is connected. If it isn't, an IOException is thrown.
	 * 
	 * @param ftpClient
	 * @param ftpFileName
	 * @throws IOException
	 */
	private static void checkFtpClientConnection(FTPClient ftpClient) throws IOException {
		if (!ftpClient.isConnected()) {
			throw new IOException(String.format("FTP connection expected open, but is closed."));
		}
	}

	/**
	 * Changes the directory of the FTPClient on the FTP server
	 * 
	 * @param ftpClient
	 * @param directoryOnFtpServer
	 * @throws IOException
	 */
	public static void navigateToFtpDirectory(FTPClient ftpClient, String directoryOnFtpServer) throws IOException {
		logger.info(String.format("Changing to new directory on server: %s", directoryOnFtpServer));
		ftpClient.changeWorkingDirectory(directoryOnFtpServer);
	}

	/**
	 * Downloads all available files from the current FTP directory.
	 * 
	 * @param ftpClient
	 * @param fileType
	 * @param localStorageDirectory
	 * @throws IOException
	 */
	public static void downloadAllFiles(FTPClient ftpClient, String fileSuffix, FTPUtil.FileType fileType,
			File localStorageDirectory) throws IOException {
		downloadAllFiles(ftpClient, new HashSet<String>(), fileSuffix, fileType, localStorageDirectory);
	}

	/**
	 * Downloads all available files from the current FTP directory. If a file suffix is specified,
	 * only files with that suffix are considered for download. Files with names in the
	 * fileNamesToLeaveOnServer set are not downloaded.
	 * 
	 * @param ftpClient
	 * @param fileNamesToLeaveOnServer
	 * @param fileSuffix
	 * @param fileType
	 * @param localStorageDirectory
	 * @throws IOException
	 */
	public static Collection<File> downloadAllFiles(FTPClient ftpClient, Set<String> fileNamesToLeaveOnServer,
			String fileSuffix, FTPUtil.FileType fileType, File localStorageDirectory) throws IOException {
		checkFtpClientConnection(ftpClient);
		Collection<File> downloadedFiles = new ArrayList<File>();
		List<FTPFile> filesAvailableForDownload = getFilesAvailableForDownload(ftpClient, fileSuffix);
		for (FTPFile fileOnFtpServer : filesAvailableForDownload) {
			if (!fileNamesToLeaveOnServer.contains(fileOnFtpServer.getName())) {
				downloadedFiles
						.add(downloadFile(ftpClient, fileOnFtpServer.getName(), fileType, localStorageDirectory));
			}
		}
		return downloadedFiles;
	}

	/**
	 * Downloads all files that are on the FTP server that are not already stored locally in the
	 * specified localStorageDirectory.
	 * 
	 * @param ftpClient
	 * @param fileNamesToLeaveOnServer
	 * @param fileSuffix
	 * @param fileType
	 * @param localStorageDirectory
	 * @throws IOException
	 */
	public static Collection<File> downloadMissingFiles(FTPClient ftpClient, String fileSuffix,
			FTPUtil.FileType fileType, File localStorageDirectory) throws IOException {
		Set<String> locallyStoredFileNames = new HashSet<String>(Arrays.asList(localStorageDirectory.list()));
		return downloadAllFiles(ftpClient, locallyStoredFileNames, fileSuffix, fileType, localStorageDirectory);
	}

	/**
	 * Downloads any file that is not already present in the specified local directory from the
	 * specified FTP server
	 * 
	 * @param localStorageDirectory
	 * @param fileSuffix
	 * @param fileType
	 * @param ftpServer
	 * @param ftpUsername
	 * @param ftpPassword
	 * @throws SocketException
	 * @throws IOException
	 */
	public static Collection<File> syncLocalDirectoryWithFtpDirectory(FTPClient ftpClient, File localStorageDirectory,
			String fileSuffix, FileType fileType) throws SocketException, IOException {
		FileUtil.validateDirectory(localStorageDirectory);
		return FTPUtil.downloadMissingFiles(ftpClient, fileSuffix, fileType, localStorageDirectory);
	}

	/**
	 * Returns the list of files available for download in the current FTP directory. If fileSuffix
	 * is null, all files are returned. Otherwise files that have the specified suffix are returned.
	 * 
	 * @param ftpClient
	 * @param fileSuffix
	 * @return
	 * @throws IOException
	 */
	private static List<FTPFile> getFilesAvailableForDownload(FTPClient ftpClient, String fileSuffix)
			throws IOException {
		if (fileSuffix == null) {
			return Arrays.asList(ftpClient.listFiles());
		} else {
			return Arrays.asList(ftpClient.listFiles(String.format("*%s", fileSuffix)));
		}
	}

	/**
	 * Pauses the current thread for n seconds depending on the input. This can be useful if a web
	 * resource limits how often you can retrieve files.
	 * 
	 * @param seconds
	 */
	public static void pause(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
