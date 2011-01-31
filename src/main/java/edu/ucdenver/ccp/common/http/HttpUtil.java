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

/**
 * Utility method for working with HTTP requests
 * 
 * @author bill
 * 
 */
public class HttpUtil {

	/**
	 * Used to log the download of a file via HTTP
	 */
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
