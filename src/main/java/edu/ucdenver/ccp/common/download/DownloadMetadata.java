package edu.ucdenver.ccp.common.download;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2017 Regents of the University of Colorado
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.apache.log4j.Logger;

import lombok.Data;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileReaderUtil;
import edu.ucdenver.ccp.common.file.FileWriterUtil;

/**
 * A class to store relevant metadata about the download of a data source.
 */
@Data
public class DownloadMetadata {
	private static final Logger logger = Logger.getLogger(DownloadMetadata.class);

	private final Calendar downloadDate;
	private final File downloadedFile;
	private final long fileSizeInBytes;
	private final Calendar fileLastModifiedDate;
	private final URL downloadUrl;

	enum DownloadMetadataProperty {
		DOWNLOAD_DATE, DOWNLOADED_FILE, FILE_SIZE_IN_BYTES, FILE_LAST_MOD_DATE, DOWNLOAD_URL, FILE_AGE_IN_DAYS
	}

	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");

	public void writePropertiesFile(File outputFile) throws IOException, URISyntaxException {
		Properties p = new Properties();
		p.setProperty(DownloadMetadataProperty.DOWNLOAD_DATE.name(), DATE_FORMATTER.format(downloadDate.getTime()));
		p.setProperty(DownloadMetadataProperty.DOWNLOADED_FILE.name(), downloadedFile.getAbsolutePath());
		p.setProperty(DownloadMetadataProperty.FILE_SIZE_IN_BYTES.name(), Long.toString(fileSizeInBytes));
		p.setProperty(DownloadMetadataProperty.FILE_LAST_MOD_DATE.name(),
				DATE_FORMATTER.format(fileLastModifiedDate.getTime()));
		p.setProperty(DownloadMetadataProperty.DOWNLOAD_URL.name(), downloadUrl.toURI().toString());

		long fileAgeInDays = getFileAgeInDays();
		p.setProperty(DownloadMetadataProperty.FILE_AGE_IN_DAYS.name(), Long.toString(fileAgeInDays));

		BufferedWriter writer = FileWriterUtil.initBufferedWriter(outputFile);
		p.store(writer, null);
		writer.close();
	}

	public long getFileAgeInDays() {
		long fileAgeInMillis = downloadDate.getTimeInMillis() - fileLastModifiedDate.getTimeInMillis();
		long fileAgeInDays = fileAgeInMillis / (24 * 60 * 60 * 1000);
		return fileAgeInDays;
	}

	public static DownloadMetadata loadFromPropertiesFile(File propertiesFile) throws IOException, ParseException {
		logger.info("Reading from properties file: " + propertiesFile.getAbsolutePath());
		Properties p = new Properties();
		BufferedReader reader = FileReaderUtil.initBufferedReader(propertiesFile, CharacterEncoding.UTF_8);
		p.load(reader);
		reader.close();

		Calendar downloadDate = Calendar.getInstance();
		downloadDate.setTime(DATE_FORMATTER.parse(p.getProperty(DownloadMetadataProperty.DOWNLOAD_DATE.name())));

		Calendar lastModDate = Calendar.getInstance();
		lastModDate.setTime(DATE_FORMATTER.parse(p.getProperty(DownloadMetadataProperty.FILE_LAST_MOD_DATE.name())));

		File downloadedFile = new File(p.getProperty(DownloadMetadataProperty.DOWNLOADED_FILE.name()));

		long fileSizeInBytes = Long.parseLong(p.getProperty(DownloadMetadataProperty.FILE_SIZE_IN_BYTES.name()));

		/*
		 * in some cases the download URL may not be provided, or may be n/a to
		 * indicate that the file was not downloaded
		 */
		URL downloadUrl = null;
		try {
			downloadUrl = new URL(p.getProperty(DownloadMetadataProperty.DOWNLOAD_URL.name()));
		} catch (java.net.MalformedURLException e) {
			downloadUrl = null;
		}

		return new DownloadMetadata(downloadDate, downloadedFile, fileSizeInBytes, lastModDate, downloadUrl);
	}

}
