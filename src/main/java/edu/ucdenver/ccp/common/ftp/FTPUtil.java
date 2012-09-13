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

/**
 * Utility method for downloading files via FTP
 * 
 * @author bill
 * 
 */
public class FTPUtil {
	/**
	 * Used to log FTP progress/status
	 */
	private static final Logger logger = Logger.getLogger(FTPUtil.class);
	/**
	 * This value is used to set the FTP buffer size
	 */
	private static final int BUFFER_SIZE = 32768000;
	/**
	 * Used for username and password if none are provided
	 */
	private static final String ANONYMOUS = "anonymous";

	/**
	 * This enum allows the user to specify the FTP download mode. TODO: Consider renaming
	 * FtpDownloadMode
	 * 
	 * @author bill
	 * 
	 */
	public static enum FileType {
		/**
		 * Indicates that the file should be downloaded using the ASCII mode
		 */
		ASCII(FTPClient.ASCII_FILE_TYPE),
		/**
		 * Indicates that the file should be downloaded using the BINARY mode
		 */
		BINARY(FTPClient.BINARY_FILE_TYPE);
		/**
		 * Integer constant indicating the download mode (either FTPClient.ASCII_FILE_TYPE or
		 * FTPClient.BINARY_FILE_TYPE)
		 */
		private final int type;

		/**
		 * Private constructor for the FileType enum
		 * 
		 * @param type
		 */
		private FileType(int type) {
			this.type = type;
		}

		/**
		 * @return the FTP file type (download mode) to use
		 */
		public int type() {
			return type;
		}

	}

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

	/**
	 * Initializes an <code>FTPClient</code> to access the server at the input location
	 * 
	 * @param ftpServer
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 */
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

	/**
	 * Downloads the requested file via FTP
	 * 
	 * @param ftpServer
	 * @param port
	 * @param remotePath
	 * @param fileName
	 * @param fileType
	 * @param workDirectory
	 * @param username
	 * @param password
	 * @return
	 * @throws IOException
	 */
	public static File downloadFile(String ftpServer, int port, String remotePath, String fileName, FileType fileType,
			File workDirectory, String username, String password) throws IOException {
		FTPClient ftpClient = null;
		File downloadedFile = null;
		try {
			FTPUtil.initializeFtpClient(ftpServer, port, username, password);
			ftpClient = FTPUtil.initializeFtpClient(ftpServer, port, username, password);
			FTPUtil.navigateToFtpDirectory(ftpClient, remotePath);
			downloadedFile = FTPUtil.downloadFile(ftpClient, fileName, fileType, workDirectory);
		}
		finally {
			FTPUtil.closeFtpClient(ftpClient);
		}
		return downloadedFile;
	}

	/**
	 * Downloads the requested file via FTP using the anonymous user
	 * 
	 * @param ftpServer
	 * @param remotePath
	 * @param fileName
	 * @param fileType
	 * @param workDirectory
	 * @return
	 * @throws IOException
	 */
	public static File downloadFile(String ftpServer, String remotePath, String fileName, FileType fileType,
			File workDirectory) throws IOException {
		FTPClient ftpClient = FTPUtil.initializeFtpClient(ftpServer, -1, ANONYMOUS, ANONYMOUS);
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
		logger.info("Downloading file via FTP: " + ftpFileName + " to " + outputFile);
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

		Collection<String> remoteFiles = Arrays.asList(ftpClient.listNames());
		if (!remoteFiles.contains(ftpFileName)) 
			throw new IOException(String.format("File %s is not available on ftp server %s. Ftp download failed...", ftpFileName, ftpClient.getRemoteAddress()));
		
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
		if (fileSuffix == null)
			return Arrays.asList(ftpClient.listFiles());

		return Arrays.asList(ftpClient.listFiles(String.format("*%s", fileSuffix)));
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
