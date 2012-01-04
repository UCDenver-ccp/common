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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.ucdenver.ccp.common.ftp.FTPUtil.FileType;

/**
 * Definition for the @FtpDownload annotation. This annotation facilitates the download of a
 * particular file via FTP and the reference of that file to a File member variable in a class. The @FtpDownload
 * annotation is "activated" by calling the DownloadUtil.download() method.
 * 
 * @author bill
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface FtpDownload {
	/**
	 * The FTP server address, e.g. ftp.ncbi.nih.gov
	 * 
	 * @return
	 */
	String server();

	/**
	 * The FTP port. This option is useful for setting up unit tests using mock ftp servers
	 * 
	 * @return
	 */
	int port() default -1;

	/**
	 * The path (not including the file) where the file to be downloaded can be found on the FTP
	 * server
	 * 
	 * @return
	 */
	String path();

	/**
	 * The name of the file to be downloaded
	 * 
	 * @return
	 */
	String filename();

	/**
	 * Specifies the download mode: ASCII or BINARY
	 * 
	 * @return
	 */
	FileType filetype();

	/**
	 * Specifies the user name to use when logging into the FTP server. Default="anonymous"
	 * 
	 * @return
	 */
	String username() default "anonymous";

	/**
	 * Specifies the password to use when logging into the FTP server. Default="anonymous"
	 * 
	 * @return
	 */
	String password() default "anonymous";
}
