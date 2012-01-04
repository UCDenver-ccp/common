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

import edu.ucdenver.ccp.common.file.FileArchiveUtil;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.ftp.FTPUtil;
import edu.ucdenver.ccp.common.http.HttpUtil;

/**
 * This class works in conjunction with the <code>FtpDownload</code> and <code>HttpDownload</code>
 * annotations to facilitate download of files and referencing (via the annotations) of those files
 * to member variables of a class.
 * 
 * @author bill
 * 
 */
public class DownloadUtil {

	/**
	 * This method works in conjunction with the {@link FtpDownload} and {@link HttpDownload} annotation to automatically download
	 * a specified file and assign it to the annotated object field.
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
				file = handleFtpDownload(workDirectory, field.getAnnotation(FtpDownload.class), userName, password, clean);
			else if (field.isAnnotationPresent(HttpDownload.class))
				file = handleHttpDownload(workDirectory, field.getAnnotation(HttpDownload.class), clean);
			
			if (file != null)
				assignField(object, field, file);
		}
	}

	/**
	 * This method works in conjunction with the {@link FtpDownload} and {@link HttpDownload} class annotation 
	 * to automatically download a specified file. 
	 * @param klass on which annotation is present
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
			f =  handleHttpDownload(workDirectory, klass.getAnnotation(HttpDownload.class), clean);
		else if (klass.isAnnotationPresent(FtpDownload.class))
			f =  handleFtpDownload(workDirectory, klass.getAnnotation(FtpDownload.class), userName, password, clean);		
		
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
	private static File handleHttpDownload(File workDirectory, HttpDownload httpd, boolean clean)
			throws IOException, IllegalArgumentException {
		URL url = new URL(httpd.url());
		String fileName = httpd.fileName();
		if (fileName.isEmpty())
			fileName = HttpUtil.getFinalPathElement(url);
		File downloadedFile = FileUtil.appendPathElementsToDirectory(workDirectory, fileName);
		if (!fileExists(downloadedFile, clean)) {
			downloadedFile = HttpUtil.downloadFile(url, downloadedFile);
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
	private static File unpackFile(File workDirectory, boolean clean,
			File downloadedFile) throws IOException {
		File unpackedDownloadedFile = unpackDownloadedFile(workDirectory, clean, downloadedFile);
		return unpackedDownloadedFile;
	}

	/**
	 * Assign file to the specified File field
	 * 
	 * @param object in which field exists
	 * @param field field to assign
	 * @param file field value to assign
	 * @throws IllegalAccessException if errors occur while assigning field value
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
	private static File handleFtpDownload(File workDirectory, FtpDownload ftpd, String userName,
			String password, boolean clean) throws IOException, IllegalArgumentException {
		String uName = (userName == null) ? ftpd.username() : userName;
		String pWord = (password == null) ? ftpd.password() : password;
		File downloadedFile = FileUtil.appendPathElementsToDirectory(workDirectory, ftpd.filename());
		if (!fileExists(downloadedFile, clean)) {
			downloadedFile = FTPUtil.downloadFile(ftpd.server(), ftpd.port(), ftpd.path(), ftpd.filename(),
					ftpd.filetype(), workDirectory, uName, pWord);
		}
		return unpackFile(workDirectory, clean, downloadedFile);
	}

	/**
	 * If clean is true, then this method always returns false (and the file is deleted). If clean
	 * is false, then this method returns downloadedFile.exists()
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
			if (unzippedFile != null)
				FileUtil.deleteFile(unzippedFile);
			return false;
		}
		return downloadedFile.exists() || (unzippedFile != null && unzippedFile.exists());
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
