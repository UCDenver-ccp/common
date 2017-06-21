package edu.ucdenver.ccp.common.download;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;

import lombok.Data;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.FileArchiveUtil;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.ftp.FTPUtil;
import edu.ucdenver.ccp.common.ftp.FTPUtil.FileType;
import edu.ucdenver.ccp.common.http.HttpUtil;

/**
 * This class works in conjunction with the <code>FtpDownload</code> and
 * <code>HttpDownload</code> annotations to facilitate download of files and
 * referencing (via the annotations) of those files to member variables of a
 * class.
 * 
 * Download completion is indicated by writing a "semaphore" file that is the
 * file name with .ready appended to it in the same directory as the downloaded
 * file.
 * 
 * @author bill
 * 
 */
public class DownloadUtil {

	private static final Logger logger = Logger.getLogger(DownloadUtil.class);

	/**
	 * File suffix used on the "ready semaphore" file that indicates that a
	 * downloaded file has been downloaded completely and is now ready for use.
	 */
	private static final String READY_SEMAPHORE_SUFFIX = ".ready";

	/**
	 * This method works in conjunction with the {@link FtpDownload} and
	 * {@link HttpDownload} annotation to automatically download a specified
	 * file and assign it to the annotated object field.
	 * 
	 * @param object
	 * @param workDirectory
	 * @param userName
	 * @param password
	 * @param clean
	 * @throws SocketException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void download(Object object, File workDirectory, String userName, String password, boolean clean)
			throws SocketException, IOException, IllegalArgumentException, IllegalAccessException {
		for (Field field : object.getClass().getDeclaredFields()) {
			File file = null;
			if (field.isAnnotationPresent(FtpDownload.class)) {
				file = handleFtpDownload(workDirectory, field.getAnnotation(FtpDownload.class), userName, password,
						clean);
			} else if (field.isAnnotationPresent(HttpDownload.class)) {
				file = handleHttpDownload(workDirectory, field.getAnnotation(HttpDownload.class), clean);
			}
			// System.out.println("Downloaded file: " + file.getName());
			if (file != null) {
				assignField(object, field, file);
				// if (clean || !readySemaphoreFileExists(file)) {
				// // if clean = false then it might already exist
				// writeReadySemaphoreFile(file);
				// }
			}
		}
	}

	/**
	 * The creation of a semaphore file indicates the success of a download. It
	 * also includes metadata regarding the file that was downloaded, including:
	 * the date of the download, the date of the file on the server if
	 * available, the size of the file, the name of the file, the file URI
	 * 
	 * @param file
	 */
	public static void writeReadySemaphoreFile(File file, URL fileUrl) {
		try {
			if (!getReadySemaphoreFile(file).exists()) {
				if (!getReadySemaphoreFile(file).createNewFile()) {
					throw new RuntimeException("Semaphore file could not be created b/c it already exists: "
							+ getReadySemaphoreFile(file).getAbsolutePath());
				}

				Calendar lastModifiedCal = Calendar.getInstance();
				lastModifiedCal.setTimeInMillis(file.lastModified());
				DownloadMetadata dmd = new DownloadMetadata(Calendar.getInstance(), file, file.length(),
						lastModifiedCal, fileUrl);
				dmd.writePropertiesFile(getReadySemaphoreFile(file));
			}
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean readySemaphoreFileExists(File file) {
		return getReadySemaphoreFile(file).exists();
	}

	public static File getReadySemaphoreFile(File file) {
		return new File(file.getAbsolutePath() + READY_SEMAPHORE_SUFFIX);
	}

	/**
	 * This method works in conjunction with the {@link FtpDownload} and
	 * {@link HttpDownload} class annotation to automatically download a
	 * specified file.
	 * 
	 * @param klass
	 *            on which annotation is present
	 * @param workDirectory
	 * @param userName
	 * @param password
	 * @param clean
	 * @return downloaded File
	 * @throws SocketException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static File download(Class<?> klass, File workDirectory, String userName, String password, boolean clean)
			throws SocketException, IOException, IllegalArgumentException {
		File f = null;

		if (klass.isAnnotationPresent(HttpDownload.class)) {
			f = handleHttpDownload(workDirectory, klass.getAnnotation(HttpDownload.class), clean);
		} else if (klass.isAnnotationPresent(FtpDownload.class)) {
			f = handleFtpDownload(workDirectory, klass.getAnnotation(FtpDownload.class), userName, password, clean);
		}
		// if (f != null) {
		// if (clean || !readySemaphoreFileExists(f)) {
		// // if clean = false then it might already exist
		// writeReadySemaphoreFile(f);
		// }
		// }
		return f;
	}

	/**
	 * This method works in conjunction with the {@link FtpDownload} and
	 * {@link HttpDownload} class annotation to automatically download a
	 * specified file annotated to a particular field
	 * 
	 * @param field
	 * @param workDirectory
	 * @param userName
	 * @param password
	 * @param clean
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static File download(Field field, File workDirectory, String userName, String password, boolean clean)
			throws SocketException, IOException, IllegalArgumentException {
		File f = null;

		if (field.isAnnotationPresent(HttpDownload.class)) {
			f = handleHttpDownload(workDirectory, field.getAnnotation(HttpDownload.class), clean);
		} else if (field.isAnnotationPresent(FtpDownload.class)) {
			f = handleFtpDownload(workDirectory, field.getAnnotation(FtpDownload.class), userName, password, clean);
		}
		// if (f != null) {
		// if (clean || !readySemaphoreFileExists(f)) {
		// // if clean = false then it might already exist
		// writeReadySemaphoreFile(f);
		// }
		// }
		return f;
	}

	/**
	 * Iterates through FtpDownload and HttpDownload annotations and prints to
	 * the designated OutputStream the locations of the files that will be
	 * downloaded.
	 * 
	 * @throws IOException
	 */
	public static void printFilesToDownload(Class<?> cls, BufferedWriter writer) throws IOException {
		for (Field field : cls.getDeclaredFields()) {
			if (field.isAnnotationPresent(FtpDownload.class)) {
				writer.write(getFtpFileToDownload(field, field.getAnnotation(FtpDownload.class)) + "\n");
			} else if (field.isAnnotationPresent(HttpDownload.class)) {
				writer.write(getHttpFileToDownload(field, field.getAnnotation(HttpDownload.class)) + "\n");
			}
		}
	}

	private static String getHttpFileToDownload(Field field, HttpDownload httpd) {
		return "#" + field.getName() + "\nwget http://" + httpd.url();
	}

	private static String getFtpFileToDownload(Field field, FtpDownload ftpd) {
		if (ftpd.port() > 0) {
			return "# " + field.getName() + "\nwget ftp://" + ftpd.server() + ":" + ftpd.port() + "/" + ftpd.path()
					+ "/" + ftpd.filename();
		} else {
			return "# " + field.getName() + "\nwget ftp://" + ftpd.server() + "/" + ftpd.path()
					+ ((ftpd.path().endsWith("/")) ? "" : "/") + ftpd.filename();
		}
	}

	/**
	 * This method works in conjunction with the HttpDownload annotation to
	 * automatically download via HTTP the specified file
	 * 
	 * @param workDirectory
	 * @param field
	 * @param clean
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	private static File handleHttpDownload(File workDirectory, HttpDownload httpd, boolean clean)
			throws IOException, IllegalArgumentException {
		URL url = new URL(httpd.url());
		String fileName = httpd.fileName();
		String targetFileName = (httpd.targetFileName().length() > 0) ? httpd.targetFileName() : null;
		File targetFile = (targetFileName == null) ? null : new File(workDirectory, targetFileName);
		if (fileName.isEmpty()) {
			fileName = HttpUtil.getFinalPathElement(url);
		}
		File downloadedFile = FileUtil.appendPathElementsToDirectory(workDirectory, fileName);
		if (!fileExists(downloadedFile, targetFile, clean, httpd.decompress())) {
			long startTime = System.currentTimeMillis();
			downloadedFile = HttpUtil.downloadFile(url, downloadedFile);
			long duration = System.currentTimeMillis() - startTime;
			logger.info("Duration of " + downloadedFile.getName() + " download: " + (duration / (1000 * 60)) + "min");
		}
		if (httpd.decompress()) {
			File unpackedFile = unpackFile(workDirectory, clean, downloadedFile, targetFileName);
			if (unpackedFile != null) {
				writeReadySemaphoreFile(unpackedFile, url);
			}
			return unpackedFile;
		}
		if (clean || !readySemaphoreFileExists(downloadedFile)) {
			if (downloadedFile != null) {
				writeReadySemaphoreFile(downloadedFile, url);
			}
		}
		return downloadedFile;
	}

	/**
	 * Unpacks (unzips) the downloaded file
	 * 
	 * @param workDirectory
	 * @param clean
	 * @param downloadedFile
	 * @throws IOException
	 */
	private static File unpackFile(File workDirectory, boolean clean, File downloadedFile, String targetFileName)
			throws IOException {
		File unpackedDownloadedFile = unpackDownloadedFile(workDirectory, clean, downloadedFile, targetFileName);
		return unpackedDownloadedFile;
	}

	/**
	 * Assign file to the specified File field
	 * 
	 * @param object
	 *            in which field exists
	 * @param field
	 *            field to assign
	 * @param file
	 *            field value to assign
	 * @throws IllegalAccessException
	 *             if errors occur while assigning field value
	 */
	private static void assignField(Object object, Field field, File file) throws IllegalAccessException {
		field.setAccessible(true);
		field.set(object, file);
	}

	/**
	 * Unpacks the specified file if necessary
	 * 
	 * @param workDirectory
	 * @param clean
	 * @param downloadedFile
	 * @param targetFileName
	 *            this input parameter indicates the name of a particular file
	 *            inside a zip archive to retrieve. It should be set to null if
	 *            the compressed file is not a zip archive
	 * @return a reference to the unpacked File
	 * @throws IOException
	 */
	public static File unpackDownloadedFile(File workDirectory, boolean clean, File downloadedFile,
			String targetFileName) throws IOException {
		File unpackedFile = downloadedFile;
		File targetFile = (targetFileName == null) ? null : new File(workDirectory, targetFileName);
		if (fileNeedsUnzipping(downloadedFile, targetFile, clean)) {
			unpackedFile = FileArchiveUtil.unzip(downloadedFile, workDirectory, targetFileName);
		} else if (FileArchiveUtil.isZippedFile(downloadedFile)) {
			// File has already been downloaded and unzipped
			unpackedFile = FileArchiveUtil.getUnzippedFileReference(downloadedFile, targetFile);
		}
		return unpackedFile;
	}

	/**
	 * Downloads the specified file via FTP, places the file in the work
	 * directory
	 * 
	 * @param workDirectory
	 * @param field
	 * @param userName
	 * @param password
	 * @param clean
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	private static File handleFtpDownload(File workDirectory, FtpDownload ftpd, String userName, String password,
			boolean clean) throws IOException, IllegalArgumentException {
		String uName = (userName == null) ? ftpd.username() : userName;
		String pWord = (password == null) ? ftpd.password() : password;
		String targetFileName = (ftpd.targetFileName().length() > 0) ? ftpd.targetFileName() : null;
		FtpInfo ftpInfo = new FtpInfo(uName, pWord, ftpd.server(), ftpd.port(), ftpd.path(), ftpd.filename(),
				ftpd.filetype(), ftpd.decompress(), targetFileName);
		return handleFtpDownload(workDirectory, ftpInfo, clean);
	}

	/**
	 * Downloads the file as specified by the user-supplied {@link FtpInfo}.
	 * 
	 * @param workDirectory
	 * @param ftpInfo
	 * @param clean
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static File handleFtpDownload(File workDirectory, FtpInfo ftpInfo, boolean clean)
			throws IOException, IllegalArgumentException {
		String targetFileName = ftpInfo.getTargetFileName();
		File targetFile = (targetFileName == null) ? null : new File(workDirectory, targetFileName);
		File downloadedFile = FileUtil.appendPathElementsToDirectory(workDirectory, ftpInfo.getFilename());
		if (!fileExists(downloadedFile, targetFile, clean, ftpInfo.isDecompress())) {
			long startTime = System.currentTimeMillis();
			downloadedFile = FTPUtil.downloadFile(ftpInfo.getServer(), ftpInfo.getPort(), ftpInfo.getPath(),
					ftpInfo.getFilename(), ftpInfo.getFileType(), workDirectory, ftpInfo.getUsername(),
					ftpInfo.getPassword());
			long duration = System.currentTimeMillis() - startTime;
			logger.info("Duration of " + downloadedFile.getName() + " download: " + (duration / (1000 * 60)) + "min");
		}
		if (ftpInfo.isDecompress()) {
			downloadedFile = unpackFile(workDirectory, clean, downloadedFile, targetFileName);
		}
		if (clean || !readySemaphoreFileExists(downloadedFile)) {
			writeReadySemaphoreFile(downloadedFile, ftpInfo.getUrl());
		}
		return downloadedFile;
	}

	/**
	 * Utility class to hold the requisite information to download a file via
	 * FTP.
	 *
	 */
	@Data
	public static class FtpInfo {
		private final String username;
		private final String password;
		private final String server;
		private final int port;
		private final String path;
		private final String filename;
		private final FileType fileType;
		private final boolean decompress;
		private final String targetFileName;

		public URL getUrl() throws MalformedURLException {
			return new URL(
					"ftp://" + server + (port > 0 ? ":" + Integer.toString(port) : "") + "/" + path + "/" + filename);
		}
	}

	/**
	 * If clean is true, then this method always returns false (and the file is
	 * deleted). If clean is false, then this method returns
	 * downloadedFile.exists()
	 * 
	 * If the downloaded file is present but the ready-semaphore file is not
	 * present then this processes waits for the semaphore file to appear. It is
	 * assumed that the downloaded file is still in the process of being
	 * downloaded by another thread.
	 * 
	 * @param downloadedFile
	 * @param targetFile
	 *            the target file is a particular file to retrieve from inside a
	 *            downloaded zip archive.
	 * @param clean
	 * @return
	 */
	public static boolean fileExists(File downloadedFile, File targetFile, boolean clean, boolean decompress) {
		File unzippedFile = null;
		if (decompress && FileArchiveUtil.isZippedFile(downloadedFile)) {
			unzippedFile = FileArchiveUtil.getUnzippedFileReference(downloadedFile, targetFile);
		}
		if (clean) {
			FileUtil.deleteFile(downloadedFile);
			if (unzippedFile != null) {
				FileUtil.deleteFile(unzippedFile);
				FileUtil.deleteFile(getReadySemaphoreFile(unzippedFile));
			} else {
				FileUtil.deleteFile(getReadySemaphoreFile(downloadedFile));
			}
			return false;
		}
		boolean fileIsPresent = downloadedFile.exists() || (unzippedFile != null && unzippedFile.exists());
		if (fileIsPresent) {
			waitForReadySemaphoreFile((unzippedFile == null) ? downloadedFile : unzippedFile);
		}
		return fileIsPresent;
	}

	/**
	 * Returns when the semaphore file is present. Checks once a minute for its
	 * existence.
	 * 
	 * @param file
	 */
	private static void waitForReadySemaphoreFile(File file) {
		while (!readySemaphoreFileExists(file)) {
			logger.info("Waiting for another process to finish downloading the file. Will continue when "
					+ getReadySemaphoreFile(file) + " is present.");
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				throw new RuntimeException(
						"Error while waiting for another process to download a file: " + file.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * If the input file is not a zip file, then this method return false
	 * immediately. Otherwise if clean is true, it deletes any previous unzipped
	 * version of the file and return true. If clean is false, it simply returns
	 * based on existence of the unzipped file (false if it exists, true if it
	 * doesn't).
	 * 
	 * @param zippedFile
	 * @param clean
	 * @return
	 */
	private static boolean fileNeedsUnzipping(File zippedFile, File targetFile, boolean clean) {
		if (!FileArchiveUtil.isZippedFile(zippedFile)) {
			return false;
		}
		File unzippedFile = FileArchiveUtil.getUnzippedFileReference(zippedFile, targetFile);
		if (clean) {
			FileUtil.deleteFile(unzippedFile);
			return true;
		}
		return !unzippedFile.exists();
	}

}
