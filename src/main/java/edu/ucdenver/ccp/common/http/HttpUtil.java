package edu.ucdenver.ccp.common.http;

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
