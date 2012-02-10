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

package edu.ucdenver.ccp.common.download;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketException;
import java.net.URL;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.calendar.CalendarUtil;
import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileArchiveUtil;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil.FileSuffixEnforcement;
import edu.ucdenver.ccp.common.file.FileWriterUtil.WriteMode;
import edu.ucdenver.ccp.common.ftp.FTPUtil;
import edu.ucdenver.ccp.common.http.HttpUtil;

/**
 * This class works in conjunction with the <code>FtpDownload</code> and <code>HttpDownload</code>
 * annotations to facilitate download of files and referencing (via the annotations) of those files
 * to member variables of a class.
 * 
 * Download completion is indicated by writing a "semaphore" file that is the file name with .ready
 * appended to it in the same directory as the downloaded file.
 * 
 * @author bill
 * 
 */
public class DownloadUtil {

	private static final Logger logger = Logger.getLogger(DownloadUtil.class);

	/**
	 * File suffix used on the "ready semaphore" file that indicates that a downloaded file has been
	 * downloaded completely and is now ready for use.
	 */
	private static final String READY_SEMAPHORE_SUFFIX = ".ready";

	/**
	 * This method works in conjunction with the {@link FtpDownload} and {@link HttpDownload}
	 * annotation to automatically download a specified file and assign it to the annotated object
	 * field.
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
			if (field.isAnnotationPresent(FtpDownload.class))
				file = handleFtpDownload(workDirectory, field.getAnnotation(FtpDownload.class), userName, password,
						clean);
			else if (field.isAnnotationPresent(HttpDownload.class))
				file = handleHttpDownload(workDirectory, field.getAnnotation(HttpDownload.class), clean);

			if (file != null) {
				assignField(object, field, file);
				if (clean) // if clean = false then it will already exist
					writeReadySemaphoreFile(file);
			}
		}
	}

	/**
	 * @param file
	 */
	private static void writeReadySemaphoreFile(File file) {
		try {
			if (!getReadySemaphoreFile(file).createNewFile())
				throw new RuntimeException("Semaphore file could not be created b/c it already exists: "
						+ getReadySemaphoreFile(file).getAbsolutePath());
			FileWriterUtil.printLines(CollectionsUtil.createList("Downloaded on " + CalendarUtil.getDateStamp("/")),
					getReadySemaphoreFile(file), CharacterEncoding.UTF_8, WriteMode.OVERWRITE,
					FileSuffixEnforcement.OFF);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean readySemaphoreFileExists(File file) {
		return getReadySemaphoreFile(file).exists();
	}

	private static File getReadySemaphoreFile(File file) {
		return new File(file.getAbsolutePath() + READY_SEMAPHORE_SUFFIX);
	}

	/**
	 * This method works in conjunction with the {@link FtpDownload} and {@link HttpDownload} class
	 * annotation to automatically download a specified file.
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

		if (klass.isAnnotationPresent(HttpDownload.class))
			f = handleHttpDownload(workDirectory, klass.getAnnotation(HttpDownload.class), clean);
		else if (klass.isAnnotationPresent(FtpDownload.class))
			f = handleFtpDownload(workDirectory, klass.getAnnotation(FtpDownload.class), userName, password, clean);

		if (f != null)
			if (clean) // if clean = false then it will already exist
				writeReadySemaphoreFile(f);
		return f;
	}

	/**
	 * This method works in conjunction with the {@link FtpDownload} and {@link HttpDownload} class
	 * annotation to automatically download a specified file annotated to a particular field
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

		if (field.isAnnotationPresent(HttpDownload.class))
			f = handleHttpDownload(workDirectory, field.getAnnotation(HttpDownload.class), clean);
		else if (field.isAnnotationPresent(FtpDownload.class))
			f = handleFtpDownload(workDirectory, field.getAnnotation(FtpDownload.class), userName, password, clean);

		if (f != null)
			if (clean) // if clean = false then it will already exist
				writeReadySemaphoreFile(f);
		return f;
	}

	/**
	 * This method works in conjunction with the HttpDownload annotation to automatically download
	 * via HTTP the specified file
	 * 
	 * @param workDirectory
	 * @param field
	 * @param clean
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	private static File handleHttpDownload(File workDirectory, HttpDownload httpd, boolean clean) throws IOException,
			IllegalArgumentException {
		URL url = new URL(httpd.url());
		String fileName = httpd.fileName();
		if (fileName.isEmpty())
			fileName = HttpUtil.getFinalPathElement(url);
		File downloadedFile = FileUtil.appendPathElementsToDirectory(workDirectory, fileName);
		if (!fileExists(downloadedFile, clean)) {
			long startTime = System.currentTimeMillis();
			downloadedFile = HttpUtil.downloadFile(url, downloadedFile);
			long duration = System.currentTimeMillis() - startTime;
			logger.info("Duration of " + downloadedFile.getName() + " download: " + (duration / (1000 * 60)) + "min");
		}
		return unpackFile(workDirectory, clean, downloadedFile);
	}

	/**
	 * Unpacks (unzips) the downloaded file
	 * 
	 * @param workDirectory
	 * @param clean
	 * @param downloadedFile
	 * @throws IOException
	 */
	private static File unpackFile(File workDirectory, boolean clean, File downloadedFile) throws IOException {
		File unpackedDownloadedFile = unpackDownloadedFile(workDirectory, clean, downloadedFile);
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
	 * @return a reference to the unpacked File
	 * @throws IOException
	 */
	public static File unpackDownloadedFile(File workDirectory, boolean clean, File downloadedFile) throws IOException {
		File unpackedFile = downloadedFile;
		if (fileNeedsUnzipping(downloadedFile, clean))
			unpackedFile = FileArchiveUtil.unzip(downloadedFile, workDirectory);
		else if (FileArchiveUtil.isZippedFile(downloadedFile))
			// File has already been downloaded and unzipped
			unpackedFile = FileArchiveUtil.getUnzippedFileReference(downloadedFile);
		return unpackedFile;
	}

	/**
	 * Downloads the specified file via FTP, places the file in the work directory
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
		File downloadedFile = FileUtil.appendPathElementsToDirectory(workDirectory, ftpd.filename());
		if (!fileExists(downloadedFile, clean)) {
			long startTime = System.currentTimeMillis();
			downloadedFile = FTPUtil.downloadFile(ftpd.server(), ftpd.port(), ftpd.path(), ftpd.filename(),
					ftpd.filetype(), workDirectory, uName, pWord);
			long duration = System.currentTimeMillis() - startTime;
			logger.info("Duration of " + downloadedFile.getName() + " download: " + (duration / (1000 * 60)) + "min");
		}
		return unpackFile(workDirectory, clean, downloadedFile);
	}

	/**
	 * If clean is true, then this method always returns false (and the file is deleted). If clean
	 * is false, then this method returns downloadedFile.exists()
	 * 
	 * If the downloaded file is present but the ready-semaphore file is not present then this
	 * processes waits for the semaphore file to appear. It is assumed that the downloaded file is
	 * still in the process of being downloaded by another thread.
	 * 
	 * @param downloadedFile
	 * @param clean
	 * @return
	 */
	public static boolean fileExists(File downloadedFile, boolean clean) {
		File unzippedFile = null;
		if (FileArchiveUtil.isZippedFile(downloadedFile))
			unzippedFile = FileArchiveUtil.getUnzippedFileReference(downloadedFile);
		if (clean) {
			FileUtil.deleteFile(downloadedFile);
			if (unzippedFile != null) {
				FileUtil.deleteFile(unzippedFile);
				FileUtil.deleteFile(getReadySemaphoreFile(unzippedFile));
			} else
				FileUtil.deleteFile(getReadySemaphoreFile(downloadedFile));
			return false;
		}
		boolean fileIsPresent = downloadedFile.exists() || (unzippedFile != null && unzippedFile.exists());
		if (fileIsPresent)
			waitForReadySemaphoreFile((unzippedFile == null) ? downloadedFile : unzippedFile);
		return fileIsPresent;
	}

	/**
	 * Returns when the semaphore file is present. Checks once a minute for its existence.
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
				throw new RuntimeException("Error while waiting for another process to download a file: "
						+ file.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * If the input file is not a zip file, then this method return false immediately. Otherwise if
	 * clean is true, it deletes any previous unzipped version of the file and return true. If clean
	 * is false, it simply returns based on existence of the unzipped file (false if it exists, true
	 * if it doesn't).
	 * 
	 * @param zippedFile
	 * @param clean
	 * @return
	 */
	private static boolean fileNeedsUnzipping(File zippedFile, boolean clean) {
		if (!FileArchiveUtil.isZippedFile(zippedFile))
			return false;
		File unzippedFile = FileArchiveUtil.getUnzippedFileReference(zippedFile);
		if (clean) {
			FileUtil.deleteFile(unzippedFile);
			return true;
		}
		return !unzippedFile.exists();
	}

}
