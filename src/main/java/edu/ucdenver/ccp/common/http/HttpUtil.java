package edu.ucdenver.ccp.common.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.common.string.StringConstants;

public class HttpUtil {

	private static final Logger logger = Logger.getLogger(HttpUtil.class);

	/**
	 * This method retrieves a file from the input URL and stores it locally in the specified
	 * storage directory
	 * 
	 * @param fileUrl
	 * @param storageDirectory
	 * @return
	 * @throws IOException
	 */
	public static File downloadFile(URL fileUrl, File localFile) throws IOException {
		logger.info(String.format("Downloading file via HTTP: %s", fileUrl.toString()));
		FileUtil.validateDirectory(localFile.getParentFile());
		InputStream httpStream = null;
		try {
			URLConnection conn = fileUrl.openConnection();
			httpStream = conn.getInputStream();
			FileUtil.copy(httpStream, localFile);
			return localFile;
		} finally {
			IOUtils.closeQuietly(httpStream);
		}
	}

	/**
	 * Returns the final path element from the input URL, i.e. the thing to the right of the last
	 * forward slash
	 * 
	 * @param fileUrl
	 * @return
	 */
	public static String getFinalPathElement(URL fileUrl) {
		String fileName = fileUrl.toString().substring(
				fileUrl.toString().lastIndexOf(StringConstants.FORWARD_SLASH) + 1);
		return fileName;
	}

}
