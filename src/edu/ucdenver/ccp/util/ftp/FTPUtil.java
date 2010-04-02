package edu.ucdenver.ccp.util.ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class FTPUtil {
	private static final Logger logger = Logger.getLogger(FTPUtil.class);

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
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(ftpServer);
		ftpClient.login(ftpUsername, ftpPassword);
		checkFtpConnection(ftpClient, ftpServer, ftpUsername, ftpPassword);
		logger.info(String.format("Connected to FTP ServeR: %s.", ftpServer));
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
			throw new IOException(String.format("FTP server (%s) refused connection for user:%s password:%s", ftpServer,
					ftpUsername, ftpPassword));
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
	 * Downloads a file by name from the connected FTP server to the local storage directory.
	 * 
	 * @param ftpClient
	 * @param ftpFileName
	 * @param localStorageDirectory
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void downloadFile(FTPClient ftpClient, String ftpFileName, int ftpFileType, File localStorageDirectory)
			throws FileNotFoundException, IOException {
		OutputStream localOutputStream = null;
		try {
			localOutputStream = new FileOutputStream(localStorageDirectory.getAbsolutePath() + File.separator
					+ ftpFileName);
			downloadFile(ftpClient, ftpFileName, ftpFileType, localOutputStream);
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
	public static void downloadFile(FTPClient ftpClient, String ftpFileName, int ftpFileType,
			OutputStream localOutputStream) throws IOException {
		checkFtpClientConnection(ftpClient);
		logger.info(String.format("Downloading file: %s", ftpFileName));
		ftpClient.setFileType(ftpFileType);
		ftpClient.enterLocalPassiveMode();

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
		ftpClient.changeWorkingDirectory(directoryOnFtpServer);
		logger.info(String.format("Changed to new directory: %s", directoryOnFtpServer));
	}

	/**
	 * Downloads all available files from the current FTP directory.
	 * 
	 * @param ftpClient
	 * @param fileType
	 * @param localStorageDirectory
	 * @throws IOException
	 */
	public static void downloadAllFiles(FTPClient ftpClient, String fileSuffix, int fileType, File localStorageDirectory)
			throws IOException {
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
	public static void downloadAllFiles(FTPClient ftpClient, Set<String> fileNamesToLeaveOnServer, String fileSuffix,
			int fileType, File localStorageDirectory) throws IOException {
		checkFtpClientConnection(ftpClient);
		List<FTPFile> filesAvailableForDownload = getFilesAvailableForDownload(ftpClient, fileSuffix);
		for (FTPFile fileOnFtpServer : filesAvailableForDownload) {
			if (!fileNamesToLeaveOnServer.contains(fileOnFtpServer.getName())) {
				downloadFile(ftpClient, fileOnFtpServer.getName(), fileType, localStorageDirectory);
			}
		}
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

}
