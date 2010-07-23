package edu.ucdenver.ccp.common.download;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketException;

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.ftp.FTPUtil;

public class DownloadUtil {

	/**
	 * This method works in conjunction with the FtpDownload annotation to automatically download
	 * via FTP a specified file.
	 * 
	 * @param object
	 * @param workDirectory
	 * @param clean
	 * @throws SocketException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void download(Object object, File workDirectory, String userName, String password, boolean clean)
			throws SocketException, IOException, IllegalArgumentException, IllegalAccessException {
		for (Field field : object.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(FtpDownload.class)) {
				FtpDownload ftpd = field.getAnnotation(FtpDownload.class);
				if (userName == null)
					userName = ftpd.username();
				if (password == null)
					password = ftpd.password();
				File downloadedFile = FileUtil.appendPathElementsToDirectory(workDirectory, ftpd.filename());
				if (!fileExists(downloadedFile, clean)) {
					downloadedFile = FTPUtil.downloadFile(ftpd.server(), ftpd.port(), ftpd.path(), ftpd.filename(),
							ftpd.filetype(), workDirectory, userName, password);
				}
				field.setAccessible(true);
				field.set(object, downloadedFile);
			}
		}
	}

	/**
	 * If clean is true, then this method always returns false (and the file is deleted). If clean
	 * is false, then this method returns downloadedFile.exists()
	 * 
	 * @param downloadedFile
	 * @param clean
	 * @return
	 */
	private static boolean fileExists(File downloadedFile, boolean clean) {
		if (clean) {
			downloadedFile.delete();
			return false;
		}
		return downloadedFile.exists();
	}
}
